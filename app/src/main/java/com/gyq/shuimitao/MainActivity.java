package com.gyq.shuimitao;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;
import android.Manifest;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EncodingUtils;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.LENGTH_SHORT;

public class MainActivity extends AppCompatActivity {
    public DatabaseHelper mydb = null;
    public static Context mainContext = null;
    public static int mode = 0;
    public static ImageButton btLearn = null;
    public static Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            int what = msg.what;
            switch (what) {
                case 1:
                    if (mode == 0)
                        btLearn.setImageResource(R.drawable.learn);
                    else
                        btLearn.setImageResource(R.drawable.test);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainContext = getApplicationContext();
        String[] permissionsGroup=new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_WIFI_STATE
        };
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.requestEach(permissionsGroup)
                .subscribe(permission -> {
                    if(!permission.granted)
                        Toast.makeText(this,"权限名称:"+permission.name+",申请结果:"+permission.granted, LENGTH_LONG).show();
                });
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        btLearn = (ImageButton) findViewById(R.id.imgLearn);
        btLearn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(MainActivity.this,LearnActivity.class);
                startActivity(intent);
            }
        });
        ImageButton btStatics = (ImageButton) findViewById(R.id.imgStatics);
        btStatics.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String info = "";
                String info1 = LearnStatics(1);
                String info2 = LearnStatics(2);
                String info3 = LearnStatics(3);
                String info4 = LearnStatics(4);
                if(!info1.equals(null))
                    info += info1+"\n";
                if(!info2.equals(null))
                    info += info2+"\n";
                if(!info3.equals(null))
                    info += info3+"\n";
                if(!info4.equals(null))
                    info += info4+"\n";
                new Gongju().ShowMsg(MainActivity.this,
                            "学习统计",info,false,-1);
            }
        });
        CheckMode();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.down_db) {
            new Gongju().ShowMsg(this,"提示",
                    "是否下载数据库，覆盖现有的数据库？",
                    true,1);
        }
        if (id == R.id.test_mode) {
            new Gongju().ShowMsg(this,"提示",
                    "是否改为测试模式？",
                    true,2);
            if(mode==1)
                setTitle("Shuimitao（测试）");

        }
        if (id == R.id.learn_mode) {
            new Gongju().ShowMsg(this,"提示",
                    "恢复为学习模式？",
                    true,3);
            if(mode==0)
                setTitle("Shuimitao" +
                        "");
        }
        return super.onOptionsItemSelected(item);
    }
    public String LearnStaticsDay(){
        mydb =new DatabaseHelper(this);
        String info = "";
        Cursor c1;
        String t1 = new Gongju().CurTime();
        String t2 = t1.substring(0,8);
        int nw = 0;
        int n1 = 0;
        int n2 = 0;
        try {
            String sel = "select spell from word where lt1 like ? or " +
                    "lt2 like ? or " +
                    "lt3 like ? or " +
                    "lt4 like ? or " +
                    "lt5 like ? or " +
                    "lt6 like ? or " +
                    "lt7 like ? or " +
                    "lt8 like ? ";
            String like1 = "%" + t2 + "%";
            String[] where ={like1,like1,like1,like1,
                    like1,like1,like1,like1};
            c1 = mydb.rselect(sel,where);
            while (c1.moveToNext()) {
                nw ++;
            }
        } catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
            return null;
        }
        try {
            String sel = "select id from statics where dt like ? and answer=1";
            String like1 = "%" + t2 + "%";
            String[] where ={like1};
            c1 = mydb.rselect(sel,where);
            while (c1.moveToNext()) {
                n1 ++;
            }
        } catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
            return null;
        }
        try {
            String sel = "select id from statics where dt like ? and answer=2";
            String like1 = "%" + t2 + "%";
            String[] where ={like1};
            c1 = mydb.rselect(sel,where);
            while (c1.moveToNext()) {
                n2 ++;
            }
        } catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
            return null;
        }
        info+="你今天共学习了 "+nw+" 个单词，";
        info+="答对了 "+n1+" 次，";
        info+="答错了 "+n2+" 次，";
        int sc = 0;
        if(n1+n2>0)
            sc = n1*100/(n1+n2);
        info+="得分 "+sc+" 分。";
        return info;
    }
    public String LearnStatics(int type){
        mydb =new DatabaseHelper(this);
        String info = "";
        Cursor c1;
        String t1 = new Gongju().CurTime();
        String t11 = new Gongju().NextLearningTime(t1,0,0,
                0,0,1);
        String t2 = "";
        if(type==1)//一天内
            t2 = new Gongju().NextLearningTime(t1,0,0,
                -1,0,0);
        else if(type==2)//一周内
            t2 = new Gongju().NextLearningTime(t1,0,0,
                    -7,0,0);
        else if(type==3)//一月内
            t2 = new Gongju().NextLearningTime(t1,0,-1,
                    0,0,0);
        else if(type==4)//一年内
            t2 = new Gongju().NextLearningTime(t1,-1,0,
                    0,0,0);
        else
            return info;
        int nw = 0;
        int n1 = 0;
        int n2 = 0;
        t1 = t11;
        try {
            String sel = "select spell from word where " +
                    "(lt1<? and lt1>?) or " +
                    "(lt2<? and lt2>?) or " +
                    "(lt3<? and lt3>?) or " +
                    "(lt4<? and lt4>?) or " +
                    "(lt5<? and lt5>?) or " +
                    "(lt6<? and lt6>?) or " +
                    "(lt7<? and lt7>?) or " +
                    "(lt8<? and lt8>?)";
            String[] where ={t1,t2,t1,t2,t1,t2,t1,t2,
                    t1,t2,t1,t2,t1,t2,t1,t2};
            c1 = mydb.rselect(sel,where);
            while (c1.moveToNext()) {
                nw ++;
            }
        } catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
            return null;
        }
        try {
            String sel = "select id from statics where " +
                    "(dt<? and dt>?) and answer=?";
            String[] where ={t1,t2,"1"};
            c1 = mydb.rselect(sel,where);
            while (c1.moveToNext()) {
                n1 ++;
            }
        } catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
            return null;
        }
        try {
            String sel = "select id from statics where " +
                    "(dt<? and dt>?) and answer=?";
            String[] where ={t1,t2,"2"};
            c1 = mydb.rselect(sel,where);
            while (c1.moveToNext()) {
                n2 ++;
            }
        } catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
            return null;
        }
        if(type==1)
            info += "你近一天内共学习了 " + nw + " 个单词，";
        else if(type==2)
            info+="你近一周内共学习了 "+nw+" 个单词，";
        else if(type==3)
            info+="你近一个月内共学习了 "+nw+" 个单词，";
        else if(type==4)
            info+="你近一年内共学习了 "+nw+" 个单词，";
        int sc = 0;
        if(n1+n2>0)
            sc = n1*100/(n1+n2);
        info+="答对了 "+n1+" 次，";
        info+="答错了 "+n2+" 次，";
        info+="得分 "+sc+" 分。";
        return info;
    }
    public static void dwonloadDBfile() {
        String data_url = mainContext.getString(R.string.data_url);
        String db_path = mainContext.
                getDatabasePath("KET.db").getAbsolutePath();//"/data/data/com.gyq.shuimitao/databases/";//数据库在手机里的路径";
        if (getDownloadFile2Cache(data_url,db_path))
            Toast.makeText(mainContext, "下载成功！", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(mainContext, "下载失败！", Toast.LENGTH_SHORT).show();
    }
    public static Boolean getDownloadFile2Cache(String url, String filePath) {
        Boolean ret = true;
        try {
            HttpGet httpRequest = new HttpGet(url);
        //    Toast.makeText(mainContext, url, Toast.LENGTH_LONG).show();
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response = (HttpResponse) httpclient.execute(httpRequest);
            HttpEntity entity = response.getEntity();
            BufferedHttpEntity bufferedHttpEntity = new BufferedHttpEntity(entity);
            InputStream is = bufferedHttpEntity.getContent();
            String fileName = null;
            //fileName = url.substring(url.lastIndexOf('/') + 1);
            FileOutputStream fos = new FileOutputStream(filePath);
            byte buf[] = new byte[1024];
            int numread;
            while ((numread = is.read(buf)) != -1) {
                fos.write(buf, 0, numread);
            }
            fos.close();
            is.close();
        } catch (IOException e) {
            Toast.makeText(mainContext, e.toString(), Toast.LENGTH_LONG).show();
            ret = false;
            e.printStackTrace();
        } catch (Exception e) {
            Toast.makeText(mainContext, e.toString(), Toast.LENGTH_LONG).show();
            ret = false;
            e.printStackTrace();
        }
        return ret;
    }
    public static void SetTestMode(){
        String db_path = mainContext.
                getDatabasePath("KET.db").getAbsolutePath();//"/data/data/com.gyq.shuimitao/databases/";//数据库在手机里的路径";
        File f1 = new File(db_path);
        if(f1.exists()) {
            String db_path_bak = db_path+".bak";
            File f2 = new File(db_path_bak);
            if(f2.exists()){
                if(!f2.delete())
                    return;
            }
            if(new Gongju().CopySdcardFile(db_path,db_path_bak)>=0) {
                Toast.makeText(mainContext, "已经设置为测试模式！",
                        Toast.LENGTH_LONG).show();
                mode = 1;
                btLearn.setBackgroundResource(R.drawable.test);
            }
        }
    }
    public static void RestoreLearnMode(){
        String db_path = mainContext.
                getDatabasePath("KET.db").getAbsolutePath();//"/data/data/com.gyq.shuimitao/databases/";//数据库在手机里的路径";
        File f1 = new File(db_path);
        if(f1.exists()) {
            if(!f1.delete())
                return;
            String db_path_bak = db_path+".bak";
            File f2 = new File(db_path_bak);
            if(!f2.exists())
                    return;
            if(new Gongju().CopySdcardFile(db_path_bak,
                    db_path)>=0) {
                Toast.makeText(mainContext, "已经恢复为学习模式！",
                        Toast.LENGTH_LONG).show();
                mode = 0;
                btLearn.setBackgroundResource(R.drawable.learn);
                f2.delete();
            }
        }
    }
    public void CheckMode(){
        String db_path = getApplicationContext().
                getDatabasePath("KET.db").getAbsolutePath();//"/data/data/com.gyq.shuimitao/databases/";//数据库在手机里的路径";
        File f1 = new File(db_path+".bak");
        if(f1.exists()) {
            mode = 1;
            btLearn.setBackgroundResource(R.drawable.test);
            new Gongju().ShowMsg(this, "提示",
                    "当前是测试模式，是否恢复为学习模式？",
                    true, 3);
        }
    }
}
class Gongju {
    private void sendMsg(String s,int type) {
        Message msg = new Message();
        Bundle bundle = new Bundle();
        bundle.clear();
        bundle.putString("send", s);
        msg.setData(bundle);
        msg.what = type;
        MainActivity.handler.sendMessage(msg);
    }
    public void ShowMsg(Context context, String title, String msg, boolean cancel, int type){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);    //设置对话框标题
        builder.setIcon(android.R.drawable.btn_star);   //设置对话框标题前的图标
        final TextView tv = new TextView(context);
        //tv.setBackgroundResource(R.drawable.fengmian);
        tv.setTextSize(25);
        tv.setTextColor(Color.BLACK);
        tv.setText(msg);
        tv.setGravity(Gravity.CENTER_VERTICAL| Gravity.CENTER_HORIZONTAL);
        tv.setMovementMethod(ScrollingMovementMethod.getInstance());
        builder.setView(tv);
        tv.setTextColor(Color.RED);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if(type==1)
                    MainActivity.dwonloadDBfile();
                if(type==2)
                    MainActivity.SetTestMode();
                if(type==3)
                    MainActivity.RestoreLearnMode();
            }
        });
        if(cancel)
            builder.setNegativeButton("取消",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        builder.setCancelable(true);    //设置按钮是否可以按返回键取消,false则不可以取消
        AlertDialog dialog = builder.create();  //创建对话框
        dialog.setCanceledOnTouchOutside(true); //设置弹出框失去焦点是否隐藏,即点击屏蔽其它地方是否隐藏
        dialog.show();
    }
    public String CurTime(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmm");
        Date date = new Date(System.currentTimeMillis());
        return simpleDateFormat.format(date);
    }
   public String NextLearningTime(String starttime, int year,
                                   int month, int day,
                                   int hour, int min) {
        String time = "";
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat mSdf = new SimpleDateFormat(
                "yyyyMMddHHmm");
        try {
            cal.setTime(mSdf.parse(starttime));
            cal.add(Calendar.YEAR, year);
            cal.add(Calendar.MONTH, month);
            cal.add(Calendar.DAY_OF_YEAR, day);
            cal.add(Calendar.HOUR_OF_DAY, hour);
            cal.add(Calendar.MINUTE, min);
            time = mSdf.format(cal.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time;
    }
    public Boolean getDownloadFile2Cache(String url,
                                         String filePath) {
        Boolean ret = true;
        try {
            HttpGet httpRequest = new HttpGet(url);
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response = (HttpResponse) httpclient.execute(httpRequest);
            HttpEntity entity = response.getEntity();
            BufferedHttpEntity bufferedHttpEntity = new BufferedHttpEntity(entity);
            InputStream is = bufferedHttpEntity.getContent();
            FileOutputStream fos = new FileOutputStream(filePath);
            byte buf[] = new byte[1024];
            int numread;
            while ((numread = is.read(buf)) != -1) {
                fos.write(buf, 0, numread);
            }
            fos.close();
            is.close();
        } catch (IOException e) {
            ret = false;
            e.printStackTrace();
        } catch (Exception e) {
            ret = false;
            e.printStackTrace();
        }
        return ret;
    }
    public int CopySdcardFile(String fromFile, String toFile)
    {
        try
        {
            InputStream fosfrom = new FileInputStream(fromFile);
            OutputStream fosto = new FileOutputStream(toFile);
            byte bt[] = new byte[1024];
            int c;
            while ((c = fosfrom.read(bt)) > 0)
            {
                fosto.write(bt, 0, c);
            }
            fosfrom.close();
            fosto.close();
            return 0;

        } catch (Exception ex)
        {
            return -1;
        }
    }

}
