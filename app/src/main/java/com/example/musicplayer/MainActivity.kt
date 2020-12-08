package com.example.musicplayer

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.SeekBar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() ,ServiceConnection{

    val TAG = "MainActivity"
    var binder:MusicService.MusicBinder?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                0
            )
        } else {
            startMusicService()
        }



        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                   binder?.currentPosition= progress
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })






    }

    fun startMusicService(){
        val intent=Intent(this,MusicService::class.java)
        intent.putExtra(MusicService.Commond,1)
        startService(intent)
        bindService(intent,this,Context.BIND_AUTO_CREATE)
    }

    fun onPlay(v: View) {
        val intent=Intent(this,MusicService::class.java)
        intent.putExtra(MusicService.Commond,1)
        startService(intent)
    }

    fun onPause(v: View) {
        val intent=Intent(this,MusicService::class.java)
        intent.putExtra(MusicService.Commond,2)
        startService(intent)
    }

    fun onStop(v: View) {
        val intent=Intent(this,MusicService::class.java)
        intent.putExtra(MusicService.Commond,3)
        startService(intent)
    }

    fun onNext(v: View) {
        val intent=Intent(this,MusicService::class.java)
        intent.putExtra(MusicService.Commond,4)
        startService(intent)
    }

    fun onPrev(v: View) {
        val intent=Intent(this,MusicService::class.java)
        intent.putExtra(MusicService.Commond,5)
        startService(intent)
    }



    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

    }



    override fun onDestroy() {
        super.onDestroy()
        unbindService(this)

    }

    override fun onServiceDisconnected(name: ComponentName?) {
        binder = null
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
       binder = service as MusicService.MusicBinder
        thread {
            while (binder !=null) {
                Thread.sleep(500)
                runOnUiThread {
                    seekBar.max = binder!!.duration
                    seekBar.progress = binder!!.currentPosition
                    textView_name.text=binder!!.musicName
                    textView_count.text="${binder!!.currentIndex+1}/${binder?.size}"
                }
            }
        }
    }
}