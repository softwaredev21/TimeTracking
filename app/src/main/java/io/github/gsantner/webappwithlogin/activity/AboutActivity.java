/*
 * ---------------------------------------------------------------------------- *
 * Gregor Santner <gsantner.github.io> wrote this file. You can do whatever
 * you want with this stuff. If we meet some day, and you think this stuff is
 * worth it, you can buy me a coke in return. Provided as is without any kind
 * of warranty. No attribution required.                  - Gregor Santner
 *
 * License of this file: Creative Commons Zero (CC0 1.0)
 *  http://creativecommons.org/publicdomain/zero/1.0/
 * ----------------------------------------------------------------------------
 */

 /*
 * Get updates:
 *  https://github.com/gsantner/onePieceOfCode/blob/master/java/aboutActivity/AboutActivity.java
 * A simple activity to show information about the app.
 * Intended to use together: SimpleMarkdownParser, Helpers, AboutActivity and it's xml-layout.
 */

package io.github.gsantner.webappwithlogin.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.live.gdev.timetracker.R;
import io.github.gsantner.webappwithlogin.util.Helpers;
import io.github.gsantner.webappwithlogin.util.HelpersA;
import io.github.gsantner.opoc.util.SimpleMarkdownParser;

@SuppressWarnings("unused")
public class AboutActivity extends AppCompatActivity {
    //####################
    //##  Ui Binding
    //####################
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.about__activity__text_app_version)
    TextView textAppVersion;

    @BindView(R.id.about__activity__text_team)
    TextView textTeam;

    @BindView(R.id.about__activity__text_contributors)
    TextView textContributors;

    @BindView(R.id.about__activity__text_license)
    TextView textLicense;

    //####################
    //##  Methods
    //####################
    @Override
    @SuppressLint("SetTextI18n")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about__activity);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        textTeam.setMovementMethod(LinkMovementMethod.getInstance());
        textLicense.setMovementMethod(LinkMovementMethod.getInstance());
        textContributors.setMovementMethod(LinkMovementMethod.getInstance());

        Helpers helpers = Helpers.get();
        helpers.setHtmlToTextView(textTeam,
                Helpers.get().loadMarkdownForTextViewFromRaw(R.raw.maintainers, "")
        );

        helpers.setHtmlToTextView(textContributors,
                Helpers.get().loadMarkdownForTextViewFromRaw(R.raw.contributors, "")
        );

        // License text MUST be shown
        try {
            helpers.setHtmlToTextView(textLicense,
                    SimpleMarkdownParser.get().parse(getString(R.string.copyright_license_text_official).replace("\n", "  \n"),
                            "", SimpleMarkdownParser.FILTER_ANDROID_TEXTVIEW).getHtml()
            );
        } catch (IOException e) {
            e.printStackTrace();
        }


        // App version
        try {
            PackageManager manager = getPackageManager();
            PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
            textAppVersion.setText(getString(R.string.app_version_v, info.versionName));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.about__activity__text_app_version)
    public void onVersionClicked(View v) {
        Helpers.get().openWebpageInExternalBrowser(getString(R.string.app_www_source));
    }

    @OnClick({R.id.about__activity__text_app_version, R.id.about__activity__button_third_party_licenses, R.id.about__activity__button_app_license})
    public void onButtonClicked(View v) {
        Context context = v.getContext();
        switch (v.getId()) {
            case R.id.about__activity__text_app_version: {
                Helpers.get().openWebpageInExternalBrowser(getString(R.string.app_www_source));
                break;
            }
            case R.id.about__activity__button_app_license: {
                HelpersA.get(this).showDialogWithHtmlTextView(R.string.licenses, Helpers.get().readTextfileFromRawRes(R.raw.license, "", ""), false, null);
                break;
            }
            case R.id.about__activity__button_third_party_licenses: {
                try {
                    HelpersA.get(this).showDialogWithHtmlTextView(R.string.licenses, new SimpleMarkdownParser().parse(
                            getResources().openRawResource(R.raw.licenses_3rd_party),
                            "", SimpleMarkdownParser.FILTER_ANDROID_TEXTVIEW).getHtml()
                    );
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }
}