package candidate.information.loksabha;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class MainActivity extends Activity {
	String TAG = "Election";
	public static final int MESSSAGE_INITIALIZED = 1;
	protected static final int CONSTITUENCY_SELECTED = 0;
	public static SQLiteDatabase myDB= null;
	public static String tableName = "myTable";
	UIHandler handler =  new UIHandler();
	private ArrayAdapter<String> conList = null;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		myDB = this.openOrCreateDatabase("DatabaseName", MODE_PRIVATE, null);		
		CSVImporter csv = new CSVImporter(myDB,this);
		new AsynchronousExecutor().execute(csv);
	}
	private OnItemClickListener conListClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			String constituency = conList.getItem(arg2);
			Message msg = new Message();
			msg.arg1 = MainActivity.CONSTITUENCY_SELECTED;
			msg.obj = constituency;
			handler.sendMessage(msg);
		}
		 
	};
	class AsynchronousExecutor extends AsyncTask<CSVImporter,Void,SQLiteDatabase> {

		@Override
		protected SQLiteDatabase doInBackground(CSVImporter... csvI) {
			return csvI[0].importCSV();
		}
		protected void onPostExecute(SQLiteDatabase dbs) {
			myDB = dbs;
			Message msg = new Message();
			msg.arg1 = MainActivity.MESSSAGE_INITIALIZED;
			handler.sendMessage(msg);
        }
		
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}


	private void listConstituencies() {
		conList = new ArrayAdapter<String>(this,R.layout.list_row_layout);
		ListView conListView = (ListView) this.findViewById(R.id.list_main);
		Cursor c = myDB.rawQuery("SELECT DISTINCT Constituency FROM " + tableName , null);
		int Column8 = c.getColumnIndex("Constituency");
		c.moveToFirst();
		if (c != null) {
		    do {
		     String constituency = c.getString(Column8);
		     conList.add(constituency);
		    }while(c.moveToNext());
		   }
		if (conList!=null) {
			conListView.setAdapter(conList);
			conListView.setOnItemClickListener(conListClickListener);
		}
	}
	public class UIHandler extends Handler {
		@Override
        public void handleMessage(Message msg) {
            int command = (int)msg.arg1;
            switch(command) {
            	case MainActivity.MESSSAGE_INITIALIZED: {
        			listConstituencies();
        			break;
            	}
            	case MainActivity.CONSTITUENCY_SELECTED: {
            		String constituency = (String) msg.obj;
            		Intent intent = new Intent(MainActivity.this,CandidatesActivtity.class);
            		intent.putExtra("constituency",constituency);
            		MainActivity.this.startActivity(intent);
            		break;
            	}
        	}
            
		}
	}
}
