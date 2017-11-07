package com.kaltura.playkit.samples.offline;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.kaltura.dtg.ContentManager;
import com.kaltura.dtg.DownloadItem;
import com.kaltura.dtg.DownloadStateListener;
import com.kaltura.playkit.LocalAssetsManager;
import com.kaltura.playkit.PKDrmParams;
import com.kaltura.playkit.PKMediaConfig;
import com.kaltura.playkit.PKMediaEntry;
import com.kaltura.playkit.PKMediaFormat;
import com.kaltura.playkit.PKMediaSource;
import com.kaltura.playkit.PlayKitManager;
import com.kaltura.playkit.Player;

import java.io.File;
import java.util.Collections;
import java.util.List;


public class MainActivity extends AppCompatActivity {


    private static final String TAG = "MainActivity";
    private static final String ASSET_URL = "https://cdnapisec.kaltura.com/p/2215841/playManifest/entryId/1_w9zx2eti/protocol/https/format/mpegdash/a.mpd";
    private static final String ASSET_ID = "asset1";
    private static final String ASSET_LICENSE_URL = null;

    private static final long MIN_EXP_SEC = 10;

    final private Context context = this;   // for ease of use in inner classes
    private Player player;
    private ContentManager contentManager;
    private LocalAssetsManager localAssetsManager;
    private PKMediaEntry originMediaEntry = mediaEntry(ASSET_ID, ASSET_URL, ASSET_LICENSE_URL);
    private PKMediaSource originMediaSource = originMediaEntry.getSources().get(0);
    private Handler mainHandler = new Handler(Looper.getMainLooper());


    private PKMediaEntry mediaEntry(String id, String url, String licenseUrl) {

        PKMediaSource source = new PKMediaSource()
                .setId(id)
                .setMediaFormat(PKMediaFormat.valueOfUrl(url))
                .setUrl(url);

        if (licenseUrl != null) {
            source.setDrmData(Collections.singletonList(
                    new PKDrmParams(licenseUrl, PKDrmParams.Scheme.WidevineCENC)));
        }

        return new PKMediaEntry()
                .setId(id)
                .setSources(Collections.singletonList(source));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMenu();
            }
        });

        startContentManager();
        startLocalAssetsManager();
        
        loadPlayer();
    }

    private void loadPlayer() {
        
        if (player == null) {
            //Create instance of the player.
            player = PlayKitManager.loadPlayer(this, null);

            //Get the layout, where the player view will be placed.
            LinearLayout layout = (LinearLayout) findViewById(R.id.player_root);
            //Add player view to the layout.
            layout.addView(player.getView());
        }
    }

    private void startLocalAssetsManager() {
        if (localAssetsManager == null) {
            localAssetsManager = new LocalAssetsManager(this);
        }
    }

    private void startContentManager() {
        if (contentManager != null) {
            return;
        }
        contentManager = ContentManager.getInstance(this);
        contentManager.getSettings().maxConcurrentDownloads = 4;

        contentManager.addDownloadStateListener(new DownloadStateListener() {
            @Override
            public void onDownloadComplete(DownloadItem item) {
                Log.d(TAG, "complete: " + item);
            }

            @Override
            public void onProgressChange(DownloadItem item, long downloadedBytes) {
                Log.d(TAG, "progress: " + downloadedBytes);
            }

            @Override
            public void onDownloadStart(DownloadItem item) {
                Log.d(TAG, "start: " + item);
            }

            @Override
            public void onDownloadPause(DownloadItem item) {
                Log.d(TAG, "pause: " + item);
            }

            @Override
            public void onDownloadFailure(DownloadItem item, Exception error) {
                Log.d(TAG, "failure: " + item);

            }

            @Override
            public void onDownloadMetadata(DownloadItem item, Exception error) {
                Log.d(TAG, "meta: " + item);
                if (error == null) {
                    item.startDownload();
                } else {

                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(context, "Error: Load Metdata Failed - check your network connection", Toast.LENGTH_SHORT).show();
                        }
                    });
                    Log.e(TAG, "onDownloadMetadata failure: " + error.getMessage());
                    contentManager.removeItem(ASSET_ID);
                }
            }

            @Override
            public void onTracksAvailable(DownloadItem item, DownloadItem.TrackSelector trackSelector) {
                Log.d(TAG, "tracks: " + item);

                // Select video track
                List<DownloadItem.Track> videoTracks = trackSelector.getAvailableTracks(DownloadItem.TrackType.VIDEO);
                DownloadItem.Track selectedVideoTrack = null;
                
                // A few recipes, pick one or cook something else
                // SD-ish track
                selectedVideoTrack = getMinimumRequiredTrack(videoTracks, 600000, true);

                // HD-ish 
                selectedVideoTrack = getMinimumRequiredTrack(videoTracks, 1300000, true);
                
                // Select the highest-bitrate video
                selectedVideoTrack = Collections.max(videoTracks, DownloadItem.Track.bitrateComparator);
                
                // Or settle for the lowest
                selectedVideoTrack = Collections.min(videoTracks, DownloadItem.Track.bitrateComparator);

                trackSelector.setSelectedTracks(DownloadItem.TrackType.VIDEO, Collections.singletonList(selectedVideoTrack));


                // Select ALL audio tracks
                trackSelector.setSelectedTracks(DownloadItem.TrackType.AUDIO, trackSelector.getAvailableTracks(DownloadItem.TrackType.AUDIO));

                // Select ALL text tracks
                trackSelector.setSelectedTracks(DownloadItem.TrackType.TEXT, trackSelector.getAvailableTracks(DownloadItem.TrackType.TEXT));
            }
        });
        contentManager.start(new ContentManager.OnStartedListener() {
            @Override
            public void onStarted() {
                Log.d(TAG, "Download Service started");
            }
        });
    }

    // Find the minimal "good enough" track. In other words, the track that has bitrate greater than or equal
    // to the requested minimum.
    private DownloadItem.Track getMinimumRequiredTrack(List<DownloadItem.Track> videoTracks, int minRequired, boolean settleForLess) {
        
        if (videoTracks == null || videoTracks.isEmpty()) {
            return null;
        }
        
        Collections.sort(videoTracks, DownloadItem.Track.bitrateComparator);
        for (DownloadItem.Track videoTrack : videoTracks) {
            if (videoTrack.getBitrate() >= minRequired) {
                return videoTrack;
            }
        }
        
        if (settleForLess) {
            return videoTracks.get(videoTracks.size()-1);
        }
        return null;
    }


    void showMenu() {
        new AlertDialog.Builder(context)
                .setItems(new String[]{"Download", "Register", "Play Local", "Unregister", "Remove", "Refresh"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0: // download
                                download();
                                break;
                            case 1: // register
                                registerDownloadedAsset();
                                break;
                            case 2: // play
                                playLocalAsset();
                                break;
                            case 3: // unregister
                                unregisterDownloadedAsset();
                                break;
                            case 4: // remove
                                removeDownload();
                                break;
                            case 5: // refresh
                                refreshLicense();
                                break;
                        }
                        Toast.makeText(context, "Selected " + which, Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }

    private void unregisterDownloadedAsset() {
        String path = contentManager.getLocalFile(ASSET_ID).getAbsolutePath();
        localAssetsManager.unregisterAsset(path, ASSET_ID, new LocalAssetsManager.AssetRemovalListener() {
            @Override
            public void onRemoved(String localAssetPath) {
                Toast.makeText(context, "unregistered", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void playLocalAsset() {
        final String path = contentManager.getLocalFile(ASSET_ID).getAbsolutePath();
        if (path == null) {
            Toast.makeText(context, "Error path is null", Toast.LENGTH_LONG).show();
            return;
        }

        localAssetsManager.checkAssetStatus(path, ASSET_ID, new LocalAssetsManager.AssetStatusListener() {
            @Override
            public void onStatus(String localAssetPath, long expiryTimeSeconds, long availableTimeSeconds, boolean isRegistered) {
                //  check if DRM content valid                           or clear content
                if ((expiryTimeSeconds >= MIN_EXP_SEC && isRegistered) || (expiryTimeSeconds == Long.MAX_VALUE && availableTimeSeconds == Long.MAX_VALUE)) {
                    playOfflineVideo(path);
                } else {
                    Toast.makeText(context, "Error License Expired or not registerd please refresh it while in online mode", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        });
    }

    private void playOfflineVideo(String path) {
        PKMediaSource mediaSource = localAssetsManager.getLocalMediaSource(ASSET_ID, path);

        player.prepare(new PKMediaConfig().setMediaEntry(new PKMediaEntry().setSources(Collections.singletonList(mediaSource))));
        player.play();
    }

    private void registerDownloadedAsset() {
        File localFile = contentManager.getLocalFile(ASSET_ID);
        if (localFile == null) {
            Toast.makeText(context, "failed localFile is null", Toast.LENGTH_LONG).show();
            return;
        }
        localAssetsManager.registerAsset(originMediaSource, localFile.getAbsolutePath(), ASSET_ID, new LocalAssetsManager.AssetRegistrationListener() {
            
            @Override
            public void onRegistered(String localAssetPath) {
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "registered", Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onFailed(String localAssetPath, Exception error) {
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "failed", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    private void removeDownload() {
        contentManager.removeItem(ASSET_ID);
    }

    private void refreshLicense() {
        final String path = contentManager.getLocalFile(ASSET_ID).getAbsolutePath();
        if (path == null) {
            Toast.makeText(context, "Error path is null", Toast.LENGTH_LONG).show();
            return;
        }
        localAssetsManager.refreshAsset(originMediaSource, path, ASSET_ID, new LocalAssetsManager.AssetRegistrationListener() {
            @Override
            public void onRegistered(String localAssetPath) {
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "refreshed", Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onFailed(String localAssetPath, Exception error) {
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "failed", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    private void download() {
        
        DownloadItem item = contentManager.findItem(ASSET_ID);
        if (item == null) {
            item = contentManager.createItem(ASSET_ID, ASSET_URL);
            item.loadMetadata();
        } else {
            item.startDownload();
        }
    }
}
