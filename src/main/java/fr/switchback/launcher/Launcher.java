package fr.switchback.launcher;

import club.minnced.discord.rpc.DiscordRPC;
import fr.flowarg.openlauncherlib.NoFramework;
import fr.switchback.launcher.utils.OS;
import fr.switchback.launcher.utils.Utils;
import fr.theshark34.openlauncherlib.minecraft.AuthInfos;
import fr.theshark34.openlauncherlib.minecraft.GameFolder;
import net.lenni0451.commons.httpclient.HttpClient;
import net.raphimc.minecraftauth.MinecraftAuth;
import net.raphimc.minecraftauth.java.JavaAuthManager;
import net.raphimc.minecraftauth.msa.model.MsaDeviceCode;
import net.raphimc.minecraftauth.msa.service.impl.DeviceCodeMsaAuthService;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.function.Consumer;

public class Launcher {
    private static AuthInfos authInfos;

    public static void auth() throws Exception {
        HttpClient httpClient = MinecraftAuth.createHttpClient();
        JavaAuthManager.Builder authManagerBuilder = JavaAuthManager.create(httpClient);
        JavaAuthManager authManager;
        if(!Utils.loggedBefore()) {
            authManager = authManagerBuilder.login(DeviceCodeMsaAuthService::new, new Consumer<MsaDeviceCode>() {
                @Override
                public void accept(MsaDeviceCode deviceCode) {
                    URI uri = URI.create(deviceCode.getDirectVerificationUri());
                    if (OS.getOS().getOsName().equals("LINUX")) {
                        try {
                            Runtime.getRuntime().exec(new String[]{"xdg-open", String.valueOf(uri)});
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    else {
                        try {
                            Desktop.getDesktop().browse(uri);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            });
        } else {
            authManager = JavaAuthManager.fromJson(httpClient, Utils.loadLoginJson());
            authManager.getMinecraftToken().refresh();
            authManager.getMinecraftProfile().refresh();
        }
        Utils.saveLoginJson(JavaAuthManager.toJson(authManager));
        authInfos = new AuthInfos(authManager.getMinecraftProfile().getUpToDate().getName(), authManager.getMinecraftToken().getUpToDate().getToken(), String.valueOf(authManager.getMinecraftProfile().getUpToDate().getId()));
    }

    public static void update() throws Exception {
        Utils.javaSetup("25");
        Utils.removeOlderFiles();
        Utils.UPDATER.update(Utils.MC_DIR);
        if(Utils.UPDATER.getModLoaderVersion().name().equals("Cleanroom"))
            Utils.removeLwjgl2();
        Utils.downloadResourcePack();
    }

    public static void launch() throws Exception {
        NoFramework noFramework = new NoFramework(Utils.MC_DIR, authInfos, GameFolder.FLOW_UPDATER);
        noFramework.getAdditionalVmArgs().addAll(List.of(Utils.RAM_SELECTOR.getRamArguments()));
        noFramework.getAdditionalVmArgs().add("-XX:+UseCompactObjectHeaders");
        noFramework.getAdditionalVmArgs().add("-XX:+UseZGC");
        noFramework.getAdditionalVmArgs().add("-XX:+ZGenerational");
        noFramework.getAdditionalArgs().add("--width=1024");
        noFramework.getAdditionalArgs().add("--height=768");
        NoFramework.ModLoader.CUSTOM.setJsonFileNameProvider((version, loaderVer) -> Utils.UPDATER.getModLoaderVersion().name() + "-" + loaderVer + ".json");
        Process process = noFramework.launch(Utils.getMinecraftVersion(), Utils.getLoaderVersion(),  NoFramework.ModLoader.CUSTOM);
        try {
            Thread.sleep(5000L);
            Main.frameInstance.setVisible(false);
            DiscordRPC.INSTANCE.Discord_Shutdown();
            process.waitFor();
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
        System.exit(0);
    }
}