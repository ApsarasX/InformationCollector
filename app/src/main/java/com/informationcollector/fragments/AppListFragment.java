package com.informationcollector.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.informationcollector.R;
import com.informationcollector.utils.info.AppInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AppListFragment extends Fragment {
    private static int[] itemIdArr = new int[]{
            R.id.app_package_name,
            R.id.app_version,
            R.id.app_name,
            R.id.app_target_sdk,
            R.id.app_min_sdk,
            R.id.app_last_update_time,
            R.id.app_first_install_time,
            R.id.app_system
    };
    private static String[] dataKeyArr = new String[]{
            "app_package_name",
            "app_version",
            "app_name",
            "app_target_sdk",
            "app_min_sdk",
            "app_last_update_time",
            "app_first_install_time",
            "app_system"
    };

    private List<Map<String, String>> mDataList = new ArrayList<>();
    private SimpleAdapter mListViewAdapter;
    private Handler mHandler = new Handler();

    private void getViewData() {
        new Thread(() -> {
            AppListFragment.this.mDataList.addAll(AppInfo.getAppListInfo(Objects.requireNonNull(getContext())));
            if (AppListFragment.this.mListViewAdapter != null) {
                mHandler.post(() -> AppListFragment.this.mListViewAdapter.notifyDataSetChanged());
            }
        }).start();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.getViewData();
        View root = inflater.inflate(R.layout.fragment_shared, container, false);
        ListView listView = root.findViewById(R.id.info_list);
        mListViewAdapter = new SimpleAdapter(getContext(), this.mDataList, R.layout.item_app, dataKeyArr, itemIdArr);
        listView.setAdapter(mListViewAdapter);
        listView.setScrollBarSize(0);
        return root;
    }
}
