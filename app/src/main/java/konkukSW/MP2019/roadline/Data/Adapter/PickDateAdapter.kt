package konkukSW.MP2019.roadline.Data.Adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import konkukSW.MP2019.roadline.Data.Dataclass.PickDate
import konkukSW.MP2019.roadline.R

class PickDateAdapter(var items:ArrayList<PickDate>): RecyclerView.Adapter<PickDateAdapter.ViewHolder>() {
    
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(p0.context).inflate(R.layout.item_pick_date,p0,false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        p0.day.text = "Day " + items.get(p1).day.toString()
        p0.date.text = items.get(p1).date
    }

    inner class ViewHolder(itemView: View)
        :RecyclerView.ViewHolder(itemView){
        var day: TextView
        var date: TextView
        init{
            day = itemView.findViewById(R.id.PDItem_day)
            date = itemView.findViewById(R.id.PDItem_date)
        }

    }
}
