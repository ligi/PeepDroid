package org.ligi.peepdroid

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import org.ligi.peepdroid.model.Peep

class PeepViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

}

class PeepAdapter(val list: List<Peep>) : RecyclerView.Adapter<PeepViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PeepViewHolder {
        val li = LayoutInflater.from(parent.context)
        val foo = li.inflate(R.layout.peep, parent, false)
        return PeepViewHolder(foo)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: PeepViewHolder, position: Int) {
        (holder.itemView as TextView).text = list[position].content
    }

}

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        peep_recycler.layoutManager = LinearLayoutManager(this)

        launch {
            val request = Request.Builder().url("https://peepeth.com/get_peeps").build()
            val response = OkHttpClient.Builder().build().newCall(request).execute()
            response.body()?.string()?.let {
                async(UI) {
                    peep_recycler.adapter = PeepAdapter(parsePeeps(it))
                }
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return super.onCreateOptionsMenu(menu)
    }
}
