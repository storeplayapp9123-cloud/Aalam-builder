package com.aalam.compiler;

import java.io.File;

/** Saare steps ke beech shared state: folders, tools, log, aur final APK. */
public class BuildContext {
    public final File projectDir;   // user ka project
    public final File outDir;       // output folder
    public final File workDir;      // temporary files (outDir/work)

    public File androidJar;         // android.jar (compile ke liye)
    public File aapt2;              // aapt2 binary (jniLibs se resolve hota hai)

    public File classesDir;         // ECJ ke .class files
    public File resFlat;            // aapt2 compile ka output (.zip)
    public File genDir;             // R.java yahan banta hai
    public File linkedApk;          // aapt2 link ka base APK (resources + manifest)
    public File apk;                // final signed APK, last step isko set karega

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
