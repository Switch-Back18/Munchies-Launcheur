package fr.mathisskate.launcheur;

import fr.mathisskate.launcheur.ui.LauncheurFrame;
import fr.mathisskate.launcheur.utils.Helpers;
import fr.theshark34.swinger.Swinger;
import fr.theshark34.swinger.animation.Animator;
import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;

import java.io.IOException;
import java.sql.Time;
import java.time.LocalTime;
import java.util.concurrent.TimeUnit;

public class Main {
    public static LauncheurFrame frameInstance;

     public static void main(String[] args) {
         Swinger.setSystemLookNFeel();
         try {
             frameInstance = new LauncheurFrame();
         } catch (IOException e) {
             e.printStackTrace();
         }
         try {
             Helpers.doUpdate();
         } catch (IOException e) {
             throw new RuntimeException(e);
         }
         Animator.fadeInFrame(frameInstance, 5);
         frameInstance.setVisible(true);
         initDiscord();
         while (true)
             DiscordRPC.discordRunCallbacks();
    }

    private static void initDiscord() {
        DiscordEventHandlers handlers = new DiscordEventHandlers.Builder().setReadyEventHandler((discordUser) -> {
            DiscordRichPresence richPresence = new DiscordRichPresence.Builder("Launcheur du Serveur Munchies").setStartTimestamps(System.currentTimeMillis() / 1000).setBigImage("icon", "Munchies : Limitless Experience - v" + Helpers.VERSION).build();
            DiscordRPC.discordUpdatePresence(richPresence);
        }).build();
        DiscordRPC.discordInitialize("399951697360846859", handlers, false);
        DiscordRPC.discordRegister("399951697360846859", "");
    }
}
