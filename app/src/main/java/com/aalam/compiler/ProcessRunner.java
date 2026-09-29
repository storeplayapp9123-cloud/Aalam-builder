package com.aalam.compiler;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

/** External binary (aapt2, d8, ...) chalata hai aur uska output BuildContext ke log me daalta hai. */
class ProcessRunner {

    static void run(BuildContext ctx, List<String> cmd) throws Exception {
        ctx.log("$ " + String.join(" ", cmd));
        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.redirectErrorStream(true);
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
