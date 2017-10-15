package com.lovejoy777.boatlog.backup;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static android.content.ContentValues.TAG;

/**
 * Created by steve on 14/10/17.
 */

public class BackupOperator {

    private String FOLDER_NAME = "BoatLog_Backups";
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

    // BACKUP
    public void exportBackup(@NonNull DriveId backupFileId) {
       writeToFile(backupFileId);
    }

    // RESTORE
    public void importBackup(@NonNull DriveId backupFileId) {
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
            fileOutput.close();
            inputstream.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
      //  backupFileContents.commit(driveApiClient, null).await();
    }

    // BACKUP
    public void writeToFile(@NonNull DriveId backupFileId) {
        if (driveApiClient != null) {
            check_folder_exists(backupFileId);
        } else {
            Log.e(TAG, "Could not connect to google drive manager");
        }
    }

    // BACKUP
    private void check_folder_exists(@NonNull DriveId backupFileId) {
        Query query =
                new Query.Builder().addFilter(Filters.and(Filters.eq(SearchableField.TITLE, FOLDER_NAME), Filters.eq(SearchableField.TRASHED, false)))
                        .build();
        Drive.DriveApi.query(driveApiClient, query).setResultCallback(new ResultCallback<DriveApi.MetadataBufferResult>() {
            @Override public void onResult(DriveApi.MetadataBufferResult result) {
                if (!result.getStatus().isSuccess()) {
                    Log.e(TAG, "Cannot create folder in the root.");
                } else {
                    boolean isFound = false;
                    for (Metadata m : result.getMetadataBuffer()) {
                        if (m.getTitle().equals(FOLDER_NAME)) {
                            Log.e(TAG, "Folder exists");
                            isFound = true;
                            DriveId driveId = m.getDriveId();
                            create_file_in_folder(driveId);
                            break;
                        }
                    }
                    if (!isFound) {
                        Log.i(TAG, "Folder not found; creating it.");
                        MetadataChangeSet changeSet = new MetadataChangeSet.Builder().setTitle(FOLDER_NAME).build();
                        Drive.DriveApi.getRootFolder(driveApiClient)
                                .createFolder(driveApiClient, changeSet)
                                .setResultCallback(new ResultCallback<DriveFolder.DriveFolderResult>() {
                                    @Override public void onResult(DriveFolder.DriveFolderResult result) {
                                        if (!result.getStatus().isSuccess()) {
                                            Log.e(TAG, "Error while trying to create the folder");
                                        } else {
                                            Log.i(TAG, "Created a folder");
                                            DriveId driveId = result.getDriveFolder().getDriveId();
                                            create_file_in_folder(driveId);
                                        }
                                    }
                                });
                    }
                }
            }
        });
    }

    // BACKUP
    private void create_file_in_folder(final DriveId driveId) {

                //------ THIS IS AN EXAMPLE FOR PICTURE ------
                //ByteArrayOutputStream bitmapStream = new ByteArrayOutputStream();
                //image.compress(Bitmap.CompressFormat.PNG, 100, bitmapStream);
                //try {
                //  outputStream.write(bitmapStream.toByteArray());
                //} catch (IOException e1) {
                //  Log.i(TAG, "Unable to write file contents.");
                //}
                //// Create the initial metadata - MIME type and title.
                //// Note that the user will be able to change the title later.
                //MetadataChangeSet metadataChangeSet = new MetadataChangeSet.Builder()
                //    .setMimeType("image/jpeg").setTitle("Android Photo.png").build();
                //------ THIS IS AN EXAMPLE FOR FILE --------
        // Toast.makeText(BackupOperator.this, "Uploading to drive. If you didn't fucked up something like usual you should see it there", Toast.LENGTH_LONG).show();

        Drive.DriveApi.newDriveContents(driveApiClient).setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {
            @Override public void onResult(@NonNull DriveApi.DriveContentsResult driveContentsResult) {
                if (!driveContentsResult.getStatus().isSuccess()) {
                    Log.e(TAG, "Error while trying to create new file contents");
                    return;
                }
        OutputStream outputStream = driveContentsResult.getDriveContents().getOutputStream();

                final File theFile = new File(app_database_path); //>>>>>> WHAT FILE ?
                try {
                    FileInputStream fileInputStream = new FileInputStream(theFile);
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                } catch (IOException e1) {
                    Log.i(TAG, "Unable to write file contents.");
                }

                // Get Time and Date
                Calendar c = Calendar.getInstance();
                System.out.println("Current time => " + c.getTime());
                SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.UK);
                String formattedDate = df.format(c.getTime());
                SimpleDateFormat dt = new SimpleDateFormat("HH:mm", Locale.UK);
                String formattedTime = dt.format(c.getTime());
                String fileDate = "_" + formattedDate;
                String fileTime = "_" + formattedTime;

                String backupFileName = "SQLiteBoatLog" + fileDate + fileTime + ".db";

                MetadataChangeSet changeSet = new MetadataChangeSet.Builder().setTitle(backupFileName).setMimeType("application/x-sqlite3").setStarred(false).build();
                DriveFolder folder = driveId.asDriveFolder();
                folder.createFile(driveApiClient, changeSet, driveContentsResult.getDriveContents())
                        .setResultCallback(new ResultCallback<DriveFolder.DriveFileResult>() {
                            @Override public void onResult(@NonNull DriveFolder.DriveFileResult driveFileResult) {
                                if (!driveFileResult.getStatus().isSuccess()) {
                                    Log.e(TAG, "Error while trying to create the file");
                                    return;
                                }
                                Log.v(TAG, "Created a file: " + driveFileResult.getDriveFile().getDriveId());
                            }
                        });
            }
        });
    }
}
