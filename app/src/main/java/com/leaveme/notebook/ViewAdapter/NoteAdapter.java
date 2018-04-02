package com.leaveme.notebook.ViewAdapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.leaveme.notebook.DataModel.DaoSession;
import com.leaveme.notebook.DataModel.Note;
import com.leaveme.notebook.DataModel.NoteDao;
import com.leaveme.notebook.DataModel.util.GreenDaoHelper;
import com.leaveme.notebook.NoteActivity;
import com.leaveme.notebook.R;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by m_space on 2018/4/1.
 */

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ItemViewHolder> {

    private List<Note> notes = new ArrayList<>();
    private Context context;
    private DaoSession session;
    private NoteDao noteDao;
    public NoteAdapter(Context context){
        this.context = context;
        init();
    }

    private void init(){
        session = GreenDaoHelper.getDaoSession(context);
        noteDao= session.getNoteDao();
        Log.e("TAG","init");
        notes = noteDao.loadAll();
    }


    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
        ItemViewHolder itemViewHolder = new ItemViewHolder(view);
        Log.e("TAG","onCreateViewHolder");
        return itemViewHolder;
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, final int position) {
        Log.e("TAG","onBindViewHolder");
        RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder)holder;
        ViewGroup.LayoutParams layoutParams = viewHolder.itemView.getLayoutParams();
        layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;

        holder.content.setText(notes.get(position).getContent());
        holder.time.setText(notes.get(position).getTimeStamp()+"");
        holder.title.setText(notes.get(position).getTitle());

        ((RecyclerView.ViewHolder) holder).itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(context, NoteActivity.class);
                intent.setAction(notes.get(position).getTimeStamp()+"");
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        Log.e("TAG","getItemCount");
        if(noteDao.count()!=notes.size()){
            notes = noteDao.loadAll();
            return notes.size();
        }
        return notes.size();
    }


    public static class ItemViewHolder extends RecyclerView.ViewHolder{

        public final TextView title;
        public final TextView time;
        public final TextView content;
        public ItemViewHolder(View itemView) {
            super(itemView);
            title = (TextView)itemView.findViewById(R.id.tv_item_tile);
            time = (TextView)itemView.findViewById(R.id.tv_item_time);
            content = (TextView)itemView.findViewById(R.id.tv_item_content);
        }
    }
}
