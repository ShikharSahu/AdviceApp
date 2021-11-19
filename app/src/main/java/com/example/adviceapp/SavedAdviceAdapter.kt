package com.example.adviceapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.adviceapp.databinding.AdviceCardBinding
import com.example.adviceapp.databinding.SavedAdviceCardsBinding


class SavedAdviceAdapter(private val dataSet: MutableList<Advice>) :
    RecyclerView.Adapter<SavedAdviceAdapter.ViewHolder>() {

    class ViewHolder(val binding: SavedAdviceCardsBinding) : RecyclerView.ViewHolder(binding.root) {}

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(SavedAdviceCardsBinding.inflate(LayoutInflater.from(viewGroup.context),viewGroup ,false))
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.binding.tvCardId.text = "Advice #${dataSet[position].id}"
        viewHolder.binding.tvCardAdvice.text = dataSet[position].adText
        viewHolder.binding.tvSavedTime.text = "saved "+ Utils.getTimeAgo(dataSet[position].timeSavedAt)
    }

    override fun getItemCount() = dataSet.size

}
