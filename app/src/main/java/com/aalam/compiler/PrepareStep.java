package com.aalam.compiler;

import java.io.File;

/**
 * Step 1: project check karna aur work folder saaf banana.
 *
 * Project structure (abhi ke liye simple):
 *   project/AndroidManifest.xml
 *   project/res/        (optional)
 *   project/src/        (Java files)
 */
public class PrepareStep implements BuildStep {

    @Override
    public String name() {
        return "Preparing";
    }

    @Override
    public void run(BuildContext ctx) throws Exception {
        if (ctx.projectDir == null || !ctx.projectDir.isDirectory()) {
            throw new Exception("Project folder nahi mila: " + ctx.projectDir);
        }

        File manifest = new File(ctx.projectDir, "AndroidManifest.xml");
        if (!manifest.isFile()) {
            throw new Exception("AndroidManifest.xml nahi mila: " + manifest);
        }

        File src = new File(ctx.projectDir, "src");
        if (!src.isDirectory()) {
            throw new Exception("src folder nahi mila: " + src);
        }

        // Purana work folder saaf karke naya banao
        deleteRecursive(ctx.workDir);
        if (!ctx.workDir.mkdirs()) {
            throw new Exception("Work folder nahi ban paya: " + ctx.workDir);
        }

        ctx.log("Project OK: " + ctx.projectDir.getName());
        ctx.log("Work folder ready: " + ctx.workDir.getPath());
    }

    private static void deleteRecursive(File f) {
        if (f == null || !f.exists()) return;
        File[] children = f.listFiles();
        if (children != null) {
            for (File c : children) deleteRecursive(c);
        }
        f.delete();
    }
}
