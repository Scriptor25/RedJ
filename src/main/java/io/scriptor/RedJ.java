package io.scriptor;

import imgui.ImGui;
import imgui.app.Application;
import imgui.app.Configuration;
import io.scriptor.debug.RedJDebug;
import io.scriptor.project.RedJProject;
import io.scriptor.resource.RedJResourceManager;
import io.scriptor.ui.RedJFileChooser;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import static org.lwjgl.glfw.GLFW.*;

public class RedJ extends Application {

    public static void main(String[] args) {
        // ToolProvider.getSystemJavaCompiler().run(System.in, System.out, System.err, "-verbose", "-d", "test", "src/main/java/io/scriptor/RedJ.java");

        launch(new RedJ());
    }

    public static int getVersion() {
        return RedJUtil.makeVersion(0, 0, 1);
    }

    private final List<Runnable> mSchedule = new Vector<>();
    private final RedJFileChooser mFileChooser = new RedJFileChooser(this);

    private RedJProject mProject = null;

    private RedJ() {
    }

    public void schedule(Runnable r) {
        mSchedule.add(r);
    }

    private void fileNew() {
        schedule(() -> ImGui.openPopup("file.new"));
    }

    private void fileSave() {
        if (mProject == null) {
            RedJDebug.warning("Failed to save project: no open project");
            return;
        }

        if (mProject.saved)
            return;

        if (mProject.file == null) {
            fileSaveAs();
            return;
        }

        try {
            mProject.save();
        } catch (IOException e) {
            RedJDebug.severe("Failed to save project: %s", e);
        }
    }

    private void fileSaveAll() {
    }

    private void fileSaveAs() {
        if (mProject == null) {
            RedJDebug.warning("Failed to save project: no open project");
            return;
        }

        mFileChooser.open("file.save_as");
    }

    private void fileOpen() {
        mFileChooser.open("file.open");
    }

    private void fileClose() {
    }

    private void fileExit() {
        glfwSetWindowShouldClose(getHandle(), true);
    }

    private void helpAbout() {
        schedule(() -> ImGui.openPopup("help.about"));
    }

    private void onFileOpen(File file) {
        if (mProject != null && !mProject.saved) {
            fileSave();
            return;
        }

        try {
            mProject = RedJProject.load(file);
        } catch (IOException e) {
            RedJDebug.severe("Failed to load project from '%s': %s", file, e);
        }
    }

    private void onFileSaveAs(File file) {
        if (mProject == null) {
            RedJDebug.warning("Failed to save project: no open project");
            return;
        }

        try {
            mProject.saveAs(file);
        } catch (IOException e) {
            RedJDebug.severe("Failed to save project as '%s': %s", file, e);
        }
    }

    private void onFileNew_Project() {
        if (mProject != null && !mProject.saved) {
            RedJDebug.info("Saving old one before creating new project");
            fileSave();
            return;
        }

        mProject = new RedJProject();
        mProject.version = getVersion();
        mProject.name = "Hello World!";
        mProject.author = "A Dude";
        mProject.files = new String[0];
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
                RedJResourceManager.getInstance().onKey(ctrl, shift, alt, key);
            }
        });

        RedJResourceManager.getInstance()
                .setActiveMenuBar("main")
                .registerAction("file.new", this::fileNew)
                .registerAction("file.save", this::fileSave)
                .registerAction("file.save_all", this::fileSaveAll)
                .registerAction("file.save_as", this::fileSaveAs)
                .registerAction("file.open", this::fileOpen)
                .registerAction("file.close", this::fileClose)
                .registerAction("file.exit", this::fileExit)
                .registerAction("help.about", this::helpAbout);
    }

    @Override
    public void process() {
        glfwSetWindowTitle(getHandle(), mProject == null ? "RedJ" : "RedJ - " + mProject.name);

        while (!mSchedule.isEmpty())
            mSchedule.remove(0).run();

        RedJResourceManager.getInstance().drawMenuBar();

        if (mFileChooser.show("Choose File")) {
            switch (mFileChooser.getAction()) {
                case "file.open":
                    onFileOpen(mFileChooser.getSelected());
                    break;
                case "file.save_as":
                    onFileSaveAs(mFileChooser.getSelected());
                    break;
            }
        }

        if (ImGui.beginPopup("file.new")) {
            if (ImGui.selectable("Project"))
                onFileNew_Project();
            ImGui.endPopup();
        }

        if (ImGui.beginPopup("help.about")) {
            ImGui.textUnformatted("RedJ");
            ImGui.textUnformatted("Version %s".formatted(RedJUtil.makeVersionString(getVersion())));
            ImGui.endPopup();
        }
    }
}
