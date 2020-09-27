package onefilesystem.exception;

public class TooLargeFileException extends OneFileSystemException {

    private static final String ERROR_TEMPLATE = "File %s is too large. Size is %s";

    public TooLargeFileException(String fileName, int size) {
        super(String.format(ERROR_TEMPLATE, fileName, size));
    }
}
