package com.industries.sarker.lister;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.industries.sarker.lister.dialogs.DeleteTaskDialogFragment;
import com.industries.sarker.lister.dialogs.EditTaskDialogFragment;

import java.util.Date;

public class DetailsActivity extends AppCompatActivity implements EditTaskDialogFragment.OnEditDataPass, DeleteTaskDialogFragment.OnDeleteDataPass {

    private TextView mNameTextView;
    private TextView mDateTextView;
    private TextView mStatusTextView;
    private TextView mNotesTextView;
    private Task mTask;
    private long mID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        createCutomActionBarTitle();

        mID = getIntent().getLongExtra("TASK_ID", 0);

        mTask = Task.load(Task.class, mID);

        mNameTextView = (TextView) findViewById(R.id.tvName);
        mDateTextView = (TextView) findViewById(R.id.tvDate);
        mStatusTextView = (TextView) findViewById(R.id.tvStatus);
        mNotesTextView = (TextView) findViewById(R.id.tvNotes);

        mNameTextView.setText(mTask.name);
        mDateTextView.setText(String.format("%tb %<te %<tY", mTask.date));

        if (mTask.completed) {
            mStatusTextView.setText("COMPLETED");

        } else {
            mStatusTextView.setText("TO-DO");
        }

        mNotesTextView.setText(mTask.notes);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.task_details_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuEdit:
                EditTaskDialogFragment detailsDialogFragment = EditTaskDialogFragment.newInstance(mID);
                detailsDialogFragment.setStyle(DialogFragment.STYLE_NO_FRAME, R.style.Dialog_FullScreen);
                detailsDialogFragment.show(getSupportFragmentManager(), "fragment_edit_task_dialog");
                break;

            case R.id.menuDelete:
                DeleteTaskDialogFragment deleteTaskDialogFragment = DeleteTaskDialogFragment.newInstance("Delete Task");
                deleteTaskDialogFragment.show(getSupportFragmentManager(), "fragment_delete_task_dialog");
                break;
        }

        return true;
    }

    @Override
    public void onEditDataPass(String name, Date date, String status, String notes) {
        mNameTextView.setText(name);
        mDateTextView.setText(String.format("%tb %<te %<tY", date));
        mStatusTextView.setText(status);
        mNotesTextView.setText(notes);
    }

    @Override
    public void onDeleteDataPass(boolean delete) {
        if (delete) {
            Task.delete(Task.class, mID);
            finish();
        }
    }

    // Customize the title of action bar
    private void createCutomActionBarTitle() {
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        LayoutInflater inflator = LayoutInflater.from(this);
        View v = inflator.inflate(R.layout.custom_action_bar, null);

        Typeface typeface = Typeface.createFromAsset(getApplication().getAssets(), "fonts/KGBlankSpaceSketch.ttf");
        ((TextView) v.findViewById(R.id.customActionBarTitle)).setTypeface(typeface);

        getSupportActionBar().setCustomView(v);
    }
}
