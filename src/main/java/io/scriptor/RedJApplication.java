package io.scriptor;

import imgui.ImGui;
import imgui.app.Application;
import imgui.app.Configuration;
import io.scriptor.model.Resource;
import io.scriptor.model.ResourceInfo;

import java.io.IOException;

public class RedJApplication extends Application {

    private final ResourceInfo resourceInfo;
    private final Resource[] resources;

    public RedJApplication() throws IOException {
        resourceInfo = ResourceInfo.read("info.yml");
        resources = resourceInfo.readAll();
    }

    @Override
    protected void configure(Configuration config) {
        config.setTitle("RedJ");
        config.setWidth(800);
        config.setHeight(600);
    }

    @Override
    public void process() {
        if (ImGui.beginMainMenuBar()) {
            if (ImGui.beginMenu("File")) {
                if (ImGui.beginMenu("New")) {
                    ImGui.endMenu();
                }
                if (ImGui.menuItem("Open")) ;
                if (ImGui.beginMenu("Recent Projects")) {
                    ImGui.endMenu();
                }
                if (ImGui.menuItem("Close Project")) ;
                ImGui.separator();
                if (ImGui.menuItem("Settings")) ;
                if (ImGui.menuItem("Save All")) ;
                ImGui.separator();
                if (ImGui.menuItem("Exit")) ;
                ImGui.endMenu();
            }
            if (ImGui.beginMenu("Edit")) {
                if (ImGui.menuItem("Undo")) ;
                if (ImGui.menuItem("Redo")) ;
                ImGui.separator();
                if (ImGui.menuItem("Cut")) ;
                if (ImGui.menuItem("Copy")) ;
                if (ImGui.menuItem("Paste")) ;
                if (ImGui.menuItem("Delete")) ;
                ImGui.separator();
                if (ImGui.beginMenu("Find")) {
                    ImGui.endMenu();
                }
                if (ImGui.menuItem("Select All")) ;
                ImGui.endMenu();
            }
            if (ImGui.beginMenu("View")) {
                ImGui.endMenu();
            }
            ImGui.endMainMenuBar();
        }
    }
}
