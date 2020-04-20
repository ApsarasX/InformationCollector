package com.informationcollector.utils.info;

import android.Manifest;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.LinkAddress;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;

import androidx.core.app.ActivityCompat;

import com.informationcollector.utils.type.Tuple;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public final class NetworkInfo {
    public static ArrayList<Tuple> getSIMInformation(Context context) {
        ArrayList<Tuple> result = new ArrayList<>();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return result;
        }
        final SubscriptionManager sm = (SubscriptionManager) context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
        if (sm != null) {
            final List<SubscriptionInfo> list = sm.getActiveSubscriptionInfoList();
            result.add(new Tuple("已激活SIM卡数量", String.valueOf(sm.getActiveSubscriptionInfoCount())));
            for (SubscriptionInfo info : list) {
                int idx = info.getSimSlotIndex() + 1;
                result.add(new Tuple("SIM卡" + idx + "名称", info.getDisplayName().toString()));
                result.add(new Tuple("SIM卡" + idx + "网络运营商", info.getCarrierName().toString()));
                result.add(new Tuple("SIM卡" + idx + "网络运营商国家代码", info.getCountryIso()));
                result.add(new Tuple("SIM卡" + idx + "电话号码", info.getNumber()));
                result.add(new Tuple("SIM卡" + idx + "是否允许漫游", String.valueOf(info.getDataRoaming() == SubscriptionManager.DATA_ROAMING_ENABLE)));
                result.add(new Tuple("SIM卡" + idx + "是否正在漫游", String.valueOf(sm.isNetworkRoaming(info.getSubscriptionId()))));
            }
        }
        return result;
    }

    public static ArrayList<Tuple> getGeneralNetworkInformation(Context context) {
        ArrayList<Tuple> result = new ArrayList<>();
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Service.CONNECTIVITY_SERVICE);
        if (cm != null) {
            Network network = cm.getActiveNetwork();
            result.add(new Tuple("网络是否可用", network != null ? "是" : "否"));
            if (network != null) {
                NetworkCapabilities netCap = cm.getNetworkCapabilities(network);
                if (netCap != null) {
                    String netStr = "未知";
                    if (netCap.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH)) {
                        netStr = "蓝牙";
                    } else if (netCap.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                        netStr = "移动数据";
                    } else if (netCap.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                        netStr = "以太网";
                    } else if (netCap.hasTransport(NetworkCapabilities.TRANSPORT_VPN)) {
                        netStr = "VPN";
                    } else if (netCap.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        netStr = "Wi-Fi";
                    } else if (netCap.hasTransport(NetworkCapabilities.TRANSPORT_WIFI_AWARE)) {
                        netStr = "Wi-Fi Aware";
                    }
                    result.add(new Tuple("网络类型", netStr));
                }
                LinkProperties linkProp = cm.getLinkProperties(network);
                if (linkProp != null) {
                    List<LinkAddress> list = linkProp.getLinkAddresses();
                    StringBuilder ipStrBuf = new StringBuilder();
                    for (LinkAddress link : list) {
                        ipStrBuf.append(link.toString()).append('\n');
                    }
                    ipStrBuf.deleteCharAt(ipStrBuf.length() - 1);
                    result.add(new Tuple("IP地址", ipStrBuf.toString()));
                    String interStr = linkProp.getInterfaceName();
                    if (interStr != null) {
                        result.add(new Tuple("网卡名称", interStr));
                        try {
                            Enumeration<NetworkInterface> enumeration = NetworkInterface.getNetworkInterfaces();
                            while (enumeration.hasMoreElements()) {
                                NetworkInterface inter = enumeration.nextElement();
                                byte[] macAddrs = inter.getHardwareAddress();
                                if (macAddrs != null && macAddrs.length != 0) {
                                    StringBuilder buf = new StringBuilder();
                                    for (byte b : macAddrs) {
                                        buf.append(String.format("%02x:", b));
                                    }
                                    if (buf.length() > 0) {
                                        buf.deleteCharAt(buf.length() - 1);
                                    }
                                    if (inter.getName().equals(interStr)) {
                                        result.add(new Tuple("MAC地址", buf.toString()));
                                        break;
                                    }
                                }
                            }
                        } catch (SocketException e) {
                            e.printStackTrace();
                        }
                    }
                }
                WifiManager wm = (WifiManager) context.getSystemService(Service.WIFI_SERVICE);
                if (wm != null) {
                    WifiInfo wi = wm.getConnectionInfo();
                    result.add(new Tuple("Wi-Fi网络速度", wi.getLinkSpeed() + "Mbps"));
                    result.add(new Tuple("Wi-Fi信号强度", wi.getRssi() + "dBm"));
                    result.add(new Tuple("Wi-Fi连接状态", wi.getSupplicantState().name()));
                }
            }
        }
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter != null) {
            result.add(new Tuple("蓝牙是否打开", String.valueOf(adapter.isEnabled())));
            result.add(new Tuple("本机蓝牙名称", adapter.getName()));
            result.add(new Tuple("蓝牙是否正在扫描", String.valueOf(adapter.isDiscovering())));
        } else {
            result.add(new Tuple("蓝牙", "不支持"));
        }
        return result;
    }

//    public ArrayList<Tuple> getBluetoothInformation() {
//        ArrayList<Tuple> result = new ArrayList<>();
//        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
//        if (adapter != null) {
//            result.add(new Tuple("蓝牙是否打开", String.valueOf(adapter.isEnabled())));
//            result.add(new Tuple("本机蓝牙名称", adapter.getName()));
//            result.add(new Tuple("蓝牙是否正在扫描", String.valueOf(adapter.isDiscovering())));
//        } else {
//            result.add(new Tuple("蓝牙", "不支持"));
//        }
//        return result;
//    }
}
