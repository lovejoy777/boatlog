package com.lovejoy777.boatlog;

import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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

    public final static String KEY_EXTRA_ENTRIES_ID = "KEY_EXTRA_ENTRIES_ID";
    public final static String KEY_EXTRA_ENTRY_NAME = "KEY_EXTRA_ENTRY_NAME";
    public final static String KEY_EXTRA_TRIPS_ID = "KEY_EXTRA_TRIPS_ID";
    public final static String KEY_EXTRA_TRIPS_NAME = "KEY_EXTRA_TRIPS_NAME";

    private static int RESULT_LOAD_IMG = 1;
    String imgDecodableString;

    private boolean fabExpanded = false;
    private FloatingActionButton fabEntries; //fabMain
    private FloatingActionButton fabFav; //fabSub
    private LinearLayout layoutFabPrintPdf;
    private LinearLayout layoutFabAddNew;
    private LinearLayout layoutFabFav;

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

        tripID = getIntent().getIntExtra(MainActivityTrips.KEY_EXTRA_TRIPS_ID, 0);
        tripName = getIntent().getStringExtra(MainActivityTrips.KEY_EXTRA_TRIPS_NAME);

        tripID = getIntent().getIntExtra(CreateEntriesActivity.KEY_EXTRA_TRIPS_ID, 0);
        tripName = getIntent().getStringExtra(CreateEntriesActivity.KEY_EXTRA_TRIPS_NAME);

        tripID = getIntent().getIntExtra(EditEntriesActivity.KEY_EXTRA_TRIPS_ID, 0);
        tripName = getIntent().getStringExtra(EditEntriesActivity.KEY_EXTRA_TRIPS_NAME);

        //Toast.makeText(getApplicationContext(), "Trip " + tripName, Toast.LENGTH_SHORT).show();

        MRL1 = (RelativeLayout) findViewById(R.id.MRL1);
        toolBar = (Toolbar) findViewById(R.id.toolbar);
        titleTextView = (TextView) findViewById(R.id.titleTextView);
        listViewEntries = (ListView) findViewById(R.id.listViewEntries);
        titleTextView.setText("" + tripName);

        dbHelper = new BoatLogDBHelper(this);

        fabEntries = (FloatingActionButton) this.findViewById(R.id.fabEntries);
        fabFav = (FloatingActionButton) this.findViewById(R.id.fabFav);
        layoutFabPrintPdf = (LinearLayout) this.findViewById(R.id.layoutFabPrintPdf);
        layoutFabAddNew = (LinearLayout) this.findViewById(R.id.layoutFabAddNew);
        layoutFabFav = (LinearLayout) this.findViewById(R.id.layoutFabFav);

        //  fabFav.setBackgroundResource(android.R.drawable.btn_star_big_off);
        fabFav.setImageResource(android.R.drawable.btn_star_big_on);

        fabEntries.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fabExpanded == true){
                    closeSubMenusFab();
                } else {
                    openSubMenusFab();
                }
            }
        });

        // PRINT PDF subFab button
        layoutFabPrintPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(MainActivityEntries.this, R.style.AlertDialogTheme);
                } else {
                    builder = new AlertDialog.Builder(MainActivityEntries.this, R.style.AlertDialogTheme);
                }
                builder.setTitle("Add an Image")
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
                                createPDF();
                            }
                        })
                        .setIcon(R.drawable.ic_photo_white)
                        .show();
                closeSubMenusFab();
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

        layoutFabFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String on = "on";
                Cursor rs = dbHelper.getFavEntries(on);
                ArrayList<String> favArray = new ArrayList<String>();
                while (rs.moveToNext()) {
                    String fav = rs.getString(rs.getColumnIndex(BoatLogDBHelper.ENTRY_COLUMN_NAME));
                    favArray.add(fav);
                }
                if (!rs.isClosed()) {
                    rs.close();
                }
                final String[] favnames = favArray.toArray(new String[favArray.size()]);

                android.support.v7.app.AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new android.support.v7.app.AlertDialog.Builder(MainActivityEntries.this, R.style.AlertDialogTheme);
                } else {
                    builder = new android.support.v7.app.AlertDialog.Builder(MainActivityEntries.this, R.style.AlertDialogTheme);
                }

                builder.setTitle("      Select a Favourite");
                builder.setIcon(android.R.drawable.btn_star_big_off);
                if (favnames == null) {
                    builder.create();
                }
                builder.setItems(favnames, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String mChosenFavourite = favnames[which];

                        Intent createEntry = new Intent(MainActivityEntries.this, CreateEntriesActivity.class);

                        createEntry.putExtra(KEY_EXTRA_ENTRIES_ID, 0);
                        createEntry.putExtra(KEY_EXTRA_TRIPS_ID, tripID);
                        createEntry.putExtra(KEY_EXTRA_TRIPS_NAME, tripName);
                        createEntry.putExtra(KEY_EXTRA_ENTRY_NAME, mChosenFavourite);

                        Bundle bndlanimation =
                                ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.anni1, R.anim.anni2).toBundle();
                        startActivity(createEntry, bndlanimation);
                        closeSubMenusFab();
                    }
                });

                builder.show();

            }
        });

        //Only main FAB is visible in the beginning
        closeSubMenusFab();
        populateListView();

        SharedPreferences myPrefs = this.getSharedPreferences("myPrefs", MODE_PRIVATE);
        Boolean NightModeOn = myPrefs.getBoolean("switch1", false);

        if (NightModeOn) {
            NightMode();
            populateListViewRed();
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
        String [] columns = new String[] {
                BoatLogDBHelper.ENTRY_COLUMN_ID,
                BoatLogDBHelper.ENTRY_COLUMN_NAME,
                BoatLogDBHelper.ENTRY_COLUMN_TIME,
                BoatLogDBHelper.ENTRY_COLUMN_DATE,
                BoatLogDBHelper.ENTRY_COLUMN_TRIP_ID

        };
        int [] widgets = new int[] {
                R.id.entryID,
                R.id.entryName,
                R.id.entryTime,
                R.id.entryDate
        };

        SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(this, R.layout.entries_info,
                cursor, columns, widgets, 0);
        listView = (ListView)findViewById(R.id.listViewEntries);
        listView.setDivider(this.getResources().getDrawable(R.drawable.list_divide));
        listView.setDividerHeight(2);
        listView.setAdapter(cursorAdapter);
    }

    private void populateListViewRed() {
        final Cursor cursor = dbHelper.getTripEntry(tripID);
        String [] columns = new String[] {
                BoatLogDBHelper.ENTRY_COLUMN_ID,
                BoatLogDBHelper.ENTRY_COLUMN_NAME,
                BoatLogDBHelper.ENTRY_COLUMN_TIME,
                BoatLogDBHelper.ENTRY_COLUMN_DATE,
                BoatLogDBHelper.ENTRY_COLUMN_TRIP_ID

        };
        int [] widgets = new int[] {
                R.id.entryID,
                R.id.entryName,
                R.id.entryTime,
                R.id.entryDate
                // R.id.entryTrip_ID
        };

        SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(this, R.layout.entries_info1,
                cursor, columns, widgets, 0);
        listView = (ListView)findViewById(R.id.listViewEntries);
        listView.setDivider(this.getResources().getDrawable(R.drawable.list_dividered));
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
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgDecodableString = cursor.getString(columnIndex);
                cursor.close();

                createPDFimage(imgDecodableString);

            } else {

                Toast.makeText(this, "No Image Selected",
                        Toast.LENGTH_LONG).show();

            }
        } catch (Exception e) {
            Toast.makeText(this, "Incompatible Image", Toast.LENGTH_LONG)

                    .show();
        }
    }

    public void createPDF()
    {

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
            if(!dir.exists())
                dir.mkdirs();
            Log.d("PDFCreator", "PDF Path: " + path);

            File file = new File(dir, "" + tripName + ".pdf" );
            FileOutputStream fOut = new FileOutputStream(file);
            PdfWriter.getInstance(doc, fOut);

            //open the document
            doc.open();

            //R.color.accent
            //Font myFont = FontFactory.getFont(FUTURA_LIGHT, BaseFont.IDENTITY_H);
            Font paraFont= new Font(Font.COURIER,16.0f,Color.RED);
            Paragraph p1 = new Paragraph("" + tripName,paraFont);
            p1.setAlignment(Paragraph.ALIGN_CENTER);
            //add paragraph 1 to document
            doc.add(p1);

            Paragraph p2 = new Paragraph(tripDeparture + "To " + tripDestination);
            Font paraFont2= new Font(Font.COURIER,14.0f,Color.GREEN);
            p2.setAlignment(Paragraph.ALIGN_LEFT);
            p2.setFont(paraFont2);
            //add paragraph 2 to document
            doc.add(p2);

            // SPACE
            Paragraph p3 = new Paragraph(" ");
            Font paraFont3= new Font(Font.BOLDITALIC,16.0f,R.color.accent);
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
        }
        finally
        {
            doc.close();
        }

        Toast.makeText(getApplicationContext(), "" + tripName + ".pdf saved to sdcard/boatLog", Toast.LENGTH_LONG).show();

    }

    public void createPDFimage(String image_path)
    {

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
            if(!dir.exists())
                dir.mkdirs();
            Log.d("PDFCreator", "PDF Path: " + path);

            File file = new File(dir, "" + tripName + ".pdf" );
            FileOutputStream fOut = new FileOutputStream(file);
            PdfWriter.getInstance(doc, fOut);

            //open the document
            doc.open();

            //add image
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            Bitmap bitmap = BitmapFactory.decodeFile(image_path);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100 , stream);
            //Bitmap resized = Bitmap.createScaledBitmap(bitmap,(int)(bitmap.getWidth()*0.5), (int)(bitmap.getHeight()*0.5), true);
            Image myImg = Image.getInstance(stream.toByteArray());
            myImg.setAlignment(Image.MIDDLE);
            myImg.scaleAbsolute(100, 100);
            //add image to document
            doc.add(myImg);

            Font paraFont= new Font(Font.COURIER,16.0f,Color.RED);
            Paragraph p1 = new Paragraph("" + tripName,paraFont);
            p1.setAlignment(Paragraph.ALIGN_CENTER);
            //add paragraph 1 to document
            doc.add(p1);

            Paragraph p2 = new Paragraph(tripDeparture + "To " + tripDestination);
            Font paraFont2= new Font(Font.COURIER,14.0f,Color.GREEN);
            p2.setAlignment(Paragraph.ALIGN_LEFT);
            p2.setFont(paraFont2);
            //add paragraph 2 to document
            doc.add(p2);

            // SPACE
            Paragraph p3 = new Paragraph(" ");
            Font paraFont3= new Font(Font.BOLDITALIC,16.0f,R.color.accent);
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
        }
        finally
        {
            doc.close();
        }

        Toast.makeText(getApplicationContext(), "" + tripName + ".pdf saved to sdcard/boatLog", Toast.LENGTH_LONG).show();

    }

    private void NightMode() {


        MRL1.setBackgroundColor(Color.BLACK);
        toolBar.setBackgroundColor(Color.BLACK);
        titleTextView.setTextColor(Color.RED);
        listViewEntries.setBackgroundColor(Color.BLACK);

    }


    //closes FAB submenus
    private void closeSubMenusFab(){
        layoutFabPrintPdf.setVisibility(View.INVISIBLE);
        layoutFabAddNew.setVisibility(View.INVISIBLE);
        layoutFabFav.setVisibility(View.INVISIBLE);
        fabEntries.setImageResource(R.drawable.ic_menu_white);
        fabExpanded = false;
    }

    //Opens FAB submenus
    private void openSubMenusFab(){
        layoutFabPrintPdf.setVisibility(View.VISIBLE);
        layoutFabAddNew.setVisibility(View.VISIBLE);
        layoutFabFav.setVisibility(View.VISIBLE);
        fabEntries.setImageResource(R.drawable.ic_close_white);
        fabExpanded = true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.back2, R.anim.back1);
    }

}
