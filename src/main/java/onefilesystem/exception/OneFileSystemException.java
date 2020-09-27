package onefilesystem.exception;

public class OneFileSystemException extends RuntimeException {

    public OneFileSystemException(String message) {
        super(message);
    }

    public OneFileSystemException(Exception e) {
        super(e);
    }
}
