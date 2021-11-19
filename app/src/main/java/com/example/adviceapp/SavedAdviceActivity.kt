package com.example.adviceapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import com.example.adviceapp.databinding.ActivitySavedAdviceBinding

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import java.lang.Exception
import androidx.recyclerview.widget.DividerItemDecoration

class SavedAdviceActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySavedAdviceBinding
    private lateinit var recycler : RecyclerView
    private lateinit var adapter : SavedAdviceAdapter
    private lateinit var allAdvices : MutableList<Advice>
    private lateinit var manager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySavedAdviceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recycler = binding.rvSavedAdvice

        allAdvices = getAllAdvice()

        adapter = SavedAdviceAdapter(allAdvices)

        val simpleItemTouchCallback: ItemTouchHelper.SimpleCallback = object :
            ItemTouchHelper.SimpleCallback(
                0,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT or ItemTouchHelper.DOWN or ItemTouchHelper.UP
            ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
                val position = viewHolder.adapterPosition
                val removedAdvice = allAdvices[position]
                if(swipeDir == ItemTouchHelper.LEFT) {

                    allAdvices.removeAt(position)
                    adapter.notifyItemRemoved(position)

                    val dbHelper = DBHelper(this@SavedAdviceActivity, null)
                    dbHelper.deleteAdvice(removedAdvice.id)
                }
                else if(swipeDir == ItemTouchHelper.RIGHT){
                    shareAdvice(removedAdvice)
                    adapter.notifyItemChanged(position)
                }
            }
        }

        manager =  LinearLayoutManager(this)
        recycler.layoutManager = manager
        recycler.adapter = adapter


        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(recycler)

        val dividerItemDecoration = DividerItemDecoration(
            recycler.context,
            manager.orientation
        )
        recycler.addItemDecoration(dividerItemDecoration)

    }

    private fun getAllAdvice() : MutableList<Advice>{
        val db = DBHelper(this, null)
        val allAdvicesRetrievedFromDB = mutableListOf<Advice>()

        try {

            val cursor = db.getCursorWithSelectAll()

            cursor!!.moveToFirst()

            allAdvicesRetrievedFromDB.add(
                Advice(
                    cursor.getInt(cursor.getColumnIndex(DBHelper.ID_COL)),
                    cursor.getString(cursor.getColumnIndex(DBHelper.ADVICE_COl)),
                    cursor.getLong(cursor.getColumnIndex(DBHelper.TIME_SAVED_COL))
                )
            )

            while (cursor.moveToNext()) {
                allAdvicesRetrievedFromDB.add(
                    Advice(
                        cursor.getInt(cursor.getColumnIndex(DBHelper.ID_COL)),
                        cursor.getString(cursor.getColumnIndex(DBHelper.ADVICE_COl)),
                        cursor.getLong(cursor.getColumnIndex(DBHelper.TIME_SAVED_COL))
                    )
                )
            }

            cursor.close()
        }
        catch (e : Exception){

        }
        return allAdvicesRetrievedFromDB

    }
    private fun shareAdvice (advice: Advice){
        val i = Intent(Intent.ACTION_SEND)
        i.type = "text/plain"
        i.putExtra(Intent.EXTRA_TEXT, "Here's some advice I read online. " +
                "Thought you'd like it too. \"${advice.adText}\"")
        startActivity(i)
    }
}