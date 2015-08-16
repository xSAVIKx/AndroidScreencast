package com.github.xsavikx.android.screencast.api.recording;

import java.io.FilterOutputStream;
import java.io.IOException;

import javax.imageio.stream.ImageOutputStream;

public class FilterImageOutputStream extends FilterOutputStream {
  private ImageOutputStream imgOut;

  public FilterImageOutputStream(ImageOutputStream iOut) {
    super(null);
    this.imgOut = iOut;
  }

  /**
   * Closes this output stream and releases any system resources associated with
   * the stream.
   * <p>
   * The <code>close</code> method of <code>FilterOutputStream</code> calls its
   * <code>flush</code> method, and then calls the <code>close</code> method of
   * its underlying output stream.
   *
   * @exception IOException
   *              if an I/O error occurs.
   * @see java.io.FilterOutputStream#flush()
   * @see java.io.FilterOutputStream#out
   */
  @Override
  public void close() throws IOException {
    flush();
    imgOut.close();
  }

  /**
   * Flushes this output stream and forces any buffered output bytes to be
   * written out to the stream.
   * <p>
   * The <code>flush</code> method of <code>FilterOutputStream</code> calls the
   * <code>flush</code> method of its underlying output stream.
   *
   * @exception IOException
   *              if an I/O error occurs.
   * @see java.io.FilterOutputStream#out
   */
  @Override
  public void flush() {
    // System.err.println(this+" discarded flush");
    // imgOut.flush();
  }

  /**
   * Writes <code>len</code> bytes from the specified <code>byte</code> array
   * starting at offset <code>off</code> to this output stream.
   * <p>
   * The <code>write</code> method of <code>FilterOutputStream</code> calls the
   * <code>write</code> method of one argument on each <code>byte</code> to
   * output.
   * <p>
   * Note that this method does not call the <code>write</code> method of its
   * underlying input stream with the same arguments. Subclasses of
   * <code>FilterOutputStream</code> should provide a more efficient
   * implementation of this method.
   *
   * @param b
   *          the data.
   * @param off
   *          the start offset in the data.
   * @param len
   *          the number of bytes to write.
   * @exception IOException
   *              if an I/O error occurs.
   * @see java.io.FilterOutputStream#write(int)
   */
  @Override
  public void write(byte b[], int off, int len) throws IOException {
    imgOut.write(b, off, len);
  }

  /**
   * Writes the specified <code>byte</code> to this output stream.
   * <p>
   * The <code>write</code> method of <code>FilterOutputStream</code> calls the
   * <code>write</code> method of its underlying output stream, that is, it
   * performs <tt>out.write(b)</tt>.
   * <p>
   * Implements the abstract <tt>write</tt> method of <tt>OutputStream</tt>.
   *
   * @param b
   *          the <code>byte</code>.
   * @exception IOException
   *              if an I/O error occurs.
   */
  @Override
  public void write(int b) throws IOException {
    imgOut.write(b);
  }
}
