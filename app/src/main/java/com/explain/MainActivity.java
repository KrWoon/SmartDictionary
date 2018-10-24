package com.explain;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Iterator;


public class MainActivity extends AppCompatActivity {
    public static NounExtracter nounExtracter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        long startTime = System.currentTimeMillis();
        InputStream[] in = new InputStream[19];

        /* 파일을 통해 단어를 받는 부분 */
        in[0] = getResources().openRawResource(R.raw.bible);
        in[1] = getResources().openRawResource(R.raw.brand);
        in[2] = getResources().openRawResource(R.raw.company_names);
        in[3] = getResources().openRawResource(R.raw.congress);
        in[4] = getResources().openRawResource(R.raw.entities);
        in[5] = getResources().openRawResource(R.raw.fashion);
        in[6] = getResources().openRawResource(R.raw.foreign);
        in[7] = getResources().openRawResource(R.raw.geolocations);
        in[8] = getResources().openRawResource(R.raw.kpop);
        in[9] = getResources().openRawResource(R.raw.lol);
        in[10] = getResources().openRawResource(R.raw.names);
        in[11] = getResources().openRawResource(R.raw.neologism);
        in[12] = getResources().openRawResource(R.raw.nouns);
        in[13] = getResources().openRawResource(R.raw.pokemon);
        in[14] = getResources().openRawResource(R.raw.profane);
        in[15] = getResources().openRawResource(R.raw.slangs);
        in[16] = getResources().openRawResource(R.raw.spam);
        in[17] = getResources().openRawResource(R.raw.twitter);
        in[18] = getResources().openRawResource(R.raw.wikipedia_title_nouns);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        HashSet<String> nounSet = new HashSet<String>();

        /* 파일 하나를 다 뜯어서 nounSet에 넣기 */
        for(int numOfFile = 0; numOfFile < 19; numOfFile++) {
            String data = null;
            int i;
            try {
                i = in[numOfFile].read();
                while (i != -1) {
                    byteArrayOutputStream.write(i);
                    i = in[numOfFile].read();
                }

                data = new String(byteArrayOutputStream.toByteArray());
                in[numOfFile].close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            String[] list = data.split("\\n");
            for (int k = 0; k < list.length; k++) {
                nounSet.add(list[k]);
            }
        }

        nounExtracter = new NounExtracter(nounSet);
    }

    public void startClicked(View v) {
        Intent startIntent = new Intent(getApplicationContext(), VoiceActivity.class);
        startActivity(startIntent);
    }

    public void exitClicked(View v) {
        finish();
    }
}


