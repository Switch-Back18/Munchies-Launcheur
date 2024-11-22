package fr.switchback.launcher.utils;

public enum OS {
    WINDOWS,
    LINUX,
    MACOS;

    public static OS getOS() {
        if(System.getProperty("os.name").toLowerCase().contains("win"))
            return WINDOWS;
        else if(System.getProperty("os.name").toLowerCase().contains("mac"))
            return MACOS;
        else
            return LINUX;
    }
}
