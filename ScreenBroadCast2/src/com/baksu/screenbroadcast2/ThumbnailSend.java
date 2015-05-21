package com.baksu.screenbroadcast2;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

class ThumbnailSend extends Thread//implements Runnable
{
    Process p;
    static boolean run;
    static String roomNum;

    ThumbnailSend()
    {
        Log.d("msg", "run : " + run);
        run = true;
        Log.d("msg", "run : " + run);
    }

    public void run()
    {
        //run = true;
        Log.d("msg", "ThumbnailSend 쓰레드 시작");

        while(true)
        {
            try
            {
                // 썸네일 추출하는 부분
                p = Runtime.getRuntime().exec(
                        new String[] {
                                "su","-c","/system/bin/ffmpeg -f fbdev -i /dev/graphics/fb0 -an -r 1 -vframes 1 -y /sdcard/thumbnail.jpg" });

                Log.d("msg", "썸네일 추출 완료");
                Thread.sleep(10000);
                Log.d("msg", "run = " + run);

                // 썸네일 서버로 전송하는 부분

                roomNum = roomNum.trim();
                Log.d("msg", "방번호 : "+roomNum);
                //BitmapFactory.Options options = new BitmapFactory.Options();
                //options.inSampleSize = 8;
                Bitmap src = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getAbsolutePath() + "/thumbnail.jpg");
                Bitmap bitmap = Bitmap.createScaledBitmap(src, 360, 640, true);
                Log.d("msg", "비트맵 생성완료");

                ByteArrayOutputStream bao = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 95, bao);
                byte [] ba = bao.toByteArray();
                Log.d("msg", "바이트어레이 생성완료");

                String ba1 = Base64.encodeToString(ba, Base64.DEFAULT);
                Log.d("msg", "인코딩 완료");

                ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("image", ba1));
                nameValuePairs.add(new BasicNameValuePair("room", roomNum));

                try {
                    Log.d("msg", "http1");
                    HttpClient client = new DefaultHttpClient();
                    Log.d("msg", "http2");
                    HttpPost post = new HttpPost("http://211.189.127.226:3000/uploadThumbnail");
                    Log.d("msg", "http3");
                    post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    Log.d("msg", "http4");
                    client.execute(post);

                    Log.d("msg", "http 부분 끝");
                    //HttpEntity entity = response.getEntity();

                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (ClientProtocolException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                if(run == false)
                {
                    break;
                }
            }
            catch(Exception e)
            {
                Log.e("msg","s: Error : "+ e);
            }
        }
    }

    public void onStop()
    {
        Log.e("msg","쓰레드 스탑 함수 들어옴");
        run = false;
    }
}