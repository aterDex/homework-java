package ru.otus.homework.hw24.web.services;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

public interface TemplateProcessor {
    void writePage(String filename, Map<String, Object> data, Writer writer) throws IOException;
}
