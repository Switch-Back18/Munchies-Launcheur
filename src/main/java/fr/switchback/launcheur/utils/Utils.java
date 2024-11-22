package fr.switchback.launcheur.utils;

import fr.flowarg.azuljavadownloader.*;
import fr.flowarg.flowio.FileUtils;
import fr.flowarg.flowlogger.ILogger;
import fr.flowarg.flowlogger.Logger;
import fr.flowarg.flowupdater.FlowUpdater;
import fr.flowarg.flowupdater.download.IProgressCallback;
import fr.flowarg.flowupdater.download.json.CurseModPackInfo;
import fr.flowarg.flowupdater.download.json.Mod;
import fr.flowarg.flowupdater.utils.ModFileDeleter;
import fr.flowarg.flowupdater.utils.UpdaterOptions;
import fr.flowarg.flowupdater.versions.*;
import fr.flowarg.flowupdater.versions.forge.ForgeVersion;
import fr.flowarg.flowupdater.versions.forge.ForgeVersionBuilder;
import fr.switchback.launcheur.Main;
import fr.theshark34.openlauncherlib.minecraft.util.GameDirGenerator;
import fr.theshark34.openlauncherlib.util.ramselector.RamSelector;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

public class Utils {
    //OpenLauncheurLib
    public static final Path MC_DIR = GameDirGenerator.createGameDir("munchies", true);
    public static final RamSelector RAM_SELECTOR = new RamSelector(MC_DIR.resolve("ram.properties"));

    //FlowUpdater
    public static final int PROJECT_ID = getProjectID();
    public static final int FILE_ID = getFileID();
    public static final ILogger LOGGER = new Logger("[Munchies Launcheur]", MC_DIR.resolve("logs.log"), true);

    public static final IProgressCallback CALLBACK = new ProgressBarAPI();
    public static final VanillaVersion VANILLA = new VanillaVersion.VanillaVersionBuilder()
            .withName(getMinecraftVersion())
            .build();

    public static final CurseModPackInfo MODPACK = new CurseModPackInfo(PROJECT_ID, FILE_ID, true);

    /*public static final NeoForgeVersion NEO_FORGE_VERSION = new NeoForgeVersionBuilder()
            .withNeoForgeVersion(getLoaderVersion())
            .build();*/

    public static final ForgeVersion FORGE_VERSION = new ForgeVersionBuilder()
            .withForgeVersion(getMinecraftVersion() + "-" + getLoaderVersion())
            .withCurseModPack(MODPACK)
            .withMods(new Mod("OptiFine_1.12.2_HD_U_G5.jar", "https://munchies.websr.fr/download/OptiFine_1.12.2_HD_U_G5.jar", "a39f8700889872b0c928428660a0d34d", 2670592))
            .withFileDeleter(new ModFileDeleter(true, "OptiFine_1.12.2_HD_U_G5.jar"))
            .build();

    public static void javaSetup() throws IOException {

        final AzulJavaDownloader downloader = new AzulJavaDownloader(new Callback() {
            @Override
            public void onStep(Step step) {
                if(step == Step.DONE)
                    Main.frameInstance.getLauncherPanel().getButtonJouer().setEnabled(true);
            }
        });
        final Path java = MC_DIR.resolve("Launcheur\\java");
        final AzulJavaBuildInfo buildInfoWindows = downloader.getBuildInfo(new RequestedJavaInfo("1.8", AzulJavaType.JDK, "Windows", "x64", true));
        final Path javaHomeWindows = downloader.downloadAndInstall(buildInfoWindows, java);
        System.setProperty("java.home",javaHomeWindows.toAbsolutePath().toString());
    }


    public static final UpdaterOptions OPTIONS = new UpdaterOptions.UpdaterOptionsBuilder()
            .withJavaPath(System.getProperty("java.home"))
            .build();

    public static final FlowUpdater UPDATER = new FlowUpdater.FlowUpdaterBuilder()
            .withVanillaVersion(VANILLA)
            .withLogger(LOGGER)
            .withProgressCallback(CALLBACK)
            .withModLoaderVersion(FORGE_VERSION)
            .withUpdaterOptions(OPTIONS)
            .build();

    //Minecraft File
    public static final Path TEMP = MC_DIR.resolve(".cfp");
    public static final File MANIFEST = MC_DIR.resolve("manifest.json").toFile();
    public static final File MANIFEST_CACHE = MC_DIR.resolve("manifest.cache.json").toFile();
    public static final Path CONFIG = MC_DIR.resolve("config");
    public static final Path SCRIPT = MC_DIR.resolve("scripts");
    //public static final Path DEFAULT_CONFIG = MC_DIR.resolve("defaultconfigs");
    //public static final Path KUBEJS = MC_DIR.resolve("kubejs");
    public static final String VERSION = getModPackVersion();
    public static final File MODPACK_ZIP = TEMP.resolve("Munchies - Origin-" + VERSION + ".zip").toFile();

    public static void doUpdate() throws IOException {
        if (MANIFEST.exists())
            MANIFEST.delete();
        if (MANIFEST_CACHE.exists())
            MANIFEST_CACHE.delete();
        if (!MODPACK_ZIP.exists()) {
            if (TEMP.toFile().exists())
                FileUtils.deleteDirectory(TEMP);
            if (CONFIG.toFile().exists())
                FileUtils.deleteDirectory(CONFIG);
            if (SCRIPT.toFile().exists())
                FileUtils.deleteDirectory(SCRIPT);
            /*if (KUBEJS.toFile().exists())
                FileUtils.deleteDirectory(KUBEJS);
            if (DEFAULT_CONFIG.toFile().exists())
                FileUtils.deleteDirectory(DEFAULT_CONFIG);*/
        }
    }

    public static ArrayList<String> readFile() throws FileNotFoundException {
        final String os = Objects.requireNonNull(System.getProperty("os.name")).toLowerCase();
        ArrayList<String> list = new ArrayList<>();
        Scanner scanner = null;
        if (os.contains("win"))
            scanner = new Scanner(Paths.get(System.getenv("APPDATA"), ".munchies", "Launcheur", "LauncheurConfig.txt").toFile());
        else if (os.contains("mac"))
            scanner = new Scanner(Paths.get(System.getProperty("user.home"), "Library", "Application Support", "munchies", "Launcheur", "LauncheurConfig.txt").toFile());
        else
            scanner = new Scanner(Paths.get(System.getProperty("user.home"), ".local", "share", "munchies", "Launcheur", "LauncheurConfig.txt").toFile());
        while (scanner.hasNext())
            list.add(scanner.nextLine());
        return list;
    }

    public static String getMinecraftVersion() {
        String id = null;
        try {
            id = readFile().get(0).split(": ")[1];
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return id;
    }

    public static String getLoaderVersion() {
        String id = null;
        try {
            id = readFile().get(1).split(": ")[1];
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return id;
    }

    public static String getModPackVersion() {
        String id = null;
        try {
            id = readFile().get(2).split(": ")[1];
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return id;
    }

    public static int getProjectID() {
        String id = null;
        try {
            id = readFile().get(3).split(": ")[1];
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return Integer.parseInt(id);
    }

    public static int getFileID() {
        String id = null;
        try {
            id = readFile().get(4).split(": ")[1];
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return Integer.parseInt(id);
    }

    public static void setMinMaxRam(int min, int max) {
        int y = 0;
        if (max - min == 9) {
            for (int i = min; i <= max; i++) {
                RAM_SELECTOR.RAM_ARRAY[y] = i + "Go";
                y++;
            }
        } else
            System.out.println(
                    "Il faut une différence de 9 entre le max est le min, ici la différence est " + (max - min));
    }
}
