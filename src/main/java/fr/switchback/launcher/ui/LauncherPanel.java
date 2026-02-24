package fr.switchback.launcher.ui;

import fr.switchback.launcher.Launcher;
import fr.switchback.launcher.Main;
import fr.switchback.launcher.utils.OS;
import fr.switchback.launcher.utils.Utils;
import fr.theshark34.swinger.event.SwingerEvent;
import fr.theshark34.swinger.event.SwingerEventListener;
import fr.theshark34.swinger.textured.STexturedButton;
import fr.theshark34.swinger.textured.STexturedProgressBar;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

public class LauncherPanel extends JPanel implements SwingerEventListener {

    //Images
    private final BufferedImage TRANSPARENT_IMAGE;
    private final BufferedImage PLAY_IMAGE;
    private final BufferedImage LAUNCHER_IMAGE;

    private final STexturedProgressBar PROGRESSBAR;

    //Buttons
    private final STexturedButton FOLDER_BUTTON;
    private final STexturedButton PLAY_BUTTON;
    private final STexturedButton OPTION_BUTTON;
    private final STexturedButton QUIT_BUTTON;
    private final STexturedButton MINIMIZED_BUTTON;
    private final STexturedButton DISCORD_BUTTON;
    private final STexturedButton SITE_BUTTON;

    private boolean isLaunching;

    public LauncherPanel() throws IOException {
        TRANSPARENT_IMAGE = ImageIO.read(Objects.requireNonNull(Main.class.getClassLoader().getResource("transparent.png")));
        PLAY_IMAGE = ImageIO.read(Objects.requireNonNull(Main.class.getClassLoader().getResource("bouton/play.png")));
        BufferedImage LOAD_IMAGE = ImageIO.read(Objects.requireNonNull(Main.class.getClassLoader().getResource("load.png")));
        BufferedImage OPTION_IMAGE = ImageIO.read(Objects.requireNonNull(Main.class.getClassLoader().getResource("bouton/option.png")));
        BufferedImage QUIT_IMAGE = ImageIO.read(Objects.requireNonNull(Main.class.getClassLoader().getResource("bouton/quit.png")));
        BufferedImage MINIMIZED_IMAGE = ImageIO.read(Objects.requireNonNull(Main.class.getClassLoader().getResource("bouton/minimized.png")));
        BufferedImage DISCORD_IMAGE = ImageIO.read(Objects.requireNonNull(Main.class.getClassLoader().getResource("bouton/discord.png")));
        BufferedImage SITE_IMAGE = ImageIO.read(Objects.requireNonNull(Main.class.getClassLoader().getResource("bouton/site.png")));
        LAUNCHER_IMAGE = ImageIO.read(Objects.requireNonNull(Main.class.getClassLoader().getResource("launcher.png")));

        PROGRESSBAR = new STexturedProgressBar(TRANSPARENT_IMAGE, LOAD_IMAGE);

        FOLDER_BUTTON = new STexturedButton(TRANSPARENT_IMAGE);
        PLAY_BUTTON = new STexturedButton(TRANSPARENT_IMAGE, PLAY_IMAGE);
        OPTION_BUTTON = new STexturedButton(TRANSPARENT_IMAGE, OPTION_IMAGE);
        QUIT_BUTTON = new STexturedButton(TRANSPARENT_IMAGE, QUIT_IMAGE);
        MINIMIZED_BUTTON = new STexturedButton(TRANSPARENT_IMAGE, MINIMIZED_IMAGE);
        DISCORD_BUTTON = new STexturedButton(TRANSPARENT_IMAGE, DISCORD_IMAGE);
        SITE_BUTTON = new STexturedButton(TRANSPARENT_IMAGE, SITE_IMAGE);

        isLaunching = false;

        setLayout(null);

        PROGRESSBAR.setBounds(409, 669, 181, 6);
        PROGRESSBAR.setStringPainted(true);
        add(PROGRESSBAR);

        PLAY_BUTTON.setBounds(346, 556, 320, 114);
        PLAY_BUTTON.setEnabled(false);
        PLAY_BUTTON.addEventListener(this);
        add(PLAY_BUTTON);

        QUIT_BUTTON.setBounds(638, 358, 50, 50);
        QUIT_BUTTON.addEventListener(this);
        add(QUIT_BUTTON);

        MINIMIZED_BUTTON.setBounds(682, 479, 50, 50);
        MINIMIZED_BUTTON.addEventListener(this);
        add(MINIMIZED_BUTTON);

        OPTION_BUTTON.setBounds(660, 525, 50, 50);
        OPTION_BUTTON.addEventListener(this);
        add(OPTION_BUTTON);

        DISCORD_BUTTON.setBounds(290, 570, 50, 50);
        DISCORD_BUTTON.addEventListener(this);
        add(DISCORD_BUTTON);

        SITE_BUTTON.setBounds(257, 508, 50, 50);
        SITE_BUTTON.addEventListener(this);
        add(SITE_BUTTON);

        FOLDER_BUTTON.setBounds(460, 285, 50, 50);
        FOLDER_BUTTON.addEventListener(this);
        add(FOLDER_BUTTON);
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.drawImage(LAUNCHER_IMAGE, 0, 0, this);
    }

    @Override
    public void onEvent(SwingerEvent e) {
        if (e.getSource() == OPTION_BUTTON)
            Utils.RAM_SELECTOR.display();
        else if (e.getSource() == QUIT_BUTTON)
            System.exit(0);
        else if (e.getSource() == MINIMIZED_BUTTON)
            Main.frameInstance.setState(Frame.ICONIFIED);
        else if (e.getSource() == FOLDER_BUTTON) {
            try {
                Desktop.getDesktop().open(Utils.MC_DIR.toFile());
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        } else if (e.getSource() == DISCORD_BUTTON) {
            try {
                URI oURL = new URI("https://discord.com/invite/erUg4NnADM");
                if (OS.getOS() == OS.LINUX)
                    Runtime.getRuntime().exec(new String[] {"xdg-open", String.valueOf(oURL)});
                else
                    Desktop.getDesktop().browse(oURL);
            } catch (URISyntaxException | IOException ex) {
                System.out.println(ex.getMessage());
            }
        } else if (e.getSource() == SITE_BUTTON) {
            try {
                URI oURL = new URI("https://munchies.websr.fr");
                if(OS.getOS() == OS.LINUX)
                    Runtime.getRuntime().exec(new String[] {"xdg-open", String.valueOf(oURL)});
                else
                    Desktop.getDesktop().browse(oURL);
            } catch (URISyntaxException | IOException ex) {
                System.out.println(ex.getMessage());
            }
        } else if (e.getSource() == PLAY_BUTTON) {
            if(!isLaunching) {
                isLaunching = true;
                PLAY_BUTTON.setTexture(PLAY_IMAGE);
                Thread launchThread = new Thread(() -> {
                    try {
                        Launcher.auth();
                        try {
                            Launcher.update();
                            if (Utils.MC_DIR.toFile().exists())
                                Utils.RAM_SELECTOR.save();
                            Launcher.launch();
                        } catch (Exception ex) {
                            System.out.println(ex.getMessage());
                        }
                    } catch (Exception ex) {
                        isLaunching = false;
                        PLAY_BUTTON.setTexture(TRANSPARENT_IMAGE);
                        JOptionPane.showMessageDialog(Main.frameInstance,
                                "Impossible de se connecter : Vérifie si tu as un compte Microsoft lié à un compte Minecraft.", "Erreur",
                                JOptionPane.ERROR_MESSAGE);
                        System.out.println(ex.getMessage());
                    }
                });
                launchThread.start();
            }
        }
    }

    public STexturedProgressBar getProgressBar() {
        return PROGRESSBAR;
    }

    public STexturedButton getPlayButton() {
        return PLAY_BUTTON;
    }
}