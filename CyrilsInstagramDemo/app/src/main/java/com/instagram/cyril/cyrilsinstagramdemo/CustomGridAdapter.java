package com.instagram.cyril.cyrilsinstagramdemo;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by cyril on 07/10/2017.
 */

public class CustomGridAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<PostData> list;
    private MediaPlayer player;
    private Boolean muteStatus = false;

    public CustomGridAdapter(Context context, ArrayList<PostData> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        View customView = null;
        LayoutInflater cyrilsInflater = LayoutInflater.from(context);
        customView = cyrilsInflater.inflate(R.layout.grid_image_view, null);
        final ImageView thumbnail = (ImageView) customView.findViewById(R.id.gridImage);
        ImageView videoSymbol = (ImageView) customView.findViewById(R.id.videoSymbol);
        RelativeLayout stackGroup = (RelativeLayout) customView.findViewById(R.id.layerGroup);
        TextView stackCount = (TextView) customView.findViewById(R.id.layerCount);
        new DownloadImageTask(false, thumbnail)
                .execute(list.get(i).thumbnailURL);
        if (list.get(i).postType.equals("video")) {

            videoSymbol.setVisibility(View.VISIBLE);
        } else if (list.get(i).postType.equals("carousel")) {
            stackGroup.setVisibility(View.VISIBLE);
            stackCount.setText(Integer.toString(list.get(i).carousel_mediaUrlList.size()));
        }
        customView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(context, R.style.AppTheme);
                dialog.setContentView(R.layout.individual_post_view);
                dialog.setCanceledOnTouchOutside(false);
                UserSession session = new UserSession(context);
                RelativeLayout backButton = (RelativeLayout) dialog.findViewById(R.id.imageButton_Back);
                int viewpagerheight = Math.round(Integer.parseInt(session.getUsableWidth()) * list.get(i).imageheight / list.get(i).imageWidth);
                LinearLayout profileDetailsTab = (LinearLayout) dialog.findViewById(R.id.profileDetailsTab);
                profileDetailsTab.measure(0, 0);
                ImageView profilePic = (ImageView) dialog.findViewById(R.id.profilePic);
                ViewPager postedImagePager = (ViewPager) dialog.findViewById(R.id.postImagePager);
                postedImagePager.getLayoutParams().height = viewpagerheight + profileDetailsTab.getMeasuredHeight();
                postedImagePager.getLayoutParams().width = Integer.parseInt(session.getUsableWidth());
                TextView captionText = (TextView) dialog.findViewById(R.id.captionText);
                if (list.get(i).postType.equals("image")) {
                    ArrayList<String> urlList = new ArrayList<>();
                    urlList.add(list.get(i).standardResolutionPostURL);
                    CustomPageAdapter adapter = new CustomPageAdapter(context, urlList, thumbnail.getDrawable());
                    postedImagePager.setAdapter(adapter);
                } else if (list.get(i).postType.equals("carousel")) {
                    CustomPageAdapter adapter = new CustomPageAdapter(context, list.get(i).carousel_mediaUrlList, thumbnail.getDrawable());
                    postedImagePager.setAdapter(adapter);
                } else {
                    postedImagePager.setVisibility(View.GONE);
                    final VideoView videoPlayer = (VideoView) dialog.findViewById(R.id.videoPlayer);
                    videoPlayer.setVisibility(View.VISIBLE);
                    videoPlayer.getLayoutParams().height = viewpagerheight;
                    final ImageView volumeButton = (ImageView) dialog.findViewById(R.id.volumeButton);
                    volumeButton.setVisibility(View.VISIBLE);
                    videoPlayer.setVideoURI(Uri.parse(list.get(i).videoURL));
                    videoPlayer.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {
                            if (muteStatus) {
                                player.setVolume(1, 1);
                                muteStatus = false;
                                volumeButton.setImageResource(R.mipmap.symbol_unmute);
                            } else {
                                player.setVolume(0, 0);
                                muteStatus = true;
                                volumeButton.setImageResource(R.mipmap.symbol_muted);
                            }
                            return false;
                        }
                    });
                    videoPlayer.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                        @Override
                        public void onScrollChanged() {
                            if (player != null) {
                                player.setVolume(0, 0);
                                muteStatus = true;
                                volumeButton.setImageResource(R.mipmap.symbol_muted);
                            }
                        }
                    });
                    videoPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer m) {
                            player = m;
                            try {
                                if (m.isPlaying()) {
                                    m.stop();
                                    m.release();
                                    m = new MediaPlayer();
                                }
                                m.setVolume(0f, 0f);
                                volumeButton.setImageResource(R.mipmap.symbol_muted);
                                m.setLooping(true);
                                m.start();
                            } catch (Exception e) {
                                Log.e("EXX", "Exception caught from onpreparedlistedner   video player  " + e.getLocalizedMessage());
                            }
                        }
                    });

                }
                new DownloadImageTask(true, profilePic)
                        .execute(list.get(i).userProfilePicURL);

                TextView profoileName = (TextView) dialog.findViewById(R.id.profileName);
                ImageView likeButton = (ImageView) dialog.findViewById(R.id.likeButton);
                TextView likeText = (TextView) dialog.findViewById(R.id.likeCount);
                ImageView commentButton = (ImageView) dialog.findViewById(R.id.commentButton);
                TextView commentText = (TextView) dialog.findViewById(R.id.commentCount);
                TextView tagsText = (TextView) dialog.findViewById(R.id.tagsText);

                profoileName.setText(list.get(i).userName);
                likeText.setText(list.get(i).likesCount + " Likes");
                commentText.setText(list.get(i).commentsCount + " Comments");
                captionText.setText(list.get(i).captionText);
                String tagText = "Tags   ";
                for (int j = 0; j < list.get(i).tagList.size(); j++) {
                    tagText += " #" + list.get(i).tagList.get(j);
                }
                tagsText.setText(tagText);
                if (list.get(i).userLikedStatus)
                    likeButton.setImageResource(R.mipmap.button_liked);
                backButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

        return customView;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    private class DownloadImageTask extends AsyncTask<String, Boolean, Bitmap> {
        ImageView bmImage;
        Boolean isroundedImage = false;

        public DownloadImageTask(Boolean isroundedImage, ImageView bmImage) {
            this.bmImage = bmImage;
            this.isroundedImage = isroundedImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getLocalizedMessage());
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            if (isroundedImage) {
                ImageFormatter formatter = new ImageFormatter(result);
                bmImage.setImageBitmap(formatter.getRoundedBitmap(200));
            } else bmImage.setImageBitmap(result);
        }
    }

}
