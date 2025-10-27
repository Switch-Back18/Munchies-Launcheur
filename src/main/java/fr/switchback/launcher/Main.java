package fr.switchback.launcher;

import fr.switchback.launcher.ui.LauncherFrame;
import fr.switchback.launcher.utils.Utils;
import fr.theshark34.swinger.Swinger;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class Main {
    public static LauncherFrame frameInstance;

     static void main(String[] args) {
         Utils.startDiscordRPC();
         Swinger.setSystemLookNFeel();
         try {
             Utils.setMinimumRam(8);
             frameInstance = new LauncherFrame();
             Utils.removeOlderFiles();
             Utils.javaSetup();
         } catch (IOException e) {
             System.out.println(e.getMessage());
         }
         frameInstance.setVisible(true);
    }
}
