package com.focusservices.library.commons.reports;

import java.io.ByteArrayOutputStream;
import java.sql.SQLException;
import java.util.Map;

import jakarta.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;

public interface JasperReportService {
    public void exportToResponseAndWrite(String title, String type, JasperPrint jasperPrint, HttpServletResponse response, ByteArrayOutputStream baos) throws JRException;

    public JasperPrint generateReport(String path, Map<String, Object> reportParams) throws JRException, SQLException;
}
