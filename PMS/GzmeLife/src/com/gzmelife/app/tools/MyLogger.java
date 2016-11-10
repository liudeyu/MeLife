package com.gzmelife.app.tools;

import android.util.Log;

import java.util.Hashtable;

/**
 * The class for print log
 * @author kesenhoo
 *
 *
 * logger工具类，单例设计模式
 *
 * 定义MyLogger过程：
 * private MyLogger HHDLog = MyLogger.HHDLog();//全局定义
 * HHDLog.e("括号中间的内容");//测试代码处编写
 *
 * 实际显示效果：
 * [美易来]: @HHD@ [ main: MainActivity.java:88 onCreate ] - 括号中间的内容
 *
 * Log.e(TAG,"级别5，错误信息");
 * Log.w(TAG,"级别4，警告信息");
 * Log.i(TAG,"级别3，一般信息");
 * Log.d(TAG,"级别2，调试信息");
 * Log.v(TAG,"级别1，无用信息");
 *
 */
public class MyLogger {

    private final static boolean                logFlag         = true;

    public final static String                  tag             = "[美易来]";
    private final static int                    logLevel        = Log.VERBOSE;
    private static Hashtable<String, MyLogger>  sLoggerTable    = new Hashtable<String, MyLogger>();
    private String							    mClassName;

    private static MyLogger                     Llog;
    private static MyLogger                     Hlog;//声明一个本类对象

    private static final String                 LDY             = "@LDY@ ";
    private static final String                 HHD             = "@HHD@ ";

    /** 私有化构造方法 */
    private MyLogger(String name) {
        mClassName = name;
    }

    /**
     * @param className
     * @return
     */
    @SuppressWarnings("unused")
    private static MyLogger getLogger(String className) {
        MyLogger classLogger = (MyLogger) sLoggerTable.get(className);
        if (classLogger == null) {
            classLogger = new MyLogger(className);
            sLoggerTable.put(className, classLogger);
        }
        return classLogger;
    }

    /**
     * Purpose:Mark user one
     *
     * @return 给外部提供一个静态方法获取对象实例
     */
    public static MyLogger HHDLog() {
        if (Hlog == null) {
            Hlog = new MyLogger(HHD);
        }
        return Hlog;
    }

    /**
     * Purpose:Mark user two
     *
     * @return
     */
    public static MyLogger LDYLog() {
        if (Llog == null) {
            Llog = new MyLogger(LDY);
        }
        return Llog;
    }

    /**
     * Get The Current Function Name
     *
     * @return
     */
    private String getFunctionName() {
        StackTraceElement[] sts = Thread.currentThread().getStackTrace();
        if (sts == null) {
            return null;
        }
        for (StackTraceElement st : sts) {
            if (st.isNativeMethod()) {
                continue;
            }
            if (st.getClassName().equals(Thread.class.getName())) {
                continue;
            }
            if (st.getClassName().equals(this.getClass().getName())) {
                continue;
            }
            return mClassName + "[ 线程:" + Thread.currentThread().getName() + "  文件:" + st.getFileName() + "  行数:" + st.getLineNumber() + "  方法:" + st.getMethodName() + " ]";
        }
        return null;
    }

    /**
     * The Log Level:i
     *
     * @param str
     */
    public void i(Object str) {
        if (logFlag) {
            if (logLevel <= Log.INFO) {
                String name = getFunctionName();
                if (name != null) {
                    Log.i(tag, name + " - " + str);
                } else {
                    Log.i(tag, str.toString());
                }
            }
        }

    }

    /**
     * The Log Level:d
     *
     * @param str
     */
    public void d(Object str) {
        if (logFlag) {
            if (logLevel <= Log.DEBUG) {
                String name = getFunctionName();
                if (name != null) {
                    Log.d(tag, name + " - " + str);
                } else {
                    Log.d(tag, str.toString());
                }
            }
        }
    }

    /**
     * The Log Level:V
     *
     * @param str
     */
    public void v(Object str) {
        if (logFlag) {
            if (logLevel <= Log.VERBOSE) {
                String name = getFunctionName();
                if (name != null) {
                    Log.v(tag, name + " - " + str);
                } else {
                    Log.v(tag, str.toString());
                }
            }
        }
    }

    /**
     * The Log Level:w
     *
     * @param str
     */
    public void w(Object str) {
        if (logFlag) {
            if (logLevel <= Log.WARN) {
                String name = getFunctionName();
                if (name != null) {
                    Log.w(tag, name + " - " + str);
                } else {
                    Log.w(tag, str.toString());
                }
            }
        }
    }

    /**
     * The Log Level:e
     *
     * @param str
     */
    public void e(Object str) {
        if (logFlag) {
            if (logLevel <= Log.ERROR) {
                String name = getFunctionName();
                if (name != null) {
                    Log.e(tag, name + " - " + str);
                } else {
                    Log.e(tag, str.toString());
                }
            }
        }
    }

    /**
     * The Log Level:e
     *
     * @param ex
     */
    public void e(Exception ex) {
        if (logFlag) {
            if (logLevel <= Log.ERROR) {
                Log.e(tag, "error", ex);
            }
        }
    }

    /**
     * The Log Level:e
     *
     * @param log
     * @param tr
     */
    public void e(String log, Throwable tr) {
        if (logFlag) {
            String line = getFunctionName();
            Log.e(tag, "{Thread:" + Thread.currentThread().getName() + "}" + "[" + mClassName + line + ":] " + log + "\n", tr);
        }
    }
}