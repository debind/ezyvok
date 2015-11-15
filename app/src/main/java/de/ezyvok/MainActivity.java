package de.ezyvok;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.channels.FileChannel;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.res.Resources;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import de.ezyvok.vok;


public class MainActivity extends Activity {
	int         CurrentState;
	int			Direction;
	String      line;
	String 	    word1;
	String      word2;
	vok         myVok;
	InputStream myFile;
	public float mPreviousX;
	Resources myResource;
	int       myLection = 0;
	/** Sample filters array */
	final String[] mFileFilter = { ".ezv" };
	
	private View.OnTouchListener mLayoutTouch = new View.OnTouchListener() {
		
		@Override
		public boolean onTouch(View arg0, MotionEvent arg1) {
			float X;	
			X =  arg1.getX();

			switch(arg1.getAction()){
				case MotionEvent.ACTION_MOVE:   
					if ((X - mPreviousX) > 6.0){
						mForeward();
						mPreviousX=X; 
					}
					if ((X - mPreviousX) < -6.0){
						mBackward();
						mPreviousX=X; 
					}
					break;
				case MotionEvent.ACTION_UP:   
					mForeward();
					break;
				case MotionEvent.ACTION_DOWN:
					mPreviousX = X;	
					break;
			}		
			return true;
		}
	};

	OnHandleFileListener mLoadFileListener = new OnHandleFileListener() {
		@Override
		public void handleFile(final String filePath) {
			Toast.makeText(MainActivity.this, "laden: " + filePath, Toast.LENGTH_SHORT).show();
			SetNewLection(filePath);
		}
	};

	OnHandleFileListener mSaveFileListener = new OnHandleFileListener() {
		@Override
		public void handleFile(final String filePath) {
			Toast.makeText(MainActivity.this, "speichern: " + filePath, Toast.LENGTH_SHORT).show();
		}
	};	
	
	public void mBackward(){
	    TextView EnglishText;
	    TextView GermanText;
		TextView InfoText;
	    if (CurrentState == 1)   {
		   word1 = myVok.GetLang1();
		   word2 = myVok.GetLang2();
		   if (word1 == null) { word1 = ""; }
		   if (word2 == null) { word1 = ""; }
		   GermanText = (TextView) findViewById(R.id.TEXTVIEW_LANGUAGE2);
		   GermanText.setText(word2);    
		   EnglishText = (TextView) findViewById(R.id.TEXTVIEW_LANGUAGE1);
		   EnglishText.setText(word1);                     	   
		   myVok.Prev();
		   CurrentState = 0;
	    }
	    else{
		   word1 = myVok.GetLang1();
		   word2 = myVok.GetLang2();
		   if (word1 == null) { word1 = ""; }
		   if (word2 == null) { word1 = ""; }
	       InfoText = (TextView) findViewById(R.id.TEXTVIEW_INFO);
	       InfoText.setText(myVok.GetCurrent()+"/"+myVok.GetNumberOfVoks());     
	       GermanText = (TextView) findViewById(R.id.TEXTVIEW_LANGUAGE2);
	       GermanText.setText("");                     	   
	       EnglishText = (TextView) findViewById(R.id.TEXTVIEW_LANGUAGE1);
	       EnglishText.setText(word1);                     	   
		   CurrentState = 1;
	    }								
	 }
	
	public void mForeward(){
	    TextView EnglishText;
	    TextView GermanText;
		TextView InfoText;
	    if (CurrentState == 1)   {
		   word1 = myVok.GetLang1();
		   word2 = myVok.GetLang2();
		   if (word1 == null) { word1 = ""; }
		   if (word2 == null) { word1 = ""; }
		   GermanText = (TextView) findViewById(R.id.TEXTVIEW_LANGUAGE2);
		   GermanText.setText(word2);    
		   EnglishText = (TextView) findViewById(R.id.TEXTVIEW_LANGUAGE1);
		   EnglishText.setText(word1);                     	   
		   myVok.Next();
		   CurrentState = 0;
	    }
	    else{
		   word1 = myVok.GetLang1();
		   word2 = myVok.GetLang2();
		   if (word1 == null) { word1 = ""; }
		   if (word2 == null) { word1 = ""; }
	       InfoText = (TextView) findViewById(R.id.TEXTVIEW_INFO);
	       InfoText.setText(myVok.GetCurrent()+"/"+myVok.GetNumberOfVoks());     
	       GermanText = (TextView) findViewById(R.id.TEXTVIEW_LANGUAGE2);
	       GermanText.setText("");                     	   
	       EnglishText = (TextView) findViewById(R.id.TEXTVIEW_LANGUAGE1);
	       EnglishText.setText(word1);                     	   
		   CurrentState = 1;
	    }								
	 }
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        View mLayout = (View)findViewById(R.id.LAYOUT);
        mLayout.setOnTouchListener(mLayoutTouch);
   }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
    	word1 = "";
    	word2 = "";
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionmenu, menu);
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.menu2, menu);
        CreateOwnDirectory();
        return true;        
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.ACTION_MENU_LANG:
            	myVok.ChangeLanguage();            	
     		    TextView Lang = (TextView) findViewById(R.id.TEXTVIEW_DIRECTION);
    		    Lang.setText(myVok.GetLanguageByText());                   	   
                return true;
			case R.id.ACTION_MENU_DIFFICULT:
				if ((word1 == "") || (word1 == null) || (word2 == "") || (word2 == null) )
				{
					Toast.makeText(MainActivity.this, "Fehler: nichts zu sichern !", Toast.LENGTH_LONG).show();
				}
				else
				{
					SaveToDifficult(word1, word2);
				}
				return true;
            case R.id.LOADVOK:          
            	OpenFileDlg();
                return true;
            case R.id.SAVETODIFFICULT:      
            	if ((word1 == "") || (word1 == null) || (word2 == "") || (word2 == null) )
            	{
            		Toast.makeText(MainActivity.this, "Fehler: nichts zu sichern !", Toast.LENGTH_LONG).show();
            	}
            	else
            	{
                	SaveToDifficult(word1, word2);
            	}

                return true;
            case R.id.LOADDIFFICULT:          
            	String DiffFilename  = Environment.getExternalStorageDirectory()+"/ezyvok/schwer.ezv";
            	File fdiff           = new File(DiffFilename);
            	if(!fdiff.exists()) {
            		Toast.makeText(MainActivity.this, "keine schweren Vokabeln gefunden !", Toast.LENGTH_LONG).show();
            	}    	
            	else
            	{
            		SetDifficultLection(DiffFilename);
            	}
                return true;
            case R.id.BACKTOVOKS:          
            	if (LoadLastLection() == false)
            	{
            		Toast.makeText(MainActivity.this, "keine Vokabeln gefunden !", Toast.LENGTH_LONG).show();
            	}
                return true;
            case R.id.DELETEDIFFICULT:
                //Put up the Yes/No message box
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder
                        .setTitle("schwere Vokabeln loeschen ?")
                        .setMessage("Bist Du sicher ?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                ClearDifficultVocs();
                                Toast.makeText(MainActivity.this, "schwere Vokabeln wurden geloescht !", Toast.LENGTH_LONG).show();
                            }
                        })
                        .setNegativeButton("No", null)						//Do nothing on no
                        .show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    public void SetNewLection(String filename){
    	TextView LectionText;
    	
    	File fsource         = new File(filename);
    	String SrcFilename   = fsource.getName();
    	
    	String DstFilename   = Environment.getExternalStorageDirectory()+"/ezyvok/vok.ezv";
    	File fdest           = new File(DstFilename);
    	
    	String CacheFilename = Environment.getExternalStorageDirectory()+"/ezyvok/Speicher/" + SrcFilename;
    	File fcache          = new File(CacheFilename);
    	
    	try{
    		CopyFile(fsource, fdest);
    	}
    	catch (IOException e){
    	}
    	
    	try{
    		CopyFile(fsource, fcache);
    	}
    	catch (IOException e){
    	}
    	
    	WriteCacheFilename(SrcFilename);
    	
        myVok        = new vok(filename);

        CurrentState = 0;

        LectionText  = (TextView) findViewById(R.id.TEXTVIEW_LECTION);
        LectionText.setText(SrcFilename);     

	    TextView Lang = (TextView) findViewById(R.id.TEXTVIEW_DIRECTION);
	    Lang.setText(myVok.GetLanguageByText());         
	    
	    mForeward();        
    }

    public void SetLastLection(String filename){

    	TextView LectionText;
    	
        myVok        = new vok(filename);

        CurrentState = 0;

        LectionText = (TextView) findViewById(R.id.TEXTVIEW_LECTION);
        LectionText.setText("Speicher: " + ReadCacheFilename());     

	    TextView Lang = (TextView) findViewById(R.id.TEXTVIEW_DIRECTION);
	    Lang.setText(myVok.GetLanguageByText());         
	    
	    mForeward();        
    }

    public void SetDifficultLection(String filename){

    	TextView LectionText;
    	
        myVok        = new vok(filename);

        CurrentState = 0;

        LectionText = (TextView) findViewById(R.id.TEXTVIEW_LECTION);
        LectionText.setText("Speicher: schwer.ezv");     

	    TextView Lang = (TextView) findViewById(R.id.TEXTVIEW_DIRECTION);
	    Lang.setText("");         
	    
	    mForeward();        
    }

    public void OpenFileDlg()
    {
    	new FileSelector(MainActivity.this, FileOperation.LOAD, mLoadFileListener, mFileFilter).show();
    }
    
    public void CreateOwnDirectory()
    {
    	File myDir = new File(Environment.getExternalStorageDirectory()+"/ezyvok");
    	if(!myDir.exists()) {
    		boolean retVal = myDir.mkdir();
    		Toast.makeText(MainActivity.this, "ezyvok Verzeichnis erstellt: " + String.valueOf(retVal), Toast.LENGTH_SHORT).show();
    	}
    	  	
    	myDir = new File(Environment.getExternalStorageDirectory()+"/ezyvok/Speicher");
    	if(!myDir.exists()) {
    		boolean retVal = myDir.mkdir();
    		Toast.makeText(MainActivity.this, "Speicher Verzeichnis erstellt: " + String.valueOf(retVal), Toast.LENGTH_SHORT).show();
    	}
    	
    	LoadLastLection();
    }
   
    private static void CopyFile(File source, File dest)
    	    throws IOException {
	    	    FileChannel inputChannel = null;
	    	    FileChannel outputChannel = null;
    	    try {
    	        inputChannel = new FileInputStream(source).getChannel();
    	        outputChannel = new FileOutputStream(dest).getChannel();
    	        outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
    	    } finally {
    	        inputChannel.close();
    	        outputChannel.close();
    	    }
    	}
    
    private static void WriteCacheFilename(String name)
    {
    	String filename = Environment.getExternalStorageDirectory()+"/ezyvok/SpeicherFile.inf";
		File myFile = new File(filename);
		
		try{
			myFile.createNewFile();
			FileOutputStream fOut          = new FileOutputStream(myFile);
			OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
			myOutWriter.append(name);
			myOutWriter.close();
			fOut.close();
		}
		catch(IOException e)
		{
		}		
    }

    private void SaveToDifficult(String word1, String word2)
    {
    	String filename = Environment.getExternalStorageDirectory()+"/ezyvok/schwer.ezv";
		File myFile = new File(filename);
		
		try{
			FileOutputStream fOut          = new FileOutputStream(myFile, true);
			OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut,  "ISO-8859-1");
			myOutWriter.append(word1 + ":" + word2 + "\n");
			myOutWriter.close();
			fOut.close();
    		Toast.makeText(MainActivity.this, "gespeichert: " + word1 + " - " + word2, Toast.LENGTH_SHORT).show();
		}
		catch(IOException e)
		{
		}		
    }
    
    
    private static void ClearDifficultVocs()
    {
    	String filename = Environment.getExternalStorageDirectory()+"/ezyvok/schwer.ezv";
		File myFile = new File(filename);
		
		try{
			myFile.delete();
			myFile.createNewFile();
		}
		catch(IOException e)
		{
		}		
    }

    private static String ReadCacheFilename()
    {
    	String filename = Environment.getExternalStorageDirectory()+"/ezyvok/SpeicherFile.inf";
		File myFile     = new File(filename);

		String name =  "? unbekannt ?";
		try
		{
			FileInputStream fIn     = new FileInputStream(myFile);
			BufferedReader myReader = new BufferedReader( new InputStreamReader(fIn) );
			name =  myReader.readLine();
			myReader.close();
		}
		catch (IOException e)
		{		
		}
		return name;
    }	
    
    private boolean LoadLastLection()
    {
    	boolean retVal;
    	String filename = Environment.getExternalStorageDirectory()+"/ezyvok/vok.ezv";
    	File myVoks = new File(filename);
    	if(myVoks.exists()) {
    		SetLastLection(filename);
    		Toast.makeText(MainActivity.this, "die letzten Vokabeln sind aktiv !", Toast.LENGTH_SHORT).show();
    		retVal = true;
    	}
    	else
    	{
    		retVal = false;
    	}
    	return retVal;
    }    
}
