package com.lovejoy777.boatlog.backup;

import android.app.Activity;
import android.content.IntentSender;
import android.support.annotation.NonNull;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.MetadataChangeSet;
import com.lovejoy777.boatlog.R;
import com.lovejoy777.boatlog.util.Intents;

/**
 * Created by steve on 14/10/17.
 */

public class BackupFilePicker {
    private static final String BACKUP_MIME_TYPE = "application/x-sqlite3";

    private final Activity activity;
    private final GoogleApiClient driveApiClient;

    public static BackupFilePicker with(@NonNull Activity activity, @NonNull GoogleApiClient driveApiClient) {
        return new BackupFilePicker(activity, driveApiClient);
    }

    private BackupFilePicker(Activity activity, GoogleApiClient driveApiClient) {
        this.activity = activity;
        this.driveApiClient = driveApiClient;
    }

    public void startBackupFileCreation(@NonNull DriveContents fileContents) {
        try {
            IntentSender intentSender = buildBackupFileCreationIntentSender(fileContents);

            activity.startIntentSenderForResult(intentSender, Intents.Requests.DRIVE_FILE_CREATE, null, 0, 0, 0);
        } catch (IntentSender.SendIntentException e) {
            throw new RuntimeException(e);
        }
    }

    private IntentSender buildBackupFileCreationIntentSender(DriveContents fileContents) {
        MetadataChangeSet fileMetadata = new MetadataChangeSet.Builder()
                .setTitle(activity.getString(R.string.name_backup))
                .setMimeType(BACKUP_MIME_TYPE)
                .build();

        return Drive.DriveApi.newCreateFileActivityBuilder()
                .setInitialMetadata(fileMetadata)
                .setInitialDriveContents(fileContents)
                .build(driveApiClient);
    }

    public void startBackupFileOpening() {
        try {
            IntentSender intentSender = buildBackupFileOpeningIntentSender();

            activity.startIntentSenderForResult(intentSender, Intents.Requests.DRIVE_FILE_OPEN, null, 0, 0, 0);
        } catch (IntentSender.SendIntentException e) {
            throw new RuntimeException(e);
        }
    }

    private IntentSender buildBackupFileOpeningIntentSender() {
        return Drive.DriveApi.newOpenFileActivityBuilder()
                .setMimeType(new String[]{BACKUP_MIME_TYPE})
                .build(driveApiClient);
    }
}
