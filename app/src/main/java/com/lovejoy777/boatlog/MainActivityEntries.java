package com.lovejoy777.boatlog;

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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by lovejoy777 on 03/10/15.
 */
public class MainActivityEntries extends AppCompatActivity {
    public final static String KEY_EXTRA_ENTRIES_ID = "KEY_EXTRA_ENTRIES_ID";
    public final static String KEY_EXTRA_TRIPS_ID = "KEY_EXTRA_TRIPS_ID";
    public final static String KEY_EXTRA_TRIPS_NAME = "KEY_EXTRA_TRIPS_NAME";

    private static int RESULT_LOAD_IMG = 1;
    String imgDecodableString;
    String image_path = "abcx";


    private ListView listView;
    ExampleDBHelper dbHelper;

    RelativeLayout MRL1;
    LinearLayout buttonLayout;

    Toolbar toolBar;
    ListView listViewEntries;

    Button addnewButton,printButton;

    TextView titleTextView;

    int tripID;
    String tripName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_entries);

        tripID = getIntent().getIntExtra(MainActivityTrips.KEY_EXTRA_TRIPS_ID, 0);
        tripName = getIntent().getStringExtra(MainActivityTrips.KEY_EXTRA_TRIPS_NAME);

        tripID = getIntent().getIntExtra(CreateOrEditEntriesActivity.KEY_EXTRA_TRIPS_ID, 0);
        tripName = getIntent().getStringExtra(CreateOrEditEntriesActivity.KEY_EXTRA_TRIPS_NAME);

        //Toast.makeText(getApplicationContext(), "Trip " + tripName, Toast.LENGTH_SHORT).show();

        MRL1 = (RelativeLayout) findViewById(R.id.MRL1);
        toolBar = (Toolbar) findViewById(R.id.toolbar);
        titleTextView = (TextView) findViewById(R.id.titleTextView);
        listViewEntries = (ListView) findViewById(R.id.listViewEntries);
        titleTextView.setText("" + tripName);

        buttonLayout = (LinearLayout) findViewById(R.id.buttonLayout);

        addnewButton = (Button) findViewById(R.id.addNew);
        addnewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivityEntries.this, CreateOrEditEntriesActivity.class);
                intent.putExtra(KEY_EXTRA_ENTRIES_ID, 0);
                intent.putExtra(KEY_EXTRA_TRIPS_ID, tripID);
                intent.putExtra(KEY_EXTRA_TRIPS_NAME, tripName);
                startActivity(intent);
            }
        });

        printButton = (Button) findViewById(R.id.print);
        printButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(MainActivityEntries.this, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(MainActivityEntries.this);
                }
                builder.setTitle("Add Image")
                        .setMessage("do you want to add an image of your trip?")
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
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                //createPDF();
            }
        });

        dbHelper = new ExampleDBHelper(this);

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
                int entryID = itemCursor.getInt(itemCursor.getColumnIndex(ExampleDBHelper.ENTRY_COLUMN_ID));
                Intent intent = new Intent(getApplicationContext(), CreateOrEditEntriesActivity.class);
                intent.putExtra(KEY_EXTRA_ENTRIES_ID, entryID);
                startActivity(intent);
            }
        });


        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> listView, View view,
                                           int position, long id) {
                Cursor itemCursor = (Cursor) MainActivityEntries.this.listView.getItemAtPosition(position);
                int entryID = itemCursor.getInt(itemCursor.getColumnIndex(ExampleDBHelper.ENTRY_COLUMN_ID));
                Intent intent = new Intent(getApplicationContext(), CreateOrEditEntriesActivity.class);
                intent.putExtra(KEY_EXTRA_ENTRIES_ID, entryID);
                startActivity(intent);
                return true;
            }
        });


    }

    private void populateListView() {
        final Cursor cursor = dbHelper.getTripEntry(tripID);
        String [] columns = new String[] {
                ExampleDBHelper.ENTRY_COLUMN_ID,
                ExampleDBHelper.ENTRY_COLUMN_NAME,
                ExampleDBHelper.ENTRY_COLUMN_TIME,
                ExampleDBHelper.ENTRY_COLUMN_DATE,
                ExampleDBHelper.ENTRY_COLUMN_TRIP_ID

        };
        int [] widgets = new int[] {
                R.id.entryID,
                R.id.entryName,
                R.id.entryTime,
                R.id.entryDate
                // R.id.entryTrip_ID
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
                ExampleDBHelper.ENTRY_COLUMN_ID,
                ExampleDBHelper.ENTRY_COLUMN_NAME,
                ExampleDBHelper.ENTRY_COLUMN_TIME,
                ExampleDBHelper.ENTRY_COLUMN_DATE,
                ExampleDBHelper.ENTRY_COLUMN_TRIP_ID

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

    private void NightMode() {


        MRL1.setBackgroundColor(Color.BLACK);
        toolBar.setBackgroundColor(Color.BLACK);
        titleTextView.setTextColor(Color.RED);

        buttonLayout.setBackgroundColor(Color.BLACK);
        addnewButton.setBackgroundResource(R.color.card_background);
        addnewButton.setTextColor(Color.RED);
        printButton.setBackgroundResource(R.color.card_background);
        printButton.setTextColor(Color.RED);

        listViewEntries.setBackgroundColor(Color.BLACK);

        // Toast.makeText(MainActivityLog.this, "Night Mode", Toast.LENGTH_LONG).show();

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
              //  ImageView imgView = (ImageView) findViewById(R.id.imgView);

                // Set the Image in ImageView after decoding the String
               // imgView.setImageBitmap(BitmapFactory.decodeFile(imgDecodableString));
            } else {

                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();

            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)

                    .show();
        }


    }


    public void createPDF()
    {

        Cursor rs = dbHelper.getTrip(tripID);
        rs.moveToFirst();
        String tripName = rs.getString(rs.getColumnIndex(ExampleDBHelper.TRIPS_COLUMN_NAME));
        String tripDeparture = rs.getString(rs.getColumnIndex(ExampleDBHelper.TRIPS_COLUMN_DEPARTURE));
        String tripDestination = rs.getString(rs.getColumnIndex(ExampleDBHelper.TRIPS_COLUMN_DESTINATION));
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

            Paragraph p2 = new Paragraph("From " + tripDeparture + "To " + tripDestination);
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
                String description = cursor.getString(cursor.getColumnIndex(ExampleDBHelper.ENTRY_COLUMN_NAME));
                String time = cursor.getString(cursor.getColumnIndex(ExampleDBHelper.ENTRY_COLUMN_TIME));
                String date = cursor.getString(cursor.getColumnIndex(ExampleDBHelper.ENTRY_COLUMN_DATE));
                String location = cursor.getString(cursor.getColumnIndex(ExampleDBHelper.ENTRY_COLUMN_LOCATION));
               // String description = cursor.getString(3);
               // String time = cursor.getString(1);
               // String date = cursor.getString(2);
               // String location = cursor.getString(4);

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

            //set footer
           // Phrase footerText = new Phrase("This is an example of a footer");
           // HeaderFooter pdfFooter = new HeaderFooter(footerText, false);
            //doc.setFooter(pdfFooter);



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
        String tripName = rs.getString(rs.getColumnIndex(ExampleDBHelper.TRIPS_COLUMN_NAME));
        String tripDeparture = rs.getString(rs.getColumnIndex(ExampleDBHelper.TRIPS_COLUMN_DEPARTURE));
        String tripDestination = rs.getString(rs.getColumnIndex(ExampleDBHelper.TRIPS_COLUMN_DESTINATION));
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

            //R.color.accent
            //Font myFont = FontFactory.getFont(FUTURA_LIGHT, BaseFont.IDENTITY_H);
            Font paraFont= new Font(Font.COURIER,16.0f,Color.RED);
            Paragraph p1 = new Paragraph("" + tripName,paraFont);
            p1.setAlignment(Paragraph.ALIGN_CENTER);
            //add paragraph 1 to document
            doc.add(p1);

            Paragraph p2 = new Paragraph("From " + tripDeparture + "To " + tripDestination);
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
                String description = cursor.getString(cursor.getColumnIndex(ExampleDBHelper.ENTRY_COLUMN_NAME));
                String time = cursor.getString(cursor.getColumnIndex(ExampleDBHelper.ENTRY_COLUMN_TIME));
                String date = cursor.getString(cursor.getColumnIndex(ExampleDBHelper.ENTRY_COLUMN_DATE));
                String location = cursor.getString(cursor.getColumnIndex(ExampleDBHelper.ENTRY_COLUMN_LOCATION));
                // String description = cursor.getString(3);
                // String time = cursor.getString(1);
                // String date = cursor.getString(2);
                // String location = cursor.getString(4);

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

            //set footer
            // Phrase footerText = new Phrase("This is an example of a footer");
            // HeaderFooter pdfFooter = new HeaderFooter(footerText, false);
            //doc.setFooter(pdfFooter);



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

}
