package com.aalam.compiler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Aalam temporary compiler: steps ko order me chalata hai.
 * Isko background thread par chalana (UI thread par nahi).
 */
public class BuildPipeline {

    /** UI is listener se progress aur log dikhayega. */
    public interface Listener {
        void onStepStart(int index, int total, String name);

        void onStepDone(int index, int total, String name);

        void onLog(String message);

        void onFinished(boolean success, File apk, String error);
    }

    private final List<BuildStep> steps = new ArrayList<>();

    public BuildPipeline() {
        steps.add(new PrepareStep());
        // Abhi ECJ 2nd hai taaki test ho sake. AAPT2 (R.java) aane par ECJ resources ke baad jayega.
        steps.add(new CompileJavaStep());
        steps.add(new PendingStep("Compiling resources (AAPT2)"));
        steps.add(new PendingStep("Linking resources (AAPT2)"));
        steps.add(new PendingStep("Dexing (D8)"));
        steps.add(new PendingStep("Packaging APK"));
        steps.add(new PendingStep("Aligning and signing"));
    }

    public List<String> stepNames() {
        List<String> names = new ArrayList<>();
        for (BuildStep s : steps) names.add(s.name());
        return names;
    }

    public void run(File projectDir, File outDir, File androidJar, Listener listener) {
        BuildContext ctx = new BuildContext(projectDir, outDir, listener);
        ctx.androidJar = androidJar;
        int total = steps.size();

        try {
            for (int i = 0; i < total; i++) {
                BuildStep step = steps.get(i);
                listener.onStepStart(i + 1, total, step.name());
                step.run(ctx);
                listener.onStepDone(i + 1, total, step.name());
            }
            listener.onFinished(true, ctx.apk, null);
        } catch (Exception e) {
            listener.onFinished(false, null, e.getMessage());
        }
    }

    /** Jo step abhi bana nahi, wo saaf error dega taaki jhoothi success na dikhe. */
    private static class PendingStep implements BuildStep {
        private final String name;

        PendingStep(String name) {
            this.name = name;
        }

        @Override
        public String name() {
            return name;
        }

        @Override
        public void run(BuildContext ctx) throws Exception {
            throw new Exception("Step abhi implement nahi hua: " + name);
        }
    }
}
