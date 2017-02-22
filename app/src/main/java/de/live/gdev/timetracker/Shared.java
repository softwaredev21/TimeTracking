package de.live.gdev.timetracker;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;

public class Shared {


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

    public static void openWebpage(Context context, String url) {
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
                openWebpage(context, "https://gsantner.github.io/donate/#donate");
            }
        }
    }
}
