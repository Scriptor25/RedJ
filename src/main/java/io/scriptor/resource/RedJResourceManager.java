package io.scriptor.resource;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import imgui.ImGui;
import io.scriptor.debug.RedJDebug;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Modifier;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class RedJResourceManager {

    private static final RedJResourceManager instance = new RedJResourceManager();

    public static RedJResourceManager getInstance() {
        return instance;
    }

    public static Gson getGsonInstance() {
        return instance.getGson();
    }

    private final Gson mGson = new GsonBuilder()
            .serializeNulls()
            .setPrettyPrinting()
            .excludeFieldsWithModifiers(Modifier.TRANSIENT, Modifier.STATIC)
            .create();
    private final Map<String, Runnable> mActions = new HashMap<>();
    private RedJMenuBar mActiveMenuBar;

    private final Map<String, RedJMenuBar> mMenuBars = new HashMap<>();
    private final Map<String, RedJMenu> mMenus = new HashMap<>();
    private final Map<String, RedJIcon> mIcons = new HashMap<>();

    private RedJResourceManager() {
        indexResources();
    }

    public Gson getGson() {
        return mGson;
    }

    public RedJIcon getIcon(String id) {
        if (!mIcons.containsKey(id))
            return null;
        try {
            return mIcons.get(id).load();
        } catch (IOException e) {
            RedJDebug.severe("Failed to load icon '%s': %s", id, e);
            return null;
        }
    }

    public RedJResourceManager setActiveMenuBar(String id) {
        mActiveMenuBar = mMenuBars.get(id);
        return this;
    }

    public void invokeAction(String action) {
        if (mActions.containsKey(action)) {
            RedJDebug.info("Invoke Action '%s'", action);

            final var act = mActions.get(action);
            if (act != null)
                act.run();
            return;
        }

        RedJDebug.warning("Undefined Action '%s'", action);
    }

    public RedJResourceManager registerAction(String action, Runnable r) {
        mActions.put(action, r);
        return this;
    }

    public void drawMenuBar() {
        if (mActiveMenuBar == null)
            return;

        final boolean open;
        if (mActiveMenuBar.isMain) open = ImGui.beginMainMenuBar();
        else open = ImGui.beginMenuBar();

        if (open) {
            for (final var menuId : mActiveMenuBar.menus) {
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

            if (mActiveMenuBar.isMain) ImGui.endMainMenuBar();
            else ImGui.endMenuBar();
        }
    }

    public void onKey(boolean ctrl, boolean shift, boolean alt, int key) {
        if (mActiveMenuBar == null)
            return;

        for (final var menuId : mActiveMenuBar.menus) {
            final var menu = mMenus.get(menuId);
            for (final var item : menu.items) {
                final var shortcuts = RedJShortcut.parseShortcut(item.shortcut);
                for (final var shortcut : shortcuts)
                    if (shortcut != null && shortcut.matches(ctrl, shift, alt, key))
                        invokeAction(item.action);
            }
        }
    }

    public void indexResources() {
        mMenuBars.clear();
        mMenus.clear();
        mIcons.clear();

        parseResourceDirectory("");
    }

    private void parseResourceDirectory(String name) {
        final var url = ClassLoader.getSystemResource(name);
        if (url == null) {
            RedJDebug.warning("Failed to get resource directory '%s'", name);
            return;
        }

        if (!url.getProtocol().equals("file")) {
            RedJDebug.warning("Resource directory '%s' does not use file protocol (uses %s)", name, url.getProtocol());
            return;
        }

        try {
            final var file = new File(url.toURI());
            final var children = file.list();
            if (children != null)
                for (final var child : children)
                    parseResource(name.isEmpty() ? child : name + "/" + child);
        } catch (URISyntaxException e) {
            RedJDebug.severe("Failed to parse resource directory '%s': %s", name, e);
        }
    }

    private void parseResource(String name) {
        if (!name.endsWith(".json")) {
            parseResourceDirectory(name);
            return;
        }

        final var stream = ClassLoader.getSystemResourceAsStream(name);
        if (stream == null) {
            RedJDebug.warning("Failed to get resource '%s'", name);
            return;
        }

        try (final var reader = new InputStreamReader(stream)) {
            final var json = mGson.<Map<String, Object>>fromJson(reader, Map.class);
            final var type = (String) json.get("type");

            switch (type) {
                case "menubar" -> parseMenuBar(json);
                case "menu" -> parseMenu(json);
                case "icon" -> parseIcon(json);
                default -> RedJDebug.warning("Undefined resource type '%s' (%s)", type, name);
            }
        } catch (IOException e) {
            RedJDebug.severe("Failed to parse resource '%s': %s", name, e);
        }
    }

    private void parseMenuBar(Map<String, Object> json) {
        RedJDebug.info("MenuBar '%s'", json.get("id"));
        final var menuBar = mGson.fromJson(mGson.toJson(json), RedJMenuBar.class);
        mMenuBars.put(menuBar.id, menuBar);
    }

    private void parseMenu(Map<String, Object> json) {
        RedJDebug.info("Menu '%s'", json.get("id"));
        final var menu = mGson.fromJson(mGson.toJson(json), RedJMenu.class);
        mMenus.put(menu.id, menu);
    }

    private void parseIcon(Map<String, Object> json) {
        RedJDebug.info("Icon '%s'", json.get("id"));
        final var icon = mGson.fromJson(mGson.toJson(json), RedJIcon.class);
        mIcons.put(icon.id, icon);
    }
}
