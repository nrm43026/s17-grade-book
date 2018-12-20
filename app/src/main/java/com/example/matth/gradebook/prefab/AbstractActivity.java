package com.example.matth.gradebook.prefab;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.matth.gradebook.CoursesActivity;
import com.example.matth.gradebook.MainActivity;
import com.example.matth.gradebook.R;
import com.example.matth.gradebook.settings.SettingsActivity;

/**
 * Prefab for support similar functionality over several activities
 * Created by Robert on 7/19/2017.
 */
public abstract class AbstractActivity extends AppCompatActivity {

    @LayoutRes
    public final int layoutResID;

    public AbstractActivity(@LayoutRes int layoutResID) {
        this.layoutResID = layoutResID;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layoutResID);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
                this.startActivity(new Intent(this, CoursesActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
