package com.wolfpackdigital.kliner.partner.shared.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.wolfpackdigital.kliner.partner.BR

abstract class BaseAdapter<ITEM, BINDING : ViewDataBinding>(
    @LayoutRes private val itemLayout: Int,
    diffUtil: DiffUtil.ItemCallback<ITEM>
) : ListAdapter<ITEM, BaseAdapter.BaseViewHolder<BINDING>>(diffUtil) {

    @CallSuper
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<BINDING> {
        val binding = DataBindingUtil.inflate<BINDING>(LayoutInflater.from(parent.context), itemLayout, parent, false)
        val viewHolder = BaseViewHolder(binding)
        return viewHolder
    }

    @CallSuper
    override fun onBindViewHolder(holder: BaseViewHolder<BINDING>, position: Int) {
        val item = getItem(position)
        holder.binding.setVariable(BR.viewModel, item)
        bind(holder.binding, item)
        holder.binding.executePendingBindings()
    }

    protected abstract fun bind(binding: BINDING, item: ITEM)

    class BaseViewHolder<out BINDING : ViewDataBinding>(val binding: BINDING) : RecyclerView.ViewHolder(binding.root)
}
