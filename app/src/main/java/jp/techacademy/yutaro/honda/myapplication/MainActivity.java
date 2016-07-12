package jp.techacademy.yutaro.honda.myapplication;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener   {

    private static final int PERMISSIONS_REQUEST_CODE = 100;
    private int nowpage =0;
    private Timer mainTimer;
    private MainTimerTask mainTimerTask;
    private Handler mHandler = new Handler();
    private Button buttonGo ;
    private Button buttonBack ;
    private Button buttonAuto ;
    private String autoLabel ;
    private boolean  flag;



    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        flag = true;




        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                getContentsInfo(nowpage);
            } else {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_CODE);
            }
        } else {
            getContentsInfo(nowpage);
        }

        buttonGo = (Button) findViewById(R.id.buttonGo);
        buttonGo.setOnClickListener(this);

        buttonBack = (Button) findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(this);

        buttonAuto = (Button) findViewById(R.id.buttonAuto);
        buttonAuto.setOnClickListener(this);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContentsInfo(nowpage);
                }
                break;
            default:
                break;
        }
    }


    public void onClick(View v) {

        if (v.getId() == R.id.buttonGo) {
            nowpage = nowpage + 1 ;
            getContentsInfo(nowpage);
        } else if (v.getId() == R.id.buttonBack) {
            nowpage = nowpage - 1 ;
            getContentsInfo(nowpage);
        } else if (v.getId() == R.id.buttonAuto) {
            nowpage = 0 ;



            autoLabel = buttonAuto.getText().toString();
            if ( flag == true){
                flag = false;
                this.mainTimer = new Timer();
                this.mainTimerTask = new MainTimerTask();
                buttonAuto.setText("停止");
                buttonGo.setEnabled(false);
                buttonBack.setEnabled(false);
                this.mainTimer.schedule(mainTimerTask, 1000,2000);//タイマースタート
            }else {
                flag = true;
                buttonAuto.setText("再生");
                buttonGo.setEnabled(true);
                buttonBack.setEnabled(true);
                this.mainTimer.cancel();
                this.mainTimer.purge();
                this.mainTimer =null;
            }
        }
    }


    private void getContentsInfo(int page)  {


        ContentResolver resolver = getContentResolver();
        Cursor cursor = resolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // データの種類
                null,
                null,
                null,
                null
        );

        int maxCount = cursor.getCount(); //カーソル内の画像数
        if ( page <= -1 ) {
            nowpage = maxCount -1 ; //0より前なら最後に移動する
        } else if  (page > maxCount-1 ) {
            nowpage = 0; //最大値の後なら最初に戻る
        }
        cursor.moveToPosition(nowpage);
        int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
        Long id = cursor.getLong(fieldIndex);
        Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
        ImageView imageVIew = (ImageView) findViewById(R.id.imageView);
        imageVIew.setImageURI(imageUri);

        cursor.close();
    }


    public class MainTimerTask extends TimerTask {
        @Override
        public void run() {
            mHandler.post( new Runnable() {
                public void run() {
                    getContentsInfo(nowpage)  ;
                    nowpage = nowpage +  1;                      //実行間隔分を加算処理
                }
            });
        }
    }

}