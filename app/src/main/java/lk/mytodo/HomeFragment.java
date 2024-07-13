package lk.mytodo;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment  {

    private List<Task> taskList;
    private TaskAdapter taskAdapter;
    private String selectedFilter = "All";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View viewMin = inflater.inflate(R.layout.fragment_home, container, false);

        Spinner spinner = viewMin.findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.pr, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // Setup the filter Spinner
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                // Get the selected filter
                selectedFilter = adapterView.getItemAtPosition(position).toString();
                // Refresh the task list based on the selected filter
                getUserTasksFromFirestore(viewMin);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Do nothing
            }
        });

        // Fetch user tasks initially without applying any filter
        getUserTasksFromFirestore(viewMin);

        return viewMin;
    }

    private void doFilter() {

    }

    private void getUserTasksFromFirestore(View view) {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        String storedEmail = sharedPreferences.getString("email", null);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            // Query Firestore based on the selected filter
            if (selectedFilter.equals("All")) {
                // Fetch all tasks
                db.collection("tasks").whereEqualTo("email", storedEmail).whereEqualTo("complete", false).get().addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Task> userTasks = new ArrayList<>();
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        Task task = documentSnapshot.toObject(Task.class);
                        task.setId(documentSnapshot.getId()); // Set the document ID
                        userTasks.add(task);
                        Log.d("HOME", "Task ID: " + task.getId());
                        Log.d("HOME", "Task Title: " + task.getTitle());
                    }
                    RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

                    taskAdapter = new TaskAdapter(userTasks, getContext()); // Pass context here
                    recyclerView.setAdapter(taskAdapter);
                }).addOnFailureListener(e -> {
                    // Handle errors
                    // ...
                });
            } else if (selectedFilter.equals("Incomplete")) {
                // Fetch all tasks
                db.collection("tasks").whereEqualTo("email", storedEmail).whereEqualTo("complete", false).get().addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Task> userTasks = new ArrayList<>();
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        Task task = documentSnapshot.toObject(Task.class);
                        task.setId(documentSnapshot.getId()); // Set the document ID
                        userTasks.add(task);
                        Log.d("HOME", "Task ID: " + task.getId());
                        Log.d("HOME", "Task Title: " + task.getTitle());
                    }
                    RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

                    taskAdapter = new TaskAdapter(userTasks, getContext()); // Pass context here
                    recyclerView.setAdapter(taskAdapter);
                }).addOnFailureListener(e -> {
                    // Handle errors
                    // ...
                });
            } else if (selectedFilter.equals("Complete within a day")) {
                // Fetch all tasks
                db.collection("tasks").whereEqualTo("email", storedEmail).whereEqualTo("complete", false).whereEqualTo("completeWithinADay", true).get().addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Task> userTasks = new ArrayList<>();
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        Task task = documentSnapshot.toObject(Task.class);
                        task.setId(documentSnapshot.getId()); // Set the document ID
                        userTasks.add(task);
                        Log.d("HOME", "Task ID: " + task.getId());
                        Log.d("HOME", "Task Title: " + task.getTitle());
                    }
                    RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

                    taskAdapter = new TaskAdapter(userTasks, getContext()); // Pass context here
                    recyclerView.setAdapter(taskAdapter);
                }).addOnFailureListener(e -> {
                    // Handle errors
                    // ...
                });
            } else {
                // Fetch tasks based on priority
                db.collection("tasks").whereEqualTo("email", storedEmail).whereEqualTo("priority", selectedFilter).whereEqualTo("complete", false).get().addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Task> userTasks = new ArrayList<>();
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        Task task = documentSnapshot.toObject(Task.class);
                        task.setId(documentSnapshot.getId()); // Set the document ID
                        userTasks.add(task);
                        Log.d("HOME", "Task ID: " + task.getId());
                        Log.d("HOME", "Task Title: " + task.getTitle());
                    }
                    RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

                    taskAdapter = new TaskAdapter(userTasks, getContext()); // Pass context here
                    recyclerView.setAdapter(taskAdapter);
                }).addOnFailureListener(e -> {
                    // Handle errors
                    // ...
                });
            }
        } else {
            Log.e("HOME", "Current user is null");
            Toast.makeText(getContext(), "User not authenticated", Toast.LENGTH_SHORT).show();
        }
    }


}
