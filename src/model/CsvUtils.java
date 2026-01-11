package model;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CsvUtils {

    public static List<String[]> readCsv(String path) throws IOException {
        List<String[]> rows = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {

            String line;
            boolean headerSkipped = false;

            while ((line = br.readLine()) != null) {

                if (!headerSkipped) { 
                    headerSkipped = true; 
                    continue; 
                }

                // Split on commas that are NOT inside quotes
                String[] values = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");

                // Remove surrounding quotes (optional)
                for (int i = 0; i < values.length; i++) {
                    values[i] = values[i].replaceAll("^\"|\"$", "").trim();
                }

                rows.add(values);
            }
        }
        return rows;
    }

    public static void appendLine(String path, String[] values) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(path, true))) {
            bw.write(String.join(",", values));
            bw.newLine();
        }
    }
    
    // ============================================================
    // NEW METHOD: Write entire CSV file
    // ============================================================
    public static void writeCsv(String path, List<String[]> data) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(path))) {
            for (String[] row : data) {
                // Format each field - add quotes if needed
                String[] formattedRow = new String[row.length];
                for (int i = 0; i < row.length; i++) {
                    String field = row[i];
                    // Add quotes if field contains comma or quotes
                    if (field.contains(",") || field.contains("\"")) {
                        field = "\"" + field.replace("\"", "\"\"") + "\"";
                    }
                    formattedRow[i] = field;
                }
                bw.write(String.join(",", formattedRow));
                bw.newLine();
            }
        }
    }
    
    // ============================================================
    // NEW METHOD: Read CSV with header included
    // (Useful for rewriting the entire file)
    // ============================================================
    public static List<String[]> readCsvWithHeader(String path) throws IOException {
        List<String[]> rows = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Split on commas that are NOT inside quotes
                String[] values = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");

                // Remove surrounding quotes (optional)
                for (int i = 0; i < values.length; i++) {
                    values[i] = values[i].replaceAll("^\"|\"$", "").trim();
                }

                rows.add(values);
            }
        }
        return rows;
    }
}