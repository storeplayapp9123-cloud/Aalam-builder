package com.aalam.compiler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/** aapt2 link: compiled resources + manifest ko jodkar base APK (resources.arsc) aur R.java banata hai. */
public class LinkResourcesStep implements BuildStep {

    @Override
    public String name() {
        return "Linking resources (AAPT2)";
    }

    @Override
    public void run(BuildContext ctx) throws Exception {
        if (ctx.androidJar == null || !ctx.androidJar.isFile()) {
            throw new Exception("android.jar nahi mila: " + ctx.androidJar);
        }
        File manifest = new File(ctx.projectDir, "AndroidManifest.xml");
        if (!manifest.isFile()) {
            throw new Exception("AndroidManifest.xml nahi mila: " + manifest);
        }
        File gen = new File(ctx.workDir, "gen");
        if (!gen.isDirectory() && !gen.mkdirs()) {
            throw new Exception("gen folder nahi bana: " + gen);
        }
        File baseApk = new File(ctx.workDir, "base.apk");

        List<String> cmd = new ArrayList<>();
        cmd.add(ctx.aapt2.getAbsolutePath());
        cmd.add("link");
        cmd.add("-I");
        cmd.add(ctx.androidJar.getAbsolutePath());
        cmd.add("--manifest");
        cmd.add(manifest.getAbsolutePath());
        cmd.add("-R");
        cmd.add(ctx.resFlat.getAbsolutePath());
        cmd.add("--java");
        cmd.add(gen.getAbsolutePath());
        cmd.add("--min-sdk-version");
        cmd.add("26");
        cmd.add("--target-sdk-version");
        cmd.add("34");
        cmd.add("-o");
        cmd.add(baseApk.getAbsolutePath());

        ProcessRunner.run(ctx, cmd);
        if (!baseApk.isFile()) throw new Exception("Link fail: " + baseApk + " nahi bana");

        ctx.genDir = gen;
        ctx.linkedApk = baseApk;
        ctx.log("Linked: " + baseApk.getName());
    }
}
