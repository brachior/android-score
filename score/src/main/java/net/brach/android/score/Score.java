package net.brach.android.score;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class Score implements Parcelable {
    int level;
    int percent;

    public Score() {
        this(0, 0);
    }

    public Score(int level, int percent) {
        this.level = level;
        this.percent = percent;
    }

    public void update(int level, int percent) {
        this.level = level;
        this.percent = percent;
    }

    /************************/
    /** {@link Parcelable} **/
    /************************/

    private Score(Parcel in) {
        level = in.readInt();
        percent = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(level);
        dest.writeInt(percent);
    }

    public static final Creator<Score> CREATOR = new Creator<Score>() {
        @Override
        public Score createFromParcel(Parcel in) {
            return new Score(in);
        }

        @Override
        public Score[] newArray(int size) {
            return new Score[size];
        }
    };
}