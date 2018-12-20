package com.example.matth.gradebook.data;

import java.text.DecimalFormat;
import java.util.Date;

/**
 * Created by Andy on 6/11/2017.
 */

public class Assignment {

    private Date dueDate;

    private String name;

    private long assignmentId;
    private long courseId;

    private int pointsPossible;
    private int pointsEarned;

    private boolean isGraded;

    private double percentage;

    public Assignment() {
    }

    public Assignment(long assignmentId, long courseId, String name, int pointsPossible,
                      int pointsEarned, Date dueDate) {
        this.assignmentId = assignmentId;
        this.courseId = courseId;
        this.name = name;
        this.pointsPossible = pointsPossible;
        this.pointsEarned = pointsEarned;
        this.isGraded = false;
        this.percentage = 0.0;
        this.dueDate = dueDate;
    }

    public Assignment(long assignmentId, long courseId, String name, int pointsPossible,
                      int pointsEarned, boolean isGraded, double percentage, Date dueDate) {
        this.assignmentId = assignmentId;
        this.courseId = courseId;
        this.name = name;
        this.pointsPossible = pointsPossible;
        this.pointsEarned = pointsEarned;
        this.isGraded = isGraded;
        this.percentage = percentage;
        this.dueDate = dueDate;
    }

    /**
     * Updates/Calculates/ReCalculates all computed fields. Used after adding or updating an
     * assignment.
     */
    public void update() {
        // TODO how to deal with isGraded?

        // make sure we don't divide by zero
        if (pointsPossible != 0) {
            percentage = (double) pointsEarned / (double) pointsPossible;
        }
    }


    public long getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(long assignmentId) {
        this.assignmentId = assignmentId;
    }

    public long getCourseId() {
        return courseId;
    }

    public void setCourseId(long courseId) {
        this.courseId = courseId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPointsPossible() {
        return pointsPossible;
    }

    public void setPointsPossible(int pointsPossible) {
        this.pointsPossible = pointsPossible;
    }

    public int getPointsEarned() {
        return pointsEarned;
    }

    public void setPointsEarned(int pointsEarned) {
        this.pointsEarned = pointsEarned;
    }

    public boolean isGraded() {
        return isGraded;
    }

    public void setGraded(boolean graded) {
        isGraded = graded;
    }

    // The percentage will be stored as a double value between 0 and 1. Ex: 78% -> 0.78
    public double getPercentage() {
        return percentage;
    }

    // If you need the nicely formatted percentage use this method.
    public String getPercentageString() {
        double p = percentage * 100;
        String df = new DecimalFormat("#.##").format(p);
        return df + "%";
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    public Date getDueDate() {
        if(dueDate == null)
        {
            dueDate = new Date();
        }
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

}
