package fr.switchback.launcheur.ui;

import fr.switchback.launcheur.Main;
import fr.theshark34.swinger.util.WindowMover;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class LauncheurFrame extends JFrame {
    private final LauncheurPanel launcheurPanel;

    public LauncheurFrame() throws IOException {
        BufferedImage icon = ImageIO.read(Objects.requireNonNull(Main.class.getClassLoader().getResource("icon.png")));
        Color transpa = new Color(255, 255, 255, 0);

        setTitle("Munchies Launcher");
        setIconImage(icon);
        setSize(1000, 750);
        setUndecorated(true);
        setResizable(false);
        setLocationRelativeTo(null);
        setLayout(null);
        setBackground(transpa);

        WindowMover mover = new WindowMover(this);
        addMouseListener(mover);
        addMouseMotionListener(mover);

        launcheurPanel = new LauncheurPanel();
        launcheurPanel.setBounds(0, 0, 1000, 750);
        add(launcheurPanel);
    }

    public LauncheurPanel getLauncherPanel() {
        return launcheurPanel;
    }
}