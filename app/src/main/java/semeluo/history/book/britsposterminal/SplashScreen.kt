package semeluo.history.book.britsposterminal

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class SplashScreen : AppCompatActivity() {

    private lateinit var splashLogo: ImageView
    private lateinit var splashText: TextView
    private lateinit var loadingIndicator: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        splashLogo = findViewById(R.id.splash_logo)
        splashText = findViewById(R.id.splash_text)
        loadingIndicator = findViewById(R.id.loadingIndicator)

        // Set the background color using the gradient (if it's not being applied)
        window.decorView.setBackgroundColor(ContextCompat.getColor(this, R.color.transparent)) // Apply gradient if necessary

        // Start loading indicator
        loadingIndicator.visibility = View.VISIBLE

        // Fade-in animation for logo
        val fadeInLogo = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        splashLogo.startAnimation(fadeInLogo)

        // Fade-in animation for text
        val fadeInText = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        splashText.startAnimation(fadeInText)

        // Set a delay to simulate loading and transition to the main activity
        Handler(Looper.getMainLooper()).postDelayed({
            // Hide loading indicator after 3 seconds
            loadingIndicator.visibility = View.GONE

            // Transition to the main activity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, 3000) // 3 seconds delay for splash screen
    }
}
