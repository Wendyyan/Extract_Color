package com.zyf.extractcolor;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends AppCompatActivity {

    private ImageView ivFloor, ivColor;
    private TextView tvColor;
    private Bitmap bitmap;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ivFloor = (ImageView)findViewById(R.id.iv_floor);
        ivColor = (ImageView)findViewById(R.id.iv_color);
        tvColor = (TextView)findViewById(R.id.tv_color);

        ivFloor.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int x = (int) event.getX();
                int y = (int) event.getY();
                //根据坐标获取
                Bitmap bitmap = ((BitmapDrawable)ivFloor.getDrawable()).getBitmap();
                if (x < bitmap.getWidth() && y < bitmap.getHeight()) {
                    int pixel = bitmap.getPixel(x,y);//获取颜色
                    int redValue = Color.red(pixel);
                    int greenValue = Color.green(pixel);
                    int blueValue = Color.blue(pixel);
                    tvColor.setText("颜色：" + redValue + ", " + greenValue + ", " + blueValue);
                    ivColor.setBackgroundColor(Color.rgb(redValue, greenValue, blueValue));
                }
                return true;
            }
        });

        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                bitmap = returnBitmap("https://hbimg.huabanimg.com/efea83a54ffd6bb2135cfe33451ff5b368c82b5a138232-wK6giD_fw658");

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ivFloor.setImageBitmap(bitmap);
                        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                            @Override
                            public void onGenerated(Palette palette) {
                                Palette.Swatch vibrant = palette.getVibrantSwatch();
                                if (vibrant != null) {
                                    tvColor.setTextColor(vibrant.getTitleTextColor());
                                    tvColor.setText("" + vibrant.getRgb());
                                    ivColor.setBackgroundColor(vibrant.getRgb());
                                }

                            }
                        });
                    }
                });
            }
        }).start();
    }

    /**
     * 根据图片的url路径获得Bitmap对象
     * @param url
     * @return
     */
    private Bitmap returnBitmap(String url) {
        Bitmap bm = null;
        try {
            URL iconUrl = new URL(url);
            URLConnection conn = iconUrl.openConnection();
            HttpURLConnection http = (HttpURLConnection) conn;

            int length = http.getContentLength();

            conn.connect();
            // 获得图像的字符流
            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is, length);
            bm = BitmapFactory.decodeStream(bis);
            bis.close();
            is.close();// 关闭流
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return bm;
    }

    @Override
    protected void onDestroy() {
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
            bitmap = null;
        }
        super.onDestroy();
    }
}
