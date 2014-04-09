package candidate.information.loksabha;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class ScoringActivity extends Activity {
	private SeekBar ageSeek;
	private TextView ageText;
	private SeekBar assetsSeek;
	private TextView assetsText;
	private SeekBar casesSeek;
	private TextView casesText;
	private SeekBar eduSeek;
	private TextView eduText;
	private ImageView submit;
	private CheckBox inc_c, bjp_c, aap_c,ind_c;
	private int age = 50;
	private int cases = 0;
	private int assets = 50000000;
	private int edu = 12;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scoring_layout);
		inc_c   = (CheckBox) findViewById(R.id.check_inc);
        bjp_c      = (CheckBox) findViewById(R.id.check_bjp);
        aap_c    = (CheckBox) findViewById(R.id.check_aap);
        ind_c = (CheckBox) findViewById(R.id.check_ind);
		submit = (ImageView) findViewById(R.id.scoring_submit);
		ageSeek = (SeekBar) findViewById(R.id.age_seek);
		ageText = (TextView) findViewById(R.id.age_seek_text);
		assetsSeek = (SeekBar) findViewById(R.id.assets_seek);
		assetsText = (TextView) findViewById(R.id.assets_seek_text);
		casesSeek = (SeekBar) findViewById(R.id.cases_seek);
		casesText = (TextView) findViewById(R.id.cases_seek_text);
		eduSeek = (SeekBar) findViewById(R.id.edu_seek);
		eduText = (TextView) findViewById(R.id.edu_seek_text);
		age = Scorer.ageLimit;
		edu = Scorer.eduLimit;
		cases = Scorer.maxCases;
		assets = Scorer.assetLimit;
		ageSeek.setProgress(Scorer.ageLimit-25);
		eduSeek.setProgress(Scorer.eduLimit);
		casesSeek.setProgress(Scorer.maxCases);
		assetsSeek.setProgress(Scorer.assetLimit/100000);
		String[] tokens = Scorer.partyLimit.split(">");
		for (String t: tokens) {
			if (t.equals("INC")) inc_c.setChecked(true);
			else if (t.equals("BJP")) bjp_c.setChecked(true);
			else if (t.equals("AAP")) aap_c.setChecked(true);
			else if (t.equals("IND")) ind_c.setChecked(true);
		}
		ageText.setText((ageSeek.getProgress()+25) + " years");
		casesText.setText(casesSeek.getProgress() + " cases");
		eduText.setText(Scorer.convertEduNtoString(eduSeek.getProgress()));
		assetsText.setText(Scorer.convertMoneyToString(assetsSeek.getProgress()*100000));
		setListeners();
	}
	public void setListeners() {
		ageSeek.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
				// TODO Auto-generated method stub
				age = arg1+25;
			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				ageText.setText(age + " years");
				
			}
			
		});
		assetsSeek.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				assets = progress*100000;
				
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				assetsText.setText(Scorer.convertMoneyToString(assets));
				
			}
			
		});
		casesSeek.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				cases = progress;
				
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				casesText.setText(cases+" cases");
				
			}
			
		});
		eduSeek.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				edu = progress;
				
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				eduText.setText(Scorer.convertEduNtoString(edu));
				
			}
			
		});
		submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				String party= "";
				if (inc_c.isChecked()) party = party+"INC>";
				if (bjp_c.isChecked()) party = party+"BJP>";
				if (ind_c.isChecked()) party = party+"IND>";
				if (aap_c.isChecked()) party = party+"AAP>";
				Scorer.setParams(age, party ,assets, edu, cases);
				Intent returnIntent = new Intent();
				setResult(RESULT_OK,returnIntent);     
				finish();
			}
			
		});
	}
}
