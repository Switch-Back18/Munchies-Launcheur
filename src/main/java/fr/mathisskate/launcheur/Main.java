package fr.mathisskate.launcheur;

import fr.mathisskate.launcheur.ui.LauncheurFrame;
import fr.mathisskate.launcheur.utils.Helpers;
import fr.theshark34.swinger.Swinger;
import fr.theshark34.swinger.animation.Animator;

import java.io.IOException;

public class Main {
    public static LauncheurFrame frameInstance;

    public static void main(String[] args)  {
        try {
            Helpers.cleanLauncheurFolder("1.4", "scripts", "config", "mods");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Helpers.cleanDirectory(Helpers.TEMP.toFile(), "1.5");
        Swinger.setSystemLookNFeel();
        try {
            frameInstance = new LauncheurFrame();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Animator.fadeInFrame(frameInstance, 5);
        frameInstance.setVisible(true);
    }
}
