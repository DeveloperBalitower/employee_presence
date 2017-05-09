package bts.co.id.employeepresences.Manager;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import bts.co.id.employeepresences.Model.StaticData;

/**
 * Created by IT on 7/14/2016.
 */
public class Log {
    public static final String HANDLER_DATA = "logger_record";
    private static final int MAX_MESSAGE_LENGTH = 4000;
    private static final boolean isDebug;
    private static final String tag;
    private static Handler messageHandler = null;
    private static int[] handlerMessageLevels = null;

    static {
        isDebug = StaticData.isDebuggable;
        tag = StaticData.PROCESS_NAME;

        Log.log(android.util.Log.VERBOSE, "Apache logging is enabled");

        java.util.logging.Logger.getLogger("org.apache.http.wire")
                .setLevel(java.util.logging.Level.FINEST);
        java.util.logging.Logger.getLogger("org.apache.http.headers")
                .setLevel(java.util.logging.Level.FINEST);

        System.setProperty("org.apache.commons.logging.Log",
                "org.apache.commons.logging.impl.SimpleLog");
        System.setProperty("org.apache.commons.logging.simplelog.showdatetime",
                "true");
        System.setProperty("org.apache.commons.logging.simplelog.log.httpclient.wire",
                "debug");
        System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http",
                "debug");
        System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.headers",
                "debug");
    }

    public static void v(int resId) {
        if (isDebug) {
            v(StaticData.applicationContext.getString(resId));
        }
    }

    public static void d(int resId) {
        if (isDebug) {
            d(StaticData.applicationContext.getString(resId));
        }
    }

    public static void i(int resId) {
        i(StaticData.applicationContext.getString(resId));
    }

    public static void w(int resId) {
        w(StaticData.applicationContext.getString(resId));
    }

    public static void e(int resId) {
        e(StaticData.applicationContext.getString(resId));
    }

    public static void wtf(int resId) {
        wtf(StaticData.applicationContext.getString(resId));
    }

    public static void v(String msg, Object... args) {
        if (isDebug) {
            log(android.util.Log.VERBOSE, msg, args);
        }
    }

    public static void d(String msg, Object... args) {
        if (isDebug) {
            log(android.util.Log.DEBUG, msg, args);
        }
    }

    public static void i(String msg, Object... args) {
        log(android.util.Log.INFO, msg, args);
    }

    public static void w(String msg, Object... args) {
        log(android.util.Log.WARN, msg, args);
    }

    public static void w(Throwable msg) {
        log(android.util.Log.WARN, msg);
    }

    public static void e(String msg, Object... args) {
        log(android.util.Log.ERROR, msg, args);
    }

    public static void e(Throwable msg) {
        log(android.util.Log.ERROR, msg);
    }

    public static void wtf(String msg, Object... args) {
        log(android.util.Log.ASSERT, msg, args);
    }

    public static void wtf(Throwable msg) {
        log(android.util.Log.ASSERT, msg);
    }

    private static void log(int level, Object msg, Object... args) {
        if (msg == null) {
            log(android.util.Log.ERROR, "Message can not be null");
            return;
        }

        if (messageHandler != null) {
            boolean needToHandleMsg = false;

            if (handlerMessageLevels != null) {
                for (int handlerLevel : handlerMessageLevels) {
                    if (handlerLevel == level) {
                        needToHandleMsg = true;
                        break;
                    }
                }
            }

            if (needToHandleMsg) {
                Message handlerMessage = new Message();
                Bundle bundle = new Bundle();
                bundle.putString(HANDLER_DATA, msg.toString());
                handlerMessage.setData(bundle);
                messageHandler.sendMessage(handlerMessage);
            }
        }

        if (msg instanceof Throwable) {
            Throwable throwable = (Throwable) msg;
            writeTextFile("Log Throwable : " + System.getProperty("line.separator") + level + System.getProperty("line.separator") + " tag :" + tag + System.getProperty("line.separator") + " location : " + getLocation() + System.getProperty("line.separator") + " message " + android.util.Log.getStackTraceString(throwable));
            android.util.Log.println(level, tag, getLocation() + android.util.Log.getStackTraceString(throwable));
        } else {
            String message = (String) msg;
            if (args.length > 0) {
                message = String.format(message, args);
            }

            if (message.length() > MAX_MESSAGE_LENGTH) {
                writeTextFile("Log : " + System.getProperty("line.separator") + level + System.getProperty("line.separator") + " tag :" + tag + System.getProperty("line.separator") + " location : " + getLocation() + System.getProperty("line.separator") + " message " + message);
                android.util.Log.println(level, tag, getLocation() + message.substring(0, MAX_MESSAGE_LENGTH));
                log(level, message.substring(MAX_MESSAGE_LENGTH));
            } else {
                writeTextFile("Log : " + System.getProperty("line.separator") + level + System.getProperty("line.separator") + " tag :" + tag + System.getProperty("line.separator") + " location : " + getLocation() + System.getProperty("line.separator") + " message " + message);
                android.util.Log.println(level, tag, getLocation() + message);
            }
        }
    }

    private static String getLocation() {
        final String className = Log.class.getName();
        final StackTraceElement[] traces = Thread.currentThread()
                .getStackTrace();
        boolean found = false;

        for (StackTraceElement trace : traces) {
            try {
                if (found) {
                    if (!trace.getClassName().startsWith(className)) {
                        Class<?> clazz = Class.forName(trace.getClassName());
                        return "[" + getClassName(clazz) + "."
                                + trace.getMethodName() + "() : "
                                + trace.getLineNumber() + "]: ";
                    }
                } else if (trace.getClassName().startsWith(className)) {
                    found = true;
                }
            } catch (ClassNotFoundException e) {
                // no need, it`s not fatal
            }
        }

        return "[]: ";
    }

    private static String getClassName(Class<?> clazz) {
        if (clazz != null) {
            if (!TextUtils.isEmpty(clazz.getSimpleName())) {
                return clazz.getSimpleName();
            }

            return getClassName(clazz.getEnclosingClass());
        }

        return "";
    }

    public static void attachMessageHandler(Handler handler, int... levels) {
        messageHandler = handler;
        handlerMessageLevels = levels;
    }

    public static void detachHandler() {
        messageHandler = null;
        handlerMessageLevels = null;
    }

    private static void writeTextFile(String message) {
        if (StaticData.applicationContext != null) {
            GlobalManager globalManager = new GlobalManager(StaticData.applicationContext);
            if (globalManager != null) {
                globalManager.writeTextFile(message);
            }
        }
    }
}
