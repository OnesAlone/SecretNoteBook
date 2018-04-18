package com.leaveme.notebook;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.leaveme.notebook.DataModel.DaoSession;
import com.leaveme.notebook.DataModel.Note;
import com.leaveme.notebook.DataModel.NoteDao;
import com.leaveme.notebook.DataModel.util.GreenDaoHelper;
import com.leaveme.notebook.R;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class NoteActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText title;
    private EditText content;
    private Toolbar toolbar;

    private FloatingActionsMenu menu;

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
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storeNote();
                NoteActivity.this.finish();
            }
        });

        if (note.getState()==0){
            toolbar.setSubtitle("当前文档未加密");
        }else {
            toolbar.setSubtitle("当前文档已加密");
        }
        title = (EditText) findViewById(R.id.edt_title);
        content = (EditText)findViewById(R.id.edt_content);

        title.setText(note.getTitle());
        content.setText(note.getContent());

        findViewById(R.id.fb_encrypt).setOnClickListener(this);
        menu = (FloatingActionsMenu)findViewById(R.id.fm_note);
    }

    private void storeNote(){
        if(content.getText().toString().equals("")&&content.getText().toString().equals("")){
            return;
        }
        DaoSession session = GreenDaoHelper.getDaoSession(this);
        NoteDao noteDao= session.getNoteDao();
        note.setContent(content.getText().toString());
        note.setTitle(title.getText().toString());
        noteDao.insertOrReplace(note);
    }

    @Override
    protected void onPause() {
        super.onPause();
        storeNote();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fb_encrypt:
                int state = note.getState();
                if(state == 0){
                    note.setState(1);
                    toolbar.setSubtitle("当前文档已加密");
                    Toast.makeText(NoteActivity.this,"已加密成功",Toast.LENGTH_SHORT).show();
                }else if (state ==1){
                    note.setState(0);
                    toolbar.setSubtitle("当前文档未加密");
                    Toast.makeText(NoteActivity.this,"已取消加密",Toast.LENGTH_SHORT).show();
                }
                menu.collapse();
                break;
        }
    }
}
