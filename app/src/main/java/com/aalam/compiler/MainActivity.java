package com.aalam.compiler;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;

/** Test screen: ek sample project banake pipeline chalata hai aur log dikhata hai. */
public class MainActivity extends Activity {

    private TextView logView;
    private ScrollView scroll;
    private Button buildBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(32, 48, 32, 32);

        buildBtn = new Button(this);
        buildBtn.setText("Build sample project");

        logView = new TextView(this);
        logView.setTypeface(Typeface.MONOSPACE);
        logView.setTextSize(13);

        scroll = new ScrollView(this);
        scroll.addView(logView);

        root.addView(buildBtn);
        root.addView(scroll, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));
        setContentView(root);

        buildBtn.setOnClickListener(v -> startBuild());
    }

    private void startBuild() {
        buildBtn.setEnabled(false);
        logView.setText("");

        new Thread(() -> {
            try {
                File project = new File(getFilesDir(), "sample");
                new File(project, "src").mkdirs();
                try (FileOutputStream out = new FileOutputStream(new File(project, "AndroidManifest.xml"))) {
                    out.write("<manifest package=\"com.sample.app\"/>".getBytes("UTF-8"));
                }
                File outDir = new File(getFilesDir(), "out");

                new BuildPipeline().run(project, outDir, new BuildPipeline.Listener() {
                    @Override
                    public void onStepStart(int i, int total, String name) {
                        append("[" + i + "/" + total + "] " + name + " ...");
                    }

                    @Override
                    public void onStepDone(int i, int total, String name) {
                        append("    done");
                    }

                    @Override
                    public void onLog(String message) {
                        append("    " + message);
                    }

                    @Override
                    public void onFinished(boolean success, File apk, String error) {
                        append(success ? "\nBUILD SUCCESS: " + apk : "\nBUILD FAILED: " + error);
                        runOnUiThread(() -> buildBtn.setEnabled(true));
                    }
                });
            } catch (Exception e) {
                append("ERROR: " + e);
                runOnUiThread(() -> buildBtn.setEnabled(true));
            }
        }).start();
    }

    private void append(String line) {
        runOnUiThread(() -> {
            logView.append(line + "\n");
            scroll.post(() -> scroll.fullScroll(ScrollView.FOCUS_DOWN));
        });
    }
}
