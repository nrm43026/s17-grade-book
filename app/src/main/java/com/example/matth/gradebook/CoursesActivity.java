package com.example.matth.gradebook;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.matth.gradebook.data.Course;
import com.example.matth.gradebook.database.GradeBookDB;
import com.example.matth.gradebook.settings.SettingsActivity;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Shows a table of courses on screen
 */
public class CoursesActivity extends AppCompatActivity {

    private GradeBookDB db;
    private List<Course> courses;
    private TableLayout table;
    private TableRow table_header;

    private View lastHeaderClicked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Init component references
        table = (TableLayout) findViewById(R.id.table_courses);
        table_header = (TableRow) findViewById(R.id.table_courses_header);

        //Setup new course button
        findViewById(R.id.button_courses_new).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CoursesActivity.this, AddEditDeleteCourseActivity.class);
                CoursesActivity.this.startActivity(intent);
            }
        });

        // Load the database
        db = new GradeBookDB(this);
        courses = db.getCourses();

        //Create and setup table
        setupActions();
        loadTable();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        //Reset to solve for issues with database changing
        db = new GradeBookDB(this);
        courses = db.getCourses();
        loadTable();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                this.startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.action_assignments:
                //Go to main page TODO switch to assignment full list if added
                Intent intent = new Intent(this, MainActivity.class);
                this.startActivity(intent);
                return true;
            case R.id.action_courses:
                //do nothing as we are on the course page
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void setupActions() {
        TextView view = (TextView) findViewById(R.id.table_courses_header_c1);
        view.setOnClickListener(new SortByIDClickListener(this));

        view = (TextView) findViewById(R.id.table_courses_header_c2);
        view.setOnClickListener(new SortByNameClickListener(this));

        view = (TextView) findViewById(R.id.table_courses_header_c3);
        view.setOnClickListener(new SortByGradeClickListener(this));
    }

    /**
     * Called to reload the table with current data
     */
    public void loadTable() {

        clearTable();

        final int rowHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 65, getResources().getDisplayMetrics());
        TableLayout.LayoutParams layout = new TableLayout.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, rowHeight);
        layout.setMargins(1, 5, 1, 5);

        // changed to pull from SQLite GradeBookDB - Andy 6/28
        for (Course course : courses) {
            // make sure the courses are updated, and pass in a db handle since
            // it's ridiculously impossible to get access to the DB from the Courses or Assignments
            // classes (you HAVE to have an active context to get a DB handle!!!! Arghhh)
            course.update(db);

            //Single row
            TableRow classRow = new TableRow(this);
            classRow.setLayoutParams(layout);
            classRow.setBackgroundColor(Color.BLUE); //TODO fix color
            classRow.setBackgroundResource(R.drawable.border);


            //Course short name
            TextView classCol1 = new TextView(this);
            classCol1.setTextSize(15);
            classCol1.setText(course.getName());
            classCol1.setTextColor(Color.WHITE);
            classCol1.setGravity(Gravity.CENTER);
            classCol1.setBackgroundResource(R.drawable.border);
            classRow.addView(classCol1);

            //Course long name
            TextView classCol2 = new TextView(this);
            classCol2.setTextSize(15);
            classCol2.setText(course.getFullName());
            classCol2.setTextColor(Color.WHITE);
            classCol2.setGravity(Gravity.CENTER);
            classCol2.setBackgroundResource(R.drawable.border);
            classRow.addView(classCol2);

            //Current grade
            TextView classCol3 = new TextView(this);
            classCol3.setTextSize(15);
            classCol3.setText(course.getGrade().letterValue);
            classCol3.setTextColor(course.getGrade().color);
            classCol3.setGravity(Gravity.CENTER);
            classCol3.setBackgroundResource(R.drawable.border);
            classRow.addView(classCol3);

            //Click listener
            classRow.setOnLongClickListener(new CourseRowClickListener(this, course));
            classRow.setOnClickListener(new CourseRowClickListener(this, course));

            //Add row to table
            table.addView(classRow);
        }
    }

    /**
     * Called to clearAllData the table with the exception of
     * the header.
     */
    public void clearTable() {
        table.removeAllViews();
        table.addView(table_header);
    }

    //TODO move from intern classes to normal classes to help navigation

    /**
     * Used to direct class clicks to assignment activity
     */
    public static class CourseRowClickListener implements View.OnLongClickListener, View.OnClickListener {
        public final CoursesActivity activity;
        public final Course course;

        public CourseRowClickListener(CoursesActivity activity, Course course) {
            this.activity = activity;
            this.course = course;
        }



        @Override
        public boolean onLongClick(View v) {
            Intent intent = new Intent(activity, AddEditDeleteCourseActivity.class);
            intent.putExtra("courseID", course.getCourseId());
            intent.putExtra("name", course.getName());
            intent.putExtra("fullName", course.getFullName());
            activity.startActivity(intent);
            return true;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(activity, CourseAssignmentActivity.class);
            intent.putExtra("courseID", course.getCourseId());
            activity.startActivity(intent);
        }

    }

    public static abstract class SortByClickListener implements View.OnClickListener {
        protected boolean invert = false;

        protected final CoursesActivity coursesActivity;

        public SortByClickListener(CoursesActivity coursesActivity) {
            this.coursesActivity = coursesActivity;
        }

        @Override
        public void onClick(View v) {
            if (coursesActivity.lastHeaderClicked == v) {
                invert = !invert;
            }
            coursesActivity.lastHeaderClicked = v;
            sort();
            coursesActivity.loadTable();
        }

        protected abstract void sort();
    }

    public static class SortByIDClickListener extends SortByClickListener {
        public SortByIDClickListener(CoursesActivity coursesActivity) {
            super(coursesActivity);
        }

        protected void sort() {
            Collections.sort(coursesActivity.courses, new IDSorter(invert));
        }
    }

    public static class IDSorter implements Comparator<Course> {
        final boolean invert;

        public IDSorter(boolean invert) {
            this.invert = invert;
        }

        @Override
        public int compare(Course o1, Course o2) {
            //TODO consider separating prefix name from numbers to improve sorting
            return invert ? o2.getName().compareTo(o1.getName()) : o1.getName().compareTo(o2.getName());
        }
    }

    public static class SortByNameClickListener extends SortByClickListener {
        public SortByNameClickListener(CoursesActivity coursesActivity) {
            super(coursesActivity);
        }

        protected void sort() {
            Collections.sort(coursesActivity.courses, new NameSorter(invert));
        }
    }

    public static class NameSorter implements Comparator<Course> {
        final boolean invert;

        public NameSorter(boolean invert) {
            this.invert = invert;
        }

        @Override
        public int compare(Course o1, Course o2) {
            return invert ? o2.getFullName().compareTo(o1.getFullName()) : o1.getFullName().compareTo(o2.getFullName());
        }
    }

    public static class SortByGradeClickListener extends SortByClickListener {
        public SortByGradeClickListener(CoursesActivity coursesActivity) {
            super(coursesActivity);
        }

        protected void sort() {
            Collections.sort(coursesActivity.courses, new GradeSorter(invert));
        }
    }

    public static class GradeSorter implements Comparator<Course> {
        final boolean invert;

        public GradeSorter(boolean invert) {
            this.invert = invert;
        }

        @Override
        public int compare(Course o1, Course o2) {
            //TODO if the option is added to change grade display type update to reflect changes
            double d1 = o1.getCurrentPercentOfGraded();
            double d2 = o2.getCurrentPercentOfGraded();
            return invert ? Double.compare(d2, d1) : Double.compare(d1, d2);
        }
    }
}
