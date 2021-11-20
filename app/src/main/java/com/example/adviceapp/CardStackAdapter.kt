package com.example.adviceapp


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.adviceapp.databinding.AdviceCardBinding


class CardStackAdapter(
    private var advices: MutableList<Advice>
) : RecyclerView.Adapter<CardStackAdapter.ViewHolder>() {

    class ViewHolder(val binding: AdviceCardBinding) : RecyclerView.ViewHolder(binding.root) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(AdviceCardBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    }

    override fun getItemCount(): Int {
        return advices.size
    }

    fun getAdvicesList(): MutableList<Advice>{
        return advices
    }

    fun setAdvicesList(advices: MutableList<Advice>) {
        this.advices = advices
    }

    interface LoadDataFromOnlineAndSet{
        fun loadDataFromOnlineAndSet(holder: ViewHolder, position: Int)
    }
}