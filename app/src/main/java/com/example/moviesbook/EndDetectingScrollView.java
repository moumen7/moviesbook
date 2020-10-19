package com.example.moviesbook;
import android.content.Context;
import android.view.View;
import android.widget.ScrollView;

public class EndDetectingScrollView extends ScrollView {
    private boolean scrollPositionChanged = true;

    private ScrollEndingListener scrollEndingListener;

    public EndDetectingScrollView(Context context) {
        super(context);
    }

    public interface ScrollEndingListener {
        void onScrolledToEnd();
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);

        View view = this.getChildAt(this.getChildCount() - 1);
        int diff = (view.getBottom() - (this.getHeight() + this.getScrollY()));
        if (diff <= 0) {
            if (scrollPositionChanged) {
                scrollPositionChanged = false;
                if (scrollEndingListener != null) {
                    scrollEndingListener.onScrolledToEnd();
                }
            }
        } else {
            scrollPositionChanged = true;
        }
    }

    public void setScrollEndingListener(ScrollEndingListener scrollEndingListener) {
        this.scrollEndingListener = scrollEndingListener;
    }
}