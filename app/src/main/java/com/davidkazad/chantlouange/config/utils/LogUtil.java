package com.davidkazad.chantlouange.config.utils;

import android.util.Log;

import com.davidkazad.chantlouange.config.Common;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.UUID;

import static com.mikepenz.iconics.Iconics.TAG;

public class LogUtil {

    private String user;
    private String tag;
    private String msg;
    private long timestamp;

    public LogUtil() {
    }

    private LogUtil(String tag, String msg) {

        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        if (firebaseUser != null) {
            this.user = firebaseUser.getUid();
        }else user = UUID.randomUUID().toString();

        this.tag = tag;
        this.msg = msg;
        this.timestamp = System.currentTimeMillis();

        Log.d(TAG, msg);
    }

    public static void d() {
        Throwable t = new Throwable();
        StackTraceElement[] elements = t.getStackTrace();
        String callerClassName = elements[1].getClassName();
        String callerMethod = elements[1].getMethodName();
        int callerLineNumber = elements[1].getLineNumber();

        LogUtil logUtil = new LogUtil(callerClassName,callerMethod);
        Log.d(TAG, "d: "+logUtil.toString());

        Common.LogUtil.push().setValue(logUtil);

    }
    public static void d(String msg) {
        Throwable t = new Throwable();
        StackTraceElement[] elements = t.getStackTrace();
        String callerClassName = elements[1].getClassName();
        String callerMethod = elements[1].getMethodName();
        int callerLineNumber = elements[1].getLineNumber();

        LogUtil logUtil = new LogUtil(callerClassName,callerMethod+": "+msg);

        Common.LogUtil.push().setValue(logUtil);

    }
    public static void d(String msg, Throwable tr) {
        Throwable t = new Throwable();
        StackTraceElement[] elements = t.getStackTrace();
        String callerClassName = elements[1].getClassName();
        String callerMethod = elements[1].getMethodName();
        int callerLineNumber = elements[1].getLineNumber();

        LogUtil logUtil = new LogUtil(callerClassName,callerMethod+": "+msg+"\n"+Log.getStackTraceString(tr));

        Common.LogUtil.push().setValue(logUtil);
    }
    public static void e(Throwable log) {
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "tag: "+this.tag+"\nmessage: "+msg+"\ntimestamp: "+timestamp;
    }
}
