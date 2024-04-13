package io.scriptor.ui;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;

public record Shortcut(boolean ctrl, boolean shift, boolean alt, int key) {

    public boolean matches(boolean ctrl, boolean shift, boolean alt, int key) {
        return this.ctrl == ctrl && this.shift == shift && this.alt == alt && this.key == key;
    }

    private static int toKey(String rep) {
        rep = rep.toUpperCase();
        return switch (rep) {
            case "ESC" -> GLFW_KEY_ESCAPE;
            default -> rep.charAt(0);
        };
    }

    public static Shortcut[] parseShortcut(String rep) {
        if (rep == null) return new Shortcut[0];
        final var reps = rep.split("\\|+");
        final var shortcuts = new Shortcut[reps.length];
        for (int i = 0; i < reps.length; i++) {
            final var opts = reps[i].split("\\++");
            boolean ctrl = false, shift = false, alt = false;
            int key = 0;
            for (final var opt : opts)
                switch (opt.toUpperCase()) {
                    case "CTRL":
                        ctrl = true;
                        break;
                    case "SHIFT":
                        shift = true;
                        break;
                    case "ALT":
                        alt = true;
                        break;
                    default:
                        key = toKey(opt);
                        break;
                }
            shortcuts[i] = new Shortcut(ctrl, shift, alt, key);
        }
        return shortcuts;
    }
}
