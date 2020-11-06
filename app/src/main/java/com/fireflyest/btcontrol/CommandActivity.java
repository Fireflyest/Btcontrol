package com.fireflyest.btcontrol;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.AutoTransition;
import androidx.transition.Transition;
import androidx.transition.TransitionManager;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.fireflyest.btcontrol.adapter.CommandList.CommandItemAdapter;
import com.fireflyest.btcontrol.bt.BleController;
import com.fireflyest.btcontrol.bt.BtManager;
import com.fireflyest.btcontrol.bt.callback.OnReceiverCallback;
import com.fireflyest.btcontrol.bt.callback.OnWriteCallback;
import com.fireflyest.btcontrol.bean.Command;
import com.fireflyest.btcontrol.bean.Mode;
import com.fireflyest.btcontrol.data.DataManager;
import com.fireflyest.btcontrol.data.SettingManager;
import com.fireflyest.btcontrol.util.AnimateUtil;
import com.fireflyest.btcontrol.util.CalendarUtil;
import com.fireflyest.btcontrol.util.StatusBarUtil;
import com.fireflyest.btcontrol.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

public class CommandActivity extends AppCompatActivity {

    private CommandItemAdapter adapter;
    private BleController bleController;
    private DataManager dataManager;

    private List<Command> commands = new ArrayList<>();

    private EditText sendEdit;
    private TextView sendButton;
    private ImageButton moreButton;
    private ConstraintLayout editBox;
    private ConstraintLayout circulationBox;
    private EditText circulationEdit;
    private RecyclerView commandList;
    private RecyclerView moreList;

    private ConstraintSet editConstraintSet = new ConstraintSet();

    private boolean circulation = false;

    private static final int SEND_COMMAND = 1;
    private static final int ADD_COMMAND = 2;

    private static String address = "";

    private String mode = "none";
    private long last_time = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_command);

        this.initBluetooth();

        this.initData();

        this.initView();

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                back();
                break;
            case R.id.nav_check:
                if(circulationBox.getVisibility() == View.GONE){
                    circulation = true;
                    AnimateUtil.show(circulationBox, 300, 0);
                }else {
                    circulation = false;
                    AnimateUtil.hide(circulationBox, 300, 0);
                }
                break;
            case R.id.nav_clear:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for(Command command : dataManager.getCommandDao().getAll()){
                            dataManager.getCommandDao().delete(command);
                        }
                    }
                }).start();
                commands.clear();
                adapter.notifyDataSetChanged();
                break;
            default:
        }
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.menu_command, menu);
        return true;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View view = getCurrentFocus();
            if(moreList.getVisibility() == View.VISIBLE){
                moreButton.setVisibility(View.VISIBLE);
                moreList.setVisibility(View.GONE);
            }
            if (isShouldHideInput(view, event)) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
            return super.dispatchTouchEvent(event);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        if (getWindow().superDispatchTouchEvent(event)) {
            return true;
        }
        return onTouchEvent(event);
    }


    @Override
    protected void onDestroy() {
        bleController.unregisterReceiveListener(address);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        this.back();
    }


    //-------------------------------------------------------------------------------


    private void initBluetooth(){
        address = SettingManager.SELECT_ADDRESS;
        bleController = (BleController) BtManager.getBtController();
        bleController.registerReceiveListener(address, new OnReceiverCallback() {
            @Override
            public void onReceive(byte[] value) {
                String string = new String(value);
                addData(string, "Receive", true);
            }
        });

    }

    private void initView(){
        StatusBarUtil.StatusBarLightMode(this);

        Toolbar toolbar = findViewById(R.id.toolbar_command);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        circulationBox = findViewById(R.id.command_circulation);
        circulationEdit = findViewById(R.id.command_circulation_delay);

        commandList = findViewById(R.id.command_list);
        commandList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CommandItemAdapter(commands);
        commandList.setAdapter(adapter);

        moreList = findViewById(R.id.command_more_list);

        sendEdit = findViewById(R.id.command_edit);
        sendButton = findViewById(R.id.command_send);
        moreButton = findViewById(R.id.command_more);
        editBox = findViewById(R.id.command_edit_box);
        editConstraintSet.clone(editBox);

        Intent intent = this.getIntent();
        boolean connect = intent.getBooleanExtra("connect", false);
        if(!connect){
            sendEdit.setEnabled(false);
            sendButton.setEnabled(false);
        }

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String sendText = sendEdit.getText().toString().trim();
                if(circulation){
                    int delay = 0;
                    try{
                        delay = Integer.parseInt(circulationEdit.getText().toString().trim());
                    }catch (NumberFormatException ignored){ }
                    new ThreadCommand(sendText, delay).start();
                }else {
                    sendCommand(sendText);
                }
            }
        });

        sendEdit.addTextChangedListener(new TextWatcher() {

            private Transition transition = new AutoTransition().setDuration(150);

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                this.transition(s.length() != 0);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

            public synchronized void transition(boolean hasText){
                TransitionManager.beginDelayedTransition(editBox, transition);
                if(hasText) {
                    moreButton.setVisibility(View.GONE);
                    sendButton.setVisibility(View.VISIBLE);
                }else {
                    moreButton.setVisibility(View.VISIBLE);
                    sendButton.setVisibility(View.GONE);
                }
                editConstraintSet.applyTo(editBox);
            }

        });

        moreButton.setOnClickListener(new View.OnClickListener() {

            private Transition transition = new AutoTransition().setDuration(150);

            @Override
            public void onClick(View v) {
                AnimateUtil.click(v, 100);
                TransitionManager.beginDelayedTransition(editBox, transition);
                if(moreList.getVisibility() == View.GONE) {
                    moreList.setVisibility(View.VISIBLE);
                }else {
                    moreList.setVisibility(View.GONE);
                }
                if(moreButton.getVisibility() == View.GONE) {
                    moreButton.setVisibility(View.VISIBLE);
                }else {
                    moreButton.setVisibility(View.GONE);
                }
                editConstraintSet.applyTo(editBox);
            }
        });

    }

    private void initData(){
        dataManager = DataManager.getInstance();

        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Command> all = dataManager.getCommandDao().findByAddress(SettingManager.SELECT_ADDRESS);
                for(Command command : all){
                    handler.obtainMessage(ADD_COMMAND, command).sendToTarget();
                }
            }
        }).start();
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case SEND_COMMAND:
                    sendCommand(String.valueOf(msg.obj));
                    break;
                case ADD_COMMAND:
                    adapter.addItem((Command)msg.obj);
                    commandList.smoothScrollToPosition(adapter.getItemCount());
                    sendEdit.setText("");
                    break;
                default:
            }
            return true;
        }
    });

    /**
     * 发送蓝牙指令
     * @param command 指令
     */
    private void sendCommand(final String command){
        byte[] bytes = (command).getBytes();
        bleController.writeBuffer(address, bytes, new OnWriteCallback() {
            @Override
            public void onSuccess() {
                addData(command, "Send", true);
            }

            @Override
            public void onFailed(int state) {
                ToastUtil.showShort(getBaseContext(), "发送失败");
                addData(command, "Send", false);
            }
        });
    }

    /**
     * 添加指令数据
     * @param command 指令
     * @param type 类型
     * @param success 是否成功
     */
    private void addData(final String command, final String type, final boolean success){
        new Thread(new Runnable() {
            @Override
            public void run() {

                if(CalendarUtil.getDate() - last_time > 180000L){
                    Command time = new Command();
                    time.setText("#");
                    time.setType("System");
                    time.setTime(CalendarUtil.getDate());
                    time.setSuccess(true);
                    time.setAddress(address);
                    dataManager.getCommandDao().insertAll(time);
                    handler.obtainMessage(ADD_COMMAND, time).sendToTarget();
                }
                last_time = CalendarUtil.getDate();

                Command data = new Command();
                data.setText(command);
                data.setType(type);
                data.setTime(CalendarUtil.getDate());
                data.setSuccess(success);
                data.setAddress(address);
                Log.e("CommandActivity",  "address ->"+ address);
                dataManager.getCommandDao().insertAll(data);
                handler.obtainMessage(ADD_COMMAND, data).sendToTarget();

                Mode m = dataManager.getModeDao().findByCode(command);
                if(success){
                    if(m != null){
                        mode = m.getCode();
                        Command system = new Command();
                        system.setText("模式修改: "+command);
                        system.setType("System");
                        system.setTime(CalendarUtil.getDate());
                        system.setSuccess(true);
                        system.setAddress(address);
                        dataManager.getCommandDao().insertAll(system);
                        handler.obtainMessage(ADD_COMMAND, system).sendToTarget();
                    }else if(SettingManager.CLOSE_CODE.equals(command)){
                        mode = SettingManager.CLOSE_CODE;
                        Command system = new Command();
                        system.setText("设备关闭: "+command);
                        system.setType("System");
                        system.setTime(CalendarUtil.getDate());
                        system.setSuccess(true);
                        system.setAddress(address);
                        dataManager.getCommandDao().insertAll(system);
                        handler.obtainMessage(ADD_COMMAND, system).sendToTarget();
                    }
                }

//                if(!lastCommand.equals(command) && "Send".equals(type)){
//                    History history = new History();
//                    history.setTime(CalendarUtil.getDate());
//                    history.setSucceed(ok);
//                    history.setName(command);
//                    dataManager.getHistoryDao().insertAll(history);
//                }
//                lastCommand = command;
            }
        }).start();
    }

    private boolean isShouldHideInput(View view, MotionEvent event) {
        if (view instanceof EditText) {
            int[] leftTop = {0, 0};
            //获取输入框当前的location位置
            view.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + view.getHeight();
            int right = left + view.getWidth();
            // 点击的是输入框区域，保留点击EditText的事件
            return !(event.getX() > left) || !(event.getX() < right) || !(event.getY() > top) || !(event.getY() < bottom);
        }
        return false;
    }

    private void back(){
        if(!"none".equals(mode)){
            Intent intent = new Intent();
            intent.putExtra("mode", mode);
            setResult(RESULT_OK, intent);
        }
        finish();
    }


    /**
     * 循环发送
     */
    class ThreadCommand extends Thread {

        private String command;
        private Integer delay;

        private ThreadCommand(String command, Integer delay){
            this.command = command;
            this.delay = delay;
        }

        @Override
        public void run() {
            while (circulation){
                try {
                    sleep(delay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                handler.obtainMessage(SEND_COMMAND, command).sendToTarget();
            }
        }

    }

}