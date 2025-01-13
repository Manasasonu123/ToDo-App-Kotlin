package com.example.todoapp



import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.todoapp.R.id.img_view


class SplashActivity : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        supportActionBar?.hide()
//        val imageView: ImageView = findViewById(R.id.img_view)
//
//// Set the pivot point to the center of the ImageView
//        imageView.post {
//            imageView.pivotX = imageView.width / 2f
//            imageView.pivotY = imageView.height / 2f
//
//            // Apply the animation
//            val popupAnimation = AnimationUtils.loadAnimation(this, R.anim.popup_anim)
//            imageView.startAnimation(popupAnimation)
//        }

        val intent = Intent(this, MainActivity::class.java)
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(intent)
            finish()
        }, 2000)
    }
}
