package lk.mytodo;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddTaskFragment extends Fragment {

    private EditText editTextTitle, editTextDescription;
    private TextView textViewDueDateTime;
    private Spinner spinnerReminder, spinnerPriority;
    private Switch switchCompleteWithinADay;
    private Button buttonAddTask, buttonReset;
    private TextView dateError;

    private FirebaseFirestore db;

    public AddTaskFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_task, container, false);

        db = FirebaseFirestore.getInstance();

        editTextTitle = view.findViewById(R.id.editTextText);
        editTextDescription = view.findViewById(R.id.editTextText2);
        textViewDueDateTime = view.findViewById(R.id.textView21);
        spinnerReminder = view.findViewById(R.id.spinnerReminder);
        spinnerPriority = view.findViewById(R.id.spinnerPriority);
        switchCompleteWithinADay = view.findViewById(R.id.switch1);
        buttonAddTask = view.findViewById(R.id.buttonAddTask);
        buttonReset = view.findViewById(R.id.button3);
        buttonAddTask.setOnClickListener(v -> addTask());
        buttonReset.setOnClickListener(v -> resetFields());
        dateError = view.findViewById(R.id.date_error);
        TextView dateTime = view.findViewById(R.id.textView21);
        Button button = view.findViewById(R.id.button2);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get current date and time
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // On date selected, open TimePickerDialog
                        final Calendar selectedDate = Calendar.getInstance();
                        selectedDate.set(Calendar.YEAR, year);
                        selectedDate.set(Calendar.MONTH, monthOfYear);
                        selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        int hour = c.get(Calendar.HOUR_OF_DAY);
                        int minute = c.get(Calendar.MINUTE);

                        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                // Set selected date and time to the TextView
                                selectedDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                selectedDate.set(Calendar.MINUTE, minute);

                                String dateTimeStr = String.format("%02d/%02d/%04d %02d:%02d", selectedDate.get(Calendar.DAY_OF_MONTH), selectedDate.get(Calendar.MONTH) + 1, selectedDate.get(Calendar.YEAR), selectedDate.get(Calendar.HOUR_OF_DAY), selectedDate.get(Calendar.MINUTE));

                                dateTime.setText(dateTimeStr);
                            }
                        }, hour, minute, true);

                        timePickerDialog.show();
                    }
                }, year, month, day);

                datePickerDialog.show();
            }
        });

        Spinner spinner = view.findViewById(R.id.spinnerReminder);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.reminder_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);


        Spinner spinner2 = view.findViewById(R.id.spinnerPriority);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(getContext(), R.array.pr2, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(adapter2);
        return view;
    }

    private void addTask() {
        String title = editTextTitle.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        String dueDateTimeStr = textViewDueDateTime.getText().toString().trim();
        String reminder = spinnerReminder.getSelectedItem().toString();
        String priority = spinnerPriority.getSelectedItem().toString();
        boolean completeWithinADay = switchCompleteWithinADay.isChecked();
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        String storedEmail = sharedPreferences.getString("email", null);

        if (TextUtils.isEmpty(title)) {
            editTextTitle.setError("Title is required.");
            return;
        }

        if (TextUtils.isEmpty(description)) {
            editTextDescription.setError("Description is required.");
            return;
        }

        if (TextUtils.isEmpty(dueDateTimeStr)) {
            textViewDueDateTime.setError("Due date and time are required.");
            return;
        }

        // Parse the due date and time
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        Date dueDateTime;
        try {
            dueDateTime = dateFormat.parse(dueDateTimeStr);
        } catch (ParseException e) {
            dateError.setText("Invalid date and time format.");
            return;
        }

        // Check if the due date and time have passed
        if (dueDateTime.before(new Date())) {
            dateError.setText("Due date and time have already passed.");
            return;
        }else{
            dateError.setText("");
        }

        Task task = new Task(title, description, dueDateTimeStr, reminder, priority, completeWithinADay, storedEmail);

        db.collection("tasks").add(task).addOnSuccessListener(documentReference -> {
            Toast.makeText(getActivity(), "Task added successfully!", Toast.LENGTH_SHORT).show();
            resetFields();
        }).addOnFailureListener(e -> {
            Toast.makeText(getActivity(), "Failed to add task: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }



    private long getReminderTimeInMillis(String reminder) {
        switch (reminder) {
            case "5 minutes before":
                return 5 * 60 * 1000;
            case "10 minutes before":
                return 10 * 60 * 1000;
            case "30 minutes before":
                return 30 * 60 * 1000;
            case "1 hour before":
                return 60 * 60 * 1000;
            default:
                return 0;
        }
    }

    private Calendar parseReminderTime(String dueDateTime, String reminder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        Calendar dueDateCalendar = Calendar.getInstance();
        try {
            Date dueDate = dateFormat.parse(dueDateTime);
            dueDateCalendar.setTime(dueDate);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        switch (reminder) {
            case "10 minutes before":
                dueDateCalendar.add(Calendar.MINUTE, -10);
                break;
            case "30 minutes before":
                dueDateCalendar.add(Calendar.MINUTE, -30);
                break;
            case "1 hour before":
                dueDateCalendar.add(Calendar.HOUR, -1);
                break;
            case "1 day before":
                dueDateCalendar.add(Calendar.DAY_OF_MONTH, -1);
                break;
            default:
                return null;
        }

        return dueDateCalendar;
    }

    private boolean isValidDateTime(String dateTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        sdf.setLenient(false);
        try {
            Date parsedDate = sdf.parse(dateTime);
            if (parsedDate.before(new Date())) {
                return false;
            }
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    private void resetFields() {
        editTextTitle.setText("");
        editTextDescription.setText("");
        textViewDueDateTime.setText("0");
        spinnerReminder.setSelection(0);
        spinnerPriority.setSelection(0);
        switchCompleteWithinADay.setChecked(false);
    }
}
