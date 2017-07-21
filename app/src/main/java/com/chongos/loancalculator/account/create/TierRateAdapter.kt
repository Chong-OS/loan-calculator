package com.chongos.loancalculator.account.create

import android.annotation.SuppressLint
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.chongos.loancalculator.R
import com.chongos.loancalculator.data.entity.Rate
import com.chongos.loancalculator.utils.view.inflate
import io.reactivex.rxkotlin.toObservable

/**
 * @author ChongOS
 * @since 20-Jul-2017
 */
class TierRateAdapter : RecyclerView.Adapter<TierRateAdapter.ViewHolder>() {

    val list = mutableListOf<Rate>()
    var onItemSelectListener: OnItemSelectListener? = null

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int) =
            ViewHolder(parent?.inflate(R.layout.item_tier_rate))

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        val context = holder?.itemView?.context
        val rate = list[position]
        holder?.itemView?.setOnClickListener { onItemSelectListener?.onSelect(rate, position) }
        holder?.textOrder?.text = context?.getString(R.string.text_fmt_tier, position + 1)
        holder?.textDetail?.text = context?.getString(R.string.text_fmt_interest_rate, rate.rate) + " " +
                if (position == list.size - 1) context?.getString(R.string.text_until_tenor_ends)
                else context?.getString(R.string.text_fmt_year, rate.months.div(12))
    }

    override fun getItemCount() = list.size

    fun add(rate: Rate) {
        list.add(rate)
        notifyDataSetChanged()
    }

    fun remove(rate: Rate) {
        remove(list.indexOf(rate))
    }

    fun remove(pos: Int) {
        if (pos < 0 || pos > list.size)
            return

        list.removeAt(pos)
        notifyDataSetChanged()
    }

    fun clear() {
        list.clear()
        notifyDataSetChanged()
    }

    fun update(index: Int, rate: Rate) {
        list[index] = rate
        notifyItemChanged(index)
    }

    class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        val textOrder = itemView?.findViewById<TextView>(R.id.textTierOrder)
        val textDetail = itemView?.findViewById<TextView>(R.id.textTierDetail)
    }

    interface OnItemSelectListener {
        fun onSelect(rate: Rate, pos: Int)
    }
}