package fr.mathisskate.launcheur.utils;

import fr.flowarg.flowupdater.download.DownloadList;
import fr.flowarg.flowupdater.download.IProgressCallback;
import fr.mathisskate.launcheur.Main;

import java.text.DecimalFormat;

public class ProgressBarAPI implements IProgressCallback {

    @Override
    public void update(DownloadList.DownloadInfo info) {
        Main.frameInstance.getLauncherPanel().getProgressBar().setMaximum((int) info.getTotalToDownloadBytes());
        Main.frameInstance.getLauncherPanel().getProgressBar().setValue((int) info.getDownloadedBytes());
    }
}