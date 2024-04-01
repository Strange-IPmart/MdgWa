package its.madruga.wpp.xposed.plugins.functions;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import its.madruga.wpp.xposed.models.XHookBase;

public class XNewChat extends XHookBase {
    public XNewChat(@NonNull ClassLoader loader, @NonNull XSharedPreferences preferences) {
        super(loader, preferences);
    }

    @Override
    public void doHook() {
        var homeActivity = findClass("com.whatsapp.HomeActivity", loader);
        findAndHookMethod(homeActivity, "onCreateOptionsMenu", Menu.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                var home = (Activity) param.thisObject;
                var menu = (Menu) param.args[0];

                var item = menu.add(0, 0, 0, "New Chat");
                item.setOnMenuItemClickListener(item1 -> {
                    var view = new LinearLayout(home);
                    var edt = new EditText(view.getContext());
                    edt.setHint("number");

                    view.addView(edt);

                    new AlertDialog.Builder(home)
                            .setTitle("Hi")
                            .setMessage("Hello World")
                            .setView(view)
                            .setPositiveButton("Message", (dialog, which) -> {
                                var number = edt.getText().toString();
                                var numberFomatted = number.replaceAll("[+\\-()/\\s]", "");
                                Toast.makeText(home, numberFomatted, Toast.LENGTH_SHORT).show();
                                    var intent = new Intent(Intent.ACTION_VIEW);
                                    intent.setData(Uri.parse("https://wa.me/" + numberFomatted));
                                    home.startActivity(intent);
                            })
                            .setNegativeButton("Cancel", (dialog, which) -> {
                                Toast.makeText(home, "CANCEL", Toast.LENGTH_SHORT).show();
                            })
                            .setCancelable(false)
                            .create().show();
                    return true;
                });

                super.afterHookedMethod(param);
            }
        });
    }
}
