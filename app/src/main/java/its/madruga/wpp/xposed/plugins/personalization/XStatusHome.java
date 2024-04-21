package its.madruga.wpp.xposed.plugins.personalization;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import java.util.ArrayList;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import its.madruga.wpp.adapters.IGStatusAdapter;
import its.madruga.wpp.views.IGStatusView;
import its.madruga.wpp.xposed.models.XHookBase;
import its.madruga.wpp.xposed.plugins.core.ResId;
import its.madruga.wpp.xposed.plugins.core.Utils;

public class XStatusHome extends XHookBase {
    public static ArrayList itens = new ArrayList<>();
    private IGStatusView igStatusView;

    public XStatusHome(@NonNull ClassLoader loader, @NonNull XSharedPreferences preferences) {
        super(loader, preferences);
    }

    @Override
    public void doHook() throws Throwable {

        var clazz = XposedHelpers.findClass("com.whatsapp.HomeActivity",loader).getSuperclass();
        XposedHelpers.findAndHookMethod(clazz, "onCreate", android.os.Bundle.class, new XC_MethodHook() {
            @Override
            @SuppressLint("DiscouragedApi")
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                var activity = (Activity) param.thisObject;
                var mainContainer = activity.findViewById(Utils.getID("main_container","id"));
                var pagerView = (ViewGroup)mainContainer.findViewById(Utils.getID("pager","id"));
                ((FrameLayout.LayoutParams)pagerView.getLayoutParams()).topMargin = Utils.dipToPixels(76);
                var toolbar = (ViewGroup)mainContainer.findViewById(Utils.getID("toolbar","id"));
                var layoutView = (LinearLayout)toolbar.getParent();
                var contentView = (FrameLayout)layoutView.getParent();
                // Cria o frame
                var frameLayout = new FrameLayout(activity);
                frameLayout.setId(ResId.id.mStatusContainer);
                var layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, Utils.dipToPixels(96));
                layoutParams.gravity = Gravity.TOP;
                frameLayout.setLayoutParams(layoutParams);
                igStatusView = new IGStatusView(activity);
                var layoutParams2 = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                layoutParams2.bottomMargin = 0;
                igStatusView.setLayoutParams(layoutParams2);
                frameLayout.addView(igStatusView);
                layoutView.addView(frameLayout,2);
            }
        });
        var clazz2 = XposedHelpers.findClass("com.whatsapp.updates.viewmodels.UpdatesViewModel",loader);
        XposedBridge.hookAllConstructors(clazz2, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                var adapter = new IGStatusAdapter(Utils.getApplication(),0);
                itens.add("test");
                itens.add("test");
                itens.add("test");
                itens.add("test");
                itens.add("test");
                itens.add("test");
                itens.add("test");
                itens.add("test");
                itens.add("test");
                itens.add("test");
                igStatusView.statusRc.setAdapter(adapter);
            }
        });
    }

    @NonNull
    @Override
    public String getPluginName() {
        return "Status on Home";
    }
}
