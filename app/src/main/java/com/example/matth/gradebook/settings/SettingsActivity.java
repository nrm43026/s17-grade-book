package com.example.matth.gradebook.settings;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.matth.gradebook.R;
import com.example.matth.gradebook.database.GradeBookDB;
import com.example.matth.gradebook.prefab.AbstractActivity;
import com.example.matth.gradebook.util.XMLHandler;

import java.io.FileNotFoundException;

/**
 * Created by Robert on 7/18/2017.
 */
public class SettingsActivity extends AbstractActivity {
    public static int IMPORT_ACTION_CODE = 6000; // as far as I can tell its a random number
    public static int EXPORT_ACTION_CODE = 6001;

    private GradeBookDB database;

    public SettingsActivity() {
        super(R.layout.activity_settings);
        database = new GradeBookDB(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        findViewById(R.id.importButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("text/xml");
                intent.addCategory(Intent.CATEGORY_OPENABLE);

                try {
                    startActivityForResult(Intent.createChooser(intent, "Select a File to Import"), IMPORT_ACTION_CODE);
                } catch (android.content.ActivityNotFoundException ex) {
                    //Notify user a file manager is needed
                    //Toast.makeText(SettingsActivity.this, R.string.no_file_manager_present, Toast.LENGTH_SHORT).show();
                }
            }
        });

        findViewById(R.id.exportButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                intent.setType("text/xml");
                intent.putExtra(Intent.EXTRA_TITLE, "courses.xml");

                try {
                    startActivityForResult(Intent.createChooser(intent, "Select a location to export"), EXPORT_ACTION_CODE);
                } catch (android.content.ActivityNotFoundException ex) {
                    //Notify user a file manager is needed
                    Toast.makeText(SettingsActivity.this, R.string.no_file_manager_present, Toast.LENGTH_SHORT).show();
                }
            }
        });
        findViewById(R.id.clearButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingsActivity.this.startActivity(new Intent(SettingsActivity.this, ClearActivity.class));
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Log.i("settings", "Request received, RequestCode:" + requestCode + " || ResultCode: " + resultCode + " || Data: " + data);
        if(requestCode == IMPORT_ACTION_CODE)
        {
            if(resultCode == RESULT_OK)
            {
                final Uri uri = data.getData();

                Intent intent = new Intent(this, ImportActivity.class);
                intent.putExtra("importFile", uri);
                //TODO inject data into intent
                this.startActivity(intent);
            }
        }
        else if(requestCode == EXPORT_ACTION_CODE)
        {
            if(resultCode == RESULT_OK)
            {

                try {
                    XMLHandler.writeCourseData(database, getContentResolver().openOutputStream(data.getData()));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
