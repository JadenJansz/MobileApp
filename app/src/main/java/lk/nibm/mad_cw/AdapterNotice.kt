package lk.nibm.mad_cw

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AdapterNotice(private val noticeList: ArrayList<NoticeArray>, context: Context) :  RecyclerView.Adapter<AdapterNotice.ViewHolder>() {

    var context : Context
    init {
        this.context = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterNotice.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_notice, parent, false)

        return AdapterNotice.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = noticeList[position]
        holder.date.text = "Date : " + currentItem.date
        holder.subject.text = currentItem.subject
        holder.message.text = currentItem.message
    }

    override fun getItemCount(): Int {
        return noticeList.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView){
        val date : TextView =itemView.findViewById(R.id.notice_date)
        val subject : TextView =itemView.findViewById(R.id.notice_subject)
        var message : TextView =itemView.findViewById(R.id.notice_message)
    }


}