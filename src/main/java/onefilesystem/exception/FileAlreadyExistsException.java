package onefilesystem.exception;

public class FileAlreadyExistsException extends OneFileSystemException {

    private static final String ERROR_TEMPLATE = "File %s already exists";

    public FileAlreadyExistsException(String fileName) {
        super(String.format(ERROR_TEMPLATE, fileName));
    }
}
