package its.madruga.wpp.adapters;

import static its.madruga.wpp.xposed.plugins.personalization.XStatusHome.itens;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlendMode;
import android.graphics.BlendModeColorFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import its.madruga.wpp.views.RoundedImageView;
import its.madruga.wpp.xposed.plugins.core.DesignUtils;
import its.madruga.wpp.xposed.plugins.core.ResId;
import its.madruga.wpp.xposed.plugins.core.Utils;

public class IGStatusAdapter extends ArrayAdapter {


    public IGStatusAdapter(@NonNull Context context, int resource) {
        super(context, resource);
    }

    @Override
    public int getCount() {
        return itens.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View result = null;
        if (convertView == null) {

            RelativeLayout relativeLayout = new RelativeLayout(this.getContext());
            RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(Utils.dipToPixels(86), ViewGroup.LayoutParams.WRAP_CONTENT);
            relativeLayout.setLayoutParams(relativeParams);

            // Criando o FrameLayout
            FrameLayout frameLayout = new FrameLayout(this.getContext());
            frameLayout.setId(ResId.id.contact_selector);
            frameLayout.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            // Criando o LinearLayout
            LinearLayout linearLayout = new LinearLayout(this.getContext());
            LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            linearLayout.setLayoutParams(linearParams);

            // Criando o RelativeLayout interno
            RelativeLayout internalRelativeLayout = new RelativeLayout(this.getContext());
            RelativeLayout.LayoutParams internalRelativeParams = new RelativeLayout.LayoutParams(Utils.dipToPixels(64), Utils.dipToPixels(64));
            internalRelativeLayout.setLayoutParams(internalRelativeParams);

            // Adicionando os elementos ao RelativeLayout interno
            ImageView contactPhoto = new RoundedImageView(this.getContext());
            contactPhoto.setId(ResId.id.contact_photo);
            RelativeLayout.LayoutParams photoParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            contactPhoto.setLayoutParams(photoParams);
            contactPhoto.setPadding(Utils.dipToPixels(2.5F), Utils.dipToPixels(2.5F), Utils.dipToPixels(2.5F), Utils.dipToPixels(2.5F));
            contactPhoto.setScaleType(ImageView.ScaleType.CENTER_CROP);
            contactPhoto.setImageDrawable(DesignUtils.getDrawableByName("avatar_contact"));

//            contactPhoto.setTag("attr08a7", Utils.dipToPixels(2.5F));
//            contactPhoto.setTag("cstErrorColor", ContextCompat.getColor(context, R.color.status_error));
//            contactPhoto.setTag("cstSeenColor", ContextCompat.getColor(context, R.color.status_seen));
//            contactPhoto.setTag("cstUnseenColor", ContextCompat.getColor(context, R.color.status_unseen));
//            contactPhoto.setTag("tbtnForegroundOnly", false);
//            contactPhoto.setTag("tbtnRadius", dpToPx(80));

            RelativeLayout addBtnRelativeLayout = new RelativeLayout(this.getContext());
            addBtnRelativeLayout.setBackgroundColor(Color.TRANSPARENT);
            addBtnRelativeLayout.setId(ResId.id.add_button);
            RelativeLayout.LayoutParams addBtnParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            addBtnParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            addBtnParams.addRule(RelativeLayout.ALIGN_PARENT_END);
            addBtnParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            addBtnRelativeLayout.setLayoutParams(addBtnParams);

            ImageView iconImageView = new ImageView(this.getContext());
            iconImageView.setId(ResId.id.icon);
            RelativeLayout.LayoutParams iconParams = new RelativeLayout.LayoutParams(Utils.dipToPixels(24), Utils.dipToPixels(24));
            iconImageView.setLayoutParams(iconParams);
            var icon = DesignUtils.getDrawableByName("ic_add_to_status_wds");
            icon.setColorFilter(new BlendModeColorFilter(Color.GREEN, BlendMode.SRC_ATOP));
            iconImageView.setImageDrawable(icon);
            iconImageView.setBackgroundColor(Color.TRANSPARENT);

            addBtnRelativeLayout.addView(iconImageView);
            internalRelativeLayout.addView(contactPhoto);
            internalRelativeLayout.addView(addBtnRelativeLayout);

            TextView contactName = new TextView(this.getContext());
            contactName.setEllipsize(TextUtils.TruncateAt.END);
            contactName.setGravity(Gravity.CENTER);
            contactName.setId(ResId.id.contact_name);
            LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            contactName.setLayoutParams(nameParams);
            contactName.setText("Name");
            contactName.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            contactName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            contactName.setTypeface(Typeface.DEFAULT_BOLD);
            contactName.setMaxLines(1);

            linearLayout.addView(internalRelativeLayout);
            linearLayout.addView(contactName);
            frameLayout.addView(linearLayout);
            relativeLayout.addView(frameLayout);
            convertView = relativeLayout;
        }
        return convertView;
    }
}
