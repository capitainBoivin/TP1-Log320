import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class Compresseur {

	HashMap<String, Integer> freqMap = new HashMap<String, Integer>();

	private static File getFile() {
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
	}
	
	public void readFile(File f){
		BufferedReader br = null;

		try {
			String current;
			br = new BufferedReader(new FileReader(f));

			while ((current = br.readLine()) != null) {
				verfierTable(current);
			}
			System.out.println(freqMap);

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
			if(freqMap.get(lettre+"")!=null)
			{
				freqMap.put(lettre+"",freqMap.get(lettre+"")+1);
			}
			else
			{
				freqMap.put(lettre+"",1);
			}
		}
	}
	//Passer a travers la liste pour les placer en ordre
	void ordoner()
	{
		for (String key : freqMap.keySet())
		{
			;
		}
	}
	
}
