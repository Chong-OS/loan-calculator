package com.chongos.loancalculator.account.list

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.chongos.loancalculator.R
import com.chongos.loancalculator.data.entity.Account
import com.chongos.loancalculator.utils.view.inflate
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

/**
 * @author ChongOS
 * @since 20-Jul-2017
 */
class AccountListAdapter : RecyclerView.Adapter<AccountListAdapter.ViewHolder>() {

    val formatter by lazy {
        (NumberFormat.getInstance(Locale.US) as DecimalFormat).apply {
            applyPattern("#,###,###,###")
        }
    }
    var list: MutableList<Account>? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int) =
            ViewHolder(parent?.inflate(R.layout.item_account))

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        val account = list?.get(position)
        val context = holder?.itemView?.context

        holder?.textName?.text = account?.name
        holder?.textDetail?.text = context?.getString(R.string.text_fmt_account_detail, formatter.format(account?.amount), account?.years)
    }

    override fun getItemCount() = list?.size ?: 0

    class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        val textName = itemView?.findViewById<TextView>(R.id.textName)
        val textDetail = itemView?.findViewById<TextView>(R.id.textDetail)
        val textPayment = itemView?.findViewById<TextView>(R.id.textPayment)
    }
}