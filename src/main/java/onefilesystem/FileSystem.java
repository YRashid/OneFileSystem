package onefilesystem;

public interface FileSystem {

    void createFile(String fileName);

    void deleteFile(String fileName);

    void writeContent(String fileName, byte[] content);

    byte[] readContent(String fileName);

}
