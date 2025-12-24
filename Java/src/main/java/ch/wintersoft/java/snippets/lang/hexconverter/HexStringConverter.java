package ch.wintersoft.java.snippets.lang.hexconverter;

/**
 * Converts strings to/from hexadecimal representations in multiple formats.
 *
 * <p>This utility class provides bidirectional conversion between plain text and hexadecimal
 * strings. The {@link #toHex(String, HexFormat)} method encodes each character as a two-digit
 * uppercase hex value using the specified format. The {@link #fromHex(String)} method decodes hex
 * strings back to text, supporting multiple input formats while enforcing strict validation.
 *
 * <h3>Supported Formats (toHex)</h3>
 *
 * <ul>
 *   <li>{@link HexFormat#BRACKETED}: <code>[48][65][6C][6C][6F]</code>
 *   <li>{@link HexFormat#ANGLE}: <code>&lt;48&gt;&lt;65&gt;&lt;6C&gt;&lt;6C&gt;&lt;6F&gt;</code>
 *   <li>{@link HexFormat#PLAIN}: <code>48656C6C6F</code>
 *   <li>{@link HexFormat#PREFIXED}: <code>0x48 0x65 0x6C 0x6C 0x6F</code>
 *   <li>{@link HexFormat#SPACE_SEPARATED}: <code>48 65 6C 6C 6F</code>
 * </ul>
 *
 * <h3>fromHex Input Requirements</h3>
 *
 * <p>The input must contain <strong>only</strong> hexadecimal content with optional decorations:
 *
 * <ul>
 *   <li>Allowed characters: <code>0-9A-Fa-fxX[]&lt;&gt; </code> (spaces)
 *   <li>Any other characters cause {@link IllegalArgumentException}
 *   <li>After stripping decorations, must contain only hex digits
 *   <li>Final hex length must be even (complete pairs)
 * </ul>
 *
 * <p>Non-printable characters (ASCII 0-31, 127+) are represented as <code>&lt;XX&gt;</code> in
 * output.
 */
public class HexStringConverter {

  /**
   * Converts a string to its hexadecimal representation using the specified format.
   *
   * @param input the input string to encode (null returns null)
   * @param format the output format ({@link HexFormat})
   * @return hexadecimal string in the specified format, or null if input is null
   * @throws NullPointerException if format is null
   */
  public String toHex(String input, HexFormat format) {
    if (input == null) return null;

    StringBuilder sb = new StringBuilder(input.length() * 4);
    for (char c : input.toCharArray()) {
      String hexPair = String.format("%02X", (int) c);

      switch (format) {
        case BRACKETED:
          sb.append('[').append(hexPair).append(']');
          break;
        case ANGLE:
          sb.append('<').append(hexPair).append('>');
          break;
        case PLAIN:
          sb.append(hexPair);
          break;
        case PREFIXED:
          if (!sb.isEmpty()) sb.append(' ');
          sb.append("0x").append(hexPair);
          break;
        case SPACE_SEPARATED:
          if (!sb.isEmpty()) sb.append(' ');
          sb.append(hexPair);
          break;
      }
    }
    return sb.toString();
  }

  /**
   * Decodes a hexadecimal string back to text, supporting multiple input formats.
   *
   * <p><strong>Strict validation rules:</strong>
   *
   * <ol>
   *   <li>Input may only contain: <code>0-9A-Fa-fxX[]&lt;&gt; </code>
   *   <li>Strips: <code>0x/0X</code>, brackets <code>[]</code>, angles <code>&lt;&gt;</code>,
   *       spaces
   *   <li>Resulting hex must be valid digits only
   *   <li>Hex length must be even (complete byte pairs)
   * </ol>
   *
   * <p><strong>Examples of valid inputs:</strong>
   *
   * <pre>
   * [48][65][6C] → "Hel"
   * &lt;48&gt;&lt;65&gt; → "He"
   * 48656C6C6F → "Hello"
   * 0x48 0x65 → "He"
   * 48 65 6C → "Hel"
   * </pre>
   *
   * <p><strong>Examples of invalid inputs (throw IllegalArgumentException):</strong>
   *
   * <pre>
   * "48 ZZ 6C"     → Z not hex
   * "48-65-6C"     → - not allowed
   * "486"          → odd length (3 chars)
   * "Text 4865"    → Text not allowed
   * </pre>
   *
   * <p>Non-printable characters become <code>&lt;XX&gt;</code> (e.g. newline → <code>&lt;0A&gt;
   * </code>).
   *
   * @param input the hex-encoded string
   * @return decoded string with non-printables as &lt;XX&gt;
   * @throws IllegalArgumentException if input violates validation rules
   * @throws NullPointerException if input is null (returns null instead)
   */
  public String fromHex(String input) {
    if (input == null) {
      return null;
    }

    // Validate allowed characters only:
    //  digits, A-F/a-f, x, brackets, angle brackets, spaces.
    //  Anything else is rejected.
    if (!input.matches("[0-9A-Fa-fxX\\[\\]<>\\s]*")) {
      throw new IllegalArgumentException("Input contains invalid characters: " + input);
    }

    // Strip known wrappers: "0x"/"0X", brackets, angles, and spaces.
    String normalized = stripDecorations(input);

    // Return empty String if normalized text is empty
    if (normalized.isEmpty()) {
      return "";
    }

    // After stripping, only hex digits are allowed.
    if (!normalized.matches("[0-9A-Fa-f]*")) {
      throw new IllegalArgumentException(
          "Input must contain only hexadecimal characters after normalization: " + normalized);
    }

    // Must be an even number of hex characters (pairs).
    if (normalized.length() % 2 != 0) {
      throw new IllegalArgumentException(
          "Normalized hex string must have even length: " + normalized.length());
    }

    // Decode pairs; non-printables stay as <XX>
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < normalized.length(); i += 2) {
      String hexPair = normalized.substring(i, i + 2);
      int code = Integer.parseInt(hexPair, 16);
      char c = (char) code;
      if (c < 32 || c > 126) {
        sb.append('<').append(hexPair.toUpperCase()).append('>');
      } else {
        sb.append(c);
      }
    }
    return sb.toString();
  }

  /**
   * Removes hexadecimal decorations from input string.
   *
   * <p>Strips:
   *
   * <ul>
   *   <li><code>0x</code> and <code>0X</code> prefixes
   *   <li>Square brackets <code>[]</code>
   *   <li>Angle brackets <code>&lt;&gt;</code>
   *   <li>All whitespace
   * </ul>
   *
   * @param input string with decorations
   * @return pure hexadecimal digits
   */
  private String stripDecorations(String input) {
    String s = input;

    // Remove "0x" / "0X" prefixes (standalone or space-separated)
    s = s.replaceAll("0[xX]", "");

    // Remove square brackets, angle brackets, and whitespace
    s =
        s.replace("[", "")
            .replace("]", "")
            .replace("<", "")
            .replace(">", "")
            .replaceAll("\\s+", "");

    return s;
  }
}
