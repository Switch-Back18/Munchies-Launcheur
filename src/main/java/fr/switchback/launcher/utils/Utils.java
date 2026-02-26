package fr.switchback.launcher.utils;

import com.google.gson.*;
import fr.flowarg.azuljavadownloader.*;
import fr.flowarg.flowio.FileUtils;
import fr.flowarg.flowlogger.ILogger;
import fr.flowarg.flowlogger.Logger;
import fr.flowarg.flowupdater.FlowUpdater;
import fr.flowarg.flowupdater.download.IProgressCallback;
import fr.flowarg.flowupdater.download.json.*;
import fr.flowarg.flowupdater.utils.ModFileDeleter;
import fr.flowarg.flowupdater.utils.UpdaterOptions;
import fr.flowarg.flowupdater.versions.*;
import fr.switchback.launcher.Main;
import fr.switchback.launcher.versions.CleanroomVersion;
import fr.switchback.launcher.versions.CleanroomVersionBuilder;
import fr.theshark34.openlauncherlib.minecraft.util.GameDirGenerator;
import fr.theshark34.openlauncherlib.util.ramselector.RamSelector;
import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;

public class Utils {
    public static final Path MC_DIR = GameDirGenerator.createGameDir("munchies", true);
    public static final Path LAUNCHER_DIR = MC_DIR.resolve("launcher");
    public static final Path TEMP = MC_DIR.resolve(".cfp");
    public static final Path MANIFEST = MC_DIR.resolve("manifest.json");
    public static final Path MANIFEST_CACHE = MC_DIR.resolve("manifest.cache.json");
    public static final Path MODPACK_ZIP = TEMP.resolve(getModPackName() + getModPackVersion() + ".zip");

    public static final RamSelector RAM_SELECTOR = new RamSelector(MC_DIR.resolve("launcher").resolve("ram.properties"));

    public static final int PROJECT_ID = getProjectID();
    public static final int FILE_ID = getFileID();

    public static final CurseModPackInfo MODPACK = new CurseModPackInfo(PROJECT_ID, FILE_ID, true);

    public static final ILogger LOGGER = new Logger("[Munchies Launcher]", MC_DIR.resolve("launcher").resolve("logs.log"), false);

    public static final IProgressCallback CALLBACK = new ProgressBarAPI();

    public static final VanillaVersion VANILLA = new VanillaVersion.VanillaVersionBuilder()
            .withName(getMinecraftVersion())
            .build();

    public static final CleanroomVersion CLEANROOM_VERSION = new CleanroomVersionBuilder()
            .withCleanroomVersion(getLoaderVersion())
            .withCurseModPack(MODPACK)
            .withFileDeleter(new ModFileDeleter(true))
            .build();

    public static final UpdaterOptions OPTIONS = new UpdaterOptions.UpdaterOptionsBuilder()
            .withJavaPath(System.getProperty("java.home"))
            .build();

    public static final FlowUpdater UPDATER = new FlowUpdater.FlowUpdaterBuilder()
            .withVanillaVersion(VANILLA)
            .withLogger(LOGGER)
            .withProgressCallback(CALLBACK)
            .withModLoaderVersion(CLEANROOM_VERSION)
            .withUpdaterOptions(OPTIONS)
            .build();

    public static void javaSetup(String javaVersion) throws IOException {
        final AzulJavaDownloader downloader = new AzulJavaDownloader(step -> {
            if(step == Callback.Step.DONE)
                Main.frameInstance.getLauncherPanel().getPlayButton().setEnabled(true);
        });
        Path java = LAUNCHER_DIR.resolve("java");
        AzulJavaBuildInfo buildInfo = downloader.getBuildInfo(new RequestedJavaInfo(javaVersion, AzulJavaType.JRE, OS.getOS().getAzulJavaOS(), AzulJavaArch.X64).setJavaFxBundled(true));
        Path javaHome = downloader.downloadAndInstall(buildInfo, java);
        System.setProperty("java.home", javaHome.toAbsolutePath().toString());
        System.out.println("Java Setup for " + OS.getOS().getOsName());
    }

    public static void removeOlderFiles() throws IOException {
        Files.deleteIfExists(MANIFEST);
        Files.deleteIfExists(MANIFEST_CACHE);
        if (!Files.exists(MODPACK_ZIP))
            if(Files.exists(TEMP))
                FileUtils.deleteDirectory(TEMP);
    }

    public static JsonObject loadLauncherConfig() {
        JsonObject json = new JsonObject();
        try(FileReader reader = new FileReader(LAUNCHER_DIR.resolve("config.json").toFile())) {
            json = JsonParser.parseReader(reader).getAsJsonObject();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return json;
    }

    public static void setMinimumRam(int min) {
        int y = 0;
        for (int i = min; i <= min + 9; i++) {
            RamSelector.RAM_ARRAY[y] = i + "Go";
            y++;
        }
    }

    public static boolean loggedBefore() {
        return Files.exists(LAUNCHER_DIR.resolve("login.json"));
    }

    public static void saveLoginJson(JsonObject json) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter fileWriter = new FileWriter(LAUNCHER_DIR.resolve("login.json").toFile())) {
            gson.toJson(json, fileWriter);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static JsonObject loadLoginJson() {
        JsonObject json = new JsonObject();
        try (FileReader reader = new FileReader(LAUNCHER_DIR.resolve("login.json").toFile())) {
            json = JsonParser.parseReader(reader).getAsJsonObject();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return json;
    }

    public static void removeLwjgl2(File file) throws IOException {
        String content = new String(Files.readAllBytes(file.toPath()));
        JsonObject root = JsonParser.parseString(content).getAsJsonObject();
        JsonArray libraries = root.getAsJsonArray("libraries");

        Iterator<JsonElement> iterator = libraries.iterator();
        while (iterator.hasNext()) {
            JsonObject lib = iterator.next().getAsJsonObject();
            String name = lib.get("name").getAsString();

            if (name.contains("org.lwjgl") && (name.contains(":2.") || name.contains("platform"))) {
                iterator.remove();
            }
        }
        try (Writer writer = new FileWriter(file)) {
            new GsonBuilder().setPrettyPrinting().create().toJson(root, writer);
        }
    }

    public static void startDiscordRPC() {
        DiscordEventHandlers handlers = new DiscordEventHandlers.Builder().setReadyEventHandler((_) -> {
            DiscordRichPresence richPresence = new DiscordRichPresence.Builder("Launcheur du Serveur Munchies").setStartTimestamps(System.currentTimeMillis() / 1000).setBigImage("logo", "Munchies : Beyond Limits - v" + Utils.getModPackVersion()).build();
            DiscordRPC.discordUpdatePresence(richPresence);
        }).build();
        DiscordRPC.discordInitialize("399951697360846859", handlers, true);
        DiscordRPC.discordRegister("399951697360846859", "");
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
}
