package com.example.englishquiz

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ResultActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val score = intent.getIntExtra("score", 0)
        findViewById<TextView>(R.id.tv_score).text = "Your Score: $score"

        findViewById<Button>(R.id.btn_retry).setOnClickListener {
            startActivity(Intent(this, QuizActivity::class.java))
            finish()
        }
    }
}
