package de.live.gdev.timetracker;

import java.util.GregorianCalendar;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

@SuppressWarnings("deprecation")
public class InfoActivity extends Activity {

	TextView tv;
	PackageManager manager;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_info);
		((Button) this.findViewById(R.id.button_kimai_browse)).setBackgroundDrawable(null);
		((Button) this.findViewById(R.id.button_DevContact)).setBackgroundDrawable(null);	
		this.tv = (TextView) this.findViewById(R.id.textView_Version);
		this.manager = this.getPackageManager();
		
		try {
			PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
			this.tv.setText("App Version \nv" + info.versionName);
		} catch (Exception e) {
		}
	}
	
	public void onKimaiClick(View v) {
		try {
			Uri uri = Uri.parse("http://www.kimai.org/");
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			startActivity(intent);
		} catch (Exception e) {
		}
	}
	
	public void onAndroidAppClick(View v)
	{
		String body = "Date = " + GregorianCalendar.getInstance().getTime().toLocaleString();
		try {
			PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
			body += "\nVersionCode = " + info.versionCode +
					"\nVersionName = "  + info.versionName+
					"\nPackageName = " + info.packageName +
					"\nAndroidVersion = " + Build.VERSION.RELEASE.toString() +
					"\n\n";
		} catch (Exception e) {
		}
		body += "Kimai for Android app reply:\n\n\n";
		
		String mail = "mailto:" + Uri.encode("gdev@live.de");
               mail+= "?subject=" + Uri.encode("Kimai Android Application");
               mail+= "&body=" + Uri.encode(body);
        
	    Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse(mail));
	    startActivity(intent);
	}

}
