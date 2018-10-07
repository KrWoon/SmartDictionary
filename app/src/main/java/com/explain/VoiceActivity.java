package com.explain;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static java.security.AccessController.getContext;

public class VoiceActivity extends AppCompatActivity {
    Intent intent;
    SpeechRecognizer mRecognizer;
    TextView textView;
    private final int MY_PERMISSIONS_RECORD_AUDIO = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice);

        // 버튼과 텍스트뷰
        textView = (TextView) findViewById(R.id.textView);
        Button startBtn = (Button) findViewById(R.id.button01);
        Button endBtn = (Button) findViewById(R.id.button02);

        // 음성 인식 허용
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {

            } else {
                // 권한을 허용하지 않은 경우
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, MY_PERMISSIONS_RECORD_AUDIO);
            }
        }



        // 음성 인식 시작
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Language 는 한국어. 영어는 "en-US"
                intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");

                mRecognizer = SpeechRecognizer.createSpeechRecognizer(VoiceActivity.this);
                mRecognizer.setRecognitionListener(new listener());
                mRecognizer.startListening(intent);
            }
        });

        // 음성 인식 종료
        endBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRecognizer.stopListening();
                Toast.makeText(VoiceActivity.this, "음성 인식 종료", Toast.LENGTH_SHORT).show();

                if(mRecognizer != null) {
                    mRecognizer.cancel();
                    mRecognizer.destroy();
                }
            }
        });
    }

//    private RecognitionListener recognitionListener = new RecognitionListener() {
    class listener implements RecognitionListener {
        @Override
        public void onReadyForSpeech(Bundle bundle) {
            // 음성 인식 준비 완료
            Toast.makeText(VoiceActivity.this, "음성 인식 준비 완료", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onBeginningOfSpeech() {
            // 사용자가 말하기 시작할 때
        }

        @Override
        public void onRmsChanged(float v) {
            // 음성의 RMS가 바뀌었을 때
        }

        @Override
        public void onBufferReceived(byte[] bytes) {
            // 음성 데이터의 buffer를 받을 수 있음
        }

        @Override
        public void onEndOfSpeech() {
            // 사용자의 말이 끝났을 때
        }

        @Override
        public void onError(int i) {
            //오류가 발생했을 때
            Toast.makeText(VoiceActivity.this, "말을 하세요", Toast.LENGTH_SHORT).show();
            if(mRecognizer != null) {
                mRecognizer.cancel();
                mRecognizer.destroy();
            }

            mRecognizer = SpeechRecognizer.createSpeechRecognizer(VoiceActivity.this);
            mRecognizer.setRecognitionListener(new listener());
            mRecognizer.startListening(intent);
        }

        @Override
        public void onResults(Bundle bundle) {
            String key = "";
            key = SpeechRecognizer.RESULTS_RECOGNITION;
            ArrayList<String> mResult = bundle.getStringArrayList(key);

            String[] rs = new String[mResult.size()];
            mResult.toArray(rs);

            // 여러 개의 String 중 첫번째 거만 출력
            textView.setText(rs[0]);
            System.out.println(mResult);
            mRecognizer.startListening(intent);
        }

        @Override
        public void onPartialResults(Bundle bundle) {
        }

        @Override
        public void onEvent(int i, Bundle bundle) {
        }
    };
}
