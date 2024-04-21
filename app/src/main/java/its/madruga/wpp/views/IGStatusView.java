package its.madruga.wpp.views;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import its.madruga.wpp.xposed.plugins.core.Utils;

public class IGStatusView extends FrameLayout {
    public HorizontalListView statusRc;
    private FrameLayout sFrag;

    public IGStatusView(@NonNull Context context) {
        super(context);
        init(context);
    }


    private void init(Context context) {
        statusRc = new HorizontalListView(context);
        var layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(Utils.dipToPixels(4), Utils.dipToPixels(10), 0, 0);
        statusRc.setLayoutParams(layoutParams);
        sFrag = new FrameLayout(context);
        sFrag.setLayoutParams(new LayoutParams(0, 0));
        addView(statusRc);
        addView(sFrag);
    }


}
