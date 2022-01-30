package github.yeori;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collection;

public class IO {

    public static <T> void flush(String fileName, Collection<T> datum) {
        try {
            Path f = Files.createFile(Path.of(fileName));
            datum.forEach(row -> {
                try {

                    Files.write(f, row.toString().getBytes(), StandardOpenOption.APPEND);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
