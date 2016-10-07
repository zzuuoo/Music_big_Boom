package com.yezi.zuo.music_big_boom;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import static com.yezi.zuo.music_big_boom.MediaData.mediaList;

/**
 * Created by zuo on 2016/10/3.
 */
public class MusicPlaying extends Activity implements View.OnClickListener{



    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playing);
        Intent intent = getIntent();
        TextView tv =(TextView)findViewById(R.id.textView2);
        tv.setText(intent.getStringExtra("playName"));
        init();

    }

    private void init(){

        ImageButton play = (ImageButton) findViewById(R.id.stop2);
        ImageButton last = (ImageButton) findViewById(R.id.previous2);
        ImageButton next = (ImageButton) findViewById(R.id.next2);
        ImageButton ro = (ImageButton)findViewById(R.id.around2);
//        TextView tv = (TextView)findViewById(R.id.textView2);
//        tv.setOnClickListener(this);
        play.setOnClickListener(this);
        last.setOnClickListener(this);
        next.setOnClickListener(this);
        ro.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.around2:
                break;
            case R.id.next2:
//                nextMusic(v);
                break;
            case R.id.previous2:
                break;
            case R.id.stop2:
                break;
            default:
                break;


        }

    }

    public void nextMusic(View v){

        if(MainActivity.mode==0) {
            if (MainActivity.position == mediaList.size() - 1) {
                MainActivity.position = 0;

            } else {
                MainActivity.position++;
            }
        }else if(MainActivity.mode==1){
            MainActivity.position=(int)(Math.random()*mediaList.size());
        }

        ImageButton play = (ImageButton) findViewById(R.id.stop2);
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

}
