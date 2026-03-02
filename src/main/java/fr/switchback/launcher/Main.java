package fr.switchback.launcher;

import fr.switchback.launcher.ui.LauncherFrame;
import fr.switchback.launcher.utils.Utils;
import fr.theshark34.swinger.Swinger;

import javax.swing.*;

public class Main {
    public static volatile LauncherFrame frameInstance;

    void main() {
        Utils.setMinimumRam(8);
        Utils.startDiscordRPC();
        SwingUtilities.invokeLater(() -> {
            try {
                Swinger.setSystemLookNFeel();
                frameInstance = new LauncherFrame();
                frameInstance.setVisible(true);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null,
                        "Erreur critique au démarrage : " + e.getMessage(),
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }
}