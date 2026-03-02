package fr.switchback.launcher;

import club.minnced.discord.rpc.DiscordRPC;
import fr.flowarg.openlauncherlib.NoFramework;
import fr.theshark34.openlauncherlib.minecraft.AuthInfos;
import fr.theshark34.openlauncherlib.minecraft.GameFolder;
import net.lenni0451.commons.httpclient.HttpClient;
import net.raphimc.minecraftauth.MinecraftAuth;
import net.raphimc.minecraftauth.java.JavaAuthManager;
import net.raphimc.minecraftauth.msa.model.MsaDeviceCode;
import net.raphimc.minecraftauth.msa.service.impl.DeviceCodeMsaAuthService;

import java.util.List;
import java.util.function.Consumer;

import static fr.switchback.launcher.utils.Utils.*;

public class Launcher {
    private static AuthInfos authInfos;

    public static void auth() throws Exception {
        HttpClient httpClient = MinecraftAuth.createHttpClient();
        JavaAuthManager.Builder authManagerBuilder = JavaAuthManager.create(httpClient);
        JavaAuthManager authManager;
        if(!loggedBefore()) {
            authManager = authManagerBuilder.login(DeviceCodeMsaAuthService::new, (Consumer<MsaDeviceCode>) deviceCode -> openWebPage(deviceCode.getDirectVerificationUri()));
        } else {
            authManager = JavaAuthManager.fromJson(httpClient, loadLoginJson());
            authManager.getMinecraftToken().refresh();
            authManager.getMinecraftProfile().refresh();
        }
        saveLoginJson(JavaAuthManager.toJson(authManager));
        authInfos = new AuthInfos(authManager.getMinecraftProfile().getUpToDate().getName(), authManager.getMinecraftToken().getUpToDate().getToken(), String.valueOf(authManager.getMinecraftProfile().getUpToDate().getId()));
    }

    public static void update() throws Exception {
        javaSetup("25");
        removeOlderFiles();
        UPDATER.update(GAME_DIR);
        if(UPDATER.getModLoaderVersion().name().equals("Cleanroom"))
            removeLwjgl2();
        downloadResourcePack();
    }

    public static void launch() throws Exception {
        NoFramework noFramework = new NoFramework(GAME_DIR, authInfos, GameFolder.FLOW_UPDATER);
        noFramework.getAdditionalVmArgs().addAll(List.of(RAM_SELECTOR.getRamArguments()));
        noFramework.getAdditionalVmArgs().add("-XX:+UseCompactObjectHeaders");
        noFramework.getAdditionalVmArgs().add("-XX:+UseZGC");
        noFramework.getAdditionalVmArgs().add("-XX:+ZGenerational");
        noFramework.getAdditionalArgs().add("--width=1024");
        noFramework.getAdditionalArgs().add("--height=768");
        NoFramework.ModLoader.CUSTOM.setJsonFileNameProvider((_, loaderVer) -> UPDATER.getModLoaderVersion().name() + "-" + loaderVer + ".json");
        Process process = noFramework.launch(getMinecraftVersion(), getLoaderVersion(),  NoFramework.ModLoader.CUSTOM);
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