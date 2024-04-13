package io.scriptor.ui;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import imgui.ImGui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class LayoutManager {

    private static final String LAYOUT = "layout/";

    private static final LayoutManager instance = new LayoutManager();

    public static LayoutManager getInstance() {
        return instance;
    }

    private final Gson mGson;
    private final Map<String, MenuBar> mMenuBars = new HashMap<>();
    private final Map<String, Menu> mMenus = new HashMap<>();

    private final Map<String, Runnable> mActions = new HashMap<>();

    private MenuBar mActive;

    private LayoutManager() {
        mGson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
        update();
    }

    public LayoutManager setActiveMenuBar(String id) {
        mActive = mMenuBars.get(id);
        return this;
    }

    public LayoutManager invokeAction(String action) {
        if (mActions.containsKey(action))
            mActions.get(action).run();
        else System.out.printf("undefined action %s%n", action);
        return this;
    }

    public LayoutManager registerAction(String action, Runnable r) {
        mActions.put(action, r);
        return this;
    }

    public LayoutManager drawMenuBar() {
        if (mActive == null)
            return this;

        final boolean open;
        if (mActive.isMain) open = ImGui.beginMainMenuBar();
        else open = ImGui.beginMenuBar();

        if (open) {
            for (final var menuId : mActive.menus) {
                final var menu = mMenus.get(menuId);
                if (menu == null) {
                    if (ImGui.beginMenu("Missing '" + menuId + "'"))
                        ImGui.endMenu();
                    continue;
                }

                if (ImGui.beginMenu(menu.label)) {
                    for (final var item : menu.items)
                        if (ImGui.menuItem(item.label, item.shortcut == null ? null : item.shortcut.toUpperCase()))
                            invokeAction(item.action);
                    ImGui.endMenu();
                }
            }

            if (mActive.isMain) ImGui.endMainMenuBar();
            else ImGui.endMenuBar();
        }

        return this;
    }

    public void onKey(boolean ctrl, boolean shift, boolean alt, int key) {
        for (final var menuId : mActive.menus) {
            final var menu = mMenus.get(menuId);
            for (final var item : menu.items) {
                final var shortcuts = Shortcut.parseShortcut(item.shortcut);
                for (final var shortcut : shortcuts)
                    if (shortcut != null && shortcut.matches(ctrl, shift, alt, key))
                        invokeAction(item.action);
            }
        }
    }

    public boolean update() {
        mMenuBars.clear();
        mMenus.clear();

        final var layoutStream = ClassLoader.getSystemResourceAsStream(LAYOUT);
        if (layoutStream == null)
            return false;

        try (final var reader = new BufferedReader(new InputStreamReader(layoutStream))) {
            for (String line = reader.readLine(); line != null; line = reader.readLine()) parseLayout(line);
        } catch (IOException e) {
            e.printStackTrace(System.err);
            return false;
        }

        return true;
    }

    private void parseLayout(String name) {
        final var stream = ClassLoader.getSystemResourceAsStream(LAYOUT + name);
        if (stream == null)
            return;
        try (final var reader = new InputStreamReader(stream)) {
            final var json = mGson.fromJson(reader, Map.class);
            final var type = LayoutType.valueOf(((String) json.get("type")).toUpperCase());

            switch (type) {
                case MENU_BAR -> parseMenuBar(name);
                case MENU -> parseMenu(name);
            }

        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
    }

    private void parseMenuBar(String name) throws IOException {
        final var stream = ClassLoader.getSystemResourceAsStream(LAYOUT + name);
        if (stream == null)
            return;
        try (final var reader = new InputStreamReader(stream)) {
            final var menuBar = mGson.fromJson(reader, MenuBar.class);
            mMenuBars.put(menuBar.id, menuBar);
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
    }

    private void parseMenu(String name) throws IOException {
        final var stream = ClassLoader.getSystemResourceAsStream(LAYOUT + name);
        if (stream == null)
            return;
        try (final var reader = new InputStreamReader(stream)) {
            final var menu = mGson.fromJson(reader, Menu.class);
            mMenus.put(menu.id, menu);
        } catch (
                IOException e) {
            e.printStackTrace(System.err);
        }
    }
}
