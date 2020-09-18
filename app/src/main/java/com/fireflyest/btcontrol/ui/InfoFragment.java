package com.fireflyest.btcontrol.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.transition.AutoTransition;
import androidx.transition.Transition;
import androidx.transition.TransitionManager;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fireflyest.btcontrol.R;
import com.fireflyest.btcontrol.bean.Device;
import com.fireflyest.btcontrol.bean.Mode;
import com.fireflyest.btcontrol.data.DataManager;
import com.fireflyest.btcontrol.data.SettingManager;
import com.fireflyest.btcontrol.util.AnimateUtil;

public class InfoFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener{

    private static final String KEY_CODE = "code";
    private static final String KEY_ADDRESS = "address";

    private TextView modeName;
    private TextView modeDesc;
    private TextView modeCode;

    private ConstraintLayout modeBox;
    private ConstraintLayout codeBox;

    private ConstraintSet modeConstraintSet = new ConstraintSet();
    private ConstraintSet codeConstraintSet = new ConstraintSet();

    private Transition transition = new AutoTransition().setDuration(150);

    private SharedPreferences sharedPreferences;

    private static final int CLEAN_TEXT = 0;
    private static final int SET_TEXT = 1;

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


//        TextView modeName = view.findViewById(R.id.control_mode_name);
//        TextView modeName = view.findViewById(R.id.control_mode_name);
//        TextView modeName = view.findViewById(R.id.control_mode_name);
//        TextView modeName = view.findViewById(R.id.control_mode_name);
//        TextView modeName = view.findViewById(R.id.control_mode_name);


        this.refreshText(SettingManager.SELECT_ADDRESS);


        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if("select_address".equals(key)) {
            this.refreshText(sharedPreferences.getString(key, ""));
        }
    }

    @Override
    public void onDestroy() {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            if(msg.what == SET_TEXT){

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
            }else if(msg.what == CLEAN_TEXT){
                modeName.setText("");
                modeDesc.setText("");
                modeCode.setText("");
            }
            return true;
        }
    });

    private void refreshText(final String address){

        TransitionManager.beginDelayedTransition(modeBox, transition);
        modeName.setVisibility(View.GONE);
        modeDesc.setVisibility(View.GONE);
        modeConstraintSet.applyTo(modeBox);
        TransitionManager.beginDelayedTransition(codeBox, transition);
        modeCode.setVisibility(View.GONE);
        codeConstraintSet.applyTo(codeBox);

        new Thread(new Runnable() {
            @Override
            public void run() {
                if("none".equals(address)){
                    handler.obtainMessage(CLEAN_TEXT).sendToTarget();
                    return;
                }
                Device device = DataManager.getInstance().getDeviceDao().findByAddress(address);
                if(null == device)return;
                Mode mode = DataManager.getInstance().getModeDao().findByCode(String.format("%s", device.getMode()));
                if(null != mode){
                    handler.obtainMessage(SET_TEXT, mode).sendToTarget();
                }

            }
        }).start();
    }

}