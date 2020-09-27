package onefilesystem;

import java.io.Closeable;

public interface FileSystem extends Closeable {

    void createFile(String fileName);

    void deleteFile(String fileName);

    void writeContent(String fileName, byte[] content);

    byte[] readContent(String fileName);

}
