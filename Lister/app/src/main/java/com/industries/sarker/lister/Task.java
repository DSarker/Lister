package com.industries.sarker.lister;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.Date;
import java.util.List;

// Active Android database schema
@Table(name = "Tasks")
public class Task extends Model {
    @Column(name = "Name", index = true)
    public String name;

    @Column(name = "Date", index = true)
    public Date date;

    @Column(name = "Notes", index = true)
    public String notes;

    @Column(name = "completed", index = true)
    public boolean completed = false;

    public Task() {
        super();
    }

    public static List<Task> getAllTasks() {
        return new Select().from(Task.class).orderBy("Date").execute();
    }
}
