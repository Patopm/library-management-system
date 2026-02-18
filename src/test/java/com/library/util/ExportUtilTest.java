package com.library.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ExportUtilTest {

    @TempDir
    Path tempDir;

    @Test
    void saveToCSVWritesAllLines() throws IOException {
        Path targetFile = tempDir.resolve("report.csv");
        List<String> lines = List.of("Header1,Header2", "A,B", "C,D");
        ExportUtil.saveToCSV(targetFile.toString(), lines);

        assertEquals(lines, Files.readAllLines(targetFile));
    }

    @Test
    void saveToCSVOverwritesExistingFile() throws IOException {
        Path targetFile = tempDir.resolve("report.csv");
        ExportUtil.saveToCSV(targetFile.toString(), List.of("old"));

        List<String> newLines = List.of("new,header", "1,2");
        ExportUtil.saveToCSV(targetFile.toString(), newLines);

        assertEquals(newLines, Files.readAllLines(targetFile));
    }
}
