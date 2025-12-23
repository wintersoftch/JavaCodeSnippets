package ch.wintersoft.java.snippets.jbbp.moxa;

import com.igormaznitsa.jbbp.JBBPCustomFieldTypeProcessor;
import com.igormaznitsa.jbbp.compiler.JBBPNamedFieldInfo;
import com.igormaznitsa.jbbp.compiler.tokenizer.JBBPFieldTypeParameterContainer;
import com.igormaznitsa.jbbp.io.*;
import com.igormaznitsa.jbbp.model.*;
import java.io.IOException;

/**
 * Custom field type processor for decoding MOXA binary report data.
 *
 * <p>This processor provides custom reader implementations for special data types found in MOXA IP
 * report packets, allowing the {@link com.igormaznitsa.jbbp.JBBPParser} to map raw bytes into
 * human-readable values such as MAC addresses, IPv4 strings, or firmware versions.
 *
 * <p>Supported custom field types:
 *
 * <ul>
 *   <li><strong>mac</strong>: Parses a 6-byte MAC address into the form {@code AA-BB-CC-DD-EE-FF}
 *   <li><strong>ipv4</strong>: Parses a 4-byte IPv4 address into dotted-decimal notation
 *   <li><strong>hex</strong>: Parses a sequence of bytes into an uppercase hexadecimal string
 *   <li><strong>fw</strong>: Parses a 4-byte firmware version into a double (e.g. 1.234)
 * </ul>
 *
 * <p>This class is primarily used by {@link MoxaIPReport#MOXA_PARSER}.
 *
 * @see com.igormaznitsa.jbbp.JBBPCustomFieldTypeProcessor
 * @see com.igormaznitsa.jbbp.JBBPParser
 * @see MoxaIPReport
 */
public class MoxaFieldTypeProcessor implements JBBPCustomFieldTypeProcessor {
  /** The list of supported custom field type names. */
  private final String[] types = new String[] {"mac", "ipv4", "hex", "fw"};

  @Override
  public String[] getCustomFieldTypes() {
    return types;
  }

  @Override
  public boolean isAllowed(
      JBBPFieldTypeParameterContainer jbbpFieldTypeParameterContainer, String s, int i, boolean b) {
    return true;
  }

  /**
   * Reads and converts a custom field type from the binary input stream.
   *
   * <p>Depending on the declared field type, calls one of the specialized extract methods to
   * convert raw bytes into a string or numeric form.
   *
   * @param in The input stream from which bytes are read.
   * @param bitOrder The bit order used for reading.
   * @param parserFlags Parser flags as set by the JBBP compiler.
   * @param customTypeFieldInfo Metadata describing the custom field type.
   * @param fieldName The name information for the field being processed.
   * @param extraData Additional data value passed from script (unused here).
   * @param readWholeStream Whether to process the entire remaining stream.
   * @param arrayLength For array types, the number of array elements.
   * @return The parsed field represented as a JBBP field model instance.
   * @throws IOException If an I/O error occurs while reading from the stream.
   */
  @Override
  public JBBPAbstractField readCustomFieldType(
      JBBPBitInputStream in,
      JBBPBitOrder bitOrder,
      int parserFlags,
      JBBPFieldTypeParameterContainer customTypeFieldInfo,
      JBBPNamedFieldInfo fieldName,
      int extraData,
      boolean readWholeStream,
      int arrayLength,
      JBBPArraySizeLimiter jbbpArraySizeLimiter)
      throws IOException {

    switch (customTypeFieldInfo.getTypeName()) {
      case "mac":
        return new JBBPFieldString(
            fieldName, extractMacAddress(in, customTypeFieldInfo.getByteOrder()));
      case "ipv4":
        return new JBBPFieldString(
            fieldName, extractIpAddress(in, customTypeFieldInfo.getByteOrder()));
      case "hex":
        if (arrayLength < 1) {
          throw new IllegalArgumentException(
              "A hex String must have a certain length (e.g. hex[5]])!");
        } else {
          return new JBBPFieldString(
              fieldName, extractHexString(in, arrayLength, customTypeFieldInfo.getByteOrder()));
        }
      case "fw":
        return new JBBPFieldDouble(
            fieldName, extractFirmware(in, customTypeFieldInfo.getByteOrder()));
      default:
        // Before reaching this point we should get a JBBPCompilationException
        throw new UnsupportedOperationException(
            "No field type with name " + customTypeFieldInfo.getTypeName() + " is defined!");
    }
  }

  /**
   * Extracts a hexadecimal string from the next {@code length} bytes in the stream.
   *
   * @param in The bit input stream to read from.
   * @param length The number of bytes to read.
   * @param byteOrder The byte order to use for reading.
   * @return A hexadecimal string (uppercase) representing the read bytes.
   * @throws IOException If reading from the stream fails.
   */
  private String extractHexString(final JBBPBitInputStream in, int length, JBBPByteOrder byteOrder)
      throws IOException {
    StringBuilder sb = new StringBuilder(length * 2 + length - 1);
    byte[] bytes = in.readByteArray(length, byteOrder);
    for (byte aByte : bytes) {
      sb.append(String.format("%02X", aByte));
    }
    return sb.toString();
  }

  /**
   * Extracts and formats a MAC address from the next six bytes.
   *
   * @param in The bit input stream to read from.
   * @param byteOrder The byte order to use for reading.
   * @return The MAC address formatted as {@code AA-BB-CC-DD-EE-FF}.
   * @throws IOException If reading from the stream fails.
   */
  private String extractMacAddress(final JBBPBitInputStream in, JBBPByteOrder byteOrder)
      throws IOException {
    int length = 6;
    byte[] bytes = in.readByteArray(length, byteOrder);
    StringBuilder sb = new StringBuilder(length * 2 + length - 1);
    for (int i = 0; i < length; i++) {
      if (i > 0) {
        sb.append('-');
      }
      sb.append(String.format("%02X", bytes[i]));
    }
    return sb.toString();
  }

  /**
   * Extracts and formats an IPv4 address from the next four bytes.
   *
   * @param in The bit input stream to read from.
   * @param byteOrder The byte order to use for reading.
   * @return The IPv4 address string in dotted-decimal format (e.g. {@code 192.168.1.1}).
   * @throws IOException If reading from the stream fails.
   */
  private String extractIpAddress(final JBBPBitInputStream in, JBBPByteOrder byteOrder)
      throws IOException {
    int length = 4;
    byte[] bytes = in.readByteArray(length, byteOrder);
    StringBuilder sb = new StringBuilder(length * 2 + length - 1);
    for (int i = 0; i < length; i++) {
      if (i > 0) {
        sb.append('.');
      }
      sb.append(bytes[i] & 0xFF);
    }
    return sb.toString();
  }

  /**
   * Extracts and formats firmware version information from four bytes.
   *
   * <p>The first byte denotes the major version part, followed by three minor values concatenated
   * to form the decimal representation.
   *
   * @param in The input stream to read from.
   * @param byteOrder The byte order to use for reading.
   * @return The firmware version as a double (e.g. {@code 1.234}).
   * @throws IOException If reading from the input stream fails.
   */
  private double extractFirmware(final JBBPBitInputStream in, JBBPByteOrder byteOrder)
      throws IOException {
    int length = 4;
    byte[] bytes = in.readByteArray(length, byteOrder);
    StringBuilder sb = new StringBuilder(length + 1);
    for (int i = 0; i < length; i++) {
      sb.append(bytes[i]);
      if (i == 0) {
        sb.append(".");
      }
    }
    return Double.parseDouble(sb.toString());
  }
}
