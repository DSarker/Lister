package com.industries.sarker.lister.dialogs;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.industries.sarker.lister.R;

public class DeleteTaskDialogFragment extends DialogFragment implements View.OnClickListener {

    private TextView mDialogTitleTextView;
    private Button mYesButton;
    private Button mNoButton;

    // Creates interface to pass data to Edit Task Dialog Fragment
    public interface OnDeleteDataPass {
        public void onDeleteDataPass(boolean deleted);
    }

    private OnDeleteDataPass dataPasser;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        dataPasser = (OnDeleteDataPass) activity;
    }

    // Passes boolean whether or not the task should deleted
    public void passData(boolean delete) {
        dataPasser.onDeleteDataPass(delete);
    }

    public DeleteTaskDialogFragment() {
        // Required empty public constructor
    }

    public static DeleteTaskDialogFragment newInstance(String title) {
        DeleteTaskDialogFragment fragment = new DeleteTaskDialogFragment();
        Bundle args = new Bundle();
        args.putString("TITLE", title);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_delete_task_dialog, container);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mDialogTitleTextView = (TextView) view.findViewById(R.id.tvDeleteDialogTitle);
        mDialogTitleTextView.setText(getArguments().getString("TITLE"));

        mYesButton = (Button) view.findViewById(R.id.btnDeleteYes);
        mNoButton = (Button) view.findViewById(R.id.btnDeleteNo);

        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        mYesButton.setOnClickListener(this);
        mNoButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnDeleteYes) {
            passData(true);
        }

        dismiss();
    }
}
