package org.acme.service;

import jakarta.enterprise.context.RequestScoped;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.acme.jasperreports.JasperInvoiceBean;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;

@RequestScoped
public class PdfDocumentGenerationService {
    public FileOutputStream generatePdfFile(String pdfFileName, String jasperReportTemplate) {
        try {

            // Load the JasperReport template
            // JasperReport compiledJasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream(jasperReportTemplate));
            JasperCompileManager.compileReportToFile(jasperReportTemplate, pdfFileName);

            // Load the compiled .jasper file
            // JasperReport jasperReport = (JasperReport) JRLoader.loadObjectFromFile(compiledJasperReport);
            // Create parameters map
            Map<String, Object> parameters = Map.of("ReportTitle", pdfFileName);
            // Create a JRBeanCollectionDataSource
            var invoiceBeanList = List.of(new JasperInvoiceBean("José", "España"));
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(invoiceBeanList);
            // Fill the report with data
            // JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

            // Export the report to PDF
            // JasperExportManager.exportReportToPdfFile(jasperPrint, pdfFileName);

//            JRPdfExporter exporter = new JRPdfExporter();
//            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
//            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(pdfOutputStream));
//
//            SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();
//            configuration.setCompressed(true);
//            exporter.setConfiguration(configuration);
//
//            exporter.exportReport();

            return new FileOutputStream(pdfFileName);
        } catch (FileNotFoundException | JRException e) {
            throw new RuntimeException(e);
        }
    }
}
