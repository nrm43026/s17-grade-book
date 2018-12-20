package com.example.matth.gradebook.data;

import android.graphics.Color;

/**
 * Stores grade results
 * Created by Rober on 6/16/2017.
 */

public enum EnumGrades {

    A_Plus("A+", Color.GREEN, .98),
    A("A", Color.GREEN, .94),
    A_MINUS("A-", Color.GREEN, .90),
    B_PLUS("B+", Color.WHITE, .86),
    B("B", Color.WHITE, .83),
    B_MINUS("B-", Color.WHITE, .80),
    C_PLUS("C+", Color.BLUE, .76),
    C("C", Color.BLUE, .73),
    C_MINUS("C-", Color.BLUE, .70),
    D_PLUS("D+", Color.YELLOW, .66),
    D("D", Color.YELLOW, .63),
    D_MINUS("D-", Color.YELLOW, .60),
    F("F", Color.RED, 0),
    ERROR("Err", Color.GRAY, -1);

    /**
     * Display color of letter grade
     */
    public final int color;

    /**
     * Display letter grade
     */
    public final String letterValue;
    /**
     * Lower range check, upper is short of grade above current.
     */
    public final double check;


    EnumGrades(String letterValue, int color, double check) {
        this.letterValue = letterValue;
        this.color = color;
        this.check = check;
    }
}
