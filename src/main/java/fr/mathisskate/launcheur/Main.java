package fr.mathisskate.launcheur;

import fr.mathisskate.launcheur.ui.LauncheurFrame;
import fr.theshark34.swinger.Swinger;
import fr.theshark34.swinger.animation.Animator;

import java.io.IOException;

public class Main {
    public static LauncheurFrame frameInstance;

     public static void main(String[] args) {
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
