package com.lovejoy777.boatlog;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
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
import com.google.android.gms.drive.OpenFileActivityBuilder;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import com.lovejoy777.boatlog.backup.BackupFilePicker;
import com.lovejoy777.boatlog.backup.BackupOperator;
import com.lovejoy777.boatlog.util.GoogleServices;
import com.lovejoy777.boatlog.util.Intents;

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

public class BackupActivity extends AppCompatActivity implements ResultCallback<DriveApi.DriveContentsResult>,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener
{

     BackupOperator backupOperator;

    private static enum BackupAction
    {
        EXPORT, IMPORT, NONE
    }

    Button button_backup;
    Button button_restore;

    private String FOLDER_NAME = "BoatLog_Backups";
    String app_database_path = "/data/data/com.lovejoy777.boatlog/databases/SQLiteBoatLog.db";

    private GoogleApiClient googleApiClient;
    private BackupAction backupAction;

    RelativeLayout MRL1;
    RelativeLayout RL1;
    ImageView image_drive;
    TextView tv_googleDrive;
    TextView tv_Version;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_backup);

        MRL1 = (RelativeLayout) findViewById(R.id.MRL1);
        RL1 = (RelativeLayout) findViewById(R.id.RL1);
        image_drive = (ImageView) findViewById(R.id.image_drive);
        tv_googleDrive = (TextView) findViewById(R.id.tv_googleDrive);
        tv_Version = (TextView) findViewById(R.id.tv_Version);

        button_backup = (Button) findViewById(R.id.backup);
        button_restore = (Button) findViewById(R.id.restore);

        //set Toolbar
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar4);
        toolbar.setNavigationIcon(R.drawable.ic_action_bak);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        SharedPreferences myPrefs = this.getSharedPreferences("myPrefs", MODE_PRIVATE);
        Boolean NightModeOn = myPrefs.getBoolean("switch1", false);

        if (NightModeOn) {
            NightMode();
        }

        //app version textView
        TextView tv_version = (TextView) findViewById(R.id.tv_Version);
        try {
            String versionName = BackupActivity.this.getPackageManager()
                    .getPackageInfo(BackupActivity.this.getPackageName(), 0).versionName;
            tv_version.setText("Version " + versionName + "");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        button_backup.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                startBackupAction(BackupAction.EXPORT);
            }
        });

        button_restore.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                startBackupAction(BackupAction.IMPORT);
            }
        });
    }

    public void startBackupAction(BackupAction backupAction) {
        this.backupAction = backupAction;
        setUpGoogleApiClient();
        setUpGoogleApiConnection();
    }

    private void setUpGoogleApiClient() {
        this.googleApiClient = buildGoogleApiClient();
    }
    private GoogleApiClient buildGoogleApiClient() {
        return new GoogleApiClient.Builder(this)
                .addApi(Drive.API)
                .addScope(Drive.SCOPE_FILE)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    private void setUpGoogleApiConnection() {
        googleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        startBackupAction();
    }
    private void startBackupAction() {

        //showProgress();
        startFilesSync();

        switch (backupAction) {
            case EXPORT:
                // CREATE PDF WITH IMAGE BACKGROUND TASK WITH PROGRESS SPINNER


               // BackgroundTaskPDFimage();
                BackgroundTaskBackup taskBackup = new BackgroundTaskBackup(BackupActivity.this);
                taskBackup.execute();

              //  startBackupFileCreation();
                break;
            case IMPORT:
                startBackupFileOpening();
                break;
            default:
                break;
        }
    }

    private class BackgroundTaskBackup extends AsyncTask<Void, Void, Void> {
        private ProgressDialog dialog;

        public BackgroundTaskBackup(BackupActivity activity) {
            dialog = new ProgressDialog(activity, R.style.AlertDialogTheme);
        }

        @Override
        protected void onPreExecute() {
            dialog.setTitle("    Backing up");
            dialog.setMessage("     To Google Drive");
            dialog.setIcon(R.drawable.ic_cloud_upload_white_48dp);
            dialog.show();
        }

        @Override
        protected void onPostExecute(Void result) {
            if (dialog.isShowing()) {
                dialog.dismiss();
                finishBackupAction();
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            startBackupFileCreation();
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private void startFilesSync() {
        Drive.DriveApi.requestSync(googleApiClient).setResultCallback(null);
    }

    private void startBackupFileCreation() {
        Drive.DriveApi.newDriveContents(googleApiClient).setResultCallback(this);
    }

    @Override
    public void onResult(DriveApi.DriveContentsResult contentsResult) {
        continueBackupFileCreation(contentsResult.getDriveContents());
    }

    private void continueBackupFileCreation(DriveContents backupFileContents) {
        exportBackup();
    }

    private void startBackupFileOpening() {
        BackupFilePicker.with(this, googleApiClient).startBackupFileOpening();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            finishBackupAction();
            return;
        }

        if (requestCode == Intents.Requests.GOOGLE_CONNECTION) {
            setUpGoogleApiConnection();
        }

        //if (requestCode == Intents.Requests.DRIVE_FILE_CREATE) {
        //    DriveId backupFileId = data.getParcelableExtra(OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);
        //    startBackupExporting(backupFileId);
        //}

        if (requestCode == Intents.Requests.DRIVE_FILE_OPEN) {
            DriveId backupFileId = data.getParcelableExtra(OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);
            startBackupRestoring(backupFileId);
        }
    }

    private void finishBackupAction() {
        this.backupAction = BackupAction.NONE;
        //hideProgress();
        //showUpdatedContents();
        //tearDownGoogleApiConnection();
        onBackPressed();
    }


    private void showUpdatedContents() {


        //Toast.makeText(getApplicationContext(), "Finished Restore", Toast.LENGTH_SHORT).show();
        //getContentResolver().notifyChange(GambitContract.Decks.getDecksUri(), null);
    }

    // RESTORE
    public void restoreBackup(@NonNull DriveId backupFileId) {
        DriveFile backupFile = backupFileId.asDriveFile();
        DriveContents backupFileContents = backupFile.open(googleApiClient, DriveFile.MODE_READ_ONLY, null).await().getDriveContents();

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

    private void tearDownGoogleApiConnection() {
        if (isGoogleApiClientConnected()) {
            googleApiClient.disconnect();
        }
    }

    private boolean isGoogleApiClientConnected() {
        return (googleApiClient != null) && (googleApiClient.isConnecting() || googleApiClient.isConnected());
    }


    private void startBackupRestoring(DriveId backupFileId) {

        BackgroundTaskRestore taskRestore = new BackgroundTaskRestore(BackupActivity.this, backupFileId);
        taskRestore.execute();
        //Toast.makeText(getApplicationContext(), "Finished Restore", Toast.LENGTH_SHORT).show();
        //BackupRestoringTask.execute(BackupOperator.with(this, googleApiClient), backupFileId);
    }

    private class BackgroundTaskRestore extends AsyncTask<DriveId, Void, DriveId> {
        private ProgressDialog dialog;

        DriveId backupFileId;
        public BackgroundTaskRestore(BackupActivity activity, DriveId backupFileId) {
            dialog = new ProgressDialog(activity, R.style.AlertDialogTheme);
            this.backupFileId = backupFileId;
        }

        @Override
        protected void onPreExecute() {
            dialog.setTitle("    Restoring");
            dialog.setMessage("     Database");
            dialog.setIcon(R.drawable.ic_cloud_download_white_48dp);
            dialog.show();
        }

        protected void onPostExecute(DriveId backupFileId) {
            if (dialog.isShowing()) {
                dialog.dismiss();
                //finishBackupAction();

            }
        }

        @Override
        protected DriveId doInBackground(DriveId... params) {
            DriveId newbackupFileId = backupFileId;
            restoreBackup(backupFileId);
            //backupOperator.restoreBackup(backupFileId);
           // BackupRestoringTask.execute(BackupOperator.with(BackupActivity.this, googleApiClient), newbackupFileId);
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return newbackupFileId;
        }


    }


    @Override
    public void onConnectionSuspended(int cause) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        GoogleServices.with(this).resolve(connectionResult);
    }

    // BACKUP
    public void exportBackup() {
        if (googleApiClient != null) {
            check_folder_exists();
        } else {
            Toast.makeText(getApplicationContext(), "Error Couldnt connect to Google", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Could not connect to google drive manager");
        }
    }

    // BACKUP
    private void check_folder_exists() {
        Query query =
                new Query.Builder().addFilter(Filters.and(Filters.eq(SearchableField.TITLE, FOLDER_NAME), Filters.eq(SearchableField.TRASHED, false)))
                        .build();
        Drive.DriveApi.query(googleApiClient, query).setResultCallback(new ResultCallback<DriveApi.MetadataBufferResult>() {
            @Override public void onResult(DriveApi.MetadataBufferResult result) {
                if (!result.getStatus().isSuccess()) {
                    Toast.makeText(getApplicationContext(), "Error Creating Folder in Drive", Toast.LENGTH_SHORT).show();
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
                        Drive.DriveApi.getRootFolder(googleApiClient)
                                .createFolder(googleApiClient, changeSet)
                                .setResultCallback(new ResultCallback<DriveFolder.DriveFolderResult>() {
                                    @Override public void onResult(DriveFolder.DriveFolderResult result) {
                                        if (!result.getStatus().isSuccess()) {
                                            Toast.makeText(getApplicationContext(), "Error Creating Folder on Drive", Toast.LENGTH_SHORT).show();
                                            Log.e(TAG, "Error while creating the folder");
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

        Drive.DriveApi.newDriveContents(googleApiClient).setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {
            @Override
            public void onResult(@NonNull DriveApi.DriveContentsResult driveContentsResult) {
                if (!driveContentsResult.getStatus().isSuccess()) {
                    Log.e(TAG, "Error while trying to create new file contents");
                    return;
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

                OutputStream outputStream = driveContentsResult.getDriveContents().getOutputStream();

                final File theFile = new File(app_database_path); //>>>>>> WHAT FILE ?
                try {
                    FileInputStream fileInputStream = new FileInputStream(theFile);
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                    outputStream.close();
                    fileInputStream.close();
                } catch (IOException e1) {
                    Toast.makeText(getApplicationContext(), "Error Writing Backup", Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "Unable to write file contents.");
                }

                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder().setTitle(backupFileName).setMimeType("application/x-sqlite3").setStarred(false).build();
                 DriveFolder folder = driveId.asDriveFolder();
                 folder.createFile(googleApiClient, changeSet, driveContentsResult.getDriveContents())
                 .setResultCallback(new ResultCallback<DriveFolder.DriveFileResult>() {
                @Override public void onResult(@NonNull DriveFolder.DriveFileResult driveFileResult) {
                if (!driveFileResult.getStatus().isSuccess()) {
                    Toast.makeText(getApplicationContext(), "Error Creating Backup", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error while trying to create the file");
                return;
                }
                    //Toast.makeText(getApplicationContext(), "Backup Exported to Drive", Toast.LENGTH_SHORT).show();
                    Log.v(TAG, "Created a file: " + driveFileResult.getDriveFile().getDriveId());
                }
                });
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                navigateUp();
                return true;

            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    private void navigateUp() {
        NavUtils.navigateUpFromSameTask(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tearDownGoogleApiConnection();
    }

    private void NightMode() {
        MRL1.setBackgroundResource(R.color.card_background);
        RL1.setBackgroundResource(R.color.card_background);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            tv_googleDrive.setTextColor(getBaseContext().getResources().getColor(R.color.night_text, getBaseContext().getTheme()));
           // tv_Version.setTextColor(getBaseContext().getResources().getColor(R.color.night_text, getBaseContext().getTheme()));

        }else {
            tv_googleDrive.setTextColor(getResources().getColor(R.color.night_text));
           // tv_Version.setTextColor(getResources().getColor(R.color.night_text));
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.back2, R.anim.back1);
        tearDownGoogleApiConnection();
    }
}