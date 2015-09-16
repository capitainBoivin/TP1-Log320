import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;


public class Compresseur {

	HashMap<Character, Integer> freqMap = new HashMap<Character, Integer>();
	ArrayList<ArrayList<Object>> freqTab = new ArrayList<ArrayList<Object>>();
	
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
	
	void verfierTable(String mot)
	{
		//Passer a travers les mots
		for (char lettre: mot.toCharArray())
		{
			if(freqMap.get(lettre)==null)
			{
				freqMap.put(lettre,freqTab.size());
				ArrayList<Object> tempo = new ArrayList<Object>();
				tempo.add(lettre);
				tempo.add(1);
				freqTab.add(tempo);
			}
			else
			{
				int indexAAjuster = freqMap.get(lettre);
				int frequence =(Integer) freqTab.get(indexAAjuster).get(1)+1;
				ArrayList<Object> tempo = freqTab.get(indexAAjuster);
				tempo.set(1,frequence);
				freqTab.set(indexAAjuster,tempo);
			}
			/*boolean done = false;
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
			}*/
		}
	}
	//Passer a travers la liste pour les placer en ordre
	private static void quickSort(ArrayList<ArrayList<Object>> tableauDeFrequences,int debut,int fin)
	{	//http://java2novice.com/java-sorting-algorithms/quick-sort/
		ArrayList<Object> pivot = new ArrayList<Object>();
        int i = debut;
        int j = fin;
        pivot = tableauDeFrequences.get(debut+(fin-debut)/2);
        while (i <= j) {

            while ((Integer)tableauDeFrequences.get(i).get(1) > (Integer)pivot.get(1)) {
                i++;
            }
            while ((Integer)tableauDeFrequences.get(j).get(1) < (Integer)pivot.get(1)) {
                j--;
            }
            if (i <= j) {
                echanger(tableauDeFrequences, i, j);
                //move index to next position on both sides
                i++;
                j--;
            }
        }
        // call quickSort() method recursively
        if (debut < j)
            quickSort(tableauDeFrequences, debut, j);
        if (i < fin)
            quickSort(tableauDeFrequences, i, fin);
	}
	
	private static void echanger (ArrayList<ArrayList<Object>> tableauDeFrequences,int indexGrand,int indexPetit) {
		ArrayList<Object> tempo = tableauDeFrequences.get(indexGrand);
		tableauDeFrequences.set(indexGrand, tableauDeFrequences.get(indexPetit));
		tableauDeFrequences.set(indexPetit, tempo);
	}
	
}
