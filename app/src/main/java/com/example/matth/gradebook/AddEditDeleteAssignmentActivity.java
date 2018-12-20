package com.example.matth.gradebook;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.example.matth.gradebook.data.Assignment;
import com.example.matth.gradebook.data.Course;
import com.example.matth.gradebook.database.GradeBookDB;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AddEditDeleteAssignmentActivity extends AppCompatActivity implements OnClickListener,
        CompoundButton.OnCheckedChangeListener{

    /*Nathan Mayer 6/28/2017
    * Thanks for the help with connecting to the database, Andy!*/

    /*Declaring private instance variables*/

    private Course course;
    private Assignment assignment;
    private GradeBookDB db;
    private boolean isNew = true;

    //added isGraded variable --matt
    private boolean isGraded = true;

    private EditText assignmentNameInput, dueDateInput, studentScoreInput, maxScoreInput;
    private Button saveButton, deleteButton;

    //matt
    private CheckBox gradedCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_delete_assignment);
         Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*Opening the database*/
        db = new GradeBookDB(this);

        /*Getting references to the EditTexts and Buttons*/

        assignmentNameInput = (EditText) findViewById(R.id.assignmentNameInput);
        dueDateInput = (EditText) findViewById(R.id.dueDateInput);
        studentScoreInput = (EditText) findViewById(R.id.studentScoreInput);
        maxScoreInput = (EditText) findViewById(R.id.maxScoreInput);
        saveButton = (Button) findViewById(R.id.saveButton);
        deleteButton = (Button) findViewById(R.id.deleteButton);

        //matt
        gradedCheckBox = (CheckBox) findViewById(R.id.gradedCheckBox);

        /*Adding on click event listeners to the Buttons*/

        saveButton.setOnClickListener(this);
        deleteButton.setOnClickListener(this);

        //matt
        gradedCheckBox.setOnCheckedChangeListener(this);

        //Get information passed from course activity --Andy

        Intent intent = getIntent();
        long courseID = intent.getLongExtra("courseID", -1);
        course = db.getCourse(courseID);

        if (intent.hasExtra("assignmentID")) {
            isNew = false;
            long assignmentID = intent.getIntExtra("assignmentID", -1);
            assignment = db.getAssignment(assignmentID);
            assignmentNameInput.setText(assignment.getName());
            String simpleDate = new SimpleDateFormat("MM/dd/yyyy").format(assignment.getDueDate());
            dueDateInput.setText(simpleDate);
            //added if statement so field will not be populated if not graded -Matt
            if(assignment.isGraded()) {
                studentScoreInput.setText(Integer.toString(assignment.getPointsEarned()));
            }
            maxScoreInput.setText(Integer.toString(assignment.getPointsPossible()));
            // set graded checkbox --Matt
            gradedCheckBox.setChecked(assignment.isGraded());
        }
    }

    /**
     * Event handler for the check box.
     * @param widget the check box
     * @param isChecked whether the box is checked
     */
    @Override
    public void onCheckedChanged(CompoundButton widget, boolean isChecked)
    {
        switch (widget.getId())
        {
            case R.id.gradedCheckBox:
                if (isChecked)
                {
                    isGraded = true;
                }
                else
                {
                    isGraded = false;
                }
                break;
        }

    }


    /*Adding Button functionality*/

    @Override
    public void onClick(View v) {

        /*Gathering EditText input*/

        String assignmentName = assignmentNameInput.getText().toString();
        String dateString = dueDateInput.getText().toString();
        String scoreString = studentScoreInput.getText().toString();
        String maxScoreString = maxScoreInput.getText().toString();

        // Now parse them into the correct types  --Andy
        Date dueDate = new Date(dateString);

        // If the points earned is empty, then we will set the isGraded as false, so that
        // it does not incorrectly calculate the total percentage for grade
        //boolean graded = true;
        int studentScore = 0;
        if (scoreString.isEmpty()) {
            isGraded = false;
            studentScore = 0;
        } else {
            studentScore = Integer.parseInt(scoreString);
        }

        int maxScore = Integer.parseInt(maxScoreString);

        double percentage = (double) studentScore / (double) maxScore;

        // Create an assignment object. We don't have assignmentID yet, that is
        // generated by DB, so for now put as -1? We might be able to make this cleaner in
        // the future...
        // Another thought, we can set the isGraded boolean by whether the score is empty

        switch (v.getId()) {
            case R.id.saveButton:

                //  --Andy
                // changed reference to graded local variable to isGraded instance variable -Matt
                if (isNew) {
                    Assignment assignmentToInsert = new Assignment(-1, course.getCourseId(), assignmentName, maxScore, studentScore, isGraded, percentage, dueDate);
                    db.insertAssignment(assignmentToInsert);
                } else {
                    assignment.setName(assignmentName);
                    assignment.setDueDate(dueDate);
                    //set graded flag based on whether the points earned was empty or not
                    // I set this to reference the isGraded instance variable that I created instead
                    //of the graded local variable --Matt
                    assignment.setGraded(isGraded);
                    assignment.setPointsEarned(studentScore);
                    assignment.setPointsPossible(maxScore);

                    assignment.update();

                    db.updateAssignment(assignment);
                }

                course.update(db);

                /*Display message after writing to database and returning to assignments activity --Nathan*/

                Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, CourseAssignmentActivity.class);
                intent.putExtra("courseID", course.getCourseId());
                startActivity(intent);
                break;

            case R.id.deleteButton:

                if (!isNew) {
                    db.deleteAssignment(assignment.getAssignmentId());
                    // Only show the deleted message if we actually deleted something
                    Toast.makeText(getApplicationContext(), "Deleted", Toast.LENGTH_SHORT).show();
                }

                Intent intent1 = new Intent(this, CourseAssignmentActivity.class);
                intent1.putExtra("courseID", course.getCourseId());
                startActivity(intent1);
                break;

        }
    }
}