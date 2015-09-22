import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;


public class Compresseur {

	private HashMap<Character, Integer> freqMap = new HashMap<Character, Integer>();
	private ArrayList<ArrayList<Object>> freqTab = new ArrayList<ArrayList<Object>>();
	private int somChar=0;
	private Node racine;
	private Node nodeActive;
	private String texte = "";
	private String ligne;

	public void readFile(File f){
		BufferedReader br = null;

		try {
			br = new BufferedReader(new FileReader(f));
			while ((ligne = br.readLine()) != null) {
				verfierTable(ligne);
				texte += ligne;
			}
			quickSort(freqTab,0,freqTab.size()-1);
			for(int i=0; i<freqTab.size(); i++) {
				this.addNode(freqTab.get(i).get(0),freqTab.get(i).get(1));
				//System.out.println(freqTab.get(i).get(0)+ " " + freqTab.get(i).get(1));
			}
			//System.out.println("============================");
			//this.lectureEnOrdreArbre(this.racine);
			encoder();
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


	//Faudrait passer le texte et une charactere a la fois et on sait que chacun des charactere exsite dans ils ont ete
	//denombre, par la suite on passe a travers la larbre a la recherhe et la lettre et ajoute les 0 et 1
	void encoder()
	{
		// Lire le fichier et mettre les zeros avant et apres chaque lettre pour faire la separation
		String code="";
		String fileEncoder = "C:\\Users\\maaj\\Desktop\\encode.txt";

		for(char aTrouver: texte.toCharArray()){
			nodeActive=racine;
			boolean continuer = true;

			while(continuer)
			{		
				if (nodeActive.leftChild.clef.equals(aTrouver)) {
					code += "0";
					continuer = false;
				} else {
					code += 1;
					nodeActive = nodeActive.rightChild;
				}
			}
			//System.out.println(code);
		}
		try
		{
			byte[] buffer = code.getBytes();
			FileOutputStream outputStream = new FileOutputStream(fileEncoder);
			outputStream.write(buffer);
			outputStream.close();
			System.out.println("Wrote " + buffer.length +
					" bytes");
		}
		catch(IOException ex)
		{
			ex.printStackTrace();
		}
	}

	// parcourir et prendre entre les 0 trouve
	void decoder()
	{
	//Chacun des characteres entre 0 et 1
		nodeActive=racine;
		char binaire='0';
		Object trouver;
		if(binaire==1)
		{
			nodeActive=nodeActive.rightChild;
		}
		else
		{
			trouver=nodeActive.clef;
		}
	//Ensuite mettre les lettres une apres lautre.

	}



	//Permet de traverser l'arbre
	void verfierTable(String mot)
	{
		//Passer a travers les mots
		for (char lettre: mot.toCharArray())
		{
			somChar++;
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


	/*--------------------------Arbre---------------------------*/

	public void addNode(Object clef,Object freq)
	{
		Node newNode=new Node(clef,freq);

		if (racine==null)
		{
			racine=new Node(null,somChar);
			//racine=newNode;
			nodeActive=racine;
		}

			if (nodeActive.leftChild==null)
			{
				//System.out.println("new node"+newNode);
				nodeActive.leftChild=newNode;
				somChar=somChar-(Integer)freq;

				nodeActive.rightChild = new Node(null,somChar);
				nodeActive=nodeActive.rightChild;
			}

		}

	public void lectureEnOrdreArbre(Node nodeActive)
	{

		if(nodeActive!=null)
		{
			//Voir gauche remonter de 1 et aller a droite
			System.out.println(nodeActive);

			lectureEnOrdreArbre(nodeActive.leftChild);

			//Par ordre de grandeur
			//System.out.println(nodeActive);

			lectureEnOrdreArbre(nodeActive.rightChild);

			//Check les deux child dun noeuds et ensuite on remonte
			//System.out.println(nodeActive);
		}
	}
}
