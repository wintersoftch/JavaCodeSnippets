package ch.wintersoft.java.snippets.lang.hexconverter;

public enum HexFormat {
  PLAIN, // 48656C6C6F
  PREFIXED, // 0x48 0x65
  BRACKETED, // [48][65][6C]
  ANGLE, // <48><65><6C>
  SPACE_SEPARATED // 48 65 6C
}
