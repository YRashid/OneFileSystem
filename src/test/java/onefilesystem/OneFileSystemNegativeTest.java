package onefilesystem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import onefilesystem.exception.FileAlreadyExistsException;
import onefilesystem.exception.FileNotFoundException;
import onefilesystem.exception.TooLargeFileException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static onefilesystem.utils.Constants.MAX_FILE_SIZE;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OneFileSystemNegativeTest {

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
    void writeToDeletedFile() {
        fileSystem.createFile(DEFAULT_FILE_NAME);
        fileSystem.deleteFile(DEFAULT_FILE_NAME);

        assertThrows(FileNotFoundException.class,
                () -> fileSystem.writeContent(DEFAULT_FILE_NAME, DEFAULT_CONTENT_BYTES));
    }

    @Test
    void readFromDeletedFile() {
        fileSystem.createFile(DEFAULT_FILE_NAME);
        fileSystem.deleteFile(DEFAULT_FILE_NAME);

        assertThrows(FileNotFoundException.class,
                () -> fileSystem.readContent(DEFAULT_FILE_NAME));
    }

    @Test
    void deleteNotCreatedFile() {
        assertThrows(FileNotFoundException.class,
                () -> fileSystem.deleteFile(DEFAULT_FILE_NAME));
    }

    @Test
    void deleteAlreadyDeletedFile() {
        fileSystem.createFile(DEFAULT_FILE_NAME);
        fileSystem.deleteFile(DEFAULT_FILE_NAME);

        assertThrows(FileNotFoundException.class,
                () -> fileSystem.deleteFile(DEFAULT_FILE_NAME));
    }

    @Test
    void createAlreadyCreatedFile() {
        fileSystem.createFile(DEFAULT_FILE_NAME);

        assertThrows(FileAlreadyExistsException.class,
                () -> fileSystem.createFile(DEFAULT_FILE_NAME));
    }

    @Test
    void writeTooLargeContent() {
        fileSystem.createFile(DEFAULT_FILE_NAME);
        byte[] tooLargeContent = new byte[MAX_FILE_SIZE];

        assertThrows(TooLargeFileException.class,
                () -> fileSystem.writeContent(DEFAULT_FILE_NAME, tooLargeContent));
    }
}