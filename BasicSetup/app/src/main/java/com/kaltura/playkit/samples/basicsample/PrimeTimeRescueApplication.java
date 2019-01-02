/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kaltura.playkit.samples.basicsample;

import android.app.Application;

import com.google.android.exoplayer2.offline.DownloadManager;
import com.google.android.exoplayer2.upstream.cache.Cache;
import com.kaltura.ptrescue.DownloadTracker;

import java.io.File;

/**
 * Placeholder application to facilitate overriding Application methods for debugging and testing.
 */
public class PrimeTimeRescueApplication extends Application {


  protected String userAgent;

  private DownloadManager downloadManager;
  private DownloadTracker downloadTracker;

  @Override
  public void onCreate() {
    super.onCreate();
  }


//  /** Returns whether extension renderers should be used. */
//  public boolean useExtensionRenderers() {
//    return "withExtensions".equals(BuildConfig.FLAVOR);
//  }


}
