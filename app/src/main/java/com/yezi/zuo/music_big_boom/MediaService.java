package com.yezi.zuo.music_big_boom;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.Nullable;

import static com.yezi.zuo.music_big_boom.MediaData.*;

import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zuo on 2016/10/2.
 */
public class MediaService extends Service {

    private static MediaPlayer mediaPlayer =null;
    private static int position=0;//歌曲位置
    private static int mode=0;//播放模式,模式0，列表循环；1，随机；2，单曲；

    //播放状态
    private final static int PLAYING=1;
    private final static int STOPPING=0;
    private static int flag;

    //广播
    private IntentFilter  intentfilter_service;
    private ServiceReceive serviceReceive;
    private LocalBroadcastManager localBroadcastManager_service;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        readMedia();//读数据歌曲
        localBroadcastManager_service = LocalBroadcastManager.getInstance(this);//获取实例
        initMediaplay();//初始化mediaPlayer
        completionListener();//监听是否播完
        nitification();//前台服务

    }

    private void initMediaplay(){
        if(mediaPlayer != null){
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer=null;
        }else{
            mediaPlayer = new MediaPlayer();
            flag=1;
        }
    }//初始化mediaPlayer


    private void nitification(){//前台服务
        Media media = mediaList.get(position);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(MediaService.this);
        builder.setSmallIcon(R.drawable.hh);
        builder.setContentTitle("MusicGoGoGo");
        builder.setContentText(media.getDisplay_name());
        Intent intent =new Intent(MediaService.this,MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(MediaService.this,0,intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        Notification notification = builder.build();
        startForeground(1,notification);
    }//前台服务

    private void completionListener(){
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener(){
            @Override
            public void onCompletion(MediaPlayer mp) {
                if(mode==0){
                    if (position== mediaList.size()-1){
                        position=0;

                    }else{
                        position++;

                    }
                }else if(mode==1)
                {
                    position=(int)(Math.random()* mediaList.size());

                }
                nitification();
                playMusic();
            }
        });


    }//监听是否播完


    private void readMedia() {//读取手机mp3文件
        Cursor cursor = null;
        cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[]{
                MediaStore.Audio.Media.TITLE,//歌曲名
                MediaStore.Audio.Media.DURATION,//时间
                MediaStore.Audio.Media.ALBUM,//专辑名
                MediaStore.Audio.Media.ARTIST,//歌手
                MediaStore.Audio.Media._ID,//歌曲ID
                MediaStore.Audio.Media.DATA,//路径
                MediaStore.Audio.Media.DISPLAY_NAME//显示全部名字
        }, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String name = cursor.getString(0);
                long time = cursor.getLong(1);
                String Album = cursor.getString(2);
                String Artist = cursor.getString(3);
                long id = cursor.getLong(4);
                String data = cursor.getString(5);
                String diaplay_name = cursor.getString(6);
                Media m = new Media();
                m.setAlbum(Album);
                m.setArtist(Artist);
                m.setData(data);
                m.setId(id);
                m.setName(name);
                m.setTime(time);
                m.setDisplay_name(diaplay_name);
                mediaList.add(m);

            } while (cursor.moveToNext());
        }
    }//读取手机音频

    @Override
    public void onDestroy(){
        super.onDestroy();
        if(mediaPlayer!=null){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer=null;
        }
    }

    @Override
    public int onStartCommand(Intent intent,int flags,int startId){
        intentfilter_service = new IntentFilter();
        intentfilter_service.addAction("to.Service.for.play.funtion");
        serviceReceive = new ServiceReceive();
        localBroadcastManager_service.registerReceiver(serviceReceive, intentfilter_service);
        return super.onStartCommand(intent,flags,startId);
    }


    public void playMusic(){
        Media media = mediaList.get(position);
        flag=0;
        nitification();
        try{
            mediaPlayer.reset();
            mediaPlayer.setDataSource(media.getData());
            mediaPlayer.prepare();
            mediaPlayer.start();
            mediaPlayer.setLooping(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        sendButton();
    }

    public void sendButton(){
        Media media = mediaList.get(position);
        Intent intent = new Intent("to.Activity.for.Button.change");
        intent.putExtra("position",position);
        intent.putExtra("mode",mode);
        intent.putExtra("playName",media.getDisplay_name());
        if(mediaPlayer.isPlaying()){
            intent.putExtra("state",PLAYING);
        }else{
            intent.putExtra("state",STOPPING);
        }

        localBroadcastManager_service.sendBroadcast(intent);
    }


    public class ServiceReceive extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            mode = intent.getIntExtra("mode",0);
            position = intent.getIntExtra("position",0);
            int funtion = intent.getIntExtra("funtion",0);
            switch (funtion){
                case 0:
                    if(flag!=1){
                        if(mediaPlayer.isPlaying()){
                            mediaPlayer.pause();

                        }else{
                            mediaPlayer.start();
                        }
                    }else{
                        playMusic();
                    }
                    break;
                case 1:
                case -1:
                case 3:
                    playMusic();
                    break;
                default:
                    break;

            }
            sendButton();
        }
    }

}
