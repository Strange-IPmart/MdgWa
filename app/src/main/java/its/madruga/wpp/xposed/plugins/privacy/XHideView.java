package its.madruga.wpp.xposed.plugins.privacy;

import static its.madruga.wpp.ClassesReference.HideView.classMessageReceipt;
import static its.madruga.wpp.ClassesReference.HideView.methodHideView;
import static its.madruga.wpp.ClassesReference.HideView.methodMessageReceipt;

import java.util.Collection;
import java.util.HashMap;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedHelpers;
import its.madruga.wpp.ClassesReference;
import its.madruga.wpp.xposed.models.XHookBase;

public class XHideView extends XHookBase {

    public XHideView(ClassLoader loader, XSharedPreferences preferences) {
        super(loader, preferences);
    }

    @Override
    public void doHook() {

        var hideread = prefs.getBoolean("hideread", false);
        var hidereadstatus = prefs.getBoolean("hidestatusview", false);

        if (!hideread && !hidereadstatus) return;

        if (hideread) {
            XposedHelpers.findAndHookMethod(classMessageReceipt, loader, methodHideView, Collection.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    param.setResult(new HashMap<>());
                    super.beforeHookedMethod(param);
                }
            });
            var mainClass = XposedHelpers.findClass(ClassesReference.HideReceipt.mainClass, loader);
            var param1Class = XposedHelpers.findClass(ClassesReference.HideReceipt.param1Class, loader);
            var param2Class = XposedHelpers.findClass("com.whatsapp.jid.DeviceJid", loader);
            var param3Class = XposedHelpers.findClass("com.whatsapp.jid.UserJid", loader);
            var param4Class = XposedHelpers.findClass(ClassesReference.HideReceipt.param4Class, loader);
            XposedHelpers.findAndHookMethod(mainClass, "A01", param1Class, param2Class, param3Class, param4Class, String.class, String[].class, int.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    var p4 = param.args[4];
                    if (p4 != null && p4.equals("read")) {
                        param.args[4] = null;
                        super.beforeHookedMethod(param);
                    }
                }
            });
        }
        if (hidereadstatus) {
            var classMessage = XposedHelpers.findClass(ClassesReference.HideView.classMessage, loader);
            XposedHelpers.findAndHookMethod(classMessageReceipt, loader, methodMessageReceipt, classMessage, new XC_MethodReplacement() {
                @Override
                protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                    return null;
                }
            });
        }



    }

}
