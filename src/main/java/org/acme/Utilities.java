package org.acme;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utilities {

    public Utilities() {
    }

    /**
     * Appends a timestamp in the format "yyyyMMdd_HHmmss" to the base file name.
     * The timestamp is generated when this method is called.
     *
     * @param baseFileName the base file name without a timestamp suffix
     * @return the base file name with a timestamp suffix
     */
    public String createFileNameWithTimestampSuffix(String baseFileName) {
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String timestamp = dateFormat.format(now);
        return baseFileName + "_" + timestamp;
    }

    public String getJasperReportTemplate() {
        return "/reports/InvoiceReport.jrxml";
    }

    /**
     * @param message
     */
    public static void writeToCentralLog(String message) {
        try {
            Files.write(
                Paths.get("central-logs.txt"), (message + "\n").getBytes(),
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND
            );
        } catch (IOException e) {
            //logger.severe("Failed to write to log file: " + e.getMessage());
        }
    }
}