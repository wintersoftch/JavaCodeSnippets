package ch.wintersoft.java.snippets.stream.filevisitor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FileOwnershipVisitorTest {

  private FileOwnershipVisitor visitor;
  private final String testOwner = "testuser";

  @BeforeEach
  void setUp() {
    visitor = new FileOwnershipVisitor(testOwner);
  }

  @Test
  void getFileEntryList_ReturnsEmptyListInitially() {
    assertTrue(visitor.getFileEntryList().isEmpty());
  }

  @Test
  void preVisitDirectory_OwnerMatches_DoesNotAddEntry() {
    Path dir = mock(Path.class);
    BasicFileAttributes attrs = mock(BasicFileAttributes.class);

    // Mock Files.getOwner to return matching owner
    try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
      UserPrincipal owner = mock(UserPrincipal.class);
      when(owner.getName()).thenReturn(testOwner);
      mockedFiles.when(() -> Files.getOwner(any(Path.class))).thenReturn(owner);

      visitor.preVisitDirectory(dir, attrs);

      assertTrue(visitor.getFileEntryList().isEmpty());
    }
  }

  @Test
  void preVisitDirectory_OwnerMismatch_AddsDirectoryEntry() {
    Path dir = mock(Path.class);
    when(dir.toString()).thenReturn("/testdir");
    BasicFileAttributes attrs = mock(BasicFileAttributes.class);
    when(attrs.isDirectory()).thenReturn(true);

    try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
      UserPrincipal otherOwner = mock(UserPrincipal.class);
      when(otherOwner.getName()).thenReturn("otheruser");
      mockedFiles.when(() -> Files.getOwner(eq(dir))).thenReturn(otherOwner);

      visitor.preVisitDirectory(dir, attrs);

      var entries = visitor.getFileEntryList();
      assertEquals(1, entries.size());
      assertEquals("/testdir", entries.getFirst().path());
      assertEquals("otheruser", entries.getFirst().owner());
      assertEquals(FileType.DIRECTORY, entries.getFirst().type());
    }
  }

  @Test
  void visitFile_OwnerMatches_DoesNotAddEntry() {
    Path dir = mock(Path.class);
    when(dir.toString()).thenReturn("/testdir");
    BasicFileAttributes attrs = mock(BasicFileAttributes.class);
    when(attrs.isDirectory()).thenReturn(true);

    try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
      UserPrincipal otherOwner = mock(UserPrincipal.class);
      when(otherOwner.getName()).thenReturn("otheruser");
      mockedFiles.when(() -> Files.getOwner(eq(dir))).thenReturn(otherOwner);

      visitor.preVisitDirectory(dir, attrs);

      var entries = visitor.getFileEntryList();
      assertEquals(1, entries.size());
      var entry = entries.getFirst();
      assertEquals("/testdir", entry.path());
      assertEquals("otheruser", entry.owner());
      assertEquals(FileType.DIRECTORY, entry.type());
    }
  }

  @Test
  void visitFile_OwnerMismatch_AddsFileEntry() {
    Path file = mock(Path.class);
    when(file.toString()).thenReturn("/testfile");
    BasicFileAttributes attrs = mock(BasicFileAttributes.class);

    try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
      UserPrincipal otherOwner = mock(UserPrincipal.class);
      when(otherOwner.getName()).thenReturn("otheruser");
      mockedFiles.when(() -> Files.getOwner(eq(file))).thenReturn(otherOwner);

      visitor.visitFile(file, attrs);

      var entries = visitor.getFileEntryList();
      assertEquals(1, entries.size());
      assertEquals(FileType.FILE, entries.getFirst().type());
    }
  }

  @Test
  void visitFileFailed_AccessDenied_AddsEntryWithAccessDenied() {
    Path filePath = mock(Path.class);
    when(filePath.toString()).thenReturn("/inaccessible");

    try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
      // Mock ALL Files calls used in visitFileFailed
      mockedFiles.when(() -> Files.isDirectory(eq(filePath))).thenReturn(false);
      mockedFiles
          .when(() -> Files.getOwner(eq(filePath)))
          .thenThrow(new IOException("Cannot get owner"));

      visitor.visitFileFailed(filePath, new AccessDeniedException("/inaccessible"));

      var entries = visitor.getFileEntryList();
      assertEquals(1, entries.size());
      var entry = entries.getFirst();
      assertEquals("/inaccessible", entry.path());
      assertEquals("Unknown", entry.owner()); // From resolveOwnerSilently catching IOException
      assertEquals(FileType.FILE, entry.type());
      assertEquals("Access Denied", entry.problem().get());
    }
  }

  @Test
  void visitFileFailed_DirectoryPath_UsesDirectoryType() {
    Path dirPath = mock(Path.class);
    when(dirPath.toString()).thenReturn("/dir");

    try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
      mockedFiles.when(() -> Files.isDirectory(eq(dirPath))).thenReturn(true);
      mockedFiles
          .when(() -> Files.getOwner(eq(dirPath)))
          .thenThrow(new IOException("Cannot get owner"));

      visitor.visitFileFailed(dirPath, new IOException("Permission denied"));

      var entry = visitor.getFileEntryList().getFirst();
      assertEquals(FileType.DIRECTORY, entry.type());
      assertEquals("Unknown", entry.owner());
    }
  }

  @Test
  void findFilesNotOwnedByUser_GetOwnerFails_DoesNotAddEntry() {
    Path file = mock(Path.class);
    BasicFileAttributes attrs = mock(BasicFileAttributes.class);

    try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
      mockedFiles
          .when(() -> Files.getOwner(eq(file)))
          .thenThrow(new IOException("Permission denied"));

      visitor.visitFile(file, attrs);

      assertTrue(visitor.getFileEntryList().isEmpty());
    }
  }
}
