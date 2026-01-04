package ch.wintersoft.java.snippets.docker;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.InetAddress;
import java.net.UnknownHostException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ValidateMergeableDockerNetworkTest {

  @Test
  void addIdenticalNetworks_resultsInMergedNetwork() throws UnknownHostException {
    MergeableDockerNetworkUsage vdn = new MergeableDockerNetworkUsage();
    vdn.addNetwork(new MergeableDockerNetwork("a", InetAddress.getByName("192.168.10.1")));
    vdn.addNetwork(new MergeableDockerNetwork("a", InetAddress.getByName("192.168.10.1")));
    vdn.addNetwork(new MergeableDockerNetwork("b"));
    vdn.addNetwork(new MergeableDockerNetwork("b"));
    assertEquals(2, vdn.getNetworks().size());
  }

  @Test
  void mergeTwoNetworksWithOneEmptyInetAddress_resultsInAMergedNetwork()
      throws UnknownHostException {
    MergeableDockerNetworkUsage vdn = new MergeableDockerNetworkUsage();
    vdn.addNetwork(new MergeableDockerNetwork("a"));
    MergeableDockerNetwork n1 =
        new MergeableDockerNetwork("a", InetAddress.getByName("192.168.10.1"));
    vdn.addNetwork(n1);
    assertEquals(1, vdn.getNetworks().size());
    assertEquals(vdn.getNetworks().get("a"), n1);
  }

  @Test
  void mergeTheSameNameWithDifferentInetAddress_throwsIllegalArgumentExceptionTest()
      throws UnknownHostException {
    MergeableDockerNetworkUsage vdn = new MergeableDockerNetworkUsage();
    MergeableDockerNetwork n1 =
        new MergeableDockerNetwork("a", InetAddress.getByName("192.168.10.1"));
    MergeableDockerNetwork n2 =
        new MergeableDockerNetwork("a", InetAddress.getByName("192.168.10.2"));
    vdn.addNetwork(n1);
    Assertions.assertThrows(
        IllegalArgumentException.class,
        () -> vdn.addNetwork(n2),
        "An IllegalArgumentException was expected");
    assertEquals(1, vdn.getNetworks().size());
  }
}
