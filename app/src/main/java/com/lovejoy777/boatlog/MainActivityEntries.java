package com.lovejoy777.boatlog;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
    String imgDecodableString;

    private ListView listView;
    BoatLogDBHelper dbHelper;

    ImageView button_createNewEntry;

    RelativeLayout MRL1;

    ListView listViewEntries;

    TextView titleTextView;

    int tripID;
    String tripName;

    int theme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initialize the associated SharedPreferences file with default values
        PreferenceManager.setDefaultValues(this, R.xml.prefs, false);
        SharedPreferences prefs1 = PreferenceManager.getDefaultSharedPreferences(this);
        setTheme(theme = getTheme(prefs1.getString("theme", "fresh")));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_entries);

        loadToolbarNavDrawer();
        button_createNewEntry = (ImageView) findViewById(R.id.button_createNewEntry);

        tripID = getIntent().getIntExtra(MainActivityTrips.KEY_EXTRA_TRIPS_ID, 0);
        tripName = getIntent().getStringExtra(MainActivityTrips.KEY_EXTRA_TRIPS_NAME);

        tripID = getIntent().getIntExtra(CreateEntriesActivity.KEY_EXTRA_TRIPS_ID, 0);
        tripName = getIntent().getStringExtra(CreateEntriesActivity.KEY_EXTRA_TRIPS_NAME);

        tripID = getIntent().getIntExtra(EditEntriesActivity.KEY_EXTRA_TRIPS_ID, 0);
        tripName = getIntent().getStringExtra(EditEntriesActivity.KEY_EXTRA_TRIPS_NAME);

        MRL1 = (RelativeLayout) findViewById(R.id.MRL1);
        titleTextView = (TextView) findViewById(R.id.titleTextView);
        listViewEntries = (ListView) findViewById(R.id.listViewEntries);
        titleTextView.setText("" + tripName + "");

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

                Intent intent = new Intent(getApplicationContext(), EditEntriesActivity.class);
                intent.putExtra(KEY_EXTRA_ENTRIES_ID, entryID);
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
                Intent intent = new Intent(getApplicationContext(), EditEntriesActivity.class);
                intent.putExtra(KEY_EXTRA_ENTRIES_ID, entryID);
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
        // Create intent to Open Image applications like Gallery, Google Photos
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start the Intent
        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
                    && null != data) {

                // Get the Image from data
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                if (cursor != null) {
                    // Move to first row
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    imgDecodableString = cursor.getString(columnIndex);
                    cursor.close();

                    BackgroundTaskPDFimage taskPDFimage = new BackgroundTaskPDFimage(MainActivityEntries.this);
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

    public void createPDFimage(String image_path) {
        Cursor rs = dbHelper.getTrip(tripID);
        rs.moveToFirst();
        String tripName = rs.getString(rs.getColumnIndex(BoatLogDBHelper.TRIPS_COLUMN_NAME));
        String tripDeparture = rs.getString(rs.getColumnIndex(BoatLogDBHelper.TRIPS_COLUMN_DEPARTURE));
        String tripDestination = rs.getString(rs.getColumnIndex(BoatLogDBHelper.TRIPS_COLUMN_DESTINATION));
        if (!rs.isClosed()) {
            rs.close();
        }
        Document doc = new Document();
        try {
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/boatLog";
            File dir = new File(path);
            if (!dir.exists())
                dir.mkdirs();
            Log.d("PDFCreator", "PDF Path: " + path);

            File file = new File(dir, "" + tripName + ".pdf");
            FileOutputStream fOut = new FileOutputStream(file);
            PdfWriter.getInstance(doc, fOut);

            //open the document
            doc.open();

            //add image
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            Bitmap bitmap = BitmapFactory.decodeFile(image_path);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            //Bitmap resized = Bitmap.createScaledBitmap(bitmap,(int)(bitmap.getWidth()*0.5), (int)(bitmap.getHeight()*0.5), true);
            Image myImg = Image.getInstance(stream.toByteArray());
            myImg.setAlignment(Image.MIDDLE);
            myImg.scaleAbsolute(100, 100);
            //add image to document
            doc.add(myImg);

            Font paraFont = new Font(Font.COURIER, 16.0f, Color.RED);
            Paragraph p1 = new Paragraph("" + tripName, paraFont);
            p1.setAlignment(Paragraph.ALIGN_CENTER);
            //add paragraph 1 to document
            doc.add(p1);

            Paragraph p2 = new Paragraph(tripDeparture + "To " + tripDestination);
            Font paraFont2 = new Font(Font.COURIER, 14.0f, Color.GREEN);
            p2.setAlignment(Paragraph.ALIGN_LEFT);
            p2.setFont(paraFont2);
            //add paragraph 2 to document
            doc.add(p2);

            // SPACE
            Paragraph p3 = new Paragraph(" ");
            Font paraFont3 = new Font(Font.BOLDITALIC, 16.0f, R.color.accent);
            p3.setAlignment(Paragraph.ALIGN_CENTER);
            p3.setFont(paraFont3);
            //add space to document
            doc.add(p3);

            // TABLE
            final Cursor cursor = dbHelper.getTripEntry(tripID);

            // define table
            PdfPTable table = new PdfPTable(4);
            table.setHorizontalAlignment(0);

            // add cells to table
            table.addCell("Description");
            table.addCell("Time");
            table.addCell("Date");
            table.addCell("Location");

            while (cursor.moveToNext()) {

                // get strings for table cells
                String description = cursor.getString(cursor.getColumnIndex(BoatLogDBHelper.ENTRY_COLUMN_NAME));
                String time = cursor.getString(cursor.getColumnIndex(BoatLogDBHelper.ENTRY_COLUMN_TIME));
                String date = cursor.getString(cursor.getColumnIndex(BoatLogDBHelper.ENTRY_COLUMN_DATE));
                String location = cursor.getString(cursor.getColumnIndex(BoatLogDBHelper.ENTRY_COLUMN_LOCATION));

                // input data into table cells
                table.addCell(description);
                table.addCell(time);
                table.addCell(date);
                table.addCell(location);
            }

            if (!cursor.isClosed()) {
                cursor.close();
            }

            //add table to document
            doc.add(table);

        } catch (DocumentException de) {
            Log.e("PDFCreator", "DocumentException:" + de);
        } catch (IOException e) {
            Log.e("PDFCreator", "ioException:" + e);
        } finally {
            doc.close();
        }
    }

    // CREATE PDF IN BACKGROUND TASK WITH PROGRESS SPINNER
    private class BackgroundTaskPDF extends AsyncTask<Void, Void, Void> {
        private ProgressDialog dialog;

        public BackgroundTaskPDF(MainActivityEntries activity) {
            dialog = new ProgressDialog(MainActivityEntries.this, R.style.AlertDialogTheme);
        }

        @Override
        protected void onPreExecute() {
            dialog.setTitle("Creating PDF");
            dialog.setIcon(R.drawable.ic_picture_as_pdf_white);
            dialog.setMessage("please wait.");
            dialog.show();
        }

        @Override
        protected void onPostExecute(Void result) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            Cursor rs = dbHelper.getTrip(tripID);
            rs.moveToFirst();
            String tripName = rs.getString(rs.getColumnIndex(BoatLogDBHelper.TRIPS_COLUMN_NAME));
            String tripDeparture = rs.getString(rs.getColumnIndex(BoatLogDBHelper.TRIPS_COLUMN_DEPARTURE));
            String tripDestination = rs.getString(rs.getColumnIndex(BoatLogDBHelper.TRIPS_COLUMN_DESTINATION));
            if (!rs.isClosed()) {
                rs.close();
            }
            Document doc = new Document();
            try {
                String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/boatLog";
                File dir = new File(path);
                if (!dir.exists())
                    dir.mkdirs();
                Log.d("PDFCreator", "PDF Path: " + path);

                File file = new File(dir, "" + tripName + ".pdf");
                FileOutputStream fOut = new FileOutputStream(file);
                PdfWriter.getInstance(doc, fOut);

                //open the document
                doc.open();

                //R.color.accent
                //Font myFont = FontFactory.getFont(FUTURA_LIGHT, BaseFont.IDENTITY_H);
                Font paraFont = new Font(Font.COURIER, 16.0f, Color.RED);
                Paragraph p1 = new Paragraph("" + tripName, paraFont);
                p1.setAlignment(Paragraph.ALIGN_CENTER);
                //add paragraph 1 to document
                doc.add(p1);

                Paragraph p2 = new Paragraph(tripDeparture + "To " + tripDestination);
                Font paraFont2 = new Font(Font.COURIER, 14.0f, Color.GREEN);
                p2.setAlignment(Paragraph.ALIGN_LEFT);
                p2.setFont(paraFont2);
                //add paragraph 2 to document
                doc.add(p2);

                // SPACE
                Paragraph p3 = new Paragraph(" ");
                Font paraFont3 = new Font(Font.BOLDITALIC, 16.0f, R.color.accent);
                p3.setAlignment(Paragraph.ALIGN_CENTER);
                p3.setFont(paraFont3);
                //add space to document
                doc.add(p3);

                // TABLE
                final Cursor cursor = dbHelper.getTripEntry(tripID);

                // define table
                PdfPTable table = new PdfPTable(4);
                table.setHorizontalAlignment(0);

                // add cells to table
                table.addCell("Description");
                table.addCell("Time");
                table.addCell("Date");
                table.addCell("Location");

                while (cursor.moveToNext()) {

                    // get strings for table cells
                    String description = cursor.getString(cursor.getColumnIndex(BoatLogDBHelper.ENTRY_COLUMN_NAME));
                    String time = cursor.getString(cursor.getColumnIndex(BoatLogDBHelper.ENTRY_COLUMN_TIME));
                    String date = cursor.getString(cursor.getColumnIndex(BoatLogDBHelper.ENTRY_COLUMN_DATE));
                    String location = cursor.getString(cursor.getColumnIndex(BoatLogDBHelper.ENTRY_COLUMN_LOCATION));

                    // input data into table cells
                    table.addCell(description);
                    table.addCell(time);
                    table.addCell(date);
                    table.addCell(location);
                }

                if (!cursor.isClosed()) {
                    cursor.close();
                }
                //add table to document
                doc.add(table);
            } catch (DocumentException de) {
                Log.e("PDFCreator", "DocumentException:" + de);
            } catch (IOException e) {
                Log.e("PDFCreator", "ioException:" + e);
            } finally {
                doc.close();
            }
            return null;
        }
    }

    // CREATE PDF WITH IMAGE BACKGROUND TASK WITH PROGRESS SPINNER
    private class BackgroundTaskPDFimage extends AsyncTask<Void, Void, Void> {
        private ProgressDialog dialog;

        public BackgroundTaskPDFimage(MainActivityEntries activity) {
            dialog = new ProgressDialog(activity, R.style.AlertDialogTheme);
        }

        @Override
        protected void onPreExecute() {
            dialog.setTitle("Creating PDF");
            dialog.setMessage("please wait.");
            dialog.setIcon(R.drawable.ic_picture_as_pdf_white);
            dialog.show();
        }

        @Override
        protected void onPostExecute(Void result) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            createPDFimage(imgDecodableString);
            return null;
        }
    }

    private void loadToolbarNavDrawer() {
        //set Toolbar
        final android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
                        menuItem.setChecked(true);
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
                                // creat pdf with image
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

    // Create Favourite
    private void createPdf() {
        BackgroundTaskPDF taskPDF = new BackgroundTaskPDF(MainActivityEntries.this);
        taskPDF.execute();
    }

    // Create Favourite
    private void createEntry() {
        Intent intent = new Intent(MainActivityEntries.this, CreateEntriesActivity.class);
        intent.putExtra(KEY_EXTRA_ENTRIES_ID, 0);
        intent.putExtra(KEY_EXTRA_TRIPS_ID, tripID);
        intent.putExtra(KEY_EXTRA_TRIPS_NAME, tripName);
        Bundle bndlanimation =
                ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.anni1, R.anim.anni2).toBundle();
        startActivity(intent, bndlanimation);
    }

    // Create Favourite
    private void createFavourite() {
        android.support.v7.app.AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new android.support.v7.app.AlertDialog.Builder(MainActivityEntries.this, R.style.AlertDialogTheme);
        } else {
            builder = new android.support.v7.app.AlertDialog.Builder(MainActivityEntries.this, R.style.AlertDialogTheme);
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
    }

    // Delete Favourite
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
        final String[] favnames = favArray.toArray(new String[favArray.size()]);

        android.support.v7.app.AlertDialog.Builder builder1;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder1 = new android.support.v7.app.AlertDialog.Builder(MainActivityEntries.this, R.style.AlertDialogTheme);
        } else {
            builder1 = new android.support.v7.app.AlertDialog.Builder(MainActivityEntries.this, R.style.AlertDialogTheme);
        }

        builder1.setTitle("      Select a Favourite");
        builder1.setIcon(R.drawable.ic_favorite_border);
        if (favnames == null) {
            builder1.create();
        }

        builder1.setItems(favnames, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                final String mChosenFavourite = favnames[which];
                android.support.v7.app.AlertDialog.Builder builder2;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder2 = new android.support.v7.app.AlertDialog.Builder(MainActivityEntries.this, R.style.AlertDialogTheme);
                } else {
                    builder2 = new android.support.v7.app.AlertDialog.Builder(MainActivityEntries.this, R.style.AlertDialogTheme);
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