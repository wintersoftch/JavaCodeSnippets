package ch.wintersoft.java.snippets.docker;

import static org.junit.jupiter.api.Assertions.assertSame;

import java.net.InetAddress;
import java.net.UnknownHostException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class MergeableDockerNetworkTest {
  @Test
  void mergingEqualNetworks_succeeds() throws UnknownHostException {
    MergeableDockerNetwork n1 =
        new MergeableDockerNetwork("a", InetAddress.getByName("192.168.0.1"));
    MergeableDockerNetwork n2 =
        new MergeableDockerNetwork("a", InetAddress.getByName("192.168.0.1"));
    assertSame(n1, n1.merge(n1));
    assertSame(n1, n1.merge(n2));
    assertSame(n2, n2.merge(n1));

    MergeableDockerNetwork n3 = new MergeableDockerNetwork("a");
    MergeableDockerNetwork n4 = new MergeableDockerNetwork("a");
    assertSame(n3, n3.merge(n4));
    assertSame(n4, n4.merge(n3));
  }

  @Test
  void mergingNetworksWithDifferentNames_throwsIllegalArgumentException()
      throws UnknownHostException {
    MergeableDockerNetwork n1 =
        new MergeableDockerNetwork("a", InetAddress.getByName("192.168.0.1"));
    MergeableDockerNetwork n2 =
        new MergeableDockerNetwork("b", InetAddress.getByName("192.168.0.1"));
    Assertions.assertThrows(
        IllegalArgumentException.class,
        () -> n1.merge(n2),
        "An IllegalArgumentException was expected");
  }

  @Test
  void mergingNetworksWithDifferentInetAddresses_throwsIllegalArgumentException()
      throws UnknownHostException {
    MergeableDockerNetwork n1 =
        new MergeableDockerNetwork("a", InetAddress.getByName("192.168.0.1"));
    MergeableDockerNetwork n2 =
        new MergeableDockerNetwork("a", InetAddress.getByName("192.168.0.2"));
    Assertions.assertThrows(
        IllegalArgumentException.class,
        () -> n1.merge(n2),
        "An IllegalArgumentException was expected");
  }

  @Test
  void mergeOneNullInetAddress_succeeds() throws UnknownHostException {
    MergeableDockerNetwork n1 =
        new MergeableDockerNetwork("a", InetAddress.getByName("192.168.0.1"));
    MergeableDockerNetwork n2 = new MergeableDockerNetwork("a");
    assertSame(n1, n1.merge(n2));
    assertSame(n1, n1.merge(n1));
    assertSame(n1, n2.merge(n1));
    assertSame(n1, n2.merge(n1));
  }
}
