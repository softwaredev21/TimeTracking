package de.live.gdev.timetracker;

import org.apache.http.util.EncodingUtils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.audiofx.BassBoost.Settings;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

@SuppressLint("SetJavaScriptEnabled")
public class MainActivity extends Activity 
{
	private static final int SETTINGS_ACTIVITY_ID = 15;
	WebView webView;
	SharedPreferences pref;

	boolean autoLogin;
	boolean sslAccept;
	String pNr;
	String username;
	String password;
	String path;
	String filename;
	Uri url;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		this.webView = (WebView) findViewById(R.id.webView1);
		this.pref = this.getSharedPreferences(this.getString(R.string.shared_pref), MODE_PRIVATE);		
		
		this.webView.setWebViewClient(new WebViewClient() {
		    public void onReceivedSslError (WebView view, SslErrorHandler handler, SslError error) 
		    {
		        if(MainActivity.this.sslAccept)
		        {
		        	handler.proceed();
		        }
		        else
		        {
		        	Toast.makeText(MainActivity.this, getString(R.string.ssl_toast_error), Toast.LENGTH_SHORT).show();
					webView.loadData(getString(R.string.ssl_webview_error_str), "text/html", "UTF-16");
		        }
		    }
		});
		
		this.webView.setWebChromeClient(new WebChromeClient());
        
		WebSettings settings = this.webView.getSettings();
		settings.setJavaScriptEnabled(true);
		settings.setDatabaseEnabled(true);
		settings.setDomStorageEnabled(true);
		settings.setBuiltInZoomControls(true);
        settings.setSupportZoom(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);

		this.reload(false);
	}
	
	public void reload(boolean forced)
	{
		this.pNr = SettingsActivity.PROFILE_APPEND[pref.getInt(SettingsActivity.ACTPROFILE_PREF, 0)];
		this.username = pref.getString(SettingsActivity.USERNAME_PREF+pNr, "");
		this.password = pref.getString(SettingsActivity.PASSWORD_PREF+pNr, "");
		this.filename = pref.getString(SettingsActivity.FILENAME_PREF+pNr, "index.php");
		this.path 	  = pref.getString(SettingsActivity.PATH_PREF+pNr, "");
		this.autoLogin= pref.getBoolean(SettingsActivity.AUTOLOGIN_PREF+pNr, false);
		this.sslAccept = pref.getBoolean(SettingsActivity.SSLACCEPT_PREF+pNr, false);
		try
		{
			this.url = Uri.parse(this.path+this.filename);
		}catch(Exception e)
		{
			url = null;
			this.webView.loadData(this.getString(R.string.no_valid_path), "text/html", "UTF-16");
			return;
		}
		
		if(url.toString().equals("") || url.toString().equals("index.php"))
		{
			this.webView.loadData(this.getString(R.string.no_valid_path), "text/html", "UTF-16");
		}
		else
		{
			this.webView.loadUrl(url.toString());
			if(this.autoLogin || forced)
			{
				String postData = "name=" + this.username + "&password=" + this.password;
				this.webView.postUrl(url.toString()+ "?a=checklogin", EncodingUtils.getBytes(postData, "base64"));
			}
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId())
		{
			case R.id.action_settings:
			{
				Intent i = new Intent(this, SettingsActivity.class);
				startActivityForResult(i, SETTINGS_ACTIVITY_ID);
			}break;
			case R.id.action_info:
			{
				Intent i = new Intent(this, InfoActivity.class);
				startActivity(i);
			}break;
			case R.id.action_login:
			{
				this.reload(true);
			}break;
		}
		
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		switch(requestCode)
		{
			case SETTINGS_ACTIVITY_ID:
			{
				this.reload(false);
			}break;
		}
		
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
