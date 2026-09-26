package eu.darkbot.util;

import com.github.manolo8.darkbot.utils.FileUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class FileUtilsTest {
    @TempDir
    Path directory;

    @Test
    void rejectsZipEntriesOutsideDestination() {
        for (String entry : List.of("../outside", "nested/../../outside", "..\\outside",
                "/outside", "C:/outside", "C:\\outside", "\\\\server\\share",
                "", "/", "\\", ".", "nested/..")) {
            assertThrows(IOException.class, () -> FileUtils.resolveZipEntry(directory, entry), entry);
        }
        String sibling = "../" + directory.getFileName() + "-other/file";
        assertThrows(IOException.class, () -> FileUtils.resolveZipEntry(directory, sibling));
    }

    @Test
    void resolvesNestedZipEntries() throws IOException {
        assertEquals(directory.resolve("nested/file"), FileUtils.resolveZipEntry(directory, "nested/file"));
        assertEquals(directory.resolve("nested"), FileUtils.resolveZipEntry(directory, "nested/"));
        assertEquals(directory.resolve("file"), FileUtils.resolveZipEntry(directory, "nested/../file"));
        assertEquals(directory.resolve("nested/file"),
                FileUtils.resolveZipEntry(directory, "nested\\other/../file"));
    }

    @Test
    void failedDownloadPreservesExistingUpdateAndRemovesTemporaryFile() throws IOException {
        Path target = directory.resolve("plugin.jar");
        Files.writeString(target, "working update");
        assertThrows(IOException.class, () -> FileUtils.copyReplacing(interruptedDownload(), target));
        assertEquals("working update", Files.readString(target));
        assertEquals(List.of(target), files());
    }

    @Test
    void failedDownloadDoesNotPublishPartialJar() throws IOException {
        Path target = directory.resolve("plugin.jar");
        assertThrows(IOException.class, () -> FileUtils.copyReplacing(interruptedDownload(), target));
        assertFalse(Files.exists(target));
        assertTrue(files().isEmpty());
    }

    @Test
    void completedDownloadReplacesExistingUpdate() throws IOException {
        Path target = directory.resolve("plugin.jar");
        Files.writeString(target, "old update");
        FileUtils.copyReplacing(new ByteArrayInputStream("new update".getBytes(StandardCharsets.UTF_8)), target);
        assertEquals("new update", Files.readString(target));
        assertEquals(List.of(target), files());
    }

    @Test
    void completedDownloadCreatesNewUpdateWithoutTemporaryFile() throws IOException {
        Path target = directory.resolve("plugin.jar");
        FileUtils.copyReplacing(new ByteArrayInputStream("new update".getBytes(StandardCharsets.UTF_8)), target);
        assertEquals("new update", Files.readString(target));
        assertEquals(List.of(target), files());
    }

    private InputStream interruptedDownload() {
        return new InputStream() {
            private int remaining = 20;

            @Override
            public int read() throws IOException {
                if (remaining-- > 0) return 'x';
                throw new IOException("Download interrupted");
            }
        };
    }

    private List<Path> files() throws IOException {
        try (Stream<Path> paths = Files.list(directory)) {
            return paths.collect(Collectors.toList());
        }
    }
}
