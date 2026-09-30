package com.aalam.compiler;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.List;

/** External binary (aapt2, d8, ...) chalata hai aur uska output BuildContext ke log me daalta hai. */
class ProcessRunner {

    static void run(BuildContext ctx, List<String> cmd) throws Exception {
        ctx.log("$ " + String.join(" ", cmd));
        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.redirectErrorStream(true);

        // aapt2 jaise binary ki dependency .so files usi folder me hoti hain
        // jahan binary khud hai (jniLibs se aaya nativeLibraryDir). LD_LIBRARY_PATH
        // batata hai linker ko wahan bhi dekhne ke liye.
        if (ctx.aapt2 != null) {
            File libDir = ctx.aapt2.getParentFile();
            if (libDir != null) {
                pb.environment().put("LD_LIBRARY_PATH", libDir.getAbsolutePath());
            }
        }

        Process p = pb.start();
        try (BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
            String line;
            while ((line = r.readLine()) != null) ctx.log(line);
        }
        int code = p.waitFor();
        if (code != 0) {
            throw new Exception("Command fail (exit " + code + "): " + cmd.get(0));
        }
    }
}
