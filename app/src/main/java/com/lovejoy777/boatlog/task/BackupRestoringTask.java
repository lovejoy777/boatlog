package com.lovejoy777.boatlog.task;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.google.android.gms.drive.DriveId;
import com.lovejoy777.boatlog.backup.BackupOperator;

/**
 * Created by steve on 14/10/17.
 */

public class BackupRestoringTask extends AsyncTask<Void, Void, Void>
{
    private final BackupOperator backupOperator;
    private final DriveId backupFileId;

    public static void execute(@NonNull BackupOperator backupOperator, @NonNull DriveId backupFileId) {
        new BackupRestoringTask(backupOperator, backupFileId).execute();
    }

    private BackupRestoringTask(BackupOperator backupOperator, DriveId backupFileId) {
        this.backupOperator = backupOperator;
        this.backupFileId = backupFileId;
    }

    @Override
    protected Void doInBackground(Void... parameters) {
        backupOperator.restoreBackup(backupFileId);
        return null;
    }
}
