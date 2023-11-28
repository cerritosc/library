package com.focusservices.library.commons.reports;

import lombok.extern.slf4j.Slf4j;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;

@Slf4j
public class JasperWriter {

    private JasperWriter() {
        throw new IllegalStateException("Utility class");
    }

    public static void write(HttpServletResponse response, ByteArrayOutputStream baos) {
        try {
            log.debug("Writing report to the stream");
            ServletOutputStream outputStream = response.getOutputStream();
            baos.writeTo(outputStream);
            outputStream.flush();
        } catch (Exception e) {
            log.error("Unable to write report to the output stream", e);
        }
    }
}
