package com.fireflyest.btcontrol.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fireflyest.btcontrol.CommandActivity;
import com.fireflyest.btcontrol.MainActivity;
import com.fireflyest.btcontrol.ModeActivity;
import com.fireflyest.btcontrol.R;
import com.fireflyest.btcontrol.bt.BtManager;
import com.fireflyest.btcontrol.data.SettingManager;
import com.fireflyest.btcontrol.util.ToastUtil;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ControlFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ControlFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ControlFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ControlFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ControlFragment newInstance(String param1, String param2) {
        ControlFragment fragment = new ControlFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_control, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        ConstraintLayout commandButton = view.findViewById(R.id.control_command_box);
        commandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(view.getContext(), CommandActivity.class);
                if(!BtManager.getBtController().isConnected(SettingManager.SELECT_ADDRESS)){
                    ToastUtil.showShort(view.getContext(), "该设备未连接");
                    intent.putExtra("connect", false);
                }else {
                    intent.putExtra("connect", true);
                }
                Activity activity = getActivity();
                if(null != activity) activity.startActivityForResult(intent, MainActivity.REQUEST_COMMAND);
            }
        });

        ConstraintLayout modeButton = view.findViewById(R.id.control_mode_box);
        modeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(view.getContext(), ModeActivity.class);
                if(!BtManager.getBtController().isConnected(SettingManager.SELECT_ADDRESS)){
                    ToastUtil.showShort(view.getContext(), "该设备未连接");
                    intent.putExtra("connect", false);
                }else {
                    intent.putExtra("connect", true);
                }
                Activity activity = getActivity();
                if(null != activity) activity.startActivityForResult(intent, MainActivity.REQUEST_MODE);
            }
        });

        super.onViewCreated(view, savedInstanceState);
    }
}