package com.industries.sarker.lister.mainscreen;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;
import com.industries.sarker.lister.DetailsActivity;
import com.industries.sarker.lister.R;
import com.industries.sarker.lister.Task;
import com.industries.sarker.lister.dialogs.DeleteTaskDialogFragment;
import com.industries.sarker.lister.dialogs.EditTaskDialogFragment;

import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements EditTaskDialogFragment.OnEditDataPass, DeleteTaskDialogFragment.OnDeleteDataPass {

    private ArrayList<Task> mTaskList;
    private RecyclerView mRecyclerView;
    private TaskAdapter mTaskAdapter;
    private FloatingActionButton mFAB;
    ArrayList<Task> mCompletedTasksList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActiveAndroid.initialize(this);

        createCutomActionBarTitle();

        mRecyclerView = (RecyclerView) findViewById(R.id.rvTasks);

        mTaskList = (ArrayList<Task>) Task.getAllTasks();

        mTaskAdapter = new TaskAdapter(mTaskList);
        setUpTaskAdapterOnItemClickListener();
        setUpTaskAdapterOnItemLongClickListener();

        mRecyclerView.setAdapter(mTaskAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST);
        mRecyclerView.addItemDecoration(itemDecoration);

        mFAB = (FloatingActionButton) findViewById(R.id.fabAdd);
        setUpFABOnClickListener();

    }

    public void setUpTaskAdapterOnItemLongClickListener() {
        mTaskAdapter.setSetOnItemLongClickListener(new TaskAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View itemView, int position) {
                // Mark task as complete or not to activate the strike through
                Task task = mTaskList.get(position);
                task.completed = !task.completed;
                task.save();
                mTaskAdapter.notifyItemChanged(position);
            }
        });
    }

    public void setUpTaskAdapterOnItemClickListener() {
        mTaskAdapter.setOnItemClickListener(new TaskAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                Task task = mTaskList.get(position);

                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                intent.putExtra("TASK_ID", task.getId());
                startActivity(intent);
            }
        });
    }

    public void setUpFABOnClickListener() {
        mFAB.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                EditTaskDialogFragment detailsDialogFragment = EditTaskDialogFragment.newInstance(-1);
                detailsDialogFragment.setStyle(DialogFragment.STYLE_NO_FRAME, R.style.Dialog_FullScreen);
                detailsDialogFragment.show(getSupportFragmentManager(), "fragment_edit_details_dialog");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshRecyclerViewAdapter();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        mCompletedTasksList = new ArrayList<>();

        for (Task task : mTaskList) {
            if (task.completed) {
                mCompletedTasksList.add(task);
            }
        }

        if (item.getItemId() == R.id.menuDeleteSweep && mCompletedTasksList.size() > 0) {
            DeleteTaskDialogFragment deleteTaskDialogFragment = DeleteTaskDialogFragment.newInstance("Delete Completed Tasks");
            deleteTaskDialogFragment.show(getSupportFragmentManager(), "fragment_delete_task_dialog");

        } else {
            Toast.makeText(MainActivity.this, "No completed tasks to delete", Toast.LENGTH_SHORT).show();
        }

        return true;
    }

    @Override
    public void onEditDataPass(String name, Date date, String status, String notes) {
        refreshRecyclerViewAdapter();
    }

    @Override
    public void onDeleteDataPass(boolean deleted) {
        if (deleted) {
            for (Task task : mCompletedTasksList) {
                Task.delete(Task.class, task.getId());
            }

            refreshRecyclerViewAdapter();
        }
    }

    public void refreshRecyclerViewAdapter() {
        mTaskList.clear();
        mTaskList.addAll(Task.getAllTasks());
        mTaskAdapter.notifyDataSetChanged();
        mRecyclerView.scrollToPosition(0);
    }
}
