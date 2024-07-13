package lk.mytodo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CompletedTaskAdapter extends RecyclerView.Adapter<CompletedTaskAdapter.CompletedTaskViewHolder> {

    private List<Task> completedTaskList;

    public CompletedTaskAdapter(List<Task> completedTaskList) {
        this.completedTaskList = completedTaskList;
    }

    @NonNull
    @Override
    public CompletedTaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_completed_task, parent, false);
        return new CompletedTaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CompletedTaskViewHolder holder, int position) {
        Task task = completedTaskList.get(position);
        holder.taskName.setText(task.getTitle());
        holder.taskDueDate.setText(task.getDueDateTime());
    }

    @Override
    public int getItemCount() {
        return completedTaskList.size();
    }

    static class CompletedTaskViewHolder extends RecyclerView.ViewHolder {
        TextView taskName;
        TextView taskDueDate;

        public CompletedTaskViewHolder(@NonNull View itemView) {
            super(itemView);
            taskName = itemView.findViewById(R.id.text_task_name);
            taskDueDate = itemView.findViewById(R.id.text_task_due_date);
        }
    }
}
