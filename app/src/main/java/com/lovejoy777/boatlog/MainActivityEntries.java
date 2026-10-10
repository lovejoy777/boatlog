package com.lovejoy777.boatlog;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
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
    private boolean fabExpanded = false;
    private FloatingActionButton fabEntries; //fabMain
    private LinearLayout layoutFabAddNew;

    private ListView listView;
    BoatLogDBHelper dbHelper;

    RelativeLayout MRL1;

    Toolbar toolBar;
    ListView listViewEntries;

    TextView titleTextView;


    int tripID;
    String tripName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_entries);

         loadToolbarNavDrawer();

        tripID = getIntent().getIntExtra(MainActivityTrips.KEY_EXTRA_TRIPS_ID, 0);
        tripName = getIntent().getStringExtra(MainActivityTrips.KEY_EXTRA_TRIPS_NAME);

        tripID = getIntent().getIntExtra(CreateEntriesActivity.KEY_EXTRA_TRIPS_ID, 0);
        tripName = getIntent().getStringExtra(CreateEntriesActivity.KEY_EXTRA_TRIPS_NAME);

        tripID = getIntent().getIntExtra(EditEntriesActivity.KEY_EXTRA_TRIPS_ID, 0);
        tripName = getIntent().getStringExtra(EditEntriesActivity.KEY_EXTRA_TRIPS_NAME);

        MRL1 = (RelativeLayout) findViewById(R.id.MRL1);
        toolBar = (Toolbar) findViewById(R.id.toolbar);
        titleTextView = (TextView) findViewById(R.id.titleTextView);
        listViewEntries = (ListView) findViewById(R.id.listViewEntries);
        titleTextView.setText("" + tripName + "");


        dbHelper = new BoatLogDBHelper(this);

        fabEntries = (FloatingActionButton) this.findViewById(R.id.fabEntries);
        layoutFabAddNew = (LinearLayout) this.findViewById(R.id.layoutFabAddNew);


        fabEntries.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fabExpanded) {
                    closeSubMenusFab();
                } else {
                    openSubMenusFab();
                }
            }
        });





        // ADD NEW subFab button
        layoutFabAddNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivityEntries.this, CreateEntriesActivity.class);
                intent.putExtra(KEY_EXTRA_ENTRIES_ID, 0);
                intent.putExtra(KEY_EXTRA_TRIPS_ID, tripID);
                intent.putExtra(KEY_EXTRA_TRIPS_NAME, tripName);
                Bundle bndlanimation =
                        ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.anni1, R.anim.anni2).toBundle();
                startActivity(intent, bndlanimation);
                closeSubMenusFab();
            }
        });

        //Only main FAB is visible in the beginning
        closeSubMenusFab();
        //populateListView();


        SharedPreferences myPrefs = this.getSharedPreferences("myPrefs", MODE_PRIVATE);
        Boolean NightModeOn = myPrefs.getBoolean("switch1", false);

        if (NightModeOn) {
            NightMode();
            populateListViewRed();
        } else {
            populateListView();
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> listView, View view,
                                    int position, long id) {
                Cursor itemCursor = (Cursor) MainActivityEntries.this.listView.getItemAtPosition(position);
                int entryID = itemCursor.getInt(itemCursor.getColumnIndex(BoatLogDBHelper.ENTRY_COLUMN_ID));

                Intent intent = new Intent(getApplicationContext(), EditEntriesActivity.class);
                intent.putExtra(KEY_EXTRA_ENTRIES_ID, entryID);
                intent.putExtra(KEY_EXTRA_TRIPS_ID, tripID);
                intent.putExtra(KEY_EXTRA_TRIPS_NAME, tripName);

                Bundle bndlanimation =
                        ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.anni1, R.anim.anni2).toBundle();
                startActivity(intent, bndlanimation);
                closeSubMenusFab();
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> listView, View view,
                                           int position, long id) {
                Cursor itemCursor = (Cursor) MainActivityEntries.this.listView.getItemAtPosition(position);
                int entryID = itemCursor.getInt(itemCursor.getColumnIndex(BoatLogDBHelper.ENTRY_COLUMN_ID));
                Intent intent = new Intent(getApplicationContext(), EditEntriesActivity.class);
                intent.putExtra(KEY_EXTRA_ENTRIES_ID, entryID);
                intent.putExtra(KEY_EXTRA_TRIPS_ID, tripID);
                intent.putExtra(KEY_EXTRA_TRIPS_NAME, tripName);

                Bundle bndlanimation =
                        ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.anni1, R.anim.anni2).toBundle();
                startActivity(intent, bndlanimation);
                closeSubMenusFab();
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
        listView.setDivider(this.getResources().getDrawable(R.drawable.list_divide));
        listView.setDividerHeight(2);
        listView.setAdapter(cursorAdapter);
    }

    private void populateListViewRed() {
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
                // R.id.entryTrip_ID
        };

        SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(this, R.layout.entries_info1,
                cursor, columns, widgets, 0);
        listView = (ListView) findViewById(R.id.listViewEntries);
        listView.setDivider(this.getResources().getDrawable(R.drawable.list_divide));
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
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
                    && null != data) {

                Uri selectedImage = data.getData();
                if (selectedImage != null) {
                    BackgroundTaskPDF taskPDFimage =
                            new BackgroundTaskPDF(MainActivityEntries.this, selectedImage);
                    taskPDFimage.execute();
                } else {
                    Toast.makeText(this, "Something went wrong.",
                            Toast.LENGTH_LONG).show();
                }

            } else {

                Toast.makeText(this, "No Image Selected",
                        Toast.LENGTH_LONG).show();

            }
        } catch (Exception e) {
            Toast.makeText(this, "Incompatible Image", Toast.LENGTH_LONG)

                    .show();
        }
    }

    public boolean generatePdf(Uri imageUri) {
        Cursor rs = dbHelper.getTrip(tripID);
        rs.moveToFirst();
        String tripName = rs.getString(rs.getColumnIndex(BoatLogDBHelper.TRIPS_COLUMN_NAME));
        String tripDeparture = rs.getString(rs.getColumnIndex(BoatLogDBHelper.TRIPS_COLUMN_DEPARTURE));
        String tripDestination = rs.getString(rs.getColumnIndex(BoatLogDBHelper.TRIPS_COLUMN_DESTINATION));
        if (!rs.isClosed()) {
            rs.close();
        }

        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        Paint paint = new Paint();
        paint.setAntiAlias(true);

        float y = 50f;
        float margin = 50f;

        // 1. Draw Image if provided
        if (imageUri != null) {
            try {
                InputStream is = getContentResolver().openInputStream(imageUri);
                if (is != null) {
                    Bitmap bmp = BitmapFactory.decodeStream(is);
                    is.close();
                    if (bmp != null) {
                        float imgWidth = 350f;
                        float imgHeight = (float) bmp.getHeight() / bmp.getWidth() * imgWidth;
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

        // 2. Trip Name
        paint.setTextSize(22f);
        paint.setFakeBoldText(true);
        paint.setColor(Color.RED);
        canvas.drawText(tripName != null ? tripName : "", margin, y, paint);
        y += 30;

        // 3. Departure to Destination
        paint.setTextSize(14f);
        paint.setFakeBoldText(false);
        paint.setColor(Color.parseColor("#008000"));
        canvas.drawText((tripDeparture != null ? tripDeparture : "") + " To " + (tripDestination != null ? tripDestination : ""), margin, y, paint);
        y += 35;

        // 4. Table Headers
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

        // 5. Table Rows
        paint.setTextSize(11f);
        paint.setFakeBoldText(false);
        final Cursor cursor = dbHelper.getTripEntry(tripID);
        while (cursor.moveToNext()) {
            String description = cursor.getString(cursor.getColumnIndex(BoatLogDBHelper.ENTRY_COLUMN_NAME));
            String time = cursor.getString(cursor.getColumnIndex(BoatLogDBHelper.ENTRY_COLUMN_TIME));
            String date = cursor.getString(cursor.getColumnIndex(BoatLogDBHelper.ENTRY_COLUMN_DATE));
            String location = cursor.getString(cursor.getColumnIndex(BoatLogDBHelper.ENTRY_COLUMN_LOCATION));

            canvas.drawText(description != null ? description : "", margin, y, paint);
            canvas.drawText(time != null ? time : "", margin + 180, y, paint);
            canvas.drawText(date != null ? date : "", margin + 280, y, paint);
            canvas.drawText(location != null ? location : "", margin + 380, y, paint);
            y += 22;

            if (y > 780) {
                break;
            }
        }
        if (!cursor.isClosed()) {
            cursor.close();
        }

        document.finishPage(page);

        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "boatLog");
        if (!dir.exists()) dir.mkdirs();
        File file = new File(dir, tripName + ".pdf");

        try (FileOutputStream fos = new FileOutputStream(file)) {
            document.writeTo(fos);
            document.close();
            return true;
        } catch (Exception e) {
            Log.e("PDFCreator", "Failed to write native PDF", e);
            if (file.exists()) {
                file.delete();
            }
            document.close();
            return false;
        }
    }

    // UNIFIED BACKGROUND TASK FOR PDF CREATION (WITH OR WITHOUT IMAGE)
    private class BackgroundTaskPDF extends AsyncTask<Void, Void, Boolean> {
        private ProgressDialog dialog;
        private final Uri imageUri;

        public BackgroundTaskPDF(MainActivityEntries activity, Uri imageUri) {
            dialog = new ProgressDialog(MainActivityEntries.this, R.style.AlertDialogTheme);
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
                Toast.makeText(MainActivityEntries.this, "PDF Created", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(MainActivityEntries.this, "Failed to create PDF", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            return generatePdf(imageUri);
        }
    }

    private void loadToolbarNavDrawer() {
        //set Toolbar
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //set NavigationDrawer
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        if (navigationView != null) {

            setupDrawerContent(navigationView);

            SharedPreferences myPrefse = this.getSharedPreferences("myPrefs", MODE_PRIVATE);
            Boolean NightModeOn = myPrefse.getBoolean("switch1", false);

            if (NightModeOn) {
                navigationView.setItemTextColor(ColorStateList.valueOf(getResources().getColor(R.color.night_text)));
                navigationView.setItemIconTintList(ColorStateList.valueOf(getResources().getColor(R.color.night_text)));
                navigationView.setBackgroundColor(getResources().getColor(R.color.card_background));
            } else {
                navigationView.setItemTextColor(ColorStateList.valueOf(Color.DKGRAY));
                navigationView.setItemIconTintList(ColorStateList.valueOf(Color.DKGRAY));
            }

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
                        menuItem.setChecked(true);
                        int id = menuItem.getItemId();
                        if (id == R.id.nav_home_create_entries) {
                            // mDrawerLayout.closeDrawers();
                            getSupportActionBar().setElevation(0);
                            mDrawerLayout.closeDrawers();
                        } else if (id == R.id.nav_create_favourites) {

                            AlertDialog.Builder builder;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                builder = new AlertDialog.Builder(MainActivityEntries.this, R.style.AlertDialogTheme);
                            } else {
                                builder = new AlertDialog.Builder(MainActivityEntries.this, R.style.AlertDialogTheme);
                            }

                            builder.setTitle("      Create a Favourite");

                            final EditText input = new EditText(MainActivityEntries.this);
                            input.setTextColor(getResources().getColor(R.color.white));
                            input.setTextSize(20);

                            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.MATCH_PARENT);
                            input.setLayoutParams(lp);
                            builder.setView(input);
                            builder.setIcon(R.drawable.ic_favorite_border);

                            builder.setPositiveButton("SAVE",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {

                                            if (dbHelper.insertFavEntry(input.getText().toString())) {
                                                Toast.makeText(getApplicationContext(), "Favourite Saved", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(getApplicationContext(), "Could not Save Favourite", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                            builder.setNegativeButton("CANCEL",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    });

                            builder.show();

                        } else if (id == R.id.nav_create_pdf) {
                            AlertDialog.Builder builder3;

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                builder3 = new AlertDialog.Builder(MainActivityEntries.this, R.style.AlertDialogTheme);
                            } else {
                                builder3 = new AlertDialog.Builder(MainActivityEntries.this, R.style.AlertDialogTheme);
                            }
                            builder3.setTitle("Add an Image")
                                    .setMessage("do you want to add an image to your trip?")
                                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // creat pdf with image
                                            loadImagefromGallery();

                                        }
                                    })
                                    .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // create pdf no image
                                            BackgroundTaskPDF taskPDF = new BackgroundTaskPDF(MainActivityEntries.this, null);
                                            taskPDF.execute();

                                        }
                                    });

                            builder3.show();


                        } else if (id == R.id.nav_delete_favourites) {

                            Cursor rs = dbHelper.getAllFavEntry();
                            ArrayList<String> favArray = new ArrayList<String>();
                            while (rs.moveToNext()) {

                                String fav = rs.getString(rs.getColumnIndex(BoatLogDBHelper.FAVENTRY_COLUMN_NAME));
                                favArray.add(fav);

                            }
                            if (!rs.isClosed()) {
                                rs.close();
                            }
                            final String[] favnames = favArray.toArray(new String[favArray.size()]);

                            AlertDialog.Builder builder1;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                builder1 = new AlertDialog.Builder(MainActivityEntries.this, R.style.AlertDialogTheme);
                            } else {
                                builder1 = new AlertDialog.Builder(MainActivityEntries.this, R.style.AlertDialogTheme);
                            }

                            builder1.setTitle("      Select a Favourite");
                            builder1.setIcon(R.drawable.ic_favorite_border);
                            if (favnames == null) {
                                builder1.create();
                            }

                            builder1.setItems(favnames, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    final String mChosenFavourite = favnames[which];

                                    AlertDialog.Builder builder2;
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                        builder2 = new AlertDialog.Builder(MainActivityEntries.this, R.style.AlertDialogTheme);
                                    } else {
                                        builder2 = new AlertDialog.Builder(MainActivityEntries.this, R.style.AlertDialogTheme);
                                    }
                                    builder2.setTitle("   Delete Favourite")
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

                                            .setIcon(R.drawable.ic_delete_white)
                                            .show();
                                }
                            });
                            builder1.show();
                        }
                        return false;
                    }
                });

    }

    private void NightMode() {
        MRL1.setBackgroundResource(R.color.card_background);
        toolBar.setBackgroundResource(R.color.card_background);
        listViewEntries.setBackgroundResource(R.color.card_background);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            titleTextView.setTextColor(getBaseContext().getResources().getColor(R.color.night_text, getBaseContext().getTheme()));
        }else {
            titleTextView.setTextColor(getResources().getColor(R.color.night_text));
        }
    }

    //closes FAB submenus
    private void closeSubMenusFab() {
        layoutFabAddNew.setVisibility(View.INVISIBLE);
        fabEntries.setImageResource(R.drawable.ic_menu_white);
        fabExpanded = false;
    }

    //Opens FAB submenus
    private void openSubMenusFab() {
        layoutFabAddNew.setVisibility(View.VISIBLE);
        fabEntries.setImageResource(R.drawable.ic_close_white);
        fabExpanded = true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.back2, R.anim.back1);
    }
}