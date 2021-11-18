package com.example.adviceapp

import android.content.ContentValues
import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.recyclerview.widget.DefaultItemAnimator
import com.example.adviceapp.databinding.ActivityMainBinding
import com.yuyakaido.android.cardstackview.*
import android.content.Intent
import android.view.SurfaceHolder
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.DiffUtil
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import layout.VolleySingletonRequestQueue


class MainActivity : AppCompatActivity(), CardStackListener, CardStackAdapter.LoadDataFromOnlineAndSet {

    lateinit var cardStackView: CardStackView
    lateinit var manager: CardStackLayoutManager
    lateinit var adapter: CardStackAdapter
    lateinit var binding: ActivityMainBinding
    val adviceList = mutableListOf<Advice>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        initializeManager()
        val advice = Advice(0,"",0)
        adviceList.add(advice)
    }



    private fun initializeManager() {
        cardStackView = binding.cardStackView
        manager = CardStackLayoutManager(this, this)
        adapter = CardStackAdapter(adviceList, this)
        manager.setStackFrom(StackFrom.None)
        manager.setVisibleCount(1)
        manager.setTranslationInterval(8.0f)
        manager.setScaleInterval(0.95f)
        manager.setSwipeThreshold(0.3f)
        manager.setMaxDegree(85.0f)
        manager.setDirections(Direction.FREEDOM)
        manager.setCanScrollHorizontal(true)
        manager.setCanScrollVertical(true)
        manager.setSwipeableMethod(SwipeableMethod.Manual)
        manager.setOverlayInterpolator(LinearInterpolator())

        cardStackView.layoutManager = manager
        cardStackView.adapter = adapter
        cardStackView.itemAnimator.apply {
            if (this is DefaultItemAnimator) {
                supportsChangeAnimations = false
            }
        }
    }

    override fun onCardDragging(direction: Direction, ratio: Float) {
        Log.d("CardStackView", "onCardDragging: d = ${direction.name}, r = $ratio")
    }

    override fun onCardSwiped(direction: Direction) {
        val cardSwipedAdvice = adviceList[manager.topPosition]
        when (direction) {
            Direction.Right -> {
                Log.d("something", cardSwipedAdvice.adText)
                shareAdvice(advice = cardSwipedAdvice)
                Toast.makeText(this,"share",Toast.LENGTH_SHORT).show()
            }
            Direction.Left -> {
                Log.d("something", cardSwipedAdvice.adText)
                Toast.makeText(this,"gone",Toast.LENGTH_SHORT).show()

                // good enough
            }
            Direction.Top -> {
                Log.d("something", cardSwipedAdvice.adText)
            }
            Direction.Bottom -> {
                cardStackView.rewind()
                Log.d("something", cardSwipedAdvice.adText)
            }
        }

    }



    private fun shareAdvice (advice: Advice){
        val i = Intent(Intent.ACTION_SEND)
        i.type = "text/plain"
        i.putExtra(Intent.EXTRA_TEXT, advice.adText)
        startActivity(i)
    }

    override fun onCardRewound() {
        Log.d("CardStackView", "onCardRewound: ${manager.topPosition}")
    }

    override fun onCardCanceled() {
        Log.d("CardStackView", "onCardCanceled: ${manager.topPosition}")
    }

    override fun onCardAppeared(view: View, position: Int) {
        Log.d("CardStackView", "onCardCanceled: ${manager.topPosition}")

    }

    override fun onCardDisappeared(view: View, position: Int) {
        Log.d("CardStackView", "onCardCanceled: ${manager.topPosition}")
    }

    override fun loadDataFromOnlineAndSet(holder: CardStackAdapter.ViewHolder, position: Int) {
        holder.binding.tvAdvice.text = ""
        holder.binding.tvAdviceNo.text = ""

        val size = adviceList.size

        holder.binding.adviceProgressBar.visibility = View.VISIBLE
        manager.setCanScrollHorizontal(false)
        manager.setCanScrollVertical(false)
        val url = "https://api.adviceslip.com/advice"
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                val res = response.getJSONObject("slip")
                val idInt = res.getInt("id")
                val adviceString = res.getString("advice")

                holder.binding.adviceProgressBar.visibility = View.GONE
                holder.binding.tvAdvice.text = adviceString
                holder.binding.tvAdviceNo.text = "Advice #$idInt"

                adviceList[size - 1].id = idInt
                adviceList[size - 1].adText = adviceString
                adapter.notifyItemChanged(size - 1)

                manager.setCanScrollHorizontal(true)
                manager.setCanScrollVertical(true)



                Toast.makeText(this, "" + size, Toast.LENGTH_SHORT).show()
                adviceList.add(Advice(0, "", 0))

            },
            { error ->
                holder.binding.tvAdvice.text = "Some error occurred"
                holder.binding.adviceProgressBar.visibility = View.GONE
                Log.d(ContentValues.TAG, "Lol eeror")
            }
        )
        VolleySingletonRequestQueue.getInstance(context = this)
            .addToRequestQueue(jsonObjectRequest)
    }


}