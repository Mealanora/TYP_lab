package lab1.util;

import java.io.*;

public class FileHelper {
    private static final String inputFile = "C:\\Users\\katya\\OneDrive\\Desktop\\уник\\4 курс\\ТЯП\\1 лаба\\TYP_lab1\\src\\input.txt";
    private static final String outputFile = "C:\\Users\\katya\\OneDrive\\Desktop\\уник\\4 курс\\ТЯП\\1 лаба\\TYP_lab1\\src\\output.txt";

    public static String read() throws IOException {
        return new String(java.nio.file.Files.readAllBytes(new File(inputFile).toPath()));
    }

    public static void write(String table, String unoptimized, String optimized) throws IOException {
        String result = String.format(
                "Таблица имён:\n%s\n\nНеоптимизированный код:\n%s\n\nОптимизированный код:\n%s",
                table, unoptimized, optimized
        );
        java.nio.file.Files.write(new File(outputFile).toPath(), result.getBytes());
    }
}
