package bsdq.bsdq.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBAdapter {
    public static final String DB_ACTION = "db_action";
    private static final String DB_NAME = "BD001.db";
    private static final int DB_VERSION = 1;
    private static DBAdapter mDBAdapter;
    private static Resources mResources;
    private static Context xContext;
    private SQLiteDatabase db;
    private DBOpenHelper dbOpenHelper;
    private boolean isOpen = false;

    private static class DBOpenHelper extends SQLiteOpenHelper {
        private static final String DB_CREATE = "CREATE TABLE RecordDBTable (_id integer primary key autoincrement, time integer , addr varchar );";
        private static final String DEVICE_DB_CREATE = "CREATE TABLE DeviceTable (addr varchar primary key , pwd varchar, devicename varchar, auto integer, mechanical_code varchar, name varchar );";
        private static final String USER_DB_CREATE = "CREATE TABLE UserTable (_id integer primary key autoincrement, name varchar , pwd1 varchar , pwd2 varchar , question varchar , anwser varchar , pwdtype integer );";

        public DBOpenHelper(Context context, String str, CursorFactory cursorFactory, int i) {
            super(context, str, cursorFactory, i);
        }

        public void onCreate(SQLiteDatabase sQLiteDatabase) {
            sQLiteDatabase.execSQL(DB_CREATE);
            sQLiteDatabase.execSQL(DEVICE_DB_CREATE);
            sQLiteDatabase.execSQL(USER_DB_CREATE);
            ContentValues contentValues = new ContentValues();
            contentValues.put("name", "scott");
            contentValues.put(UserTable.PWD1, "123456");
            contentValues.put(UserTable.PWD2, "");
            contentValues.put(UserTable.PWDTYPE, Integer.valueOf(0));
            contentValues.put(UserTable.QUESTION, "");
            contentValues.put(UserTable.ANWSER, "");
            sQLiteDatabase.insert(UserTable.DB_TABLE, null, contentValues);
        }

        public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
            sQLiteDatabase.execSQL("DROP TABLE IF EXISTS RecordDBTable");
            onCreate(sQLiteDatabase);
            Log.e(DBAdapter.DB_ACTION, "Upgrade");
        }
    }

    private DBAdapter() {
    }

    public static DBAdapter init(Context context) {
        if (mDBAdapter != null) {
            return mDBAdapter;
        }
        xContext = context;
        mResources = xContext.getResources();
        mDBAdapter = new DBAdapter();
        return mDBAdapter;
    }

    public void open() throws SQLiteException {
        if (!this.isOpen) {
            this.dbOpenHelper = new DBOpenHelper(xContext, DB_NAME, null, 1);
            try {
                this.db = this.dbOpenHelper.getWritableDatabase();
            } catch (SQLiteException unused) {
                this.db = this.dbOpenHelper.getReadableDatabase();
            }
        }
    }

    public void close() {
        SQLiteDatabase sQLiteDatabase = this.db;
    }

    public long insert(String str, ContentValues contentValues) {
        if (contentValues == null) {
            return -1;
        }
        return this.db.insert(str, null, contentValues);
    }

    public long deleteOneData(String str, String str2, String[] strArr) {
        return (long) this.db.delete(str, str2, strArr);
    }

    public long deleteAllData(String str) {
        return (long) this.db.delete(str, null, null);
    }

    public Cursor queryAllData(String str) {
        return this.db.query(str, null, null, null, null, null, null);
    }

    public Cursor queryAllDataOrderbyDesc(String str) {
        StringBuilder sb = new StringBuilder();
        sb.append("select * from RecordDBTable where addr = '");
        sb.append(str);
        sb.append("' order by _id desc");
        return this.db.rawQuery(sb.toString(), null);
    }

    public Cursor queryDevice(String str) {
        StringBuilder sb = new StringBuilder();
        sb.append("select * from DeviceTable where addr = '");
        sb.append(str);
        sb.append("' ");
        return this.db.rawQuery(sb.toString(), null);
    }

    public Cursor queryAllData(String str, String[] strArr) {
        return this.db.query(str, strArr, null, null, null, null, null);
    }

    public int upDataforTable(String str, ContentValues contentValues, String str2, String[] strArr) {
        return this.db.update(str, contentValues, str2, strArr);
    }
}
