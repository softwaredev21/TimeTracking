package de.live.gdev.timetracker;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class SettingsActivity extends Activity {
	
	SharedPreferences pref;
	
	Button saveButton;
	CheckBox autoLogin;
	CheckBox sslAccept;
	EditText path;
	EditText username;
	EditText password;
	EditText filename;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		this.pref = this.getSharedPreferences(this.getString(R.string.shared_pref), MODE_PRIVATE);
		this.saveButton = (Button) findViewById(R.id.buttonSaveSettings);
		this.sslAccept = (CheckBox) findViewById(R.id.checkBoxAcceptAllSsl);
		this.path = (EditText) findViewById(R.id.editTextKimaiPath);
		this.username = (EditText) findViewById(R.id.edUsername);
		this.filename = (EditText) findViewById(R.id.editTextFileName);
		this.password = (EditText) findViewById(R.id.edPassword);
		this.autoLogin = (CheckBox) findViewById(R.id.checkboxAutologin);

		this.path.setText(pref.getString("path", ""));
		this.username.setText(pref.getString("username", ""));
		this.password.setText(pref.getString("password", ""));
		this.autoLogin.setChecked(pref.getBoolean("autologin", false));
		this.filename.setText(pref.getString("filename", "index.php"));
		this.sslAccept.setChecked(pref.getBoolean("sslAccept", false));
	}
	
	public void onSaveButtonClicked(View v)
	{
		try
		{
			String p = this.path.getText().toString();
			if(p.charAt(p.length()-1) != '/')
				this.path.append("/");
		}
		catch(Exception e){}
		
		Editor editor = this.pref.edit();
		editor.putBoolean("autologin", autoLogin.isChecked());
		editor.putBoolean("sslAccept", sslAccept.isChecked());
		editor.putString("path", path.getText().toString());
		editor.putString("username", username.getText().toString());
		editor.putString("password", password.getText().toString());
		editor.putString("filename", filename.getText().toString());
		editor.apply();
		
		Toast.makeText(this, this.getString(R.string.save_settings_succ),Toast.LENGTH_SHORT).show();
		this.finish();
	}

}
