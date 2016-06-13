package com.industries.sarker.lister.mainscreen;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.industries.sarker.lister.R;
import com.industries.sarker.lister.Task;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public TextView dateTextView;

        public ViewHolder(final View itemView) {
            super(itemView);

            nameTextView = (TextView) itemView.findViewById(R.id.tvItemName);
            dateTextView = (TextView) itemView.findViewById(R.id.tvItemDate);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (sItemClickListener != null) {
                        sItemClickListener.onItemClick(itemView, getLayoutPosition());
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (sItemLongClickListener != null) {
                        sItemLongClickListener.onItemLongClick(itemView, getLayoutPosition());
                    }
                    return true;
                }
            });
        }
    }

    private List<Task> mTaskList;

    private static OnItemClickListener sItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        sItemClickListener = listener;
    }

    private static OnItemLongClickListener sItemLongClickListener;

    public interface OnItemLongClickListener {
        void onItemLongClick(View itemView, int position);
    }

    public void setSetOnItemLongClickListener(OnItemLongClickListener listener) {
        sItemLongClickListener = listener;
    }

    public TaskAdapter(List<Task> taskList) {
        mTaskList = taskList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View taskView = layoutInflater.inflate(R.layout.item_task, parent, false);

        ViewHolder viewHolder = new ViewHolder(taskView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Task task = mTaskList.get(position);

        TextView nameTextView = holder.nameTextView;
        TextView dateTextView = holder.dateTextView;

        nameTextView.setText(task.name);

        if (task.completed) {
            nameTextView.setPaintFlags(nameTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            nameTextView.setPaintFlags(nameTextView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }

        dateTextView.setText(String.format("%tb \t %<te", task.date));
    }

    @Override
    public int getItemCount() {
        return mTaskList.size();
    }

}
