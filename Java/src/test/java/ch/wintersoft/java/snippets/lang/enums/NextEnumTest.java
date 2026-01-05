package ch.wintersoft.java.snippets.lang.enums;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

/** Unit tests for {@link NextEnum}. */
class NextEnumTest {

  @ParameterizedTest
  @EnumSource(NextEnum.class)
  void getValue_returnsCorrectString(NextEnum nextEnum) {
    assertNotNull(nextEnum.getValue());
    assertFalse(nextEnum.getValue().isEmpty());
  }

  @ParameterizedTest
  @CsvSource({"FIRST, SECOND", "SECOND, THIRD", "THIRD, FIRST"})
  void next_returnsExpectedNextValue(NextEnum current, NextEnum expectedNext) {
    assertEquals(expectedNext, current.next());
    assertEquals(expectedNext.getValue(), current.next().getValue());
  }

  @Test
  void next_cyclesCorrectlyMultipleTimes() {
    NextEnum current = NextEnum.FIRST;
    assertEquals(NextEnum.SECOND, current.next());
    assertEquals(NextEnum.THIRD, current.next().next());
    assertEquals(NextEnum.FIRST, current.next().next().next());
    assertEquals(NextEnum.SECOND, current.next().next().next().next());
  }

  @Test
  void next_ordinalCalculationIsCorrect() {
    assertEquals(0, NextEnum.FIRST.ordinal());
    assertEquals(1, NextEnum.SECOND.ordinal());
    assertEquals(2, NextEnum.THIRD.ordinal());

    // Verify math: (ordinal + 1) % length
    assertEquals((0 + 1) % 3, NextEnum.FIRST.next().ordinal());
    assertEquals((2 + 1) % 3, NextEnum.THIRD.next().ordinal());
  }

  @Test
  void values_returnsAllThreeConstants() {
    NextEnum[] all = NextEnum.values();
    assertEquals(3, all.length);
    assertArrayEquals(new NextEnum[] {NextEnum.FIRST, NextEnum.SECOND, NextEnum.THIRD}, all);
  }
}
