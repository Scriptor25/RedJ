package io.scriptor;

import imgui.app.Application;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        final var redJAppInstance = new RedJApplication();
        Application.launch(redJAppInstance);
    }
}
