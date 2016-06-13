package com.industries.sarker.lister.dialogs;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.industries.sarker.lister.R;
import com.industries.sarker.lister.Task;

import java.util.Calendar;
import java.util.Date;

public class EditTaskDialogFragment extends DialogFragment implements Toolbar.OnMenuItemClickListener {

    private EditText mNameEditText;
    private DatePicker mDatePicker;
    private Spinner mStatusSpinner;
    private EditText mNotesEditText;

    private static long mID;
    private Toolbar mToolbar;

    // Creates interface to pass data to Main Activity
    public interface OnEditDataPass {
        public void onEditDataPass(String name, Date date, String status, String notes);
    }

    private OnEditDataPass dataPasser;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        dataPasser = (OnEditDataPass) activity;
    }

    // Passes either a new or updated name, date, and notes
    public void passData(String name, Date date, String status, String notes) {
        dataPasser.onEditDataPass(name, date, status, notes);
    }

    public EditTaskDialogFragment() {
        // Required empty public constructor
    }

    // Creates a new instance and and sets the id of the current task to be edited
    // Or if the id passes is -1, a new task is being created
    public static EditTaskDialogFragment newInstance(long id) {
        EditTaskDialogFragment fragment = new EditTaskDialogFragment();
        mID = id;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_details_dialog, container);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mToolbar = (Toolbar) view.findViewById(R.id.tbDetailsToolbar);
        mToolbar.inflateMenu(R.menu.new_task_menu);

        TextView toolbarTitle = (TextView) view.findViewById(R.id.tvDetailsToolbarTitle);
        Typeface typeface = Typeface.createFromAsset(getActivity().getApplication().getAssets(), "fonts/KGBlankSpaceSketch.ttf");
        toolbarTitle.setTypeface(typeface);

        // Initializes the the values of the views
        // If editing the task, the values are populated with details of the task
        // If creating a new task, the values are populated with default values
        mNameEditText = (EditText) view.findViewById(R.id.etNewName);
        mDatePicker = (DatePicker) view.findViewById(R.id.dpDatePicker);
        mStatusSpinner = (Spinner) view.findViewById(R.id.statusSpinner);
        mNotesEditText = (EditText) view.findViewById(R.id.etNewNotes);

        String name = mID == -1 ? "" : Task.load(Task.class, mID).name;
        mNameEditText.setText(name);

        Date date = mID == -1 ? new Date() : Task.load(Task.class, mID).date;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        mDatePicker.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.status_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mStatusSpinner.setAdapter(adapter);

        if (mID != -1 && Task.load(Task.class, mID).completed) {
            mStatusSpinner.setSelection(1);
        }

        String notes = mID == -1 ? "" : Task.load(Task.class, mID).notes;
        mNotesEditText.setText(notes);

        mToolbar.setOnMenuItemClickListener(this);
    }

    @Override
    public void onResume() {
        // Makes the dialog appear like an activity
        Window window = getDialog().getWindow();

        ViewGroup.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes((WindowManager.LayoutParams) params);

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(Color.parseColor("#1d2d44"));
        }

        // Keyboard pop up automatically if creating a new task
        if (mID == -1) {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }

        super.onResume();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        // If a new task is being created save it
        // Otherwise update the current task
        if (item.getItemId() == R.id.menu_save && checkValidForm()) {

            Task task;

            if (mID == -1) {
                task = new Task();

            } else {
                task = Task.load(Task.class, mID);
            }

            task.name = mNameEditText.getText().toString();

            Calendar calendar = Calendar.getInstance();
            calendar.set(mDatePicker.getYear(), mDatePicker.getMonth(), mDatePicker.getDayOfMonth());
            task.date = calendar.getTime();

            task.completed = mStatusSpinner.getSelectedItemId() == 0 ? false : true;
            String status = mStatusSpinner.getSelectedItemId() == 0 ? "TO-DO" : "COMPLETED";

            task.notes = mNotesEditText.getText().toString();

            task.save();

            passData(task.name, calendar.getTime(), status, task.notes);
            dismiss();
        }

        return true;
    }

    // Prevents empty task name
    private boolean checkValidForm() {
        if (mNameEditText.getText().toString().trim().equals("")) {
            Toast.makeText(getContext(), "Task name cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}
