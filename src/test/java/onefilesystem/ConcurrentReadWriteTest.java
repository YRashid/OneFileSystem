package onefilesystem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import onefilesystem.exception.FileNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static java.util.concurrent.CompletableFuture.runAsync;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ConcurrentReadWriteTest {

    private static final String FILE_SYSTEM_DIRECTORY = ".";
    private static final String FILE_SYSTEM_NAME = "test-fs";
    private static final String DEFAULT_FILE_NAME = "file";
    private static final String DEFAULT_CONTENT = "default-content";
    private FileSystem fileSystem;

    private ExecutorService executorService;
    private final static int THREADS_COUNT = 50 * 2;

    @BeforeEach
    public void before() {
        fileSystem = new OneFileSystem(FILE_SYSTEM_DIRECTORY, FILE_SYSTEM_NAME);
        executorService = Executors.newFixedThreadPool(THREADS_COUNT);
    }

    @AfterEach
    public void after() throws IOException {
        Files.delete(Paths.get(FILE_SYSTEM_DIRECTORY, FILE_SYSTEM_NAME));
        executorService.shutdown();
    }


    /**
     * Проверяем, что контент файла можно будет прочитать только после того как он полностью запишется.
     * <p>
     * write потоки создают по одному файлу, read потоки пытаются прочитать контент этих файлов и сравнить контент с
     * ожидаемым.
     */
    @Test
    void test() {
        Collection<CompletableFuture<Void>> futures = new ArrayList<>(THREADS_COUNT);
        var barrier = new CyclicBarrier(THREADS_COUNT);

        for (int i = 0; i < THREADS_COUNT / 2; i++) {
            futures.add(asyncRead(i, barrier));
            futures.add(asyncWrite(i, barrier));
        }

        futures.forEach(CompletableFuture::join);
    }

    /**
     * Прочитать файл DEFAULT_FILE_NAME + id после его появления и сравнить с ожидаемым контентом
     */
    private CompletableFuture<Void> asyncRead(int id, CyclicBarrier barrier) {
        return runAsync(() -> {
            barrierAwait(barrier);

            String actualContent = tryReadUntilFileNotExists(DEFAULT_FILE_NAME + id);
            assertEquals(DEFAULT_CONTENT + id, actualContent);

        }, executorService);
    }

    /**
     * Создать файл с именем DEFAULT_FILE_NAME + id и записать в него строку DEFAULT_CONTENT + id
     */
    private CompletableFuture<Void> asyncWrite(int id, CyclicBarrier barrier) {
        return runAsync(() -> {
            barrierAwait(barrier);
            createFile(DEFAULT_FILE_NAME + id, DEFAULT_CONTENT + id);
        }, executorService);
    }

    private void barrierAwait(CyclicBarrier barrier) {
        try {
            barrier.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } catch (BrokenBarrierException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Пытаемся прочесть контент.
     * <p>
     * FileNotFoundException означает, что файл еще не был создан
     * Пустой контент означает, что контент еще не был записан.
     */
    private String tryReadUntilFileNotExists(String fileName) {
        String result = null;
        boolean fileExists = false;
        while (!fileExists) {
            try {
                byte[] bytes = fileSystem.readContent(fileName);
                result = new String(bytes);

                if (!result.equals("")) {
                    fileExists = true;
                }
            } catch (FileNotFoundException unimportant) {
                // файл еще не создан, попробуем прочитать еще раз
            }
        }

        return result;
    }


    /**
     * Создать файл с именем fileName и записать в него content
     */
    private void createFile(String fileName, String content) {
        fileSystem.createFile(fileName);
        fileSystem.writeContent(fileName, content.getBytes());
    }

}
