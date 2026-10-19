package com.lovejoy777.boatlog;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import androidx.core.app.NavUtils;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BackupActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_CREATE_BACKUP = 1001;
    private static final int REQUEST_CODE_OPEN_BACKUP = 1002;

    Button button_backup;
    Button button_restore;

    RelativeLayout MRL1;
    RelativeLayout RL1;
    ImageView image_drive;
    TextView tv_googleDrive;
    TextView tv_Version;
    TextView tv_drive_info;
    TextView tv_no_connection;

    int theme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initialize the associated SharedPreferences file with default values
        PreferenceManager.setDefaultValues(this, R.xml.prefs, false);
        SharedPreferences prefs1 = PreferenceManager.getDefaultSharedPreferences(this);
        setTheme(theme = getTheme(prefs1.getString("theme", "fresh")));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_backup);

        MRL1 = (RelativeLayout) findViewById(R.id.MRL1);
        RL1 = (RelativeLayout) findViewById(R.id.RL1);
        image_drive = (ImageView) findViewById(R.id.image_drive);
        tv_googleDrive = (TextView) findViewById(R.id.tv_googleDrive);
        tv_Version = (TextView) findViewById(R.id.tv_Version);
        tv_drive_info = (TextView) findViewById(R.id.tv_drive_info);
        tv_no_connection = (TextView) findViewById(R.id.tv_no_connection);

        button_backup = (Button) findViewById(R.id.backup);
        button_restore = (Button) findViewById(R.id.restore);

        if (button_backup != null) {
            button_backup.setText("Backup Database");
        }
        if (button_restore != null) {
            button_restore.setText("Restore Database");
        }

        if (tv_drive_info != null) {
            tv_drive_info.setText("Backup and restore your BoatLog database to /Documents/boatlog/backups/ or any location.");
        }
        if (tv_no_connection != null) {
            tv_no_connection.setVisibility(View.GONE);
        }
        if (image_drive != null) {
            image_drive.setVisibility(View.VISIBLE);
        }

        //set Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar4);
        if (toolbar != null) {
            toolbar.setNavigationIcon(R.drawable.ic_action_bak);
            setSupportActionBar(toolbar);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }

        //app version textView
        try {
            String versionName = getPackageManager()
                    .getPackageInfo(getPackageName(), 0).versionName;
            if (tv_Version != null) {
                tv_Version.setText("Version " + versionName);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        // BACKUP BUTTON
        if (button_backup != null) {
            button_backup.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    startSystemBackupPicker();
                }
            });
        }

        // RESTORE BUTTON
        if (button_restore != null) {
            button_restore.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    startSystemRestorePicker();
                }
            });
        }
    }

    private void startSystemBackupPicker() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String dateStr = df.format(new Date());
        intent.putExtra(Intent.EXTRA_TITLE, "BoatLog_Backup_" + dateStr + ".db");

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            File backupDir = getBackupDirectory();
            if (backupDir != null && backupDir.exists()) {
                intent.putExtra(android.provider.DocumentsContract.EXTRA_INITIAL_URI, Uri.fromFile(backupDir));
            }
        }

        try {
            startActivityForResult(intent, REQUEST_CODE_CREATE_BACKUP);
        } catch (Exception e) {
            Log.e("BackupActivity", "System document picker unavailable", e);
            Toast.makeText(this, "Unable to open File Picker: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void startSystemRestorePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            File backupDir = getBackupDirectory();
            if (backupDir != null && backupDir.exists()) {
                intent.putExtra(android.provider.DocumentsContract.EXTRA_INITIAL_URI, Uri.fromFile(backupDir));
            }
        }

        try {
            startActivityForResult(intent, REQUEST_CODE_OPEN_BACKUP);
        } catch (Exception e) {
            Log.e("BackupActivity", "System document picker unavailable", e);
            Toast.makeText(this, "Unable to open file picker: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK || data == null || data.getData() == null) {
            return;
        }

        Uri uri = data.getData();

        if (requestCode == REQUEST_CODE_CREATE_BACKUP) {
            performBackupToUri(uri);
        } else if (requestCode == REQUEST_CODE_OPEN_BACKUP) {
            performRestoreFromUri(uri);
        }
    }

    private void performBackupToUri(Uri targetUri) {
        try {
            BoatLogDBHelper dbHelper = new BoatLogDBHelper(this);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            if (db != null && db.isOpen()) {
                db.close();
            }

            File dbFile = getDatabasePath("SQLiteBoatLog.db");
            if (!dbFile.exists()) {
                Toast.makeText(this, "No database found to backup!", Toast.LENGTH_SHORT).show();
                return;
            }

            try (InputStream in = new FileInputStream(dbFile);
                 OutputStream out = getContentResolver().openOutputStream(targetUri)) {
                if (out == null) {
                    throw new IOException("Cannot open output stream for URI: " + targetUri);
                }
                byte[] buffer = new byte[4096];
                int length;
                while ((length = in.read(buffer)) > 0) {
                    out.write(buffer, 0, length);
                }
                out.flush();
            }

            Toast.makeText(this, "Backup Saved Successfully!", Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            Log.e("BackupActivity", "Error backing up database", e);
            Toast.makeText(this, "Backup Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void performRestoreFromUri(Uri sourceUri) {
        try {
            BoatLogDBHelper dbHelper = new BoatLogDBHelper(this);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            if (db != null && db.isOpen()) {
                db.close();
            }

            File dbFile = getDatabasePath("SQLiteBoatLog.db");
            File dbWal = new File(dbFile.getPath() + "-wal");
            File dbShm = new File(dbFile.getPath() + "-shm");

            if (dbWal.exists()) dbWal.delete();
            if (dbShm.exists()) dbShm.delete();

            try (InputStream in = getContentResolver().openInputStream(sourceUri);
                 OutputStream out = new FileOutputStream(dbFile)) {
                if (in == null) {
                    throw new IOException("Cannot open input stream for URI: " + sourceUri);
                }
                byte[] buffer = new byte[4096];
                int length;
                while ((length = in.read(buffer)) > 0) {
                    out.write(buffer, 0, length);
                }
                out.flush();
            }

            Toast.makeText(this, "Database Restored Successfully!", Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            Log.e("BackupActivity", "Error restoring database", e);
            Toast.makeText(this, "Restore Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private File getBackupDirectory() {
        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "boatLog/backups");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        if (!dir.exists()) {
            dir = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "backups");
            if (!dir.exists()) {
                dir.mkdirs();
            }
        }
        return dir;
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

    private int getTheme(String themePref) {
        switch (themePref) {
            case "dark":
                return R.style.AppTheme_NoActionBar_Dark;
            default:
                return R.style.AppTheme_NoActionBar;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.back2, R.anim.back1);
    }
}
