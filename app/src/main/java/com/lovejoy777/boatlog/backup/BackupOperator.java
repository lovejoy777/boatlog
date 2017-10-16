package com.lovejoy777.boatlog.backup;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by steve on 14/10/17.
 */

public class BackupOperator {

    String app_database_path = "/data/data/com.lovejoy777.boatlog/databases/SQLiteBoatLog.db";

    private final Context context;
    private final GoogleApiClient driveApiClient;

    public static BackupOperator with(@NonNull Context context, @NonNull GoogleApiClient driveApiClient) {
        return new BackupOperator(context, driveApiClient);
    }

    private BackupOperator(Context context, GoogleApiClient driveApiClient) {
        this.context = context.getApplicationContext();
        this.driveApiClient = driveApiClient;
    }
    // RESTORE
    public void restoreBackup(@NonNull DriveId backupFileId) {
        DriveFile backupFile = backupFileId.asDriveFile();
        DriveContents backupFileContents = backupFile.open(driveApiClient, DriveFile.MODE_READ_ONLY, null).await().getDriveContents();

        // FROM PATH
        InputStream inputstream = backupFileContents.getInputStream();
        // TO PATH
        File driveOutFile = new File(app_database_path);
        try {
            FileOutputStream fileOutput = new FileOutputStream(driveOutFile);
            byte[] buffer = new byte[1024];
            int bufferLength;
            while ((bufferLength = inputstream.read(buffer)) != -1) {
                fileOutput.write(buffer, 0, bufferLength);
            }

           // Toast.makeText(this.context, "Restore Complete", Toast.LENGTH_SHORT).show();
            fileOutput.close();
            inputstream.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //  backupFileContents.commit(driveApiClient, null).await();
    }
}
