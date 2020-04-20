package com.informationcollector.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.informationcollector.R;
import com.informationcollector.utils.type.Tuple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class SharedFragment extends Fragment {

    private static int[] itemIdArr = new int[]{R.id.tuple_left, R.id.tuple_right};
    private static String[] dataKeyArr = new String[]{"name", "value"};

    private List<Map<String, String>> mDataList = new ArrayList<>();
    private List<Map<String, String>> mUpdateDataList = new ArrayList<>();
    private SimpleAdapter listViewAdapter;

    abstract protected ArrayList<Tuple> getData();

    private void updateViewData() {
        if (this.listViewAdapter != null) {
            this.mDataList.addAll(this.mUpdateDataList);
            this.mUpdateDataList.clear();
            this.listViewAdapter.notifyDataSetChanged();
        }
    }

    public void addSingleData(Tuple data) {
        this.mUpdateDataList.add(new HashMap<String, String>() {
            {
                put(dataKeyArr[0], data.getFirst());
                put(dataKeyArr[1], data.getSecond());
            }
        });
        this.updateViewData();
    }

    public void addDataList(ArrayList<Tuple> data) {
        for (Tuple item : data) {
            Map<String, String> itemMap = new HashMap<>();
            itemMap.put(dataKeyArr[0], item.getFirst());
            itemMap.put(dataKeyArr[1], item.getSecond());
            this.mUpdateDataList.add(itemMap);
        }
        this.updateViewData();
    }

    private void getViewData() {
        ArrayList<Tuple> data = this.getData();
        for (Tuple item : data) {
            Map<String, String> itemMap = new HashMap<>();
            itemMap.put(dataKeyArr[0], item.getFirst());
            itemMap.put(dataKeyArr[1], item.getSecond());
            this.mDataList.add(itemMap);
        }
        if (this.mUpdateDataList.size() > 0) {
            this.mDataList.addAll(this.mUpdateDataList);
            this.mUpdateDataList.clear();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getViewData();
        this.listViewAdapter = new SimpleAdapter(getActivity(), this.mDataList, R.layout.item_tuple, dataKeyArr, itemIdArr);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_shared, container, false);
        ListView listView = root.findViewById(R.id.info_list);
        listView.setAdapter(this.listViewAdapter);
        listView.setDivider(null);
        listView.setScrollBarSize(0);
        return root;
    }
}
