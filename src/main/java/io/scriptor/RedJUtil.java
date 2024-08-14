package io.scriptor;

public class RedJUtil {

    private RedJUtil() {
    }

    public static int makeVersion(int major, int minor, int patch) {
        return (major & 0b1111111111) << 22 | (minor & 0b1111111111) << 12 | (patch & 0b111111111111);
    }

    public static int getVersionMajor(int version) {
        return (version >> 22) & 0b1111111111;
    }

    public static int getVersionMinor(int version) {
        return (version >> 12) & 0b1111111111;
    }

    public static int getVersionPatch(int version) {
        return version & 0b111111111111;
    }

    public static String makeVersionString(int version) {
        return "%d.%d.%d".formatted(getVersionMajor(version), getVersionMinor(version), getVersionPatch(version));
    }
}
