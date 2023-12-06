package net.penguincoders.doit;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import net.penguincoders.doit.Adapters.ToDoAdapter;
import net.penguincoders.doit.Model.ToDoModel;
import net.penguincoders.doit.Utils.DatabaseHandler;

import java.util.Objects;

public class AddNewTask extends BottomSheetDialogFragment {
    private static final String TAG = "ActionBottomDialog";
    private EditText newTaskText;
    private Button newTaskSaveButton;
    private DatabaseHandler db;
    private boolean isUpdate = false;
    private ToDoModel taskToUpdate;

    public static AddNewTask newInstance() {
        return new AddNewTask();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.DialogStyle);
        db = new DatabaseHandler(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_task, container, false);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        newTaskText = view.findViewById(R.id.newTaskText);
        newTaskSaveButton = view.findViewById(R.id.newTaskButton);
        Bundle bundle = getArguments();
        if (bundle != null) {
            isUpdate = true;
            taskToUpdate = new ToDoModel();
            taskToUpdate.setId(bundle.getInt("id"));
            String task = bundle.getString("task");
            newTaskText.setText(task);
            if (task != null && !task.isEmpty()) {
                newTaskSaveButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark));
            }
        }

        newTaskSaveButton.setOnClickListener(v -> saveTask());
        newTaskText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().isEmpty()) {
                    newTaskSaveButton.setEnabled(false);
                    newTaskSaveButton.setTextColor(Color.GRAY);
                } else {
                    newTaskSaveButton.setEnabled(true);
                    newTaskSaveButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void saveTask() {
        String text = newTaskText.getText().toString().trim();
        if (isUpdate) {
            taskToUpdate.setTask(text);
            db.updateTask(taskToUpdate);
        } else {
            ToDoModel newTask = new ToDoModel(text, 0);
            db.insertTask(newTask);
        }
        dismiss();
        if (getActivity() instanceof DialogCloseListener) {
            ((DialogCloseListener) getActivity()).handleDialogClose(requireDialog());
        }
    }
}

