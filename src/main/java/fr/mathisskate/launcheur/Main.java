package fr.mathisskate.launcheur;

import fr.mathisskate.launcheur.ui.LauncheurFrame;
import fr.mathisskate.launcheur.utils.Helpers;
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
         try {
             Helpers.doUpdate();
         } catch (IOException e) {
             throw new RuntimeException(e);
         }
         Animator.fadeInFrame(frameInstance, 5);
        frameInstance.setVisible(true);
    }
}
