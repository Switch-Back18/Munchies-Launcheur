package fr.mathisskate.launcheur;

import fr.litarvan.openauth.AuthenticationException;
import fr.litarvan.openauth.microsoft.MicrosoftAuthResult;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticationException;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticator;
import fr.mathisskate.launcheur.utils.Helpers;
import fr.theshark34.openlauncherlib.LaunchException;
import fr.theshark34.openlauncherlib.external.ExternalLaunchProfile;
import fr.theshark34.openlauncherlib.external.ExternalLauncher;
import fr.theshark34.openlauncherlib.minecraft.AuthInfos;
import fr.theshark34.openlauncherlib.minecraft.GameFolder;
import fr.theshark34.openlauncherlib.minecraft.MinecraftLauncher;
import fr.theshark34.openlauncherlib.util.ProcessLogManager;
import fr.theshark34.supdate.BarAPI;
import fr.theshark34.supdate.application.integrated.FileDeleter;
import fr.theshark34.swinger.animation.Animator;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;

public class Launcheur {
    public static MicrosoftAuthResult result;
    private static MicrosoftAuthenticator authentificator;
    private static AuthInfos authInfos;

    public static void auth(boolean logged) throws AuthenticationException, MicrosoftAuthenticationException {
        authentificator = new MicrosoftAuthenticator();
        if (!logged) {
            result = authentificator.loginWithWebview();
        } else {
            result = authentificator.loginWithRefreshToken(Helpers.SAVER.get("token"));
        }
        System.out.println("[Pseudo Minecraft] " + result.getProfile().getName());
        authInfos = new AuthInfos(result.getProfile().getName(), result.getAccessToken(),
                result.getProfile().getId());
    }

    public static void update() throws Exception {
        Helpers.sUpdate.getServerRequester().setRewriteEnabled(true);
        Helpers.sUpdate.addApplication(new FileDeleter());
        Thread threadBar = new Thread() {
            private int val;
            private int max;

            @Override
            public void run() {
                while (!isInterrupted()) {
                    val = (int) (BarAPI.getNumberOfTotalDownloadedBytes() / 1000);
                    max = (int) (BarAPI.getNumberOfTotalBytesToDownload() / 1000);

                    Main.frameInstance.getLauncherPanel().getProgressBar().setMaximum(max);
                    Main.frameInstance.getLauncherPanel().getProgressBar().setValue(val);
                }
            }
        };
        threadBar.start();
        Helpers.sUpdate.start();
        threadBar.interrupt();
    }

    public static void launch() throws LaunchException, IOException {
        ExternalLaunchProfile profile = MinecraftLauncher.createExternalProfile(Helpers.MC_INFOS, GameFolder.FLOW_UPDATER, authInfos);
        profile.getVmArgs().addAll(
                Arrays.asList(Helpers.RAM_SELECTOR.getRamArguments())
        );
        ExternalLauncher launcher = new ExternalLauncher(profile);
        Process p = launcher.launch();
        ProcessLogManager manager = new ProcessLogManager(p.getInputStream(), Paths.get(Helpers.MC_DIR + "logs.txt"));
        manager.start();
        try {
            Animator.fadeOutFrame(Main.frameInstance, 5);
            Thread.sleep(5000L);
            Main.frameInstance.setVisible(false);
            p.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }
}