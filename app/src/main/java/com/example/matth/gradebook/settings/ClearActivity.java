package com.example.matth.gradebook.settings;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

import com.example.matth.gradebook.R;
import com.example.matth.gradebook.database.GradeBookDB;
import com.example.matth.gradebook.prefab.AbstractActivity;

public class ClearActivity extends AbstractActivity {

    private GradeBookDB database;

    public ClearActivity() {
        super(R.layout.activity_clear);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        database = new GradeBookDB(this);

        Button confirmButton = (Button) findViewById(R.id.clearConfirmButton);
        confirmButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Switch sw = (Switch) findViewById(R.id.clearSafetySwitch);
                if (sw.isEnabled()) {
                    database.clearAllData();
                    ClearActivity.this.finish();
                }
                return true;
            }
        });

        findViewById(R.id.clearCancelButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClearActivity.this.finish();
            }
        });
    }
}
