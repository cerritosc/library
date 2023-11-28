package com.focusservices.library.commons.reports;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Map;

import jakarta.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import com.focusservices.library.commons.Constants;

@Slf4j
@Service
public class JasperReportServiceImpl implements JasperReportService {

    @Autowired
    private DataSource dataSource;

    @Override
    public void exportToResponseAndWrite(String title, String type, JasperPrint jasperPrint, HttpServletResponse response, ByteArrayOutputStream baos) throws JRException {
        this.exportToResponse(title, type, jasperPrint, response, baos);
        JasperWriter.write(response, baos);
    }

    @Override
    public JasperPrint generateReport(String path, Map<String, Object> reportParams) throws JRException, SQLException {
        InputStream stream = this.getClass().getResourceAsStream(path);
        return JasperFillManager.fillReport(stream, reportParams, dataSource.getConnection());
    }

    private HttpServletResponse exportToResponse(String title, String type, JasperPrint jasperPrint, HttpServletResponse response, ByteArrayOutputStream baos) throws JRException {
        String fileName = title;
        String fileExtension = ".";
        String contentType;
        if (type.equalsIgnoreCase(Constants.EXTENSION_XLS)) {
            JasperExporter.exportToXLS(jasperPrint, baos);
            fileExtension += Constants.EXTENSION_XLS;
            contentType = Constants.APPLICATION_VND_MS_EXCEL;
        } else {
            if (type.equalsIgnoreCase(Constants.EXTENSION_PDF)) {
                JasperExporter.exportToPdf(jasperPrint, baos);
                fileExtension += Constants.EXTENSION_PDF;
                contentType = Constants.APPLICATION_PDF;
            } else {
                if (type.equalsIgnoreCase(Constants.EXTENSION_DOCX)) {
                    JasperExporter.exportToWord(jasperPrint, baos);
                    fileExtension += Constants.EXTENSION_DOCX;
                    contentType = Constants.APPLICATION_VND_WORD_DOCUMENT;
                } else {
                    throw new JRException("No logic set for type " + type);
                }
            }
        }
        fileName += fileExtension;
        response.setHeader("Content-Disposition", "inline; filename=" + fileName);
        response.setContentType(contentType);
        response.setContentLength(baos.size());
        return response;
    }
}
