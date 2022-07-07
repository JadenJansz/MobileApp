package lk.nibm.mad_cw

import android.media.Image
import android.os.Parcel
import android.os.Parcelable
import android.widget.ImageView
import com.google.firebase.storage.StorageReference

data class UserArray(var uid: String ?= null,var fname: String ?= null, var lname: String?= null, var email: String?= null,var dob : String?= null, var profileImage:String?= null, var contact: String?= null,
        var emergency: String?= null, var nic: String?= null) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(uid)
        parcel.writeString(fname)
        parcel.writeString(lname)
        parcel.writeString(email)
        parcel.writeString(dob)
        parcel.writeString(profileImage)
        parcel.writeString(contact)
        parcel.writeString(emergency)
        parcel.writeString(nic)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserArray> {
        override fun createFromParcel(parcel: Parcel): UserArray {
            return UserArray(parcel)
        }

        override fun newArray(size: Int): Array<UserArray?> {
            return arrayOfNulls(size)
        }
    }

}
