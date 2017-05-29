package de.live.gdev.timetracker.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import org.apache.http.util.EncodingUtils;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import de.live.gdev.timetracker.BuildConfig;
import de.live.gdev.timetracker.R;
import de.live.gdev.timetracker.util.AppSettings;
import de.live.gdev.timetracker.util.Profile;
import io.github.gsantner.opoc.util.Helpers;
import io.github.gsantner.opoc.util.HelpersA;
import io.github.gsantner.opoc.util.SimpleMarkdownParser;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public static final boolean LOAD_IN_DESKTOP_MODE = true;

    @BindView(R.id.web_view)
    WebView webView;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;

    @BindView(R.id.nav_view)
    NavigationView navigationView;

    Profile profile;

    @Override
    @SuppressLint("SetTextI18n")
    protected void onCreate(Bundle savedInstanceState) {
        // Setup UI
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main__activity);
        ButterKnife.bind(this);

        // Setup bars
        setSupportActionBar(toolbar);
        navigationView.setNavigationItemSelectedListener(this);

        navigationView.getMenu().findItem(R.id.action_donate_bitcoin).setVisible(!BuildConfig.IS_GPLAY_BUILD);
        profile = Profile.getDefaultProfile(this);
        ((TextView) navigationView.getHeaderView(0).findViewById(R.id.navheader_subtext))
                .setText("v" + Helpers.get().get().getAppVersionName());


        webView.setWebChromeClient(new WebChromeClient());
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setBuiltInZoomControls(true);

        if (LOAD_IN_DESKTOP_MODE) {
            settings.setSupportZoom(true);
            settings.setLoadWithOverviewMode(true);
            settings.setUseWideViewPort(true);
        }

        // Apply web settings
        webView.setWebViewClient(new WebViewClient() {
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                if (profile.isAcceptAllSsl()) {
                    handler.proceed();
                } else {
                    Snackbar.make(findViewById(android.R.id.content), R.string.ssl_toast_error, Snackbar.LENGTH_SHORT).show();
                    webView.loadData(getString(R.string.ssl_webview_error_str), "text/html", "UTF-16");
                }
            }
        });


        // Show first start dialog / changelog
        AppSettings appSettings = new AppSettings(this);
        try {
            if (appSettings.isAppFirstStart()) {
                HelpersA.get(this).showDialogWithHtmlTextView(R.string.changelog, new SimpleMarkdownParser().parse(
                        getResources().openRawResource(R.raw.licenses_3rd_party),
                        SimpleMarkdownParser.FILTER_ANDROID_TEXTVIEW, "").getHtml()
                );
            } else if (appSettings.isAppCurrentVersionFirstStart()) {
                HelpersA.get(this).showDialogWithHtmlTextView(R.string.changelog,
                        new SimpleMarkdownParser().parse(
                                getResources().openRawResource(R.raw.changelog),
                                SimpleMarkdownParser.FILTER_ANDROID_TEXTVIEW, "").getHtml());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main__menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return handleBarClick(item) || super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        handleBarClick(item);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public boolean handleBarClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings: {
                startActivityForResult(new Intent(this, SettingsActivity.class), SettingsActivity.ACTIVITY_ID);
                return true;
            }
            case R.id.action_login: {
                loadWebapp(true);
                return true;
            }
            case R.id.action_info: {
                startActivity(new Intent(this, InfoActivity.class));
                return true;
            }
            case R.id.action_exit: {
                webView.clearCache(true);
                webView.clearFormData();
                webView.clearHistory();
                webView.clearMatches();
                webView.clearSslPreferences();
                finish();
                return true;
            }
            case R.id.action_donate_bitcoin: {
                Helpers.get().get().showDonateBitcoinRequest();
                return true;
            }
            case R.id.action_homepage_additional: {
                Helpers.get().get().openWebpageInExternalBrowser(getString(R.string.page_additional_homepage));
                return true;
            }
            case R.id.action_homepage_author: {
                Helpers.get().get().openWebpageInExternalBrowser(getString(R.string.page_author));
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SettingsActivity.ACTIVITY_ID &&
                resultCode == SettingsActivity.RESULT.CHANGED) {
            profile = Profile.getDefaultProfile(this);
        }
    }

    @Override
    public boolean onKeyDown(int key, KeyEvent e) {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }
        if ((key == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(key, e);
    }

    @Override
    protected void onResume() {
        super.onResume();
        profile.reloadSettings();
        loadWebapp(profile.isAutoLogin());
    }

    @OnClick(R.id.fab)
    public void onFloatingActionButtonClicked(View v) {
        drawer.openDrawer(GravityCompat.START);
    }

    @OnLongClick(R.id.fab)
    public boolean onFloatingActionButtonLongClicked(View v) {
        loadWebapp(false);
        return true;
    }

    public void loadWebapp(boolean doLogin) {
        Uri url;
        try {
            url = Uri.parse(profile.getFullPath());
        } catch (Exception e) {
            webView.loadData(getString(R.string.no_valid_path), "text/html", "UTF-16");
            return;
        }

        String url_s = url.toString();
        if (TextUtils.isEmpty(url_s) || url_s.equals("index.php")) {
            webView.loadData(getString(R.string.no_valid_path), "text/html", "UTF-16");
        } else {
            webView.loadUrl(url_s);
            if (doLogin) {
                url_s += "?a=checklogin";
                String postData = "name=" + profile.getUsername() + "&password=" + profile.getPassword();
                this.webView.postUrl(url_s, EncodingUtils.getBytes(postData, "base64"));
            }
        }
    }
}
