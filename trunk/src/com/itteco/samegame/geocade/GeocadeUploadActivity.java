/**
 * Copyright (c) 2009 Itteco Software (http://itteco.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.itteco.samegame.geocade;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import com.itteco.samegame.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * User: szotov
 */
public class GeocadeUploadActivity extends Activity {
    public static final String URL = "URL";

    private WebView webView;
    private WebViewClient webViewClient;

    private Button buttonGo;
    private EditText enterUri;

    public class MyChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            GeocadeUploadActivity.this.setProgress(newProgress * 100);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_PROGRESS);
        setContentView(R.layout.geocade_upload);

        setButtonGo((Button) findViewById(R.id.button_go));
        setEnterUri((EditText) findViewById(R.id.enter_uri));
        getEnterUri().setVisibility(View.GONE);
        getButtonGo().setVisibility(View.GONE);

        setWebView((WebView) findViewById(R.id.wv1));
        setWebViewClient(new WebViewClient());
        getWebView().setWebViewClient(getWebViewClient());
        getWebView().setWebChromeClient(new MyChromeClient());

        getWebView().setHorizontalScrollBarEnabled(true);
        getWebView().getSettings().setJavaScriptEnabled(true);
        getWebView().getSettings().setLoadsImagesAutomatically(true);


        Log.v("Score:", getIntent().getExtras().getString(URL));
        getWebView().loadUrl(getIntent().getExtras().getString(URL));
        webView.requestFocus();

        getButtonGo().setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String url = getEnterUri().getText().toString();
                getWebView().loadUrl(url);
            }
        });
    }

    public String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public void setWebView(WebView webView) {
        this.webView = webView;
    }

    public WebView getWebView() {
        return webView;
    }

    public void setWebViewClient(WebViewClient webViewClient) {
        this.webViewClient = webViewClient;
    }

    public WebViewClient getWebViewClient() {
        return webViewClient;
    }

    public void setButtonGo(Button buttonGo) {
        this.buttonGo = buttonGo;
    }

    public Button getButtonGo() {
        return buttonGo;
    }

    public void setEnterUri(EditText enterUri) {
        this.enterUri = enterUri;
    }

    public EditText getEnterUri() {
        return enterUri;
    }

    private static final int MENU_BACK_TO_GAME_SCORE = Menu.FIRST;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(Menu.NONE, MENU_BACK_TO_GAME_SCORE, MENU_BACK_TO_GAME_SCORE, R.string.return_to_game_score);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case MENU_BACK_TO_GAME_SCORE:
                backToGameScore();
                break;
        }
        return true;
    }

    private void backToGameScore() {
        finish();
    }


}
