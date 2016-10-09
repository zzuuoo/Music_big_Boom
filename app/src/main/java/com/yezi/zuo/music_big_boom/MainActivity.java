package com.yezi.zuo.music_big_boom;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Method;
import static com.yezi.zuo.music_big_boom.MediaData.*;

public class MainActivity extends Activity implements View.OnClickListener,ActivityCompat.OnRequestPermissionsResultCallback{

    public static int position=0;
    public static int mode=0;//模式0，列表循环；1，随机；2，单曲；

    //-1,上一首，2，下一首，0；暂停/播放
    private final static int START=0;
    private final static int NEXT=1;
    private final static int PREVIOUS =-1;
    private final static int CHOICE=2;

    public static SeekBar progressBar;

    //发广播专用
    private ActivityReceive localReceive_activity;
    private LocalBroadcastManager localBroadcastManager_activity;//广播管理
    private IntentFilter intentFilter_activity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//去标题栏
        setContentView(R.layout.activity_main);
        Thread.setDefaultUncaughtExceptionHandler(new CrashHandler());

        ActivityCollector.addActivity(this);

        applyPermission();//获取读音乐权限


        localBroadcastManager_activity = LocalBroadcastManager.getInstance(this);//获取实例
        init();

        //ListView布局
        MediaAdapter adapter = new MediaAdapter(MainActivity.this,R.layout.media_item,mediaList);
        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new ItemClickEvent());

        //注册广播
        intentFilter_activity = new IntentFilter();
        intentFilter_activity.addAction("to.Activity.for.Button.change");
        localReceive_activity = new ActivityReceive();
        localBroadcastManager_activity.registerReceiver(localReceive_activity, intentFilter_activity);
    }


    public void applyPermission(){

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //申请权限
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }else{
            Intent intent =new Intent();
            intent.setClass(MainActivity.this,MediaService.class);
            startService(intent);

        }

    }//申请权限


    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[],int [] grantResut){

        switch (requestCode){
            case 1:
                if(grantResut.length>0&&grantResut[0]== PackageManager.PERMISSION_GRANTED){

                    Intent intent =new Intent();
                    intent.setClass(MainActivity.this,MediaService.class);
                    startService(intent);

                }else{
                    Toast.makeText(MainActivity.this,"你禁止了这个权限",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    private final class ItemClickEvent implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            MainActivity.position=position;
            Media media = new Media();
            media = mediaList.get(position);
            TextView setTv = (TextView)findViewById(R.id.textView);;
            setTv.setText(media.getDisplay_name());
            ImageButton play = (ImageButton) findViewById(R.id.stop);
            play.setImageResource(R.drawable.sp);
            sendFuntion(3);

        }
    }//listView监听

    private void init(){//初始化按钮
        ImageButton play = (ImageButton) findViewById(R.id.stop);
        ImageButton last = (ImageButton) findViewById(R.id.previous);
        ImageButton next = (ImageButton) findViewById(R.id.next);
        ImageButton ro = (ImageButton)findViewById(R.id.around);
        TextView tv = (TextView)findViewById(R.id.textView);


        if(MediaService.mediaPlayer!=null){
            position=MediaService.position;
            mode=MediaService.mode;
            Media media = mediaList.get(position);
            tv.setText(media.getDisplay_name());
            switch (mode){
                case 0:
                    ro.setImageResource(R.drawable.orr);
                    break;
                case 1:
                    ro.setImageResource(R.drawable.rand);
                    break;
                case 2:
                    ro.setImageResource(R.drawable.single);
                    break;
                default:
                    break;
            }
            if(MediaService.mediaPlayer.isPlaying()){

                play.setImageResource(R.drawable.sp);
            }else{
                play.setImageResource(R.drawable.st);
            }

//            sendFuntion(0);


        }
        progressBar = (SeekBar) findViewById(R.id.seekbar);

        /*进度条监听*/
        progressBar.setOnSeekBarChangeListener(new ProgressBarChange());

        if (MediaService.mediaPlayer!=null){
            progressBar.setMax(MediaService.mediaPlayer.getDuration());
            progressBar.setProgress(MediaService.mediaPlayer.getCurrentPosition());
            position=MediaService.position;
            mode=MediaService.mode;
            Media media = mediaList.get(position);
            tv.setText(media.getDisplay_name());
        }

        tv.setOnClickListener(this);
        play.setOnClickListener(this);
        last.setOnClickListener(this);
        next.setOnClickListener(this);
        ro.setOnClickListener(this);
    }//初始化按钮

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.previous:
                previousMusic(v);//按钮颜色变化
                sendFuntion(PREVIOUS);
                break;
            case R.id.next:
                nextMusic(v);//按钮颜色变化
                sendFuntion(NEXT);
                break;
            case R.id.stop:
                sendFuntion(START);
                break;
            case R.id.around:
                around();
                sendFuntion(CHOICE);
                break;
            case R.id.textView:
                StartPlaying();
                break;
            default:
                break;

        }
    }//按钮监听

    public void StartPlaying(){
        Intent intent = new Intent(MainActivity.this,MusicPlaying.class);
        Media media = mediaList.get(position);
        intent.putExtra("playName",media.getDisplay_name());
        startActivity(intent);
    }

    public void around() {
        ImageButton play = (ImageButton) findViewById(R.id.around);
        if (mode == 0) {
            play.setImageResource(R.drawable.rand);
            mode = 1;//随机
        } else if (mode == 1) {
            mode = 2;//单曲
            play.setImageResource(R.drawable.single);
        } else {
            mode = 0;//顺序
            play.setImageResource(R.drawable.orr);
        }
    }

    public void nextMusic(View v){

        if(mode==0) {
            if (position == mediaList.size() - 1) {
                position = 0;

            } else {
                position++;
            }
        }else if(mode==1){
            position=(int)(Math.random()*mediaList.size());
        }

        ImageButton play = (ImageButton) findViewById(R.id.stop);
        play.setImageResource(R.drawable.sp);

        v.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()== MotionEvent.ACTION_DOWN){
                    ((ImageButton)v).setImageDrawable(getResources().getDrawable(R.drawable.next_a));
                }else if(event.getAction()==MotionEvent.ACTION_UP){
                    ((ImageButton)v).setImageDrawable(getResources().getDrawable(R.drawable.next_b));
                }
                return false;
            }
        });
    }

    public void previousMusic(View v){

        if(mode==0) {
            if (position == 0) {
                position = mediaList.size()-1;

            } else {
                position--;
            }
        }else if(mode==1){
            position=(int)(Math.random()*mediaList.size());
        }

        ImageButton play = (ImageButton) findViewById(R.id.stop);
        play.setImageResource(R.drawable.sp);

        v.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_DOWN){
                    ((ImageButton) v).setImageDrawable(getResources().getDrawable(R.drawable.last_a));
                }else if(event.getAction()==MotionEvent.ACTION_UP){
                    ((ImageButton)v).setImageDrawable(getResources().getDrawable(R.drawable.last_b));
                }
                return false;
            }
        });

    }

    public void sendFuntion(int funtion){
        Intent intent = new Intent("to.Service.for.play.funtion");
        intent.putExtra("position",position);
        intent.putExtra("mode",mode);
        intent.putExtra("funtion",funtion);
        localBroadcastManager_activity.sendBroadcast(intent);
    }//发广播

//    public void sendUpdate(){
//        Intent intent = new Intent("to.Service.for.back");
//        localBroadcastManager_activity.sendBroadcast(intent);
//    }//发广播

    public  class ActivityReceive extends BroadcastReceiver {//接收广播后的处理
        @Override
        public void onReceive(Context context, Intent intent) {
            int state;
            ImageButton play = (ImageButton) findViewById(R.id.stop);
            state = intent.getIntExtra("state",0);
            if(state==1){
                play.setImageResource(R.drawable.sp);
            }else{
                play.setImageResource(R.drawable.st);
            }
            position = intent.getIntExtra("position",0);
            mode = intent.getIntExtra("mode",0);
            TextView setTv = (TextView)findViewById(R.id.textView);;
            setTv.setText(intent.getStringExtra("playName"));
            ImageButton setMode = (ImageButton) findViewById(R.id.around);
            switch (mode){
                case 0:
                    setMode.setImageResource(R.drawable.orr);
                    break;
                case 1:
                    setMode.setImageResource(R.drawable.rand);
                    break;
                case 2:
                    setMode.setImageResource(R.drawable.single);
                    break;
                default:
                    break;
            }

        }
    }//收广播

    @Override
    protected void onDestroy(){
        super.onDestroy();
        ActivityCollector.finishAll();
        ActivityCollector.removeActivity(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main,menu);
        setIconEnable(menu,true);
        return true;
    }//菜单

    private void setIconEnable(Menu menu, boolean enable) {
        try
        {
//未知的类
            Class<?> clazz = Class.forName("com.android.internal.view.menu.MenuBuilder");
            Method m = clazz.getDeclaredMethod("setOptionalIconsVisible", boolean.class);
            m.setAccessible(true);
            //MenuBuilder实现Menu接口，创建菜单时，传进来的menu其实就是MenuBuilder对象(java的多态特征)
            m.invoke(menu, enable);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }//菜单图标

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.quit_item:
                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                dialog.setTitle("警告！！！");
                dialog.setMessage("确定退出吗");
                dialog.setCancelable(false);
                dialog.setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent quit = new Intent(MainActivity.this,MediaService.class);
                        stopService(quit);
                        finish();
                        Toast.makeText(MainActivity.this,"成功退出",Toast.LENGTH_SHORT).show();
                    }
                });
                dialog.setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(MainActivity.this,"就知道你不会离开我",Toast.LENGTH_SHORT).show();
                    }
                });
                dialog.show();
                break;
            case R.id.small_item:
                Intent small = new Intent(Intent.ACTION_MAIN);
                small.addCategory(Intent.CATEGORY_HOME);
                small.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(small);
                break;
            default:
                break;
        }
        return true;
    }//菜单点击事件响应
}
