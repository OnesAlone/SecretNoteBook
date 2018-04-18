package com.leaveme.notebook;

import java.util.Calendar;

/**
 * Created by m_space on 2018/4/2.
 */

public class dateFormatString {
    public static String transform(long time){
        String month,day,year,hour,minute,second;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        day = calendar.get(Calendar.DAY_OF_MONTH)+"\n\r";
        month = calendar.get(Calendar.MONTH)+1+"/";
        year = calendar.get(Calendar.YEAR)+"/";
        hour = calendar.get(Calendar.HOUR_OF_DAY)+":";
        minute = calendar.get(Calendar.MINUTE)+":";
        second = calendar.get(Calendar.SECOND)+"";
        return year+month+day+hour+minute+second;
    }
}
