package com.example.memesshare

import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Request.Method.GET
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kotlinx.android.synthetic.main.activity_main.*

@ExperimentalStdlibApi
class MainActivity : AppCompatActivity() {
    var currentImageUrl:String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        loadMeme()
    }

    private fun loadMeme(){
        nextBtn.isEnabled=false
        shareBtn.isEnabled=false
        progressBar.visibility= View.VISIBLE

        /*The below code shows how to use the convenience method Volley.newRequestQueue (commented below) to
        set up a RequestQueue, taking advantage of Volley’s default behaviors.*/
        // Instantiate the RequestQueue.
//        val queue = Volley.newRequestQueue(this)
//        val url = "https://www.google.com"
        val url="https://meme-api.herokuapp.com/gimme"
// Request a string response from the provided URL.
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET,url,null,
            { response ->
                currentImageUrl=response.getString("url")
                Glide.with(this).load(currentImageUrl).listener(object: RequestListener<Drawable>{
                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        progressBar.visibility=View.GONE
                        nextBtn.isEnabled=true
                        shareBtn.isEnabled=true
                        return false
                    }

                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        Toast.makeText(this@MainActivity, e?.message, Toast.LENGTH_SHORT).show()
                        progressBar.visibility=View.GONE
                        nextBtn.isEnabled=true
                        return false
                    }
                }).into(memeImage)
            },
            {
                Toast.makeText(this,"Something went wrong!",Toast.LENGTH_SHORT).show()
            })

//         Add the request to the RequestQueue.
//        queue.add(jsonObjectRequest)

    /*
        Lets walk through the explicit steps of creating a RequestQueue,
        to allow you to supply your own custom behavior.
        This lesson also describes the recommended practice of creating a RequestQueue as a singleton,
        which makes the RequestQueue last the lifetime of your app.
    */
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest)

    }

    fun nextMemeBtn(view: android.view.View) {
        loadMeme()
    }
    fun shareMemeBtn(view: android.view.View) {
        val intent=Intent(Intent.ACTION_SEND)
        intent.type="text/plain"
        intent.putExtra(Intent.EXTRA_TEXT,"Hey, checkout this meme from reddit! $currentImageUrl")
        val chooser=Intent.createChooser(intent,"Share this meme using...")
        startActivity(chooser)
    }
}