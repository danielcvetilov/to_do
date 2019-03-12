package com.dnl.to_do.data;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

@Entity(tableName = "task_group")
public class TaskGroup implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public int id;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "position")
    public int position;

    public TaskGroup() {

    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public TaskGroup createFromParcel(Parcel in) {
            return new TaskGroup(in);
        }

        public TaskGroup[] newArray(int size) {
            return new TaskGroup[size];
        }
    };

    private TaskGroup(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.position = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeInt(this.position);
    }
}
