package com.aalam.compiler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/** aapt2 compile: res/ ke andar sab files ko .flat me compile karke ek zip banata hai. */
public class CompileResourcesStep implements BuildStep {

    @Override
    public String name() {
        return "Compiling resources (AAPT2)";
    }

    @Override
    public void run(BuildContext ctx) throws Exception {
        if (ctx.aapt2 == null || !ctx.aapt2.isFile()) {
            throw new Exception("aapt2 binary nahi mila: " + ctx.aapt2);
        }
        File res = new File(ctx.projectDir, "res");
        if (!res.isDirectory()) {
            throw new Exception("res folder nahi mila: " + res);
        }
        File out = new File(ctx.workDir, "res-compiled.zip");

        List<String> cmd = new ArrayList<>();
        cmd.add(ctx.aapt2.getAbsolutePath());
        cmd.add("compile");
        cmd.add("--dir");
        cmd.add(res.getAbsolutePath());
        cmd.add("-o");
        cmd.add(out.getAbsolutePath());

        ProcessRunner.run(ctx, cmd);
        if (!out.isFile()) throw new Exception("Resources compile fail: " + out + " nahi bana");

        ctx.resFlat = out;
        ctx.log("Resources compiled: " + out.getName());
    }
}
