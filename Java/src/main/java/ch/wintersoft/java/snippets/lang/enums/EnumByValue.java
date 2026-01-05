package ch.wintersoft.java.snippets.lang.enums;

import java.util.Arrays;

/**
 * Represents a set of constants mapped to specific integer values.
 *
 * <p>This enum provides a utility method to retrieve an instance based on its internal value, with
 * a default fallback mechanism.
 */
public enum EnumByValue {
  ENUM_VALUE_1(1),
  ENUM_VALUE_2(2);

  private final int value;

  EnumByValue(int value) {
    this.value = value;
  }

  /**
   * Gets the integer value associated with this enum constant.
   *
   * @return the integer value.
   */
  public int getValue() {
    return value;
  }

  /**
   * Looks up an enum constant by its integer value.
   *
   * @param i the integer value to look for.
   * @return the matching {@link EnumByValue}, or {@link #ENUM_VALUE_1} if no match is found.
   */
  public static EnumByValue getByValue(int i) {
    return Arrays.stream(EnumByValue.values())
        .filter(enumByValue -> enumByValue.value == i)
        .findFirst()
        .orElse(ENUM_VALUE_1);
  }
}
