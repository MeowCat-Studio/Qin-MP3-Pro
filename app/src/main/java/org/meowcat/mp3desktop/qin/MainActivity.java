package org.meowcat.mp3desktop.qin;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;


public class MainActivity extends AppCompatActivity {

    BatteryManager mBatteryManager;
    WebView mWebView;
    boolean confirmFinish = false;

    public static void openApp(Context context, String packageName) {
        final Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        Objects.requireNonNull(intent).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        context.startActivity(intent);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBatteryManager = (BatteryManager) getSystemService(BATTERY_SERVICE);
        mWebView = findViewById(R.id.webView);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(new JavaScript(), "java");
        mWebView.loadUrl("file:///android_asset/meowcat/meowcat.html");
    }

    public void finish() {
        if (confirmFinish) {
            super.finish();
        }
    }

    @SuppressWarnings("unused")
    public class JavaScript {
        @JavascriptInterface
        public void startApplication(final String packageName) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    openApp(getApplicationContext(), packageName);
                }
            });
        }

        @JavascriptInterface
        public int batteryInfo() {
            return mBatteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        }

        @JavascriptInterface
        public int batteryState() {
            return mBatteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_STATUS);
        }

        @JavascriptInterface
        public void finish() {
            confirmFinish = true;
            MainActivity.this.finish();
        }
    }
}
