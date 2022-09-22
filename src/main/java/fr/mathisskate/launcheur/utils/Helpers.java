package fr.mathisskate.launcheur.utils;

import fr.flowarg.flowlogger.ILogger;
import fr.flowarg.flowlogger.Logger;
import fr.flowarg.flowupdater.FlowUpdater;
import fr.flowarg.flowupdater.download.IProgressCallback;
import fr.flowarg.flowupdater.download.json.CurseModPackInfo;
import fr.flowarg.flowupdater.utils.ModFileDeleter;
import fr.flowarg.flowupdater.utils.UpdaterOptions;
import fr.flowarg.flowupdater.versions.AbstractForgeVersion;
import fr.flowarg.flowupdater.versions.ForgeVersionBuilder;
import fr.flowarg.flowupdater.versions.VanillaVersion;
import fr.flowarg.openlauncherlib.NewForgeVersionDiscriminator;
import fr.theshark34.openlauncherlib.minecraft.GameInfos;
import fr.theshark34.openlauncherlib.minecraft.GameType;
import fr.theshark34.openlauncherlib.minecraft.GameVersion;
import fr.theshark34.openlauncherlib.util.Saver;
import fr.theshark34.openlauncherlib.util.ramselector.RamSelector;
import java.nio.file.Path;

public class Helpers {
    //OpenLauncheurLib
    public static final NewForgeVersionDiscriminator FORGE = new NewForgeVersionDiscriminator("36.2.39", "1.16.5", "net.minecraftforge", "20210115.111550");
    public static final GameType version = GameType.V1_13_HIGHER_FORGE.setNFVD(FORGE);
    public static final GameVersion MC_VERSION = new GameVersion("1.16.5", version);
    public static final GameInfos MC_INFOS = new GameInfos("munchies", MC_VERSION, null);
    public static final Path MC_DIR = MC_INFOS.getGameDir();
    public static final Saver SAVER = new Saver(Helpers.MC_DIR.resolve("options.properties"));
    public static final RamSelector RAM_SELECTOR = new RamSelector(Helpers.MC_DIR.resolve("ram.properties"));


    //FlowUpdater
    public static final ILogger LOGGER = new Logger("[Munchies]", Helpers.MC_DIR.resolve("logs.log"), true);
    //public static final Path TEMP = MC_INFOS.getGameDir().resolve(".cfp");
    public static IProgressCallback CALLBACK = new ProgressBarAPI();
    public static VanillaVersion VANILLA = new VanillaVersion.VanillaVersionBuilder()
            .withName("1.16.5")
            .build();

    public static final CurseModPackInfo MODPACK = new CurseModPackInfo(626647, 4000796, true);

    public static final UpdaterOptions OPTIONS = new UpdaterOptions.UpdaterOptionsBuilder().build();

    public static final AbstractForgeVersion FORGE_VERSION = new ForgeVersionBuilder(ForgeVersionBuilder.ForgeVersionType.NEW)
            .withForgeVersion("1.16.5-36.2.39")
            .withCurseModPack(MODPACK)
            .withFileDeleter(new ModFileDeleter(true))
            .build();

    public static final FlowUpdater UPDATER = new FlowUpdater.FlowUpdaterBuilder()
            .withVanillaVersion(VANILLA)
            .withLogger(LOGGER)
            .withModLoaderVersion(FORGE_VERSION)
            .withProgressCallback(CALLBACK)
            .withUpdaterOptions(OPTIONS)
            .build();
}
