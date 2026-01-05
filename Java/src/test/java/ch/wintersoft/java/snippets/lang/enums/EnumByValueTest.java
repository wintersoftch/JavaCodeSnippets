package ch.wintersoft.java.snippets.lang.enums;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class EnumByValueTest {

  @ParameterizedTest(name = "Value {0} should return {1}")
  @CsvSource({"1, ENUM_VALUE_1", "2, ENUM_VALUE_2"})
  @DisplayName("Should return correct enum constant for valid values")
  void getByValue_ValidValues(int input, EnumByValue expected) {
    assertEquals(expected, EnumByValue.getByValue(input));
  }

  @Test
  @DisplayName("Should return ENUM_VALUE_1 when value does not exist")
  void getByValue_InvalidValueReturnsDefault() {
    // Testing with a value that doesn't exist (99)
    assertEquals(EnumByValue.ENUM_VALUE_1, EnumByValue.getByValue(99));
  }

  @Test
  @DisplayName("getValue should return the correct underlying integer")
  void getValue_ReturnsCorrectInt() {
    assertEquals(1, EnumByValue.ENUM_VALUE_1.getValue());
    assertEquals(2, EnumByValue.ENUM_VALUE_2.getValue());
  }
}
