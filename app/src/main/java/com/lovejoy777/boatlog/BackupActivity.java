package com.lovejoy777.boatlog;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.OpenFileActivityBuilder;
import com.lovejoy777.boatlog.backup.BackupFilePicker;
import com.lovejoy777.boatlog.backup.BackupOperator;
import com.lovejoy777.boatlog.task.BackupExportingTask;
import com.lovejoy777.boatlog.task.BackupImportingTask;
import com.lovejoy777.boatlog.util.GoogleServices;
import com.lovejoy777.boatlog.util.Intents;

/**
 * Created by steve on 14/10/17.
 */

public class BackupActivity extends AppCompatActivity implements ResultCallback<DriveApi.DriveContentsResult>,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener
{

    Button button_backup;
    Button button_restore;

    private static enum BackupAction
    {
        EXPORT, IMPORT, NONE
    }

    private GoogleApiClient googleApiClient;
    private BackupAction backupAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_backup);

        button_backup = (Button) findViewById(R.id.backup);
        button_restore = (Button) findViewById(R.id.restore);

        button_backup.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
               // Toast.makeText(BackupActivity.this, "Btn 1 clicked", Toast.LENGTH_LONG).show();
                startBackupAction(BackupAction.EXPORT);

            }
        });

        button_restore.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                startBackupAction(BackupAction.IMPORT);

            }
        });
    }

    private void startBackupAction(BackupAction backupAction) {
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

        startFilesSync();

        switch (backupAction) {
            case EXPORT:
                startBackupFileCreation();
                break;

            case IMPORT:
                startBackupFileOpening();
                break;

            default:
                break;
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
        BackupFilePicker.with(this, googleApiClient).startBackupFileCreation(backupFileContents);
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

        if (requestCode == Intents.Requests.DRIVE_FILE_CREATE) {
            DriveId backupFileId = data.getParcelableExtra(OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);
            startBackupExporting(backupFileId);
        }

        if (requestCode == Intents.Requests.DRIVE_FILE_OPEN) {
            DriveId backupFileId = data.getParcelableExtra(OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);
            startBackupImporting(backupFileId);
        }
    }

    private void finishBackupAction() {
        this.backupAction = BackupAction.NONE;

        showUpdatedContents();
        tearDownGoogleApiConnection();
    }


    private void showUpdatedContents() {
        //getContentResolver().notifyChange(GambitContract.Decks.getDecksUri(), null);
    }

    private void tearDownGoogleApiConnection() {
        if (isGoogleApiClientConnected()) {
            googleApiClient.disconnect();
        }
    }

    private boolean isGoogleApiClientConnected() {
        return (googleApiClient != null) && (googleApiClient.isConnecting() || googleApiClient.isConnected());
    }

    private void startBackupExporting(DriveId backupFileId) {
        BackupExportingTask.execute(BackupOperator.with(this, googleApiClient), backupFileId);
    }

    private void startBackupImporting(DriveId backupFileId) {
        BackupImportingTask.execute(BackupOperator.with(this, googleApiClient), backupFileId);
    }

    @Override
    public void onConnectionSuspended(int cause) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        GoogleServices.with(this).resolve(connectionResult);
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
}