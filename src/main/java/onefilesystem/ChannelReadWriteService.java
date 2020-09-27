package onefilesystem;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import onefilesystem.exception.OneFileSystemException;
import onefilesystem.model.FileHeader;

import static onefilesystem.utils.ByteBufferUtils.toByteBuffer;

/**
 * Сервис для чтения/записи данных на диске
 */
class ChannelReadWriteService {
    private final FileChannel channel;

    ChannelReadWriteService(FileChannel channel) {
        this.channel = channel;
    }

    /**
     * Прочитать данные в буффер.
     *
     * @param byteBuffer - буффер
     * @param position   - с какой позиции читать
     */
    protected void readBuffer(ByteBuffer byteBuffer, long position) {
        try {
            channel.read(byteBuffer, position);
            byteBuffer.rewind();
        } catch (IOException e) {
            throw new OneFileSystemException(e);
        }
    }

    /**
     * Обновить заголовок в файловой системе
     *
     * @param fileHeader - заголовок файла
     */
    protected void updateFileHeader(FileHeader fileHeader) throws IOException {
        ByteBuffer byteBuffer = toByteBuffer(fileHeader);
        channel.write(byteBuffer, fileHeader.getHeaderPosition());
    }

    /**
     * Обновить контент файла в файловой системе
     *
     * @param content  - контент
     * @param position - с какой позиции писать
     */
    protected void updateContent(byte[] content, long position) throws IOException {
        ByteBuffer byteBuffer = toByteBuffer(content);
        channel.write(byteBuffer, position);
    }
}
