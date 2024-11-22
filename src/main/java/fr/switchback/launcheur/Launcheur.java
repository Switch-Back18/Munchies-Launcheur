package fr.switchback.launcheur;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fr.flowarg.openlauncherlib.NoFramework;
import fr.switchback.launcheur.utils.Utils;
import fr.theshark34.openlauncherlib.minecraft.AuthInfos;
import fr.theshark34.openlauncherlib.minecraft.GameFolder;
import net.arikia.dev.drpc.DiscordRPC;
import net.lenni0451.commons.httpclient.HttpClient;
import net.raphimc.minecraftauth.MinecraftAuth;
import net.raphimc.minecraftauth.step.java.session.StepFullJavaSession;
import net.raphimc.minecraftauth.step.msa.StepMsaDeviceCode;

import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.util.Scanner;

public class Launcheur {
    private static AuthInfos authInfos;

    public static void auth(boolean loggedBefore) throws Exception {
        if (!loggedBefore) {
            HttpClient httpClient = MinecraftAuth.createHttpClient();
            StepFullJavaSession.FullJavaSession javaSession = MinecraftAuth.JAVA_DEVICE_CODE_LOGIN.getFromInput(httpClient, new StepMsaDeviceCode.MsaDeviceCodeCallback(msaDeviceCode -> {
                Desktop desktop = Desktop.getDesktop();
                try {
                    desktop.browse(URI.create(msaDeviceCode.getDirectVerificationUri()));
                } catch (IOException e) {
                    System.out.println(e.getMessage());;
                }
            }));
            JsonObject serializedSession = MinecraftAuth.JAVA_DEVICE_CODE_LOGIN.toJson(javaSession);
            FileWriter fileWriter = new FileWriter(Utils.MC_DIR.resolve("login.json").toFile());
            fileWriter.write(serializedSession.toString());
            fileWriter.close();
            authInfos = new AuthInfos(javaSession.getMcProfile().getName(), javaSession.getMcProfile().getMcToken().getAccessToken(),
                    javaSession.getMcProfile().getId().toString());
            System.out.println("[Pseudo Minecraft] " + authInfos.getUsername());
            return;
        }
        String jsonString = "";
        JsonObject session = new JsonObject();
        JsonParser parser = new JsonParser();
        Scanner scanner = new Scanner(Utils.MC_DIR.resolve("login.json").toFile());
        while (scanner.hasNextLine())
            jsonString += scanner.nextLine();
        session = parser.parse(jsonString).getAsJsonObject();
        StepFullJavaSession.FullJavaSession javaSession = MinecraftAuth.JAVA_DEVICE_CODE_LOGIN.fromJson(session);
        authInfos = new AuthInfos(javaSession.getMcProfile().getName(), javaSession.getMcProfile().getMcToken().getAccessToken(),
                javaSession.getMcProfile().getId().toString());
        System.out.println("[Pseudo Minecraft] " + authInfos.getUsername());
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