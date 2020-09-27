package onefilesystem.utils;

import java.nio.ByteBuffer;

import onefilesystem.model.FileHeader;
import org.apache.commons.lang3.SerializationUtils;

import static onefilesystem.utils.Constants.FILE_HEADER_SIZE;
import static onefilesystem.utils.Constants.INT_SIZE;
import static onefilesystem.utils.Constants.MAX_FILE_SIZE;
import static onefilesystem.validation.ValidationService.checkState;

public class ByteBufferUtils {
    private ByteBufferUtils() {
    }

    public static ByteBuffer toByteBuffer(FileHeader fileHeader) {

        byte[] serializedObject = SerializationUtils.serialize(fileHeader);
        checkState(INT_SIZE + serializedObject.length <= FILE_HEADER_SIZE, "Increase FILE_HEADER_SIZE");

        ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[FILE_HEADER_SIZE]);
        byteBuffer.putInt(serializedObject.length);
        byteBuffer.put(serializedObject);

        byteBuffer.rewind();
        return byteBuffer;
    }

    public static ByteBuffer toByteBuffer(byte[] content) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[MAX_FILE_SIZE]);
        byteBuffer.put(content);
        byteBuffer.rewind();
        return byteBuffer;
    }

    public static FileHeader createFileHeaderFrom(ByteBuffer byteBuffer) {
        int serializedObjectLength = byteBuffer.getInt();
        byte[] serializedObject = new byte[serializedObjectLength];
        byteBuffer.get(serializedObject);
        return SerializationUtils.deserialize(serializedObject);
    }
}
