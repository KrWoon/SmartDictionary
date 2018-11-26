package com.explain;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.explain.ListViewUI.ListViewAdapter;

import org.json.JSONArray;
import org.json.JSONException;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import static com.explain.MainActivity.nounExtracter;


public class VoiceActivity extends AppCompatActivity {
    Intent intent;
    SpeechRecognizer mRecognizer;
    private ListView wordView = null;
    private ListViewAdapter lvAdapter = null;
    Button startBtn;
    Button endBtn;
    HashSet<String> returnvalue = null;
    HashSet<String> set = new HashSet<String>();
    Queue<String> q = new LinkedList<String>();
    private final int MY_PERMISSIONS_RECORD_AUDIO = 1;
    private SQLite db = null;
    HashMap<String, Integer> map = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice);

        // 버튼과 텍스트뷰
        wordView = (ListView) findViewById(R.id.wordView);
        startBtn = (Button) findViewById(R.id.button01);
        endBtn = (Button) findViewById(R.id.button02);

        /** sqlite에서 hashmap 형식으로 데이터 가져오기 */
        db = new SQLite(this.getApplicationContext());
//        db.deleteData();
        map = db.getAllData();

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
                startBtn.setVisibility(View.GONE);
                endBtn.setVisibility(View.VISIBLE);

                // Language 는 한국어. 영어는 "en-US"
//                Toast.makeText(VoiceActivity.this, "음성 인식 시작", Toast.LENGTH_SHORT).show();
                intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");
                intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);

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

    public void onPause() {
        super.onPause();
        db.deleteData();
        db.insertAllData(map);
    }

    public String getCurrentTime() {
        /* 현재 시간 계산 */
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        return sdf.format(date);
    }

    public Boolean isImportant(HashMap<String, Integer> data, String targetWord){
        /** 처음 사용되는 단어는 뜻을 출력 */
        if(!map.containsKey(targetWord)) {
            return true;
        }

        int numberOfNoun = data.size();                         // 명사의 갯수
        int totalCountOfNoun = 0;                               // value 들의 합 (명사 사용 횟수들의 총 합)
        for (Map.Entry<String, Integer> element : data.entrySet()){
            totalCountOfNoun += element.getValue();
        }


        double averageUseCount;
        if(numberOfNoun == 0)
            averageUseCount = 1;
        else
            averageUseCount = (double) totalCountOfNoun / numberOfNoun;


        if(data.get(targetWord) < averageUseCount)
            return true;
        else
            return false;
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
            String key = SpeechRecognizer.RESULTS_RECOGNITION;
            ArrayList<String> message = bundle.getStringArrayList(key);

            /* 문장을 형태소 단위로 분리 */
            returnvalue = nounExtracter.getNoun(message.get(0));

            /* 형태소 단위로 분리된 단어들을 출력 */
            Iterator<String> it = returnvalue.iterator();
            while(it.hasNext()) {
                String targetWord = it.next();

                /** 처음 사용되는 단어는 뜻을 출력 */
                if(!map.containsKey(targetWord)) {
                    map.put(targetWord, 1);
                }
                else {
                    /** 횟수 하나 증가 */
                    int calls = map.get(targetWord);
                    map.put(targetWord, calls + 1);
                }
            }

            /** 음성인식 재시작 */
            mRecognizer.startListening(intent);
        }

        @Override
        public void onPartialResults(Bundle bundle) {
            /** 음성 인식 중간 중간에 계속해서 단어 추출*/
            if ((bundle != null) && bundle.containsKey(SpeechRecognizer.RESULTS_RECOGNITION)) {

                String key = SpeechRecognizer.RESULTS_RECOGNITION;
                ArrayList<String> message = bundle.getStringArrayList(key);

                /* 문장을 형태소 단위로 분리 */
                returnvalue = nounExtracter.getNoun(message.get(0));

                /* 형태소 단위로 분리된 단어들을 출력 */
                Iterator<String> it = returnvalue.iterator();
                while(it.hasNext()) {
                    String targetWord = it.next();

                    if(set.contains(targetWord)) {
                        // 만약 이미 출력한 단어면 생략
                    } else {
                        /** 중요명사면 출력 **/
                        if(isImportant(map, targetWord) == true) {
                            set.add(targetWord);
                            q.offer(targetWord);
                        }
                    }
                }

                /* 큐에 담아놨다가 순서대로 호출 */
                for(int i=0; i<q.size(); i++) {
                    new JSONTask().execute("https://smartdictionary2.herokuapp.com/");//AsyncTask 시작시킴
//                    new JSONTask().execute("http://175.115.66.200:3000/");//AsyncTask 시작시킴
                }
            }
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
                jsonObject.accumulate("word", q.poll());

                HttpURLConnection con = null;
                BufferedReader reader = null;

                try{
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

            try {
                JSONObject jobject = new JSONObject(result);

                String resultWord = jobject.getString("word");
                String resultDescription = jobject.getString("description");
                String resultLink = jobject.getString("link");

                lvAdapter.addItem(resultWord, resultDescription, getCurrentTime(), resultLink);
                lvAdapter.notifyDataSetChanged();

            } catch(JSONException e) {
                e.printStackTrace();
            }
        }
    }
}



