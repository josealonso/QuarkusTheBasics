package org.acme.utils;

import net.sf.jasperreports.engine.JasperCompileManager;

public class CompileReport {
    public static void main(String[] args) {
        try {
            JasperCompileManager.compileReportToFile("tuReporte.jrxml", "tuReporte.jasper");
            System.out.println("Informe compilado correctamente.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
