package com.xxj.mvvm.demo.tscprint;

import android.hardware.usb.UsbDevice;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.xxj.mvvm.demo.tscprint.adapter.USBDeviceRecAdapter;
import com.xxj.mvvm.demo.tscprint.utils.usb.USBUtil;

import java.io.UnsupportedEncodingException;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private UsbDevice mDevice;
    private USBDeviceRecAdapter deviceRecAdapter = new USBDeviceRecAdapter(new USBDeviceRecAdapter.OnItemClick() {
        @Override
        public void onItemClick(UsbDevice device) {
            if (USBUtil.getInstance().hasPermission(device)) {
                mDevice = device;
            } else {
                USBUtil.getInstance().requestPermission(device);
            }
        }
    });
    private USBUtil usbUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((RecyclerView) findViewById(R.id.usb_devices)).setAdapter(deviceRecAdapter);
        findViewById(R.id.load_devices).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<UsbDevice> deviceList = USBUtil.getInstance().getDeviceList();
                deviceRecAdapter.setData(deviceList);
            }
        });
        findViewById(R.id.test_print).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usbUtil = USBUtil.getInstance();
                String s;
                if (usbUtil.openPort(mDevice)) {
                    usbUtil.sendMessage(setup(40, 30, 4, 4, 0, 2, 0));
                    s = "HOME\n";
//                    usbUtil.sendMessage(s.getBytes());
                    usbUtil.sendMessage(clearbuffer());
                    try {
                        s = "TEXT 100,200,\"TSS24.BF2\",0,1,1,\"你好打印机\n";
                        usbUtil.sendMessage(s.getBytes("GBK"));
                        s = "TEXT 100,100,\"TSS24.BF2\",0,1,1,\"你好打印机\n";
                        usbUtil.sendMessage(s.getBytes("GBK"));
                    } catch (UnsupportedEncodingException e) {
                        Log.e("MainActivity", "onClick  3" + "失败" + e.toString());
                        return;
                    }
                    usbUtil.sendMessage(barcode(10, 20, "128", 20, 1, 0, 3, 3, "123456789"));
                    usbUtil.sendMessage(printlabel(1,1));
                }
            }
        });
    }

    public byte[] setup(int width, int height, int speed, int density, int sensor, int sensor_distance, int sensor_offset) {
        String message = "";
        String size = "SIZE " + width + " mm" + ", " + height + " mm";
        String speed_value = "SPEED " + speed;
        String density_value = "DENSITY " + density;
        String sensor_value = "";
        if (sensor == 0) {
            sensor_value = "GAP " + sensor_distance + " mm" + ", " + sensor_offset + " mm";
        } else if (sensor == 1) {
            sensor_value = "BLINE " + sensor_distance + " mm" + ", " + sensor_offset + " mm";
        }

        message = size + "\r\n" + speed_value + "\r\n" + density_value + "\r\n" + sensor_value + "\r\n";
        return message.getBytes();
    }

    public byte[] clearbuffer() {
        String message = "CLS\r\n";
        return message.getBytes();
    }


    public byte[] barcode(int x, int y, String type, int height, int human_readable, int rotation, int narrow, int wide, String string) {
        String message = "";
        String barcode = "BARCODE ";
        String position = x + "," + y;
        String mode = "\"" + type + "\"";
        String height_value = "" + height;
        String human_value = "" + human_readable;
        String rota = "" + rotation;
        String narrow_value = "" + narrow;
        String wide_value = "" + wide;
        String string_value = "\"" + string + "\"";
        message = barcode + position + " ," + mode + " ," + height_value + " ," + human_value + " ," + rota + " ," + narrow_value + " ," + wide_value + " ," + string_value + "\r\n";
        return message.getBytes();
    }

    public byte[] printlabel(int quantity, int copy) {
        String message = "";
        message = "PRINT " + quantity + ", " + copy + "\r\n";
        return message.getBytes();
    }

    @Override
    protected void onResume() {
        super.onResume();
        USBUtil.getInstance().registerReceiver(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        USBUtil.getInstance().unRegisterReceiver(this);
    }
}
