package io.scriptor;

import imgui.ImGui;
import imgui.app.Application;
import imgui.app.Configuration;
import io.scriptor.ui.LayoutManager;

import java.util.List;
import java.util.Vector;

import static org.lwjgl.glfw.GLFW.*;

public class RedJ extends Application {

    public static void main(String[] args) {
        // ToolProvider.getSystemJavaCompiler().run(System.in, System.out, System.err, "-verbose", "-d", "test", "src/main/java/io/scriptor/RedJ.java");
        launch(new RedJ());
    }

    private final List<Runnable> mSchedule = new Vector<>();

    private RedJ() {
    }

    @Override
    protected void configure(Configuration config) {
        config.setWidth(800);
        config.setHeight(600);
        config.setTitle("RedJ");
    }

    @Override
    protected void preRun() {
        final var callback = glfwSetKeyCallback(getHandle(), null);
        glfwSetKeyCallback(getHandle(), (window, key, scancode, action, mods) -> {
            if (callback != null)
                callback.invoke(window, key, scancode, action, mods);

            if (action == GLFW_RELEASE) {
                final var ctrl = (mods & GLFW_MOD_CONTROL) != 0;
                final var shift = (mods & GLFW_MOD_SHIFT) != 0;
                final var alt = (mods & GLFW_MOD_ALT) != 0;
                LayoutManager.getInstance().onKey(ctrl, shift, alt, key);
            }
        });

        LayoutManager.getInstance()
                .setActiveMenuBar("main")
                .registerAction("file.exit", () -> glfwSetWindowShouldClose(getHandle(), true))
                .registerAction("help.about", () -> schedule(() -> ImGui.openPopup("help.about")));
    }

    @Override
    public void process() {
        while (!mSchedule.isEmpty())
            mSchedule.remove(0).run();
        LayoutManager.getInstance().drawMenuBar();

        if (ImGui.beginPopup("help.about")) {
            ImGui.textUnformatted("RedJ");
            ImGui.textUnformatted("Version 0.0.1");
            ImGui.endPopup();
        }
    }

    private void schedule(Runnable r) {
        mSchedule.add(r);
    }
}
