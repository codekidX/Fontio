package com.codekidlabs.fontio;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;


public class GeoTextView extends TextView {
    public GeoTextView(Context context) {
        super(context);
        init();
    }

    public GeoTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GeoTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/Geo-Regular.ttf");
        setTypeface(typeface);
    }

    public static Typeface getGeoTypeface(Context context) {
        return Typeface.createFromAsset(context.getAssets(), "fonts/Geo-Regular.ttf");
    }
}
