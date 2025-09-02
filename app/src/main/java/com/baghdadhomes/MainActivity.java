package com.baghdadhomes;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends Activity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private CoordinatorLayout container;
    private WebView webViewPopUp;
    private AlertDialog builder;
    private String userAgent;
    public WebView mWebView;
    private BottomNavigationView bottomNavigationView;
    private SpinKitView spinKitView;
    private RelativeLayout fp;
    private int PROGRESS_DIALOG_COUNTER;
    SwipeRefreshLayout swipeRefreshLayout;
    private final int FILECHOOSER_RESULTCODE = 1;
    private final int REQUEST_SELECT_FILE = 2;
    ValueCallback<Uri[]> uploadMessage = null;
    ValueCallback<Uri> mUploadMessage = null;
    String appUrl = "https://najafhome.com/";
    String mailUrl = "mailto:";
    String mobileUrl = "tel:";
    String whatsappShare = "https://api.whatsapp.com/send?text=%D8%A8%D9%8A%D8%AA+%D9%81%D9%8A+%D8%AD%D9%8A+%D8%A7%D9%84%D8%AC%D8%A7%D9%85%D8%B9%D9%87%C2%A0https%3A%2F%2Fnajafhome.com%2Fproperty%2F%25d8%25a8%25d9%258a%25d8%25aa-%25d9%2581%25d9%258a-%25d8%25ad%25d9%258a-%25d8%25a7%25d9%2584%25d8%25ac%25d8%25a7%25d9%2585%25d8%25b9%25d9%2587%2F";
    //String whatsappShare = "https://api.whatsapp.com/send?text=%D9%84%D9%84%D8%A3%D9%8A%D8%AC%D8%A7%D8%B1+%D8%B9%D9%85%D8%A7%D8%AF+%D8%B3%D9%83%D8%B1+%D8%AA%D8%AC%D8%A7%D8%B1%D9%8A%C2%A0https%3A%2F%2Fnajafhome.com%2Fstaging%2Fproperty%2Fimad-sukar-commercial%2F";
    String whatsappUrl = "https://api.whatsapp.com/send?phone=+9647735032549&text=%D9%85%D8%B1%D8%AD%D8%A8%D8%A7%20%D8%A7%D9%86%20%D9%85%D9%87%D8%AA%D9%85%20%D8%A8%D9%80%D9%80%D9%80%20";

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        fp = findViewById(R.id.fpa);
        mWebView = findViewById(R.id.webView);
        spinKitView = findViewById(R.id.spin_kit);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        bottomNavigationView = findViewById(R.id.nav_view);
        container = findViewById(R.id.container);


        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelected(true);
        bottomNavigationView.animate();
        bottomNavigationView.setBackground(null);




        WebSettings webSettings = mWebView.getSettings();

       /* mWebView.getSettings().setAllowFileAccess(true);
        mWebView.getSettings().setDomStorageEnabled(true);//
        mWebView.getSettings().setDatabaseEnabled(true);//
        mWebView.getSettings().setPluginState(WebSettings.PluginState.ON);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);
        mWebView.getSettings().setBlockNetworkImage(false);
        mWebView.getSettings().setBlockNetworkLoads(false);
        String dir = this.getApplicationContext().getDir("database", Context.MODE_PRIVATE).getPath();
        mWebView.getSettings().setGeolocationDatabasePath(dir);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setGeolocationEnabled(true);
        webSettings.setPluginState(WebSettings.PluginState.ON);*/


        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);
        //webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setDomStorageEnabled(true);

        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSettings.setDomStorageEnabled(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSettings.setUseWideViewPort(true);
        webSettings.setSaveFormData(true);
        webSettings.setEnableSmoothTransition(true);
        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        //webSettings.setSupportMultipleWindows(true);
        CookieManager.getInstance().setAcceptCookie(true);
        if(android.os.Build.VERSION.SDK_INT >= 21)
            CookieManager.getInstance().setAcceptThirdPartyCookies(mWebView, true);

        //userAgent = System.getProperty("https.agent");
        //webSettings.setUserAgentString(userAgent + getString(R.string.app_name));
        //webSettings.setUserAgentString(USER_AGENT);



        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);

        //mWebView.setWebViewClient(new MyWebViewClient());
        //mWebView.setWebChromeClient(new MyWebChrome());
        mWebView.setScrollbarFadingEnabled(false);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setDisplayZoomControls(false);
        mWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //  webSettings.setCacheMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }


        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);


        CookieManager manager = CookieManager.getInstance();
        if (Build.VERSION.SDK_INT < 22) {
            manager.setAcceptCookie(true);
        } else {
            manager.setAcceptThirdPartyCookies(mWebView, true);
        }


        if (isNetworkAvailable()) {
            //mWebView.loadUrl("http://najafhome.com/");
            loadScreen(appUrl);
            mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
            bottomNavigationView.setSelectedItemId(R.id.home);
        } else {
            alertDialog(MainActivity.this, "Connection error!", "Check your internet connection and try again later");
        }


        fp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomNavigationView.setSelectedItemId(R.id.add);
                mWebView.loadUrl("https://najafhome.com/create-listing/");
                //mWebView.setWebChromeClient(new WebChromeClient());
                mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
                //mWebView.loadUrl("https://najafhome.com/");

            }
        });


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mWebView.clearCache(true);
                swipeRefreshLayout.setRefreshing(false);
                mWebView.reload();
                Log.d("urlUrl123", appUrl);
            }
        });


        swipeRefreshLayout.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                if (mWebView.getScrollY() == 0)
                    swipeRefreshLayout.setEnabled(true);
                else
                    swipeRefreshLayout.setEnabled(false);
            }
        });


        mWebView.setWebChromeClient(new WebChromeClient(){

            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);

                Log.e("onProgressChanged", "----------------" + newProgress);
                if (PROGRESS_DIALOG_COUNTER < 1) {
                    spinKitView.setVisibility(View.VISIBLE);
                    PROGRESS_DIALOG_COUNTER++;
                }
                if (newProgress == 100) {
                    spinKitView.setVisibility(View.GONE);
                    PROGRESS_DIALOG_COUNTER = 0;
                }
            }

            protected void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                mUploadMessage = uploadMsg;
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");

                    startActivityForResult(Intent.createChooser(intent, "File Browser"), FILECHOOSER_RESULTCODE);

            }

            public boolean onShowFileChooser(WebView mWebView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
                if (uploadMessage != null) {
                    uploadMessage.onReceiveValue(null);
                    uploadMessage = null;
                }

                uploadMessage = filePathCallback;
                Intent intent = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    intent = fileChooserParams.createIntent();
                }
                try {
                    startActivityForResult(intent, REQUEST_SELECT_FILE);
                } catch (ActivityNotFoundException e) {
                    uploadMessage = null;
                    return false;
                }
                return true;
            }
        });


    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
/*
        switch (item.getItemId()) {
            case R.id.home:
                if (isNetworkAvailable()) {
                    //mWebView.loadUrl("https://najafhome.com/");
                    loadScreen(appUrl);
                    mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
                } else {
                    alertDialog(MainActivity.this, "Connection error!", "Check your internet connection and try again later");
                }
                return true;

            case R.id.commercial:
                if (isNetworkAvailable()) {
                    // mWebView.loadUrl("http://najafhome.com/%d8%aa%d8%ac%d8%a7%d8%b1%d9%8a/");
                    loadScreen("https://najafhome.com/%d8%aa%d8%ac%d8%a7%d8%b1%d9%8a/");
                    mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
                } else {
                    alertDialog(MainActivity.this, "Connection error!", "Check your internet connection and try again later");
                }
                return true;

            case R.id.area:
                if (isNetworkAvailable()) {
                    loadScreen("https://najafhome.com/%d8%a7%d9%84%d9%85%d9%86%d8%a7%d8%b7%d9%82/");
                    mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
                    // mWebView.loadUrl("http://najafhome.com/%d8%a7%d9%84%d9%85%d9%86%d8%a7%d8%b7%d9%82/");
                } else {
                    alertDialog(MainActivity.this, "Connection error!", "Check your internet connection and try again later");
                }
                return true;

            case R.id.properties:
                if (isNetworkAvailable()) {
                    // mWebView.loadUrl("http://najafhome.com/husprop/");
                    loadScreen("https://najafhome.com/husprop/");
                    mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
                } else {
                    alertDialog(MainActivity.this, "Connection error!", "Check your internet connection and try again later");
                }
                return true;

            case R.id.add:
                if (isNetworkAvailable()) {
                    // mWebView.loadUrl("http://najafhome.com/contact-2/");
                    loadScreen("https://najafhome.com/create-listing/");
                    mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
                } else {
                    alertDialog(MainActivity.this, "Connection error!", "Check your internet connection and try again later");
                }
                return true;
        }
*/
        return false;

    }

    public void alertDialog(Activity activity, String title, String msg) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(msg).setCancelable(false).setPositiveButton(
                "Ok", (dialog, id) -> {
                    dialog.dismiss();
                    finish();
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager)
                getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();

    }

/*
    private class MyWebViewClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            System.out.println("onPageStarted");
//            spinKitView.setVisibility(View.VISIBLE);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            String host = Uri.parse(url).getHost();

            if( url.startsWith("http:") || url.startsWith("https:") ) {
                if(Uri.parse(url).getPath().equals("/connection-compte.html")) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(appUrl));
                    startActivity(browserIntent);
                    return true ;
                }

                if (host.equals("www.example.com")) {
                    if (webViewPopUp != null) {
                        webViewPopUp.setVisibility(View.GONE);
                        container.removeView(webViewPopUp);
                        webViewPopUp = null;
                    }
                    return false;
                }
                if (host.equals("m.facebook.com") || host.equals("www.facebook.com") || host.equals("facebook.com")) {
                    return false;
                }
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
                return true;
            }
            return true;
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            Log.d("onReceivedSslError", "onReceivedSslError");
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if(url.startsWith("https://m.facebook.com/v2.7/dialog/oauth")){
                if(webViewPopUp!=null) {
                    webViewPopUp.setVisibility(View.GONE);
                    container.removeView(webViewPopUp);
                    webViewPopUp=null;
                }
                view.loadUrl(appUrl);
                return;
            }
        }

*/


    @Override
    public void onBackPressed() {
        if (bottomNavigationView.getSelectedItemId() == R.id.home) {
            super.onBackPressed();
        } else {
            loadScreen(appUrl);
            bottomNavigationView.setSelectedItemId(R.id.home);
        }
    }

    private class MyWebChrome extends WebChromeClient {

        @Override
        public void onCloseWindow(WebView window) {
            try {
                webViewPopUp.destroy();
            } catch (Exception e) {
                Log.d("Destroyed with Error ", e.getStackTrace().toString());
            }

            try {
                builder.dismiss();
            } catch (Exception e) {
                Log.d("Dismissed with Error: ", e.getStackTrace().toString());
            }

        }


        @Override
        public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
            webViewPopUp = new WebView(MainActivity.this.getApplicationContext());
            webViewPopUp.setVerticalScrollBarEnabled(false);
            webViewPopUp.setHorizontalScrollBarEnabled(false);
            webViewPopUp.setWebChromeClient(new MyWebChrome());
            webViewPopUp.getSettings().setJavaScriptEnabled(true);
            webViewPopUp.getSettings().setSaveFormData(true);
            webViewPopUp.getSettings().setEnableSmoothTransition(true);
            webViewPopUp.getSettings().setUserAgentString(userAgent + "yourAppName");

            // pop the  webview with alert dialog
            builder = new AlertDialog.Builder(MainActivity.this).create();
            builder.setTitle("");
            builder.setView(webViewPopUp);
            builder.setButton("Close", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    webViewPopUp.destroy();
                    dialog.dismiss();
                }
            });
            builder.show();
            builder.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);

            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setAcceptCookie(true);
            if(android.os.Build.VERSION.SDK_INT >= 21) {
                cookieManager.setAcceptThirdPartyCookies(webViewPopUp, true);
                cookieManager.setAcceptThirdPartyCookies(mWebView, true);
            }

            webViewPopUp.setLayoutParams(new CoordinatorLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            container.addView(webViewPopUp);


            WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
            transport.setWebView(webViewPopUp);
            resultMsg.sendToTarget();

            return true;
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);

            Log.e("onProgressChanged", "----------------" + newProgress);
            if (PROGRESS_DIALOG_COUNTER < 1) {
                spinKitView.setVisibility(View.VISIBLE);
                PROGRESS_DIALOG_COUNTER++;
            }
            if (newProgress == 100) {
                spinKitView.setVisibility(View.GONE);
                PROGRESS_DIALOG_COUNTER = 0;
            }
        }
    }

    public void loadScreen(String url) {
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d("linkLink", url);
                //appUrl = url;
                if (url.equals("https://najafhome.com/")){
                    bottomNavigationView.setSelectedItemId(R.id.home);
                    mWebView.loadUrl(url);
                } else if(url.equals("https://najafhome.com/%d8%aa%d8%ac%d8%a7%d8%b1%d9%8a/")){
                    bottomNavigationView.setSelectedItemId(R.id.commercial);
                    mWebView.loadUrl(url);
                } else if(url.equals("https://najafhome.com/%d8%a7%d9%84%d9%85%d9%86%d8%a7%d8%b7%d9%82/")){
                    bottomNavigationView.setSelectedItemId(R.id.area);
                    mWebView.loadUrl(url);
                } else if (url.equals("https://najafhome.com/husprop/")){
                    bottomNavigationView.setSelectedItemId(R.id.properties);
                    mWebView.loadUrl(url);
                } else if (url.equals("https://najafhome.com/create-listing/")){
                    bottomNavigationView.setSelectedItemId(R.id.add);
                    mWebView.loadUrl(url);
                } else if (url.equals("https://najafhome.com/all-agency/")){
                    //agencies
                    //bottomNavigationView.setSelectedItemId(R.id.home);
                    mWebView.loadUrl(url);
                }else if (url.equals("https://najafhome.com/aboutnajafhome/")){
                    //about najaf
                    //bottomNavigationView.setSelectedItemId(R.id.home);
                    mWebView.loadUrl(url);
                }else if (url.equals("https://najafhome.com/contact-2/")){
                    //contact us
                    //bottomNavigationView.setSelectedItemId(R.id.home);
                    mWebView.loadUrl(url);
                }else if (url.equals("https://najafhome.com/favorite-properties/")){
                    //favorite properties
                    bottomNavigationView.setSelectedItemId(R.id.properties);
                    mWebView.loadUrl(url);
                }else if (url.equals("https://najafhome.com/my-properties/")){
                    //my properties
                    bottomNavigationView.setSelectedItemId(R.id.properties);
                    mWebView.loadUrl(url);
                }else if (url.equals("https://najafhome.com/login/")) {
                    mWebView.clearCache(true);
                    bottomNavigationView.setSelectedItemId(R.id.home);
                    mWebView.loadUrl(url);
                } else if (url.equals("https://najafhome.com/staging?login=success")) {
                    mWebView.clearCache(true);
                    bottomNavigationView.setSelectedItemId(R.id.home);
                    mWebView.loadUrl(url);
                }else if (url.equals("https://najafhome.com/?login=success")) {
                    mWebView.clearCache(true);
                    bottomNavigationView.setSelectedItemId(R.id.home);
                    mWebView.loadUrl(url);
                } else if (url.equals("https://najafhome.com/wp-login.php?action=logout&redirect_to=https://najafhome.com/staging&_wpnonce=7ccd0cb0f1")) {
                    mWebView.clearCache(true);
                    bottomNavigationView.setSelectedItemId(R.id.home);
                    mWebView.loadUrl(url);
                } else if(url.contains("https://api.whatsapp.com")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                    return true;
                } else if(url.contains(whatsappUrl)) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(whatsappUrl));
                    startActivity(intent);
                    return true;
                } else if (url.contains(mobileUrl)) {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
                    startActivity(intent);
                }else if (url.contains(mailUrl)) {
                    Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse(url));
                    startActivity(intent);
                } else {
                    bottomNavigationView.setSelectedItemId(R.id.add);
                    mWebView.loadUrl(url);
                }
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);

            }
        });
        mWebView.loadUrl(url);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (requestCode == REQUEST_SELECT_FILE) {
                if (uploadMessage == null)
                    return;
                uploadMessage.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, intent));
                uploadMessage = null;
            }
        }
        else if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == mUploadMessage)
                return;
            Uri result = intent == null || resultCode != MainActivity.RESULT_OK ? null : intent.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        mWebView.clearCache(true);
    }

    @Override
    protected void onDestroy() {
        mWebView.clearCache(true);
        super.onDestroy();
    }

}