package com.yezi.zuo.music_big_boom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by zuo on 2016/10/2.
 */
public class MarqueeTextView extends TextView {
    public MarqueeTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MarqueeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MarqueeTextView(Context context) {
        super(context);
    }

    @Override
    public boolean isFocused(){
        return true;
    }
}
