package ch.wintersoft.java.snippets.lang;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class BooleanToByteArrayTest {
  @Test
  void testBooleanToByte() {
    BooleanToByteArray btb = new BooleanToByteArray();

    // Tests returning a byte[] with 1 byte
    assertArrayEquals(new byte[] {0}, btb.convertBooleansToByteArray(false));
    assertArrayEquals(new byte[] {1}, btb.convertBooleansToByteArray(true));
    assertArrayEquals(new byte[] {2}, btb.convertBooleansToByteArray(false, true));
    assertArrayEquals(new byte[] {5}, btb.convertBooleansToByteArray(true, false, true));
    assertArrayEquals(new byte[] {8}, btb.convertBooleansToByteArray(false, false, false, true));
    assertArrayEquals(new byte[] {10}, btb.convertBooleansToByteArray(false, true, false, true));
    assertArrayEquals(
        new byte[] {20}, btb.convertBooleansToByteArray(false, false, true, false, true));
    assertArrayEquals(
        new byte[] {50}, btb.convertBooleansToByteArray(false, true, false, false, true, true));

    // Tests returning a byte[] with 2 bytes
    final byte[] bytes =
        btb.convertBooleansToByteArray(
            true, false, false, false, false, false, false, true, true, false, true);
    assertArrayEquals(new byte[] {-127, 5}, bytes);
  }
}
