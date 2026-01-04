package ch.wintersoft.java.snippets.lang.enums;

/**
 * Represents sequential enum values with cycling navigation.
 *
 * <p>This enum defines three ordered positions: {@link #FIRST}, {@link #SECOND}, and {@link
 * #THIRD}. Each constant stores a human-readable string representation via the {@link #getValue()}
 * method. The {@link #next()} method provides cyclic navigation, wrapping from {@link #THIRD} back
 * to {@link #FIRST} for use cases like carousel navigation, steppers, or round-robin scheduling.
 *
 * <p><strong>Example usage:</strong>
 *
 * {@snippet lang="java" :
 * NextEnum current = NextEnum.FIRST;
 * System.out.println(current.getValue());  // "first"
 * System.out.println(current.next().getValue());  // "second"
 * System.out.println(NextEnum.THIRD.next().getValue());  // "first" (cycles)
 * }
 */
public enum NextEnum {
  FIRST("first"),
  SECOND("second"),
  THIRD("third");
  private final String value;

  NextEnum(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  /**
   * Returns the next enum value in declaration order, cycling back to {@link #FIRST} from {@link
   * #THIRD}.
   *
   * <p>This method uses modular arithmetic on {@link #ordinal()} for efficient O(1) cycling
   * regardless of enum size. Adding enum constants requires no method changes.
   *
   * @return the subsequent enum value, or the first if currently last
   */
  public NextEnum next() {
    return NextEnum.values()[(this.ordinal() + 1) % NextEnum.values().length];
  }
}
