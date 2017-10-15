package com.lovejoy777.boatlog.task;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.google.android.gms.drive.DriveId;
import com.lovejoy777.boatlog.backup.BackupOperator;

/**
 * Created by steve on 15/10/17.
 */

public class BackupExportingTask extends AsyncTask<Void, Void, Void>
{
    private final BackupOperator backupOperator;

    private final DriveId backupFileId;

    public static void execute(@NonNull BackupOperator backupOperator, @NonNull DriveId backupFileId) {
        new BackupExportingTask(backupOperator, backupFileId).execute();
    }

    private BackupExportingTask(BackupOperator backupOperator, DriveId backupFileId) {
        this.backupOperator = backupOperator;
        this.backupFileId = backupFileId;
    }

    @Override
    protected Void doInBackground(Void... parameters) {
        backupOperator.exportBackup(backupFileId);
        return null;
    }
}