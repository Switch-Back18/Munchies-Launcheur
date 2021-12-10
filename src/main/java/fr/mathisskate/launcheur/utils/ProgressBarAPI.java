package fr.mathisskate.launcheur.utils;

import fr.flowarg.flowupdater.download.IProgressCallback;
import fr.mathisskate.launcheur.Main;

import java.text.DecimalFormat;

public class ProgressBarAPI implements IProgressCallback {

    private final DecimalFormat decimalFormat = new DecimalFormat("#.#");

    @Override
    public void update(long downloaded, long max) {
        Main.frameInstance.getLauncherPanel().getProgressBar().setMaximum((int) max);
        Main.frameInstance.getLauncherPanel().getProgressBar().setValue((int) (downloaded));
    }
}
