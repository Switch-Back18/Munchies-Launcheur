package fr.switchback.launcher.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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
import fr.switchback.launcher.Main;
import fr.theshark34.openlauncherlib.minecraft.util.GameDirGenerator;
import fr.theshark34.openlauncherlib.util.ramselector.RamSelector;
import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

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
        final AzulJavaDownloader downloader = new AzulJavaDownloader(step -> {
            if(step == Callback.Step.DONE)
                Main.frameInstance.getLauncherPanel().getButtonJouer().setEnabled(true);
        });
        Path java = MC_DIR.resolve("Launcher").resolve("java");
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
    public static final String VERSION = getModPackVersion();
    public static final Path MODPACK_ZIP = TEMP.resolve(getModPackName() + VERSION + ".zip");

    public static void removeOlderFiles() throws IOException {
        Files.deleteIfExists(MANIFEST);
        Files.deleteIfExists(MANIFEST_CACHE);
        if (!Files.exists(MODPACK_ZIP))
            if(Files.exists(TEMP))
                FileUtils.deleteDirectory(TEMP);
    }

    public static JsonObject loadLauncherConfig() {
        JsonObject json = new JsonObject();
        try(FileReader reader = new FileReader(MC_DIR.resolve("Launcher").resolve("config.json").toFile())) {
            json = JsonParser.parseReader(reader).getAsJsonObject();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return json;
    }

    public static String getMinecraftVersion() {
        return loadLauncherConfig().get("MINECRAFT_VERSION").getAsString();
    }

    public static String getLoaderVersion() {
        return loadLauncherConfig().get("LOADER_VERSION").getAsString();
    }

    public static String getModPackVersion() {
        return loadLauncherConfig().get("MODPACK_VERSION").getAsString();
    }

    public static int getProjectID() {
        return loadLauncherConfig().get("MODPACK_ID").getAsInt();
    }

    public static int getFileID() {
        return loadLauncherConfig().get("MODPACK_FILE").getAsInt();
    }

    public static String getModPackName() {
        return loadLauncherConfig().get("MODPACK_NAME").getAsString();
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

    public static void saveJsonLogin(JsonObject json) {
        try (FileWriter fileWriter = new FileWriter(MC_DIR.resolve("login.json").toFile())) {
            fileWriter.write(json.toString());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static JsonObject loadJsonLogin() {
        JsonObject json = new JsonObject();
        try (FileReader reader = new FileReader(MC_DIR.resolve("login.json").toFile())) {
            json = JsonParser.parseReader(reader).getAsJsonObject();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return json;
    }

    public static void startDiscordRPC() {
        DiscordEventHandlers handlers = new DiscordEventHandlers.Builder().setReadyEventHandler((_) -> {
            DiscordRichPresence richPresence = new DiscordRichPresence.Builder("Launcheur du Serveur Munchies").setStartTimestamps(System.currentTimeMillis() / 1000).setBigImage("logo", "Munchies : Beyond Limits - v" + Utils.getModPackVersion()).build();
            DiscordRPC.discordUpdatePresence(richPresence);
        }).build();
        DiscordRPC.discordInitialize("399951697360846859", handlers, true);
        DiscordRPC.discordRegister("399951697360846859", "");
    }
}
