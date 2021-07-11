package com.cmq.test.skin;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.cmq.skin.SkinException;
import com.cmq.skin.SkinManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

@Route(path = "/skin/main")
public class TestSkinMainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_skin_act_main);
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copy();
            }
        });
        findViewById(R.id.reset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SkinManager.reset();
            }
        });
    }

    private void copy() {
        try {
            InputStream is = getResources().getAssets().open("skin.apk");
            byte[] buffer = new byte[2024];
            File dir = new File(getCacheDir(), "skin");
            if (!dir.exists()) dir.mkdirs();
            File repair = new File(dir, "skin.apk");
            if (repair.exists()) {
              repair.delete();
            }
            repair.createNewFile();
            FileOutputStream fos = new FileOutputStream(repair);
            int length = -1;
            while ((length = is.read(buffer)) != -1) {
                fos.write(buffer, 0, length);
            }
            is.close();
            fos.close();
            try {
                SkinManager.applySkin(repair.getPath());
            } catch (SkinException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
