package com.example.matth.gradebook.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.example.matth.gradebook.data.*;


import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Andy on 6/22/2017.  NOT YET WORKING 6/22/17 745pm
 * I was working at the same time Robert was on the DB stuff, we can merge our
 * work and keep best of both going forward.
 *
 * This creates a SQLite DB if none exists, and creates a couple fake/testing entries
 */

public class GradeBookDB {

    // database constants
    public static final String DB_NAME = "gradebook.db";
    public static final int    DB_VERSION = 3;

    // course table constants
    // Course(long courseId, String name, String fullName,
    //       long totalPoints, long currentPoints, double currentPercentOfGraded,
    //      double currentPercentOfAll)
    public static final String COURSE_TABLE = "course";

    public static final String COURSE_ID = "_id";
    public static final int    COURSE_ID_COL = 0;

    public static final String COURSE_NAME = "course_name";
    public static final int    COURSE_NAME_COL = 1;

    public static final String COURSE_FULL_NAME = "course_full_name";
    public static final int    COURSE_FULL_NAME_COL = 2;

    public static final String COURSE_TOTAL_POINTS = "course_total_points";
    public static final int    COURSE_TOTAL_POINTS_COL = 3;

    public static final String COURSE_CURRENT_POINTS = "course_current_points";
    public static final int    COURSE_CURRENT_POINTS_COL = 4;

    /* These are unused now, as they should be calculated from the points, rather than
       imported from the DB
    public static final String COURSE_PERCENT_OF_GRADED = "course_percent_of_graded";
    public static final int    COURSE_NAME_COL = 6;

    public static final String COURSE_PERCENT_OF_ALL = "course_percent_of_all";
    public static final int    COURSE_NAME_COL = 7;
    */

    // assignment table constants
    // Assignment(long assignmentId, long courseId, String name, long pointsPossible,
    // long pointsEarned, boolean isGraded, double percentage, Date dueDate)
    public static final String ASSIGNMENT_TABLE = "assignment";

    public static final String ASSIGNMENT_ID = "_id";
    public static final int    ASSIGNMENT_ID_COL = 0;

    public static final String ASSIGNMENT_COURSE_ID = "assignment_course_id";
    public static final int    ASSIGNMENT_COURSE_ID_COL = 1;

    public static final String ASSIGNMENT_NAME = "assignment_name";
    public static final int    ASSIGNMENT_NAME_COL = 2;

    public static final String ASSIGNMENT_POINTS_POSSIBLE = "assignment_points_possible";
    public static final int    ASSIGNMENT_POINTS_POSSIBLE_COL = 3;

    public static final String ASSIGNMENT_POINTS_EARNED = "assignment_points_earned";
    public static final int    ASSIGNMENT_POINTS_EARNED_COL = 4;

    public static final String ASSIGNMENT_IS_GRADED = "assignment_is_graded";
    public static final int    ASSIGNMENT_IS_GRADED_COL = 5;

    /* This is unused, as we will calculate the percentage, not store in DB
     static final String ASSIGNMENT_PERCENTAGE = "assignment_percentage";
     static final int    ASSIGNMENT_PERCENTAGE = ?;
    */

    public static final String ASSIGNMENT_DUE_DATE = "assignment_due_date";
    public static final int    ASSIGNMENT_DUE_DATE_COL = 6;

    // CREATE and DROP TABLE statements
    public static final String CREATE_COURSE_TABLE =
            "CREATE TABLE " + COURSE_TABLE + " (" +
                    COURSE_ID   + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COURSE_NAME + " TEXT    UNIQUE, " +
                    COURSE_FULL_NAME + " TEXT   UNIQUE," +
                    COURSE_TOTAL_POINTS + " INTEGER, " +
                    COURSE_CURRENT_POINTS + " INTEGER)";

    public static final String CREATE_ASSIGNMENT_TABLE =
            "CREATE TABLE " + ASSIGNMENT_TABLE + " (" +
                    ASSIGNMENT_ID         + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    ASSIGNMENT_COURSE_ID    + " INTEGER, " +
                    ASSIGNMENT_NAME       + " TEXT, " +
                    ASSIGNMENT_POINTS_POSSIBLE      + " INTEGER, " +
                    ASSIGNMENT_POINTS_EARNED  + " INTEGER, " +
                    // IS_GRADED is boolean, but SQLite
                    // only has integer, so 1 is true, 0 false
                    ASSIGNMENT_IS_GRADED + " INTEGER, " +
                    ASSIGNMENT_DUE_DATE + " TEXT)";

    public static final String DROP_COURSE_TABLE =
            "DROP TABLE IF EXISTS " + COURSE_TABLE;

    public static final String DROP_ASSIGNMENT_TABLE =
            "DROP TABLE IF EXISTS " + ASSIGNMENT_TABLE;


    private static class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context, String name,
                        SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // create tables
            db.execSQL(CREATE_COURSE_TABLE);
            db.execSQL(CREATE_ASSIGNMENT_TABLE);

            // TODO Remove once DB is created. This is for testing only!
            // insert sample courses
            db.execSQL("INSERT INTO course VALUES (1, 'COMP394', 'CS Practicum II', 1000, 786 )");
            db.execSQL("INSERT INTO course VALUES (2, 'COMP294', 'CS Practicum I', 1000, 954 )");
            db.execSQL("INSERT INTO course VALUES (3, 'HUMN305', 'Global Issues', 1000, 886 )");
            db.execSQL("INSERT INTO course VALUES (4, 'PSY101', 'Intro to Psychology', 1000, 586 )");

            // TODO Remove once DB is created. This is for testing only!
            // insert sample assignments
            db.execSQL("INSERT INTO assignment VALUES (1, 1, 'Week 5 Status Report', " +
                    "40, '37', '1', '6/01/2017')");
            db.execSQL("INSERT INTO assignment VALUES (2, 1, 'Week 6 Status Report', " +
                    "40, '33', '1', '6/02/2017')");
            db.execSQL("INSERT INTO assignment VALUES (3, 1, 'Final Project', " +
                    "100, '95', '1', '7/03/2017')");
            db.execSQL("INSERT INTO assignment VALUES (4, 2, 'Weekly Status Report', " +
                    "40, '37', '1', '6/04/2017')");
            db.execSQL("INSERT INTO assignment VALUES (5, 2, 'Weekly Status Report', " +
                    "40, '38', '1', '6/05/2017')");
            db.execSQL("INSERT INTO assignment VALUES (6, 2, 'Final Project Report', " +
                    "100, '87', '1', '7/06/2017')");
            db.execSQL("INSERT INTO assignment VALUES (7, 3, 'Discussion Week 4', " +
                    "40, '0', '0', '6/07/2017')");
            db.execSQL("INSERT INTO assignment VALUES (8, 3, 'Reading Review', " +
                    "30, '28', '1', '6/08/2017')");
            db.execSQL("INSERT INTO assignment VALUES (9, 3, 'Nation Report', " +
                    "100, '99', '1', '6/09/2017')");
            db.execSQL("INSERT INTO assignment VALUES (10, 4, 'Week 3 Post to Discuss', " +
                    "40, '19', '1', '6/10/2017')");
            db.execSQL("INSERT INTO assignment VALUES (11, 4, 'Week 4 Quiz', " +
                    "10, '7', '1', '6/11/2017')");
            db.execSQL("INSERT INTO assignment VALUES (12, 4, 'Final Exam', " +
                    "100, '0', '0', '6/12/2017')");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db,
                              int oldVersion, int newVersion) {

            Log.d("Assignment course", "Upgrading db from version "
                    + oldVersion + " to " + newVersion);

            Log.d("Assignment course", "Deleting all data!");
            db.execSQL(GradeBookDB.DROP_COURSE_TABLE);
            db.execSQL(GradeBookDB.DROP_ASSIGNMENT_TABLE);
            onCreate(db);
        }
    }

    // database object and database helper object
    private SQLiteDatabase db;
    private DBHelper dbHelper;
    private Context context;

    // constructor
    public GradeBookDB(Context context) {
        this.context = context;
        dbHelper = new DBHelper(context, DB_NAME, null, DB_VERSION);
    }

    // private methods
    private void openReadableDB() {
        db = dbHelper.getReadableDatabase();
    }

    private void openWriteableDB() {
        db = dbHelper.getWritableDatabase();
    }

    private void closeDB() {
        if (db != null)
            db.close();
    }


    // public methods
    public ArrayList<Course> getCourses() {
        ArrayList<Course> courses = new ArrayList<Course>();
        openReadableDB();
        Cursor cursor = db.query(COURSE_TABLE,
                null, null, null, null, null, null);
        while (cursor.moveToNext()) {


            // Course percentages and the assignment list will have to be updated somehow...
            // TODO test this. Also maybe add a way to get the assignments? Or add them later?
            Course course = new Course(cursor.getLong(COURSE_ID_COL),
                    cursor.getString(COURSE_NAME_COL),
                    cursor.getString(COURSE_FULL_NAME_COL),
                    cursor.getInt(COURSE_TOTAL_POINTS_COL),
                    cursor.getInt(COURSE_CURRENT_POINTS_COL),
                    0.0, // Course percentages and the assignment list will have to be updated somehow...
                    0.0,
                    new ArrayList<Assignment>());

            courses.add(course);
        }
        cursor.close();

        // Now we are going to get all the assignments and load those into the courses we retrieved
        for (Course c : courses) {
            c.setAssignments(getAssignments(c.getCourseId()));
            c.update(this); // and then calculate the grades/percentages
        }

        closeDB();
        return courses;
    }

    public Course getCourse(String name) {
        String where = COURSE_NAME + "= ?";
        String[] whereArgs = { name };

        openReadableDB();
        Cursor cursor = db.query(COURSE_TABLE, null,
                where, whereArgs, null, null, null);
        Course course = null;
        cursor.moveToFirst();
        course = new Course(cursor.getLong(COURSE_ID_COL),
                cursor.getString(COURSE_NAME_COL),
                cursor.getString(COURSE_FULL_NAME_COL),
                cursor.getInt(COURSE_TOTAL_POINTS_COL),
                cursor.getInt(COURSE_CURRENT_POINTS_COL),
                0.0,
                0.0,
                new ArrayList<Assignment>() );
        cursor.close();
        this.closeDB();

        course.update(this);
        return course;
    }

    public Course getCourse(long id) {
        String where = COURSE_ID + "= ?";
        String[] whereArgs = { Long.toString(id) };

        openReadableDB();
        Cursor cursor = db.query(COURSE_TABLE, null,
                where, whereArgs, null, null, null);
        Course course = null;
        cursor.moveToFirst();
        course = new Course(cursor.getLong(COURSE_ID_COL),
                cursor.getString(COURSE_NAME_COL),
                cursor.getString(COURSE_FULL_NAME_COL),
                cursor.getInt(COURSE_TOTAL_POINTS_COL),
                cursor.getInt(COURSE_CURRENT_POINTS_COL),
                0.0,
                0.0,
                new ArrayList<Assignment>() );
        cursor.close();
        this.closeDB();

        course.update(this);
        return course;
    }

    public ArrayList<Assignment> getAssignments(String courseName) {
        String where =
                ASSIGNMENT_COURSE_ID + "= ?";
        long courseID = getCourse(courseName).getCourseId();
        String[] whereArgs = { Long.toString(courseID) };

        this.openReadableDB();
        Cursor cursor = db.query(ASSIGNMENT_TABLE, null,
                where, whereArgs,
                null, null, null);
        ArrayList<Assignment> assignments = new ArrayList<Assignment>();
        while (cursor.moveToNext()) {
            assignments.add(getAssignmentFromCursor(cursor));
        }
        if (cursor != null)
            cursor.close();
        this.closeDB();
        return assignments;
    }

    public ArrayList<Assignment> getAssignments(long id) {
        String where =
                ASSIGNMENT_COURSE_ID + "= ? ";
        //long courseID = getCourse(courseName).getCourseId();
        String[] whereArgs = { Long.toString(id) };

        this.openReadableDB();
        Cursor cursor = db.query(ASSIGNMENT_TABLE, null,
                where, whereArgs,
                null, null, null);
        ArrayList<Assignment> assignments = new ArrayList<Assignment>();
        while (cursor.moveToNext()) {
            assignments.add(getAssignmentFromCursor(cursor));
        }
        if (cursor != null)
            cursor.close();
        this.closeDB();
        return assignments;
    }

    public Assignment getAssignment(long id) {
        String where = ASSIGNMENT_ID + "= ?";
        String[] whereArgs = { Long.toString(id) };

        this.openReadableDB();
        Cursor cursor = db.query(ASSIGNMENT_TABLE,
                null, where, whereArgs, null, null, null);
        cursor.moveToFirst();
        Assignment assignment = getAssignmentFromCursor(cursor);
        if (cursor != null)
            cursor.close();
        this.closeDB();

        return assignment;
    }

    private static Assignment getAssignmentFromCursor(Cursor cursor) {
        if (cursor == null || cursor.getCount() == 0){
            return null;
        }
        else {
            try {
                // Some values are calculated, not stored in DB
                // Also we must convert String date from DB to actual Date object
                boolean isGraded = (cursor.getInt(ASSIGNMENT_IS_GRADED_COL) == 1) ? true : false;
                double percentage = (double)cursor.getInt(ASSIGNMENT_POINTS_EARNED_COL) /
                        (double)cursor.getInt(ASSIGNMENT_POINTS_POSSIBLE_COL);
                Date dueDate = new Date(cursor.getString(ASSIGNMENT_DUE_DATE_COL));

                Assignment assignment = new Assignment(
                        cursor.getLong(ASSIGNMENT_ID_COL),
                        cursor.getLong(ASSIGNMENT_COURSE_ID_COL),
                        cursor.getString(ASSIGNMENT_NAME_COL),
                        cursor.getInt(ASSIGNMENT_POINTS_POSSIBLE_COL),
                        cursor.getInt(ASSIGNMENT_POINTS_EARNED_COL),
                        isGraded,
                        percentage,
                        dueDate);
                return assignment;
            }
            catch(Exception e) {
                return null;
            }
        }
    }

    public long insertAssignment(Assignment assignment) {
        ContentValues cv = new ContentValues();
        cv.put(ASSIGNMENT_COURSE_ID, assignment.getCourseId());
        cv.put(ASSIGNMENT_NAME, assignment.getName());
        cv.put(ASSIGNMENT_POINTS_POSSIBLE, assignment.getPointsPossible());
        cv.put(ASSIGNMENT_POINTS_EARNED, assignment.getPointsEarned());
        cv.put(ASSIGNMENT_IS_GRADED, assignment.isGraded());
        cv.put(ASSIGNMENT_DUE_DATE, assignment.getDueDate().toString());

        this.openWriteableDB();
        long rowID = db.insert(ASSIGNMENT_TABLE, null, cv);
        this.closeDB();

        return rowID;
    }

    public long insertCourse(Course course) {
        ContentValues cv = new ContentValues();
        cv.put(COURSE_NAME, course.getName());
        cv.put(COURSE_FULL_NAME, course.getFullName());
        cv.put(COURSE_CURRENT_POINTS, course.getCurrentPoints());
        cv.put(COURSE_TOTAL_POINTS, course.getTotalPoints());

        this.openWriteableDB();
        long rowID = db.insert(COURSE_TABLE, null, cv);
        this.closeDB();

        return rowID;

    }

    public int updateAssignment(Assignment assignment) {
        ContentValues cv = new ContentValues();
        cv.put(ASSIGNMENT_COURSE_ID, assignment.getCourseId());
        cv.put(ASSIGNMENT_NAME, assignment.getName());
        cv.put(ASSIGNMENT_POINTS_POSSIBLE, assignment.getPointsPossible());
        cv.put(ASSIGNMENT_POINTS_EARNED, assignment.getPointsEarned());
        cv.put(ASSIGNMENT_IS_GRADED, assignment.isGraded());
        cv.put(ASSIGNMENT_DUE_DATE, assignment.getDueDate().toString());

        String where = ASSIGNMENT_ID + "= ?";
        String[] whereArgs = { String.valueOf(assignment.getAssignmentId()) };

        this.openWriteableDB();
        int rowCount = db.update(ASSIGNMENT_TABLE, cv, where, whereArgs);
        this.closeDB();

        // TODO implement a listener for when assignments are altered/updated
        // We aren't implementing this yet...
        //broadcastAssignmentModified();

        return rowCount;
    }

    public int updateCourse(Course course)
    {
        ContentValues cv = new ContentValues();
        cv.put(COURSE_NAME, course.getName());
        cv.put(COURSE_FULL_NAME, course.getFullName());
        cv.put(COURSE_CURRENT_POINTS, course.getCurrentPoints());
        cv.put(COURSE_TOTAL_POINTS, course.getTotalPoints());

        String where = COURSE_ID + "= ?";
        String[] whereArgs = {String.valueOf(course.getCourseId())};

        this.openWriteableDB();
        int rowCount = db.update(COURSE_TABLE, cv, where, whereArgs);
        this.closeDB();

        return rowCount;

    }

    public int deleteAssignment(long id) {
        String where = ASSIGNMENT_ID + "= ?";
        String[] whereArgs = { String.valueOf(id) };

        this.openWriteableDB();
        int rowCount = db.delete(ASSIGNMENT_TABLE, where, whereArgs);
        this.closeDB();

        // TODO implement a listener for when assignments are altered/updated
        // We aren't implementing this yet...
        //broadcastAssignmentModified();

        return rowCount;
    }


    public int deleteAssignmentsByCourseId(long id){
        String where = ASSIGNMENT_COURSE_ID + "= ?";
        String[] whereArgs = { String.valueOf(id) };

        this.openWriteableDB();
        int rowCount = db.delete(ASSIGNMENT_TABLE, where, whereArgs);
        this.closeDB();
        return rowCount;
    }

    /**
     * Deletes a Course without deleting the assignments associated with that course.
     * You must ALWAYS also call deleteAssignmentsByCousreId to delete the assignemts or
     * you will have orphaned assignments that cause the Main Activity to crash.
     * @param id the course id
     * @return the row count
     */
    public int deleteCourse(long id)
    {
        String where = COURSE_ID + "= ?";
        String[] whereArgs = { String.valueOf(id)};

        this.openWriteableDB();
        int rowCount = db.delete(COURSE_TABLE, where, whereArgs);
        this.closeDB();

        return rowCount;
    }

    /**
     * Called to clear all stored data in the database
     */
    public void clearAllData() {
        for(Course course : getCourses())
        {
            if(course.getAssignments() != null)
            {
                for(Assignment assignment : course.getAssignments())
                {
                    deleteAssignment(assignment.getAssignmentId());
                }
            }
            deleteCourse(course.getCourseId());
        }
    }

    public ArrayList<Assignment> getUpcomingAssignments() {
        //TODO implement a way to search for upcoming assignments
        return new ArrayList<Assignment>();
    }

    /**
     * Gets a list of all assignments in the database
     * @return an ArrayList of Assignments
     */
    public ArrayList<Assignment> getAllAssignments()
    {
        this.openReadableDB();
        Cursor cursor = db.query(ASSIGNMENT_TABLE, null,
                null, null, null, null, null);
        ArrayList<Assignment> assignments = new ArrayList<>();
        while (cursor.moveToNext())
        {
            assignments.add(getAssignmentFromCursor(cursor));
        }
        if (cursor != null)
            cursor.close();
        this.closeDB();

        return assignments;
    }
}