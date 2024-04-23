package its.madruga.wpp.views;

import android.content.Context;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import its.madruga.wpp.adapters.IGStatusAdapter;
import its.madruga.wpp.xposed.plugins.core.Utils;

public class IGStatusView extends FrameLayout {
    public HorizontalListView mStatusListView;

    public IGStatusAdapter mStatusAdapter;
    private FrameLayout mStatusFrag;

    public IGStatusView(@NonNull Context context) {
        super(context);
        init(context);
    }


    private void init(Context context) {
        mStatusListView = new HorizontalListView(context);
        var layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(Utils.dipToPixels(4), Utils.dipToPixels(10), 0, 0);
        mStatusListView.setLayoutParams(layoutParams);
        mStatusFrag = new FrameLayout(context);
        mStatusFrag.setLayoutParams(new LayoutParams(0, 0));
        addView(mStatusListView);
        addView(mStatusFrag);
    }

    @Override
    public void setTranslationY(float f) {
        if(this.getHeight() > 0) {
            int v = f > ((float)this.getHeight()) ? GONE : VISIBLE;
            if(v == VISIBLE) {
                super.setTranslationY(f);
            }
            this.setVisibility(v);
        }
    }


    public void updateList() {
        post(() -> {
            mStatusAdapter.notifyDataSetChanged();
            this.invalidate();
        });
    }

    public void setAdapter(IGStatusAdapter mStatusAdapter) {
        this.mStatusAdapter = mStatusAdapter;
        mStatusListView.setAdapter(mStatusAdapter);
    }
}
