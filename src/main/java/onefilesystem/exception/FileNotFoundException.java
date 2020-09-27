package onefilesystem.exception;

public class FileNotFoundException extends OneFileSystemException {

    private static final String ERROR_TEMPLATE = "File %s not found";

    public FileNotFoundException(String fileName) {
        super(String.format(ERROR_TEMPLATE, fileName));
    }
}
