package com.example.wen.com1614080901115;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;


public class Download extends SecondActivity
        implements View.OnClickListener {

    public static final String TAG = Download.class.getSimpleName();
    public static final String WEB = "http://www.bing.com";
    public static final String IMAGE_URL_PREFIX = "https://raw.githubusercontent.com/hzuapps/android-labs/master/app/src/main/res/drawable/";

    static String[] imageNames = {"image_bmp.png", "image_gif.png", "image_ico.png",
            "image_jpeg.png", "image_png.png", "image_tiff.png"};

    private Button check;
    private Button xiazai;
    private EditText download;
    private TextView view;

    // “images”子目录
    private File mImagesDir;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.download);

        check = (Button) findViewById(R.id.check);
        xiazai = (Button) findViewById(R.id.xiazai);
        download = (EditText) findViewById(R.id.download);
        view = (TextView) findViewById(R.id.view);

        check.setOnClickListener(this);
        xiazai.setOnClickListener(this);

        // 获取内部存储目录: files
        File mPrivateRootDir = getFilesDir();
        // 获取内部存储子目录: files/images
        mImagesDir = new File(mPrivateRootDir, "images");
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.check) {
            checkNetworkState(); // 检查网络
        } else if (view.getId() == R.id.xiazai) {
            downloadImages(); // 下载图片
        }
    }

    // 检查网络状态
    private void checkNetworkState() {
        // 取得连接管理器
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // 检查当前激的网络
        assert connMgr != null;
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        boolean mConnected;
        if (networkInfo != null && networkInfo.isConnected()) {
            mConnected = true;
        } else {
            mConnected = false;
        }

        String types = "";

        // 检查Wi-Fi
        networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        boolean isWifiConn = networkInfo != null && networkInfo.isConnected();
        types += isWifiConn ? "Wi-Fi" : "";

        // 检查数据网络
        networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        boolean isMobileConn = networkInfo != null && networkInfo.isConnected();
        types += isMobileConn ? "流量" : "";

        // 检查蓝牙
        networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_BLUETOOTH);
        boolean isBluetoothConn = networkInfo != null && networkInfo.isConnected();
        types += isBluetoothConn ? ", 蓝牙" : "";

        check.setTextColor(mConnected ? Color.GREEN : Color.RED);
        check.setText(mConnected ? "网络正常 (" +types + ")" : "网络未连接!");
    }

    // 下载图片
    private void downloadImages() {
        // 创建下载器
        DownloadFile mFileDownloader = new DownloadFile(new DownloadFile.OnImageDownloadListener() {
            @Override
            public void onError(String error) {
                Toast.makeText(Download.this, error, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onProgressChange(int percent) {
                Log.i(TAG, "当前进度 = " + percent);
            }

            @Override
            public void onComplete(Bitmap bitmap, String imageUrl) {
                // 下载的图片格式为PNG
                final Bitmap.CompressFormat format = Bitmap.CompressFormat.PNG;
                // 解析出原始文件名
                String filename = imageUrl.replace(IMAGE_URL_PREFIX, "");
                final File imageFile = new File(mImagesDir, filename);
                // 将文件保存到磁盘中
                DownloadFile.writeToDisk(imageFile, bitmap, new DownloadFile.OnBitmapSaveListener() {
                    @Override
                    public void onBitmapSaved() {
                        Toast.makeText(Download.this, "文件已保存: " + imageFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onBitmapSaveError(String error) {
                        Toast.makeText(Download.this, error, Toast.LENGTH_LONG).show();
                    }
                }, format, false);
            }
        });

        // 下载所有文件
        for(String imageName : imageNames) {
            String imageUrl = IMAGE_URL_PREFIX + imageName;
            mFileDownloader.download(imageUrl, true);
        }
    }

}
