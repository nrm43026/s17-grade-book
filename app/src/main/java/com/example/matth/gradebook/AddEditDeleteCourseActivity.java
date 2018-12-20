package com.example.matth.gradebook;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.example.matth.gradebook.data.Assignment;
import com.example.matth.gradebook.data.Course;
import com.example.matth.gradebook.database.GradeBookDB;

import java.util.ArrayList;

public class AddEditDeleteCourseActivity extends AppCompatActivity implements OnClickListener{

    /*Nathan Mayer 6/28/2017*/

    //TODO: Get the add/delete buttons to write to and from the database, get the edit button to change existing input

    /*Declaring private instance variables*/

    private Course course;
    private GradeBookDB db;
    private boolean isNew = true;

    String TAG = "A/E/D Course";

    private EditText courseShortInput, courseFullInput;
    private Button saveButton, deleteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_delete_course);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*Opening the database*/
        db = new GradeBookDB(this);

        /*Referencing Buttons and EditTexts*/

        courseShortInput = (EditText)findViewById(R.id.courseIdInput);
        courseFullInput = (EditText)findViewById(R.id.courseNameInput);
        saveButton = (Button)findViewById(R.id.saveButton);
        deleteButton = (Button)findViewById(R.id.deleteButton);

        /*Adding on click event listeners*/

        saveButton.setOnClickListener(this);
        deleteButton.setOnClickListener(this);

        //getting intent -Matt
        Intent intent = getIntent();
        Log.d(TAG, intent.toString());
        //setting EditText
        if (intent.hasExtra("courseID")) {
            isNew = false;
            long courseID = intent.getLongExtra("courseID", -1);
            Log.d(TAG, courseID + "");
            course = db.getCourse(courseID);
            courseShortInput.setText(course.getName());
            courseFullInput.setText(course.getFullName());
        }
    }

    /*Adding button functionality*/

    @Override
    public void onClick(View v){

        /*Gathering EditText Input*/

        String name = courseShortInput.getText().toString();
        String fullName = courseFullInput.getText().toString();

        switch (v.getId()){
            case R.id.saveButton:

                if (isNew){
                    Course courseToInsert = new Course(name, fullName);
                    db.insertCourse(courseToInsert);
                } else{
                    course.setName(name);
                    course.setFullName(fullName);

                    db.updateCourse(course);
                }

                Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, CoursesActivity.class);
                startActivity(intent);
                break;

            case R.id.deleteButton:

                if (!isNew){
                    db.deleteCourse(course.getCourseId());
                    //added to delete assignments as well
                    db.deleteAssignmentsByCourseId(course.getCourseId());
                    Toast.makeText(getApplicationContext(), "Deleted", Toast.LENGTH_SHORT).show();
                }

                Intent intent1 = new Intent(this, CoursesActivity.class);
                startActivity(intent1);
                break;
        }
    }
}