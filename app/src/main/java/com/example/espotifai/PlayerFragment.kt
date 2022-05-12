package com.example.espotifai

import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import com.example.espotifai.databinding.FragmentPlayerBinding
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_player.*
import kotlinx.android.synthetic.main.item_song.view.*
import java.lang.Exception
import kotlin.random.Random

class PlayerFragment : Fragment() {

    private lateinit var mbinding: FragmentPlayerBinding
    private var mActivity:MainActivity? = null
    private lateinit var  songs : ArrayList<SongInfo>
    private var mp : MediaPlayer? = null
    private var position : Int? = null

    lateinit var runnable: Runnable
    private var handler = Handler()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mbinding = FragmentPlayerBinding.inflate(inflater, container, false)
        return mbinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mActivity = activity as? MainActivity
        songs = mActivity?.songs!!
        position = mActivity?.position
        mp = mActivity?.mp
        //cambia el botón de play/pause según el estado de la canción
        if(mp?.isPlaying == true){
            changePlayButton(true)
        }else{
            changePlayButton(false)
        }


        //reemplaza los datos génericos con los metadatos de la canción
        setData(position, songs)

        //play-pause
        btnPlayGrande.setOnClickListener{
            mActivity?.pauseOrUnpause(mp)
            if(mp?.isPlaying == true){
               changePlayButton(true)
            }else{
                changePlayButton(false)
            }

        }

        //cancion siguiente
        btnNext.setOnClickListener{
            playNext()
        }

        //cancion anterior
        btnPrevious.setOnClickListener{
            position = position!! - 1
            if(position!! < 0) {
                position = songs!!.size - 1
            }
            mActivity?.playSong(position!!)
            //actualiza el mp según el mp del mainActivity
            mp = mActivity?.mp
            setData(position, songs)
        }

        //permite avanzar cancion con el seekbar
        seekbar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, pos: Int, changed: Boolean) {
                if(changed){
                    mp!!.seekTo(pos)
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }
        })

        runnable = Runnable {
            seekbar.progress = mp!!.currentPosition
            handler.postDelayed(runnable, 1000)
        }
        handler.postDelayed(runnable, 1000)
        mp!!.setOnCompletionListener {
            seekbar.progress = 0
        }


        mp!!.setOnCompletionListener{
            playNext()
        }



    }

    fun getRandom(max : Int) {
        var next = Random.nextInt(0, max)
    }

    fun changePlayButton(isPlaying:Boolean){
        if (isPlaying){
            btnPlayGrande.setImageResource(R.drawable.ic_pause_circle)
        }else{
            btnPlayGrande.setImageResource(R.drawable.ic_play_circle)
        }
    }

    fun playNext(){
        position = position!! + 1
        if(position!! >= songs!!.size) {
            position = 0
        }
        mActivity?.playSong(position!!)
        //actualiza el mp según el mp del mainActivity
        mp = mActivity?.mp
        setData(position, songs)
    }

    fun setData(position: Int?, songs : ArrayList<SongInfo>?){
        tvTitleGrande.text = position?.let { songs?.get(it)?.title }
        tvAuthorGrande.text = position?.let { songs?.get(it)?.author }
        var array = position?.let { songs?.get(it)?.coverUrl }
        var size = position?.let { songs?.get(it)?.coverUrl }?.size
        var bitmap = size?.let { BitmapFactory.decodeByteArray(array ,0, it) }
        IvCoverGrande.setImageBitmap(bitmap)

        seekbar.progress = 0
        seekbar.max = mActivity!!.mp!!.duration

        //actualiza los datos en el miniplayer
        mActivity?.setDataMini(position!!)
    }


}