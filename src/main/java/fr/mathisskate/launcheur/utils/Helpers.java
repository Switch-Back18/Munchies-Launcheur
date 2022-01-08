package fr.mathisskate.launcheur.utils;

import fr.theshark34.openlauncherlib.minecraft.GameInfos;
import fr.theshark34.openlauncherlib.minecraft.GameTweak;
import fr.theshark34.openlauncherlib.minecraft.GameType;
import fr.theshark34.openlauncherlib.minecraft.GameVersion;
import fr.theshark34.openlauncherlib.util.Saver;
import fr.theshark34.openlauncherlib.util.ramselector.RamSelector;
import fr.theshark34.supdate.SUpdate;

import java.nio.file.Path;

public class Helpers {
    //OpenLauncheurLib
    //public static final NewForgeVersionDiscriminator forge = new NewForgeVersionDiscriminator("36.2.8", "1.16.5", "net.minecraftforge", "20210115.111550");
    //public static final GameType version = GameType.V1_13_HIGHER_FORGE.setNFVD(forge);
    public static final GameVersion MC_VERSION = new GameVersion("1.12.2", GameType.V1_8_HIGHER);
    public static final GameInfos MC_INFOS = new GameInfos("munchies", MC_VERSION, new GameTweak[]{GameTweak.FORGE});
    public static final Path MC_DIR = MC_INFOS.getGameDir();
    public static final Saver SAVER = new Saver(Helpers.MC_DIR.resolve("options.properties"));
    public static final RamSelector RAM_SELECTOR = new RamSelector(Helpers.MC_DIR.resolve("ram.properties"));
    public final static SUpdate sUpdate = new SUpdate("https://munchies.websr.fr/Munchies", MC_DIR.toFile());
}
