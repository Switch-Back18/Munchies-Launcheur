package fr.switchback.launcher.utils;

import fr.flowarg.flowupdater.download.DownloadList;
import fr.flowarg.flowupdater.download.IProgressCallback;
import fr.switchback.launcher.Main;

public class ProgressBarAPI implements IProgressCallback {

    @Override
    public void update(DownloadList.DownloadInfo info) {
        Main.frameInstance.getLauncherPanel().getProgressBar().setMaximum((int) info.getTotalToDownloadBytes());
        Main.frameInstance.getLauncherPanel().getProgressBar().setValue((int) info.getDownloadedBytes());
    }
}