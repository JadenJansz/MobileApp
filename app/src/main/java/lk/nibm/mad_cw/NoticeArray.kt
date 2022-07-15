package lk.nibm.mad_cw

import android.os.Parcel
import android.os.Parcelable

data class NoticeArray(var date : String?= null, var message : String?= null, var subject : String?= null) :
    Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(date)
        parcel.writeString(message)
        parcel.writeString(subject)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<NoticeArray> {
        override fun createFromParcel(parcel: Parcel): NoticeArray {
            return NoticeArray(parcel)
        }

        override fun newArray(size: Int): Array<NoticeArray?> {
            return arrayOfNulls(size)
        }
    }
}