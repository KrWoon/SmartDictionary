package com.explain;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.explain.ListViewUI.ListViewAdapter;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;

import static com.explain.MainActivity.nounExtracter;


public class VoiceActivity extends AppCompatActivity {
    Intent intent;
    SpeechRecognizer mRecognizer;
    private ListView wordView = null;
    private ListViewAdapter lvAdapter = null;
    String time="";
    Button startBtn;
    Button endBtn;
    HashSet<String> returnvalue = null;
    private final int MY_PERMISSIONS_RECORD_AUDIO = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice);

        // 버튼과 텍스트뷰
        wordView = (ListView) findViewById(R.id.wordView);
        startBtn = (Button) findViewById(R.id.button01);
        endBtn = (Button) findViewById(R.id.button02);

        lvAdapter = new ListViewAdapter(this.getApplicationContext());
        wordView.setAdapter(lvAdapter);


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
//                new JSONTask().execute("https://freeorder1010.herokuapp.com/order/post");//AsyncTask 시작시킴

                startBtn.setVisibility(View.GONE);
                endBtn.setVisibility(View.VISIBLE);

                // Language 는 한국어. 영어는 "en-US"
//                Toast.makeText(VoiceActivity.this, "음성 인식 시작", Toast.LENGTH_SHORT).show();
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

                startBtn.setVisibility(View.VISIBLE);
                endBtn.setVisibility(View.GONE);

                if(mRecognizer != null) {
                    mRecognizer.stopListening();
//                    Toast.makeText(VoiceActivity.this, "음성 인식 종료", Toast.LENGTH_SHORT).show();

                    mRecognizer.cancel();
                    mRecognizer.destroy();
                }
            }
        });
    }

    class listener implements RecognitionListener {
        @Override
        public void onReadyForSpeech(Bundle bundle) {
            // 음성 인식 준비 완료
//            Toast.makeText(VoiceActivity.this, "음성 인식 준비 완료", Toast.LENGTH_SHORT).show();
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
//            Toast.makeText(VoiceActivity.this, "말을 하세요", Toast.LENGTH_SHORT).show();
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

            /* 현재 시간 계산 */
            long now = System.currentTimeMillis();
            Date date = new Date(now);
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            time = sdf.format(date);

            /* 분석하는 문장은 최대 2개 */
            int len = 1;
            if(rs.length > 1)
                len = 2;

            /* 두개 의 문장을 분석해서 basket에 담는다 */
            HashSet<String> basket = new HashSet<String>();
            for(int i=0; i<len; i++) {
                returnvalue = nounExtracter.getNoun(rs[i]);
                basket.addAll(returnvalue);
            }

            /* basket 셋에 담긴 단어를 리스트뷰에 넣는다. */
            Iterator<String> it = basket.iterator();
            while(it.hasNext()) {
                // 보여줄 단어와 시간을 word에 저장
                lvAdapter.addItem(it.next());
            }

            lvAdapter.addItem("", rs[0], time);
            lvAdapter.notifyDataSetChanged();
            mRecognizer.startListening(intent);
        }

        @Override
        public void onPartialResults(Bundle bundle) {
        }

        @Override
        public void onEvent(int i, Bundle bundle) {
        }
    };


    /* 데이터 전송을 맡음 */
    public class JSONTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                //JSONObject를 만들고 key value 형식으로 값을 저장해준다.
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("user_id", "androidTest");
                jsonObject.accumulate("name", "yun");

                HttpURLConnection con = null;
                BufferedReader reader = null;

                try{
                    //URL url = new URL("http://192.168.25.16:3000/users");
                    URL url = new URL(urls[0]);
                    //연결을 함
                    con = (HttpURLConnection) url.openConnection();

                    con.setRequestMethod("POST");//POST방식으로 보냄
                    con.setRequestProperty("Cache-Control", "no-cache");//캐시 설정
                    con.setRequestProperty("Content-Type", "application/json");//application JSON 형식으로 전송
                    con.setRequestProperty("Accept", "text/html");//서버에 response 데이터를 html로 받음
                    con.setDoOutput(true);//Outstream으로 post 데이터를 넘겨주겠다는 의미
                    con.setDoInput(true);//Inputstream으로 서버로부터 응답을 받겠다는 의미
                    con.connect();

                    //서버로 보내기위해서 스트림 만듬
                    OutputStream outStream = con.getOutputStream();
                    //버퍼를 생성하고 넣음
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outStream));
                    writer.write(jsonObject.toString());
                    writer.flush();
                    writer.close();//버퍼를 받아줌

                    //서버로 부터 데이터를 받음
                    InputStream stream = con.getInputStream();

                    reader = new BufferedReader(new InputStreamReader(stream));

                    StringBuffer buffer = new StringBuffer();

                    String line = "";
                    while((line = reader.readLine()) != null){
                        buffer.append(line);
                    }

                    return buffer.toString();//서버로 부터 받은 값을 리턴해줌 아마 OK!!가 들어올것임

                } catch (MalformedURLException e){
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if(con != null){
                        con.disconnect();
                    }
                    try {
                        if(reader != null){
                            reader.close();//버퍼를 닫아줌
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            lvAdapter.addItem("속도 테스트", result, "00:00");
            lvAdapter.notifyDataSetChanged();
        }
    }
}



