package com.example.mymusicplayer

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private var mediaPlayer: MediaPlayer? = null
    private lateinit var playButton: Button
    private lateinit var nextButton: Button
    private lateinit var previousButton: Button
    private lateinit var seekBar: SeekBar
    private lateinit var songTitle: TextView
    private lateinit var currentTime: TextView
    private lateinit var totalTime: TextView

    private var songIndex = 0
    private val handler = Handler(Looper.getMainLooper())

    private val songs = listOf(
        R.raw.relax_beat,
        R.raw.spirit_in_the_woods,
        R.raw.valley_sunset,
        R.raw.daredevil,
        R.raw.my_mind_goes_wah_wah,
        R.raw.awake,
        R.raw.perfect_cell_theme,
        R.raw.nightmare_king,
        R.raw.l_no_theme
    )

    private val songNames = listOf(
        "Relax Beat",
        "Spirit in the Woods",
        "Valley Sunset",
        "Daredevil",
        "My Mind Goes Wah Wah",
        "Awake",
        "Perfect Cell Theme",
        "Nightmare King",
        "L no Theme"
    )

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        playButton = findViewById(R.id.playButton)
        nextButton = findViewById(R.id.nextButton)
        previousButton = findViewById(R.id.previousButton)
        seekBar = findViewById(R.id.seekBar)
        songTitle = findViewById(R.id.songTitle)
        currentTime = findViewById(R.id.currentTime)
        totalTime = findViewById(R.id.totalTime)

        initializePlayer()

        playButton.setOnClickListener {
            mediaPlayer?.let { mp ->
                if (!mp.isPlaying) {
                    mp.start()
                    playButton.text = "Pause"
                    updateSeekBar()
                } else {
                    mp.pause()
                    playButton.text = "Play"
                }
            }
        }

        nextButton.setOnClickListener {
            changeSong(1)
        }

        previousButton.setOnClickListener {
            changeSong(-1)
        }

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer?.seekTo(progress)
                    currentTime.text = formatTime(progress)
                }
            }

            override fun onStartTrackingTouch(sb: SeekBar?) {}
            override fun onStopTrackingTouch(sb: SeekBar?) {}
        })

        Toast.makeText(this, "onCreate Called", Toast.LENGTH_SHORT).show()
    }

    private fun initializePlayer() {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer.create(this, songs[songIndex])

        songTitle.text = songNames[songIndex]
        seekBar.progress = 0

        mediaPlayer?.let { mp ->
            seekBar.max = mp.duration
            totalTime.text = formatTime(mp.duration)
        }

        playButton.text = "Play"

        mediaPlayer?.setOnCompletionListener {
            changeSong(1)
        }
    }

    private fun changeSong(direction: Int) {
        songIndex = (songIndex + direction + songs.size) % songs.size
        initializePlayer()
        mediaPlayer?.start()
        playButton.text = "Pause"
        updateSeekBar()
    }

    private fun updateSeekBar() {
        mediaPlayer?.let { mp ->
            seekBar.progress = mp.currentPosition
            currentTime.text = formatTime(mp.currentPosition)

            if (mp.isPlaying) {
                handler.postDelayed({ updateSeekBar() }, 500)
            }
        }
    }

    private fun formatTime(ms: Int): String {
        val minutes = ms / 1000 / 60
        val seconds = (ms / 1000) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    override fun onPause() {
        super.onPause()
        mediaPlayer?.pause()
        playButton.text = "Play"
    }

    override fun onStop() {
        super.onStop()
        mediaPlayer?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
        handler.removeCallbacksAndMessages(null)
    }
}
