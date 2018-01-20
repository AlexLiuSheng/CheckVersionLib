package com.allenliu.sample;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.allenliu.sample.v1.V1Activity;
import com.allenliu.sample.v2.V2Activity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void mainOnClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.btn_v1:
                intent = new Intent(this, V1Activity.class);
                startActivity(intent);
                break;
            case R.id.btn_v2:
                intent = new Intent(this, V2Activity.class);
                startActivity(intent);

                break;
        }
    }
}
