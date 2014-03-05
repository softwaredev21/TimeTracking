package de.live.gdev.timetracker;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
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
		try {
			Uri uri = Uri.parse("https://github.com/kimai/android");
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			startActivity(intent);
		} catch (Exception e) {
		}
	}
}
