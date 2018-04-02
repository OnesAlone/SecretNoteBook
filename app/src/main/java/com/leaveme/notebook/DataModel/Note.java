package com.leaveme.notebook.DataModel;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by m_space on 2018/4/1.
 */

@Entity
public class Note {
    @Id(autoincrement = true)
    private long id;

    private String title;//笔记标题
    private String content;//笔记内容
    private long timeStamp;//笔记时间
    private int state;//笔记状态
    private String pictureId;//笔记中的图片id
    @Generated(hash = 1047120823)
    public Note(long id, String title, String content, long timeStamp, int state,
            String pictureId) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.timeStamp = timeStamp;
        this.state = state;
        this.pictureId = pictureId;
    }
    @Generated(hash = 1272611929)
    public Note() {
    }
    public long getId() {
        return this.id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getTitle() {
        return this.title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getContent() {
        return this.content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public long getTimeStamp() {
        return this.timeStamp;
    }
    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
    public int getState() {
        return this.state;
    }
    public void setState(int state) {
        this.state = state;
    }
    public String getPictureId() {
        return this.pictureId;
    }
    public void setPictureId(String pictureId) {
        this.pictureId = pictureId;
    }

}
