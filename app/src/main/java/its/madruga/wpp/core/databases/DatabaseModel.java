package its.madruga.wpp.core.databases;

import static de.robv.android.xposed.XposedHelpers.findClass;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;


public class DatabaseModel {
    public SQLiteOpenHelper database;
    public SQLiteDatabase writetableDb;

    public DatabaseModel(String dbName, ClassLoader loader) {
        get(dbName, loader);
    }

    public void get(String db, ClassLoader loader) {
        XposedBridge.hookAllConstructors(findClass(db, loader), new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                database = (SQLiteOpenHelper) param.thisObject;
                writetableDb = database.getWritableDatabase();
                super.afterHookedMethod(param);
            }
        });
    }
}
