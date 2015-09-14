import java.io.*;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

public class Compresseur {

	//HashMap<String, Integer> freqMap = new HashMap<String, Integer>();
	ArrayList<ArrayList<Object>> freqTab = new ArrayList<ArrayList<Object>>();
	/*private static File getFile() {
		BufferedReader buffReader = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Entrer le lien du texte � d�compresser.");
		File file=null;
		try {
			String filePath = "file:///" + buffReader.readLine();
			URL url = new URL(filePath);
			file = new File(url.getFile());
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return file;
	}*/
	
	public void readFile(File f){
		BufferedReader br = null;

		try {
			String current;
			br = new BufferedReader(new FileReader(f));

			while ((current = br.readLine()) != null) {
				verfierTable(current);
			}
			for(int i=0; i<freqTab.size(); i++) {
				System.out.println(freqTab.get(i).get(0)+ " " + freqTab.get(i).get(1));
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}	
	}

	/*void verfierTable(String mot)
	{
		//Passer a travers les mots
		for (char lettre: mot.toCharArray())
		{
			for 
			if(freqMap.get(lettre+"")!=null)
			{
				freqMap.put(lettre+"",freqMap.get(lettre+"")+1);
			}
			else
			{
				freqMap.put(lettre+"",1);
			}
		}
	}*/
	
	void verfierTable(String mot)
	{
		//Passer a travers les mots
		for (char lettre: mot.toCharArray())
		{
			boolean done = false;
			for (int i=0; i<freqTab.size(); i++) {
				System.out.println(freqTab.get(i).get(0));
				if ((freqTab.get(i).get(0)) == (Object)lettre){
					Integer freq = (Integer)(freqTab.get(i)).get(1);
					freqTab.get(i).set(1, freq++);
					done = true;
				}
			}
			if (done == false) {
					ArrayList<Object> tempo = new ArrayList <Object>();
					tempo.add(0,lettre);
					tempo.add(1,1);
					freqTab.add(tempo);
			}
		}
	}
	//Passer a travers la liste pour les placer en ordre
	void ordonner()
	{
	
	}
}
