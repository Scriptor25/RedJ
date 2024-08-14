package io.scriptor.ui;

import imgui.ImGui;
import imgui.flag.ImGuiInputTextFlags;
import imgui.flag.ImGuiMouseButton;
import imgui.flag.ImGuiSelectableFlags;
import imgui.type.ImBoolean;
import imgui.type.ImString;
import io.scriptor.RedJ;
import io.scriptor.debug.RedJDebug;
import io.scriptor.resource.RedJResourceManager;

import java.io.File;

public class RedJFileChooser {

    private final RedJ mContext;

    private File mDirectory = new File("").getAbsoluteFile();
    private File mSelected = null;
    private String mAction = null;
    private int mFileIndex = -1;
    private boolean mHasResult = false;

    private final ImBoolean mOpen = new ImBoolean();
    private final ImString mFileName = new ImString();
    private final ImString mDirectoryName = new ImString();

    public RedJFileChooser(RedJ context) {
        mContext = context;
    }

    public boolean show(String name) {
        if (!mOpen.get())
            return false;

        if (!ImGui.begin(name, mOpen)) {
            ImGui.end();
            return false;
        }

        final var height = ImGui.getFrameHeightWithSpacing() * 2;
        if (ImGui.beginChild("##files", 0.0f, -height, true)) {

            if (ImGui.selectable(".."))
                if (mDirectory.getParentFile() != null)
                    mDirectory = mDirectory.getParentFile();

            final var iconFolder = RedJResourceManager.getInstance().getIcon("folder");
            final var iconFolderOpen = RedJResourceManager.getInstance().getIcon("folder_open");
            final var iconDraft = RedJResourceManager.getInstance().getIcon("draft");

            final var files = mDirectory.listFiles();
            if (files != null) {
                for (int i = 0; i < files.length; i++) {
                    final var file = files[i];
                    if (!file.isDirectory() && (!file.isFile() || !file.getName().endsWith(".redj")))
                        continue;

                    ImGui.pushID(i);

                    int texture = 0;
                    if (file.isFile())
                        texture = iconDraft.texture;
                    else if (file.isDirectory()) {
                        final var list = file.list();
                        if (list != null && list.length > 0)
                            texture = iconFolder.texture;
                        else
                            texture = iconFolderOpen.texture;
                    }
                    ImGui.image(texture, ImGui.getFrameHeight(), ImGui.getFrameHeight());
                    ImGui.sameLine();

                    final var selected = mFileIndex == i;
                    if (ImGui.selectable(file.getName(), selected, ImGuiSelectableFlags.AllowDoubleClick))
                        if (file.isFile()) {
                            mFileName.set(file.getName());
                            mFileIndex = i;
                            if (ImGui.isMouseDoubleClicked(ImGuiMouseButton.Left))
                                select();
                        } else if (file.isDirectory())
                            mDirectory = file;

                    ImGui.popID();
                }
            }
            ImGui.endChild();
        }

        if (ImGui.inputText("Filename", mFileName, ImGuiInputTextFlags.EnterReturnsTrue))
            select();

        if (ImGui.button("New Directory"))
            mContext.schedule(() -> ImGui.openPopup("chooser.mkdir"));

        ImGui.sameLine();
        if (ImGui.button("Select")) select();

        ImGui.sameLine();
        if (ImGui.button("Cancel"))
            cancel();

        ImGui.end();

        if (ImGui.beginPopup("chooser.mkdir")) {
            if (ImGui.inputText("Directory Name", mDirectoryName, ImGuiInputTextFlags.EnterReturnsTrue)) {
                var newDirectory = new File(mDirectory, mDirectoryName.get());
                for (int i = 1; newDirectory.exists(); i++)
                    newDirectory = new File(mDirectory, mDirectoryName.get() + " (" + i + ")");

                if (!newDirectory.mkdir())
                    RedJDebug.warning("Failed to mkdir '%s'", newDirectory);

                mDirectoryName.clear();
                ImGui.closeCurrentPopup();
            }
            ImGui.endPopup();
        }

        return mHasResult;
    }

    public File getSelected() {
        return mSelected;
    }

    public String getAction() {
        return mAction;
    }

    public void open(String action) {
        mAction = action;
        mOpen.set(true);
    }

    public void select() {
        mSelected = new File(mDirectory, mFileName.get());
        mOpen.set(false);
        mHasResult = true;
    }

    public void cancel() {
        mOpen.set(false);
        mHasResult = false;
    }
}
