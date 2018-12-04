package com.explain;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

public class DbActivity extends AppCompatActivity {
    private SQLite db = null;
    TextView dbText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_db);

        dbText = (TextView) findViewById(R.id.dbTextView);
        dbText.setMovementMethod(new ScrollingMovementMethod());

        /** sqlite에서 hashmap 형식으로 데이터 가져오기 */
        db = new SQLite(this.getApplicationContext());
        HashMap<String, Integer> map = db.getAllData();

        String s = "";

        int numberOfNoun = map.size();                         // 명사의 갯수
        int totalCountOfNoun = 0;

        for (Map.Entry<String, Integer> element : map.entrySet()){
            totalCountOfNoun += element.getValue();
        }

        s += "∑명사별 사용횟수 / 사용한 명사의 갯수\n" + totalCountOfNoun + " / " + numberOfNoun + "\n";
        s += "기준점 :  " + (double)totalCountOfNoun / (double)numberOfNoun + "\n\n";


        for (Map.Entry<String, Integer> element : map.entrySet()){
            s += element.getKey() + " " + element.getValue() + "\n";
        }

        dbText.setText(s);

    }

    public void deleteClicked(View v) {
        db.deleteData();
    }
}
