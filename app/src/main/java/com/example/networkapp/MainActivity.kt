package com.example.networkapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.squareup.picasso.Picasso
import org.json.JSONObject

// TODO (1: Fix any bugs)
// TODO (2: Add function saveComic(...) to save comic info when downloaded
// TODO (3: Automatically load previously saved comic when app starts)

class MainActivity : AppCompatActivity() {

    private lateinit var requestQueue: RequestQueue
    lateinit var titleTextView: TextView
    lateinit var descriptionTextView: TextView
    lateinit var numberEditText: EditText
    lateinit var showButton: Button
    lateinit var comicImageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestQueue = Volley.newRequestQueue(this)

        titleTextView = findViewById(R.id.comicTitleTextView)
        descriptionTextView = findViewById(R.id.comicDescriptionTextView)
        numberEditText = findViewById(R.id.comicNumberEditText)
        showButton = findViewById(R.id.showComicButton)
        comicImageView = findViewById(R.id.comicImageView)

        showButton.setOnClickListener {
            downloadComic(numberEditText.text.toString())
        }

        loadSavedComic()
    }

    // Fetches comic from web as JSONObject
    private fun downloadComic(comicId: String) {
        val url = "https://xkcd.com/$comicId/info.0.json"
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response -> showComic(response) },
            { error ->
                Toast.makeText(this, "Error fetching comic: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        )
        requestQueue.add(jsonObjectRequest)
    }

    // Display a comic for a given comic JSON object
    private fun showComic(comicObject: JSONObject) {
        titleTextView.text = comicObject.getString("title")
        descriptionTextView.text = comicObject.getString("alt")
        Picasso.get().load(comicObject.getString("img")).into(comicImageView)
        saveComic(comicObject)
    }


    // Implement this function
    private fun saveComic(comicObject: JSONObject) {
        val sharedPreferences = getSharedPreferences("comics", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("saved_comic", comicObject.toString())
        editor.apply()
        Toast.makeText(this, "Comic saved!", Toast.LENGTH_SHORT).show()
    }

    private fun loadSavedComic() {
        val sharedPreferences = getSharedPreferences("comics", MODE_PRIVATE)
        val savedComicString = sharedPreferences.getString("saved_comic", null)

        if (savedComicString != null) {
            val savedComic = JSONObject(savedComicString)
            showComic(savedComic)
        } else {
            Toast.makeText(this, "No saved comic found!", Toast.LENGTH_SHORT).show()
        }
    }


}