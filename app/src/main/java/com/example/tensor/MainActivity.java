package com.example.tensor;

import android.app.Activity;


import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class MainActivity extends Activity implements SearchView.OnQueryTextListener {

    final int result = 1;
    final int search = 2;
    final int sort = 3;

    ArrayList<Mons_data> mons_list = new ArrayList<>();
    ArrayList<Mons_data> selected_data = new ArrayList<>();
    EditText mini,max;
    MyAdapter adapter;
    int display_name=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //全てのデータを代入するのはここの方が良さそう。
        data_reader();
        //最初の画面を表示するメソッド呼び出し。
        SetResultScreen();
    }

    private void SetResultScreen() {
        setContentView(R.layout.results);

        //入力データを受け取る。
        //まだ二つしか要素がないがこれから。
        int mini_tern=1,max_tern=100;
        if(mini!=null&&!mini.getText().toString().equals("")) {
            mini_tern = Integer.parseInt(mini.getText().toString());
        }
        if(max!=null&&!max.getText().toString().equals("")) {
            max_tern = Integer.parseInt(max.getText().toString());
        }
        // ListViewにArrayAdapterを設定する
        ListView listView = (ListView)findViewById(R.id.listView);
        //ここでデータを挿入する。
        data_selector(mini_tern,max_tern);
        adapter = new MyAdapter(MainActivity.this);
        adapter.setList(selected_data);
        listView.setAdapter(adapter);
        listView.setTextFilterEnabled(true);

        //SearchViewの設定諸々
        SearchView search_text = (SearchView) findViewById(R.id.search_text);
        // SearchViewの初期表示状態を設定
        search_text.setIconifiedByDefault(false);
        // SearchViewにOnQueryChangeListenerを設定
        search_text.setOnQueryTextListener(this);
        // SearchViewのSubmitボタンを使用不可にする
        search_text.setSubmitButtonEnabled(true);
        // SearchViewに何も入力していない時のテキストを設定
        search_text.setQueryHint("モンスターが検索できるよ");

        //ソート画面へ行くボタン
        Button search_button = findViewById(R.id.search_button);
        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ソートの画面へ
                display_name = result;
                SetSearchScreen();
            }
        });

        //ソート画面へ行くボタン
        Button sort_button = findViewById(R.id.sort_button);
        sort_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ソートの画面へ
                display_name = result;
                SetSortScreen();
            }
        });
    }

    private void SetSortScreen() {
        setContentView(R.layout.sort);
        Button search_button = findViewById(R.id.search_button);
        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //結果の画面へ
                display_name = sort;
                SetResultScreen();
            }
        });
    }

    private void SetSearchScreen() {
        setContentView(R.layout.search_menu);

        mini = (EditText) findViewById(R.id.mini_tern);
        max = (EditText) findViewById(R.id.max_tern);
        //検索画面へ移行するボタン
        Button search_button = findViewById(R.id.search_button);
        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //結果表示画面へ
                display_name = search;
                SetResultScreen();
            }
        });
    }

    //起動時にデータの読み込み(毎回するのは重すぎて本番だとやってられないかも)
    public void data_reader() {
        InputStream is = null;
        BufferedReader br = null;
        try {
            try {
                // assetsフォルダ内の data_mons.csv をオープンする
                is = this.getAssets().open("data_mons_test.csv");
                br = new BufferedReader(new InputStreamReader(is));

                // １行ずつ読み込み
                String line;

                while ((line = br.readLine()) != null) {
                    Mons_data tem=new Mons_data(line);
                    mons_list.add(tem);
                }
            } finally {
                if (is != null) is.close();
                if (br != null) br.close();
            }
        } catch (Exception e){
            // エラー発生時の処理
            e.printStackTrace();
        }
    }

    public void data_selector(int mini_tern, int max_tern){
        selected_data = new ArrayList<>();
        for(Mons_data monster : mons_list){
            if(monster.getshortest_tern()>=mini_tern){
                if(monster.getshortest_tern()<=max_tern) {
                    selected_data.add(monster);
                }
            }
        }
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        if(!query.equals("")){
            final ArrayList<Mons_data> filtered_mons_data = new ArrayList<>();
            for (Mons_data monster: selected_data) {
                    if ((monster.getname()!=null&&monster.getname().contains(query))||(monster.getskill_name()!=null&&monster.getskill_name().contains(query))) {
                        filtered_mons_data.add(monster);
                }
            }
            //データをセット
            adapter.setList(filtered_mons_data);
            //反映
            adapter.notifyDataSetChanged();
        }
        else{
            //何も変化を加えていないデータをセット
            adapter.setList(selected_data);
            //反映
            adapter.notifyDataSetChanged();
        }

        return true;
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            switch (display_name) {
                case result:
                    SetResultScreen();
                    break;
                case search:
                    SetSearchScreen();
                    break;
                case sort:
                    SetSortScreen();
                    break;
                    default:
                        onDestroy();
            }
        }
        return false/*super.onKeyDown(keyCode, event)*/;
    }

}
