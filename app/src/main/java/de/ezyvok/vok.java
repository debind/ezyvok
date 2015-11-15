package de.ezyvok;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;


public class vok {
	private String[][] voks;
	private String     mlanguage1;
	private String     mlanguage2;
	private int NumberOfVoks;
	private int CurrentVok;
	private String line;
	private BufferedReader bufReader;
	final String splitter = ":";
	private int direction;
		
	public vok(String filename){
		direction = 0;

		try{
			voks = new String[1024][2];

			File myFile = new File(filename);
			FileInputStream fIn = new FileInputStream(myFile);
			bufReader = new BufferedReader( new InputStreamReader(fIn, "ISO-8859-1") );

			NumberOfVoks = 0;
			CurrentVok   = 0;
			while ( (line = bufReader.readLine()) != null){ 
				if (line.contains("="))
				{							
				}
				else if (line.contains("#") && line.contains("-"))
				{
					mlanguage1 = line.split("#", 2)[1].split("-", 2)[0];
					mlanguage2 = line.split("#", 2)[1].split("-", 2)[1];
				}
				else if(line.contains(splitter))
				{
					if (NumberOfVoks<1024){
						voks[NumberOfVoks][0] = line.split(splitter, 2)[0];
						voks[NumberOfVoks][1] = line.split(splitter, 2)[1];
						NumberOfVoks++;
					}
				}
			}
		}
		catch(IOException e){
		}
	}
	
	public int GetNumberOfVoks(){
		return NumberOfVoks;
	}
	
	public String GetLang1(){
		if (direction==0) return voks[CurrentVok][0];
		else              return voks[CurrentVok][1];
	}

	public void SetLanguage(int dir){
		direction = dir;
	}

	public void ChangeLanguage(){
		direction++;
		if (direction > 1) direction = 0;
	}

	public String GetLanguageByText(){
		if (direction == 0) return mlanguage1  + " <-- " + mlanguage2 ;
		if (direction == 1) return mlanguage2 + " --> " + mlanguage1;

		return "invalid";
	}

	public String GetLang2(){
		if (direction==0) return voks[CurrentVok][1];
		else              return voks[CurrentVok][0];
	}

	public int GetCurrent(){
		return CurrentVok;
	}
		
	public void Next(){
		CurrentVok++;
		if (CurrentVok >= NumberOfVoks){
			CurrentVok=0;
		}
	}
	public void Prev(){
		if (NumberOfVoks == 0) { CurrentVok = 0; }
		else
		{
			CurrentVok--;
			if (CurrentVok < 0){
				CurrentVok = NumberOfVoks - 1;
			}
		}
	}

}
