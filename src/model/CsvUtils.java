package model;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CsvUtils {

    // Reads CSV file and returns data as list of string arrays
    public static List<String[]> readCsv(String path) throws IOException {
        List<String[]> rows = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {

            String line;
            boolean headerSkipped = false;

            while ((line = br.readLine()) != null) {

               // Skip header row on first read
                if (!headerSkipped) { 
                    headerSkipped = true; 
                    continue; 
                }

             // Advanced split: commas not inside quotation marks
                String[] values = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");

             // Clean up values by removing surrounding quotes and whitespace
                for (int i = 0; i < values.length; i++) {
                    values[i] = values[i].replaceAll("^\"|\"$", "").trim();
                }

                rows.add(values);
            }
        }
        return rows;
    }

// Appends a single line to existing CSV file
    public static void appendLine(String path, String[] values) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(path, true))) {
            // Write values as comma-separated line
            bw.write(String.join(",", values));
            bw.newLine();
        }
    }
    //Made By Misbah Al Rehman. SRN: 24173647
 // Writes complete dataset to CSV file (overwrites existing content)
    public static void writeCsv(String path, List<String[]> data) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(path))) {
            for (String[] row : data) {
                // Format each field with proper quote handling
                String[] formattedRow = new String[row.length];
                for (int i = 0; i < row.length; i++) {
                    String field = row[i];
                    // Add quotes if field contains special characters
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
    
    // Reads CSV file including header row (preserves complete structure)
    public static List<String[]> readCsvWithHeader(String path) throws IOException {
        List<String[]> rows = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Advanced split for CSV with quoted values
                String[] values = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");

                // Clean up values by removing surrounding quotes and whitespace
                for (int i = 0; i < values.length; i++) {
                    values[i] = values[i].replaceAll("^\"|\"$", "").trim();
                }

                rows.add(values);
            }
        }
        return rows;
    }
}