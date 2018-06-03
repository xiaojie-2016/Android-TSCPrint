package com.xxj.mvvm.demo.tscprint.utils;

import java.io.UnsupportedEncodingException;

/**
 * TSC 指令工具类
 * Created by xxj on 05/22.
 */

public class TSCUtils {
    /**
     * 设置打印纸的宽高
     *
     * @param width  width
     * @param height height
     */
    public byte[] setSize(int width, int height) {
        String size = "SIZE " + width + " mm" + ", " + height + " mm";
        return size.getBytes();
    }

    /**
     * 设置打印速度
     *
     * @param speed speed
     */
    public byte[] setSpeed(int speed) {
        String speed_value = "SPEED " + speed;
        return speed_value.getBytes();
    }

    /**
     * 打印机初始化
     *
     * @param width           纸宽度
     * @param height          纸高度
     * @param speed           打印速度
     * @param density         打印浓度， 0 使用最淡的打印浓度， 15 使用最深的打印浓度
     * @param sensor          1. GAP
     *                        该指令定义两张卷标纸间的垂直间距距离
     *                        m 两标签纸中间的垂直距离
     *                        0≤m≤1（inch），0≤m≤25.4（mm）
     *                        n 垂直间距的偏移
     *                        n ≤ 标签纸张长度 (inch或mm)
     *                        2.不知道。。。
     * @param sensor_distance 传感器距离
     * @param sensor_offset   传感器偏移
     */
    public byte[] setup(int width, int height, int speed, int density, int sensor, int sensor_distance, int sensor_offset) {
        String message;
        String size = "SIZE " + width + " mm" + ", " + height + " mm";
        String speed_value = "SPEED " + speed;
        String density_value = "DENSITY " + density;
        String sensor_value = "";
        if (sensor == 0) {
            sensor_value = "GAP " + sensor_distance + " mm" + ", " + sensor_offset + " mm";
        } else if (sensor == 1) {
            sensor_value = "BLINE " + sensor_distance + " mm" + ", " + sensor_offset + " mm";
        }
        message = size + "\n" + speed_value + "\n" + density_value + "\n" + sensor_value + "\n";
        return message.getBytes();
    }

    /**
     * 清除影像缓冲区(image buffer)的数据
     *
     * @return CLS
     */
    public byte[] clearBuffer() {
        String message = "CLS\r\n";
        return message.getBytes();
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
    public byte[] printText(int x, int y, String font, int rotation, int x_multiplication, int y_multiplication, String text) throws UnsupportedEncodingException {
        String message;
        String s = "TEXT ";
        String position = x + "," + y;
        String size_value = "\"" + font + "\"";
        String rota = "" + rotation;
        String x_value = "" + x_multiplication;
        String y_value = "" + y_multiplication;
        String string_value = "\"" + text + "\"";
        message = s + position + " ," + size_value + " ," + rota + " ," + x_value + " ," + y_value + " ," + string_value + "\r\n";
        return message.getBytes("GBK");
    }

}
