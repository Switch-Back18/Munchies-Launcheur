package fr.switchback.launcher.ui;

import fr.switchback.launcher.Main;
import fr.theshark34.swinger.util.WindowMover;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class LauncherFrame extends JFrame {
    private final LauncherPanel LAUNCHER_PANEL;

    public LauncherFrame() throws IOException {
        setTitle("Munchies Launcher");
        setSize(1000, 750);
        setUndecorated(true);
        setResizable(false);
        BufferedImage icon = ImageIO.read(Objects.requireNonNull(Main.class.getClassLoader().getResource("icon.png")));
        setIconImage(icon);
        Color transparent = new Color(255, 255, 255, 0);
        setBackground(transparent);
        setLocationRelativeTo(null);
        setLayout(null);
        WindowMover mover = new WindowMover(this);
        addMouseListener(mover);
        addMouseMotionListener(mover);
        LAUNCHER_PANEL = new LauncherPanel();
        LAUNCHER_PANEL.setBounds(0, 0, 1000, 750);
        add(LAUNCHER_PANEL);
    }

    public LauncherPanel getLauncherPanel() {
        return LAUNCHER_PANEL;
    }
}