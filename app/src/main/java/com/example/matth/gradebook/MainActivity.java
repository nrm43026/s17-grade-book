package com.example.matth.gradebook;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.view.View.OnClickListener;

import com.example.matth.gradebook.data.Course;
import com.example.matth.gradebook.database.GradeBookDB;
import com.example.matth.gradebook.data.Assignment;
import com.example.matth.gradebook.settings.SettingsActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class  MainActivity extends AppCompatActivity implements OnClickListener{

    private static final String TAG = "MainActivity";
    //Declare variable for the ListView -Matt
    private ListView weeklyRemindersListView;
    private Button goToCoursesButton;
    private Button testAddEditDeleteAssignmentButton;
    private Button testAddEditDeleteCourseButton;
    private Button testCourseAssignmentsButton;

    GradeBookDB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Get a reference to the Listview -Matt
        weeklyRemindersListView = (ListView) findViewById(R.id.weeklyRemindersListView);
        goToCoursesButton = (Button) findViewById(R.id.goToCoursesButton);

        /* Removing test buttons since navigation is functioning properly -Nathan

        testAddEditDeleteAssignmentButton = (Button) findViewById(R.id.testAddEditDeleteAssignmentButton);
        testAddEditDeleteCourseButton = (Button) findViewById(R.id.testAddEditDeleteCourseButton);
        testCourseAssignmentsButton = (Button) findViewById(R.id.testCourseAssignmentsButton);
        */

        goToCoursesButton.setOnClickListener(this);

        /*
        testAddEditDeleteAssignmentButton.setOnClickListener(this);
        testAddEditDeleteCourseButton.setOnClickListener(this);
        testCourseAssignmentsButton.setOnClickListener(this);
        */

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // Load the database and update all the courses and assignments
        db = new GradeBookDB(this);
        ArrayList<Course> courses = db.getCourses();
        for (Course c : courses) {
            c.update(db);
        }


        //Call the updateListView method that I wrote below -Matt
        MainActivity.this.updateListView();

        //db.deleteAssignmentsByCourseId(5);
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
                //this.startActivity(new Intent(this, MainActivity.class));
                return true;
            case R.id.action_courses:
                //Go to course page
                this.startActivity(new Intent(this, CoursesActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

   @Override
   public void onClick(View v)
    {
      switch (v.getId())
      {
          case R.id.goToCoursesButton:
              Intent intent = new Intent(this, CoursesActivity.class);
                startActivity(intent);
                break;

          /*    Removing test buttons -Nathan

          case R.id.testAddEditDeleteAssignmentButton:
              Intent intent1 = new Intent(this, AddEditDeleteAssignmentActivity.class);
              startActivity(intent1);
              break;
          case R.id.testAddEditDeleteCourseButton:
              Intent intent2 = new Intent(this, AddEditDeleteCourseActivity.class);
              startActivity(intent2);
              break;
          case R.id.testCourseAssignmentsButton:
              Intent intent3 = new Intent(this, CourseAssignmentActivity.class);
              startActivity(intent3);
              break;
              */
        }
    }

    /**
     * Updates the listView in the main activity with assignments due in the next 7 days.
     */
    public void updateListView()
    {
        //create an arraylist to store assignments
        //ArrayList<Assignment> assignments = new ArrayList<>();
        // I changed this to use the SQLite database to pull some dummy assignments
        ArrayList<Assignment> assignments = db.getAllAssignments();
        //TODO calculate assignments due within 7 days from list

        Date today = new Date();
        Date inSevenDays = new Date();
        inSevenDays.setDate(today.getDate() + 7);

        ArrayList<Assignment> thisWeekAssignments = new ArrayList<>();
        for (Assignment assignment : assignments)
        {
            if(assignment.getDueDate().after(today) && assignment.getDueDate().before(inSevenDays))
            {
                thisWeekAssignments.add(assignment);
            }
        }

        //create a Arraylist of Hashmaps containing assignment data for the SimpleAdaptor
        ArrayList<HashMap<String, String>> data = new ArrayList<>();
        for (Assignment assignment : thisWeekAssignments)
        {
            Course course = db.getCourse(assignment.getCourseId());
            HashMap<String, String> map = new HashMap<>();
            map.put("assignment name", assignment.getName());
            String simpleDate = new SimpleDateFormat("MM/dd/yyyy").format(assignment.getDueDate());
            map.put("due", simpleDate);
            map.put("course", course.getName());
            data.add(map);
        }

        //create the resource variable for the SimpleAdapter.
        //This references the listview_weekly_reminder_assignment layout that was created
        //to display assignment info in the listView widget in the content_main layout.
        int resource = R.layout.listview_weekly_reminder_assignment;

        //array of keys for the SimpleAdapter
        String[] from = {"assignment name", "due", "course"};

        //create an array of IDs corresponding to the widgets in the
        //listview_weekly_reminder_assignment layout for the adapter
        int[] to = {R.id.assignmentNameTextView, R.id.dueDateTextView, R.id.courseNameTextView};

        //create the SimpleAdapter and set it.
        SimpleAdapter adapter = new SimpleAdapter(this, data, resource, from, to);
        weeklyRemindersListView.setAdapter(adapter);

    }
        //StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        //StrictMode.setThreadPolicy(policy);
    public void go_to_Courses(View view) {
        Intent intent = new Intent(this, CoursesActivity.class);
        startActivity(intent);
     }
}
