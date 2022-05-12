package com.example.espotifai

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.icu.text.Transliterator
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.BaseAdapter
import android.widget.SeekBar
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationBuilderWithBuilderAccessor
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_player.*
import kotlinx.android.synthetic.main.item_song.view.*
import java.lang.Exception


class MainActivity : AppCompatActivity(), MainAux {

    var songs = ArrayList<SongInfo>()
    var mp : MediaPlayer? = null
    var adapter : MySongAdapter? = null
    var position : Int? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()//oculta la barra de titulo
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bottomAppBar.visibility = View.INVISIBLE //el mini reproductor empieza oculto

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            loadSongs()
        }else{
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),111)
        }

        tvCount.text = songs.size.toString() + " songs"
        recyclerView2.setOnItemClickListener{parent, view, pos, id ->

            playSong(pos)

            bottomAppBar.visibility = View.VISIBLE //Se hace visible el bottom app bar y se llenan los datos
            setDataMini(pos)
        }

        btnPlay.setOnClickListener{
            pauseOrUnpause(mp)
        }

        //Se abre el Player grande cuando das click
        bottomAppBar.setOnClickListener{
            val fragment = PlayerFragment()
            val fragmentManager = supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.add(R.id.containerMain, fragment)
            fragmentTransaction.commit()

        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: kotlin.Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 111 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            loadSongs()
    }

    inner class MySongAdapter : BaseAdapter{

        var mySongList = ArrayList<SongInfo>()

        constructor(mySongList: ArrayList<SongInfo>) : super() {
            this.mySongList = mySongList
        }

        override fun getCount(): Int {
            return mySongList.size
        }

        override fun getItem(p0: Int): Any {
            return mySongList[p0]
        }

        override fun getItemId(p0: Int): Long {
            return p0.toLong()
        }

        override fun getView(position: Int, p1: View?, p2: ViewGroup?): View {
            var myView = layoutInflater.inflate(R.layout.item_song, null)
            var song = mySongList[position]
            myView.tvTitle.text = song.title
            myView.tvAuthor.text = song.author
            if(song.coverUrl == null){

            }else{
                var bitmap  = BitmapFactory.decodeByteArray(song.coverUrl,0, song.coverUrl!!.size)
                myView.IvCover.setImageBitmap(bitmap)
            }
            return myView
        }

    }

    @SuppressLint("Range")
    private fun loadSongs() {
        var uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        var selection = MediaStore.Audio.Media.IS_MUSIC + "!=0"
        var rs = contentResolver.query(uri, null, selection, null, null)
        var meta = MediaMetadataRetriever()
        if(rs != null){
            while(rs!!.moveToNext()){
                var url = rs!!.getString(rs!!.getColumnIndex(MediaStore.Audio.Media.DATA))
                var author = rs!!.getString(rs!!.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                var title = rs!!.getString(rs!!.getColumnIndex(MediaStore.Audio.Media.TITLE))
                meta.setDataSource(url)
                var cover = meta.embeddedPicture
                songs.add(SongInfo(title,author,url,cover))
            }
        }
        adapter= MySongAdapter(songs)
        recyclerView2.adapter = adapter

    }


    override fun pauseOrUnpause(mp: MediaPlayer?) {
        if(mp?.isPlaying == true) {
            mp?.pause()
            btnPlay.setImageResource(R.drawable.ic_play)
        }else{
            mp?.start()
            btnPlay.setImageResource(R.drawable.ic_pause)
        }
    }

    override fun playSong(pos: Int) {
        position = pos
        if(mp?.isPlaying == true) {
            mp?.stop()
            btnPlay.setImageResource(R.drawable.ic_play)
        }
        mp = MediaPlayer()
        try{
            mp!!.setDataSource(songs[pos].songUrl)
            mp!!.prepare()
            mp!!.start()
            btnPlay.setImageResource(R.drawable.ic_pause)


        }catch (e: Exception){
        }
    }

    override fun setDataMini(pos: Int) {
        var bitmap  = BitmapFactory.decodeByteArray(songs[pos].coverUrl,0, songs[pos].coverUrl!!.size)
        ivCoverMini.setImageBitmap(bitmap)
        tvAuthorMini.setText(songs[pos].author)
        tvTitleMini.setText(songs[pos].title)
    }

}
