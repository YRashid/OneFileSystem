package onefilesystem.utils;

public class Constants {
    private Constants() {
    }

    public static final int MAX_FILES_COUNT = 1024;
    public static final int FILE_HEADER_SIZE = 256;
    public static final int MAX_FILE_SIZE = 1_0000_000;
    public static final long CONTENT_START_POSITION = (long) MAX_FILES_COUNT * FILE_HEADER_SIZE;
    public static final int INT_SIZE = 4;

}
