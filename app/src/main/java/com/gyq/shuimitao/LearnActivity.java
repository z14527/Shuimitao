package com.gyq.shuimitao;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class LearnActivity extends AppCompatActivity {

    public ListView mListView = null;
    public TextView mTextView = null;
    public TextView mNext = null;
    public DatabaseHelper mydb = null;
    private MyAdapter adapter = null;
    private List<String> mDataList = null;
    private String spell = "";
    private String chinese = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn);
        mListView = (ListView) findViewById(R.id.listView);
        mTextView = (TextView) findViewById(R.id.spell);
        process();
        mNext = (TextView) findViewById(R.id.tv_forword);
        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                process();
            }
        });
    }
    public void process(){
        mDataList = getWord();
        if(mDataList.equals(null))
            return;
        spell = mDataList.get(0);
        chinese = mDataList.get(1);
        mDataList.remove(0);
        mTextView.setText(spell);
        if(spell.length()>=16)
            mTextView.setTextSize(40);
        else if(spell.length()>=8)
            mTextView.setTextSize(60);
        else
            mTextView.setTextSize(80);
        int START = 0;
        int END = 3;
        List<String> listText = new ArrayList<String>();
        Random random = new Random();
        int n1 = random.nextInt(END - START + 1) + START;
        for(int i=n1;i<n1+4;i++) {
            listText.add(mDataList.get(i%4));
        }
        if(adapter == null) {
            adapter = new MyAdapter(listText, this);
            mListView.setAdapter(adapter);
        }else {
            adapter.setListText(listText);
            adapter.notifyDataSetChanged();
        }
    }
    public List<String> getWord(){
        mydb =new DatabaseHelper(this);
        List<String> info = new ArrayList<String>();
        Random random = new Random();
        int START = 1;
        int END = 1434;
        int n1 = random.nextInt(END - START + 1) + START;
        int n2 = random.nextInt(END - START + 1) + START;
        int n3 = random.nextInt(END - START + 1) + START;
        int n4 = random.nextInt(END - START + 1) + START;
        String[] fn = {"spell","chinese"};
        Cursor c1 = mydb.select(""+n1,fn);
        try {
            while (c1.moveToNext()) {
                info.add(c1.getString(0));
                info.add(c1.getString(1));
                break;
            }
            c1 = mydb.select("" + n2, fn);
            while (c1.moveToNext()) {
                info.add(c1.getString(1));
                break;
            }
            c1 = mydb.select("" + n3, fn);
            while (c1.moveToNext()) {
                info.add(c1.getString(1));
                break;
            }
            c1 = mydb.select("" + n4, fn);
            while (c1.moveToNext()) {
                info.add(c1.getString(1));
                break;
            }
        }catch (Exception e){
            Toast.makeText(this,e.toString(),Toast.LENGTH_LONG).show();
            return null;
        }
        return info;
    }
    public class MyAdapter extends BaseAdapter {
        private List<String> listText;
        private Context context;
        private ViewHolder viewholder;
        private MyClickListener myClickListener = null;
        private Map<Integer,Boolean> map=new HashMap<>();
        public MyAdapter(List<String> listText,Context context){
            this.listText=listText;
            this.context=context;
        }
        public void setListText(List<String> mListText){
            listText = mListText;
            checkAll();
            checkReverse();
        }
        public void checkAll(){
            for(int i=0;i<listText.size();i++) {
                if(map.containsKey(i))
                    map.remove(i);
                map.put(i, true);
            }
        }
        public void checkReverse(){
            for(int i=0;i<listText.size();i++) {
                if(map.containsKey(i))
                    map.remove(i);
                else
                    map.put(i, true);
            }
        }
        public List<String> getSelectText(){
            return listText;
        }
         @Override
        public int getCount() {
            //return返回的是int类型，也就是页面要显示的数量。
            return listText.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view;
            if (convertView==null){
                //通过一个打气筒 inflate 可以把一个布局转换成一个view对象
                view=View.inflate(context,R.layout.listview_item,null);
                            }else {
                view=convertView;//复用历史缓存对象
            }
            viewholder = new ViewHolder();
            myClickListener = new MyClickListener(position);
            viewholder.tvChinese = (TextView) view.findViewById(R.id.tv_chinese);
            viewholder.checkBox = (CheckBox) view.findViewById(R.id.rb_check_button);
//单选按钮的文字
            viewholder.tvChinese.setText(listText.get(position));
//            viewholder.checkBox.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (viewholder.checkBox.isChecked()){
//                        checkAll();
//                        checkReverse();
//                        //if(map.containsKey(position))
//                        //    map.remove(position);
//                        map.put(position,true);
//                        notifyDataSetChanged();
//                        if(listText.get(position).equals(chinese))
//                            Toast.makeText(context,
//                                    "答对了，你很棒！",
//                                    Toast.LENGTH_LONG).show();
//                        else
//                            Toast.makeText(context,
//                                    "答错了，要继续努力哦！",
//                                    Toast.LENGTH_LONG).show();
//                    }else {
//                        map.remove(position);
//
//                    }
//                }
//            });
            viewholder.tvChinese.setOnClickListener(myClickListener);
            viewholder.checkBox.setOnClickListener(myClickListener);
            if(map!=null&&map.containsKey(position)){
                viewholder.checkBox.setChecked(true);
            }else {
                viewholder.checkBox.setChecked(false);
            }
            return view;
        }
        public class ViewHolder{
            TextView tvChinese;
            CheckBox checkBox;
        }

        private class MyClickListener implements View.OnClickListener {
            private int position;
            public MyClickListener(int position){
                this.position = position;
            }
            @Override
            public void onClick(View v) {
                if(listText.get(position).equals(chinese))
                    Toast.makeText(context,
                            "答对了，你很棒！",
                            Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(context,
                            "答错了，要继续努力哦！",
                            Toast.LENGTH_LONG).show();
              //  if (viewholder.checkBox.isChecked()){
                    checkAll();
                    checkReverse();
                    //if(map.containsKey(position))
                    //    map.remove(position);
                    map.put(position,true);
              //  }else {
               //     map.remove(position);
               // }
               notifyDataSetChanged();
            }
        }
     }
}

