package ch.wintersoft.java.snippets.lang;

public class NormalizeDomainNames {
  private NormalizeDomainNames() {}

  /**
   * Normalize a standard domain name by replacing all characters except a-z, A-Z, 0-9, dot (.) and
   * hyphen (-) with a hyphen (-).
   *
   * <p>Note: This method does not validate the domain name according to RFC 1035 or RFC 1123. It
   * only performs a simple normalization.
   *
   * <p>Examples:
   *
   * <ul>
   *   <li>normalizeDomainName("absolute.valid.domain.name") = "absolute.valid.domain.name"
   *   <li>normalizeDomainName("we-have.under_scores.here") = "we-have.under-scores.here"
   * </ul>
   *
   * @param name Domain name to be normalized
   * @return Normalized domain name
   */
  public static String normalizeDomainName(String name) {
    return name.replaceAll("[^a-zA-Z0-9.-]+", "-");
  }
}
