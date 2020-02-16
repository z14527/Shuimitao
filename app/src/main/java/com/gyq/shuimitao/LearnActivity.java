package com.gyq.shuimitao;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
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

import com.sdsmdg.tastytoast.TastyToast;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static java.lang.Math.min;

public class LearnActivity extends AppCompatActivity {

    public ListView mListView = null;
    public TextView mTextView = null;
    public TextView mNext = null;
    public TextView mInfo = null;
    MediaPlayer mediaPlayer = null;
    public DatabaseHelper mydb = null;
    private MyAdapter adapter = null;
    private List<String> mDataList = null;
    private String spell = "";
    private String chinese = "";
    private String id = "";
    private boolean bp = false;
    private boolean bps = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn);
        if(MainActivity.mode==1)
            setTitle("Shuimitao（测试）");
        else
            setTitle("Shuimitao");
        mListView = (ListView) findViewById(R.id.listView);
        mTextView = (TextView) findViewById(R.id.spell);
        mTextView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(bps) {
                    mediaPlayer.start();
                    return;
                }
                String mp3_url = getString(R.string.mp3_url);
                if(spell.contains(" "))
                    mp3_url+=spell.split(" ")[0];
                else
                    mp3_url+=spell;
                mp3_url+=".mp3";
                String db_path = getApplicationContext().
                        getDatabasePath("KET.db").getAbsolutePath();//"/data/data/com.gyq.shuimitao/databases/";//数据库在手机里的路径";
                String mp3_file = db_path.replace("KET.db","ket.mp3");
                if (new Gongju().getDownloadFile2Cache(mp3_url,mp3_file)) {
                    File f1 = new File(mp3_file);
                    if(!f1.exists())
                        return;
                    if (!bp) {
                        mediaPlayer = new MediaPlayer();
                        bp = true;
                    }
                    try {
                        mediaPlayer.setDataSource(mp3_file);
                    } catch (IOException e) {
                        return;
                    }
                    try {
                        mediaPlayer.prepare();
                    } catch (IOException e) {
                        return;
                    }
                    mediaPlayer.start();
                    bps = true;
                }
            }
        });
        process();
        mNext = (TextView) findViewById(R.id.tv_forword);
        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if(bp){
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    bp = false;
                    bps = false;
                }
                process();
            }
        });
        mInfo = (TextView) findViewById(R.id.tv_info);
        mInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if(id.equals(""))
                    return;
                List<String> list1 = WordLearningInfo(id);
                String info = "";
                for(int i=0;i<list1.size();i++)
                    info = info + "\n" + list1.get(i);
                new Gongju().ShowMsg(LearnActivity.this,
                        spell+" 学习情况",
                       info,false,-1);
            }
        });
    }
    public void process(){
        try{
            mDataList = getWord();
        }catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
            return;
        }
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
        Cursor c1;
        String t1 = new Gongju().CurTime();
        try {
            String sel = "select id,spell,chinese,lf,ltn from word where lf=? and ltn <?";
            String[] where ={"2",t1};
            c1 = mydb.rselect(sel,where);
            while (c1.moveToNext()) {
                id = ""+c1.getInt(0);
                info.add(c1.getString(1));
                info.add(c1.getString(2));
                break;
            }
        } catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
            return null;
        }
        try {
            int START1 = 1;
            int END1 = 1434;
            String[] fn = {"spell", "chinese"};
            Random random = new Random();
            while(info.size()<5) {
                int n1 = random.nextInt(END1 - START1 + 1) + START1;
                c1 = mydb.select(""+n1, fn);
                while (c1.moveToNext()) {
                    if (info.size() < 1) {
                        id = ""+n1;
                        info.add(c1.getString(0));
                    }
                    info.add(c1.getString(1));
                    break;
                }
            }
        }catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
            return null;
        }
        return info;
    }
    public List<String> WordLearningInfo(String id1){
        mydb =new DatabaseHelper(this);
        List<String> info = new ArrayList<String>();
        String[] fn = {"lf","lt1","lt2","lt3","lt4",
        "lt5","lt6","lt7","lt8"};
        Cursor c1 = mydb.select(id1,fn);
        try {
            while (c1.moveToNext()) {
                for(int i=0;i<fn.length;i++){
                    if(i==0){
                        int info1 = c1.getInt(i);
                        if(info1==0)
                            info.add("一次都没有测试过");
                        else if(info1==1)
                            info.add("首次测试通过");
                        else
                            info.add("首次测试没有通过");
                    }else{
                        Long info1 = c1.getLong(i);
                        if(info1==0)
                            info.add("还没有进行第"+i+"区段测试");
                        else {
                            String info11 = ""+info1;
                            String year = info11.substring(0,4);
                            String month = info11.substring(4,6);
                            String day = info11.substring(6,8);
                            String hour = info11.substring(8,10);
                            String minute = info11.substring(10,12);
                            info.add("第" + i + "区段首次测试时间："+
                            year+"年"+month+"月"+day+"日"+
                            hour+":"+minute);
                        }
                    }
                }
                break;
            }
        }catch (Exception e){
            Toast.makeText(this,e.toString(),Toast.LENGTH_LONG).show();
            return null;
        }
        return info;
    }
    public void UpdateDbLearning(String test_time,
                                 String is_success){
        mydb =new DatabaseHelper(this);
        String[] fn = {"lf","lt1","lt2","lt3","lt4",
                "lt5","lt6","lt7","lt8"};
        Cursor c1 = mydb.select(id,fn);
        try {
            while (c1.moveToNext()) {
                for(int i=0;i<fn.length;i++){
                    if(i==0){
                        int info1 = c1.getInt(i);
                        if(info1==0) {
                            mydb.update(id,fn[i],is_success);
                            mydb.update(id,fn[i+1],test_time);
                            String nlt = new Gongju().NextLearningTime(test_time,0,0,
                                    0,0,5);
                            mydb.update(id,"ltn",nlt);
                            break;
                        }
                    }else{
                        String lt = LastLearningTime();
                        if(lt.contains(":")) {
                            String lt1 = lt.split(":")[0];
                            int lt2 = Integer.parseInt(lt.split(":")[1]);
                            int k = UpdateIndex(lt1, test_time, lt2);
                            if(k != lt2) {
                                mydb.update(id, fn[k], test_time);
                                String nlt = "";
                                if(k==2)
                                    nlt = new Gongju().NextLearningTime(test_time,0,
                                            0,0,0,30);
                                else if(k==3)
                                    nlt = new Gongju().NextLearningTime(test_time,0,
                                            0,0,12,0);
                                else if(k==4)
                                    nlt = new Gongju().NextLearningTime(test_time,0,
                                            0,1,0,0);
                                else if(k==5)
                                    nlt = new Gongju().NextLearningTime(test_time,0,
                                            0,2,0,0);
                                else if(k==6)
                                    nlt = new Gongju().NextLearningTime(test_time,0,
                                            0,4,0,0);
                                else if(k==7)
                                    nlt = new Gongju().NextLearningTime(test_time,0,
                                            0,7,0,0);
                                else
                                    nlt = new Gongju().NextLearningTime(test_time,0,
                                            0,15,0,0);
                                mydb.update(id,"ltn",nlt);
                            }
                            break;
                        }
                    }
                }
                break;
            }
        }catch (Exception e){
            Toast.makeText(this,e.toString(),Toast.LENGTH_LONG).show();
        }
    }
    public void InsertDbStatics(String id,String test_time,
                                 String is_success){
        mydb =new DatabaseHelper(this);
        String[] fn = {"id","answer","dt"};
        String[] fv = {id,is_success,test_time};
        try {
            mydb.insert("statics",fn,fv);
        }catch (Exception e){
            Toast.makeText(this,e.toString(),Toast.LENGTH_LONG).show();
        }
    }

    public String LastLearningTime(){
        mydb =new DatabaseHelper(this);
        String[] fn = {"lt1","lt2","lt3","lt4",
                "lt5","lt6","lt7","lt8"};
        Cursor c1 = mydb.select(id,fn);
        String info = "";
        try {
            while (c1.moveToNext()) {
                for(int i=0;i<fn.length;i++){
                    Long info1 = c1.getLong(i);
                    if(info1 != 0) {
                        info = info1 + ":" + (i + 1);
                        return info;
                    }
                }
                break;
            }
        }catch (Exception e){
            Toast.makeText(this,e.toString(),Toast.LENGTH_LONG).show();
        }
        return info;
    }
    public int UpdateIndex(String last_time,String cur_time,
                           int last_index) {
/*
第一个记忆周期：5分钟
第二个记忆周期：30分钟
第三个记忆周期：12小时
第四个记忆周期：1天
第五个记忆周期：2天
第六个记忆周期：4天
第七个记忆周期：7天
第八个记忆周期：15天
 */
        int[] dtime = {0, 5, 30, 12 * 60, 24 * 60, 2 * 24 * 60, 4 * 24 * 60,
                7 * 24 * 60, 15 * 24 * 60};
        String year1 = cur_time.substring(0, 4);
        String month1 = cur_time.substring(4, 6);
        String day1 = cur_time.substring(6, 8);
        String hour1 = cur_time.substring(8, 10);
        String minute1 = cur_time.substring(10, 12);
        String year2 = last_time.substring(0, 4);
        String month2 = last_time.substring(4, 6);
        String day2 = last_time.substring(6, 8);
        String hour2 = last_time.substring(8, 10);
        String minute2 = last_time.substring(10, 12);
        int dt1 = (Integer.parseInt(year1) -
                Integer.parseInt(year2)) * 365 * 24 * 60
                + (Integer.parseInt(month1) -
                Integer.parseInt(month2)) * 30 * 24 * 60
                + (Integer.parseInt(day1) -
                Integer.parseInt(day2)) * 24 * 60
                + (Integer.parseInt(hour1) -
                Integer.parseInt(hour2)) * 60
                + (Integer.parseInt(minute1) -
                Integer.parseInt(minute2));
        if (dt1 < dtime[last_index])
            return last_index;
        return min(last_index + 1, 8);
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
                String t1 = new Gongju().CurTime();
                if(listText.get(position).equals(chinese)) {
//                    Toast.makeText(context,
//                            "答对了，你很棒！",
//                            Toast.LENGTH_LONG).show();
                    TastyToast.makeText(getApplicationContext(),
                            "答对了，你很棒！",
                            TastyToast.LENGTH_LONG, TastyToast.SUCCESS);
                    UpdateDbLearning(t1,"1");
                    InsertDbStatics(id,t1,"1");
                }
                else {
//                    Toast.makeText(context,
//                            "答错了，要继续努力哦！",
//                            Toast.LENGTH_LONG).show();
                    TastyToast.makeText(getApplicationContext(),
                            "答错了，要继续努力哦！",
                            TastyToast.LENGTH_LONG, TastyToast.ERROR);
                    UpdateDbLearning(t1,"2");
                    InsertDbStatics(id,t1,"2");
                }
                checkAll();
                checkReverse();
                map.put(position,true);
                notifyDataSetChanged();
            }
        }
     }
}

