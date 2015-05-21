package com.baksu.screenbroadcast2;

import java.io.*;
import java.lang.Process;

import android.app.*;
import android.content.*;
import android.content.res.*;
import android.media.MediaRecorder;
import android.os.*;
import android.util.*;

public class ScreenRecordService extends Service
{
	public static boolean isRecording = false;
	private Process p;
	private MediaRecorder recorder;
	private String result;

	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	public void onCreate() {
		super.onCreate();
	}

	public int onStartCommand(Intent intent, int flags, int startId)
	{
		result = intent.getStringExtra("roomNum");
		result = result.trim();
		Log.i("tq","result : " +result);
		Log.d("msg", "record part in");
		
		Handler handler = new Handler();
		copyAssets();
		
		if(isRecording == false)
		{
			isRecording = true;
			
			handler.post(new Runnable()
			{
				public void run()
				{
					try
					{

						// 녹음 코드
						String PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
						String RECORD_FILE = "/sound.mp3";
											
						recorder = new MediaRecorder();
						recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
						recorder.setAudioSamplingRate(44100);
						recorder.setAudioEncodingBitRate(128000);
						recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
						recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
						recorder.setOutputFile(PATH + RECORD_FILE);
									
						
						try
						{
							recorder.prepare();
							recorder.start();
							Log.d("msg", "sound record start : " + PATH + RECORD_FILE);
						}
						catch (IllegalStateException e)
						{
							Log.d("msg", "sound record error : " + e.getMessage());
							e.printStackTrace();
						}
						catch (IOException e)
						{
							Log.d("msg", "sound record error : " + e.getMessage());
							e.printStackTrace();
						}
						
						Log.i("tq","resultdfdfdfdfdfdf : " +result);
						Log.i("tq","/system/bin/ffmpeg -i /sdcard/sound.mp3 -f fbdev -i /dev/graphics/fb0 -ab 128k -ar 44100 -ac 2 -f flv -r 28 -vb 1M -s 360x640 rtmp://211.189.127.46/live/"+result+" &>> /sdcard/log.txt");
						// 녹화 코드
						p = Runtime.getRuntime().exec(new String[] { "su", "-c","/system/bin/mount -o remount,rw /system" });
						p.waitFor();
						p = Runtime.getRuntime().exec(new String[] { "su", "-c","/system/bin/cat /data/data/com.example.screenbroadcast2/ffmpeg > /system/bin/ffmpeg" });
						p.waitFor();
						p = Runtime.getRuntime().exec(new String[] { "su", "-c","/system/bin/chmod 700 /system/bin/ffmpeg" });
						p.waitFor();
						p = Runtime.getRuntime().exec(new String[] { "su", "-c","/system/bin/mount -o remount,ro /system" });
						p.waitFor();
						//createNotificationStop();
						p = Runtime.getRuntime().exec(
								new String[] {
										//"su","-c","/system/bin/ffmpeg -i /sdcard/sound.mp3 -f fbdev -i /dev/graphics/fb0 -ab 128k -ar 44100 -ac 2 -f flv -r 28 -vb 1M -s 360x640 rtmp://211.189.127.46/live/test2 &>> /sdcard/log.txt" });
										//"su","-c","/system/bin/ffmpeg -f fbdev -i /dev/graphics/fb0 -i /sdcard/sound.mp3 -ab 128k -ar 44100 -ac 2 -f flv -r 28 -vb 1M -s 360x640 rtmp://211.189.127.46/live/test2 &>> /sdcard/log.txt" });
										"su","-c","/system/bin/ffmpeg -i /sdcard/sound.mp3 -f fbdev -i /dev/graphics/fb0 -ab 128k -ar 44100 -ac 2 -f flv -r 28 -vb 1M -s 360x640 rtmp://211.189.127.46/live/"+result+" &>> /sdcard/log.txt" });
										//"su","-c","/system/bin/ffmpeg -i /sdcard/sound.mp3 -itsoffset 00:00:03.0 -f fbdev -i /dev/graphics/fb0 -ab 128k -ar 44100 -ac 2 -f flv -r 25 -vb 2M -s 360x640 -mpp 0:0 -map 1:0 rtmp://211.189.127.46/live/test2 &>> /sdcard/log.txt" });
						Log.d("msg", "video record start");
						
						Process p1 = Runtime.getRuntime().exec(
								new String[] {
										"su","-c","/system/bin/ffmpeg -f fbdev -i /dev/graphics/fb0 -an -r 1 -vframes 1 -y /sdcard/thumbnail.jpg" });
						
						Log.d("msg", "썸네일 추출 완료 첫번째");

					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			});
		}
		else
		{
			kill();
		}
		return Service.START_NOT_STICKY;
	}

	public void onDestroy()
	{
		kill();
	}

	private void copyAssets()
	{
		copyFFMPEG();
	}

	private void copyFFMPEG()
	{
		AssetManager assetManager = getApplicationContext().getAssets();
		InputStream in = null;
		OutputStream out = null;
		try {
			in = assetManager.open("ffmpeg");
			out = new FileOutputStream("/data/data/com.example.screenbroadcast2/ffmpeg");
			copyFile(in, out);
			in.close();
			in = null;
			out.flush();
			out.close();
			out = null;
		} catch (IOException e) {
			Log.e("tag", "Failed to copy asset file: ffmpeg", e);
		}
	}

	private void copyFile(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[1024];
		int read;
		while ((read = in.read(buffer)) != -1) {
			out.write(buffer, 0, read);
		}
	}

	private void kill()
	{
		if(isRecording)
		{
			try
			{
				DataOutputStream out = new DataOutputStream(p.getOutputStream());
				out.writeBytes("q\n");
				p.waitFor();
				out.flush();
				out.close();

				Log.d("msg", "video record stop");
			}
			catch (Exception e)
			{
				Log.d("msg", "video stop error : " + e.getMessage());
				// createNotificationStop(getString(R.string.service_fault),	
				// e.getMessage());
				e.printStackTrace();
			}
			
			try
			{								
				recorder.stop();
				recorder.release();
				recorder = null;
				Log.d("msg", "sound record stop");
			}
			catch (Exception e)
			{
				Log.d("msg", "sound stop error : " + e.getMessage());
				// createNotificationStop(getString(R.string.service_fault),	
				// e.getMessage());
				e.printStackTrace();
			}
			
			this.stopForeground(true);
			this.stopSelf();
			
			File delFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/sound.mp3");
			boolean del = delFile.delete();
			Log.d("msg", "sound file del : " + del);
			
			isRecording = false;
		}
	}

	private void createNotificationStop() {
		Intent intent = new Intent(this, ScreenRecordService.class);
		intent.putExtra("kill", "dammit");
		// createNotificationStop(getString(R.string.service_recording),
		// getString(R.string.stop_recording_video));
	}
}