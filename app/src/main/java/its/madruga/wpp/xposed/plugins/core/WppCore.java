package its.madruga.wpp.xposed.plugins.core;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import its.madruga.wpp.xposed.Unobfuscator;

public class WppCore {

    private static Object mainActivity;
    private static Field contactManagerField;
    private static Method getContactMethod;
    private static Class<?> mGenJidClass;
    private static Method mGenJidMethod;

    public static void Initialize(ClassLoader loader) throws Throwable {

        // init Main activity
        XposedBridge.hookAllMethods(XposedHelpers.findClass("com.whatsapp.HomeActivity", loader), "onCreate", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                mainActivity = param.thisObject;
            }
        });
        XposedBridge.hookAllMethods(XposedHelpers.findClass("com.whatsapp.HomeActivity", loader), "onResume", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                mainActivity = param.thisObject;
            }
        });

        // init ContactManager
        contactManagerField = Unobfuscator.loadContactManagerField(loader);
        getContactMethod = Unobfuscator.loadGetContactInfoMethod(loader);

        // init UserJID
        var mSendReadClass = XposedHelpers.findClass("com.whatsapp.jobqueue.job.SendReadReceiptJob", loader);
        var subClass = Arrays.stream(mSendReadClass.getConstructors()).filter(c -> c.getParameterTypes().length == 8).findFirst().orElse(null).getParameterTypes()[0];
        mGenJidClass = Arrays.stream(subClass.getFields()).filter(field -> Modifier.isStatic(field.getModifiers())).findFirst().orElse(null).getType();
        mGenJidMethod = Arrays.stream(mGenJidClass.getMethods()).filter(m -> m.getParameterCount() == 1 && !Modifier.isStatic(m.getModifiers())).findFirst().orElse(null);
    }

    public static Object getContactManager() {
        if (mainActivity == null) return null;
        try {
            contactManagerField.setAccessible(true);
            return contactManagerField.get(mainActivity);
        } catch (Exception e) {
            XposedBridge.log(e);
        }
        return null;
    }

    public static String getContactName(String rawJid) {
        try {
            var contact = getContactMethod.invoke(getContactManager(), createUserJid(rawJid));
            var stringField = Arrays.stream(contact.getClass().getDeclaredFields()).filter(f -> f.getType().equals(String.class)).toArray(Field[]::new);
            return (String) stringField[3].get(contact);
        } catch (Exception e) {
            XposedBridge.log(e);
        }
        return null;
    }

    public static Object createUserJid(String rawjid) {
        var genInstance = XposedHelpers.newInstance(mGenJidClass);
        try {
            return mGenJidMethod.invoke(genInstance, rawjid);
        } catch (Exception e) {
            XposedBridge.log(e);
        }
        return null;
    }


}
