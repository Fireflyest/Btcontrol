package com.fireflyest.btcontrol.data;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.fireflyest.btcontrol.bean.Command;
import com.fireflyest.btcontrol.bean.Device;
import com.fireflyest.btcontrol.bean.Mode;
import com.fireflyest.btcontrol.bean.Record;
import com.fireflyest.btcontrol.dao.CommandDao;
import com.fireflyest.btcontrol.dao.DeviceDao;
import com.fireflyest.btcontrol.dao.ModeDao;
import com.fireflyest.btcontrol.dao.RecordDao;

@Database(entities = {Command.class, Mode.class, Device.class, Record.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract CommandDao commandDao();
    public abstract DeviceDao deviceDao();
    public abstract ModeDao modeDao();
    public abstract RecordDao recordDao();
}
