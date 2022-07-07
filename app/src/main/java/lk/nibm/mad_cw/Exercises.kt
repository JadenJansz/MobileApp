package lk.nibm.mad_cw

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

data class Exercises(var name: String ?= null, var weight : Double, var date : String ?= null, var week : String? = null ) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readDouble(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeDouble(weight)
        parcel.writeString(date)
        parcel.writeString(week)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Exercises> {
        override fun createFromParcel(parcel: Parcel): Exercises {
            return Exercises(parcel)
        }

        override fun newArray(size: Int): Array<Exercises?> {
            return arrayOfNulls(size)
        }
    }


}