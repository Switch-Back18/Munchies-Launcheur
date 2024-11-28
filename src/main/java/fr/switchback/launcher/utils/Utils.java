package fr.switchback.launcher.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fr.flowarg.azuljavadownloader.*;
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
import fr.switchback.launcher.Main;
import fr.theshark34.openlauncherlib.minecraft.util.GameDirGenerator;
import fr.theshark34.openlauncherlib.util.ramselector.RamSelector;
import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Scanner;

public class Utils {
    public static final Path MC_DIR = GameDirGenerator.createGameDir("munchies", true);
    public static final RamSelector RAM_SELECTOR = new RamSelector(MC_DIR.resolve("ram.properties"));

    public static final int PROJECT_ID = getProjectID();
    public static final int FILE_ID = getFileID();
    public static final ILogger LOGGER = new Logger("[Munchies Launcher]", MC_DIR.resolve("logs.log"), false);

    public static final IProgressCallback CALLBACK = new ProgressBarAPI();
    public static final VanillaVersion VANILLA = new VanillaVersion.VanillaVersionBuilder()
            .withName(getMinecraftVersion())
            .build();

    public static final CurseModPackInfo MODPACK = new CurseModPackInfo(PROJECT_ID, FILE_ID, true);

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
        Path java = MC_DIR.resolve("Launcher").resolve("java");;
        switch (OS.getOS()) {
            case WINDOWS :
                AzulJavaBuildInfo buildInfoWindows = downloader.getBuildInfo(new RequestedJavaInfo("1.8", AzulJavaType.JRE, AzulJavaOS.WINDOWS, AzulJavaArch.X64).setJavaFxBundled(true));
                Path javaHomeWindows = downloader.downloadAndInstall(buildInfoWindows, java);
                System.setProperty("java.home", javaHomeWindows.toAbsolutePath().toString());
                System.out.println("Java for Windows");
                break;
            case MACOS :
                AzulJavaBuildInfo buildInfoMac = downloader.getBuildInfo(new RequestedJavaInfo("1.8", AzulJavaType.JRE, AzulJavaOS.MACOS, AzulJavaArch.X64).setJavaFxBundled(true));
                Path javaHomeMac = downloader.downloadAndInstall(buildInfoMac, java);
                System.setProperty("java.home", javaHomeMac.toAbsolutePath().toString());
                System.out.println("Java for MacOS");
                break;
            case LINUX :
                AzulJavaBuildInfo buildInfoLinux = downloader.getBuildInfo(new RequestedJavaInfo("1.8", AzulJavaType.JRE, AzulJavaOS.LINUX, AzulJavaArch.X64).setJavaFxBundled(true));
                Path javaHomeLinux = downloader.downloadAndInstall(buildInfoLinux, java);
                System.setProperty("java.home", javaHomeLinux.toAbsolutePath().toString());
                System.out.println("Java for Linux");
                break;
            default:
                break;
        }
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

    public static final Path TEMP = MC_DIR.resolve(".cfp");
    public static final Path MANIFEST = MC_DIR.resolve("manifest.json");
    public static final Path MANIFEST_CACHE = MC_DIR.resolve("manifest.cache.json");
    public static final Path CONFIG = MC_DIR.resolve("config");
    public static final Path SCRIPT = MC_DIR.resolve("scripts");
    public static final String VERSION = getModPackVersion();
    public static final Path MODPACK_ZIP = TEMP.resolve("Munchies - Origin-" + VERSION + ".zip");

    public static void removeOlderFiles() throws IOException {
        Files.deleteIfExists(MANIFEST);
        Files.deleteIfExists(MANIFEST_CACHE);
        if (!Files.exists(MODPACK_ZIP)) {
            Files.deleteIfExists(TEMP);
            Files.deleteIfExists(CONFIG);
            Files.deleteIfExists(SCRIPT);
        }
    }

    public static ArrayList<String> readLauncherConfigFile() {
        ArrayList<String> list = new ArrayList<>();
        try {
            Scanner scanner = new Scanner(MC_DIR.resolve("Launcher").resolve("LauncherConfig.txt"));
            while (scanner.hasNext())
                list.add(scanner.nextLine());
            scanner.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return list;
    }

    public static String getMinecraftVersion() {
        return readLauncherConfigFile().getFirst().split(": ")[1];
    }

    public static String getLoaderVersion() {
        return readLauncherConfigFile().get(1).split(": ")[1];
    }

    public static String getModPackVersion() {
        return readLauncherConfigFile().get(2).split(": ")[1];
    }

    public static int getProjectID() {
        return Integer.parseInt(readLauncherConfigFile().get(3).split(": ")[1]);
    }

    public static int getFileID() {
        return Integer.parseInt(readLauncherConfigFile().get(4).split(": ")[1]);
    }

    public static void setMinimumRam(int min) {
        int y = 0;
        for (int i = min; i <= min + 9; i++) {
            RamSelector.RAM_ARRAY[y] = i + "Go";
            y++;
        }
    }

    public static boolean loggedBefore() {
        return Files.exists(MC_DIR.resolve("login.json"));
    }

    public static void saveJson(JsonObject json) {
        try {
            FileWriter fileWriter = new FileWriter(MC_DIR.resolve("login.json").toFile());
            fileWriter.write(json.toString());
            fileWriter.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static JsonObject loadJson() {
        StringBuilder jsonString = new StringBuilder();
        try {
            Scanner scanner = new Scanner(MC_DIR.resolve("login.json"));
            while (scanner.hasNextLine())
                jsonString.append(scanner.nextLine());
            scanner.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return JsonParser.parseString(jsonString.toString()).getAsJsonObject();
    }

    public static void startDiscordRPC() {
        DiscordEventHandlers handlers = new DiscordEventHandlers.Builder().setReadyEventHandler((discordUser) -> {
            DiscordRichPresence richPresence = new DiscordRichPresence.Builder("Launcheur du Serveur Munchies").setStartTimestamps(System.currentTimeMillis() / 1000).setBigImage("logo", "Munchies : Beyond Limits - v" + Utils.getModPackVersion()).build();
            DiscordRPC.discordUpdatePresence(richPresence);
        }).build();
        DiscordRPC.discordInitialize("399951697360846859", handlers, false);
        DiscordRPC.discordRegister("399951697360846859", "");
    }
}
