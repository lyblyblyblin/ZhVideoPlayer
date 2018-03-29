package com.zh.zhvideoplayer.player;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.provider.MediaStore.Video.Thumbnails;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zh.zhvideoplayer.R;

import java.util.ArrayList;

import static com.zh.zhvideoplayer.TTSTool.ttsToSpe;

/**
 * 本地视频列表的适配器
 */
public class LocalVideoListAdapter extends BaseAdapter {

    private Context mContext;
    private LocalVideoImageLoader mVideoImageLoader;
    private int defaultSelection = -1;
    private ArrayList<String> paths;
    private ArrayList<String> names;

    public LocalVideoListAdapter(Context context, ArrayList<String> names, ArrayList<String> paths,boolean ttsiswork) {
        super();
        this.paths = paths;
        this.names = names;
        this.mContext = context;
        mVideoImageLoader = new LocalVideoImageLoader(context);// 初始化缩略图载入方法
    }

    /**
     * @param position 设置高亮状态的item
     */
    public void setSelectPosition(int position) {
        if (!(position < 0 || position > paths.size())) {
            defaultSelection = position;
            notifyDataSetChanged();
        }
    }
    /**
     * @param position 删除item
     */
    public void removePosition(int position) {
        if (!(position < 0 || position > paths.size())) {
            paths.remove(position);
            names.remove(position);
            notifyDataSetChanged();
        }
    }
    /**
     * @param position speak 信息
     */
    public void setSpeakPosition(int position) {
        if (!(position < 0 || position > paths.size())) {
            ttsToSpe(mContext,names.get(position));
        }
    }



    @Override
    public int getCount() {
        return paths.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.video_local_video_item, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.setData(names.get(position), paths.get(position));
        viewHolder.image.setTag(mVideoImageLoader.hashKeyForDisk(paths.get(position)));//绑定imageview
        mVideoImageLoader.showThumbByAsynctack(paths.get(position), viewHolder.image);


        if (position == defaultSelection) {// 选中时设置单纯颜色
            convertView.setBackgroundColor(Color.parseColor("#80CBC4"));
        } else {// 未选中时设置selector
            convertView.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }
        return convertView;
    }

    private static class ViewHolder {
        private View view;
        private ImageView image;
        private TextView title;
        Bitmap bitmap = Bitmap.createBitmap(10, 10, Config.ARGB_4444);

        public ViewHolder(View view) {
            this.view = view;
            image = (ImageView) view.findViewById(R.id.image);
            title = (TextView) view.findViewById(R.id.title);

        }

        public void setData(String videoname, String videopath) {
            title.setText(videoname);
            image.setImageResource(R.drawable.loading_11);
        }

        /**
         * 获取视频文件截图
         *
         * @param path 视频文件的路径
         * @return Bitmap 返回获取的Bitmap
         */
        public static Bitmap getVideoThumb(String path) {
            MediaMetadataRetriever media = new MediaMetadataRetriever();
            media.setDataSource(path);
            return media.getFrameAtTime();
        }

        public static Bitmap getVideoThumb1(String path) {
            Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(path,
                    Thumbnails.MICRO_KIND);//MINI_KIND: 512 x 384，MICRO_KIND: 96 x 96,
//			bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
            return bitmap;
        }
    }
}
