package net.penguincoders.doit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import net.penguincoders.doit.Adapters.ToDoAdapter;
import net.penguincoders.doit.Model.ToDoModel;
import net.penguincoders.doit.Utils.DatabaseHandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements DialogCloseListener {

    private DatabaseHandler db;
    private RecyclerView tasksRecyclerView;
    private ToDoAdapter tasksAdapter;
    private FloatingActionButton fab;
    private List<ToDoModel> taskList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        initializeComponents();
        setupRecyclerView();
        loadTasksFromDatabase();
        setClickListeners();
    }

    private void initializeComponents() {
        db = new DatabaseHandler(this);
        db.openDatabase();
        tasksRecyclerView = findViewById(R.id.tasksRecyclerView);
        fab = findViewById(R.id.fab);
    }

    private void setupRecyclerView() {
        tasksAdapter = new ToDoAdapter(db, MainActivity.this);
        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        tasksRecyclerView.setAdapter(tasksAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new RecyclerItemTouchHelper(tasksAdapter));
        itemTouchHelper.attachToRecyclerView(tasksRecyclerView);
    }

    private void loadTasksFromDatabase() {
        taskList.clear();
        taskList.addAll(db.getAllTasks());
        Collections.reverse(taskList);
        tasksAdapter.setTasks(taskList);
        tasksAdapter.notifyDataSetChanged();
    }

    private void setClickListeners() {
        fab.setOnClickListener(v -> openAddNewTaskDialog());
    }

    private void openAddNewTaskDialog() {
        AddNewTask.newInstance().show(getSupportFragmentManager(), AddNewTask.TAG);
    }

    @Override
    public void handleDialogClose(DialogInterface dialog) {
        loadTasksFromDatabase();
    }
}
