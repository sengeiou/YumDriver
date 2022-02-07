package com.yum_driver.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.yum_driver.R;


public class WebViewActivity extends BaseActivity {

	private WebView webview;
    private ProgressDialog progressBar;
    private String url;
    private ImageView ivBack;
    private TextView txtToolbarName;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);

        webview =  findViewById(R.id.termsweb);
        ivBack = findViewById(R.id.ivBack);
        txtToolbarName = findViewById(R.id.txtToolbarName);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

	    WebSettings settings = webview.getSettings();
        settings.setJavaScriptEnabled(true);
        webview.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);

        url = getIntent().getStringExtra("url");
        txtToolbarName.setText(getIntent().getStringExtra("toolbar"));

        progressBar = ProgressDialog.show(WebViewActivity.this,"",""+getResources().getString(R.string.loading));

        webview.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                if (progressBar.isShowing()) {
                    progressBar.dismiss();
                }
            }

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(WebViewActivity.this, "Oh no! " + description, Toast.LENGTH_SHORT).show();

            }
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);

                if (url.startsWith("tel:")) {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
                    startActivity(intent);
                    return true;
                }else if(url.startsWith("http:") || url.startsWith("https:")) {
                    view.loadUrl(url);
                    return true;
                }else if (url.startsWith("mailto:")) {
                    String mail = url.replace("mailto:", "");
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("message/rfc822");
                    intent.putExtra(Intent.EXTRA_EMAIL, mail);
                    startActivity(Intent.createChooser(intent, "Send Email"));
                    return true;
                }
                else if(url.startsWith("whatsapp:"))
                {
                    try {
                        PackageManager pm = getPackageManager();
                        pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                    } catch (PackageManager.NameNotFoundException e) {
                        Toast.makeText(WebViewActivity.this, getResources().getString(R.string.whats_app_error), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                    return true;
                }
                return false;
            }
        });
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setDomStorageEnabled(true);
        webview.setOverScrollMode(WebView.OVER_SCROLL_NEVER);

        webview.loadUrl(url);
    }
    

    @Override
    public void onBackPressed() {
        if (webview.canGoBack()) {
            webview.goBack();
        }else {
            super.onBackPressed();
        }
    }

}
