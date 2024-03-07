package fr.mathisskate.launcheur.utils;

import fr.flowarg.flowio.FileUtils;
import fr.flowarg.flowlogger.ILogger;
import fr.flowarg.flowlogger.Logger;
import fr.flowarg.flowupdater.FlowUpdater;
import fr.flowarg.flowupdater.download.IProgressCallback;
import fr.flowarg.flowupdater.download.json.CurseModPackInfo;
import fr.flowarg.flowupdater.utils.ModFileDeleter;
import fr.flowarg.flowupdater.utils.UpdaterOptions;
import fr.flowarg.flowupdater.versions.AbstractForgeVersion;
import fr.flowarg.flowupdater.versions.ForgeVersionBuilder;
import fr.flowarg.flowupdater.versions.ForgeVersionType;
import fr.flowarg.flowupdater.versions.VanillaVersion;
import fr.flowarg.openlauncherlib.NewForgeVersionDiscriminator;
import fr.theshark34.openlauncherlib.minecraft.GameInfos;
import fr.theshark34.openlauncherlib.minecraft.GameType;
import fr.theshark34.openlauncherlib.minecraft.GameVersion;
import fr.theshark34.openlauncherlib.util.Saver;
import fr.theshark34.openlauncherlib.util.ramselector.RamSelector;
import net.lingala.zip4j.ZipFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

public class Helpers {
    //OpenLauncheurLib
    public static final NewForgeVersionDiscriminator FORGE = new NewForgeVersionDiscriminator(getForgeVersion(), getMinecraftVersion(), "net.minecraftforge", getMCP());
    public static final GameType GAME_VERSION = GameType.V1_13_HIGHER_FORGE.setNFVD(FORGE);
    public static final GameVersion MC_VERSION = new GameVersion(getMinecraftVersion(), GAME_VERSION);
    public static final GameInfos MC_INFOS = new GameInfos("munchies", MC_VERSION, null);
    public static final Path MC_DIR = MC_INFOS.getGameDir();
    public static final Saver SAVER = new Saver(Helpers.MC_DIR.resolve("options.properties"));
    public static final RamSelector RAM_SELECTOR = new RamSelector(Helpers.MC_DIR.resolve("ram.properties"));

    //FlowUpdater
    public static final int PROJECT_ID = getProjectID();
    public static final int FILE_ID = getFileID();
    public static final ILogger LOGGER = new Logger("[Munchies Launcheur]", Helpers.MC_DIR.resolve("logs.log"), true);

    public static IProgressCallback CALLBACK = new ProgressBarAPI();
    public static VanillaVersion VANILLA = new VanillaVersion.VanillaVersionBuilder()
            .withName(getMinecraftVersion())
            .build();

    public static final CurseModPackInfo MODPACK = new CurseModPackInfo(PROJECT_ID, FILE_ID, true);

    public static final UpdaterOptions OPTIONS = new UpdaterOptions.UpdaterOptionsBuilder().build();

    public static final AbstractForgeVersion FORGE_VERSION = new ForgeVersionBuilder(ForgeVersionType.NEW)
            .withForgeVersion(getMinecraftVersion() + "-" + getForgeVersion())
            .withCurseModPack(MODPACK)
            .withFileDeleter(new ModFileDeleter(true))
            .build();

    public static final FlowUpdater UPDATER = new FlowUpdater.FlowUpdaterBuilder()
            .withVanillaVersion(VANILLA)
            .withLogger(LOGGER)
            .withProgressCallback(CALLBACK)
            .withUpdaterOptions(OPTIONS)
            .withModLoaderVersion(FORGE_VERSION)
            .build();

    //Minecraft File
    public static final Path TEMP = MC_INFOS.getGameDir().resolve(".cfp");
    public static final File MANIFEST = MC_INFOS.getGameDir().resolve("manifest.json").toFile();
    public static final File MANIFEST_CACHE = MC_INFOS.getGameDir().resolve("manifest.cache.json").toFile();
    public static final Path CONFIG = MC_INFOS.getGameDir().resolve("config");
    public static final Path DEFAULT_CONFIG = MC_INFOS.getGameDir().resolve("defaultconfigs");
    public static final Path SCRIPT = MC_INFOS.getGameDir().resolve("scripts");
    public static final Path KUBEJS = MC_INFOS.getGameDir().resolve("kubejs");
    public static final String VERSION = getModPackVersion();
    public static final File MODPACK_ZIP = TEMP.resolve("[Munchies]Over The Limit-" + VERSION + ".zip").toFile();

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
            if (KUBEJS.toFile().exists())
                FileUtils.deleteDirectory(KUBEJS);
            if (SCRIPT.toFile().exists())
                FileUtils.deleteDirectory(SCRIPT);
            if (DEFAULT_CONFIG.toFile().exists())
                FileUtils.deleteDirectory(DEFAULT_CONFIG);
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

    public static String getForgeVersion() {
        String id = null;
        try {
            id = readFile().get(1).split(": ")[1];
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return id;
    }

    public static String getMCP() {
        String id = null;
        try {
            id = readFile().get(2).split(": ")[1];
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return id;
    }


    public static String getModPackVersion() {
        String id = null;
        try {
            id = readFile().get(3).split(": ")[1];
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return id;
    }

    public static int getProjectID() {
        String id = null;
        try {
            id = readFile().get(4).split(": ")[1];
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return Integer.parseInt(id);
    }

    public static int getFileID() {
        String id = null;
        try {
            id = readFile().get(5).split(": ")[1];
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return Integer.parseInt(id);
    }

    public static void downloadServerIP() throws IOException {
        URL serverIP_URL = new URL("https://munchies.websr.fr/download/servers.zip");
        File serverIP_ZIP = new File(MC_DIR.toFile(), "servers.zip");
        File serverIP_FILE = new File(MC_DIR.toFile(), "servers.dat");
        if (serverIP_FILE.exists())
            serverIP_FILE.delete();
        if (serverIP_ZIP.exists())
            serverIP_ZIP.delete();
        org.apache.commons.io.FileUtils.copyURLToFile(serverIP_URL, serverIP_ZIP);
        unZip(serverIP_ZIP, MC_DIR);
    }

    public static void unZip(File source, Path target) throws IOException {
        new ZipFile(source).extractAll(target.toString());
        source.delete();
    }
}
