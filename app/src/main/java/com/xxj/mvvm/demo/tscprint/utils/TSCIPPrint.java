package com.xxj.mvvm.demo.tscprint.utils;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 通过 IP TSC 打印
 * Created by xxj on 05/22.
 */

public class TSCIPPrint {

    private ExecutorService executor = Executors.newSingleThreadExecutor();

    private Socket socket;
    private int netPort = 9100;   //default 9100  0X238c
    private String charSet = "GB18030";     //default GB18030
    //    private String charSet = "GB2312";
    private TSCUtils tscUtils = new TSCUtils();
    private OutputStream outputStream;
    private OutputStreamWriter writer;
    private InputStream inputStream;
    private boolean connected;

    /**
     * 设置端口号，有默认值，除非自定义，否则不调用
     *
     * @param netPort 端口号
     */
    public TSCIPPrint netPort(int netPort) {
        this.netPort = netPort;
        Log.e("TSCIPPrint", "netPort  " + netPort);
        return this;
    }

    /**
     * 打开 Socket 连接
     *
     * @param ipAddress IP 地址
     */
    public boolean open_thread(String ipAddress) {
        return open_thread(ipAddress, netPort, 2000);
    }

    /**
     * 打开 Socket 连接
     *
     * @param ipAddress ipAddress
     * @param port      port
     * @param timeout   timeout
     */
    public boolean open_thread(final String ipAddress, final int port, final int timeout) {
        executor.execute(new Runnable() {
            public void run() {
//                StrictMode.setThreadPolicy((new StrictMode.ThreadPolicy.Builder()).detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
//                StrictMode.setVmPolicy((new android.os.StrictMode.VmPolicy.Builder()).detectLeakedSqlLiteObjects().detectLeakedClosableObjects().penaltyLog().penaltyDeath().build());
                if (socket != null) {
                    close();
                }
                try {
                    socket = new Socket();
                    socket.connect(new InetSocketAddress(ipAddress, port), timeout);
                    inputStream = socket.getInputStream();
                    outputStream = socket.getOutputStream();
                    connected = true;
                    outputStream.write(tscUtils.setup(40, 30, 4, 4, 0, 0, 0));
                    String s = "FEED 40\nSELFTEST";
                    outputStream.write(s.getBytes());
                    outputStream.write(tscUtils.printText(10,10,"TSS24.BF2",0,1,1,"好好"));
                    outputStream.write(tscUtils.printText(10,10,"1",0,1,1,"asdasd"));
                    outputStream.flush();
                } catch (Exception var2) {
                    connected = false;
                    Log.e("TSCIPPrint", "run  " + var2.toString());
                }
            }
        });
        Log.e("TSCIPPrint", "open_thread  " + connected);
        return connected;
    }

    /**
     * 发送打印指令
     *
     * @param command command
     */
    public void sendCommand_thread(final String command) {
        sendCommand_thread(command.getBytes());
    }

    /**
     * 发送打印指令
     *
     * @param command command
     */
    public void sendCommand_thread(final byte[] command) {
        if (connected) {
            executor.execute(new Runnable() {
                public void run() {
                    try {
                        outputStream.write(command);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    /**
     * 打印机初始化
     *
     * @param sensor          0
     * @param sensor_distance 0
     * @param sensor_offset   0
     */
    public void setup_thread(final int width, final int height, final int speed, final int density, final int sensor, final int sensor_distance, final int sensor_offset) {
        if (connected) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        outputStream.write(tscUtils.setup(width, height, speed, density, sensor, sensor_distance, sensor_offset));
                    } catch (IOException e) {
                        Log.e("TSCIPPrint", "setup_thread  " + e.toString());
                    }
                }
            });
        }
    }

    /**
     * 打印文字
     *
     * @param x                文字 X 方向启始点坐标
     * @param y                文字 Y 方向启始点坐标
     * @param font             字体名称
     *                         1 8 x 12 dot 英数字体
     *                         2 12 x 20 dot英数字体
     *                         3 16 x 24 dot英数字体
     *                         4 24 x 32 dot英数字体
     *                         5 32 x 48 dot英数字体
     *                         6 14 x 19 dot英数字体 OCR-B
     *                         7 21 x 27 dot 英数字体OCR-B
     *                         8 14 x25 dot英数字体OCR-A
     *                         TST24.BF2 繁体中文 24 x 24 font (大五码)
     *                         TSS24.BF2 简体中文 24 x 24 font (GB 码)
     *                         K 韩文 24 x 24 font (KS 码)
     *                         注: 五号字英文字母仅可打印大写字母
     *                         若要打印双引号时(“)在程序中请使用\[“]来打印双引号
     *                         若要打印 0D(hex)字符时，请在程序中使用\[R]来打印 CR
     *                         若要打印 0A(hex)字符时，请在程序中使用\[A]来打印 LF
     * @param rotation         文字旋转角度(顺时钟方向)
     *                         0 0 度
     *                         90 90 度
     *                         180 180
     *                         270 270
     * @param x_multiplication X 方向放大倍率 1~10
     * @param y_multiplication Y 方向放大倍率 1~10
     * @param text             你要打印的文字
     * @return 范例 TEXT 100,100,”4”,0,1,1,”DEMO FOR TEXT
     */
    public void printerfont_thread(final int x, final int y, final String font, final int rotation, final int x_multiplication, final int y_multiplication, final String text) {
        if (connected) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        outputStream.write(tscUtils.printText(x, y, font, rotation, x_multiplication, y_multiplication, text));
                    } catch (IOException e) {
                        Log.e("TSCIPPrint", "printerfont_thread  "+e.toString());
                    }
                }
            });
        }
    }

    /**
     * 打印机关闭
     */
    public void close() {
        try {
            if (socket != null)
                socket.close();
            if (outputStream != null)
                outputStream.close();
            if (writer != null)
                writer.close();
            if (inputStream != null)
                inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        socket = null;
        connected = false;
        Log.e("TSCIPPrint", "close  ");
    }

}
