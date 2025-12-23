package ch.wintersoft.java.snippets.stream.filevisitor;

import java.util.Optional;
import org.jspecify.annotations.NonNull;

/**
 * An immutable data carrier representing metadata and status information for a filesystem entry
 * encountered during a scan.
 *
 * <p>This record encapsulates ownership details, the nature of the filesystem object, and any
 * diagnostic messages encountered if access was restricted.
 *
 * @param path The absolute or relative path string of the filesystem entry.
 * @param owner The name of the user principal that owns the entry on the filesystem.
 * @param type The categorization of the entry as defined by {@link FileType}.
 * @param problem An {@link Optional} containing an error message (e.g., "Access Denied") if an
 *     {@link java.io.IOException} occurred during traversal; otherwise empty.
 */
public record FileEntry(
    @NonNull String path,
    @NonNull String owner,
    @NonNull FileType type,
    Optional<String> problem) {}
