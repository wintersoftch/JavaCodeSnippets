package ch.wintersoft.java.snippets.jbbp.moxa;

import static org.junit.jupiter.api.Assertions.*;

import com.igormaznitsa.jbbp.JBBPParser;
import com.igormaznitsa.jbbp.exceptions.JBBPCompilationException;
import com.igormaznitsa.jbbp.model.JBBPFieldDouble;
import com.igormaznitsa.jbbp.model.JBBPFieldString;
import com.igormaznitsa.jbbp.model.JBBPFieldStruct;
import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class MoxaFieldTypeProcessorTest {
  @Test
  void parseIpAddressTest() throws IOException {
    byte[] ipAddress = new byte[] {-64, -88, 30, -3};
    JBBPFieldStruct parsed =
        JBBPParser.prepare("ipv4 ipAddress;", new MoxaFieldTypeProcessor()).parse(ipAddress);
    assertEquals(
        "192.168.30.253",
        parsed.findFieldForNameAndType("ipAddress", JBBPFieldString.class).getAsString());
    parsed = JBBPParser.prepare("<ipv4 ipAddress;", new MoxaFieldTypeProcessor()).parse(ipAddress);
    assertEquals(
        "253.30.168.192",
        parsed.findFieldForNameAndType("ipAddress", JBBPFieldString.class).getAsString());
  }

  @Test
  void parseMacAddressTest() throws IOException {
    byte[] macAddress = new byte[] {0, -112, -24, -100, 13, 91};
    JBBPFieldStruct parsed =
        JBBPParser.prepare("mac macAddress;", new MoxaFieldTypeProcessor()).parse(macAddress);
    assertEquals(
        "00-90-E8-9C-0D-5B",
        parsed.findFieldForNameAndType("macAddress", JBBPFieldString.class).getAsString());
    parsed = JBBPParser.prepare("<mac macAddress;", new MoxaFieldTypeProcessor()).parse(macAddress);
    assertEquals(
        "5B-0D-9C-E8-90-00",
        parsed.findFieldForNameAndType("macAddress", JBBPFieldString.class).getAsString());
  }

  @Test
  void parseHexStringTest() throws IOException {
    byte[] hex = new byte[] {-128, 0, 113, -95};
    JBBPFieldStruct parsed =
        JBBPParser.prepare("hex[4] hex;", new MoxaFieldTypeProcessor()).parse(hex);
    assertEquals(
        "800071A1", parsed.findFieldForNameAndType("hex", JBBPFieldString.class).getAsString());
    parsed = JBBPParser.prepare("<hex[4] hex;", new MoxaFieldTypeProcessor()).parse(hex);
    assertEquals(
        "A1710080", parsed.findFieldForNameAndType("hex", JBBPFieldString.class).getAsString());
  }

  @Test
  void parseFirmwareTest() throws IOException {
    byte[] firmware = new byte[] {1, 4, 0, 0};
    JBBPFieldStruct parsed =
        JBBPParser.prepare("fw fw;", new MoxaFieldTypeProcessor()).parse(firmware);
    assertEquals(1.4, parsed.findFieldForNameAndType("fw", JBBPFieldDouble.class).getAsDouble());
    parsed = JBBPParser.prepare("<fw fw;", new MoxaFieldTypeProcessor()).parse(firmware);
    assertEquals(0.041, parsed.findFieldForNameAndType("fw", JBBPFieldDouble.class).getAsDouble());
  }

  @Test
  void mapToMoxaIPReport() throws IOException {
    byte[] moxaReport =
        new byte[] {
          77, 79, 88, 65, 1, 9, 112, 111, 119, 101, 114, 98, 111, 120, 50, 2, 2, -95, 113, 3, 6, 0,
          -112, -24, -100, 13, 91, 4, 4, -117, 34, 0, 0, 5, 4, -64, -88, 30, -3, 6, 4, -1, -1, -1,
          0, 7, 4, -64, -88, 30, 1, 8, 4, 0, 0, 4, 1, 9, 4, -95, 113, 0, -128
        };
    MoxaIPReport moxaIPReport =
        MoxaIPReport.MOXA_PARSER.parse(moxaReport).mapTo(new MoxaIPReport());

    assertEquals("MOXA", moxaIPReport.header);
    assertEquals("powerbox2", moxaIPReport.serverName);
    assertEquals("71A1", moxaIPReport.hwId);
    assertEquals("800071A1", moxaIPReport.apId);
    assertEquals("00-90-E8-9C-0D-5B", moxaIPReport.macAddress);
    assertEquals(8843, moxaIPReport.serialNumber);
    assertEquals("192.168.30.253", moxaIPReport.ipAddress);
    assertEquals("255.255.255.0", moxaIPReport.netmask);
    assertEquals("192.168.30.1", moxaIPReport.gateway);
    assertEquals(1.4, moxaIPReport.firmware);
  }

  @Test
  void hexWithoutLengthTest() {
    byte[] bytes = new byte[] {1, 4, 0, 0};
    JBBPParser parser = JBBPParser.prepare("hex exception;", new MoxaFieldTypeProcessor());
    Assertions.assertThrows(
        IllegalArgumentException.class,
        () -> parser.parse(bytes),
        "An IllegalArgumentException was expected");
  }

  @Test
  void hexWith0LengthTest() {
    MoxaFieldTypeProcessor moxaFieldTypeProcessor = new MoxaFieldTypeProcessor();
    Assertions.assertThrows(
        JBBPCompilationException.class,
        () -> JBBPParser.prepare("hex[0] exception;", moxaFieldTypeProcessor),
        "A JBBPCompilationException error was expected");
  }

  @Test
  void invalidTypeTest() {
    MoxaFieldTypeProcessor moxaFieldTypeProcessor = new MoxaFieldTypeProcessor();
    Assertions.assertThrows(
        JBBPCompilationException.class,
        () -> JBBPParser.prepare("notexist exception;", moxaFieldTypeProcessor),
        "A JBBPCompilationException error was expected");
  }
}
