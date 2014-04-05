package candidate.information.loksabha;


import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class DescriptionActivity extends Activity {
	String constituency = null;
	String name = null;
	String party = null;
	int cases = 0;
	int assets = 0;
	int lia = 0;
	int age = 0;
	int score = 0;
	String liaS = null;
	String assetsS = null;
	String edu = null;
	SQLiteDatabase myDB = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.description_layout);
		Bundle extras = getIntent().getExtras();
		constituency = extras.getString("constituency");
		name = extras.getString("name");
		party = extras.getString("party");
		myDB = MainActivity.myDB;
		TextView text = (TextView) findViewById(R.id.main_screen_text);
		text.setText(name);
		Cursor c = myDB.rawQuery("SELECT DISTINCT * FROM " + MainActivity.tableName + " WHERE Constituency = "+"'"+constituency+"'"+" AND Party = "+"'"+party+"'"+" AND Name = "+"'"+name+"'", null);
		int c3 = c.getColumnIndex("CriminalCases");
		int c4 = c.getColumnIndex("Education");
		int c5 = c.getColumnIndex("Age");
		int c6 = c.getColumnIndex("Assets");
		int c7 = c.getColumnIndex("Liabilities");
		c.moveToFirst();
		if (c != null) {
		    do {
		     cases = c.getInt(c3);
		     edu = c.getString(c4);
		     age = c.getInt(c5);
		     assets = c.getInt(c6);
		     assetsS = Scorer.convertMoneyToString(assets);
		     lia = c.getInt(c7);
		     liaS = Scorer.convertMoneyToString(lia);
		     if (c.isLast()) break;
		    }while(c.moveToNext());
		   }
		display();
		
	}
	public void display() {
		score = 0;
		TableLayout tl=(TableLayout)findViewById(R.id.desc_table);
        tl.addView(addRow("Age: ",age,(age<=Scorer.ageLimit)));
        String[] tokens = Scorer.partyLimit.split(">");
        boolean p = false;
        for (String t: tokens) {
        	if (party.equals(t)) {
        		p = true;
        		break;
        	}
        }
        tl.addView(addRow("Party: ",party,p));
        tl.addView(addRow("Criminal Cases: ",cases,(cases<=Scorer.maxCases)));
        tl.addView(addRow("Education: ",edu,Scorer.getEdu(edu)>=Scorer.eduLimit));
        tl.addView(addRow("Assets: ",assetsS,(assets-lia>=100000 && assets-lia<=Scorer.assetLimit)));
        tl.addView(addRow("Liabilities: ",liaS,(lia<=assets/10)));
        TextView txt = (TextView)this.findViewById(R.id.score_text);
        txt.setText("Score "+ score+ "/6");
	}
	public TableRow addRow(String title, String value,boolean tick) {
		TableRow tr =  new TableRow(this);
        TextView c1 = new TextView(this);
        c1.setText(title);
        TextView c2 = new TextView(this);
        c2.setText(value);
        ImageView c3 = new ImageView(this);
        if (tick) {
        	score++;
        	c3.setImageResource(R.drawable.tick);
        }
        else c3.setImageResource(R.drawable.cross);
        tr.addView(c1);
        tr.addView(c2);
        tr.addView(c3);
        return tr;
	}
	public TableRow addRow(String title, int value, boolean tick) {
        return addRow(title,String.valueOf(value),tick);
	}
}
