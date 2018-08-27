package apps.hosamazzam.com.intdv_task.Db;

import android.arch.persistence.room.Room;
import android.content.Context;

public class DBHelper {
    private static AppDatabase appDatabase;

    private DBHelper(Context context) {
        appDatabase = Room.databaseBuilder(context,
                AppDatabase.class, "my-addresses").build();
    }

    public AppDatabase getInstance(Context context) {
        if (appDatabase == null) {
            new DBHelper(context);
        }

        return appDatabase;
    }

}
