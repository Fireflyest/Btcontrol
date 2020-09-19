package com.fireflyest.btcontrol.data;

import android.content.Context;

import androidx.room.Room;

import com.fireflyest.btcontrol.dao.CommandDao;
import com.fireflyest.btcontrol.dao.DeviceDao;
import com.fireflyest.btcontrol.dao.ModeDao;
import com.fireflyest.btcontrol.dao.RecordDao;

public class DataManager {

    private static DataManager dataManager = new DataManager();

    public static DataManager getInstance(){return dataManager;}

    private static final String database = "bt-control";

    private AppDatabase appDatabase;

    private DataManager(){
    }

    public synchronized void initDataManager(final Context context){
        if(null != appDatabase)return;
        new Thread(new Runnable() {
            @Override
            public void run() {
                appDatabase = Room.databaseBuilder(context, AppDatabase.class, database)
                        .fallbackToDestructiveMigration()
                        .build();
            }
        }).start();
    }

    public DeviceDao getDeviceDao(){
        return appDatabase.deviceDao();
    }

    public CommandDao getCommandDao(){
        return appDatabase.commandDao();
    }

    public ModeDao getModeDao(){
        return appDatabase.modeDao();
    }

    public RecordDao getRecordDao(){
        return appDatabase.recordDao();
    }

}
