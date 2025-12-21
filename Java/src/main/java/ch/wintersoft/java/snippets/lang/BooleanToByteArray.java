package ch.wintersoft.java.snippets.lang;

import java.io.ByteArrayOutputStream;

public class BooleanToByteArray {

  /**
   * Convert all the given booleans to an array of bytes.
   *
   * <p>For every 8 boolean values one byte is created where the first parameter becomes the least
   * significant bit (LSB) Examples:
   *
   * <ul>
   *   <li>convertBooleansToByteArray(true)=byte[]{1}
   *   <li>convertBooleansToByteArray(false, true)=byte[]{2}
   * </ul>
   *
   * @param booleans a vararg sequence of boolean values to be converted to byte[]
   * @return Array of bytes representing the received boolean values
   */
  public byte[] convertBooleansToByteArray(boolean... booleans) {
    byte aByte = 0;
    final double bitCount = 8;
    ByteArrayOutputStream bout = new ByteArrayOutputStream();
    for (int i = 0; i < booleans.length; i++) {
      // Mask with 0xFF to treat byte as unsigned during bitwise OR, then shift the bit into
      // position
      aByte = (byte) ((aByte & 0xFF) | (booleans[i] ? 1 : 0) << (int) (i % bitCount));
      // Flush to stream if byte is full or if we've reached the end of the input
      if ((i > 0 && (i + 1) % bitCount == 0) || i == booleans.length - 1) {
        bout.write(aByte);
        aByte = 0;
      }
    }
    return bout.toByteArray();
  }
}
