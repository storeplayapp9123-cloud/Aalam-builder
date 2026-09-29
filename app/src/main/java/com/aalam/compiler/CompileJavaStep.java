package com.aalam.compiler;

import org.eclipse.jdt.internal.compiler.batch.Main;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

/** ECJ se project/src ke .java files ko .class me compile karta hai. */
public class CompileJavaStep implements BuildStep {

    @Override
    public String name() {
        return "Compiling Java (ECJ)";
    }

    @Override
    public void run(BuildContext ctx) throws Exception {
        if (ctx.androidJar == null || !ctx.androidJar.isFile()) {
            throw new Exception("android.jar nahi mila: " + ctx.androidJar);
        }

        File src = new File(ctx.projectDir, "src");
        File classes = new File(ctx.workDir, "classes");
        if (!classes.isDirectory() && !classes.mkdirs()) {
            throw new Exception("classes folder nahi ban paya: " + classes);
        }
        ctx.classesDir = classes;

        List<String> args = new ArrayList<>();
        args.add("-source");
        args.add("1.8");
        args.add("-target");
        args.add("1.8");
        args.add("-encoding");
        args.add("UTF-8");
        args.add("-proc:none");
        args.add("-nowarn");
        args.add("-bootclasspath");
        args.add(ctx.androidJar.getAbsolutePath());
        args.add("-d");
        args.add(classes.getAbsolutePath());
        args.add(src.getAbsolutePath());

        StringWriter out = new StringWriter();
        StringWriter err = new StringWriter();
        boolean ok;
        try {
            try (PrintWriter o = new PrintWriter(out); PrintWriter e = new PrintWriter(err)) {
                ok = new Main(o, e, false).compile(args.toArray(new String[0]));
            }
        } catch (LinkageError le) {
            throw new Exception("ECJ Android par chal nahi paya: " + le);
        }

        logLines(ctx, out.toString());
        logLines(ctx, err.toString());

        if (!ok) {
            throw new Exception("Java compile fail hua (upar log dekho)");
        }
        ctx.log("Compiled " + countClasses(classes) + " class file(s)");
    }

    private static void logLines(BuildContext ctx, String text) {
        if (text == null) return;
        for (String line : text.split("\n")) {
            if (!line.trim().isEmpty()) ctx.log(line);
        }
    }

    private static int countClasses(File dir) {
        int n = 0;
        File[] children = dir.listFiles();
        if (children == null) return 0;
        for (File c : children) {
            if (c.isDirectory()) n += countClasses(c);
            else if (c.getName().endsWith(".class")) n++;
        }
        return n;
    }
}
