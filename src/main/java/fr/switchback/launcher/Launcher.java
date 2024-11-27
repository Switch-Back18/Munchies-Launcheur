package fr.switchback.launcher;

import fr.flowarg.openlauncherlib.NoFramework;
import fr.switchback.launcher.utils.OS;
import fr.switchback.launcher.utils.Utils;
import fr.theshark34.openlauncherlib.minecraft.AuthInfos;
import fr.theshark34.openlauncherlib.minecraft.GameFolder;
import net.arikia.dev.drpc.DiscordRPC;
import net.lenni0451.commons.httpclient.HttpClient;
import net.raphimc.minecraftauth.MinecraftAuth;
import net.raphimc.minecraftauth.step.java.session.StepFullJavaSession.FullJavaSession;
import net.raphimc.minecraftauth.step.msa.StepMsaDeviceCode;

import java.awt.*;
import java.io.IOException;
import java.net.URI;

public class Launcher {
    private static AuthInfos authInfos;

    public static void auth() throws Exception {
        HttpClient httpClient = MinecraftAuth.createHttpClient();
        FullJavaSession javaSession = null;
        if (!Utils.loggedBefore()) {
            javaSession = MinecraftAuth.JAVA_DEVICE_CODE_LOGIN.getFromInput(httpClient, new StepMsaDeviceCode.MsaDeviceCodeCallback(msaDeviceCode -> {
                try {
                    URI uri = URI.create(msaDeviceCode.getDirectVerificationUri());
                    if(OS.getOS() == OS.LINUX)
                        Runtime.getRuntime().exec(new String[] {"xdg-open", String.valueOf(uri)});
                    else
                        Desktop.getDesktop().browse(uri);
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }));
        } else {
            javaSession = MinecraftAuth.JAVA_DEVICE_CODE_LOGIN.refresh(httpClient, MinecraftAuth.JAVA_DEVICE_CODE_LOGIN.fromJson(Utils.loadJson()));
        }
        Utils.saveJson(MinecraftAuth.JAVA_DEVICE_CODE_LOGIN.toJson(javaSession));
        authInfos = new AuthInfos(javaSession.getMcProfile().getName(), javaSession.getMcProfile().getMcToken().getAccessToken(), String.valueOf(javaSession.getMcProfile().getId()));
        System.out.println("[Minecraft Username] : " + javaSession.getMcProfile().getName());
    }

    public static void update() throws Exception {
        Utils.UPDATER.update(Utils.MC_DIR);
    }

    public static void launch() throws Exception {
        NoFramework noFramework = new NoFramework(Utils.MC_DIR, authInfos, GameFolder.FLOW_UPDATER);
        noFramework.getAdditionalVmArgs().add(Utils.RAM_SELECTOR.getRamArguments()[1]);
        Process process = noFramework.launch(Utils.getMinecraftVersion(), Utils.getLoaderVersion(), NoFramework.ModLoader.FORGE);
        try {
            Thread.sleep(5000L);
            Main.frameInstance.setVisible(false);
            DiscordRPC.discordShutdown();
            process.waitFor();
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
        System.exit(0);
    }
}