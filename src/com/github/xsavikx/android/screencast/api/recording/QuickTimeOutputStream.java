package com.github.xsavikx.android.screencast.api.recording;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.LinkedList;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;

public class QuickTimeOutputStream {

	/**
	 * Atom base class.
	 */
	private abstract class Atom {

		/**
		 * The type of the atom. A String with the length of 4 characters.
		 */
		protected String type;
		/**
		 * The offset of the atom relative to the start of the
		 * ImageOutputStream.
		 */
		protected long offset;

		/**
		 * Creates a new Atom at the current position of the ImageOutputStream.
		 * 
		 * @param type
		 *            The type of the atom. A string with a length of 4
		 *            characters.
		 */
		public Atom(String type) throws IOException {
			this.type = type;
			offset = out.getStreamPosition();
		}

		/**
		 * Writes the atom to the ImageOutputStream and disposes it.
		 */
		public abstract void finish() throws IOException;

		/**
		 * Returns the size of the atom including the size of the atom header.
		 * 
		 * @return The size of the atom.
		 */
		public abstract long size();
	}

	/**
	 * A CompositeAtom contains an ordered list of Atoms.
	 */
	private class CompositeAtom extends Atom {

		private LinkedList<Atom> children;
		private boolean finished;

		/**
		 * Creates a new CompositeAtom at the current position of the
		 * ImageOutputStream.
		 * 
		 * @param type
		 *            The type of the atom.
		 */
		public CompositeAtom(String type) throws IOException {
			super(type);
			out.writeLong(0); // make room for the atom header
			children = new LinkedList<Atom>();
		}

		public void add(Atom child) throws IOException {
			if (children.size() > 0) {
				children.getLast().finish();
			}
			children.add(child);
		}

		/**
		 * Writes the atom and all its children to the ImageOutputStream and
		 * disposes of all resources held by the atom.
		 * 
		 * @throws java.io.IOException
		 */
		@Override
		public void finish() throws IOException {
			if (!finished) {
				if (size() > 0xffffffffL) {
					throw new IOException("CompositeAtom \"" + type
							+ "\" is too large: " + size());
				}

				long pointer = out.getStreamPosition();
				out.seek(offset);
				try (DataAtomOutputStream headerData = new DataAtomOutputStream(
						new FilterImageOutputStream(out));) {

					headerData.writeInt((int) size());
					headerData.writeType(type);
					for (Atom child : children) {
						child.finish();
					}
					out.seek(pointer);
					finished = true;
				}
			}
		}

		@Override
		public long size() {
			long length = 8;
			for (Atom child : children) {
				length += child.size();
			}
			return length;
		}
	}

	/**
	 * Data Atom.
	 */
	protected class DataAtom extends Atom {

		private DataAtomOutputStream data;
		private boolean finished;

		/**
		 * Creates a new DataAtom at the current position of the
		 * ImageOutputStream.
		 * 
		 * @param type
		 *            The type of the atom.
		 */
		public DataAtom(String name) throws IOException {
			super(name);
			out.writeLong(0); // make room for the atom header
			data = new DataAtomOutputStream(new FilterImageOutputStream(out));
		}

		@Override
		public void finish() throws IOException {
			if (!finished) {
				long sizeBefore = size();

				if (size() > 0xffffffffL) {
					throw new IOException("DataAtom \"" + type
							+ "\" is too large: " + size());
				}

				long pointer = out.getStreamPosition();
				out.seek(offset);
				try (DataAtomOutputStream headerData = new DataAtomOutputStream(
						new FilterImageOutputStream(out));) {
					headerData.writeUInt(size());
					headerData.writeType(type);
					out.seek(pointer);
					finished = true;
					long sizeAfter = size();
					if (sizeBefore != sizeAfter) {
						System.err.println("size mismatch " + sizeBefore + ".."
								+ sizeAfter);
					}
				}
			}
		}

		/**
		 * Returns the offset of this atom to the beginning of the random access
		 * file
		 * 
		 * @return
		 */
		public long getOffset() {
			return offset;
		}

		public DataAtomOutputStream getOutputStream() {
			if (finished) {
				throw new IllegalStateException("DataAtom is finished");
			}
			return data;
		}

		@Override
		public long size() {
			return 8 + data.size();
		}
	}
	/**
	 * QuickTime stores media data in samples. A sample is a single element in a
	 * sequence of time-ordered data. Samples are stored in the mdat atom.
	 */
	private static class Sample {

		/**
		 * Offset of the sample relative to the start of the QuickTime file.
		 */
		long offset;
		/** Data length of the sample. */
		long length;
		/**
		 * The duration of the sample in time scale units.
		 */
		int duration;

		/**
		 * Creates a new sample.
		 * 
		 * @param duration
		 * @param offset
		 * @param length
		 */
		public Sample(int duration, long offset, long length) {
			this.duration = duration;
			this.offset = offset;
			this.length = length;
		}
	}
	/**
	 * The states of the movie output stream.
	 */
	private static enum States {

		STARTED, FINISHED, CLOSED;
	}
	/**
	 * Supported video formats.
	 */
	public static enum VideoFormat {

		RAW, JPG, PNG;
	}
	/**
	 * WideDataAtom can grow larger then 4 gigabytes.
	 */
	protected class WideDataAtom extends Atom {

		private DataAtomOutputStream data;
		private boolean finished;

		/**
		 * Creates a new DataAtom at the current position of the
		 * ImageOutputStream.
		 * 
		 * @param type
		 *            The type of the atom.
		 */
		public WideDataAtom(String type) throws IOException {
			super(type);
			out.writeLong(0); // make room for the atom header
			out.writeLong(0); // make room for the atom header
			data = new DataAtomOutputStream(new FilterImageOutputStream(out));
		}

		@Override
		public void finish() throws IOException {
			if (!finished) {
				long pointer = out.getStreamPosition();
				out.seek(offset);
				try (DataAtomOutputStream headerData = new DataAtomOutputStream(
						new FilterImageOutputStream(out));) {

					if (size() <= 0xffffffffL) {
						headerData.writeUInt(8);
						headerData.writeType("wide");
						headerData.writeUInt(size());
						headerData.writeType(type);
					} else {
						headerData.writeInt(1); // special value for extended
												// size
												// atoms
						headerData.writeType(type);
						headerData.writeLong(size());
					}

					out.seek(pointer);
					finished = true;
				}
			}
		}

		/**
		 * Returns the offset of this atom to the beginning of the random access
		 * file
		 * 
		 * @return
		 */
		public long getOffset() {
			return offset;
		}

		public DataAtomOutputStream getOutputStream() {
			if (finished) {
				throw new IllegalStateException("Atom is finished");
			}
			return data;
		}

		@Override
		public long size() {
			long size = 8 + data.size();
			return (size > 0xffffffffL) ? size + 8 : size;
		}
	}
	/**
	 * Output stream of the QuickTimeOutputStream.
	 */
	private ImageOutputStream out;

	/**
	 * Current video format.
	 */
	private VideoFormat videoFormat;

	/**
	 * Quality of JPEG encoded video frames.
	 */
	private float quality = 0.9f;

	/**
	 * Creation time of the movie output stream.
	 */
	private Date creationTime;

	/**
	 * Width of the video frames. All frames must have the same width. The value
	 * -1 is used to mark unspecified width.
	 */
	private int imgWidth = -1;
	/**
	 * Height of the video frames. All frames must have the same height. The
	 * value -1 is used to mark unspecified height.
	 */
	private int imgHeight = -1;

	/**
	 * The timeScale of the movie. A time value that indicates the time scale
	 * for this media-that is, the number of time units that pass per second in
	 * its time coordinate system.
	 */
	private int timeScale = 600;

	/**
	 * The current state of the movie output stream.
	 */
	private States state = States.FINISHED;

	/**
	 * List of video frames.
	 */
	private LinkedList<Sample> videoFrames;

	/**
	 * This atom holds the movie frames.
	 */
	private WideDataAtom mdatAtom;

	/**
	 * Creates a new output stream with the specified image videoFormat and
	 * framerate.
	 * 
	 * @param file
	 *            the output file
	 * @param format
	 *            Selects an encoder for the video format "JPG" or "PNG".
	 * @exception IllegalArgumentException
	 *                if videoFormat is null or if framerate is <= 0
	 */
	public QuickTimeOutputStream(File file, VideoFormat format)
			throws IOException {
		if (file.exists()) {
			file.delete();
		}
		out = new FileImageOutputStream(file);

		if (format == null) {
			throw new IllegalArgumentException("format must not be null");
		}

		this.videoFormat = format;

		this.videoFrames = new LinkedList<Sample>();
	}

	/**
	 * Closes the movie file as well as the stream being filtered.
	 * 
	 * @exception IOException
	 *                if an I/O error has occurred
	 */
	public void close() throws IOException {
		if (state == States.STARTED) {
			finish();
		}
		if (state != States.CLOSED) {
			out.close();
			state = States.CLOSED;
		}
	}

	/**
	 * Check to make sure that this stream has not been closed
	 */
	private void ensureOpen() throws IOException {
		if (state == States.CLOSED) {
			throw new IOException("Stream closed");
		}
	}

	/**
	 * Sets the state of the QuickTimeOutpuStream to started.
	 * <p>
	 * If the state is changed by this method, the prolog is written.
	 */
	private void ensureStarted() throws IOException {
		if (state != States.STARTED) {
			creationTime = new Date();
			writeProlog();
			mdatAtom = new WideDataAtom("mdat");
			state = States.STARTED;
		}
	}

	/**
	 * Finishes writing the contents of the QuickTime output stream without
	 * closing the underlying stream. Use this method when applying multiple
	 * filters in succession to the same output stream.
	 * 
	 * @exception IllegalStateException
	 *                if the dimension of the video track has not been specified
	 *                or determined yet.
	 * @exception IOException
	 *                if an I/O exception has occurred
	 */
	public void finish() throws IOException {
		ensureOpen();
		if (state != States.FINISHED) {
			if (imgWidth == -1 || imgHeight == -1) {
				throw new IllegalStateException(
						"image width and height must be specified");
			}
			mdatAtom.finish();
			writeEpilog();
			state = States.FINISHED;
			imgWidth = imgHeight = -1;
		}
	}

	/**
	 * Returns the time scale of this media.
	 * 
	 * @return time scale
	 */
	public int getTimeScale() {
		return timeScale;
	}

	/**
	 * Returns the video compression quality.
	 * 
	 * @return video compression quality
	 */
	public float getVideoCompressionQuality() {
		return quality;
	}

	/**
	 * Sets the time scale for this media, that is, the number of time units
	 * that pass per second in its time coordinate system.
	 * <p>
	 * The default value is 600.
	 * 
	 * @param newValue
	 */
	public void setTimeScale(int newValue) {
		if (newValue <= 0) {
			throw new IllegalArgumentException("timeScale must be greater 0");
		}
		this.timeScale = newValue;
	}

	/**
	 * Sets the compression quality of the video track. A value of 0 stands for
	 * "high compression is important" a value of 1 for
	 * "high image quality is important".
	 * <p>
	 * Changing this value affects frames which are subsequently written to the
	 * QuickTimeOutputStream. Frames which have already been written are not
	 * changed.
	 * <p>
	 * This value has no effect on videos encoded with the PNG format.
	 * <p>
	 * The default value is 0.9.
	 * 
	 * @param newValue
	 */
	public void setVideoCompressionQuality(float newValue) {
		this.quality = newValue;
	}

	/**
	 * Sets the dimension of the video track.
	 * <p>
	 * You need to explicitly set the dimension, if you add all frames from
	 * files or input streams.
	 * <p>
	 * If you add frames from buffered images, then QuickTimeOutputStream can
	 * determine the video dimension from the image width and height.
	 * 
	 * @param width
	 * @param height
	 */
	public void setVideoDimension(int width, int height) {
		if (width < 1 || height < 1) {
			throw new IllegalArgumentException(
					"width and height must be greater zero.");
		}
		this.imgWidth = width;
		this.imgHeight = height;
	}

	private void writeEpilog() throws IOException {
		Date modificationTime = new Date();
		int duration = 0;
		for (Sample s : videoFrames) {
			duration += s.duration;
		}

		DataAtom leaf;

		/* Movie Atom ========= */
		CompositeAtom moovAtom = new CompositeAtom("moov");

		/*
		 * Movie Header Atom ------------- The data contained in this atom
		 * defines characteristics of the entire QuickTime movie, such as time
		 * scale and duration. It has an atom type value of 'mvhd'.
		 * 
		 * typedef struct { byte version; byte[3] flags; mactimestamp
		 * creationTime; mactimestamp modificationTime; int timeScale; int
		 * duration; int preferredRate; short preferredVolume; byte[10]
		 * reserved; int[9] matrix; int previewTime; int previewDuration; int
		 * posterTime; int selectionTime; int selectionDuration; int
		 * currentTime; int nextTrackId; } movieHeaderAtom;
		 */
		leaf = new DataAtom("mvhd");
		moovAtom.add(leaf);
		DataAtomOutputStream d = leaf.getOutputStream();
		d.writeByte(0); // version
		// A 1-byte specification of the version of this movie header atom.

		d.writeByte(0); // flags[0]
		d.writeByte(0); // flags[1]
		d.writeByte(0); // flags[2]
		// Three bytes of space for future movie header flags.

		d.writeMacTimestamp(creationTime); // creationTime
		// A 32-bit integer that specifies the calendar date and time (in
		// seconds since midnight, January 1, 1904) when the movie atom was
		// created. It is strongly recommended that this value should be
		// specified using coordinated universal time (UTC).

		d.writeMacTimestamp(modificationTime); // modificationTime
		// A 32-bit integer that specifies the calendar date and time (in
		// seconds since midnight, January 1, 1904) when the movie atom was
		// changed. BooleanIt is strongly recommended that this value should be
		// specified using coordinated universal time (UTC).

		d.writeInt(timeScale); // timeScale
		// A time value that indicates the time scale for this movie-that is,
		// the number of time units that pass per second in its time coordinate
		// system. A time coordinate system that measures time in sixtieths of a
		// second, for example, has a time scale of 60.

		d.writeInt(duration); // duration
		// A time value that indicates the duration of the movie in time scale
		// units. Note that this property is derived from the movie's tracks.
		// The value of this field corresponds to the duration of the longest
		// track in the movie.

		d.writeFixed16D16(1d); // preferredRate
		// A 32-bit fixed-point number that specifies the rate at which to play
		// this movie. A value of 1.0 indicates normal rate.

		d.writeShort(256); // preferredVolume
		// A 16-bit fixed-point number that specifies how loud to play this
		// movie's sound. A value of 1.0 indicates full volume.

		d.write(new byte[10]); // reserved;
		// Ten bytes reserved for use by Apple. Set to 0.

		d.writeFixed16D16(1f); // matrix[0]
		d.writeFixed16D16(0f); // matrix[1]
		d.writeFixed2D30(0f); // matrix[2]
		d.writeFixed16D16(0f); // matrix[3]
		d.writeFixed16D16(1f); // matrix[4]
		d.writeFixed2D30(0); // matrix[5]
		d.writeFixed16D16(0); // matrix[6]
		d.writeFixed16D16(0); // matrix[7]
		d.writeFixed2D30(1f); // matrix[8]
		// The matrix structure associated with this movie. A matrix shows how
		// to map points from one coordinate space into another. See "Matrices"
		// for a discussion of how display matrices are used in QuickTime:
		// http://developer.apple.com/documentation/QuickTime/QTFF/QTFFChap4/chapter_5_section_4.html#//apple_ref/doc/uid/TP40000939-CH206-18737

		d.writeInt(0); // previewTime
		// The time value in the movie at which the preview begins.

		d.writeInt(0); // previewDuration
		// The duration of the movie preview in movie time scale units.

		d.writeInt(0); // posterTime
		// The time value of the time of the movie poster.

		d.writeInt(0); // selectionTime
		// The time value for the start time of the current selection.

		d.writeInt(0); // selectionDuration
		// The duration of the current selection in movie time scale units.

		d.writeInt(0); // currentTime;
		// The time value for current time position within the movie.

		d.writeInt(2); // nextTrackId
		// A 32-bit integer that indicates a value to use for the track ID
		// number of the next track added to this movie. Note that 0 is not a
		// valid track ID value.

		/* Track Atom ======== */
		CompositeAtom trakAtom = new CompositeAtom("trak");
		moovAtom.add(trakAtom);

		/*
		 * Track Header Atom ----------- The track header atom specifies the
		 * characteristics of a single track within a movie. A track header atom
		 * contains a size field that specifies the number of bytes and a type
		 * field that indicates the format of the data (defined by the atom type
		 * 'tkhd').
		 * 
		 * typedef struct { byte version; byte flag0; byte flag1; byte set
		 * TrackHeaderFlags flag2; mactimestamp creationTime; mactimestamp
		 * modificationTime; int trackId; byte[4] reserved; int duration;
		 * byte[8] reserved; short layer; short alternateGroup; short volume;
		 * byte[2] reserved; int[9] matrix; int trackWidth; int trackHeight; }
		 * trackHeaderAtom;
		 */
		leaf = new DataAtom("tkhd");
		trakAtom.add(leaf);
		d = leaf.getOutputStream();
		d.write(0); // version
		// A 1-byte specification of the version of this track header.

		d.write(0); // flag[0]
		d.write(0); // flag[1]
		d.write(0xf); // flag[2]
		// Three bytes that are reserved for the track header flags. These flags
		// indicate how the track is used in the movie. The following flags are
		// valid (all flags are enabled when set to 1):
		//
		// Track enabled
		// Indicates that the track is enabled. Flag value is 0x0001.
		// Track in movie
		// Indicates that the track is used in the movie. Flag value is
		// 0x0002.
		// Track in preview
		// Indicates that the track is used in the movie's preview. Flag
		// value is 0x0004.
		// Track in poster
		// Indicates that the track is used in the movie's poster. Flag
		// value is 0x0008.

		d.writeMacTimestamp(creationTime); // creationTime
		// A 32-bit integer that indicates the calendar date and time (expressed
		// in seconds since midnight, January 1, 1904) when the track header was
		// created. It is strongly recommended that this value should be
		// specified using coordinated universal time (UTC).

		d.writeMacTimestamp(modificationTime); // modificationTime
		// A 32-bit integer that indicates the calendar date and time (expressed
		// in seconds since midnight, January 1, 1904) when the track header was
		// changed. It is strongly recommended that this value should be
		// specified using coordinated universal time (UTC).

		d.writeInt(1); // trackId
		// A 32-bit integer that uniquely identifies the track. The value 0
		// cannot be used.

		d.writeInt(0); // reserved;
		// A 32-bit integer that is reserved for use by Apple. Set this field to
		// 0.

		d.writeInt(duration); // duration
		// A time value that indicates the duration of this track (in the
		// movie's time coordinate system). Note that this property is derived
		// from the track's edits. The value of this field is equal to the sum
		// of the durations of all of the track's edits. If there is no edit
		// list, then the duration is the sum of the sample durations, converted
		// into the movie timescale.

		d.writeLong(0); // reserved
		// An 8-byte value that is reserved for use by Apple. Set this field to
		// 0.

		d.writeShort(0); // layer;
		// A 16-bit integer that indicates this track's spatial priority in its
		// movie. The QuickTime Movie Toolbox uses this value to determine how
		// tracks overlay one another. Tracks with lower layer values are
		// displayed in front of tracks with higher layer values.

		d.writeShort(0); // alternate group
		// A 16-bit integer that specifies a collection of movie tracks that
		// contain alternate data for one another. QuickTime chooses one track
		// from the group to be used when the movie is played. The choice may be
		// based on such considerations as playback quality, language, or the
		// capabilities of the computer.

		d.writeShort(0); // volume
		// A 16-bit fixed-point value that indicates how loudly this track's
		// sound is to be played. A value of 1.0 indicates normal volume.

		d.writeShort(0); // reserved
		// A 16-bit integer that is reserved for use by Apple. Set this field to
		// 0.

		d.writeFixed16D16(1f); // matrix[0]
		d.writeFixed16D16(0f); // matrix[1]
		d.writeFixed2D30(0f); // matrix[2]
		d.writeFixed16D16(0f); // matrix[3]
		d.writeFixed16D16(1f); // matrix[4]
		d.writeFixed2D30(0); // matrix[5]
		d.writeFixed16D16(0); // matrix[6]
		d.writeFixed16D16(0); // matrix[7]
		d.writeFixed2D30(1f); // matrix[8]
		// The matrix structure associated with this track.
		// See Figure 2-8 for an illustration of a matrix structure:
		// http://developer.apple.com/documentation/QuickTime/QTFF/QTFFChap2/chapter_3_section_3.html#//apple_ref/doc/uid/TP40000939-CH204-32967

		d.writeFixed16D16(imgWidth); // width
		// A 32-bit fixed-point number that specifies the width of this track in
		// pixels.

		d.writeFixed16D16(imgHeight); // height
		// A 32-bit fixed-point number that indicates the height of this track
		// in pixels.

		/* Media Atom ========= */
		CompositeAtom mdiaAtom = new CompositeAtom("mdia");
		trakAtom.add(mdiaAtom);

		/*
		 * Media Header atom ------- typedef struct { byte version; byte[3]
		 * flags; mactimestamp creationTime; mactimestamp modificationTime; int
		 * timeScale; int duration; short language; short quality; }
		 * mediaHeaderAtom;
		 */
		leaf = new DataAtom("mdhd");
		mdiaAtom.add(leaf);
		d = leaf.getOutputStream();
		d.write(0); // version
		// One byte that specifies the version of this header atom.

		d.write(0); // flag[0]
		d.write(0); // flag[1]
		d.write(0); // flag[2]
		// Three bytes of space for media header flags. Set this field to 0.

		d.writeMacTimestamp(creationTime); // creationTime
		// A 32-bit integer that specifies (in seconds since midnight, January
		// 1, 1904) when the media atom was created. It is strongly recommended
		// that this value should be specified using coordinated universal time
		// (UTC).

		d.writeMacTimestamp(modificationTime); // modificationTime
		// A 32-bit integer that specifies (in seconds since midnight, January
		// 1, 1904) when the media atom was changed. It is strongly recommended
		// that this value should be specified using coordinated universal time
		// (UTC).

		d.writeInt(timeScale); // timeScale
		// A time value that indicates the time scale for this media-that is,
		// the number of time units that pass per second in its time coordinate
		// system.

		d.writeInt(duration); // duration
		// The duration of this media in units of its time scale.

		d.writeShort(0); // language;
		// A 16-bit integer that specifies the language code for this media.
		// See "Language Code Values" for valid language codes:
		// http://developer.apple.com/documentation/QuickTime/QTFF/QTFFChap4/chapter_5_section_2.html#//apple_ref/doc/uid/TP40000939-CH206-27005

		d.writeShort(0); // quality
		// A 16-bit integer that specifies the media's playback quality-that is,
		// its suitability for playback in a given environment.

		/** Media Handler Atom ------- */
		leaf = new DataAtom("hdlr");
		mdiaAtom.add(leaf);
		/*
		 * typedef struct { byte version; byte[3] flags; magic componentType;
		 * magic componentSubtype; magic componentManufacturer; int
		 * componentFlags; int componentFlagsMask; cstring componentName; }
		 * handlerReferenceAtom;
		 */
		d = leaf.getOutputStream();
		d.write(0); // version
		// A 1-byte specification of the version of this handler information.

		d.write(0); // flag[0]
		d.write(0); // flag[1]
		d.write(0); // flag[2]
		// A 3-byte space for handler information flags. Set this field to 0.

		d.writeType("mhlr"); // componentType
		// A four-character code that identifies the type of the handler. Only
		// two values are valid for this field: 'mhlr' for media handlers and
		// 'dhlr' for data handlers.

		d.writeType("vide"); // componentSubtype
		// A four-character code that identifies the type of the media handler
		// or data handler. For media handlers, this field defines the type of
		// data-for example, 'vide' for video data or 'soun' for sound data.
		//
		// For data handlers, this field defines the data reference type-for
		// example, a component subtype value of 'alis' identifies a file alias.

		d.writeInt(0); // componentManufacturer
		// Reserved. Set to 0.

		d.writeInt(0); // componentFlags
		// Reserved. Set to 0.

		d.writeInt(0); // componentFlagsMask
		// Reserved. Set to 0.

		d.write(0); // componentName (empty string)
		// A (counted) string that specifies the name of the component-that is,
		// the media handler used when this media was created. This field may
		// contain a zero-length (empty) string.

		/* Media Information atom ========= */
		CompositeAtom minfAtom = new CompositeAtom("minf");
		mdiaAtom.add(minfAtom);

		/* Video media information atom -------- */
		leaf = new DataAtom("vmhd");
		minfAtom.add(leaf);
		/*
		 * typedef struct { byte version; byte flag1; byte flag2; byte set
		 * vmhdFlags flag3; short graphicsMode; ushort[3] opcolor; }
		 * videoMediaInformationHeaderAtom;
		 */
		d = leaf.getOutputStream();
		d.write(0); // version
		// One byte that specifies the version of this header atom.

		d.write(0); // flag[0]
		d.write(0); // flag[1]
		d.write(0x1); // flag[2]
		// Three bytes of space for media header flags.
		// This is a compatibility flag that allows QuickTime to distinguish
		// between movies created with QuickTime 1.0 and newer movies. You
		// should always set this flag to 1, unless you are creating a movie
		// intended for playback using version 1.0 of QuickTime. This flag's
		// value is 0x0001.

		d.writeShort(0x40); // graphicsMode (0x40 = ditherCopy)
		// A 16-bit integer that specifies the transfer mode. The transfer mode
		// specifies which Boolean operation QuickDraw should perform when
		// drawing or transferring an image from one location to another.
		// See "Graphics Modes" for a list of graphics modes supported by
		// QuickTime:
		// http://developer.apple.com/documentation/QuickTime/QTFF/QTFFChap4/chapter_5_section_5.html#//apple_ref/doc/uid/TP40000939-CH206-18741

		d.writeUShort(0); // opcolor[0]
		d.writeUShort(0); // opcolor[1]
		d.writeUShort(0); // opcolor[2]
		// Three 16-bit values that specify the red, green, and blue colors for
		// the transfer mode operation indicated in the graphics mode field.

		/* Handle reference atom -------- */
		// The handler reference atom specifies the media handler component that
		// is to be used to interpret the media's data. The handler reference
		// atom has an atom type value of 'hdlr'.
		leaf = new DataAtom("hdlr");
		minfAtom.add(leaf);
		/*
		 * typedef struct { byte version; byte[3] flags; magic componentType;
		 * magic componentSubtype; magic componentManufacturer; int
		 * componentFlags; int componentFlagsMask; cstring componentName; }
		 * handlerReferenceAtom;
		 */
		d = leaf.getOutputStream();
		d.write(0); // version
		// A 1-byte specification of the version of this handler information.

		d.write(0); // flag[0]
		d.write(0); // flag[1]
		d.write(0); // flag[2]
		// A 3-byte space for handler information flags. Set this field to 0.

		d.writeType("dhlr"); // componentType
		// A four-character code that identifies the type of the handler. Only
		// two values are valid for this field: 'mhlr' for media handlers and
		// 'dhlr' for data handlers.

		d.writeType("alis"); // componentSubtype
		// A four-character code that identifies the type of the media handler
		// or data handler. For media handlers, this field defines the type of
		// data-for example, 'vide' for video data or 'soun' for sound data.
		// For data handlers, this field defines the data reference type-for
		// example, a component subtype value of 'alis' identifies a file alias.

		d.writeInt(0); // componentManufacturer
		// Reserved. Set to 0.

		d.writeInt(0); // componentFlags
		// Reserved. Set to 0.

		d.writeInt(0); // componentFlagsMask
		// Reserved. Set to 0.

		d.write(0); // componentName (empty string)
		// A (counted) string that specifies the name of the component-that is,
		// the media handler used when this media was created. This field may
		// contain a zero-length (empty) string.

		/* Data information atom ===== */
		CompositeAtom dinfAtom = new CompositeAtom("dinf");
		minfAtom.add(dinfAtom);

		/* Data reference atom ----- */
		// Data reference atoms contain tabular data that instructs the data
		// handler component how to access the media's data.
		leaf = new DataAtom("dref");
		dinfAtom.add(leaf);
		/*
		 * typedef struct { ubyte version; ubyte[3] flags; int numberOfEntries;
		 * dataReferenceEntry dataReference[numberOfEntries]; }
		 * dataReferenceAtom;
		 * 
		 * set { dataRefSelfReference=1 // I am not shure if this is the correct
		 * value for this flag } drefEntryFlags;
		 * 
		 * typedef struct { int size; magic type; byte version; ubyte flag1;
		 * ubyte flag2; ubyte set drefEntryFlags flag3; byte[size - 12] data; }
		 * dataReferenceEntry;
		 */
		d = leaf.getOutputStream();
		d.write(0); // version
		// A 1-byte specification of the version of this data reference atom.

		d.write(0); // flag[0]
		d.write(0); // flag[1]
		d.write(0); // flag[2]
		// A 3-byte space for data reference flags. Set this field to 0.

		d.writeInt(1); // numberOfEntries
		// A 32-bit integer containing the count of data references that follow.

		d.writeInt(12); // dataReference.size
		// A 32-bit integer that specifies the number of bytes in the data
		// reference.

		d.writeType("alis"); // dataReference.type
		// A 32-bit integer that specifies the type of the data in the data
		// reference. Table 2-4 lists valid type values:
		// http://developer.apple.com/documentation/QuickTime/QTFF/QTFFChap2/chapter_3_section_4.html#//apple_ref/doc/uid/TP40000939-CH204-38840

		d.write(0); // dataReference.version
		// A 1-byte specification of the version of the data reference.

		d.write(0); // dataReference.flag1
		d.write(0); // dataReference.flag2
		d.write(0x1); // dataReference.flag3
		// A 3-byte space for data reference flags. There is one defined flag.
		//
		// Self reference
		// This flag indicates that the media's data is in the same file as
		// the movie atom. On the Macintosh, and other file systems with
		// multifork files, set this flag to 1 even if the data resides in
		// a different fork from the movie atom. This flag's value is
		// 0x0001.

		/* Sample Table atom ========= */
		CompositeAtom stblAtom = new CompositeAtom("stbl");
		minfAtom.add(stblAtom);

		/* Sample Description atom ------- */
		// The sample description atom stores information that allows you to
		// decode samples in the media. The data stored in the sample
		// description varies, depending on the media type. For example, in the
		// case of video media, the sample descriptions are image description
		// structures. The sample description information for each media type is
		// explained in "Media Data Atom Types":
		// http://developer.apple.com/documentation/QuickTime/QTFF/QTFFChap3/chapter_4_section_1.html#//apple_ref/doc/uid/TP40000939-CH205-SW1
		leaf = new DataAtom("stsd");
		stblAtom.add(leaf);
		/*
		 * typedef struct { byte version; byte[3] flags; int numberOfEntries;
		 * sampleDescriptionEntry sampleDescriptionTable[numberOfEntries]; }
		 * sampleDescriptionAtom;
		 * 
		 * typedef struct { int size; magic type; byte[6] reserved; // six bytes
		 * that must be zero short dataReferenceIndex; // A 16-bit integer that
		 * contains the index of the data reference to use to retrieve data
		 * associated with samples that use this sample description. Data
		 * references are stored in data reference atoms. byte[size - 16] data;
		 * } sampleDescriptionEntry;
		 */
		d = leaf.getOutputStream();
		d.write(0); // version
		// A 1-byte specification of the version of this sample description
		// atom.

		d.write(0); // flag[0]
		d.write(0); // flag[1]
		d.write(0); // flag[2]
		// A 3-byte space for sample description flags. Set this field to 0.

		d.writeInt(1); // number of Entries
		// A 32-bit integer containing the number of sample descriptions that
		// follow.

		// A 32-bit integer indicating the number of bytes in the sample
		// description.
		switch (videoFormat) {
		case RAW: {
			d.writeInt(86); // sampleDescriptionTable[0].size
			d.writeType("raw "); // sampleDescriptionTable[0].type

			// A 32-bit integer indicating the format of the stored data.
			// This depends on the media type, but is usually either the
			// compression format or the media type.

			d.write(new byte[6]); // sampleDescriptionTable[0].reserved
			// Six bytes that must be set to 0.

			d.writeShort(1); // sampleDescriptionTable[0].dataReferenceIndex
			// A 16-bit integer that contains the index of the data
			// reference to use to retrieve data associated with samples
			// that use this sample description. Data references are stored
			// in data reference atoms.

			// Video Sample Description
			// ------------------------
			// The format of the following fields is described here:
			// http://developer.apple.com/documentation/QuickTime/QTFF/QTFFChap3/chapter_4_section_2.html#//apple_ref/doc/uid/TP40000939-CH205-BBCGICBJ

			d.writeShort(0); // sampleDescriptionTable.videoSampleDescription.version
			// A 16-bit integer indicating the version number of the
			// compressed data. This is set to 0, unless a compressor has
			// changed its data format.

			d.writeShort(0); // sampleDescriptionTable.videoSampleDescription.revisionLevel
			// A 16-bit integer that must be set to 0.

			d.writeType("java"); // sampleDescriptionTable.videoSampleDescription.manufacturer
			// A 32-bit integer that specifies the developer of the
			// compressor that generated the compressed data. Often this
			// field contains 'appl' to indicate Apple Computer, Inc.

			d.writeInt(0); // sampleDescriptionTable.videoSampleDescription.temporalQuality
			// A 32-bit integer containing a value from 0 to 1023 indicating
			// the degree of temporal compression.

			d.writeInt(512); // sampleDescriptionTable.videoSampleDescription.spatialQuality
			// A 32-bit integer containing a value from 0 to 1024 indicating
			// the degree of spatial compression.

			d.writeUShort(imgWidth); // sampleDescriptionTable.videoSampleDescription.width
			// A 16-bit integer that specifies the width of the source image
			// in pixels.

			d.writeUShort(imgHeight); // sampleDescriptionTable.videoSampleDescription.height
			// A 16-bit integer that specifies the height of the source image in
			// pixels.

			d.writeFixed16D16(72.0); // sampleDescriptionTable.videoSampleDescription.horizontalResolution
			// A 32-bit fixed-point number containing the horizontal
			// resolution of the image in pixels per inch.

			d.writeFixed16D16(72.0); // sampleDescriptionTable.videoSampleDescription.verticalResolution
			// A 32-bit fixed-point number containing the vertical
			// resolution of the image in pixels per inch.

			d.writeInt(0); // sampleDescriptionTable.videoSampleDescription.dataSize
			// A 32-bit integer that must be set to 0.

			d.writeShort(1); // sampleDescriptionTable.videoSampleDescription.frameCount
			// A 16-bit integer that indicates how many frames of compressed
			// data are stored in each sample. Usually set to 1.

			d.writePString("None", 32); // sampleDescriptionTable.videoSampleDescription.compressorName
			// A 32-byte Pascal string containing the name of the compressor
			// that created the image, such as "jpeg".

			d.writeShort(24); // sampleDescriptionTable.videoSampleDescription.depth
			// A 16-bit integer that indicates the pixel depth of the
			// compressed image. Values of 1, 2, 4, 8 ,16, 24, and 32
			// indicate the depth of color images. The value 32 should be
			// used only if the image contains an alpha channel. Values of
			// 34, 36, and 40 indicate 2-, 4-, and 8-bit grayscale,
			// respectively, for grayscale images.

			d.writeShort(-1); // sampleDescriptionTable.videoSampleDescription.colorTableID
			// A 16-bit integer that identifies which color table to use.
			// If this field is set to -1, the default color table should be
			// used for the specified depth. For all depths below 16 bits
			// per pixel, this indicates a standard Macintosh color table
			// for the specified depth. Depths of 16, 24, and 32 have no
			// color table.

			break;
		}
		case JPG: {
			d.writeInt(86); // sampleDescriptionTable[0].size
			d.writeType("jpeg"); // sampleDescriptionTable[0].type

			// A 32-bit integer indicating the format of the stored data.
			// This depends on the media type, but is usually either the
			// compression format or the media type.

			d.write(new byte[6]); // sampleDescriptionTable[0].reserved
			// Six bytes that must be set to 0.

			d.writeShort(1); // sampleDescriptionTable[0].dataReferenceIndex
			// A 16-bit integer that contains the index of the data
			// reference to use to retrieve data associated with samples
			// that use this sample description. Data references are stored
			// in data reference atoms.

			// Video Sample Description
			// ------------------------
			// The format of the following fields is described here:
			// http://developer.apple.com/documentation/QuickTime/QTFF/QTFFChap3/chapter_4_section_2.html#//apple_ref/doc/uid/TP40000939-CH205-BBCGICBJ

			d.writeShort(0); // sampleDescriptionTable.videoSampleDescription.version
			// A 16-bit integer indicating the version number of the
			// compressed data. This is set to 0, unless a compressor has
			// changed its data format.

			d.writeShort(0); // sampleDescriptionTable.videoSampleDescription.revisionLevel
			// A 16-bit integer that must be set to 0.

			d.writeType("java"); // sampleDescriptionTable.videoSampleDescription.manufacturer
			// A 32-bit integer that specifies the developer of the
			// compressor that generated the compressed data. Often this
			// field contains 'appl' to indicate Apple Computer, Inc.

			d.writeInt(0); // sampleDescriptionTable.videoSampleDescription.temporalQuality
			// A 32-bit integer containing a value from 0 to 1023 indicating
			// the degree of temporal compression.

			d.writeInt(512); // sampleDescriptionTable.videoSampleDescription.spatialQuality
			// A 32-bit integer containing a value from 0 to 1024 indicating
			// the degree of spatial compression.

			d.writeUShort(imgWidth); // sampleDescriptionTable.videoSampleDescription.width
			// A 16-bit integer that specifies the width of the source image
			// in pixels.

			d.writeUShort(imgHeight); // sampleDescriptionTable.videoSampleDescription.height
			// A 16-bit integer that specifies the height of the source image in
			// pixels.

			d.writeFixed16D16(72.0); // sampleDescriptionTable.videoSampleDescription.horizontalResolution
			// A 32-bit fixed-point number containing the horizontal
			// resolution of the image in pixels per inch.

			d.writeFixed16D16(72.0); // sampleDescriptionTable.videoSampleDescription.verticalResolution
			// A 32-bit fixed-point number containing the vertical
			// resolution of the image in pixels per inch.

			d.writeInt(0); // sampleDescriptionTable.videoSampleDescription.dataSize
			// A 32-bit integer that must be set to 0.

			d.writeShort(1); // sampleDescriptionTable.videoSampleDescription.frameCount
			// A 16-bit integer that indicates how many frames of compressed
			// data are stored in each sample. Usually set to 1.

			d.writePString("Photo - JPEG", 32); // sampleDescriptionTable.videoSampleDescription.compressorName
			// A 32-byte Pascal string containing the name of the compressor
			// that created the image, such as "jpeg".

			d.writeShort(24); // sampleDescriptionTable.videoSampleDescription.depth
			// A 16-bit integer that indicates the pixel depth of the
			// compressed image. Values of 1, 2, 4, 8 ,16, 24, and 32
			// indicate the depth of color images. The value 32 should be
			// used only if the image contains an alpha channel. Values of
			// 34, 36, and 40 indicate 2-, 4-, and 8-bit grayscale,
			// respectively, for grayscale images.

			d.writeShort(-1); // sampleDescriptionTable.videoSampleDescription.colorTableID
			// A 16-bit integer that identifies which color table to use.
			// If this field is set to -1, the default color table should be
			// used for the specified depth. For all depths below 16 bits
			// per pixel, this indicates a standard Macintosh color table
			// for the specified depth. Depths of 16, 24, and 32 have no
			// color table.

			break;
		}
		case PNG: {
			d.writeInt(86); // sampleDescriptionTable[0].size
			d.writeType("png "); // sampleDescriptionTable[0].type
			// A 32-bit integer indicating the format of the stored data.
			// This depends on the media type, but is usually either the
			// compression format or the media type.

			d.write(new byte[6]); // sampleDescriptionTable[0].reserved
			// Six bytes that must be set to 0.

			d.writeShort(1); // sampleDescriptionTable[0].dataReferenceIndex
			// A 16-bit integer that contains the index of the data
			// reference to use to retrieve data associated with samples
			// that use this sample description. Data references are stored
			// in data reference atoms.

			// Video Sample Description
			// ------------------------
			// The format of the following fields is described here:
			// http://developer.apple.com/documentation/QuickTime/QTFF/QTFFChap3/chapter_4_section_2.html#//apple_ref/doc/uid/TP40000939-CH205-BBCGICBJ

			d.writeShort(0); // sampleDescriptionTable.videoSampleDescription.version
			// A 16-bit integer indicating the version number of the
			// compressed data. This is set to 0, unless a compressor has
			// changed its data format.

			d.writeShort(0); // sampleDescriptionTable.videoSampleDescription.revisionLevel
			// A 16-bit integer that must be set to 0.

			d.writeType("java"); // sampleDescriptionTable.videoSampleDescription.manufacturer
			// A 32-bit integer that specifies the developer of the
			// compressor that generated the compressed data. Often this
			// field contains 'appl' to indicate Apple Computer, Inc.

			d.writeInt(0); // sampleDescriptionTable.videoSampleDescription.temporalQuality
			// A 32-bit integer containing a value from 0 to 1023 indicating
			// the degree of temporal compression.

			d.writeInt(512); // sampleDescriptionTable.videoSampleDescription.spatialQuality
			// A 32-bit integer containing a value from 0 to 1024 indicating
			// the degree of spatial compression.

			d.writeUShort(imgWidth); // sampleDescriptionTable.videoSampleDescription.width
			// A 16-bit integer that specifies the width of the source image
			// in pixels.

			d.writeUShort(imgHeight); // sampleDescriptionTable.videoSampleDescription.height
			// A 16-bit integer that specifies the height of the source image in
			// pixels.

			d.writeFixed16D16(72.0); // sampleDescriptionTable.videoSampleDescription.horizontalResolution
			// A 32-bit fixed-point number containing the horizontal
			// resolution of the image in pixels per inch.

			d.writeFixed16D16(72.0); // sampleDescriptionTable.videoSampleDescription.verticalResolution
			// A 32-bit fixed-point number containing the vertical
			// resolution of the image in pixels per inch.

			d.writeInt(0); // sampleDescriptionTable.videoSampleDescription.dataSize
			// A 32-bit integer that must be set to 0.

			d.writeShort(1); // sampleDescriptionTable.videoSampleDescription.frameCount
			// A 16-bit integer that indicates how many frames of compressed
			// data are stored in each sample. Usually set to 1.

			d.writePString("PNG", 32); // sampleDescriptionTable.videoSampleDescription.compressorName
			// A 32-byte Pascal string containing the name of the compressor
			// that created the image, such as "jpeg".

			d.writeShort(24); // sampleDescriptionTable.videoSampleDescription.depth
			// A 16-bit integer that indicates the pixel depth of the
			// compressed image. Values of 1, 2, 4, 8 ,16, 24, and 32
			// indicate the depth of color images. The value 32 should be
			// used only if the image contains an alpha channel. Values of
			// 34, 36, and 40 indicate 2-, 4-, and 8-bit grayscale,
			// respectively, for grayscale images.

			d.writeShort(-1); // sampleDescriptionTable.videoSampleDescription.colorTableID
			// A 16-bit integer that identifies which color table to use.
			// If this field is set to -1, the default color table should be
			// used for the specified depth. For all depths below 16 bits
			// per pixel, this indicates a standard Macintosh color table
			// for the specified depth. Depths of 16, 24, and 32 have no
			// color table.

			break;
		}
		}

		/* Time to Sample atom ---- */
		// Time-to-sample atoms store duration information for a media's
		// samples, providing a mapping from a time in a media to the
		// corresponding data sample. The time-to-sample atom has an atom type
		// of 'stts'.
		leaf = new DataAtom("stts");
		stblAtom.add(leaf);
		/*
		 * typedef struct { byte version; byte[3] flags; int numberOfEntries;
		 * timeToSampleTable timeToSampleTable[numberOfEntries]; }
		 * timeToSampleAtom;
		 * 
		 * typedef struct { int sampleCount; int sampleDuration; }
		 * timeToSampleTable;
		 */
		d = leaf.getOutputStream();
		d.write(0); // version
		// A 1-byte specification of the version of this time-to-sample atom.

		d.write(0); // flag[0]
		d.write(0); // flag[1]
		d.write(0); // flag[2]
		// A 3-byte space for time-to-sample flags. Set this field to 0.

		// count runs of video frame durations
		int runCount = 1;
		int prevDuration = videoFrames.size() == 0 ? 0
				: videoFrames.get(0).duration;
		for (Sample s : videoFrames) {
			if (s.duration != prevDuration) {
				runCount++;
				prevDuration = s.duration;
			}
		}
		d.writeInt(runCount); // numberOfEntries
		// A 32-bit integer containing the count of entries in the
		// time-to-sample table.

		int runLength = 0;
		prevDuration = videoFrames.size() == 0 ? 0
				: videoFrames.get(0).duration;
		for (Sample s : videoFrames) {
			if (s.duration != prevDuration) {
				if (runLength > 0) {
					d.writeInt(runLength); // timeToSampleTable[0].sampleCount
					// A 32-bit integer that specifies the number of consecutive
					// samples that have the same duration.

					d.writeInt(prevDuration); // timeToSampleTable[0].sampleDuration
					// A 32-bit integer that specifies the duration of each
					// sample.
				}
				prevDuration = s.duration;
				runLength = 1;
			} else {
				runLength++;
			}
		}
		if (runLength > 0) {
			d.writeInt(runLength); // timeToSampleTable[0].sampleCount
			// A 32-bit integer that specifies the number of consecutive
			// samples that have the same duration.

			d.writeInt(prevDuration); // timeToSampleTable[0].sampleDuration
			// A 32-bit integer that specifies the duration of each
			// sample.
		}
		/* sample to chunk atom -------- */
		// The sample-to-chunk atom contains a table that maps samples to chunks
		// in the media data stream. By examining the sample-to-chunk atom, you
		// can determine the chunk that contains a specific sample.
		leaf = new DataAtom("stsc");
		stblAtom.add(leaf);
		/*
		 * typedef struct { byte version; byte[3] flags; int numberOfEntries;
		 * sampleToChunkTable sampleToChunkTable[numberOfEntries]; }
		 * sampleToChunkAtom;
		 * 
		 * typedef struct { int firstChunk; int samplesPerChunk; int
		 * sampleDescription; } sampleToChunkTable;
		 */
		d = leaf.getOutputStream();
		d.write(0); // version
		// A 1-byte specification of the version of this time-to-sample atom.

		d.write(0); // flag[0]
		d.write(0); // flag[1]
		d.write(0); // flag[2]
		// A 3-byte space for time-to-sample flags. Set this field to 0.

		d.writeInt(1); // number of entries
		// A 32-bit integer containing the count of entries in the
		// sample-to-chunk table.

		d.writeInt(1); // first chunk
		// The first chunk number using this table entry.

		d.writeInt(1); // samples per chunk
		// The number of samples in each chunk.

		d.writeInt(1); // sample description
		// The identification number associated with the sample description for
		// the sample. For details on sample description atoms, see "Sample
		// Description Atoms.":
		// http://developer.apple.com/documentation/QuickTime/QTFF/QTFFChap2/chapter_3_section_5.html#//apple_ref/doc/uid/TP40000939-CH204-25691

		/* sample size atom -------- */
		// The sample size atom contains the sample count and a table giving the
		// size of each sample. This allows the media data itself to be
		// unframed. The total number of samples in the media is always
		// indicated in the sample count. If the default size is indicated, then
		// no table follows.
		leaf = new DataAtom("stsz");
		stblAtom.add(leaf);
		/*
		 * typedef struct { byte version; byte[3] flags; int sampleSize; int
		 * numberOfEntries; sampleSizeTable sampleSizeTable[numberOfEntries]; }
		 * sampleSizeAtom;
		 * 
		 * typedef struct { int size; } sampleSizeTable;
		 */
		d = leaf.getOutputStream();
		d.write(0); // version
		// A 1-byte specification of the version of this time-to-sample atom.

		d.write(0); // flag[0]
		d.write(0); // flag[1]
		d.write(0); // flag[2]
		// A 3-byte space for time-to-sample flags. Set this field to 0.

		d.writeUInt(0); // sample size
		// A 32-bit integer specifying the sample size. If all the samples are
		// the same size, this field contains that size value. If this field is
		// set to 0, then the samples have different sizes, and those sizes are
		// stored in the sample size table.

		d.writeUInt(videoFrames.size()); // number of entries
		// A 32-bit integer containing the count of entries in the sample size
		// table.

		for (Sample s : videoFrames) {
			d.writeUInt(s.length); // sample size
			// The size field contains the size, in bytes, of the sample in
			// question. The table is indexed by sample number-the first entry
			// corresponds to the first sample, the second entry is for the
			// second sample, and so on.
		}
		//
		/* chunk offset atom -------- */
		// The chunk-offset table gives the index of each chunk into the
		// containing file. There are two variants, permitting the use of
		// 32-bit or 64-bit offsets. The latter is useful when managing very
		// large movies. Only one of these variants occurs in any single
		// instance of a sample table atom.
		if (videoFrames.size() == 0
				|| videoFrames.getLast().offset <= 0xffffffffL) {
			/* 32-bit chunk offset atom -------- */
			leaf = new DataAtom("stco");
			stblAtom.add(leaf);
			/*
			 * typedef struct { byte version; byte[3] flags; int
			 * numberOfEntries; chunkOffsetTable
			 * chunkOffsetTable[numberOfEntries]; } chunkOffsetAtom;
			 * 
			 * typedef struct { int offset; } chunkOffsetTable;
			 */
			d = leaf.getOutputStream();
			d.write(0); // version
			// A 1-byte specification of the version of this time-to-sample
			// atom.

			d.write(0); // flag[0]
			d.write(0); // flag[1]
			d.write(0); // flag[2]
			// A 3-byte space for time-to-sample flags. Set this field to 0.

			d.writeUInt(videoFrames.size()); // number of entries
			// A 32-bit integer containing the count of entries in the chunk
			// offset table.

			for (Sample s : videoFrames) {
				d.writeUInt(s.offset); // offset
				// The offset contains the byte offset from the beginning of the
				// data stream to the chunk. The table is indexed by chunk
				// number-the first table entry corresponds to the first chunk,
				// the second table entry is for the second chunk, and so on.
			}
		} else {
			/* 64-bit chunk offset atom -------- */
			leaf = new DataAtom("co64");
			stblAtom.add(leaf);
			/*
			 * typedef struct { byte version; byte[3] flags; int
			 * numberOfEntries; chunkOffsetTable
			 * chunkOffset64Table[numberOfEntries]; } chunkOffset64Atom;
			 * 
			 * typedef struct { long offset; } chunkOffset64Table;
			 */
			d = leaf.getOutputStream();
			d.write(0); // version
			// A 1-byte specification of the version of this time-to-sample
			// atom.

			d.write(0); // flag[0]
			d.write(0); // flag[1]
			d.write(0); // flag[2]
			// A 3-byte space for time-to-sample flags. Set this field to 0.

			d.writeUInt(videoFrames.size()); // number of entries
			// A 32-bit integer containing the count of entries in the chunk
			// offset table.

			for (Sample s : videoFrames) {
				d.writeLong(s.offset); // offset
				// The offset contains the byte offset from the beginning of the
				// data stream to the chunk. The table is indexed by chunk
				// number-the first table entry corresponds to the first chunk,
				// the second table entry is for the second chunk, and so on.
			}
		}
		//
		moovAtom.finish();
	}

	/**
	 * Writes a frame to the video track.
	 * <p>
	 * If the dimension of the video track has not been specified yet, it is
	 * derived from the first buffered image added to the QuickTimeOutputStream.
	 *
	 * @param image
	 *            The frame image.
	 * @param duration
	 *            The duration of the frame in time scale units.
	 * 
	 * @throws IllegalArgumentException
	 *             if the duration is less than 1, or if the dimension of the
	 *             frame does not match the dimension of the video track.
	 * @throws IOException
	 *             if writing the image failed.
	 */
	public void writeFrame(BufferedImage image, int duration)
			throws IOException {
		if (duration <= 0) {
			throw new IllegalArgumentException("duration must be greater 0");
		}
		ensureOpen();
		ensureStarted();

		// Get the dimensions of the first image
		if (imgWidth == -1) {
			imgWidth = image.getWidth();
			imgHeight = image.getHeight();
		} else {
			// The dimension of the image must match the dimension of the video
			// track
			if (imgWidth != image.getWidth() || imgHeight != image.getHeight()) {
				throw new IllegalArgumentException("Dimensions of image["
						+ videoFrames.size() + "] (width=" + image.getWidth()
						+ ", height=" + image.getHeight()
						+ ") differs from image[0] (width=" + imgWidth
						+ ", height=" + imgHeight);
			}
		}

		long offset = out.getStreamPosition();

		switch (videoFormat) {
		case RAW: {
			WritableRaster raster = image.getRaster();
			int[] raw = new int[imgWidth * 3]; // holds a scanline of raw image
												// data with 3 channels of 32
												// bit data
			byte[] bytes = new byte[imgWidth * 3]; // holds a scanline of raw
													// image data with 3
													// channels of 8 bit data
			for (int y = 0; y < imgHeight; y++) {
				raster.getPixels(0, y, imgWidth, 1, raw);
				for (int k = 0, n = imgWidth * 3; k < n; k++) {
					bytes[k] = (byte) raw[k];
				}
				mdatAtom.getOutputStream().write(bytes);
			}
			break;
		}
		case JPG: {
			ImageWriter iw = ImageIO.getImageWritersByMIMEType(
					"image/jpeg").next();
			ImageWriteParam iwParam = iw.getDefaultWriteParam();
			iwParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			iwParam.setCompressionQuality(quality);
			MemoryCacheImageOutputStream imgOut = new MemoryCacheImageOutputStream(
					mdatAtom.getOutputStream());
			iw.setOutput(imgOut);
			IIOImage img = new IIOImage(image, null, null);
			iw.write(null, img, iwParam);
			iw.dispose();
			break;
		}
		case PNG:
		default: {
			ImageWriter iw = ImageIO.getImageWritersByMIMEType(
					"image/png").next();
			ImageWriteParam iwParam = iw.getDefaultWriteParam();
			MemoryCacheImageOutputStream imgOut = new MemoryCacheImageOutputStream(
					mdatAtom.getOutputStream());
			iw.setOutput(imgOut);
			IIOImage img = new IIOImage(image, null, null);
			iw.write(null, img, iwParam);
			iw.dispose();
			break;
		}
		}
		long length = out.getStreamPosition() - offset;
		videoFrames.add(new Sample(duration, offset, length));
	}

	/**
	 * Writes a frame from a file to the video track.
	 * <p>
	 * This method does not inspect the contents of the file. The contents has
	 * to match the video format. For example, it is your responsibility to only
	 * add JPG files if you have chosen the JPEG video format.
	 * <p>
	 * If you add all frames from files or from input streams, then you have to
	 * explicitly set the dimension of the video track before you call finish()
	 * or close().
	 *
	 * @param file
	 *            The file which holds the image data.
	 * @param duration
	 *            The duration of the frame in time scale units.
	 * 
	 * @throws IllegalStateException
	 *             if the duration is less than 1.
	 * @throws IOException
	 *             if writing the image failed.
	 */
	public void writeFrame(File file, int duration) throws IOException {

		try (FileInputStream in = new FileInputStream(file);) {
			writeFrame(in, duration);
		}
	}

	/**
	 * Writes a frame to the video track.
	 * <p>
	 * This method does not inspect the contents of the input stream. The
	 * contents has to match the video format. For example, it is your
	 * responsibility to only add JPG files if you have chosen the JPEG video
	 * format.
	 * <p>
	 * If you add all frames from files or from input streams, then you have to
	 * explicitly set the dimension of the video track before you call finish()
	 * or close().
	 *
	 * @param in
	 *            The input stream which holds the image data.
	 * @param duration
	 *            The duration of the frame in time scale units.
	 * 
	 * @throws IllegalArgumentException
	 *             if the duration is less than 1.
	 * @throws IOException
	 *             if writing the image failed.
	 */
	public void writeFrame(InputStream in, int duration) throws IOException {
		if (duration <= 0) {
			throw new IllegalArgumentException("duration must be greater 0");
		}
		ensureOpen();
		ensureStarted();

		long offset = out.getStreamPosition();
		try (OutputStream mdatOut = mdatAtom.getOutputStream();) {
			byte[] buf = new byte[512];
			int len;
			while ((len = in.read(buf)) != -1) {
				mdatOut.write(buf, 0, len);
			}
			long length = out.getStreamPosition() - offset;
			videoFrames.add(new Sample(duration, offset, length));
		}
	}

	private void writeProlog() throws IOException {
		/*
		 * File type atom
		 * 
		 * typedef struct { magic brand; bcd4 versionYear; bcd2 versionMonth;
		 * bcd2 versionMinor; magic[4] compatibleBrands; } ftypAtom;
		 */
		DataAtom ftypAtom = new DataAtom("ftyp");
		DataAtomOutputStream d = ftypAtom.getOutputStream();
		d.writeType("qt  "); // brand
		d.writeBCD4(2005); // versionYear
		d.writeBCD2(3); // versionMonth
		d.writeBCD2(0); // versionMinor
		d.writeType("qt  "); // compatibleBrands
		d.writeInt(0); // compatibleBrands (0 is used to denote no value)
		d.writeInt(0); // compatibleBrands (0 is used to denote no value)
		d.writeInt(0); // compatibleBrands (0 is used to denote no value)
		ftypAtom.finish();
	}
}
