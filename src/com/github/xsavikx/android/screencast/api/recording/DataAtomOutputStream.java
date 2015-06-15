package com.github.xsavikx.android.screencast.api.recording;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.GregorianCalendar;

public class DataAtomOutputStream extends FilterOutputStream {

  protected static final long MAC_TIMESTAMP_EPOCH = new GregorianCalendar(1904, GregorianCalendar.JANUARY, 1)
      .getTimeInMillis();
  /**
   * The number of bytes written to the data output stream so far. If this
   * counter overflows, it will be wrapped to Integer.MAX_VALUE.
   */
  protected long written;

  public DataAtomOutputStream(OutputStream out) {
    super(out);
  }

  /**
   * Increases the written counter by the specified value until it reaches
   * Long.MAX_VALUE.
   */
  protected void incCount(int value) {
    long temp = written + value;
    if (temp < 0) {
      temp = Long.MAX_VALUE;
    }
    written = temp;
  }

  /**
   * Returns the current value of the counter <code>written</code>, the number
   * of bytes written to this data output stream so far. If the counter
   * overflows, it will be wrapped to Integer.MAX_VALUE.
   *
   * @return the value of the <code>written</code> field.
   * @see java.io.DataOutputStream#written
   */
  public final long size() {
    return written;
  }

  /**
   * Writes <code>len</code> bytes from the specified byte array starting at
   * offset <code>off</code> to the underlying output stream. If no exception is
   * thrown, the counter <code>written</code> is incremented by <code>len</code>
   * .
   *
   * @param b
   *          the data.
   * @param off
   *          the start offset in the data.
   * @param len
   *          the number of bytes to write.
   * @exception IOException
   *              if an I/O error occurs.
   * @see java.io.FilterOutputStream#out
   */
  @Override
  public synchronized void write(byte b[], int off, int len) throws IOException {
    out.write(b, off, len);
    incCount(len);
  }

  /**
   * Writes the specified byte (the low eight bits of the argument
   * <code>b</code>) to the underlying output stream. If no exception is thrown,
   * the counter <code>written</code> is incremented by <code>1</code> .
   * <p>
   * Implements the <code>write</code> method of <code>OutputStream</code>.
   *
   * @param b
   *          the <code>byte</code> to be written.
   * @exception IOException
   *              if an I/O error occurs.
   * @see java.io.FilterOutputStream#out
   */
  @Override
  public synchronized void write(int b) throws IOException {
    out.write(b);
    incCount(1);
  }

  /**
   * Writes a <code>BCD2</code> to the underlying output stream.
   *
   * @param v
   *          an <code>int</code> to be written.
   * @exception IOException
   *              if an I/O error occurs.
   * @see java.io.FilterOutputStream#out
   */
  public void writeBCD2(int v) throws IOException {
    out.write(((v % 100 / 10) << 4) | (v % 10));
    incCount(1);
  }

  /**
   * Writes a <code>BCD4</code> to the underlying output stream.
   *
   * @param v
   *          an <code>int</code> to be written.
   * @exception IOException
   *              if an I/O error occurs.
   * @see java.io.FilterOutputStream#out
   */
  public void writeBCD4(int v) throws IOException {
    out.write(((v % 10000 / 1000) << 4) | (v % 1000 / 100));
    out.write(((v % 100 / 10) << 4) | (v % 10));
    incCount(2);
  }

  /**
   * Writes out a <code>byte</code> to the underlying output stream as a 1-byte
   * value. If no exception is thrown, the counter <code>written</code> is
   * incremented by <code>1</code>.
   *
   * @param v
   *          a <code>byte</code> value to be written.
   * @exception IOException
   *              if an I/O error occurs.
   * @see java.io.FilterOutputStream#out
   */
  public final void writeByte(int v) throws IOException {
    out.write(v);
    incCount(1);
  }

  /**
   * Writes 32-bit fixed-point number divided as 16.16.
   *
   * @param f
   *          an <code>int</code> to be written.
   * @exception IOException
   *              if an I/O error occurs.
   * @see java.io.FilterOutputStream#out
   */
  public void writeFixed16D16(double f) throws IOException {
    double v = (f >= 0) ? f : -f;

    int wholePart = (int) v;
    int fractionPart = (int) ((v - wholePart) * 65536);
    int t = (wholePart << 16) + fractionPart;

    if (f < 0) {
      t = t - 1;
    }
    writeInt(t);
  }

  /**
   * Writes 32-bit fixed-point number divided as 2.30.
   *
   * @param f
   *          an <code>int</code> to be written.
   * @exception IOException
   *              if an I/O error occurs.
   * @see java.io.FilterOutputStream#out
   */
  public void writeFixed2D30(double f) throws IOException {
    double v = (f >= 0) ? f : -f;

    int wholePart = (int) v;
    int fractionPart = (int) ((v - wholePart) * 1073741824);
    int t = (wholePart << 30) + fractionPart;

    if (f < 0) {
      t = t - 1;
    }
    writeInt(t);
  }

  /**
   * Writes 16-bit fixed-point number divided as 8.8.
   *
   * @param f
   *          an <code>int</code> to be written.
   * @exception IOException
   *              if an I/O error occurs.
   * @see java.io.FilterOutputStream#out
   */
  public void writeFixed8D8(float f) throws IOException {
    float v = (f >= 0) ? f : -f;

    int wholePart = (int) v;
    int fractionPart = (int) ((v - wholePart) * 256);
    int t = (wholePart << 8) + fractionPart;

    if (f < 0) {
      t = t - 1;
    }
    writeUShort(t);
  }

  /**
   * Writes an <code>int</code> to the underlying output stream as four bytes,
   * high byte first. If no exception is thrown, the counter
   * <code>written</code> is incremented by <code>4</code>.
   *
   * @param v
   *          an <code>int</code> to be written.
   * @exception IOException
   *              if an I/O error occurs.
   * @see java.io.FilterOutputStream#out
   */
  public void writeInt(int v) throws IOException {
    out.write((v >>> 24) & 0xff);
    out.write((v >>> 16) & 0xff);
    out.write((v >>> 8) & 0xff);
    out.write((v >>> 0) & 0xff);
    incCount(4);
  }

  public void writeLong(long v) throws IOException {
    out.write((int) (v >>> 56) & 0xff);
    out.write((int) (v >>> 48) & 0xff);
    out.write((int) (v >>> 40) & 0xff);
    out.write((int) (v >>> 32) & 0xff);
    out.write((int) (v >>> 24) & 0xff);
    out.write((int) (v >>> 16) & 0xff);
    out.write((int) (v >>> 8) & 0xff);
    out.write((int) (v >>> 0) & 0xff);
    incCount(8);
  }

  /**
   * Writes a 32-bit Mac timestamp (seconds since 1902).
   * 
   * @param date
   * @throws java.io.IOException
   */
  public void writeMacTimestamp(Date date) throws IOException {
    long millis = date.getTime();
    long qtMillis = millis - MAC_TIMESTAMP_EPOCH;
    long qtSeconds = qtMillis / 1000;
    writeUInt(qtSeconds);
  }

  /**
   * Writes a Pascal String.
   * 
   * @param s
   * @throws java.io.IOException
   */
  public void writePString(String s) throws IOException {
    if (s.length() > 0xffff) {
      throw new IllegalArgumentException("String too long for PString");
    }
    if (s.length() < 256) {
      out.write(s.length());
    } else {
      out.write(0);
      writeShort(s.length()); // increments +2
    }
    for (int i = 0; i < s.length(); i++) {
      out.write(s.charAt(i));
    }
    incCount(1 + s.length());
  }

  /**
   * Writes a Pascal String padded to the specified fixed size in bytes
   *
   * @param s
   * @param length
   *          the fixed size in bytes
   * @throws java.io.IOException
   */
  public void writePString(String s, int length) throws IOException {
    if (s.length() > length) {
      throw new IllegalArgumentException("String too long for PString of length " + length);
    }
    if (s.length() < 256) {
      out.write(s.length());
    } else {
      out.write(0);
      writeShort(s.length()); // increments +2
    }
    for (int i = 0; i < s.length(); i++) {
      out.write(s.charAt(i));
    }

    // write pad bytes
    for (int i = 1 + s.length(); i < length; i++) {
      out.write(0);
    }

    incCount(length);
  }

  /**
   * Writes a signed 16 bit integer value.
   * 
   * @param v
   *          The value
   * @throws java.io.IOException
   */
  public void writeShort(int v) throws IOException {
    out.write((v >> 8) & 0xff);
    out.write((v >>> 0) & 0xff);
    incCount(2);
  }

  /**
   * Writes an Atom Type identifier (4 bytes).
   * 
   * @param s
   *          A string with a length of 4 characters.
   */
  public void writeType(String s) throws IOException {
    if (s.length() != 4) {
      throw new IllegalArgumentException("type string must have 4 characters");
    }

    try {
      out.write(s.getBytes("ASCII"), 0, 4);
      incCount(4);
    } catch (UnsupportedEncodingException e) {
      throw new InternalError(e.toString());
    }
  }

  /**
   * Writes an unsigned 32 bit integer value.
   * 
   * @param v
   *          The value
   * @throws java.io.IOException
   */
  public void writeUInt(long v) throws IOException {
    out.write((int) ((v >>> 24) & 0xff));
    out.write((int) ((v >>> 16) & 0xff));
    out.write((int) ((v >>> 8) & 0xff));
    out.write((int) ((v >>> 0) & 0xff));
    incCount(4);
  }

  public void writeUShort(int v) throws IOException {
    out.write((v >> 8) & 0xff);
    out.write((v >>> 0) & 0xff);
    incCount(2);
  }

}
