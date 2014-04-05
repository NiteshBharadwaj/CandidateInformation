package candidate.information.loksabha;

import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class CandidatesActivtity extends Activity {
	private String constituency = null; 
	private ArrayAdapter<String> canList = null;
	private SQLiteDatabase myDB = null;
	private HashMap<String,Integer> scoreList= new HashMap<String,Integer>();
	private ScoreComparator comparator = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.candidates_layout);
		Bundle extras = getIntent().getExtras();
		constituency = extras.getString("constituency");
		TextView text = (TextView) findViewById(R.id.main_screen_text);
		text.setText(constituency);
		myDB = MainActivity.myDB;
		canList = new ArrayAdapter<String>(this,R.layout.list_row_layout);
		listCandidates(constituency,"",false);
		ListView canListView = (ListView) this.findViewById(R.id.list_candi);
		if (canList!=null) {
			canListView.setAdapter(canList);
			canListView.setOnItemClickListener(canListClickListener);
		}
		computeScores(constituency);
		comparator = new ScoreComparator();
		setListeners();
		
	}
	
	private void setListeners() {
		ImageView view = (ImageView)this.findViewById(R.id.image_oldie);
		view.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				
				canList.clear();
				listCandidates(constituency,"Age",true);
			}
			
		});
		view = (ImageView)this.findViewById(R.id.image_book);
		view.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				
				canList.clear();
				listCandidates(constituency,"Education",false);
			}
			
		});
		view = (ImageView)this.findViewById(R.id.image_crime);
		view.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				
				canList.clear();
				listCandidates(constituency,"CriminalCases",true);
			}
			
		});
		view = (ImageView)this.findViewById(R.id.image_money);
		view.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				
				canList.clear();
				listCandidates(constituency,"Assets",false);
			}
			
		});
		view = (ImageView)this.findViewById(R.id.image_score);
		view.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(CandidatesActivtity.this,ScoringActivity.class);
				startActivityForResult(i,100);
			}
			
		});
	}
	private OnItemClickListener canListClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			String s = canList.getItem(arg2);
			s = s.split(">")[0]+">";
			String[] tokens = s.split("<");
			String name = tokens[0].substring(0,tokens[0].length()-1);
			String party = tokens[1].substring(0,tokens[1].length()-1);
			
			Intent i = new Intent(CandidatesActivtity.this,DescriptionActivity.class);
			i.putExtra("constituency", constituency);
			i.putExtra("name", name);
			i.putExtra("party", party);
			startActivity(i);
		}
		 
	};
	public void listCandidates(String constituency, String sortCriterion, boolean sortOrder) {
		if (myDB==null) return;
		addCandidate(constituency,"BJP",sortCriterion,sortOrder);
		addCandidate(constituency,"INC",sortCriterion,sortOrder);
		addCandidate(constituency,"AAP",sortCriterion,sortOrder);
		addCandidate(constituency,"",sortCriterion,sortOrder);	
	}
	private void addCandidate(String constituency, String party, String criteria, boolean asc) {
		Cursor c = null; 
		boolean parB = true;
		if (party.equals("")) parB = false;
		boolean criB = true;
		if (criteria.equals("")) criB = false;
		if (parB) {
			if (criB) {
				if (asc)
					c = myDB.rawQuery("SELECT DISTINCT * FROM " + MainActivity.tableName + " WHERE Constituency = "+"'"+constituency+"'"+" AND Party = '"+party+"'"+" ORDER BY " +criteria+" ASC", null);
				else c = myDB.rawQuery("SELECT DISTINCT * FROM " + MainActivity.tableName + " WHERE Constituency = "+"'"+constituency+"'"+" AND Party = '"+party+"'"+" ORDER BY " +criteria+" DESC", null);
			}
			else {
				c = myDB.rawQuery("SELECT DISTINCT * FROM " + MainActivity.tableName + " WHERE Constituency = "+"'"+constituency+"'"+" AND Party = '"+party+"'", null);
			}
		}
		else {
			if (criB) {
				if (asc) {
					c = myDB.rawQuery("SELECT DISTINCT * FROM " + MainActivity.tableName + " WHERE Constituency = "+"'"+constituency+"'"+" ORDER BY " +criteria+" ASC", null);
				} else c = myDB.rawQuery("SELECT DISTINCT * FROM " + MainActivity.tableName + " WHERE Constituency = "+"'"+constituency+"'"+" ORDER BY " +criteria+" DESC", null);
			}
			else {
				c = myDB.rawQuery("SELECT DISTINCT * FROM " + MainActivity.tableName + " WHERE Constituency = "+"'"+constituency+"'", null);
			}
		}
		
		int c1 = c.getColumnIndex("Name");
		int c2 = c.getColumnIndex("Party");
		int c3 = c.getColumnIndex("CriminalCases");
		int c4 = c.getColumnIndex("Education");
		int c5 = c.getColumnIndex("Age");
		int c6 = c.getColumnIndex("Assets");
		c.moveToFirst();
		if (c != null) {
		    do {
		     String name = c.getString(c1);
		     party = c.getString(c2);
		     int cases = c.getInt(c3);
		     String edu = c.getString(c4);
		     int age = c.getInt(c5);
		     int assets = c.getInt(c6);
		     String pop = name+" "+"<"+party+">";
		     if (criB) {
		    	 if (criteria.equals("Education")) pop = pop+" "+edu;
		    	 else if (criteria.equals("Age")) pop = pop+" "+age+" years";
		    	 else if (criteria.equals("CriminalCases")) pop = pop+" "+cases+" cases";
		    	 else if (criteria.equals("Assets")) {
			    	 DecimalFormat f = new DecimalFormat("##.00");
			    	 String assetsS = "";
				     if (assets<100000) assetsS = "Rs " + assets + " only";
				     else if (assets<10000000) {
				    	 assetsS = "Rs " +  f.format(assets/100000.00) + " lakhs";
				     }
				     else {
				    	 assetsS = "Rs " + f.format(assets/10000000.00) + " crores";
				     }
				     pop = pop +" "+assetsS;
		    	 }
		    	 
		     }
		     if (parB) canList.add(pop);
		     else {
		    	 if (!(party.equals("AAP")|| party.equals("BJP") || party.equals("INC"))) canList.add(pop);
		     }
		     if (c.isLast()) break;
		    }while(c.moveToNext());
		   }
	}
	
	public void computeScores(String constituency) {
		if (myDB==null) return;
		Cursor c = myDB.rawQuery("SELECT DISTINCT * FROM " + MainActivity.tableName + " WHERE Constituency = "+"'"+constituency+"'", null);
		int c1 = c.getColumnIndex("Name");
		int c2 = c.getColumnIndex("Party");
		int c3 = c.getColumnIndex("CriminalCases");
		int c4 = c.getColumnIndex("Education");
		int c5 = c.getColumnIndex("Age");
		int c6 = c.getColumnIndex("Assets");
		int c7 = c.getColumnIndex("Liabilities");
		c.moveToFirst();
		if (c != null) {
		    do {
		     int score = 0;
		     String name = c.getString(c1);
		     String party = c.getString(c2);
		     int cases = c.getInt(c3);
		     String edu = c.getString(c4);
		     int age = c.getInt(c5);
		     int assets = c.getInt(c6);
		     int lia = c.getInt(c7);
		     Scorer scorer = new Scorer(party,cases,age,assets,lia,edu);
		     score = scorer.score();
		     String pop = name+" "+"<"+party+">";
		     scoreList.put(pop, score);
		     if (c.isLast()) break;
		    }while(c.moveToNext());
		   }
		
	}
	public class ScoreComparator implements Comparator<String> {

		@Override
		public int compare(String lhs, String rhs) {
			lhs = lhs.split(">")[0]+">";
			rhs = rhs.split(">")[0]+">";
			if (scoreList.containsKey(lhs) && scoreList.containsKey(rhs)) {
				int s1 = scoreList.get(lhs);
				int s2 = scoreList.get(rhs);
				return s2-s1;
			}
			return 0;
		}
		
	}
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		  if (requestCode == 100) {

		     if(resultCode == RESULT_OK){      
		    	computeScores(constituency);
		 		canList.sort(comparator);
		     }
		     if (resultCode == RESULT_CANCELED) {    
		         //Write your code if there's no result
		     }
		  }
	}
}
