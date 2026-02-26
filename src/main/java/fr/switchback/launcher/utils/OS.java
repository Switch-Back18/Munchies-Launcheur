package fr.switchback.launcher.utils;

import fr.flowarg.azuljavadownloader.AzulJavaOS;

public class OS {
    private final String OS_NAME;
    private final AzulJavaOS AZUL_JAVA_OS;

    public OS(String osName, AzulJavaOS azulJavaOS) {
        OS_NAME = osName;
        AZUL_JAVA_OS = azulJavaOS;
    }

    public static OS getOS() {
        if(System.getProperty("os.name").toLowerCase().contains("win"))
            return new OS("WINDOWS", AzulJavaOS.WINDOWS);
        else if(System.getProperty("os.name").toLowerCase().contains("mac"))
            return new OS("MACOS", AzulJavaOS.MACOS);
        return new OS("LINUX", AzulJavaOS.LINUX);
    }

    public AzulJavaOS getAzulJavaOS() {
        return AZUL_JAVA_OS;
    }

    public String getOsName() {
        return OS_NAME;
    }
}
