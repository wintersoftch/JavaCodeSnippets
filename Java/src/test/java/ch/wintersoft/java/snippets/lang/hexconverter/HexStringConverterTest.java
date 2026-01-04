package ch.wintersoft.java.snippets.lang.hexconverter;

import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

class HexStringConverterTest {

  private HexStringConverter converter;

  @BeforeEach
  void setUp() {
    converter = new HexStringConverter();
  }

  @Test
  void toHex_nullInput_returnsNull() {
    assertNull(converter.toHex(null, HexFormat.BRACKETED));
  }

  @Test
  void toHex_nullFormat_throwsNullPointerException() {
    assertThrows(NullPointerException.class, () -> converter.toHex("0a", null));
  }

  @ParameterizedTest
  @MethodSource("toHexProvider")
  void toHex_correctlyEncodesString(HexFormat format, String input, String expected) {
    String result = converter.toHex(input, format);
    assertEquals(expected, result);
  }

  static Stream<Arguments> toHexProvider() {
    String helloWorld = "Hello\nWorld!";
    return Stream.of(
        Arguments.of(
            HexFormat.BRACKETED, helloWorld, "[48][65][6C][6C][6F][0A][57][6F][72][6C][64][21]"),
        Arguments.of(
            HexFormat.ANGLE, helloWorld, "<48><65><6C><6C><6F><0A><57><6F><72><6C><64><21>"),
        Arguments.of(HexFormat.PLAIN, helloWorld, "48656C6C6F0A576F726C6421"),
        Arguments.of(
            HexFormat.PREFIXED,
            helloWorld,
            "0x48 0x65 0x6C 0x6C 0x6F 0x0A 0x57 0x6F 0x72 0x6C 0x64 0x21"),
        Arguments.of(HexFormat.SPACE_SEPARATED, helloWorld, "48 65 6C 6C 6F 0A 57 6F 72 6C 64 21"),
        Arguments.of(HexFormat.BRACKETED, "A", "[41]"),
        Arguments.of(HexFormat.PLAIN, "", ""));
  }

  @Test
  void fromHex_nullInput_returnsNull() {
    assertNull(converter.fromHex(null));
  }

  @ParameterizedTest
  @MethodSource("fromHexProvider")
  void fromHex_correctlyDecodesAllFormats(String hexInput, String expected) {
    String result = converter.fromHex(hexInput);
    assertEquals(expected, result);
  }

  static Stream<Arguments> fromHexProvider() {
    String helloExpected = "Hello<0A>World!";
    return Stream.of(
        // ONLY valid hex inputs
        Arguments.of("[48][65][6C][6C][6F][0A][57][6F][72][6C][64][21]", helloExpected),
        Arguments.of("<48><65><6C><6C><6F><0A><57><6F><72><6C><64><21>", helloExpected),
        Arguments.of("48656C6C6F0A576F726C6421", helloExpected),
        Arguments.of("0x48 0x65 0x6C 0x6C 0x6F 0x0A 0x57 0x6F 0x72 0x6C 0x64 0x21", helloExpected),
        Arguments.of("48 65 6C 6C 6F 0A 57 6F 72 6C 64 21", helloExpected),
        Arguments.of("0x48 [65] 6C<6C> 6F [0A] 57", "Hello<0A>W"), // All valid pairs
        Arguments.of("0x48 0x65 0x6c", "Hel"), // Lowercase hex
        Arguments.of("", ""), // Empty
        Arguments.of("[41]", "A"));
  }

  @Test
  void fromHex_threeValidHexChars_throwsOddLength() {
    // "48656C" normalizes to "48656C" (6 chars, even)
    // But "486" would be invalid chars, caught by validateAllHexPairs
    assertThrows(
        IllegalArgumentException.class,
        () -> converter.fromHex("48 65 6")); // '6' is invalid standalone
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "48 5", // odd length
        "48 65 6C XX", // invalid hex
        "48 65 gh 6C", // invalid lowercase hex
        "[48][65]xyz" // trailing garbage
      })
  void fromHex_handlesMalformedInput_byThrowingInvalidArgumentException(String malformed) {
    assertThrows(
        IllegalArgumentException.class,
        () -> converter.fromHex(malformed)); // lowercase invalid? No, valid!
    assertThrows(IllegalArgumentException.class, () -> converter.fromHex(malformed)); // XX invalid
  }

  @Test
  void fromHex_nonPrintableChars_convertedToAngleHex() {
    String result = converter.fromHex("0D0A"); // CR LF
    assertEquals("<0D><0A>", result);
  }

  @Test
  void roundTrip_allFormats_workCorrectly() {
    String original = "Hello\nWorld!\tTest\u007F";
    HexFormat[] formats = {
      HexFormat.BRACKETED,
      HexFormat.ANGLE,
      HexFormat.PLAIN,
      HexFormat.PREFIXED,
      HexFormat.SPACE_SEPARATED
    };

    for (HexFormat format : formats) {
      String encoded = converter.toHex(original, format);
      String decoded = converter.fromHex(encoded);
      // Printable chars preserved, non-printable as <XX>
      assertEquals("Hello<0A>World!<09>Test<7F>", decoded);
    }
  }
}
