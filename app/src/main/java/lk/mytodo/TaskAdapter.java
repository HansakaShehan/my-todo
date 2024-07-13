package lk.mytodo;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private List<Task> taskList;
    private Context context;
    private OnTaskItemClickListener itemClickListener;

    public TaskAdapter(List<Task> taskList, Context context) {
        this.taskList = taskList;
        this.context = context;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.bind(task);
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public interface OnTaskItemClickListener {
        void onTaskItemClick(Task task);
    }

    class TaskViewHolder extends RecyclerView.ViewHolder {
        private TextView nameTextView;
        private TextView dueDateTextView;
        private TextView priority;
        private CheckBox completeCheckBox;
        private ConstraintLayout taskLayout;
        private TextView des;

        private ImageView deleteBtn;

        TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.text_task_name);
            dueDateTextView = itemView.findViewById(R.id.text_task_due_date);
            priority = itemView.findViewById(R.id.textView16);
            completeCheckBox = itemView.findViewById(R.id.checkBox); // Ensure your item_task.xml has this CheckBox
            taskLayout = itemView.findViewById(R.id.linearLayout);
            des = itemView.findViewById(R.id.text_task_description);
            deleteBtn=itemView.findViewById(R.id.image_delete);
        }

        void bind(Task task) {
            nameTextView.setText(task.getTitle());
            dueDateTextView.setText(task.getDueDateTime());
            priority.setText(task.getPriority());
            completeCheckBox.setChecked(task.isComplete());
            des.setText(task.getDescription());

            // Define the date format pattern
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            try {
                Date dueDateTime = sdf.parse(task.getDueDateTime());
                Date currentDateTime = new Date();

                if (dueDateTime != null && dueDateTime.before(currentDateTime)) {
                    // Due date and time have passed
                    taskLayout.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.overdueBackground));
                    nameTextView.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.overdueText));
                    dueDateTextView.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.overdueText));
                } else {
                    // Due date and time are in the future or equal to current time
                    if (task.isCompleteWithinADay()) {
                        // Set background color for tasks to be completed within a day
                        taskLayout.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.colorWithinADay));
                    } else {
                        // Set default background color
                        taskLayout.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.gray));
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

            completeCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    // Mark task as complete in Firestore and remove it from the list
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("tasks").document(task.getId()).update("complete", true).addOnSuccessListener(aVoid -> {
                        // Remove task from local list
                        int position = getAdapterPosition();
                        taskList.remove(position);
                        notifyItemRemoved(position);

                        // Optionally, show a Toast message
                        Toast.makeText(context, "Task marked as complete and removed", Toast.LENGTH_SHORT).show();
                    }).addOnFailureListener(e -> Log.e("TaskAdapter", "Error updating task status", e));
                }
            });

            deleteBtn.setOnClickListener(view -> {
                // Perform the deletion operation here
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Confirm Delete")
                        .setMessage("Are you sure you want to delete this task?")
                        .setPositiveButton("Yes", (dialogInterface, i) -> {
                            // Delete the task from Firestore and remove it from the list
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            db.collection("tasks").document(task.getId())
                                    .delete()
                                    .addOnSuccessListener(aVoid -> {
                                        // Remove the task from the local list
                                        int position = getAdapterPosition();
                                        taskList.remove(position);
                                        notifyItemRemoved(position);

                                        // Optionally, show a Toast message
                                        Toast.makeText(context, "Task deleted successfully", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> Log.e("TaskAdapter", "Error deleting task", e));
                        })
                        .setNegativeButton("No", null)
                        .show();
            });

        }
    }
}
