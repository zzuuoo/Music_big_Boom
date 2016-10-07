package com.yezi.zuo.music_big_boom;

import android.media.MediaPlayer;
import android.widget.SeekBar;

/**
 * Created by zuo on 2016/10/3.
 */
public class ProgressBarChange implements SeekBar.OnSeekBarChangeListener {

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if(fromUser){
            MediaService.mediaPlayer.seekTo(progress);
            //调用seekTo方法，音乐播放器从新位置progress播放
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

        MediaService.mediaPlayer.pause();
        //开始拖动进度条，让它停止播放；
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {


        MediaService.mediaPlayer.start();
        //停止拖动进度条，音乐播放
    }

}
