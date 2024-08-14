package io.scriptor.resource;

import io.scriptor.debug.RedJDebug;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.batik.transcoder.svg2svg.SVGTranscoder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;

public class RedJIcon {

    public String id;
    public String source;

    public transient int texture;
    public transient int width, height;

    public RedJIcon load() throws IOException {
        if (texture != 0)
            return this;

        final var outputStream = new ByteArrayOutputStream();

        final var transcoder = new PNGTranscoder();
        final var input = new TranscoderInput(ClassLoader.getSystemResourceAsStream(source));
        final var output = new TranscoderOutput(outputStream);

        try {
            transcoder.transcode(input, output);
            outputStream.flush();
        } catch (TranscoderException e) {
            RedJDebug.severe("Failed to transcode '%s': %s", source, e);
            return null;
        }

        final var image = (BufferedImage) ImageIO.read(new ByteArrayInputStream(outputStream.toByteArray()));

        texture = glGenTextures();
        width = image.getWidth();
        height = image.getHeight();

        glBindTexture(GL_TEXTURE_2D, texture);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_BGRA, GL_UNSIGNED_BYTE, image.getRGB(0, 0, width, height, new int[width * height], 0, width));
        glBindTexture(GL_TEXTURE_2D, 0);

        return this;
    }

    public void cleanup() {
        glDeleteTextures(texture);
        texture = width = height = 0;
    }

    @Override
    public String toString() {
        return "RedJIcon { id=%s, source=%s }".formatted(id, source);
    }
}
