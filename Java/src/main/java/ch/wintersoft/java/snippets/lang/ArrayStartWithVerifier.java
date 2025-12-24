package ch.wintersoft.java.snippets.lang;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class ArrayStartWithVerifier {

  private ArrayStartWithVerifier() {}

  /**
   * Verifies if a byte array starts with the byte representation of a string.
   *
   * @param sourceArray The byte array to check.
   * @param prefix The string prefix to look for.
   * @return true if the source starts with the prefix; false otherwise.
   */
  public static boolean startsWith(byte[] sourceArray, String prefix) {
    // if either array or prefix is null it cannot match
    if (sourceArray == null || prefix == null) {
      return false;
    }

    // Convert the prefix String to a byte array
    byte[] prefixByteArray = prefix.getBytes(StandardCharsets.UTF_8);

    // If the prefix is longer than the source array it cannot match
    if (prefixByteArray.length > sourceArray.length) {
      return false;
    }

    // We use Arrays.mismatch (Java 9+) to check if there is a mismatch from the beginning until the
    // end of the prefixByteArray.
    return Arrays.mismatch(
            sourceArray, 0, prefixByteArray.length, prefixByteArray, 0, prefixByteArray.length)
        == -1;
  }
}
