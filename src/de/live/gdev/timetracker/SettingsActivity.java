package de.live.gdev.timetracker;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

public class SettingsActivity extends Activity implements OnCheckedChangeListener {
		
	// UI
	private RadioGroup profileGroup;
	private RadioButton[] profiles = new RadioButton[3];
	private Button saveButton;
	private CheckBox autoLogin;
	private CheckBox sslAccept;
	private EditText path;
	private EditText username;
	private EditText password;
	private EditText filename;
	
	// Shared Preference
	private SharedPreferences pref;
	public static final String AUTOLOGIN_PREF = "autologin";
	public static final String SSLACCEPT_PREF = "sslAccept";
	public static final String PATH_PREF = "path";
	public static final String USERNAME_PREF = "username";
	public static final String PASSWORD_PREF = "password";
	public static final String FILENAME_PREF = "filename";
	public static final String ACTPROFILE_PREF = "actProfile";
	public static final String[] PROFILE_APPEND = { "", "2", "3" };
	
	//Profiles
	private int[] profileIds = new int[3];
	private int actProfile;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		this.pref = this.getSharedPreferences(this.getString(R.string.shared_pref), MODE_PRIVATE);
		this.saveButton = (Button) findViewById(R.id.buttonSaveSettings);		
		
		this.profileGroup = (RadioGroup) this.findViewById(R.id.radioProfiles);
		this.profileIds[0] = R.id.radioProfileDefault;
		this.profileIds[1] = R.id.radioProfile2;
		this.profileIds[2] = R.id.radioProfile3; 
		this.profiles[0] = (RadioButton) findViewById(this.profileIds[0]);
		this.profiles[1] = (RadioButton) findViewById(this.profileIds[1]);
		this.profiles[2] = (RadioButton) findViewById(this.profileIds[2]);
		this.sslAccept = (CheckBox) findViewById(R.id.checkBoxAcceptAllSsl);
		this.path = (EditText) findViewById(R.id.editTextKimaiPath);
		this.username = (EditText) findViewById(R.id.edUsername);
		this.filename = (EditText) findViewById(R.id.editTextFileName);
		this.password = (EditText) findViewById(R.id.edPassword);
		this.autoLogin = (CheckBox) findViewById(R.id.checkboxAutologin);

		// Load profile
		this.actProfile = pref.getInt(ACTPROFILE_PREF, 0);
		this.profileGroup.setOnCheckedChangeListener(this);
		this.profileGroup.check(this.profileIds[this.actProfile]);
		
		// Force load default profile 
		if(this.actProfile == 0)
			this.onCheckedChanged(this.profileGroup, profileIds[0]);
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
		
		String nr = PROFILE_APPEND[this.actProfile];		
		Editor editor = this.pref.edit();
		editor.putBoolean(AUTOLOGIN_PREF+nr, autoLogin.isChecked());
		editor.putBoolean(SSLACCEPT_PREF+nr, sslAccept.isChecked());
		editor.putString(PATH_PREF+nr, path.getText().toString());
		editor.putString(USERNAME_PREF+nr, username.getText().toString());
		editor.putString(PASSWORD_PREF+nr, password.getText().toString());
		editor.putString(FILENAME_PREF+nr, filename.getText().toString());
		editor.putInt(ACTPROFILE_PREF, actProfile);
		editor.apply();
		
		Toast.makeText(this, this.getString(R.string.save_settings_succ),Toast.LENGTH_SHORT).show();
		this.finish();
	}

	
	@Override
	public void onCheckedChanged(RadioGroup rg, int id) {
		// Determine Profile number
		this.actProfile = findActProfileNr();
		String nr = PROFILE_APPEND[this.actProfile];
		
		// Get profile data
		this.path.setText(pref.getString(PATH_PREF+nr, ""));
		this.username.setText(pref.getString(USERNAME_PREF+nr, ""));
		this.password.setText(pref.getString(PASSWORD_PREF+nr, ""));
		this.autoLogin.setChecked(pref.getBoolean(AUTOLOGIN_PREF+nr, false));
		this.filename.setText(pref.getString(FILENAME_PREF+nr, "index.php"));
		this.sslAccept.setChecked(pref.getBoolean(SSLACCEPT_PREF+nr, false));
	}
	
	private int findActProfileNr(){
		int id = this.profileGroup.getCheckedRadioButtonId();
		for(int i=0; i < profileIds.length; i++){
			if(profileIds[i] == id)
				return i;
		}
		return 0;
	}

}
