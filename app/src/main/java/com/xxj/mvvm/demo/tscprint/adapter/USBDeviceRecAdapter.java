package com.xxj.mvvm.demo.tscprint.adapter;

import android.hardware.usb.UsbDevice;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xxj.mvvm.demo.tscprint.R;
import com.xxj.mvvm.demo.tscprint.utils.usb.USBUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 52492 on 05/24.
 */

public class USBDeviceRecAdapter extends RecyclerView.Adapter<USBDeviceRecAdapter.MyHolder> {
    private List<UsbDevice> data;
    private OnItemClick onItemClick;

    public USBDeviceRecAdapter(OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
        data = new ArrayList<>();
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_usb_device, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        final UsbDevice usbDevice = data.get(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick.onItemClick(usbDevice);
            }
        });
        holder.deviceName.setText("设备名：" + usbDevice.getDeviceName() +
                "\t\t制造商：" + usbDevice.getManufacturerName() +
                "\t\t产品名：" + usbDevice.getProductName() +
                "\n序列号：" + usbDevice.getSerialNumber() +
                "\t\t厂家ID（V_ID）:" + usbDevice.getVendorId() +
                "\t\t产品ID（P_ID）：" + usbDevice.getProductId() +
                "\t\t是否拥有权限：" + USBUtil.getInstance().hasPermission(usbDevice)
        );
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setData(List<UsbDevice> data) {
        if (data != null) {
            this.data = data;
        } else {
            this.data.clear();
        }
        notifyDataSetChanged();
    }

    class MyHolder extends RecyclerView.ViewHolder {
        TextView deviceName;

        public MyHolder(View itemView) {
            super(itemView);
            deviceName = itemView.findViewById(R.id.device_name);
        }
    }

    public interface OnItemClick{
        void onItemClick(UsbDevice device);
    }
}
