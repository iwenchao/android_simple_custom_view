package com.wenchaos.draw;

import java.util.ArrayList;
import java.util.Random;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.example.test.R;
import com.wenchaos.draw.WeightView.IActionEndListener;

public class MainActivity extends Activity implements IActionEndListener{
	private WeightView weightView;
	private Button loadMore,pullToRefresh;
	private ArrayList<Float> mPoints = new ArrayList<Float>();
	private ArrayList<String> mXAxis = new ArrayList<String>();
	private int posCenter;
	private int COUNT = 5;
	private int  mXAxisIndex = 100;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		
		
		weightView = (WeightView) findViewById(R.id.test_view);
		loadMore = (Button) findViewById(R.id.load_more);
		pullToRefresh = (Button) findViewById(R.id.PullToRefresh);
		loadMore.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mPoints.addAll(0,loadMoreDataF());
				mXAxis.addAll(0,loadMoreDataS());
				weightView.setPointes(mPoints, mXAxis, posCenter+(COUNT));
			}
		});
		pullToRefresh.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mPoints.addAll(0,loadMoreDataF());
				mXAxis.addAll(0,loadMoreDataS());
				weightView.setPointes(mPoints, mXAxis, mPoints.size());
			}
		});
		weightView.setOnEndListener(this);
		
	}
	
	
	private ArrayList<Float> loadMoreDataF(){
		ArrayList<Float> data = new ArrayList<Float>();
		for (int i = 0; i < COUNT; i++) {
			float k = produceFloat();
			data.add(k);
			Log.v("WEI", "new 第 "+ i +" 个----"+k );
		}
		return data;
		
	}
	private ArrayList<String> loadMoreDataS(){
		ArrayList<String> data = new ArrayList<String>();
		for (int i = 0; i < COUNT; i++) {
			String k = produceString();
			data.add(k);
		}
		return data;
		
	}
	
	private Float produceFloat(){
		int max=150;
	    int min=-5;
	    Random random = new Random();
	    int s = random.nextInt(max)%(max-min+1) + min;
		return (float)s;
		
	}
	private String produceString(){
		String result = ""+Math.abs(mXAxisIndex)+"/"+Math.abs(mXAxisIndex);
		mXAxisIndex--;
		return result;
	}


	@Override
	public void actionEnd(int position) {
		Log.v("WEI", "停止后position："+position );
		posCenter = position;
	}
}
