package ch.wintersoft.java.snippets.lang;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class NormalizeDomainNamesTest {
  @Test
  void normalizeDomainNames() {
    assertEquals(
        "absolute.valid.domain.name",
        NormalizeDomainNames.normalizeDomainName("absolute.valid.domain.name"));
    assertEquals(
        "we-have.under-scores.here",
        NormalizeDomainNames.normalizeDomainName("we-have.under_scores.here"));
  }
}
