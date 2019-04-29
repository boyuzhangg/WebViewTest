package com.example.webviewtest;

import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    /**
     * Default local URL that is launched by the test apps.
     */
    public static final String INTERNAL_URL = "file:///android_asset/web_view_text_box.html";
    private final JSInterface mJavascriptInterface = new JSInterface();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (getIntent().getData() == null) {
            getIntent().setData(Uri.parse(INTERNAL_URL));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Uri uri = getIntent().getData();
        if (uri != null) {
            String launchUrl = uri.toString();
            ((TextView) findViewById(R.id.uri)).setText(launchUrl);

            final WebView webview = (WebView) findViewById(R.id.web_view);
            webview.getSettings().setJavaScriptEnabled(true);
            webview.getSettings().setDatabaseEnabled(true);
            webview.addJavascriptInterface(mJavascriptInterface, "INTERFACE");
            webview.setWebViewClient(new WebViewClient() {
                public void onPageFinished(final WebView view, final String url) {
                    pullTextFromWebView(webview);
                }
            });
            webview.loadUrl(launchUrl);
        }
    }

    @Override
    public void onActionModeStarted(final ActionMode mode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Menu popUpMenu = mode.getMenu();
            int lastElementIndex = popUpMenu.size() - 1;
            ArrayList<String> menuTitles = new ArrayList<>();
            for (int i = 0; i <= lastElementIndex; i++) {
                MenuItem item = popUpMenu.getItem(i);
                String title = item.getTitle().toString();
                menuTitles.add(title);

                item.setOnMenuItemClickListener(new CustomFloatingActionBarClickListener());
            }
        }
        super.onActionModeStarted(mode);
    }

    /**
     * Get the Content from the WebView and load it into the text box.
     *
     * @param button The button pressed to trigger the get.
     */
    public void getWebViewContent(final View button) {
        WebView webview = (WebView) findViewById(R.id.web_view);
        pullTextFromWebView(webview);
    }

    private class CustomFloatingActionBarClickListener implements MenuItem.OnMenuItemClickListener {
        @Override
        public boolean onMenuItemClick(final MenuItem item) {
            return onOptionsItemSelected(item);
        }
    }

    private class JSInterface {
        /**
         * Get the text content from the web view and output to the text view.
         *
         * @param webViewContent the text content of the webview.
         */
        @JavascriptInterface
        public void getContent(final String webViewContent) {
            final TextView webViewContextView = (TextView) findViewById(R.id.text_from_web);
            webViewContextView.post(new Runnable() {
                public void run() {
                    webViewContextView.setText(webViewContent.toString());
                }
            });
        }
    }

    /**
     * Pull the contents of the webview and set it in a text view. This will only work if the page is INTERNAL_URL.
     *
     * @param webview The webview to get the content from.
     */
    private void pullTextFromWebView(final WebView webview) {
        webview.loadUrl("javascript:window.INTERFACE.getContent(document.getElementsByName('textContent')[0].value);");
    }
}
