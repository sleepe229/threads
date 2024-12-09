package cringe;

import java.io.IOException;

import static cringe.Counter.sumWithThreads;
import static cringe.Counter.sumWithoutThreads;
import static cringe.FileGeneration.newFile;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        final int COUNT_OF_FILES = 7750;
        final int COUNT_OF_LINES = 10000;
        final int COUNT_OF_THREADS = 100;
        final String PATH = newFile(COUNT_OF_FILES, COUNT_OF_LINES);

        var startOneThreadTime = System.nanoTime();
        System.out.println(sumWithoutThreads(PATH));
        var endOneThreadTime = System.nanoTime();

        var startManyThreadTime = System.nanoTime();
        System.out.println(sumWithThreads(PATH, COUNT_OF_THREADS, COUNT_OF_FILES));
        var endManyThreadTime = System.nanoTime();

        System.out.println(Time.executeTime(startOneThreadTime, endOneThreadTime) + " " + Time.executeTime(startManyThreadTime, endManyThreadTime));
    }
}