package com.leaveme.notebook.ViewAdapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.leaveme.notebook.CommomDialog;
import com.leaveme.notebook.DataModel.DaoSession;
import com.leaveme.notebook.DataModel.Note;
import com.leaveme.notebook.DataModel.NoteDao;
import com.leaveme.notebook.DataModel.util.GreenDaoHelper;
import com.leaveme.notebook.NoteActivity;
import com.leaveme.notebook.R;
import com.leaveme.notebook.dateFormatString;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Created by m_space on 2018/4/1.
 */

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ItemViewHolder> implements ItemTouchHelperAdapter{

    private List<Note> notes = new ArrayList<>();
    private Context context;
    private DaoSession session;
    private NoteDao noteDao;

    private final OnStartDragListener mDragStartListener;
    private OnItemClickListener onItemClickListener;

    //记录当前点击位置
    private int notePosition;

    public NoteAdapter(Context context, OnStartDragListener dragStartListener){
        mDragStartListener = dragStartListener;
        this.context = context;
        init();
    }

    private void init(){
        session = GreenDaoHelper.getDaoSession(context);
        noteDao= session.getNoteDao();
        notes = noteDao.queryBuilder().where(NoteDao.Properties.State.ge(0)).list();
    }


    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
        ItemViewHolder itemViewHolder = new ItemViewHolder(view);
        return itemViewHolder;
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, final int position) {
        RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder)holder;
        ViewGroup.LayoutParams layoutParams = viewHolder.itemView.getLayoutParams();
        layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;

        if(notes.get(position).getState()==0) {
            holder.content.setText(notes.get(position).getContent());
        }else {
            holder.content.setText("核心机密!!!");
        }
        holder.time.setText(dateFormatString.transform(notes.get(position).getTimeStamp()));
        holder.title.setText(notes.get(position).getTitle());

        ((RecyclerView.ViewHolder) holder).itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notePosition = position;
                if (notes.get(position).getState()==1) {
                    onItemClickListener.onItemClick(v, position);
                }else {
                    openNote();
                }
            }
        });
    }

    public void openNote(){
        Intent intent = new Intent();
        intent.setClass(context, NoteActivity.class);
        intent.setAction(notes.get(notePosition).getTimeStamp()+"");
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        //对文档中的内容条目进行动态刷新，当出现删除或者新增操作时，能够及时的通知UI界面刷新内容
        int size = noteDao.queryBuilder().where(NoteDao.Properties.State.ge(0)).list().size();
        if(size!=notes.size()){
            notes = noteDao.queryBuilder().where(NoteDao.Properties.State.ge(0)).list();
            return notes.size();
        }
        return notes.size();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(notes, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onItemDismiss(final int position) {
        new CommomDialog(context, R.style.dialog, "您确定删除此条记录？", new CommomDialog.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog, boolean confirm) {
                if(confirm){
                    Note n = notes.get(position);
                    n.setState(-1);
                    session.getNoteDao().update(n);
                    notes.remove(position);
                    notifyItemRemoved(position);
                    dialog.dismiss();
                }else {
                    NoteAdapter.this.notifyDataSetChanged();
                }
            }
        }).setTitle("提示").show();
    }

    public static interface  OnItemClickListener{
        void onItemClick(View v,int position);
    }
    public void setOnItemClickListener(OnItemClickListener itemClickListener){
        this.onItemClickListener = itemClickListener;
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder{

        public final TextView title;
        public final TextView time;
        public final TextView content;
        public ItemViewHolder(View itemView) {
            super(itemView);
            title = (TextView)itemView.findViewById(R.id.tv_item_tile);
            time = (TextView)itemView.findViewById(R.id.tv_item_time);
            content = (TextView)itemView.findViewById(R.id.tv_item_content);
        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }
    }
}
