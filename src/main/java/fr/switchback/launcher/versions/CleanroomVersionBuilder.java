package fr.switchback.launcher.versions;

import fr.flowarg.flowupdater.utils.builderapi.BuilderArgument;
import fr.flowarg.flowupdater.utils.builderapi.BuilderException;
import fr.flowarg.flowupdater.versions.ModLoaderVersionBuilder;

public class CleanroomVersionBuilder extends ModLoaderVersionBuilder<CleanroomVersion, CleanroomVersionBuilder> {
    private final BuilderArgument<String> forgeVersionArgument = new BuilderArgument<String>("ForgeVersion").required();

    public CleanroomVersionBuilder withCleanroomVersion(String forgeVersion)
    {
        this.forgeVersionArgument.set(forgeVersion);
        return this;
    }

    @Override
    public CleanroomVersion build() throws BuilderException
    {
        return new CleanroomVersion(
                this.forgeVersionArgument.get(),
                this.modsArgument.get(),
                this.curseModsArgument.get(),
                this.modrinthModsArgument.get(),
                this.fileDeleterArgument.get(),
                this.curseModPackArgument.get(),
                this.modrinthPackArgument.get()
        );
    }
}
