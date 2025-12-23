package ch.wintersoft.java.snippets.docker;

import java.util.HashMap;
import java.util.Map;

/**
 * Aggregates and manages a collection of {@link MergeableDockerNetwork} objects.
 * <p>
 * This class ensures that network data remains consolidated by automatically
 * merging new network information with existing entries sharing the same name.
 */
public class MergeableDockerNetworkUsage {
    private final HashMap<String, MergeableDockerNetwork> networks = new HashMap<>();

    /**
     * Adds a network to the network collection.
     * <p>
     * If a network with the same name already exists, the new network data is
     * merged into the existing entry. Otherwise, a new entry is created.
     *
     * @param mergeableDockerNetwork the network data to add or merge.
     */
    public void addNetwork(MergeableDockerNetwork mergeableDockerNetwork) {
        if (networks.containsKey(mergeableDockerNetwork.name())) {
            networks.computeIfPresent(
                mergeableDockerNetwork.name(),
                (k, existingMergeableDockerNetwork) -> existingMergeableDockerNetwork.merge(mergeableDockerNetwork));
        } else {
            networks.put(mergeableDockerNetwork.name(), mergeableDockerNetwork);
        }
    }

    /**
     * Retrieves the current map of consolidated docker networks.
     *
     * @return a {@link Map} where keys are network names and values are
     * their corresponding {@link MergeableDockerNetwork} objects.
     */
    public Map<String, MergeableDockerNetwork> getNetworks() {
        return networks;
    }
}
