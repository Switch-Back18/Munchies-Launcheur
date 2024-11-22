package fr.switchback.launcheur.ui;

import fr.switchback.launcheur.Launcheur;
import fr.switchback.launcheur.Main;
import fr.switchback.launcheur.utils.Utils;
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
    private final BufferedImage IMAGE_TRANSPARENT;
    private final BufferedImage IMAGE_JOUER;
    private final BufferedImage IMAGE_LOAD;
    private final BufferedImage IMAGE_OPTION;
    private final BufferedImage IMAGE_QUIT;
    private final BufferedImage IMAGE_MINI;
    private final BufferedImage IMAGE_DISCORD;
    private final BufferedImage IMAGE_SITE;
    private final BufferedImage IMAGE_LAUNCHEUR;

    private STexturedProgressBar progressBar;

    private STexturedButton buttonDossier;
    private STexturedButton buttonJOUER;
    private STexturedButton buttonOPTION;
    private STexturedButton buttonQUIT;
    private STexturedButton buttonMINI;
    private STexturedButton buttonDISCORD;
    private STexturedButton buttonSITE;

    private boolean isLaunch = false;

    public LauncheurPanel() throws IOException {
        IMAGE_TRANSPARENT = ImageIO.read(Objects.requireNonNull(Main.class.getClassLoader().getResource("transpa.png")));
        IMAGE_JOUER = ImageIO.read(Objects.requireNonNull(Main.class.getClassLoader().getResource("bouton/jouer.png")));
        IMAGE_LOAD = ImageIO.read(Objects.requireNonNull(Main.class.getClassLoader().getResource("load.png")));
        IMAGE_OPTION = ImageIO.read(Objects.requireNonNull(Main.class.getClassLoader().getResource("bouton/option.png")));
        IMAGE_QUIT = ImageIO.read(Objects.requireNonNull(Main.class.getClassLoader().getResource("bouton/fermer.png")));
        IMAGE_MINI = ImageIO.read(Objects.requireNonNull(Main.class.getClassLoader().getResource("bouton/mini.png")));
        IMAGE_DISCORD = ImageIO.read(Objects.requireNonNull(Main.class.getClassLoader().getResource("bouton/discord.png")));
        IMAGE_SITE = ImageIO.read(Objects.requireNonNull(Main.class.getClassLoader().getResource("bouton/site.png")));
        IMAGE_LAUNCHEUR = ImageIO.read(Objects.requireNonNull(Main.class.getClassLoader().getResource("launcheur.png")));

        progressBar = new STexturedProgressBar(IMAGE_TRANSPARENT, IMAGE_LOAD);

        buttonDossier = new STexturedButton(IMAGE_TRANSPARENT);
        buttonJOUER = new STexturedButton(IMAGE_TRANSPARENT, IMAGE_JOUER);
        buttonOPTION = new STexturedButton(IMAGE_TRANSPARENT, IMAGE_OPTION);
        buttonQUIT = new STexturedButton(IMAGE_TRANSPARENT, IMAGE_QUIT);
        buttonMINI = new STexturedButton(IMAGE_TRANSPARENT, IMAGE_MINI);
        buttonDISCORD = new STexturedButton(IMAGE_TRANSPARENT, IMAGE_DISCORD);
        buttonSITE = new STexturedButton(IMAGE_TRANSPARENT, IMAGE_SITE);

        setLayout(null);

        progressBar.setBounds(409, 669, 181, 6);
        progressBar.setStringPainted(true);
        add(progressBar);

        buttonJOUER.setBounds(346, 556, 320, 114);
        buttonJOUER.setEnabled(false);
        buttonJOUER.addEventListener(this);
        add(buttonJOUER);

        buttonQUIT.setBounds(638, 358, 50, 50);
        buttonQUIT.addEventListener(this);
        add(buttonQUIT);

        buttonMINI.setBounds(682, 479, 50, 50);
        buttonMINI.addEventListener(this);
        add(buttonMINI);

        buttonOPTION.setBounds(660, 525, 50, 50);
        buttonOPTION.addEventListener(this);
        add(buttonOPTION);

        buttonDISCORD.setBounds(290, 570, 50, 50);
        buttonDISCORD.addEventListener(this);
        add(buttonDISCORD);

        buttonSITE.setBounds(257, 508, 50, 50);
        buttonSITE.addEventListener(this);
        add(buttonSITE);

        buttonDossier.setBounds(460, 285, 50, 50);
        buttonDossier.addEventListener(this);
        add(buttonDossier);
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.drawImage(IMAGE_LAUNCHEUR, 0, 0, this);
    }

    @Override
    public void onEvent(SwingerEvent e) {
        Desktop desktop = Desktop.getDesktop();
        if (e.getSource() == buttonOPTION) {
            Utils.RAM_SELECTOR.display();
        } else if (e.getSource() == buttonQUIT) {
            System.exit(0);
        } else if (e.getSource() == buttonMINI) {
            Main.frameInstance.setState(Frame.ICONIFIED);
        } else if (e.getSource() == buttonDossier) {
            try {
                Desktop.getDesktop().open(Utils.MC_DIR.toFile());
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        } else if (e.getSource() == buttonDISCORD) {
            try {
                URI oURL = new URI("https://discord.com/invite/erUg4NnADM");
                desktop.browse(oURL);
            } catch (URISyntaxException | IOException ex) {
                System.out.println(ex.getMessage());
            }
        } else if (e.getSource() == buttonSITE) {
            try {
                URI oURL = new URI("https://munchies.websr.fr");
                desktop.browse(oURL);
            } catch (URISyntaxException | IOException ex) {
                System.out.println(ex.getMessage());
            }
        } else if (e.getSource() == buttonJOUER) {
            if(!isLaunch) {
                isLaunch = true;
                buttonJOUER.setTexture(IMAGE_JOUER);
                Thread t = new Thread(() -> {
                    try {
                        Launcheur.auth(hasLogged());
                        try {
                            Launcheur.update();
                            if (Utils.MC_DIR.toFile().exists()) {
                                Utils.RAM_SELECTOR.save();
                            }
                            Launcheur.launch();
                        } catch (Exception ex) {
                            System.out.println(ex.getMessage());
                        }
                    } catch (Exception ex) {
                        isLaunch = false;
                        buttonJOUER.setTexture(IMAGE_TRANSPARENT);
                        JOptionPane.showMessageDialog(Main.frameInstance,
                                "Impossible de se connecter : Verifie si tu as un compte Minecraft / Microsoft.", "Erreur",
                                JOptionPane.ERROR_MESSAGE);
                        System.out.println(ex.getMessage());
                    }
                });
                t.start();
            }
        }
    }

    public STexturedProgressBar getProgressBar() {
        return progressBar;
    }

    public STexturedButton getButtonJouer() {
        return buttonJOUER;
    }

    private boolean hasLogged() {
        return Utils.MC_DIR.resolve("login.json").toFile().exists();
    }
}