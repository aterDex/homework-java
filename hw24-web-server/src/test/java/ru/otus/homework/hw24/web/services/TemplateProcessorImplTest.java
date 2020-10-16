package ru.otus.homework.hw24.web.services;

import org.jsoup.Jsoup;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TemplateProcessorImplTest {

    @Test
    void constructor() {
        assertThrows(IllegalArgumentException.class, () -> new TemplateProcessorImpl(null));
    }

    @Test
    void writePageNotFound() throws IOException {
        var template = new TemplateProcessorImpl("/html/templates/test/folderWhichNotExists");
        try (var writer = new StringWriter()) {
            assertThrows(IOException.class, () -> template.writePage("fileWhichNotExists.html", Collections.emptyMap(), writer));
        }
    }

    @Test
    void writePage() throws IOException {
        var template = new TemplateProcessorImpl("/html/templates/test");
        try (var writer = new StringWriter()) {
            assertThrows(IOException.class, () -> template.writePage("test.html", Collections.emptyMap(), writer));
        }

        final var text1 = "KKvv ZZqq";
        final var text2 = "2133";

        try (var writer = new StringWriter()) {
            template.writePage("test.html", Map.of("text", text1, "data", Map.of("text", text2)), writer);

            var document = Jsoup.parse(writer.toString());
            assertEquals(text1, document.select("div#text1").text());
            assertEquals(text2, document.select("div#text2").text());
            assertEquals(0, document.select("div#text3").size());
        }
    }
}