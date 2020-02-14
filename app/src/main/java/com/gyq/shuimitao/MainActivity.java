package com.gyq.shuimitao;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.LENGTH_SHORT;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        ImageButton btLearn = (ImageButton) findViewById(R.id.imgLearn);
        btLearn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Toast.makeText(MainActivity.this,getString(R.string.main_activity_button2_name), LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this,LearnActivity.class);
                startActivity(intent);
            }
        });
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
            dwonloadDBfile();
        }
        return super.onOptionsItemSelected(item);
    }
    public void dwonloadDBfile() {
        String data_url = getString(R.string.data_url);
        DatabaseHelper mydb = new DatabaseHelper(this);
        String db_path = getApplicationContext().
                getDatabasePath("KET.db").getAbsolutePath();//"/data/data/com.gyq.shuimitao/databases/";//数据库在手机里的路径";
        if (getDownloadFile2Cache(data_url,db_path))
            Toast.makeText(getApplicationContext(), "下载成功！", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(getApplicationContext(), "下载失败！", Toast.LENGTH_SHORT).show();
    }
    public Boolean getDownloadFile2Cache(String url, String filePath) {
        Boolean ret = true;
        try {
            HttpGet httpRequest = new HttpGet(url);
            Toast.makeText(getApplicationContext(), url, Toast.LENGTH_LONG).show();
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
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
            ret = false;
            e.printStackTrace();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
            ret = false;
            e.printStackTrace();
        }
        return ret;
    }
}
class Gongju {
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
}
