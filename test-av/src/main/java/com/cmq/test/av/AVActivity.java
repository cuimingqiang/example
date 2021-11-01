package com.cmq.test.av;

import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.cmq.base.BaseActivity;
import com.cmq.player.AVPlayer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import androidx.annotation.Nullable;

@Route(path = "/player/main")
public class AVActivity extends BaseActivity {
    AVPlayer player = new AVPlayer();

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.av_activity_player);
        Button copy = findViewById(R.id.copy);
        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            InputStream is = getAssets().open("test.mp4");
                            byte[] buffer = new byte[2048];
                            File file = new File(getCacheDir(), "test.mp4");
                            if (file.exists()) return;
                            file.createNewFile();
                            FileOutputStream fos = new FileOutputStream(file);
                            int length = -1;
                            while ((length = is.read(buffer)) != -1) {
                                fos.write(buffer, 0, length);
                            }
                            is.close();
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        });
        SurfaceView surfaceView = findViewById(R.id.surfaceView);
        player.setSurfaceView(surfaceView);
        findViewById(R.id.play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File(getCacheDir(), "test.mp4");
                if (file.exists()) {
                    player.setDatasource(file.getPath());
                    player.setOnPreparedListener(new AVPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared() {
                            player.start();
                        }
                    });

                    player.prepare();
                }
            }
        });

    }

    @Override
    protected void onDestroy() {
        player.release();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
