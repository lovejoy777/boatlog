package com.lovejoy777.boatlog.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.lovejoy777.boatlog.BackupActivity;

/**
 * Created by steve on 14/10/17.
 */

public class Intents {
    private Intents() {
    }

    public static final class Requests
    {
        private Requests() {
        }
        public static final int GOOGLE_CONNECTION = 1;
        public static final int DRIVE_FILE_CREATE = 2;
        public static final int DRIVE_FILE_OPEN = 3;
    }

    private static final class UriMasks
    {
        private UriMasks() {
        }

        public static final String GOOGLE_PLAY_APP = "market://details?id=%s";
        public static final String GOOGLE_PLAY_WEB = "https://play.google.com/store/apps/details?id=%s";
    }

    public static final class Builder
    {
        private final Context context;

        public static Builder with(@NonNull Context context) {
            return new Builder(context);
        }

        private Builder(Context context) {
            this.context = context.getApplicationContext();
        }

        public Intent buildBackupIntent() {
            return new Intent(context, BackupActivity.class);
        }

        public Intent buildGooglePlayAppIntent() {
            String googlePlayUri = String.format(UriMasks.GOOGLE_PLAY_APP, Android.getApplicationId());

            return new Intent(Intent.ACTION_VIEW, Uri.parse(googlePlayUri));
        }

        public Intent buildGooglePlayWebIntent() {
            String googlePlayUri = String.format(UriMasks.GOOGLE_PLAY_WEB, Android.getApplicationId());

            return new Intent(Intent.ACTION_VIEW, Uri.parse(googlePlayUri));
        }
    }
}
