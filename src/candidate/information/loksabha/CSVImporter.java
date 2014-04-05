package candidate.information.loksabha;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class CSVImporter {
	SQLiteDatabase myDB= null;
	Context ctx = null;
	public CSVImporter(SQLiteDatabase db, Context con) {
		myDB = db;
		ctx = con;
		try {	 
			   
			   /* Create a Table in the Database. */
			   myDB.execSQL("CREATE TABLE IF NOT EXISTS "
			     + MainActivity.tableName
			     + " (Name VARCHAR(100), Party VARCHAR(100), CriminalCases INT(3), Education VARCHAR(100), Age INT(3), Assets INT(100), Liabilities INT(100), Constituency VARCHAR(100));");
			   
			   /* Insert data to a Table
			   myDB.execSQL("INSERT INTO "
			     + MainActivity.tableName
			     + " (Field1, Field2)"
			     + " VALUES ('Saranga', 22);");*/
		}
		catch (Exception e) {
			Log.e("CSVImporter", "CSV Import Failure",e);
		}
	}
	public SQLiteDatabase importCSV() {
		BufferedReader is = new BufferedReader(new InputStreamReader(ctx.getResources().openRawResource(R.raw.data)));
		try {
			String line = "";         
            while ((line = is.readLine())!=null) {
                if (!line.startsWith(",")) {
                    String[] tokens = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
                    	myDB.execSQL("INSERT INTO "
               			     + MainActivity.tableName
               			     + " (Name, Party, CriminalCases, Education, Age, Assets, Liabilities, Constituency)"
               			     + " VALUES ('"+tokens[0]+"', '"+tokens[1]+"', "+tokens[2]+", '"+tokens[3]+"', "+tokens[4]+", '"+tokens[5]+"', '"+tokens[6]+"', '"+tokens[7]+"');");
                }
            }
		}
        catch (FileNotFoundException e) {
        	Log.e("CSVImporter","File Not Found",e);
        }
        catch (IOException e) {
        	Log.e("CSVImporter","IO Error",e);
        }
		return myDB;
	}
	
}
