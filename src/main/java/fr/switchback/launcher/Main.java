package fr.switchback.launcher;

import fr.switchback.launcher.ui.LauncherFrame;
import fr.switchback.launcher.utils.Utils;
import fr.theshark34.swinger.Swinger;

import java.io.IOException;

public class Main {
    public static LauncherFrame frameInstance;
    void main() {
        Utils.setMinimumRam(8);
        Utils.startDiscordRPC();
        Swinger.setSystemLookNFeel();
        try {
            frameInstance = new LauncherFrame();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        frameInstance.setVisible(true);
    }
}
