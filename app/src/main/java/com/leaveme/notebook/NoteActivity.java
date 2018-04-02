package com.leaveme.notebook;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.leaveme.notebook.DataModel.DaoSession;
import com.leaveme.notebook.DataModel.Note;
import com.leaveme.notebook.DataModel.NoteDao;
import com.leaveme.notebook.DataModel.util.GreenDaoHelper;
import com.leaveme.notebook.R;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class NoteActivity extends AppCompatActivity {

    private EditText title;
    private EditText content;

    private Note note;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        DaoSession session = GreenDaoHelper.getDaoSession(this);
        NoteDao noteDao= session.getNoteDao();

        Intent intent = getIntent();
        String time = intent.getAction();
        long currentTime = 0;
        if(null==time||time.equals("")){
            currentTime = new Date().getTime();
            note = new Note();
            note.setId(noteDao.count());
            note.setContent("");
            note.setTitle("");
            note.setPictureId("");
            note.setState(0);
            note.setTimeStamp(currentTime);
        }else {
            currentTime = Long.parseLong(time.trim());
            List<Note> n = noteDao.queryBuilder().where(NoteDao.Properties.TimeStamp.eq(currentTime)).list();
            if(n.size()>0) {
                note = n.get(0);
            }else {
                Toast.makeText(this,"something error",Toast.LENGTH_SHORT).show();
            }
        }

        initView();
    }

    private void initView(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storeNote();
                NoteActivity.this.finish();
            }
        });
        title = (EditText) findViewById(R.id.edt_title);
        content = (EditText)findViewById(R.id.edt_content);

        title.setText(note.getTitle());
        content.setText(note.getContent());
    }

    private void storeNote(){
        DaoSession session = GreenDaoHelper.getDaoSession(this);
        NoteDao noteDao= session.getNoteDao();
        note.setContent(content.getText().toString());
        note.setTitle(title.getText().toString());
        noteDao.insertOrReplace(note);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        storeNote();
        finish();
    }
}
