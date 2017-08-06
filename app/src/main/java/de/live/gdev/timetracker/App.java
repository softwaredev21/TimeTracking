package de.live.gdev.timetracker;

import android.app.Application;

import de.live.gdev.timetracker.util.AppSettings;

public class App extends Application {
    private volatile static App app;

    public static App get() {
        return app;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;

        AppSettings appSettings = AppSettings.get();
        if (appSettings.isAppFirstStart(false)) {
            appSettings.loadProfile(2);
            appSettings.setProfileLoginUsername("user_");
            appSettings.setProfilePathDomainAndDirectory("https://demo.kimai.org");
            appSettings.loadProfile(0);
        }
    }
}
