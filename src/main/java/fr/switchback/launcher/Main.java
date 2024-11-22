package fr.switchback.launcher;

import fr.switchback.launcher.ui.LauncherFrame;
import fr.switchback.launcher.utils.Utils;
import fr.theshark34.swinger.Swinger;
import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;

import java.io.IOException;

public class Main {
    public static LauncherFrame frameInstance;

     public static void main(String[] args) {
         initDiscord();
         Swinger.setSystemLookNFeel();
         try {
             frameInstance = new LauncherFrame();
             Utils.javaSetup();
             Utils.doUpdate();
             Utils.setMinMaxRam(8, 17);
         } catch (IOException e) {
             System.out.println(e.getMessage());
         }
         frameInstance.setVisible(true);
    }

    private static void initDiscord() {
        DiscordEventHandlers handlers = new DiscordEventHandlers.Builder().setReadyEventHandler((discordUser) -> {
            DiscordRichPresence richPresence = new DiscordRichPresence.Builder("Launcheur du Serveur Munchies").setStartTimestamps(System.currentTimeMillis() / 1000).setBigImage("logo", "Munchies : Beyond Limits - v" + Utils.getModPackVersion()).build();
            DiscordRPC.discordUpdatePresence(richPresence);
        }).build();
        DiscordRPC.discordInitialize("399951697360846859", handlers, false);
        DiscordRPC.discordRegister("399951697360846859", "");
    }
}
