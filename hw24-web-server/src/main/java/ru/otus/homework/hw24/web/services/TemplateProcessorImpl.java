package ru.otus.homework.hw24.web.services;

import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import freemarker.template.TemplateNotFoundException;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

public class TemplateProcessorImpl implements TemplateProcessor {

    private final Configuration configuration;

    public TemplateProcessorImpl(String templatesDir) {
        configuration = new Configuration(Configuration.VERSION_2_3_30);
        configuration.setClassForTemplateLoading(this.getClass(), templatesDir);
        configuration.setDefaultEncoding("UTF-8");
    }

    @Override
    public void writePage(String filename, Map<String, Object> data, Writer writer) throws IOException {
        try {
            configuration.getTemplate(filename).process(data, writer);
        } catch (TemplateException | TemplateNotFoundException e) {
            throw new IOException(e);
        }
    }
}
