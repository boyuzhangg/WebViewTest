package com.example.webviewtest;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.webkit.WebView;


public class CustomWebView extends WebView {

    public CustomWebView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public ActionMode startActionMode(final ActionMode.Callback callback, final int type) {
        return super.startActionMode(callback, type);
    }
}
