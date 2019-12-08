/*
 * Copyright (C) 2014 Pedro Vicente Gómez Sánchez.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kaltura.playkit.samples.miniplayer;

import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.FragmentActivity;
import com.github.pedrovgs.DraggableListener;
import com.github.pedrovgs.DraggablePanel;
import com.kaltura.playkitdemo.R;


/**
 * Sample activity created to show a video from YouTube using a YouTubePlayer.
 *
 * @author Pedro Vicente Gómez Sánchez.
 */
public class PlayerActivity extends FragmentActivity {

    public static boolean USE_TEXTURE = false;

  DraggablePanel draggablePanel;

 // private YouTubePlayer youtubePlayer;
  private PlayerFragment youtubeFragment;

  /**
   * Initialize the Activity with some injected data.
   */
  @Override
  protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_youtube_sample);
      getWindow().setFormat(PixelFormat.TRANSLUCENT);
      draggablePanel = (DraggablePanel)findViewById(R.id.draggable_panel);
      findViewById(R.id.iv_thumbnail).setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              draggablePanel.maximize();
          }
      });
      initializeFragment();
      initializeDraggablePanel();
      hookDraggablePanelListeners();
  }

  /**
   * Initialize the YouTubeSupportFrament attached as top fragment to the DraggablePanel widget and
   * reproduce the YouTube video represented with a YouTube url.
   */
  private void initializeFragment() {
    youtubeFragment = new PlayerFragment();
  }

  /**
   * Initialize and configure the DraggablePanel widget with two fragments and some attributes.
   */
  private void initializeDraggablePanel() {
      draggablePanel.setFragmentManager(getSupportFragmentManager());
      draggablePanel.setTopFragment(youtubeFragment);
      MoviePosterFragment moviePosterFragment = new MoviePosterFragment();
    /*moviePosterFragment.setPoster(VIDEO_POSTER_THUMBNAIL);
    moviePosterFragment.setPosterTitle(VIDEO_POSTER_TITLE);*/
      draggablePanel.setBottomFragment(moviePosterFragment);
      draggablePanel.initializeView();
      draggablePanel.setTopFragmentResize(!USE_TEXTURE);
  }

  /**
   * Hook the DraggableListener to DraggablePanel to pause or resume the video when the
   * DragglabePanel is maximized or closed.
   */
  private void hookDraggablePanelListeners() {
    draggablePanel.setDraggableListener(new DraggableListener() {
      @Override
      public void onMaximized() {
        playVideo();
      }

      @Override
      public void onMinimized() {
        //Empty
      }

      @Override
      public void onClosedToLeft() {
        pauseVideo();
      }

      @Override
      public void onClosedToRight() {
        pauseVideo();
      }
    });
  }

  /**
   * Pause the video reproduced in the YouTubePlayer.
   */
  private void pauseVideo() {
    /*if (youtubePlayer.isPlaying()) {
      youtubePlayer.pause();
    }*/
  }

  /**
   * Resume the video reproduced in the YouTubePlayer.
   */
  private void playVideo() {
    /*if (!youtubePlayer.isPlaying()) {
      youtubePlayer.play();
    }*/
  }
}
