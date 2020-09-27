package onefilesystem;

import java.io.Closeable;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;

import lombok.extern.slf4j.Slf4j;
import onefilesystem.exception.OneFileSystemException;

/**
 * Инициализирует файловую систему, если она еще не была создана.
 * Не thread-safe
 */
@Slf4j
class OneFileSystemStartupHelper implements Closeable {

    private final Path fileSystemPath;
    private RandomAccessFile randomAccessFile;
    private FileChannel channel;

    /**
     * @param fileSystemDirectory - директория в основной файловой системе
     * @param fileSystemName      - имя файла в основной файловой системе
     */
    OneFileSystemStartupHelper(String fileSystemDirectory, String fileSystemName) {
        fileSystemPath = Paths.get(fileSystemDirectory, fileSystemName);
    }

    protected FileChannel init() {
        createFileSystemFileIfNotExists(fileSystemPath);

        try {
            randomAccessFile = new RandomAccessFile(fileSystemPath.toFile(), "rw");
            channel = randomAccessFile.getChannel();
            channel.lock();
        } catch (IOException e) {
            throw new OneFileSystemException(e);
        }

        return channel;
    }

    private void createFileSystemFileIfNotExists(Path fileSystemPath) {
        boolean fileAlreadyExists;
        try {
            fileAlreadyExists = fileSystemPath.toFile().createNewFile();
        } catch (IOException e) {
            throw new OneFileSystemException(e);
        }

        if (fileAlreadyExists) {
            log.info("Created new file system : {}", fileSystemPath);
        } else {
            log.info("Found an existing file system : {}", fileSystemPath);
        }
    }

    public void close() throws IOException {
        channel.close();
        randomAccessFile.close();
    }
}
