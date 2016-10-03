package com.yezi.zuo.music_big_boom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by zuo on 2016/10/2.
 */
public class MediaAdapter extends ArrayAdapter<Media> {

    private  int resourceId;
    public  MediaAdapter(Context context, int textViewResourceId, List<Media> objects){
        super(context,textViewResourceId,objects);
        resourceId =textViewResourceId;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        Media me = getItem(position);
        View view;
        ViewHolder viewHolder;
        if(convertView ==null){
            view = LayoutInflater.from(getContext()).inflate(resourceId,null);
            viewHolder = new ViewHolder();
            viewHolder.fruitname = (TextView)view.findViewById(R.id.music_name);
            viewHolder.mediaImage =(ImageView)view.findViewById(R.id.media_image);
            view.setTag(viewHolder);
        }else{
            view=convertView;
            viewHolder = (ViewHolder)view.getTag();
        }
        viewHolder.fruitname.setText(me.getDisplay_name());
        viewHolder.mediaImage.setImageResource(R.drawable.hh);
        return view;


    }
    class ViewHolder{
        TextView fruitname;
        ImageView mediaImage;
    }

}
