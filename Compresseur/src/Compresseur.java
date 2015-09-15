import java.io.*;
import java.util.ArrayList;


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
			System.out.println("============================");
			quickSort(freqTab,0,freqTab.size()-1);
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
				if ((freqTab.get(i).get(0)) == (Object)lettre){
					Integer freq = (Integer)(freqTab.get(i)).get(1) + 1;
					freqTab.get(i).set(1, freq);
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
	private static void quickSort(ArrayList<ArrayList<Object>> tableauDeFrequences,int debut,int fin)
	{	
		int i = debut;
        int j = fin; 
		if (i < j) {
			ArrayList<Object> pivot = tableauDeFrequences.get(debut);
			while (j > i){
				  while ((Integer)tableauDeFrequences.get(i).get(1) >= (Integer)pivot.get(1) && i <= fin && j > i) { 
                      System.out.println((Integer)tableauDeFrequences.get(i).get(1));
				  	  System.out.println((Integer)pivot.get(1));
					  i++;
				  }
				  
				  while ((Integer)tableauDeFrequences.get(j).get(1) <= (Integer)pivot.get(1) && j >= debut && j > i){ 
					  System.out.println((Integer)tableauDeFrequences.get(j).get(1));
				  	  System.out.println((Integer)pivot.get(1));
					  j--;
					  
				  }
				  if (i < j) {
						echanger(tableauDeFrequences,i,j);
				  }
			}
			echanger(tableauDeFrequences,debut,j);
			
			quickSort(tableauDeFrequences, debut, j - 1); 
			quickSort(tableauDeFrequences, j + 1, fin);
		}
	}
	
	private static void echanger (ArrayList<ArrayList<Object>> tableauDeFrequences,int indexGrand,int indexPetit) {
		System.out.println("dans le echanger");
		ArrayList<Object> tempo = tableauDeFrequences.get(indexGrand);
		tableauDeFrequences.set(indexGrand, tableauDeFrequences.get(indexPetit));
		tableauDeFrequences.set(indexPetit, tempo);
	}
	
}
