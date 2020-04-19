package com.informationcollector.utils;

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
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;

public final class NetworkInfo extends BaseInfo {
    public NetworkInfo(Context context) {
        super(context);
    }

    @Override
    public void output() {
        Dexter.withContext(this.context).withPermission(Manifest.permission.ACCESS_NETWORK_STATE).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse response) {
                NetworkInfo.this.getSIMInformation();
                NetworkInfo.this.getGeneralNetworkInformation();
                NetworkInfo.this.getBluetoothInformation();
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse response) {
                if (response.isPermanentlyDenied()) {
                    Toast.makeText(NetworkInfo.this.context, "需开启请求网络状态权限", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest request, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).check();
    }

    private void getSIMInformation() {
        if (ActivityCompat.checkSelfPermission(NetworkInfo.this.context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        final SubscriptionManager sm = (SubscriptionManager) NetworkInfo.this.context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
        if (sm != null) {
            final List<SubscriptionInfo> list = sm.getActiveSubscriptionInfoList();
            Log.i("已激活SIM卡数量", String.valueOf(sm.getActiveSubscriptionInfoCount()));
            Log.i("最大可激活SIM卡数量", String.valueOf(sm.getActiveSubscriptionInfoCountMax()));
            for (SubscriptionInfo info : list) {
                int idx = info.getSimSlotIndex() + 1;
                Log.i("SIM卡" + idx + "名称", info.getDisplayName().toString());
                Log.i("SIM卡" + idx + "网络运营商", info.getCarrierName().toString());
                Log.i("SIM卡" + idx + "网络运营商国家代码", info.getCountryIso());
                Log.i("SIM卡" + idx + "电话号码", info.getNumber());
                Log.i("SIM卡" + idx + "是否允许漫游", String.valueOf(info.getDataRoaming() == SubscriptionManager.DATA_ROAMING_ENABLE));
                Log.i("SIM卡" + idx + "是否正在漫游", String.valueOf(sm.isNetworkRoaming(info.getSubscriptionId())));
            }
        }
    }

    private void getGeneralNetworkInformation() {
        ConnectivityManager cm = (ConnectivityManager) this.context.getSystemService(Service.CONNECTIVITY_SERVICE);
        if (cm != null) {
            Network network = cm.getActiveNetwork();
            Log.i("网络是否可用", network != null ? "是" : "否");
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
                    Log.i("网络类型", netStr);
                }
                LinkProperties linkProp = cm.getLinkProperties(network);
                if (linkProp != null) {
                    List<LinkAddress> list = linkProp.getLinkAddresses();
                    for (LinkAddress link : list) {
                        Log.i("IP地址", link.toString());
                    }
                    String interStr = linkProp.getInterfaceName();
                    if (interStr != null) {
                        Log.i("网卡名称", interStr);
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
                                        Log.i("MAC地址", buf.toString());
                                        break;
                                    }
                                }
                            }
                        } catch (SocketException e) {
                            e.printStackTrace();
                        }
                    }
                }
                WifiManager wm = (WifiManager) this.context.getSystemService(Service.WIFI_SERVICE);
                if (wm != null) {
                    WifiInfo wi = wm.getConnectionInfo();
                    Log.i("Wi-Fi网络速度", wi.getLinkSpeed() + "Mbps");
                    Log.i("Wi-Fi信号强度", wi.getRssi() + "dBm");
                    Log.i("Wi-Fi连接状态", wi.getSupplicantState().name());
                }
            }
        }
    }

    private void getBluetoothInformation() {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter != null) {
            Log.i("蓝牙是否打开", String.valueOf(adapter.isEnabled()));
            Log.i("本机蓝牙名称", adapter.getName());
            Log.i("蓝牙是否正在扫描", String.valueOf(adapter.isDiscovering()));
        } else {
            Log.i("蓝牙", "不支持");
        }
    }
}
