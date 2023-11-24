package fr.mathisskate.launcheur.ui;

import fr.litarvan.openauth.AuthenticationException;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticationException;
import fr.mathisskate.launcheur.Launcheur;
import fr.mathisskate.launcheur.Main;
import fr.mathisskate.launcheur.utils.Helpers;
import fr.theshark34.openlauncherlib.util.ramselector.RamSelector;
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

public class LauncheurPanel extends JPanel implements SwingerEventListener {

    //Images
    private final BufferedImage TRANSPA = ImageIO.read(Objects.requireNonNull(Main.class.getClassLoader().getResource("transpa.png")));
    private final STexturedButton DOSSIER = new STexturedButton(TRANSPA);
    private final BufferedImage IMG_JOUER = ImageIO.read(Objects.requireNonNull(Main.class.getClassLoader().getResource("bouton/jouer.png")));
    private final STexturedButton JOUER = new STexturedButton(TRANSPA, IMG_JOUER);
    private final BufferedImage IMG_LOAD = ImageIO.read(Objects.requireNonNull(Main.class.getClassLoader().getResource("load.png")));
    private final STexturedProgressBar PROGRESS_BAR = new STexturedProgressBar(TRANSPA, IMG_LOAD);
    private final BufferedImage IMG_OPTION = ImageIO.read(Objects.requireNonNull(Main.class.getClassLoader().getResource("bouton/option.png")));
    private final STexturedButton OPTION = new STexturedButton(TRANSPA, IMG_OPTION);
    private final BufferedImage IMG_QUIT = ImageIO.read(Objects.requireNonNull(Main.class.getClassLoader().getResource("bouton/fermer.png")));
    private final STexturedButton QUIT = new STexturedButton(TRANSPA, IMG_QUIT);
    private final BufferedImage IMG_MINI = ImageIO.read(Objects.requireNonNull(Main.class.getClassLoader().getResource("bouton/mini.png")));
    private final STexturedButton MINI = new STexturedButton(TRANSPA, IMG_MINI);
    private final BufferedImage IMG_DISCORD = ImageIO.read(Objects.requireNonNull(Main.class.getClassLoader().getResource("bouton/discord.png")));
    private final STexturedButton DISCORD = new STexturedButton(TRANSPA, IMG_DISCORD);
    private final BufferedImage IMG_SITE = ImageIO.read(Objects.requireNonNull(Main.class.getClassLoader().getResource("bouton/site.png")));
    private final STexturedButton SITE = new STexturedButton(TRANSPA, IMG_SITE);
    private final BufferedImage LAUNCHEUR = ImageIO.read(Objects.requireNonNull(Main.class.getClassLoader().getResource("launcheur.png")));

    private boolean isLaunch = false;

    public LauncheurPanel() throws IOException {
        setLayout(null);
        setMinMaxRam(8, 17);

        JOUER.setBounds(346, 556, 320, 114);
        JOUER.addEventListener(this);
        add(JOUER);

        PROGRESS_BAR.setBounds(409, 669, 181, 6);
        PROGRESS_BAR.setStringPainted(true);
        add(PROGRESS_BAR);

        QUIT.setBounds(638, 358, 50, 50);
        QUIT.addEventListener(this);
        add(QUIT);

        MINI.setBounds(682, 479, 50, 50);
        MINI.addEventListener(this);
        add(MINI);

        OPTION.setBounds(660, 525, 50, 50);
        OPTION.addEventListener(this);
        add(OPTION);

        DISCORD.setBounds(290, 570, 50, 50);
        DISCORD.addEventListener(this);
        add(DISCORD);

        SITE.setBounds(257, 508, 50, 50);
        SITE.addEventListener(this);
        add(SITE);

        DOSSIER.setBounds(460, 285, 50, 50);
        DOSSIER.addEventListener(this);
        add(DOSSIER);
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.drawImage(LAUNCHEUR, 0, 0, this);
    }

    @Override
    public void onEvent(SwingerEvent e) {
        if (e.getSource() == OPTION) {
            Helpers.RAM_SELECTOR.display();
        } else if (e.getSource() == QUIT) {
            System.exit(0);
        } else if (e.getSource() == MINI) {
            Main.frameInstance.setState(Frame.ICONIFIED);
        } else if (e.getSource() == DOSSIER) {
            try {
                Desktop.getDesktop().open(Helpers.MC_DIR.toFile());
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        } else if (e.getSource() == DISCORD) {
            try {
                Desktop desktop = Desktop.getDesktop();
                URI oURL = new URI("https://discord.com/invite/erUg4NnADM");
                desktop.browse(oURL);
            } catch (URISyntaxException | IOException ex) {
                System.out.println(ex.getMessage());
            }
        } else if (e.getSource() == SITE) {
            try {
                Desktop desktop = Desktop.getDesktop();
                URI oURL = new URI("https://munchies.websr.fr");
                desktop.browse(oURL);
            } catch (URISyntaxException | IOException ex) {
                System.out.println(ex.getMessage());
            }
        } else if (e.getSource() == JOUER) {
            if(!isLaunch) {
                isLaunch = true;
                JOUER.setTexture(IMG_JOUER);
                Thread t = new Thread(() -> {
                    try {
                        Launcheur.auth(hasLogged());
                        try {
                            Launcheur.update();
                            if (Helpers.MC_DIR.toFile().exists()) {
                                Helpers.RAM_SELECTOR.save();
                                Helpers.SAVER.set("token", Launcheur.result.getRefreshToken());
                            }
                        } catch (Exception ex) {
                            System.out.println(ex.getMessage());
                        }
                        try {
                            Launcheur.launch();
                        } catch (Exception ex) {
                            System.out.println(ex.getMessage());
                        }
                    } catch (AuthenticationException | MicrosoftAuthenticationException ex) {
                        isLaunch = false;
                        JOUER.setTexture(TRANSPA);
                        JOptionPane.showMessageDialog(Main.frameInstance,
                                "Impossible de se connecter : Verifie si tu as un compte Minecraft lie Microsoft.", "Erreur",
                                JOptionPane.ERROR_MESSAGE);
                        System.out.println(ex.getMessage());
                    } catch (Exception ex) {
                        isLaunch = false;
                        JOUER.setTexture(TRANSPA);
                        JOptionPane.showMessageDialog(null, "Impossible de mettre a jour ton jeu ! : " + ex.getMessage(),
                                "Erreur !", JOptionPane.ERROR_MESSAGE);
                        System.out.println(ex.getMessage());
                    }
                });
                t.start();
            }
        }
    }

    public STexturedProgressBar getProgressBar() {
        return PROGRESS_BAR;
    }

    public void setMinMaxRam(int min, int max) {
        int y = 0;
        if (max - min == 9) {
            for (int i = min; i <= max; i++) {
                RamSelector.RAM_ARRAY[y] = i + "Go";
                y++;
            }
        } else
            System.out.println(
                    "Il faut une différence de 9 entre le max est le min, ici la différence est " + (max - min));
    }

    private boolean hasLogged() {
        return Helpers.SAVER.get("token") != null;
    }
}