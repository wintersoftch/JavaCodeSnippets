package ch.wintersoft.java.snippets.lang;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ArrayStartWithVerifierTest {

  @Test
  @DisplayName("Should return true when byte array starts with the prefix")
  void testStartsWith_Success() {
    byte[] data = "Hello World".getBytes(StandardCharsets.UTF_8);
    assertTrue(ArrayStartWithVerifier.startsWith(data, "Hello"), "Should match 'Hello'");
  }

  @Test
  @DisplayName("Should return false when prefix is not at the start")
  void testStartsWith_NoMatch() {
    byte[] data = "Hello World".getBytes(StandardCharsets.UTF_8);
    assertFalse(ArrayStartWithVerifier.startsWith(data, "World"), "Should not match 'World'");
  }

  @Test
  @DisplayName("Should return false when prefix is longer than source array")
  void testStartsWith_PrefixTooLong() {
    byte[] data = "Hi".getBytes(StandardCharsets.UTF_8);
    assertFalse(
        ArrayStartWithVerifier.startsWith(data, "Hello World"),
        "Prefix longer than source should fail");
  }

  @Test
  @DisplayName("Should return false if either input is null")
  void testStartsWith_NullInputs() {
    byte[] data = "Test".getBytes(StandardCharsets.UTF_8);
    assertFalse(ArrayStartWithVerifier.startsWith(null, "Test"), "Null source should return false");
    assertFalse(ArrayStartWithVerifier.startsWith(data, null), "Null prefix should return false");
  }

  @Test
  @DisplayName("Should handle empty strings and arrays correctly")
  void testStartsWith_EmptyInputs() {
    byte[] data = "Test".getBytes(StandardCharsets.UTF_8);
    byte[] emptyData = new byte[0];

    assertTrue(
        ArrayStartWithVerifier.startsWith(data, ""), "Any array should start with an empty string");
    assertTrue(
        ArrayStartWithVerifier.startsWith(emptyData, ""), "Empty array starts with empty string");
    assertFalse(
        ArrayStartWithVerifier.startsWith(emptyData, "NonEmpty"),
        "Empty array cannot start with non-empty string");
  }

  @Test
  @DisplayName("Should be case sensitive")
  void testStartsWith_CaseSensitivity() {
    byte[] data = "Java".getBytes(StandardCharsets.UTF_8);
    assertFalse(
        ArrayStartWithVerifier.startsWith(data, "java"), "Verification should be case sensitive");
  }
}
