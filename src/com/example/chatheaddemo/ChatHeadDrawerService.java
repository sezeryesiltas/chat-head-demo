package com.example.chatheaddemo;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.IBinder;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ChatHeadDrawerService extends Service {

	private WindowManager mWindowManager;
	private View mChatHead;
	private ImageView mChatHeadImageView;
	private TextView mChatHeadTextView;
	private LinearLayout mLayout;
	private static int screenWidth;
	private static int screenHeight;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@SuppressLint("NewApi")
	@Override
	public void onCreate() {
		super.onCreate();

//		Typeface tf = Typeface.createFromAsset(getAssets(), "Roboto-Light.ttf");

		mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
		Display display = mWindowManager.getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		screenWidth = size.x;
		screenHeight = size.y;

		LayoutInflater inflater = LayoutInflater.from(this);
		mChatHead = inflater.inflate(R.layout.chathead_view, null);
		mChatHeadImageView = (ImageView) mChatHead
				.findViewById(R.id.chathead_imageview);
		mChatHeadTextView = (TextView) mChatHead
				.findViewById(R.id.chathead_textview);
		mLayout = (LinearLayout) mChatHead
				.findViewById(R.id.chathead_linearlayout);

//		mChatHeadTextView.setTypeface(tf);

		final WindowManager.LayoutParams parameters = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.WRAP_CONTENT, // Width
				WindowManager.LayoutParams.WRAP_CONTENT, // Height
				WindowManager.LayoutParams.TYPE_PHONE, // Type
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, // Flag
				PixelFormat.TRANSLUCENT // Format
		);

		parameters.x = 0;
		parameters.y = 50;
		parameters.gravity = Gravity.TOP | Gravity.LEFT;

		// Drag support!
		mChatHeadImageView.setOnTouchListener(new OnTouchListener() {

			int initialX, initialY;
			float initialTouchX, initialTouchY;

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					initialX = parameters.x;
					initialY = parameters.y;
					initialTouchX = event.getRawX();
					initialTouchY = event.getRawY();
					Toast.makeText(getApplication(),
							"Drag it to here to remove!", Toast.LENGTH_SHORT)
							.show();
					return true;

				case MotionEvent.ACTION_MOVE:
					mChatHeadTextView.setVisibility(View.GONE);
					parameters.x = initialX
							+ (int) (event.getRawX() - initialTouchX);
					parameters.y = initialY
							+ (int) (event.getRawY() - initialTouchY);
					mWindowManager.updateViewLayout(mChatHead, parameters);
					return true;

				case MotionEvent.ACTION_UP:

					if (parameters.y > screenHeight * 0.6) {
						mChatHead.setVisibility(View.GONE);
						Toast.makeText(getApplication(), "Removed!",
								Toast.LENGTH_SHORT).show();
						stopSelf();
					}

					if (parameters.x < screenWidth / 2) {
						mLayout.removeAllViews();
						mLayout.addView(mChatHeadImageView);
						mLayout.addView(mChatHeadTextView);
						mChatHeadTextView.setVisibility(View.VISIBLE);

					} else { // Set textView to left of image
						mLayout.removeAllViews();
						mLayout.addView(mChatHeadTextView);
						mLayout.addView(mChatHeadImageView);
						mChatHeadTextView.setVisibility(View.VISIBLE);
					}
					return true;

				default:
					return false;
				}
			}
		});

		mWindowManager.addView(mChatHead, parameters);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mChatHead != null)
			mWindowManager.removeView(mChatHead);
	}
}
