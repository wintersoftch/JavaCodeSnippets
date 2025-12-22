package ch.wintersoft.java.snippets.jbbp.moxa;

import com.igormaznitsa.jbbp.JBBPParser;
import com.igormaznitsa.jbbp.io.JBBPByteOrder;
import com.igormaznitsa.jbbp.mapper.Bin;
import com.igormaznitsa.jbbp.mapper.BinType;

/**
 * Represents a parsed MOXA device IP report structure.
 *
 * <p>This class uses a {@link JBBPParser} to decode a binary report packet sent by MOXA networking
 * devices. The parser extracts various network and hardware attributes including serial number, IP
 * configuration, and firmware version.
 *
 * <p>The structure definition within {@link #MOXA_PARSER} describes the packet layout using the
 * JBBP DSL syntax. The corresponding fields are annotated with {@link Bin} to automatically map
 * binary parts of the stream to Java properties.
 *
 * <p>Example usage:
 *
 * <pre>{@code
 * byte[] data = ...; // raw UDP data from a Moxa device
 * MoxaIPReport report = MoxaIPReport.MOXA_PARSER.parse(data).mapTo(new MoxaIPReport());
 * System.out.println(report);
 * }</pre>
 *
 * @see com.igormaznitsa.jbbp.JBBPParser
 * @see MoxaFieldTypeProcessor
 */
public class MoxaIPReport {
  /** Internal constant script fragment to skip two bytes in the JBBP parser. */
  private static final String SKIP_2 = "skip:2;";

  /**
   * Predefined parser for decoding MOXA binary reports.
   *
   * <p>The parser defines the structure of the binary message, including headers, network
   * addresses, and firmware data. It uses a custom {@link MoxaFieldTypeProcessor} to interpret
   * specialized field types such as {@code mac}, {@code ipv4}, and {@code fw}.
   */
  public static final JBBPParser MOXA_PARSER =
      JBBPParser.prepare(
          "byte[4] header;"
              + "skip;"
              + "byte nameLength;"
              + "byte[nameLength] serverName;"
              + "skip:1;"
              + "byte hwLength;"
              + "<hex[hwLength] hwId;"
              + SKIP_2
              + "mac macAddress;"
              + SKIP_2
              + "<int serialNumber;"
              + SKIP_2
              + "ipv4 ipAddress;"
              + SKIP_2
              + "ipv4 netmask;"
              + SKIP_2
              + "ipv4 gateway;"
              + SKIP_2
              + "<fw firmware;"
              + "skip:1;"
              + "byte apLength;"
              + "<hex[apLength] apId;",
          new MoxaFieldTypeProcessor());

  /** Packet header (4 bytes) identifying the protocol version. */
  @Bin(order = 1, type = BinType.BYTE_ARRAY, arraySizeExpr = "4")
  public String header;

  /** MOXA server name (ASCII), extracted from the report. */
  @Bin(order = 2, type = BinType.BYTE_ARRAY, arraySizeExpr = "9")
  public String serverName;

  /** Hardware identifier string, parsed from hexadecimal bytes. */
  @Bin(order = 3, byteOrder = JBBPByteOrder.LITTLE_ENDIAN)
  public String hwId;

  /** Access point identifier associated with the device. */
  @Bin(order = 4, byteOrder = JBBPByteOrder.LITTLE_ENDIAN)
  public String apId;

  /** Device MAC address represented as a human-readable string. */
  @Bin(order = 5)
  public String macAddress;

  /** Device serial number in little-endian integer format. */
  @Bin(order = 6, byteOrder = JBBPByteOrder.LITTLE_ENDIAN)
  public int serialNumber;

  /** IPv4 address assigned to the device. */
  @Bin(order = 7, byteOrder = JBBPByteOrder.LITTLE_ENDIAN)
  public String ipAddress;

  /** IPv4 subnet mask. */
  @Bin(order = 8, byteOrder = JBBPByteOrder.LITTLE_ENDIAN)
  public String netmask;

  /** Default gateway IPv4 address. */
  @Bin(order = 9, byteOrder = JBBPByteOrder.LITTLE_ENDIAN)
  public String gateway;

  /** Device firmware version in numeric form. */
  @Bin(order = 10, byteOrder = JBBPByteOrder.LITTLE_ENDIAN)
  public double firmware;

  /**
   * Returns a formatted string representation of the parsed report.
   *
   * <p>The output is intended for diagnostic logging or console output, displaying all parsed
   * fields in a readable format.
   *
   * @return A multi-line string with all device properties.
   */
  @Override
  public String toString() {
    return "MOXA IP Address report\n"
        + "######################\n"
        + "protocol header: "
        + header
        + "\n"
        + "Server name:     "
        + serverName
        + "\n"
        + "Hardware ID:     "
        + hwId
        + "\n"
        + "MAC Address:     "
        + macAddress
        + "\n"
        + "Serial Number:   "
        + serialNumber
        + "\n"
        + "IP address:      "
        + ipAddress
        + "\n"
        + "Netmask:         "
        + netmask
        + "\n"
        + "Gateway:         "
        + gateway
        + "\n"
        + "Firmware:        "
        + firmware
        + "\n"
        + "AP ID:           "
        + apId
        + "\n";
  }
}
