package com.aalam.compiler;

import java.io.File;

/** Saare steps ke beech shared state: folders, log, aur final APK. */
public class BuildContext {
    public final File projectDir;   // user ka project
    public final File outDir;       // output folder
    public final File workDir;      // temporary files (outDir/work)
    public File apk;                // last step isko set karega

    private final BuildPipeline.Listener listener;

    public BuildContext(File projectDir, File outDir, BuildPipeline.Listener listener) {
        this.projectDir = projectDir;
        this.outDir = outDir;
        this.workDir = new File(outDir, "work");
        this.listener = listener;
    }

    public void log(String message) {
        listener.onLog(message);
    }
}
