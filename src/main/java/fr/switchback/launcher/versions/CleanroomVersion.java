package fr.switchback.launcher.versions;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fr.flowarg.flowupdater.download.json.*;
import fr.flowarg.flowupdater.utils.IOUtils;
import fr.flowarg.flowupdater.utils.ModFileDeleter;
import fr.flowarg.flowupdater.versions.AbstractModLoaderVersion;
import fr.flowarg.flowupdater.versions.ModLoaderUtils;
import fr.flowarg.flowupdater.versions.ParsedLibrary;
import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.net.URL;
import java.nio.file.*;
import java.util.HashMap;
import java.util.List;

public class CleanroomVersion extends AbstractModLoaderVersion {
    private final String versionId;

    public CleanroomVersion(String modLoaderVersion, List<Mod> mods, List<CurseFileInfo> curseMods,
                        List<ModrinthVersionInfo> modrinthMods, ModFileDeleter fileDeleter, CurseModPackInfo curseModPackInfo,
                        ModrinthModPackInfo modrinthModPackInfo)
    {
        super(modLoaderVersion, mods, curseMods, modrinthMods, fileDeleter, curseModPackInfo, modrinthModPackInfo, null);
        this.versionId = "cleanroom-" + modLoaderVersion;
    }

    @Override
    public boolean isModLoaderAlreadyInstalled(@NotNull Path installDir)
    {
        final Path versionJsonFile = installDir.resolve(this.versionId + ".json");

        if(Files.notExists(versionJsonFile))
            return false;

        try {
            final JsonObject object = JsonParser.parseReader(Files.newBufferedReader(versionJsonFile))
                    .getAsJsonObject();
            {
                final boolean firstPass = ModLoaderUtils.parseNewVersionInfo(installDir, object).stream().allMatch(ParsedLibrary::isInstalled);
                return firstPass;
            }
        }
        catch (Exception e)
        {
            this.logger.err("An error occurred while checking if the mod loader is already installed.");
            return false;
        }
    }

    @Override
    public void install(@NotNull Path installDir) throws Exception
    {
        super.install(installDir);

        final String installerUrl = String.format("https://repo.cleanroommc.com/releases/com/cleanroommc/cleanroom/%s/cleanroom-%s-installer.jar",
                this.modLoaderVersion, this.modLoaderVersion);
        final String[] installerUrlParts = installerUrl.split("/");
        final Path installerFile = installDir.resolve(installerUrlParts[installerUrlParts.length - 1]);
        IOUtils.download(
                this.logger,
                new URL(installerUrl),
                installerFile
        );

        this.logger.info("Installing libraries...");
        final URI uri = URI.create("jar:" + installerFile.toAbsolutePath().toUri());
        try (final FileSystem zipFs = FileSystems.newFileSystem(uri, new HashMap<>()))
        {
            final Path versionFile = zipFs.getPath("version.json");
            final Path versionJsonFile = installDir.resolve(this.versionId + ".json");
            Files.copy(versionFile, versionJsonFile, StandardCopyOption.REPLACE_EXISTING);

            ModLoaderUtils.parseNewVersionInfo(installDir, JsonParser.parseReader(Files.newBufferedReader(versionFile)).getAsJsonObject())
                    .stream()
                    .filter(parsedLibrary -> !parsedLibrary.isInstalled())
                    .forEach(parsedLibrary -> {
                        if(parsedLibrary.getUrl().isPresent())
                            parsedLibrary.download(this.logger);
                        else
                        {
                            try
                            {
                                final String[] name = parsedLibrary.getArtifact().split(":");
                                final String group = name[0].replace('.', '/');
                                final String artifact = name[1];
                                final boolean hasExtension = name[2].contains("@");
                                final String version = name[2].contains("@") ? name[2].split("@")[0] : name[2];
                                final String extension = hasExtension ? name[2].split("@")[1] : "jar";
                                String classifier = "";
                                if(name.length == 4)
                                    classifier = "-" + name[3];
                                Files.createDirectories(parsedLibrary.getPath().getParent());
                                Files.copy(zipFs.getPath("maven/" + group + '/' + artifact + '/' + version + '/' + artifact + "-" + version + classifier + "." + extension), parsedLibrary.getPath(), StandardCopyOption.REPLACE_EXISTING);
                            } catch (Exception e)
                            {
                                this.logger.printStackTrace(e);
                            }
                        }
                    });
        } catch (Exception e) {
            this.logger.printStackTrace(e);
        }
        Files.deleteIfExists(installerFile);
    }

    @Override
    public String name()
    {
        return "Cleanroom";
    }
}
