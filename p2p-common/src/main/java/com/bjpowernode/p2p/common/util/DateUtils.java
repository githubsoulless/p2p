package com.bjpowernode.p2p.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {
    public static Date getDateByAddDays(Date date, Integer count) {
        //创建一个日期处理类对象
        Calendar calendar = Calendar.getInstance();

        //设置日期处理类的时间
        calendar.setTime(date);

        //添加日期的天数
        calendar.add(Calendar.DAY_OF_MONTH,count);


        return calendar.getTime();
    }


    public static void main(String[] args) throws ParseException {
        System.out.println(getDateByAddDays(new SimpleDateFormat("yyyy-MM-dd").parse("2008-08-08"),-1));
    }
    public static Date getDateByAddMonths(Date date, Integer count) {
        //创建一个日期处理类对象
        Calendar calendar = Calendar.getInstance();

        //设置日期处理类的时间
        calendar.setTime(date);

        //添加日期的月数
        calendar.add(Calendar.MONTH,count);


        return calendar.getTime();
    }

    public static String getTimeStamp() {
        return new SimpleDateFormat("yyyyMMddmmssSSS").format(new Date());
    }
}
