package ma.ensa.projet

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity


class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash_screen)
        val t1: Thread = object : Thread() {
            override fun run() {
                try {
                    sleep(7000)
                    val intent: Intent = Intent(
                        this@SplashScreen,
                        MainActivity::class.java
                    )
                    startActivity(intent)
                    this@SplashScreen.finish()
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }
        t1.start()
    }
}