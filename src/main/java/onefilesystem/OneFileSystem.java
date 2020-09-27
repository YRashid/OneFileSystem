package onefilesystem;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import lombok.extern.slf4j.Slf4j;
import onefilesystem.exception.OneFileSystemException;
import onefilesystem.model.FileHeader;
import onefilesystem.validation.ValidationService;
import org.apache.commons.lang3.SerializationException;

import static onefilesystem.utils.ByteBufferUtils.createFileHeaderFrom;
import static onefilesystem.utils.Constants.FILE_HEADER_SIZE;
import static onefilesystem.utils.Constants.MAX_FILES_COUNT;

@Slf4j
public class OneFileSystem implements FileSystem, Closeable {

    private final OneFileSystemStartupHelper oneFileSystemStartupHelper;

    private final ReadWriteLock lock;
    private final Map<String, FileHeader> existingFiles = new HashMap<>();
    private final AtomicLong lastFileNumber = new AtomicLong();
    private final ChannelReadWriteService readWriteService;

    /**
     * @param fileSystemDirectory - директория в основной файловой системе
     * @param fileSystemName      - имя файла в основной файловой системе
     */
    public OneFileSystem(String fileSystemDirectory, String fileSystemName) {
        oneFileSystemStartupHelper = new OneFileSystemStartupHelper(fileSystemDirectory, fileSystemName);
        FileChannel channel = oneFileSystemStartupHelper.init();

        readWriteService = new ChannelReadWriteService(channel);
        readAllFilesHeaders();
        this.lock = new ReentrantReadWriteLock();
    }

    /**
     * Создать файл с пустым контентом
     *
     * @param fileName- имя файла
     */
    public void createFile(String fileName) {
        //todo: нужно переиспользовать место удаленных файлов

        lock.writeLock().lock();

        try {
            ValidationService.isFileAlreadyExists(existingFiles, fileName);

            FileHeader fileHeader = new FileHeader(lastFileNumber.getAndIncrement(), fileName);
            readWriteService.updateFileHeader(fileHeader);
            existingFiles.put(fileHeader.getFileName(), fileHeader);
        } catch (IOException e) {
            lastFileNumber.decrementAndGet();
            throw new OneFileSystemException(e);
        } catch (RuntimeException e) {
            lastFileNumber.decrementAndGet();
            throw e;
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Прочитать контент файла
     *
     * @param fileName- имя файла
     * @return - контент
     */
    public byte[] readContent(String fileName) {
        lock.readLock().lock();

        try {
            FileHeader fileHeader = getFileHeader(fileName);
            long contentPosition = fileHeader.getContentPosition();
            int contentRealSize = fileHeader.getContentRealSize();

            ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[contentRealSize]);
            readWriteService.readBuffer(byteBuffer, contentPosition);
            return byteBuffer.array();

        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Удалить файл.
     * Помечаем в хедере файла, что файл удален
     *
     * @param fileName - имя файла
     */
    public void deleteFile(String fileName) {
        lock.writeLock().lock();

        try {
            FileHeader fileHeader = getFileHeader(fileName);
            fileHeader.setDeleted(true);
            fileHeader.setContentRealSize(0);

            readWriteService.updateFileHeader(fileHeader);
        } catch (IOException e) {
            throw new OneFileSystemException(e);
        } finally {
            lock.writeLock().unlock();
        }

    }

    /**
     * Записать контент в существующий файл
     *
     * @param fileName - имя файла
     * @param content  - контент
     */
    public void writeContent(String fileName, byte[] content) {
        ValidationService.checkContentSize(content, fileName);

        lock.writeLock().lock();

        try {
            FileHeader fileHeader = getFileHeader(fileName);
            readWriteService.updateContent(content, fileHeader.getContentPosition());

            fileHeader.setContentRealSize(content.length);
            readWriteService.updateFileHeader(fileHeader);
        } catch (IOException e) {
            throw new OneFileSystemException(e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Прочитать все заголовки файлов в файловой системе.
     */
    private void readAllFilesHeaders() {
        /*
          В идеале где-то внутри файловой системы нужно хранить общее количество файлов, но сейчас читаем пока объекты
          сериализуются
         */

        try {
            for (int i = 0; i < MAX_FILES_COUNT; i++) {
                FileHeader header = readFileHeader(i * (long) FILE_HEADER_SIZE);
                existingFiles.put(header.getFileName(), header);
                lastFileNumber.getAndIncrement();
            }
        } catch (SerializationException unimportant) {
            log.info("Read {} headers", existingFiles.size());
        }
    }

    /**
     * Прочитать заголовок файла
     *
     * @param position - смещение заголовка
     * @return - объект FileHeader
     */
    private FileHeader readFileHeader(long position) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[FILE_HEADER_SIZE]);
        readWriteService.readBuffer(byteBuffer, position);
        return createFileHeaderFrom(byteBuffer);
    }

    /**
     * Достать объект FileHeader по имени файла из мапы всех заголовков.
     * Бросает FileNotFoundException если такого файла нет.
     *
     * @param fileName - имя файла
     * @return - объект FileHeader
     */
    private FileHeader getFileHeader(String fileName) {
        ValidationService.isFileExists(existingFiles, fileName);
        return existingFiles.get(fileName);
    }

    @Override
    public void close() throws IOException {
        oneFileSystemStartupHelper.close();
    }
}
