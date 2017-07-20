package com.chongos.loancalculator.account.create

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.chongos.loancalculator.R
import com.chongos.loancalculator.data.entity.Rate
import com.chongos.loancalculator.utils.view.inflate

/**
 * @author ChongOS
 * @since 20-Jul-2017
 */
class TierRateAdapter : RecyclerView.Adapter<TierRateAdapter.ViewHolder>() {

    private val list = mutableListOf<Rate>()

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int) =
            ViewHolder(parent?.inflate(R.layout.item_tier_rate))

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        val context = holder?.itemView?.context
        val rate = list[position]
        holder?.textOrder?.text = context?.getString(R.string.text_fmt_tier, rate.order)
        holder?.textDetail?.text = "interest"
    }

    override fun getItemCount() = list.size

    fun add(rate: Rate) {
        list.add(rate)
        notifyItemInserted(list.size)
    }

    fun remove(rate: Rate) {
        val index = list.indexOf(rate)
        if (index < 0 || index > list.size)
            return

        list.remove(rate)
        notifyItemRemoved(index)
    }

    fun update(index: Int, rate: Rate) {
        list[index] = rate
        notifyItemChanged(index)
    }

    class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        val textOrder = itemView?.findViewById<TextView>(R.id.textTierOrder)
        val textDetail = itemView?.findViewById<TextView>(R.id.textTierDetail)
    }
}