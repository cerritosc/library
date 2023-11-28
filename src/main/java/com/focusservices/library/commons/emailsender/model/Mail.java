package com.focusservices.library.commons.emailsender.model;

import java.util.List;
import java.util.Map;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Mail {
    private String from;
    private String mailTo;
    private String subject;
    private List<Object> attachments;
    private Map<String, Object> props;
}
