package de.live.gdev.timetracker.util;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.RawRes;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.SpannableString;
import android.util.TypedValue;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import de.live.gdev.timetracker.BuildConfig;
import de.live.gdev.timetracker.R;

public class Helpers {

    public static String getAppVersionName(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            return info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "unknown";
        }
    }


    public static void openWebpageWithExternalBrowser(Context context, String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(intent);
    }

    public static void donateBitcoinRequest(Context context) {
        if (!BuildConfig.IS_GPLAY_BUILD) {
            String btcUri = String.format("bitcoin:%s?amount=%s&label=%s&message=%s",
                    "1B9ZyYdQoY9BxMe9dRUEKaZbJWsbQqfXU5", "0.01", "Have some coke, and a nice day", "Have some coke, and a nice day");
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(btcUri));
            try {
                context.startActivity(intent);
            } catch (ActivityNotFoundException e) {
                openWebpageWithExternalBrowser(context, "https://gsantner.github.io/donate/#donate");
            }
        }
    }

    public static String readTextfileFromRawRes(Context context, @RawRes int rawRessourceId, String linePrefix, String linePostfix) {
        StringBuilder sb = new StringBuilder();
        String line;
        BufferedReader br = null;
        linePrefix = linePrefix == null ? "" : linePrefix;
        linePostfix = linePostfix == null ? "" : linePostfix;

        try {
            br = new BufferedReader(new InputStreamReader(context.getResources().openRawResource(rawRessourceId)));
            while ((line = br.readLine()) != null) {
                sb.append(linePrefix);
                sb.append(line);
                sb.append(linePostfix);
                sb.append("\n");
            }
        } catch (Exception ignored) {
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException ignored) {
                }
            }
        }
        return sb.toString();
    }

    public static void showDialogWithRawFileInWebView(Context context, String fileInRaw, @StringRes int resTitleId) {
        WebView wv = new WebView(context);
        wv.loadUrl("file:///android_res/raw/" + fileInRaw);
        AlertDialog.Builder dialog = new AlertDialog.Builder(context)
                .setPositiveButton(R.string.ok, null)
                .setTitle(resTitleId)
                .setView(wv);
        dialog.show();
    }

    public static String loadRawMarkdownForTextView(Context context, @RawRes int rawMdFile, String prepend) {
        try {
            return new SimpleMarkdownParser()
                    .parse(context.getResources().openRawResource(rawMdFile),
                            SimpleMarkdownParser.FILTER_ANDROID_TEXTVIEW, prepend)
                    .replaceColor("#000001", ContextCompat.getColor(context, R.color.accent))
                    .removeMultiNewlines().replaceBulletCharacter("*").getHtml();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static void showDialogWithHtmlTextView(Context context, String html, @StringRes int resTitleId) {
        LinearLayout layout = new LinearLayout(context);
        TextView textView = new TextView(context);
        ScrollView root = new ScrollView(context);
        int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20,
                context.getResources().getDisplayMetrics());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(margin, 0, margin, 0);
        layout.setLayoutParams(layoutParams);

        layout.addView(textView);
        root.addView(layout);

        textView.setText(new SpannableString(Html.fromHtml(html)));
        AlertDialog.Builder dialog = new AlertDialog.Builder(context)
                .setPositiveButton(android.R.string.ok, null)
                .setTitle(resTitleId)
                .setView(root);
        dialog.show();
    }

}
