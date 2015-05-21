package com.baksu.screenbroadcast2;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

@SuppressLint("NewApi")
public class CamView extends SurfaceView implements SurfaceHolder.Callback{
	private SurfaceHolder _holder;
	private Camera _camera = null;
	
    private int                 mFrameWidth;
    private int                 mFrameHeight;
	
	public CamView(Context context){
		super(context);
		_holder = getHolder();
		_holder.addCallback(this);
		_holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}
	public CamView(Context context, AttributeSet attrs){
		super(context,attrs);
	}
	public CamView(Context context, AttributeSet attrs, int defStyle){
		super(context, attrs, defStyle);
		
	}
//
//    public void surfaceCreated(SurfaceHolder holder) {
//        Log.i(TAG, "surfaceCreated");
//        mCamera = Camera.open();
//        
//        mCamera.setPreviewCallback(new PreviewCallback() {
//            public void onPreviewFrame(byte[] data, Camera camera) {
//                synchronized (SampleViewBase.this) {
//                    mFrame = data;
//                    SampleViewBase.this.notify();
//                }
//            }
//        });
//        (new Thread(this)).start();
//    }
	public void surfaceCreated(SurfaceHolder holder){
		_camera = Camera.open();
		try{
			_camera.setPreviewDisplay(_holder);
		}
		
		catch(Exception e){
			_camera.release();
			_camera=null;
		}
	}
    public int getFrameWidth() {
        return mFrameWidth;
    }

    public int getFrameHeight() {
        return mFrameHeight;
    }

	public void surfaceChanged(SurfaceHolder holder,int format, int width, int height){
		
		 if( _camera != null) {
			 Log.i("Test","카메라 시작");
	            Camera.Parameters _params = _camera.getParameters();

	            List<Camera.Size> sizes = _params.getSupportedPreviewSizes();
	            mFrameWidth = width;
	            mFrameHeight = height;
	            Log.i("Test",mFrameHeight+"mFrameWidth"+mFrameWidth);

	            // selecting optimal camera preview size
	            {
	                double minDiff = Double.MAX_VALUE;
	                for (Camera.Size size : sizes) {
	                    if (Math.abs(size.height - height) < minDiff) {
	                        mFrameWidth = size.width;
	                        mFrameHeight = size.height;
	                        minDiff = Math.abs(size.height - height);
	                    }
	                }
	            }
	            Log.i("Test",getFrameWidth()+"세로"+getFrameHeight());
	            _params.setPreviewSize(getFrameWidth(), getFrameHeight());
	            _camera.setParameters(_params);
	         //   try {
	       //     	_camera.setPreviewDisplay(null);
	          //  	} catch (IOException e) {
//					Log.e("Test", "mCamera.setPreviewDisplay fails: " + e);
	//	}
	            _camera.startPreview();
	            
	        }
		Log.i("Test","sas카메라널d");
//		
//		Camera.Parameters _params = _camera.getParameters();
//		_params.setPreviewSize(width, height);
//		_camera.setParameters(_params);
//		_camera.startPreview();
	}
	
	public boolean Capture(Camera.PictureCallback jpegHandler){
		if(_camera != null){
			_camera.takePicture(null,null,jpegHandler);
			return true;
		}
		else{
			return false;
		}
	}
	
	public void surfaceDestroyed(SurfaceHolder holder){
		_camera.stopPreview();
		_camera.release();
		_camera = null;
	}
}

