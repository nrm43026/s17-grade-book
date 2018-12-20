package com.example.matth.gradebook.settings;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.matth.gradebook.R;
import com.example.matth.gradebook.data.Assignment;
import com.example.matth.gradebook.data.Course;
import com.example.matth.gradebook.database.GradeBookDB;
import com.example.matth.gradebook.prefab.AbstractActivity;
import com.example.matth.gradebook.util.XMLHandler;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by Robert on 7/19/2017.
 */
public class ImportActivity extends AbstractActivity {

    private Uri importFile;
    private GradeBookDB database;

    public ImportActivity() {
        super(R.layout.activity_import);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        importFile = intent.getParcelableExtra("importFile");

        database = new GradeBookDB(this);

        //TODO consider loading when created so to disable loaded information before merging. As this will improve user handling/usability.
        findViewById(R.id.importMergeButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ImportActivity.this.importFile(getContentResolver().openInputStream(importFile), false);
                    ImportActivity.this.finish();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    //TODO display error
                }
            }
        });


        findViewById(R.id.importOverrideButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ImportActivity.this.importFile(getContentResolver().openInputStream(importFile), true);
                    ImportActivity.this.finish();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    //TODO display error
                }
            }
        });

        findViewById(R.id.importCancelButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImportActivity.this.finish();
            }
        });
    }

    protected void importFile(InputStream inputStream, boolean clear) {
        if (clear) {
            Log.d("import", "Clearing database");
            database.clearAllData();
        }
        List<Course> courses = XMLHandler.loadFromXML(inputStream);
        for (Course course : courses) {
            Log.d("import", "Adding course '" + course.getName() + "' to database.");
            long row =  database.insertCourse(course);
            Log.d("import", "\t Row: " + row);
            //DEMO DAY MOD course name is a unique identifier -Matt
            long courseID = database.getCourse(course.getName()).getCourseId();
            Log.d("import", "Course ID: " + courseID);

            for (Assignment assignment : course.getAssignments()) {
                //DEMO DAY MOD changing courseId to the correct value -matt
                assignment.setCourseId(courseID);
                Log.d("import", "Adding assignment '" + assignment.getName() + "' to database.");
                row = database.insertAssignment(assignment);
                Log.d("import", "\t Row: " + row);
            }
        }

        Log.d("import", "Testing data inserted");
        List<Course> list = database.getCourses();
        for (Course course : list) {
            Log.d("import", "Course: " + course.getName());
            course.update(database);
            for (Assignment assignment : course.getAssignments()) {
                Log.d("import", "\t" + assignment);
            }
        }
    }
}
