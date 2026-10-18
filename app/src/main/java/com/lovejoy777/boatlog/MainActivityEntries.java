package com.lovejoy777.boatlog;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by lovejoy777 on 03/10/15.
 */
public class MainActivityEntries extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;

    public final static String KEY_EXTRA_ENTRIES_ID = "KEY_EXTRA_ENTRIES_ID";
    public final static String KEY_EXTRA_ENTRY_NAME = "KEY_EXTRA_ENTRY_NAME";
    public final static String KEY_EXTRA_TRIPS_ID = "KEY_EXTRA_TRIPS_ID";
    public final static String KEY_EXTRA_TRIPS_NAME = "KEY_EXTRA_TRIPS_NAME";

    private static int RESULT_LOAD_IMG = 1;

    private ListView listView;
    BoatLogDBHelper dbHelper;

    ImageView button_createNewEntry;

    RelativeLayout MRL1;

    ListView listViewEntries;

    int tripID;
    String tripName;
    String entryName;
    String[] mFileList;

    int theme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initialize the associated SharedPreferences file with default values
        PreferenceManager.setDefaultValues(this, R.xml.prefs, false);
        SharedPreferences prefs1 = PreferenceManager.getDefaultSharedPreferences(this);
        setTheme(theme = getTheme(prefs1.getString("theme", "fresh")));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_entries);

        tripID = getIntent().getIntExtra(MainActivityTrips.KEY_EXTRA_TRIPS_ID, 0);
        tripName = getIntent().getStringExtra(MainActivityTrips.KEY_EXTRA_TRIPS_NAME);
        tripID = getIntent().getIntExtra(CreateEntriesActivity.KEY_EXTRA_TRIPS_ID, 0);
        tripName = getIntent().getStringExtra(CreateEntriesActivity.KEY_EXTRA_TRIPS_NAME);
        tripID = getIntent().getIntExtra(EditEntriesActivity.KEY_EXTRA_TRIPS_ID, 0);
        tripName = getIntent().getStringExtra(EditEntriesActivity.KEY_EXTRA_TRIPS_NAME);

        loadToolbarNavDrawer(tripName);
        button_createNewEntry = (ImageView) findViewById(R.id.button_createNewEntry);
        TypedArray ta = obtainStyledAttributes(new int[]{R.attr.colorLightTextPrimary});
        button_createNewEntry.setColorFilter(ta.getColor(0, Color.WHITE), PorterDuff.Mode.SRC_ATOP);
        ta.recycle();

        MRL1 = (RelativeLayout) findViewById(R.id.MRL1);
        listViewEntries = (ListView) findViewById(R.id.listViewEntries);

        dbHelper = new BoatLogDBHelper(this);

        button_createNewEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createEntry();
            }
        });

        populateListView();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> listView, View view,
                                    int position, long id) {
                Cursor itemCursor = (Cursor) MainActivityEntries.this.listView.getItemAtPosition(position);
                int entryID = itemCursor.getInt(itemCursor.getColumnIndex(BoatLogDBHelper.ENTRY_COLUMN_ID));
                String entryName = itemCursor.getString(itemCursor.getColumnIndex(BoatLogDBHelper.ENTRY_COLUMN_NAME));

                Intent intent = new Intent(getApplicationContext(), EditEntriesActivity.class);
                intent.putExtra(KEY_EXTRA_ENTRIES_ID, entryID);
                intent.putExtra(KEY_EXTRA_ENTRY_NAME, entryName);
                intent.putExtra(KEY_EXTRA_TRIPS_ID, tripID);
                intent.putExtra(KEY_EXTRA_TRIPS_NAME, tripName);

                Bundle bndlanimation =
                        ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.anni1, R.anim.anni2).toBundle();
                startActivity(intent, bndlanimation);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> listView, View view,
                                           int position, long id) {
                Cursor itemCursor = (Cursor) MainActivityEntries.this.listView.getItemAtPosition(position);
                int entryID = itemCursor.getInt(itemCursor.getColumnIndex(BoatLogDBHelper.ENTRY_COLUMN_ID));
                String entryName = itemCursor.getString(itemCursor.getColumnIndex(BoatLogDBHelper.ENTRY_COLUMN_NAME));

                Intent intent = new Intent(getApplicationContext(), EditEntriesActivity.class);
                intent.putExtra(KEY_EXTRA_ENTRIES_ID, entryID);
                intent.putExtra(KEY_EXTRA_ENTRY_NAME, entryName);
                intent.putExtra(KEY_EXTRA_TRIPS_ID, tripID);
                intent.putExtra(KEY_EXTRA_TRIPS_NAME, tripName);

                Bundle bndlanimation =
                        ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.anni1, R.anim.anni2).toBundle();
                startActivity(intent, bndlanimation);
                return true;
            }
        });
    }

    private void populateListView() {
        final Cursor cursor = dbHelper.getTripEntry(tripID);
        String[] columns = new String[]{
                BoatLogDBHelper.ENTRY_COLUMN_ID,
                BoatLogDBHelper.ENTRY_COLUMN_NAME,
                BoatLogDBHelper.ENTRY_COLUMN_TIME,
                BoatLogDBHelper.ENTRY_COLUMN_DATE,
                BoatLogDBHelper.ENTRY_COLUMN_TRIP_ID

        };
        int[] widgets = new int[]{
                R.id.entryID,
                R.id.entryName,
                R.id.entryTime,
                R.id.entryDate
        };

        SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(this, R.layout.entries_info,
                cursor, columns, widgets, 0);
        listView = (ListView) findViewById(R.id.listViewEntries);
        listView.setDividerHeight(2);
        listView.setAdapter(cursorAdapter);
    }

    public void loadImagefromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        galleryIntent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK && data != null) {
                Uri selectedImage = data.getData();
                if (selectedImage != null) {
                    BackgroundTaskPDF taskPDFimage = new BackgroundTaskPDF(MainActivityEntries.this, selectedImage);
                    taskPDFimage.execute();
                } else {
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error picking image", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean generatePdf(Uri imageUri) {
        Cursor rs = null;
        try {
            rs = dbHelper.getTrip(tripID);
            String rawTripName = "Trip_" + tripID;
            String tripDeparture = "";
            String tripDestination = "";
            if (rs != null && rs.moveToFirst()) {
                rawTripName = rs.getString(rs.getColumnIndex(BoatLogDBHelper.TRIPS_COLUMN_NAME));
                tripDeparture = rs.getString(rs.getColumnIndex(BoatLogDBHelper.TRIPS_COLUMN_DEPARTURE));
                tripDestination = rs.getString(rs.getColumnIndex(BoatLogDBHelper.TRIPS_COLUMN_DESTINATION));
                rs.close();
                rs = null;
            }

            if (rawTripName == null || rawTripName.trim().isEmpty()) {
                rawTripName = "Trip_" + tripID;
            }
            String safeTripName = rawTripName.replaceAll("[\\\\/:*?\"<>|]", "_");

            PdfDocument document = new PdfDocument();
            int pageNumber = 1;
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, pageNumber).create();
            PdfDocument.Page page = document.startPage(pageInfo);
            Canvas canvas = page.getCanvas();

            Paint paint = new Paint();
            paint.setAntiAlias(true);

            float y = 50f;
            float margin = 50f;

            // 1. Draw Image if provided (Page 1 only)
            if (imageUri != null) {
                try {
                    InputStream is = getContentResolver().openInputStream(imageUri);
                    if (is != null) {
                        Bitmap bmp = BitmapFactory.decodeStream(is);
                        is.close();
                        if (bmp != null) {
                            float imgWidth = 350f;
                            float imgHeight = (float) bmp.getHeight() / (float) bmp.getWidth() * imgWidth;
                            float left = (595 - imgWidth) / 2f;

                            android.graphics.RectF destRect = new android.graphics.RectF(left, y, left + imgWidth, y + imgHeight);

                            Paint imgPaint = new Paint();
                            imgPaint.setAntiAlias(true);
                            imgPaint.setFilterBitmap(true);
                            imgPaint.setDither(true);

                            canvas.drawBitmap(bmp, null, destRect, imgPaint);
                            y += imgHeight + 25;
                            bmp.recycle();
                        }
                    }
                } catch (Exception e) {
                    Log.e("PDFCreator", "Failed to load image for PDF", e);
                }
            }

            // 2. Trip Name (Page 1)
            paint.setTextSize(22f);
            paint.setFakeBoldText(true);
            paint.setColor(Color.RED);
            canvas.drawText(rawTripName, margin, y, paint);
            y += 30;

            // 3. Departure to Destination (Page 1)
            paint.setTextSize(14f);
            paint.setFakeBoldText(false);
            paint.setColor(Color.parseColor("#008000"));
            canvas.drawText((tripDeparture != null ? tripDeparture : "") + " To " + (tripDestination != null ? tripDestination : ""), margin, y, paint);
            y += 35;

            // 4. Table Headers (Page 1)
            paint.setTextSize(12f);
            paint.setFakeBoldText(true);
            paint.setColor(Color.BLACK);
            canvas.drawText("Description", margin, y, paint);
            canvas.drawText("Time", margin + 180, y, paint);
            canvas.drawText("Date", margin + 280, y, paint);
            canvas.drawText("Location", margin + 380, y, paint);
            y += 8;

            paint.setStrokeWidth(1f);
            canvas.drawLine(margin, y, 595 - margin, y, paint);
            y += 20;

            // 5. Table Rows (Supports multi-page)
            paint.setTextSize(11f);
            paint.setFakeBoldText(false);
            Cursor cursor = dbHelper.getTripEntry(tripID);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    if (y > 780f) {
                        document.finishPage(page);
                        pageNumber++;
                        pageInfo = new PdfDocument.PageInfo.Builder(595, 842, pageNumber).create();
                        page = document.startPage(pageInfo);
                        canvas = page.getCanvas();
                        y = 50f;

                        // Redraw table headers on the new page
                        paint.setTextSize(12f);
                        paint.setFakeBoldText(true);
                        paint.setColor(Color.BLACK);
                        canvas.drawText("Description", margin, y, paint);
                        canvas.drawText("Time", margin + 180, y, paint);
                        canvas.drawText("Date", margin + 280, y, paint);
                        canvas.drawText("Location", margin + 380, y, paint);
                        y += 8;

                        paint.setStrokeWidth(1f);
                        canvas.drawLine(margin, y, 595 - margin, y, paint);
                        y += 20;

                        // Reset paint for table rows
                        paint.setTextSize(11f);
                        paint.setFakeBoldText(false);
                    }

                    String description = cursor.getString(cursor.getColumnIndex(BoatLogDBHelper.ENTRY_COLUMN_NAME));
                    String time = cursor.getString(cursor.getColumnIndex(BoatLogDBHelper.ENTRY_COLUMN_TIME));
                    String date = cursor.getString(cursor.getColumnIndex(BoatLogDBHelper.ENTRY_COLUMN_DATE));
                    String location = cursor.getString(cursor.getColumnIndex(BoatLogDBHelper.ENTRY_COLUMN_LOCATION));

                    canvas.drawText(description != null ? description : "", margin, y, paint);
                    canvas.drawText(time != null ? time : "", margin + 180, y, paint);
                    canvas.drawText(date != null ? date : "", margin + 280, y, paint);
                    canvas.drawText(location != null ? location : "", margin + 380, y, paint);
                    y += 22;
                }
                cursor.close();
            }

            document.finishPage(page);

            File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "boatLog");
            if (!dir.exists()) dir.mkdirs();
            File file = new File(dir, safeTripName + ".pdf");

            try (FileOutputStream fos = new FileOutputStream(file)) {
                document.writeTo(fos);
                document.close();
                Log.d("PDFCreator", "PDF Created successfully: " + file.getAbsolutePath());
                return true;
            } catch (Exception e) {
                Log.e("PDFCreator", "Failed to write native PDF", e);
                if (file.exists()) {
                    file.delete();
                }
                document.close();
                return false;
            }
        } catch (Exception e) {
            Log.e("PDFCreator", "Error generating PDF", e);
            return false;
        } finally {
            if (rs != null && !rs.isClosed()) rs.close();
        }
    }

    private class BackgroundTaskPDF extends AsyncTask<Void, Void, Boolean> {
        private ProgressDialog dialog;
        private final Uri imageUri;

        public BackgroundTaskPDF(MainActivityEntries activity, Uri imageUri) {
            dialog = new ProgressDialog(activity, R.style.AlertDialogTheme);
            this.imageUri = imageUri;
        }

        @Override
        protected void onPreExecute() {
            dialog.setTitle("Creating PDF");
            dialog.setIcon(R.drawable.ic_picture_as_pdf_white);
            dialog.setMessage("please wait.");
            dialog.show();
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            if (success != null && success) {
                Toast.makeText(MainActivityEntries.this, "PDF Saved to Documents/boatLog", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(MainActivityEntries.this, "Error saving PDF", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            return generatePdf(imageUri);
        }
    }

    private void loadToolbarNavDrawer(String tripNam) {
        //set Toolbar
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(4);
        TypedArray ta = obtainStyledAttributes(new int[]{R.attr.colorLightTextPrimary});
        Drawable Btn = getResources().getDrawable(R.drawable.ic_action_menu);
        Btn.setColorFilter(ta.getColor(0, Color.WHITE), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(Btn);
        toolbar.setTitleTextColor(ta.getColor(0, Color.WHITE));
        getSupportActionBar().setTitle("" + tripNam + "");
        ta.recycle();
        //set NavigationDrawer
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }
    }

    //navigationDrawerIcon Onclick
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //set NavigationDrawerContent
    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        mDrawerLayout.closeDrawers();
                        menuItem.setChecked(false);
                        int id = menuItem.getItemId();
                        switch (id) {
                            case R.id.nav_home_entries:
                                getSupportActionBar().setElevation(0);
                                mDrawerLayout.closeDrawers();
                                break;
                            case R.id.nav_create_entry:
                                createEntry();
                                break;
                            case R.id.nav_create_pdfimage:
                                // create pdf with image
                                loadImagefromGallery();
                                break;
                            case R.id.nav_create_pdf:
                                // create pdf no image
                                createPdf();
                                break;
                            case R.id.nav_create_favourites:
                                createFavourite();
                                break;
                            case R.id.nav_delete_favourites:
                                deleteFavourite();
                                break;
                        }
                        return false;
                    }
                }
        );
    }

    private void createPdf() {
        BackgroundTaskPDF taskPDF = new BackgroundTaskPDF(MainActivityEntries.this, null);
        taskPDF.execute();
    }

    private void createEntry() {
        Intent intent = new Intent(MainActivityEntries.this, CreateEntriesActivity.class);
        intent.putExtra(KEY_EXTRA_ENTRIES_ID, 0);
        intent.putExtra(KEY_EXTRA_TRIPS_ID, tripID);
        intent.putExtra(KEY_EXTRA_TRIPS_NAME, tripName);
        Bundle bndlanimation =
                ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.anni1, R.anim.anni2).toBundle();
        startActivity(intent, bndlanimation);
    }

    private void createFavourite() {
        AlertDialog.Builder builder;

        builder = new AlertDialog.Builder(MainActivityEntries.this);
        TypedArray ta = obtainStyledAttributes(new int[]{R.attr.colorTextPrimary});
        Drawable Btn = getResources().getDrawable(R.drawable.ic_favorite_border);
        Btn.setColorFilter(ta.getColor(0, Color.WHITE), PorterDuff.Mode.SRC_ATOP);
        builder.setIcon(Btn);
        ta.recycle();
        builder.setTitle("      Create a Favourite");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setTextColor(getResources().getColor(R.color.night_text));
        input.setTextSize(20);
        builder.setView(input);
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (dbHelper.insertFavEntry(input.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "Favourite Saved", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Could not Save Favourite", Toast.LENGTH_SHORT).show();
                }
            }
        })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // cancel
                    }
                })
                .show();
    }

    private void deleteFavourite() {
        Cursor rs = dbHelper.getAllFavEntry();
        ArrayList<String> favArray = new ArrayList<String>();
        while (rs.moveToNext()) {
            String fav = rs.getString(rs.getColumnIndex(BoatLogDBHelper.FAVENTRY_COLUMN_NAME));
            favArray.add(fav);
        }
        if (!rs.isClosed()) {
            rs.close();
        }
        mFileList = favArray.toArray(new String[favArray.size()]);

        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(MainActivityEntries.this);
        TypedArray ta = obtainStyledAttributes(new int[]{R.attr.colorTextPrimary});
        Drawable Btn = getResources().getDrawable(R.drawable.ic_favorite_border);
        Btn.setColorFilter(ta.getColor(0, Color.WHITE), PorterDuff.Mode.SRC_ATOP);
        builder.setIcon(Btn);
        ta.recycle();
        builder.setTitle("      Select a Favourite");
        if (mFileList == null) {
            builder.create();
        }
        builder.setItems(mFileList, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                final String mChosenFavourite = mFileList[which];

                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(MainActivityEntries.this);
                TypedArray ta = obtainStyledAttributes(new int[]{R.attr.colorTextPrimary});
                Drawable Btn = getResources().getDrawable(R.drawable.ic_delete_white);
                Btn.setColorFilter(ta.getColor(0, Color.WHITE), PorterDuff.Mode.SRC_ATOP);
                builder.setIcon(Btn);
                ta.recycle();
                builder.setTitle("   Delete Favourite")
                        .setMessage("    " + mChosenFavourite)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dbHelper.deleteFavEntry(mChosenFavourite);
                                Toast.makeText(getApplicationContext(), "Deleted Successfully", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // cancelled by user
                            }
                        })
                        .show();
            }
        });
        builder.show();
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
