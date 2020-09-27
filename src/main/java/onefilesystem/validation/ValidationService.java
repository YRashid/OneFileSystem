package onefilesystem.validation;

import java.util.Map;

import onefilesystem.exception.FileAlreadyExistsException;
import onefilesystem.exception.FileNotFoundException;
import onefilesystem.exception.TooLargeFileException;
import onefilesystem.model.FileHeader;

import static onefilesystem.utils.Constants.MAX_FILE_SIZE;

public class ValidationService {
    private ValidationService() {
    }

    /**
     * Если файл существует, то бросить FileAlreadyExistsException
     */
    public static void isFileAlreadyExists(Map<String, FileHeader> existingFiles, String fileName) {
        FileHeader fileHeader = existingFiles.get(fileName);
        if (fileHeader != null && !fileHeader.isDeleted()) {
            throw new FileAlreadyExistsException(fileName);
        }
    }

    /**
     * Если файл не существует, то бросить FileNotFoundException
     */
    public static void isFileExists(Map<String, FileHeader> existingFiles, String fileName) {
        FileHeader fileHeader = existingFiles.get(fileName);
        if (fileHeader == null || fileHeader.isDeleted()) {
            throw new FileNotFoundException(fileName);
        }
    }

    /**
     * Если размер файла больше разрешенного, то бросить TooLargeFileException
     */
    public static void checkContentSize(byte[] content, String fileName) {
        if (content.length >= MAX_FILE_SIZE) {
            throw new TooLargeFileException(fileName, content.length);
        }
    }

    public static void checkState(boolean expression, Object errorMessage) {
        if (!expression) {
            throw new IllegalStateException(String.valueOf(errorMessage));
        }
    }
}
