/**
 * Copyright (c) 1993-2023 AutoNavi, Inc. All rights reserved. This software is
 * the confidential and proprietary information of AutoNavi, Inc.
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the license
 * agreement you entered into with AutoNavi.
 */
package com.allenliu.versionchecklib;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 * 日志相关类:默认是测试环境<br>
 * <b>支持：存储Log日志文件到本地。发送Log日志信息到服务器</b>
 *
 * @author yuchao.wang
 * @since 2014-4-23
 */
public class LogUtil {
    private static boolean isShowLog=true;

    /* ========================下面的是本地存储相关的========================== */
    /**
     * 写日志对象
     */
    private LogWriter logWriter;

    /**
     * 写入本地日志线程
     */
    private class LogWriter extends Thread {
        /**
         * 文件路径
         */
        private String mFilePath;
        /**
         * 调用这个类的线程
         */
        private int mPid;
        /**
         * 线程运行标志
         */
        private boolean isRunning = true;

        /**
         * @param filePath 文件路径
         * @param pid
         */
        public LogWriter(String filePath, int pid) {
            this.mPid = pid;
            this.mFilePath = filePath;
        }

        @Override
        public void run() {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.CHINA);//日期格式化对象
            Process process = null;//进程
            BufferedReader reader = null;
            FileWriter writer = null;
            try {
                //执行命令行
                String cmd = "logcat *:e *:w | grep";
                process = Runtime.getRuntime().exec(cmd);
                //得到输入流
                reader = new BufferedReader(new InputStreamReader(process.getInputStream()), 1024);
                //创建文件
                File file = new File(mFilePath);
                if (!file.exists()) {
                    file.getParentFile().mkdirs();
                    file.createNewFile();
                }
                writer = new FileWriter(file, true);
                //循环写入文件
                String line = null;
                while (isRunning) {
                    line = reader.readLine();
                    if (line != null && line.length() > 0) {
                        writer.append("PID:" + this.mPid + "\t"
                                + sdf.format(new Date(System.currentTimeMillis())) + "\t" + line
                                + "\n");
                        writer.flush();
                    } else {
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (process != null) {
                    process.destroy();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (writer != null) {
                    try {
                        writer.flush();
                        writer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                process = null;
                reader = null;
                writer = null;
            }
        }

        public void end() {
            isRunning = false;
        }
    }

    /**
     * 整个应用只需要调用一次即可:开始本地记录
     *
     * @param filePath 要写入的目的文件路径
     * @param iswrite    是否需要写入sdk
     */
    public void startWriteLogToSdcard(String filePath, boolean iswrite) {

        if (iswrite) {
            if (logWriter == null) {
                try {
                    /** LogUtil这个类的pid,必须在类外面得到 */
                    logWriter = new LogWriter(filePath, android.os.Process.myPid());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            logWriter.start();
        }
    }

    /**
     * 整个应用只需要调用一次即可:结束本地记录
     */
    public void endWriteLogToSdcard() {
        if (logWriter != null) {
            logWriter.end();
        }
    }

    /* ========================下面的是需要上传的数据========================== */
    private LogUploader logUploader;

    /**
     * 日志上传线程
     */
    private class LogUploader extends Thread {
        /**
         * 当前线程是否正在运行
         */
        private boolean isRunning = true;
        /**
         * 上传所需要的url
         */
        private String mStrUrl;
        /**
         * 上传所需要的其他参数
         */
        private HashMap<String, String> mAllParams;
        /**
         * 上传所需要pid
         */
        private int mPid;

        /**
         * 构造方法
         *
         * @param strUrl    上传所需要的url
         * @param allParams 需要上传的额外的参数【除了日志以外】
         * @param pid       日志所在的pid
         */
        public LogUploader(String strUrl, HashMap<String, String> allParams, int pid) {
            this.mStrUrl = strUrl;
            this.mAllParams = allParams;
            this.mPid = pid;
        }

        @Override
        public void run() {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.CHINA);//日期格式化对象
            Process process = null;//进程
            BufferedReader reader = null;
            try {
                //执行命令行,得到输入流
                String cmd = "logcat *:e *:w | grep";
                process = Runtime.getRuntime().exec(cmd);
                reader = new BufferedReader(new InputStreamReader(process.getInputStream()), 1024);
                String line = null;
                while (isRunning) {
                    line = reader.readLine();
                    if (line != null && line.length() > 0) {
                        String log = "PID:" + this.mPid + "\t"
                                + sdf.format(new Date(System.currentTimeMillis())) + "\t" + line;
                        mAllParams.put("log", log);
                    } else {
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (process != null) {
                    process.destroy();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                process = null;
                reader = null;
            }
        }

        public void end() {
            isRunning = false;
        }
    }

    /**
     * 整个应用调用一次即可：上传日志数据
     *
     * @param strUrl    上传所需要的url
     * @param allParams 需要上传的额外的参数【除了日志以外】
     * @param isUploadLog    是否需要上传
     */
    public void startUploadLog(String strUrl, HashMap<String, String>
            allParams, boolean isUploadLog) {

        if (isUploadLog) {
            if (logUploader == null) {
                logUploader = new LogUploader(strUrl, allParams, android.os.Process.myPid());
            }
            logUploader.start();
        }
    }

    /**
     * 整个应用调用一次即可：结束上传日志数据
     */
    public void endUploadLog() {
        if (logUploader != null) {
            logUploader.end();
        }
    }

    /* ========================下面的是直接使用的========================== */

    /**
     * verbose详细日志
     *
     * @param tag     日志标记
     * @param message 日志信息
     */
    public static void v(String tag, String message) {

        if (isShowLog) {
            Log.v(tag, getDetailMessage(message));
        }
    }

    /**
     * verbose详细日志
     *
     * @param message 日志信息
     */
    public static void v(String message) {
        if (isShowLog) {
            String[] output = getTagAndDetailMessage(message);
            Log.v(output[0], output[1]);
        }
    }

    /**
     * error错误日志
     *
     * @param tag     日志标记
     * @param message 日志信息
     */
    public static void e(String tag, String message) {

        if (isShowLog) {
            Log.e(tag, getDetailMessage(message));
        }
    }

    /**
     * error错误日志
     *
     * @param message 日志信息
     */
    public static void e(String message) {

        if (isShowLog) {
            String[] output = getTagAndDetailMessage(message);
            Log.e(output[0], output[1]);
        }
    }

    /**
     * info信息日志
     *
     * @param tag     日志标记
     * @param message 日志信息
     */
    public static void i(String tag, String message) {

        if (isShowLog) {
            Log.i(tag, getDetailMessage(message));
        }
    }

    /**
     * info信息日志
     *
     * @param message 日志信息
     */
    public static void i(String message) {

        if (isShowLog) {
            String[] output = getTagAndDetailMessage(message);
            Log.i(output[0], output[1]);
        }
    }

    /**
     * debug调试日志
     *
     * @param tag     日志标记
     * @param message 日志信息
     */
    public static void d(String tag, String message) {

        if (isShowLog) {
            Log.d(tag, getDetailMessage(message));
        }
    }

    /**
     * debug调试日志
     *
     * @param message 日志信息
     */
    public static void d(String message) {

        if (isShowLog) {
            String[] output = getTagAndDetailMessage(message);
            Log.d(output[0], output[1]);
        }
    }

    /**
     * warn警告日志
     *
     * @param tag     日志标记
     * @param message 日志信息
     */
    public static void w(String tag, String message) {

        if (isShowLog) {
            Log.w(tag, getDetailMessage(message));
        }
    }

    /**
     * warn警告日志
     *
     * @param message 日志信息
     */
    public static void w(String message) {

        if (isShowLog) {
            String[] output = getTagAndDetailMessage(message);
            Log.w(output[0], output[1]);
        }
    }

    /**
     * 得到默认tag【类名】以及信息详情
     *
     * @param message 要显示的信息
     * @return 默认tag【类名】以及信息详情,默认信息详情【类名+方法名+行号+message】
     */
    private static String[] getTagAndDetailMessage(String message) {
        String output[] = new String[2];
        for (StackTraceElement ste : (new Throwable()).getStackTrace()) {
            //栈顶肯定是LogUtil这个类自己
            if (LogUtil.class.getName().equals(ste.getClassName())) {
                continue;
            }
            //栈顶的下一个就是需要调用这个类的地方
            else {
                int b = ste.getClassName().lastIndexOf(".") + 1;
                output[0] = ste.getClassName().substring(b);
                output[1] = output[0] + "->" + ste.getMethodName() + "():" + ste.getLineNumber()
                        + "->" + message;
                break;
            }
        }
        return output;
    }

    /**
     * 得到一个信息的详细的情况【类名+方法名+行号】
     *
     * @param message 要显示的信息
     * @return 一个信息的详细的情况【类名+方法名+行号+message】
     */
    private static String getDetailMessage(String message) {
        String detailMessage = "";
        for (StackTraceElement ste : (new Throwable()).getStackTrace()) {
            //栈顶肯定是LogUtil这个类自己
            if (LogUtil.class.getName().equals(ste.getClassName())) {
                continue;
            }
            //栈顶的下一个就是需要调用这个类的地方[此处取出类名和方法名还有行号]
            else {
                int b = ste.getClassName().lastIndexOf(".") + 1;
                String TAG = ste.getClassName().substring(b);
                detailMessage = TAG + "->" + ste.getMethodName() + "():" + ste.getLineNumber()
                        + "->" + message;
                break;
            }
        }
        return detailMessage;
    }
}
