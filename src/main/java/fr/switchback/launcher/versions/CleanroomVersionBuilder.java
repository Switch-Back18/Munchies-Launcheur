package fr.switchback.launcher.versions;

import fr.flowarg.flowupdater.utils.builderapi.BuilderArgument;
import fr.flowarg.flowupdater.utils.builderapi.BuilderException;
import fr.flowarg.flowupdater.versions.ModLoaderVersionBuilder;

public class CleanroomVersionBuilder extends ModLoaderVersionBuilder<CleanroomVersion, CleanroomVersionBuilder> {
    private final BuilderArgument<String> cleanroomVersionArgument = new BuilderArgument<String>("CleanroomVersion").required();

    public CleanroomVersionBuilder withCleanroomVersion(String cleanroomVersion)
    {
        this.cleanroomVersionArgument.set(cleanroomVersion);
        return this;
    }

    @Override
    public CleanroomVersion build() throws BuilderException
    {
        return new CleanroomVersion(
                this.cleanroomVersionArgument.get(),
                this.modsArgument.get(),
                this.curseModsArgument.get(),
                this.modrinthModsArgument.get(),
                this.fileDeleterArgument.get(),
                this.curseModPackArgument.get(),
                this.modrinthPackArgument.get()
        );
    }
}
