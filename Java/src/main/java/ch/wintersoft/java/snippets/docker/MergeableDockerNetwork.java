package ch.wintersoft.java.snippets.docker;

import java.net.InetAddress;

/**
 * A MergeableDockerNetwork object represents a Docker network with a name and an optional InetAddress which can be
 * merged under certain circumstances.
 */
public record MergeableDockerNetwork(String name, InetAddress inetAddress) {

    public MergeableDockerNetwork(String name) {
        this(name, null);
    }

    /**
     * Merges two DockerNetwork objects. These object can only be merged in the following
     * cases:
     *
     * <ul>
     *   <li>both objects are equal
     *   <li>both objects have the same name and at least one inetAddress is null
     * </ul>
     *
     * @param o Another DockerNetwork object to be merged
     * @return The merged DockerNetwork object
     */
    public MergeableDockerNetwork merge(MergeableDockerNetwork o) {
        if (this.equals(o)) {
            return this;
        }

        if (!this.name.equals(o.name)) {
            throw new IllegalArgumentException("Name of both DockerNetwork objects must be identical!");
        }

        if (this.inetAddress != null && o.inetAddress != null) {
            throw new IllegalArgumentException(
                "Cannot merge DockerNetwork objects if none of the inetAddress field is null!");
        }

        if (this.inetAddress == null && o.inetAddress != null) {
            return o;
        }

        // o.inetAddress cannot be null here, so no need to check it.
        if (this.inetAddress != null) {
            return this;
        }
        // this should never happen
        throw new IllegalArgumentException("Unknown Error merging DockerNetwork objects");
    }
}
