package ch.wintersoft.java.snippets.stream.filevisitor;

/**
 * Represents the fundamental type of a filesystem entry encountered during a file tree traversal.
 *
 * <p>This enum is used to categorize paths identified by the {@link java.nio.file.FileVisitor} to
 * distinguish between containers (directories) and leaf nodes (regular files).
 */
public enum FileType {
  /** Identifies a filesystem entry that acts as a container for other files and directories. */
  DIRECTORY,
  /**
   * Identifies a regular filesystem entry containing data, excluding special Unix types like pipes,
   * sockets, or devices.
   */
  FILE
}
