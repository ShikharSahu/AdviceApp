package com.example.adviceapp

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
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import layout.VolleySingletonRequestQueue
import android.view.Menu
import android.view.MenuItem


class MainActivity : AppCompatActivity(), CardStackListener{

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
        adapter = CardStackAdapter(adviceList)
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
        val cardSwipedAdvice = adviceList[manager.topPosition-1]
        cardSwipedAdvice.timeSavedAt = System.currentTimeMillis()

        when (direction) {
            Direction.Right -> {
                Log.d("something", cardSwipedAdvice.adText)
                val db = DBHelper(this, null)
                db.addAdvice(cardSwipedAdvice)
                Toast.makeText(this,"Saved!",Toast.LENGTH_SHORT).show()

            }
            Direction.Left -> {
                Log.d("something", cardSwipedAdvice.adText)
//                Toast.makeText(this,"gone",Toast.LENGTH_SHORT).show()
            }
            Direction.Top -> {
                Log.d("something", cardSwipedAdvice.adText)
                shareAdvice(advice = cardSwipedAdvice)
//                Toast.makeText(this,"share",Toast.LENGTH_SHORT).show()
            }
            Direction.Bottom -> {
//                Log.d("something", cardSwipedAdvice.adText)
            }
        }

    }



    private fun shareAdvice (advice: Advice){
        val i = Intent(Intent.ACTION_SEND)
        i.type = "text/plain"
        i.putExtra(Intent.EXTRA_TEXT, "Here's some advice I read online. " +
                "Thought you'd like it too. \"${advice.adText}\"")
        startActivity(i)
    }

    override fun onCardRewound() {
        Log.d("CardStackView", "onCardRewound: ${manager.topPosition}")
    }

    override fun onCardCanceled() {
        Log.d("CardStackView", "onCardCanceled: ${manager.topPosition}")
    }

    override fun onCardAppeared(view: View, position: Int) {

        val tvAdvice = view.findViewById<TextView>(R.id.tvAdvice)
        val tvAdviceNo = findViewById<TextView>(R.id.tvAdviceNo)
        val adviceProgressBar = findViewById<ProgressBar>(R.id.adviceProgressBar)

        tvAdvice.text = ""
        tvAdviceNo.text =""

        val size = adviceList.size

        adviceProgressBar.visibility = View.VISIBLE
        adviceList.add(Advice(0, "", 0))

        val url = "https://api.adviceslip.com/advice"
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                val res = response.getJSONObject("slip")
                val idInt = res.getInt("id")
                val adviceString = res.getString("advice")

                adviceProgressBar.visibility = View.GONE
                tvAdvice.text = adviceString
                tvAdviceNo.text = "Advice #$idInt"

                adviceList[size - 1].id = idInt
                adviceList[size - 1].adText = adviceString
                adapter.notifyItemChanged(size - 1)

//                Toast.makeText(this, "" + size, Toast.LENGTH_SHORT).show()

            },
            { error ->
                tvAdvice.text = "Some error occurred"
                adviceProgressBar.visibility = View.GONE
                Log.d(TAG, "Lol eeror")
            }
        )
        VolleySingletonRequestQueue.getInstance(context = this)
            .addToRequestQueue(jsonObjectRequest)

    }

    override fun onCardDisappeared(view: View, position: Int) {
        val tvAdvice = view.findViewById<TextView>(R.id.tvAdvice)
        val tvAdviceNo = findViewById<TextView>(R.id.tvAdviceNo)

        tvAdvice.text = ""
        tvAdviceNo.text =""

        Log.d("CardStackView", "onCardCanceled: ${manager.topPosition}")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.navigation_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id: Int = item.getItemId()
        return when (id) {
            R.id.savedAdviceMenuButton -> {
//                Toast.makeText(applicationContext, "Item 1 Selected", Toast.LENGTH_LONG).show()
                val intent = Intent(this, SavedAdviceActivity::class.java)
                startActivity(intent)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

}