package fr.switchback.launcher;

import fr.switchback.launcher.ui.LauncherFrame;
import fr.switchback.launcher.utils.Utils;
import fr.theshark34.swinger.Swinger;

import java.io.IOException;

public class Main {
    public static LauncherFrame frameInstance;

     public static void main(String[] args) {
         Utils.initDiscord();
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
}
