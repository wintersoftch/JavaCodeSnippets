package ch.wintersoft.java.snippets.stream.filevisitor;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.jspecify.annotations.NonNull;

/**
 * A specialized {@link SimpleFileVisitor} that identifies filesystem entries not owned by a
 * specific user while gracefully handling permission errors.
 *
 * <p>This visitor traverses a file tree and populates a list of {@link FileEntry} objects for any
 * file or directory whose owner does not match the provided username. If a directory or file cannot
 * be accessed due to an {@link AccessDeniedException}, the error is captured within the entry
 * rather than terminating the scan.
 */
public class FileOwnershipVisitor extends SimpleFileVisitor<Path> {

  private final String fileOwner;
  private final List<FileEntry> fileEntryList = new ArrayList<>();

  /**
   * Constructs a new visitor targeting entries NOT owned by the specified user.
   *
   * @param fileOwner The name of the user whose files should be excluded from the results.
   */
  public FileOwnershipVisitor(String fileOwner) {
    this.fileOwner = fileOwner;
  }

  /**
   * Retrieves the list of discovered entries and encountered access problems.
   *
   * @return A non-null list of {@link FileEntry} objects.
   */
  public List<FileEntry> getFileEntryList() {
    return fileEntryList;
  }

  /**
   * Invoked for a directory before entries in the directory are visited.
   *
   * @param dir A reference to the directory.
   * @param attrs The directory's basic attributes.
   * @return {@link FileVisitResult#CONTINUE} to proceed with the scan.
   */
  @Override
  @NonNull
  public FileVisitResult preVisitDirectory(@NonNull Path dir, @NonNull BasicFileAttributes attrs) {
    findFilesNotOwnedByUser(dir, attrs);
    return FileVisitResult.CONTINUE;
  }

  /**
   * Invoked for a file in a directory.
   *
   * @param file A reference to the file.
   * @param attrs The file's basic attributes.
   * @return {@link FileVisitResult#CONTINUE} to proceed with the scan.
   */
  @Override
  @NonNull
  public FileVisitResult visitFile(@NonNull Path file, @NonNull BasicFileAttributes attrs) {
    findFilesNotOwnedByUser(file, attrs);
    return FileVisitResult.CONTINUE;
  }

  /**
   * Invoked for a file or directory that could not be visited. Captures access denied errors and
   * other IO exceptions as problematic entries.
   *
   * @param file A reference to the file.
   * @param exc The I/O exception that prevented the visit.
   * @return {@link FileVisitResult#CONTINUE} to attempt visiting other files.
   */
  @Override
  @NonNull
  public FileVisitResult visitFileFailed(@NonNull Path file, @NonNull IOException exc) {
    FileType type = Files.isDirectory(file) ? FileType.DIRECTORY : FileType.FILE;
    String owner = resolveOwnerSilently(file);

    fileEntryList.add(
        new FileEntry(
            file.toString(),
            owner,
            type,
            Optional.of(
                exc instanceof AccessDeniedException ? "Access Denied" : exc.getMessage())));
    return FileVisitResult.CONTINUE;
  }

  /**
   * Logic to check ownership and add to the result list if the owner does not match the exclusion
   * criteria.
   */
  private void findFilesNotOwnedByUser(Path path, BasicFileAttributes attrs) {
    try {
      String owner = Files.getOwner(path).getName();
      if (!owner.equals(fileOwner)) {
        fileEntryList.add(
            new FileEntry(
                path.toString(),
                owner,
                attrs.isDirectory() ? FileType.DIRECTORY : FileType.FILE,
                Optional.empty()));
      }
    } catch (IOException e) {
      // Usually handled by visitFileFailed if it occurs during actual walk
    }
  }

  /** Helper to retrieve the owner name safely for problematic paths. */
  private String resolveOwnerSilently(Path path) {
    try {
      return Files.getOwner(path).getName();
    } catch (IOException e) {
      return "Unknown";
    }
  }
}
