package com.dnl.to_do.data;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

@Entity(tableName = "task")
public class Task implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public int id;

    @ColumnInfo(name = "group_id")
    public int groupId;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "state")
    public int state;

    @ColumnInfo(name = "position")
    public int position;

    public Task() {

    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Task createFromParcel(Parcel in) {
            return new Task(in);
        }

        public Task[] newArray(int size) {
            return new Task[size];
        }
    };

    private Task(Parcel in) {
        this.id = in.readInt();
        this.groupId = in.readInt();
        this.name = in.readString();
        this.state = in.readInt();
        this.position = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeInt(this.groupId);
        dest.writeString(this.name);
        dest.writeInt(this.state);
        dest.writeInt(this.position);
    }
}
