package onefilesystem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class OneFileSystemPositiveTest {

    private static final String FILE_SYSTEM_DIRECTORY = ".";
    private static final String FILE_SYSTEM_NAME = "test-fs";
    private static final String DEFAULT_FILE_NAME = "file";
    private static final String DEFAULT_CONTENT = "default-content";
    private static final byte[] DEFAULT_CONTENT_BYTES = DEFAULT_CONTENT.getBytes();
    private FileSystem fileSystem;

    @BeforeEach
    public void before() {
        fileSystem = new OneFileSystem(FILE_SYSTEM_DIRECTORY, FILE_SYSTEM_NAME);
    }

    @AfterEach
    public void after() throws IOException {
        Files.delete(Paths.get(FILE_SYSTEM_DIRECTORY, FILE_SYSTEM_NAME));
    }

    @Test
    void createFile() {
        fileSystem.createFile(DEFAULT_FILE_NAME);
        byte[] actualContent = fileSystem.readContent(DEFAULT_FILE_NAME);
        assertEquals(0, actualContent.length);
    }

    @Test
    void writeContent() {
        fileSystem.createFile(DEFAULT_FILE_NAME);

        fileSystem.writeContent(DEFAULT_FILE_NAME, DEFAULT_CONTENT_BYTES);

        assertContent(DEFAULT_FILE_NAME, DEFAULT_CONTENT);
    }

    @Test
    void rewriteContent() {
        fileSystem.createFile(DEFAULT_FILE_NAME);

        fileSystem.writeContent(DEFAULT_FILE_NAME, DEFAULT_CONTENT_BYTES);

        String newContent = "new-test-content";
        fileSystem.writeContent(DEFAULT_FILE_NAME, newContent.getBytes());

        assertContent(DEFAULT_FILE_NAME, newContent);
    }

    @Test
    void rewriteContentToEmpty() {
        fileSystem.createFile(DEFAULT_FILE_NAME);

        fileSystem.writeContent(DEFAULT_FILE_NAME, DEFAULT_CONTENT_BYTES);

        byte[] emptyContent = {};
        fileSystem.writeContent(DEFAULT_FILE_NAME, emptyContent);

        byte[] actualContent = fileSystem.readContent(DEFAULT_FILE_NAME);
        assertArrayEquals(emptyContent, actualContent);
    }

    @Test
    void writeContentAfterRecreate() {
        fileSystem.createFile(DEFAULT_FILE_NAME);
        fileSystem.deleteFile(DEFAULT_FILE_NAME);

        fileSystem.createFile(DEFAULT_FILE_NAME);
        fileSystem.writeContent(DEFAULT_FILE_NAME, DEFAULT_CONTENT_BYTES);

        assertContent(DEFAULT_FILE_NAME, DEFAULT_CONTENT);
    }

    @Test
    void createTenFiles() {
        for (int i = 0; i < 10; i++) {
            String fileName = DEFAULT_FILE_NAME + i;
            String expectedContent = DEFAULT_CONTENT + i;

            fileSystem.createFile(fileName);
            fileSystem.writeContent(fileName, expectedContent.getBytes());

            assertContent(fileName, expectedContent);
        }
    }

    private void assertContent(String fileName, String expectedContent) {
        byte[] actualContent = fileSystem.readContent(fileName);
        assertEquals(expectedContent, new String(actualContent));
    }
}