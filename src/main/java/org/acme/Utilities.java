package org.acme;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Utilities {
    public Utilities() {
    }

    public String createFileNameWithTimestampSuffix(String baseFileName) {
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String timestamp = dateFormat.format(now);
        return baseFileName + "_" + timestamp;
    }
}