package com.example.matth.gradebook;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.view.View.OnClickListener;

import com.example.matth.gradebook.data.Assignment;
import com.example.matth.gradebook.data.Course;
import com.example.matth.gradebook.database.GradeBookDB;
import com.example.matth.gradebook.settings.SettingsActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class CourseAssignmentActivity extends AppCompatActivity implements OnClickListener{

    private ListView courseAssignmentsListView;
    private Button courseAssignmentsEditButton;
    private Course course;
    private GradeBookDB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_assignment);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        courseAssignmentsListView = (ListView) findViewById(R.id.course_assignment_list);
        courseAssignmentsEditButton = (Button) findViewById(R.id.editCourseButton);

        //Get information passed from course activity
        Intent intent = getIntent();
        long courseID = intent.getLongExtra("courseID", 1);

        // Get DB handle
        db = new GradeBookDB(this);

        //Get course
        course = db.getCourse(courseID);

        //Update totals for grades
        course.update(db);

        courseAssignmentsEditButton.setOnClickListener(this);


        //Log debug information to note what course was loaded
        Log.d("Assignment", "Loading with courseID = " + courseID + "  Course = " + course);

        CourseAssignmentActivity.this.updateCourseName();
        CourseAssignmentActivity.this.updateCourseStats();
        CourseAssignmentActivity.this.updateListView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                this.startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.action_assignments:
                //Go to main page TODO switch to assignment full list if added
                this.startActivity(new Intent(this, MainActivity.class));
                return true;
            case R.id.action_courses:
                //Go to course page
                this.startActivity(new Intent(this, CoursesActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void updateCourseStats() {
        String stats = "Current points: " + course.getCurrentPoints() + " - Total Points: " +
                course.getTotalPoints() +
                "\nCurrent Percentage: " + course.getCurrentPercentOfGradedString() +
                " - Total Percentage: " + course.getCurrentPercentAllString();
        TextView courseStats = (TextView) findViewById(R.id.courseStatsTextView);
        courseStats.setText(stats);
    }

    public void updateCourseName() {
        String name = "" + course.getName() + " - " + course.getFullName();
        TextView courseName = (TextView) findViewById(R.id.courseName);
        courseName.setText(name);
    }

    // thanks to Matt for the hint on how to display list of entries!
    // I was pulling my hair out trying to figure this out.... -Andy
    public void updateListView()
    {

        //TODO need to sort assignments based off due date, not DB id index

        ArrayList<Assignment> assignments = db.getAssignments(course.getCourseId());

        //create a Arraylist of Hashmaps containing assignment data for the SimpleAdaptor
        ArrayList<HashMap<String, String>> data = new ArrayList<>();
        for (Assignment assignment : assignments)
        {
            HashMap<String, String> map = new HashMap<>();
            map.put("assignment name", assignment.getName());
            String score = assignment.getPointsEarned() + "/" + assignment.getPointsPossible();
            map.put("score", "Points: " + score);
            map.put("percentage", "Score: " + assignment.getPercentageString());
            String simpleDate = new SimpleDateFormat("MM/dd/yyyy").format(assignment.getDueDate());
            map.put("due", "Due date: " + simpleDate);
            // this is a hidden assignment ID for use when clicked on
            map.put("assignment_id", Long.toString(assignment.getAssignmentId()));

            data.add(map);
        }

        //create the resource variable for the SimpleAdapter.
        //This references the listview_course_assignments layout that was created
        //to display assignment info in the listView widget in the content_course_assignment layout.
        int resource = R.layout.listview_course_assignments;

        //array of keys for the SimpleAdapter
        String[] from = {"assignment name", "score", "percentage", "due", "assignment_id" };

        //create an array of IDs corresponding to the widgets in the
        //listview_course_assignments layout for the adapter
        int[] to = {R.id.assignmentNameTextView, R.id.scoreTextView, R.id.percentageTextView, R.id.dueDateTextView, R.id.hiddenAssignmentID };


        //create the SimpleAdapter and set it.
        SimpleAdapter adapter = new SimpleAdapter(this, data, resource, from, to);
        courseAssignmentsListView.setAdapter(adapter);


        courseAssignmentsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object listItem = courseAssignmentsListView.getItemAtPosition(position);
                Log.d("CourseAssignment", "Registered a click on listview item: " + listItem.toString());
                // cast the object to a hashmap, since that's what it is
                HashMap<String, String> listMap = (HashMap<String, String>)listItem;
                editAssignment(Integer.parseInt(listMap.get("assignment_id")));

            }
        });
    }

    public void go_to_Courses(View view) {
        Intent intent = new Intent(this, CoursesActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View view){
        switch (view.getId()) {
            case R.id.editCourseButton:

                Intent editCourse = new Intent(this, AddEditDeleteCourseActivity.class);
                editCourse.putExtra("courseID", course.getCourseId());
                editCourse.putExtra("name", course.getName());
                editCourse.putExtra("fullName", course.getFullName());
                startActivity(editCourse);
                break;
        }
    }

    public void addAssignment(View view) {
        Intent intent = new Intent(this, AddEditDeleteAssignmentActivity.class);
        intent.putExtra("courseID", course.getCourseId());
        startActivity(intent);
    }

    public void editAssignment(int id) {
        Intent intent = new Intent(this, AddEditDeleteAssignmentActivity.class);
        intent.putExtra("courseID", course.getCourseId());
        intent.putExtra("assignmentID", id);
        startActivity(intent);
    }
}
