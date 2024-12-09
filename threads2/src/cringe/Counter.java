package cringe;

import java.io.IOException;
import java.nio.file.*;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class Counter {
    public static int sumWithoutThreads(String PATH) throws IOException {
        Path start = Paths.get(PATH);
        try (Stream<Path> paths = Files.walk(start)) {
            return paths.
                    filter(Files::isRegularFile)
                    .mapToInt(Counter::sumNumbersInFile)
                    .sum();
        }
    }

    public static int sumWithThreads(String PATH, int numberOfThreads, final int COUNT_OF_FILES) throws IOException, InterruptedException {
        Path start = Paths.get(PATH);
        int[] array = new int[COUNT_OF_FILES];
        final int numberOfCreatedThreads = Math.min(numberOfThreads, COUNT_OF_FILES);
        try(ExecutorService ES= Executors.newFixedThreadPool(numberOfCreatedThreads)) {
            for (int i = 0; i < numberOfThreads; i++) {
                final int finalI = i;
                ES.submit(() -> {
                    System.out.println(Thread.currentThread().getName());
                    for (int j = finalI; j < COUNT_OF_FILES; j+=numberOfThreads) {
                        array[j] = sumNumbersInFile(Path.of(start + "/" + j + ".txt"));
                    }
                });
            }
            ES.shutdown();

            if(!ES.awaitTermination(1, TimeUnit.MINUTES)) {
                throw new RuntimeException("Time await.");
            }
        }

    return Arrays.stream(array).sum();
    }

    public static int sumNumbersInFile(Path file) {
        try(Stream<String> lines = Files.lines(file)){
            return lines.mapToInt(Integer::parseInt).sum();
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        } catch (NumberFormatException e) {
            throw new NumberFormatException();
        }
    }
}
