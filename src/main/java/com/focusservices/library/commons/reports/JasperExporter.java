package com.focusservices.library.commons.reports;

import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.export.*;

import java.io.ByteArrayOutputStream;

@Slf4j
public class JasperExporter {

    private static final String MSG_CONFIG_START = "Preparando configuraciones de la exportacion";

    private JasperExporter() {
        throw new IllegalStateException("Utility class");
    }

    public static void exportToXLS(JasperPrint jasperPrint, ByteArrayOutputStream baos) throws JRException {
        log.debug(MSG_CONFIG_START);
        SimpleXlsReportConfiguration configuration = new SimpleXlsReportConfiguration();
        configuration.setOnePagePerSheet(true);
        configuration.setDetectCellType(true);
        configuration.setCollapseRowSpan(false);
        log.debug("Iniciando exportacion a excel en response");
        JRXlsExporter jrXlsExporter = new JRXlsExporter();
        jrXlsExporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        jrXlsExporter.setExporterOutput(new SimpleOutputStreamExporterOutput(baos));
        jrXlsExporter.setConfiguration(configuration);
        log.debug("Finalizando exportacion a excel en response");
        jrXlsExporter.exportReport();
    }

    public static void exportToPdf(JasperPrint jasperPrint, ByteArrayOutputStream baos) throws JRException {
        log.debug(MSG_CONFIG_START);
        SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();
        log.debug("Iniciando exportacion a pdf en response");
        JRPdfExporter jrPdfExporter = new JRPdfExporter();
        jrPdfExporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        jrPdfExporter.setExporterOutput(new SimpleOutputStreamExporterOutput(baos));
        jrPdfExporter.setConfiguration(configuration);
        log.debug("Finalizando exportacion a pdf en response");
        jrPdfExporter.exportReport();
    }

    public static void exportToWord(JasperPrint jasperPrint, ByteArrayOutputStream baos) throws JRException {
        log.debug(MSG_CONFIG_START);
        SimpleDocxExporterConfiguration configuration = new SimpleDocxExporterConfiguration();
        log.debug("Iniciando exportacion a word en response");
        JRDocxExporter jrDocxExporter = new JRDocxExporter();
        jrDocxExporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        jrDocxExporter.setExporterOutput(new SimpleOutputStreamExporterOutput(baos));
        jrDocxExporter.setConfiguration(configuration);
        log.debug("Finalizando exportacion a word en response");
        jrDocxExporter.exportReport();
    }
}
