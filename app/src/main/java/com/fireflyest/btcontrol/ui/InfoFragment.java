package com.fireflyest.btcontrol.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.AutoTransition;
import androidx.transition.Transition;
import androidx.transition.TransitionManager;

import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fireflyest.btcontrol.R;
import com.fireflyest.btcontrol.adapter.RecordList.RecordItemAdapter;
import com.fireflyest.btcontrol.bean.Device;
import com.fireflyest.btcontrol.bean.Mode;
import com.fireflyest.btcontrol.bean.Record;
import com.fireflyest.btcontrol.data.DataManager;
import com.fireflyest.btcontrol.data.SettingManager;
import com.fireflyest.btcontrol.util.CalendarUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.lang.Thread.sleep;

public class InfoFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener{

    private static final String PROGRESS_TIME = "连续开启%s小时";
    private static final String PROGRESS_PERCENT = "%s%%";

    private TextView modeName;
    private TextView modeDesc;
    private TextView modeCode;
    private TextView progressText;
    private TextView progressPercent;
    private TextView controlText;
    private TextView controlTime;
    private ProgressBar progressBar;

    private ConstraintLayout modeBox;
    private ConstraintLayout codeBox;

    private ConstraintSet modeConstraintSet = new ConstraintSet();
    private ConstraintSet codeConstraintSet = new ConstraintSet();

    private Transition transition = new AutoTransition().setDuration(200);

    private SharedPreferences sharedPreferences;

    private RecordItemAdapter adapter;

    private List<Record> records = new ArrayList<>();

    private static final int CLEAN_VIEW = 0;
    private static final int REFRESH_MODE = 1;
    private static final int REFRESH_PROGRESS = 2;
    private static final int REFRESH_RECORDS = 3;
    private static final int REFRESH_CONTROL = 4;

    public InfoFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context context = getContext();
        if(context != null){
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        modeName = view.findViewById(R.id.control_mode_name);
        modeDesc = view.findViewById(R.id.control_mode_desc);
        modeCode = view.findViewById(R.id.control_mode_code);

        modeBox = view.findViewById(R.id.control_mode_box);
        modeConstraintSet.clone(modeBox);
        codeBox = view.findViewById(R.id.control_mode_code_box);
        codeConstraintSet.clone(codeBox);

        progressText = view.findViewById(R.id.control_mode_progress_time);
        progressPercent = view.findViewById(R.id.control_mode_progress_percent);
        progressBar = view.findViewById(R.id.control_mode_progress);

        controlText = view.findViewById(R.id.control_mode_code_control);
        controlTime = view.findViewById(R.id.control_mode_code_control_time);

        final RecyclerView recordList = view.findViewById(R.id.control_mode_record_list);
        recordList.setLayoutManager(new LinearLayoutManager(view.getContext()));
        adapter = new RecordItemAdapter(records);
        recordList.setAdapter(adapter);

        new Thread(new Runnable() {
            @Override
            public void run() {

                while (true){
                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Device device = DataManager.getInstance().getDeviceDao().findByAddress(SettingManager.SELECT_ADDRESS);
                    if(null != device) handler.obtainMessage(REFRESH_PROGRESS, device).sendToTarget();
                }
            }
        }).start();

        this.refreshLayout(SettingManager.SELECT_ADDRESS);

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if("select_address".equals(key)) {
            this.refreshLayout(sharedPreferences.getString(key, ""));
        }
    }

    @Override
    public void onDestroy() {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
    }

    //---------------------------------------------------------------------


    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case REFRESH_MODE:
                    Mode mode = (Mode) msg.obj;
                    modeName.setText(mode.getName());
                    modeDesc.setText(mode.getDesc());
                    modeCode.setText(mode.getCode());

                    TransitionManager.beginDelayedTransition(modeBox, transition);
                    modeName.setVisibility(View.VISIBLE);
                    modeDesc.setVisibility(View.VISIBLE);
                    modeConstraintSet.applyTo(modeBox);

                    TransitionManager.beginDelayedTransition(codeBox, transition);
                    modeCode.setVisibility(View.VISIBLE);
                    codeConstraintSet.applyTo(codeBox);
                    break;
                case REFRESH_PROGRESS:
                    Device device = (Device) msg.obj;

                    long progress;
                    if(device.isOpen()){
                        progress = CalendarUtil.getDate() - device.getStart();
                    }else {
                        progress = device.getEnd() - device.getStart();
                    }
                    int percent = (int)(progress*100/device.getProgress());

                    progressText.setText(String.format(PROGRESS_TIME, CalendarUtil.convertHour(progress)));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        progressBar.setProgress(percent, true);
                    }else {
                        progressBar.setProgress(percent);
                    }
                    progressPercent.setText(String.format(PROGRESS_PERCENT, percent));

                    break;
                case CLEAN_VIEW:

                    modeName.setText("");
                    modeDesc.setText("");
                    modeCode.setText("");

                    progressText.setText(String.format(PROGRESS_TIME, 0));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        progressBar.setProgress(0, true);
                    }else {
                        progressBar.setProgress(0);
                    }
                    progressPercent.setText(String.format(PROGRESS_PERCENT, 0));
                    break;
                case REFRESH_RECORDS:
                    adapter.notifyDataSetChanged();
                    break;
                case REFRESH_CONTROL:
                    Record record = (Record) msg.obj;
                    switch (record.getType()) {
                        case "Change":
                            controlText.setText("切换");
                            break;
                        case "Close":
                            controlText.setText("关闭");
                            break;
                        case "Open":
                            controlText.setText("开启");
                            break;
                        default:
                    }
                    controlTime.setText(CalendarUtil.convertTime(record.getTime()));
                    break;
                default:
            }
            return true;
        }
    });

    private void refreshLayout(final String address){

        new Thread(new Runnable() {
            @Override
            public void run() {

                handler.obtainMessage(CLEAN_VIEW).sendToTarget();

                try {
                    sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if("none".equals(address)) return;

                Device device = DataManager.getInstance().getDeviceDao().findByAddress(address);
                if(null == device)return;
                handler.obtainMessage(REFRESH_PROGRESS, device).sendToTarget();
                Mode mode = DataManager.getInstance().getModeDao().findByCode(String.format("%s", device.getMode()));
                if(null != mode){
                    handler.obtainMessage(REFRESH_MODE, mode).sendToTarget();
                }

                records.clear();
                List<Record> temp = DataManager.getInstance().getRecordDao().findByAddress(address);
                Collections.reverse(temp);
                if(temp.size() > 10){
                    records.addAll(temp.subList(0, 10));
                    handler.obtainMessage(REFRESH_CONTROL, temp.get(0)).sendToTarget();
                }else if(temp.size() > 0){
                    records.addAll(temp);
                    handler.obtainMessage(REFRESH_CONTROL, temp.get(0)).sendToTarget();
                }
                handler.obtainMessage(REFRESH_RECORDS).sendToTarget();


            }
        }).start();
    }

}