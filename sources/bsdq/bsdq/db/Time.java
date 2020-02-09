package bsdq.bsdq.db;

public class Time {
    public String addr;
    public int id;
    public String stime;
    public long time;

    public Time(int i, String str, long j, String str2) {
        this.id = i;
        this.addr = str;
        this.time = j;
        this.stime = str2;
    }
}
