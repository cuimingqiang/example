package com.cmq.test.dex;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * dx --dex --output=repair.dex 源jar
 */
@Route(path = "/dex/main")
public class TestDexMainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_dex_act_main);
        Button button = findViewById(R.id.button);
        //button.setText("我被修改了");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copy();
                Toast.makeText(TestDexMainActivity.this, "文件替换成功", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void copy(){
        try {
            InputStream is = getResources().getAssets().open("repair.dex");
            byte[] buffer = new byte[2024];
            File dir = new File(getCacheDir(),"dex");
            if(!dir.exists())dir.mkdirs();
            File repair = new File(dir,"repair.dex");
            if(!repair.exists()) {
                repair.createNewFile();
                FileOutputStream fos = new FileOutputStream(repair);
                int length = -1;
                while ((length = is.read(buffer))!=-1){
                    fos.write(buffer,0,length);
                }
                is.close();
                fos.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
