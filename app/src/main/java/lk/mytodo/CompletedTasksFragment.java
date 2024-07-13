package lk.mytodo;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class CompletedTasksFragment extends Fragment {

    private List<Task> completedTaskList;
    private CompletedTaskAdapter taskAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_completed_tasks, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.completed_tasks_recy);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        completedTaskList = new ArrayList<>();
        taskAdapter = new CompletedTaskAdapter(completedTaskList);
        recyclerView.setAdapter(taskAdapter);

        fetchCompletedTasks(); // Fetch completed tasks from Firestore

        return view;
    }

    private void fetchCompletedTasks() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        String storedEmail = sharedPreferences.getString("email", null);
        // Initialize Firestore instance
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Query Firestore for completed tasks
        db.collection("tasks")
                .whereEqualTo("complete", true).whereEqualTo("email", storedEmail)  // Filter completed tasks
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    completedTaskList.clear(); // Clear the existing list
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        // Convert each document snapshot to a Task object and add it to the list
                        Task task = documentSnapshot.toObject(Task.class);
                        completedTaskList.add(task);
                    }
                    taskAdapter.notifyDataSetChanged(); // Notify the adapter about the data change
                })
                .addOnFailureListener(e -> {
                    // Handle errors
                    Toast.makeText(getContext(), "Error fetching completed tasks: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
