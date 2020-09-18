package com.fireflyest.btcontrol;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;

import com.fireflyest.btcontrol.adapter.ModeList.ModeItemAdapter;
import com.fireflyest.btcontrol.api.BleController;
import com.fireflyest.btcontrol.api.callback.OnWriteCallback;
import com.fireflyest.btcontrol.bean.Mode;
import com.fireflyest.btcontrol.data.DataManager;
import com.fireflyest.btcontrol.dialog.AddModeDialog;
import com.fireflyest.btcontrol.util.StatusBarUtil;
import com.fireflyest.btcontrol.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

public class ModeActivity extends AppCompatActivity implements AddModeDialog.NoticeDialogListener{

    private DataManager dataManager;

    private ModeItemAdapter adapter;
    private BleController controller;

    private RecyclerView modeList;

    private List<Mode> modes = new ArrayList<>();

    public static final int UPDATE_LIST = 0;
    public static final int UPDATE_ITEM = 1;
    public static final int BACK_RESULT = 2;

    private String mode = "none";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mode);

        this.initData();

        this.initView();

        this.initBluetooth();

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                back();
                break;
            case R.id.nav_add:
                new AddModeDialog().show(getSupportFragmentManager(), "Add item");
                break;
            default:
        }
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.menu_mode, menu);
        return true;
    }

    @Override
    public void onDialogAddClick(@NonNull final Mode mode) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int id = (int) dataManager.getModeDao().insert(mode);
                mode.setId(id);
                handler.obtainMessage(UPDATE_ITEM, mode).sendToTarget();
            }
        }).start();
    }

    @Override
    public void onBackPressed() {
        this.back();
    }

    //----------------------------------------------------------------





    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case UPDATE_LIST:
                    adapter.notifyDataSetChanged();
                    break;
                case UPDATE_ITEM:
                    Mode mode = (Mode)msg.obj;
                    adapter.addItem(0, mode);
                    modeList.smoothScrollToPosition(0);
                    break;
                case BACK_RESULT:
                    sendCommand(msg.obj.toString());
                    break;
                default:
            }
            return true;
        }
    });

    private void initView(){
        StatusBarUtil.StatusBarLightMode(this);

        Toolbar toolbar = findViewById(R.id.toolbar_mode);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        modeList = findViewById(R.id.mode_list);
        adapter = new ModeItemAdapter(modes, handler);
        modeList.setAdapter(adapter);
        modeList.setLayoutManager(new GridLayoutManager(this, 2, RecyclerView.VERTICAL, false));

    }

    private void initData(){

        dataManager = DataManager.getInstance();

        new Thread(new Runnable() {
            @Override
            public void run() {
                dataManager = DataManager.getInstance();
                modes.addAll(dataManager.getModeDao().getAll());
                handler.obtainMessage(UPDATE_LIST).sendToTarget();
            }
        }).start();
    }

    private void initBluetooth(){
        controller = BleController.getInstance();
    }

    /**
     * 发送蓝牙指令
     * @param command 指令
     */
    private void sendCommand(final String command){
        byte[] bytes = (command).getBytes();
        controller.writeBuffer(bytes, new OnWriteCallback() {
            @Override
            public void onSuccess() {
                ToastUtil.showShort(getBaseContext(), "模式修改: "+command);
                mode = command;
            }

            @Override
            public void onFailed(int state) {
                ToastUtil.showShort(getBaseContext(), "修改失败");
            }
        });
    }

    private void back(){
        if(!"none".equals(mode)){
            Intent intent = new Intent();
            intent.putExtra("mode", mode);
            setResult(RESULT_OK, intent);
        }
        finish();
    }

}