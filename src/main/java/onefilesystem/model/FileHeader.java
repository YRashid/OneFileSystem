package onefilesystem.model;

import java.io.Serializable;

import lombok.Data;

import static onefilesystem.utils.Constants.CONTENT_START_POSITION;
import static onefilesystem.utils.Constants.FILE_HEADER_SIZE;
import static onefilesystem.utils.Constants.MAX_FILE_SIZE;

@Data
public class FileHeader implements Serializable {
    private final long order;
    private final long headerPosition;
    private final long contentPosition;

    private String fileName;
    private boolean isDeleted;
    private int contentRealSize;

    public FileHeader(long order, String fileName) {
        this.order = order;
        this.fileName = fileName;
        this.isDeleted = false;
        this.headerPosition = order * FILE_HEADER_SIZE;
        this.contentPosition = CONTENT_START_POSITION + order * MAX_FILE_SIZE;
        this.contentRealSize = 0;
    }
}
