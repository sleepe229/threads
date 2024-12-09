package cringe;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.ThreadLocalRandom;

public class FileGeneration {
    public static String newFile(final int COUNT_OF_FILES, final int COUNT_OF_LINES) {
//        clearDirectory("src/cringe/files");
//        try {
//            for (int i = 0; i < COUNT_OF_FILES; i++) {
//                System.out.println(new File("").getAbsoluteFile());
//                File file = new File("src/cringe/files/" + i + ".txt");
//                file.createNewFile();
//                addValuesToFile(file, COUNT_OF_LINES);
//            }
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
        return new File("src/cringe/files").getAbsolutePath();
    }

    public static void addValuesToFile(File file, final int COUNT_OF_LINES) {
        try (PrintWriter out = new PrintWriter(file)) {
            for (int i = 0; i < COUNT_OF_LINES; i++) {
                out.println(ThreadLocalRandom.current().nextInt(-10, 10 + 1));
                }
        } catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    public static void clearDirectory(String directoryPath) {
        File directory = new File(directoryPath);

        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();

            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        file.delete();
                    }
                }
            }
        } else {
            directory.mkdirs();
        }
    }
}
