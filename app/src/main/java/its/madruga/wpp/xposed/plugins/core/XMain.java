package its.madruga.wpp.xposed.plugins.core;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.Instrumentation;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Arrays;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import its.madruga.wpp.BuildConfig;
import its.madruga.wpp.xposed.Unobfuscator;
import its.madruga.wpp.xposed.UnobfuscatorCache;
import its.madruga.wpp.xposed.models.XHookBase;
import its.madruga.wpp.xposed.plugins.functions.XAntiEditMessage;
import its.madruga.wpp.xposed.plugins.functions.XAntiRevoke;
import its.madruga.wpp.xposed.plugins.functions.XBlueTick;
import its.madruga.wpp.xposed.plugins.functions.XCallPrivacy;
import its.madruga.wpp.xposed.plugins.functions.XChatLimit;
import its.madruga.wpp.xposed.plugins.functions.XDndMode;
import its.madruga.wpp.xposed.plugins.functions.XMediaQuality;
import its.madruga.wpp.xposed.plugins.functions.XNewChat;
import its.madruga.wpp.xposed.plugins.functions.XOthers;
import its.madruga.wpp.xposed.plugins.functions.XPinnedLimit;
import its.madruga.wpp.xposed.plugins.functions.XShareLimit;
import its.madruga.wpp.xposed.plugins.functions.XStatusDownload;
import its.madruga.wpp.xposed.plugins.functions.XViewOnce;
import its.madruga.wpp.xposed.plugins.personalization.XBioAndName;
import its.madruga.wpp.xposed.plugins.personalization.XBubbleColors;
import its.madruga.wpp.xposed.plugins.personalization.XChangeColors;
import its.madruga.wpp.xposed.plugins.personalization.XChatsFilter;
import its.madruga.wpp.xposed.plugins.personalization.XIGStatus;
import its.madruga.wpp.xposed.plugins.personalization.XSecondsToTime;
import its.madruga.wpp.xposed.plugins.personalization.XShowOnline;
import its.madruga.wpp.xposed.plugins.privacy.XFreezeLastSeen;
import its.madruga.wpp.xposed.plugins.privacy.XGhostMode;
import its.madruga.wpp.xposed.plugins.privacy.XHideArchive;
import its.madruga.wpp.xposed.plugins.privacy.XHideReceipt;
import its.madruga.wpp.xposed.plugins.privacy.XHideTag;
import its.madruga.wpp.xposed.plugins.privacy.XHideView;

public class XMain {
    public static Application mApp;

    private static final ArrayList<ErrorItem> list = new ArrayList<>();

    public static void Initialize(@NonNull ClassLoader loader, @NonNull XSharedPreferences pref, String sourceDir) {

        if (!Unobfuscator.initDexKit(sourceDir)) {
            XposedBridge.log("Can't init dexkit");
            return;
        }
        XposedHelpers.findAndHookMethod(Instrumentation.class, "callApplicationOnCreate", Application.class, new XC_MethodHook() {
            @SuppressWarnings("deprecation")
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                mApp = (Application) param.args[0];

                DesignUtils.mPrefs = pref;
                new UnobfuscatorCache(mApp,pref);
                XDatabases.Initialize(loader, pref);
                WppCore.Initialize(loader);

                PackageManager packageManager = mApp.getPackageManager();
                pref.registerOnSharedPreferenceChangeListener((sharedPreferences, s) -> pref.reload());
                PackageInfo packageInfo = packageManager.getPackageInfo(mApp.getPackageName(), 0);
                XposedBridge.log(packageInfo.versionName);
                plugins(loader, pref,packageInfo.versionName);
                registerReceivers();
//                    XposedHelpers.setStaticIntField(XposedHelpers.findClass("com.whatsapp.util.Log", loader), "level", 5);
            }
        });

        XposedHelpers.findAndHookMethod("com.whatsapp.HomeActivity", loader, "onCreate", Bundle.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                if (!list.isEmpty()) {
                    new AlertDialog.Builder((Activity) param.thisObject)
                            .setTitle("Error detected")
                            .setMessage("The following options aren't working:\n\n" + String.join("\n", list.stream().map(ErrorItem::getPluginName).toArray(String[]::new)))
                            .setPositiveButton("Copy to clipboard", (dialog, which) -> {
                                var clipboard = (ClipboardManager) mApp.getSystemService(Context.CLIPBOARD_SERVICE);
                                ClipData clip = ClipData.newPlainText("text",String.join("\n", list.stream().map(ErrorItem::toString).toArray(String[]::new)));
                                clipboard.setPrimaryClip(clip);
                                Toast.makeText(mApp, "Copied to clipboard", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            })
                            .show();
                }
            }
        });
    }

    private static void registerReceivers() {
        BroadcastReceiver restartReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Toast.makeText(context, "Rebooting " + context.getPackageManager().getApplicationLabel(context.getApplicationInfo()) + "...", Toast.LENGTH_SHORT).show();
                new Handler(Looper.getMainLooper()).postDelayed(() -> Utils.doRestart(context), 100);
            }
        };
        var intentRestart = new IntentFilter(BuildConfig.APPLICATION_ID + ".WHATSAPP.RESTART");
        ContextCompat.registerReceiver(mApp, restartReceiver, intentRestart, ContextCompat.RECEIVER_EXPORTED);
    }

    private static void plugins(@NonNull ClassLoader loader, @NonNull XSharedPreferences pref,@NonNull String versionWpp) {

        var classes = new Class<?>[]{
                XAntiEditMessage.class,
                XAntiRevoke.class,
                XBioAndName.class,
                XBlueTick.class,
                XBubbleColors.class,
                XCallPrivacy.class,
                XChangeColors.class,
                XChatLimit.class,
                XChatsFilter.class,
                XShowOnline.class,
                XDndMode.class,
                XFreezeLastSeen.class,
                XGhostMode.class,
                XHideArchive.class,
                XHideReceipt.class,
                XHideTag.class,
                XHideView.class,
                XIGStatus.class,
                XMediaQuality.class,
                XNewChat.class,
                XOthers.class,
                XPinnedLimit.class,
                XSecondsToTime.class,
                XShareLimit.class,
                XStatusDownload.class,
                XViewOnce.class,
        };

        for (var classe : classes) {
            try {
                var constructor = classe.getConstructor(ClassLoader.class, XSharedPreferences.class);
                var plugin = (XHookBase) constructor.newInstance(loader, pref);
                plugin.doHook();
            } catch (Throwable e) {
                XposedBridge.log(e);
                var error = new ErrorItem();
                error.setPluginName(classe.getSimpleName());
                error.setWhatsAppVersion(versionWpp);
                error.setError(e.getMessage()+": "+ Arrays.toString(Arrays.stream(e.getStackTrace()).filter(s -> !s.getClassName().startsWith("android") && !s.getClassName().startsWith("com.android")).map(StackTraceElement::toString).toArray()));
                list.add(error);
            }
        }
    }


    private static class ErrorItem {
        private String pluginName;
        private String whatsAppVersion;
        private String error;

        @NonNull
        @Override
        public String toString() {
            return  "pluginName='" + getPluginName() + '\'' +
                    "\nwhatsAppVersion='" + getWhatsAppVersion() + '\'' +
                    "\nerror='" + getError() + '\'';
        }

        public String getWhatsAppVersion() {
            return whatsAppVersion;
        }

        public void setWhatsAppVersion(String whatsAppVersion) {
            this.whatsAppVersion = whatsAppVersion;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }

        public String getPluginName() {
            return pluginName;
        }

        public void setPluginName(String pluginName) {
            this.pluginName = pluginName;
        }
    }
}
