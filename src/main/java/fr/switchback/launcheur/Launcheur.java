package fr.switchback.launcheur;

import fr.flowarg.openlauncherlib.NoFramework;
import fr.litarvan.openauth.AuthenticationException;
import fr.litarvan.openauth.microsoft.MicrosoftAuthResult;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticationException;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticator;
import fr.switchback.launcheur.utils.Utils;
import fr.theshark34.openlauncherlib.minecraft.AuthInfos;
import fr.theshark34.openlauncherlib.minecraft.GameFolder;
import net.arikia.dev.drpc.DiscordRPC;

public class Launcheur {
    public static MicrosoftAuthResult result;
    private static MicrosoftAuthenticator authentificator;
    private static AuthInfos authInfos;

    public static void auth(boolean logged) throws AuthenticationException, MicrosoftAuthenticationException {
        authentificator = new MicrosoftAuthenticator();
        if (!logged) {
            result = authentificator.loginWithWebview();
        } else {
            result = authentificator.loginWithRefreshToken(Utils.SAVER.get("token"));
        }
        System.out.println("[Pseudo Minecraft] " + result.getProfile().getName());
        authInfos = new AuthInfos(result.getProfile().getName(), result.getAccessToken(),
                result.getProfile().getId());
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