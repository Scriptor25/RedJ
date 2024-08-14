package io.scriptor.project;

import io.scriptor.resource.RedJResourceManager;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class RedJProject {

    public static RedJProject load(File file) throws IOException {
        final var project = RedJResourceManager.getGsonInstance().fromJson(new FileReader(file), RedJProject.class);
        project.file = file;
        project.saved = true;
        return project;
    }

    public int version;
    public String name;
    public String author;
    public String[] files;

    public transient File file;
    public transient boolean saved = false;

    public void saveAs(File file) throws IOException {
        final var json = RedJResourceManager.getGsonInstance().toJson(this);
        try (final var writer = new FileWriter(file, false)) {
            writer.write(json);
            writer.flush();
        }
        this.file = file;
        this.saved = true;
    }

    public void save() throws IOException {
        if (file == null)
            throw new IllegalStateException("file == null");
        saveAs(file);
    }
}
