package com.instagram.cyril.cyrilsinstagramdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by cyril on 06/10/2017.
 */

public class CustomAdapter extends ArrayAdapter<PostData> {

    private ArrayList<PostData> postDatas = new ArrayList<PostData>();
    private MediaPlayer player;
    private Boolean muteStatus = true;
    UserSession session;
    Context context;

    public CustomAdapter(@NonNull Context context, ArrayList<PostData> list) {
        super(context, R.layout.user_post_tab, list);
        this.context = context;
        this.postDatas = list;
        session = new UserSession(getContext());
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View customView = null;
        try {
            if (postDatas.get(position).isProfileTab) {
                LayoutInflater cyrilsInflater = LayoutInflater.from(getContext());
                customView = cyrilsInflater.inflate(R.layout.profile_top_tab, parent, false);
                LinearLayout outerLayout = (LinearLayout) customView.findViewById(R.id.outerScrollLayout);
                ImageView profilePic = (ImageView) customView.findViewById(R.id.profilePic);
                TextView posts = (TextView) customView.findViewById(R.id.postsTextField);
                TextView followers = (TextView) customView.findViewById(R.id.followersTextField);
                TextView followedBy = (TextView) customView.findViewById(R.id.followingTextField);
                TextView profileName = (TextView) customView.findViewById(R.id.profileName);
                TextView bio = (TextView) customView.findViewById(R.id.bioTextField);

                posts.setText(postDatas.get(position).posts);
                followers.setText(postDatas.get(position).followers);
                followedBy.setText(postDatas.get(position).following);
                profileName.setText(postDatas.get(position).userFullName);
                bio.setText(session.getUserBio());
                new DownloadImageTask(context, profilePic).execute(session.getProfilePicUrl());

            } else {
                LayoutInflater cyrilsInflater = LayoutInflater.from(getContext());
                customView = cyrilsInflater.inflate(R.layout.user_post_tab, parent, false);
                ImageView profilePic = (ImageView) customView.findViewById(R.id.profilePic);
                ViewPager postedImagePager = (ViewPager) customView.findViewById(R.id.postImagePager);
                TextView captionText = (TextView) customView.findViewById(R.id.captionText);
                int viewpagerheight = Math.round(Integer.parseInt(session.getUsableWidth()) * postDatas.get(position).imageheight / postDatas.get(position).imageWidth);
                if (postDatas.get(position).postType.equals("image")) {
                    postedImagePager.getLayoutParams().height = viewpagerheight + 30;
                    ArrayList<String> urlList = new ArrayList<>();
                    urlList.add(postDatas.get(position).standardResolutionPostURL);
                    CustomPageAdapter adapter = new CustomPageAdapter(getContext(), urlList, null,postDatas.get(position).imageWidth,postDatas.get(position).imageheight);
                    postedImagePager.setAdapter(adapter);
                } else if (postDatas.get(position).postType.equals("carousel")) {

                    postedImagePager.getLayoutParams().height = viewpagerheight + 30;
                    CustomPageAdapter adapter = new CustomPageAdapter(getContext(), postDatas.get(position).carousel_mediaUrlList, null,postDatas.get(position).imageWidth,postDatas.get(position).imageheight);
                    postedImagePager.setAdapter(adapter);
                } else {
                    viewpagerheight = Math.round((Integer.parseInt(session.getUsableWidth())) * postDatas.get(position).imageheight / postDatas.get(position).imageWidth);
                    postedImagePager.setVisibility(View.GONE);
                    final VideoView videoPlayer = (VideoView) customView.findViewById(R.id.videoPlayer);
                    videoPlayer.setVisibility(View.VISIBLE);
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) videoPlayer.getLayoutParams();
                    params.width = Integer.parseInt(session.getUsableWidth());
                    params.height = viewpagerheight;
                    //params.leftMargin = 30;
                    //params.rightMargin = 30;
                    params.addRule(RelativeLayout.CENTER_HORIZONTAL);
                    videoPlayer.setLayoutParams(params);
                    final ImageView volumeButton = (ImageView) customView.findViewById(R.id.volumeButton);
                    volumeButton.setVisibility(View.VISIBLE);
                    videoPlayer.setVideoURI(Uri.parse(postDatas.get(position).videoURL));
                    videoPlayer.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {try{
                            if (muteStatus) {
                                player.setVolume(1, 1);
                                muteStatus = false;
                                volumeButton.setImageResource(R.mipmap.symbol_unmute);
                            } else {
                                player.setVolume(0, 0);
                                muteStatus = true;
                                volumeButton.setImageResource(R.mipmap.symbol_muted);
                            }} catch (Exception e) {
                            Log.e("EXX", "Exception caught from onTouchListener_video player CustomAdapter  " + e.getLocalizedMessage());
                        }
                            return false;
                        }
                    });
                    videoPlayer.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                        @Override
                        public void onScrollChanged() {try{
                            if (player != null) {
                                player.setVolume(0, 0);
                                muteStatus = true;
                                volumeButton.setImageResource(R.mipmap.symbol_muted);
                            }} catch (Exception e) {
                            Log.e("EXX", "Exception caught from onScrollchangedListener_video player CustomAdapter  " + e.getLocalizedMessage());
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
                //new DownloadImageTask(false, postPic).execute(postDatas.get(position).standardResolutionPostURL);
                new DownloadImageTask(context, profilePic)
                        .execute(postDatas.get(position).userProfilePicURL);

                TextView profoileName = (TextView) customView.findViewById(R.id.profileName);
                ImageView likeButton = (ImageView) customView.findViewById(R.id.likeButton);
                TextView likeText = (TextView) customView.findViewById(R.id.likeCount);
                ImageView commentButton = (ImageView) customView.findViewById(R.id.commentButton);
                TextView commentText = (TextView) customView.findViewById(R.id.commentCount);
                TextView tagsText = (TextView) customView.findViewById(R.id.tagsText);

                profoileName.setText(postDatas.get(position).userName);
                likeText.setText(postDatas.get(position).likesCount + " Likes");
                commentText.setText(postDatas.get(position).commentsCount + " Comments");
                captionText.setText(postDatas.get(position).captionText);

                tagsText.setText("Tags   ");
                for (int i = 0; i < postDatas.get(position).tagList.size(); i++) {
                    tagsText.setText(tagsText.getText() + " #" + postDatas.get(position).tagList.get(i));
                }
                if (postDatas.get(position).userLikedStatus)
                    likeButton.setImageResource(R.mipmap.button_liked);
            }

        } catch (Exception e) {
            Log.e("EXCEPTIOn", "Exception caught from getView  from CustomAdapter   *******      " + e.getLocalizedMessage());
        }

        return customView;
    }

    @Nullable
    @Override
    public PostData getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public boolean hasStableIds() {
        return super.hasStableIds();
    }


}
