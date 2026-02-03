package com.library.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class ExportUtil {
    public static void saveToCSV(String fileName, List<String> lines)
        throws IOException {
        Path path = Paths.get(fileName);
        Files.write(path, lines);
    }
}