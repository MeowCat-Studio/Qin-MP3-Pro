package org.meowcat.mp3desktop.qin;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    BatteryManager mBatteryManager;
    AudioManager mAudioManager;
    WebView mWebView;
    long keyDownTimestamp = 0;

    public static void openApp(Context context, String packageName) {
        final Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        Objects.requireNonNull(intent).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        context.startActivity(intent);
    }

    public static void openApp(Context context, String packageName, String activityName) {
        Intent intent = new Intent().setComponent(new ComponentName(packageName, activityName));
        Objects.requireNonNull(intent).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        context.startActivity(intent);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBatteryManager = (BatteryManager) getSystemService(BATTERY_SERVICE);
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mWebView = findViewById(R.id.webView);
        mWebView.setBackgroundColor(0);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(new JavaScript(), "java");
        mWebView.getSettings().setDefaultTextEncodingName("utf-8");
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
                AlertDialog.Builder b = new AlertDialog.Builder(mWebView.getContext());
                String[] content = message.split(":",2);
                b.setTitle(content[0]);
                b.setMessage(content[1]);
                b.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        result.confirm();
                    }
                });
                b.setCancelable(false);
                b.create().show();
                return true;
            }
        });
        mWebView.loadUrl("file:///android_asset/meowcat/meowcat.html");
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (keyDownTimestamp == 0) {
                    keyDownTimestamp = System.currentTimeMillis();
                }
                return true;
            case KeyEvent.KEYCODE_VOLUME_UP:
                return true;
            default:
                return super.onKeyDown(keyCode, event);
        }
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_DOWN:
            case KeyEvent.KEYCODE_VOLUME_UP:
            case KeyEvent.KEYCODE_HOME:
            case KeyEvent.KEYCODE_APP_SWITCH:
            case KeyEvent.KEYCODE_BACK:
                return true;
            default:
                return super.onKeyUp(keyCode, event);
        }
    }

    public void finish() {}

    @SuppressWarnings("unused")
    public class JavaScript {
        @JavascriptInterface
        public void startApplication(final String packageName) {
            try {
                getPackageManager().getPackageInfo(packageName, PackageManager.GET_GIDS);
                openApp(getApplicationContext(), packageName);
            } catch (PackageManager.NameNotFoundException e) {
                Toast.makeText(MainActivity.this, "组件" + packageName + "缺失，无法启动。", Toast.LENGTH_SHORT).show();
            }
        }

        @JavascriptInterface
        public void startApplication(final String packageName, final String activityName) {
            try {
                getPackageManager().getPackageInfo(packageName, PackageManager.GET_GIDS);
                openApp(getApplicationContext(), packageName, activityName);
            } catch (PackageManager.NameNotFoundException e) {
                Toast.makeText(MainActivity.this, "组件" + packageName + "缺失，无法启动。", Toast.LENGTH_SHORT).show();
            }
        }

        @JavascriptInterface
        public String getStringInfo(final String info) {
            switch (info) {
                case "DeviceBrand":
                    return Build.BRAND;
                case "DeviceModel":
                    return Build.MODEL;
                case "DeviceSDK":
                    return "" + Build.VERSION.SDK_INT;
                case "Version":
                    return BuildConfig.VERSION_NAME;
                default:
                    return null;
            }
        }

        @JavascriptInterface
        public int getIntInfo(final String info) {
            switch (info) {
                case "BatteryNumber":
                    return mBatteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
                case "BatteryStatus":
                    return mBatteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_STATUS);
                case "HeadSet":
                    if (mAudioManager == null) {
                        return 0;
                    }
                    AudioDeviceInfo[] audioDevices = mAudioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS);
                    for (AudioDeviceInfo deviceInfo : audioDevices) {
                        if (deviceInfo.getType() == AudioDeviceInfo.TYPE_WIRED_HEADPHONES || deviceInfo.getType() == AudioDeviceInfo.TYPE_WIRED_HEADSET) {
                            return 1;
                        } else if (deviceInfo.getType() == AudioDeviceInfo.TYPE_USB_HEADSET) {
                            return 2;
                        }
                    }
                    return 0;
                default:
                    return 0;
            }
        }
    }
}
