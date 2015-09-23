import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;


public class Compresseur {

	private HashMap<Character, Integer> freqMap = new HashMap<Character, Integer>();
	private ArrayList<Node> nodeTab = new ArrayList<Node>();
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
			quickSort(nodeTab,0,nodeTab.size()-1);
			constructHuffmanTree(nodeTab);
			//System.out.println("============================");
			//this.lectureEnOrdreArbre(this.racine);
			//encoder();
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
		//Juste changer le nom du user :P
		String fileEncoder = "C:\\Users\\Catherine\\Desktop\\encode.txt.huf";

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
			System.out.println("1");
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
				freqMap.put(lettre,nodeTab.size());
				Node newNode = new Node(lettre,1);
				nodeTab.add(newNode);
			}
			else
			{
				int indexAAjuster = freqMap.get(lettre);
				nodeTab.get(indexAAjuster).freq = (Integer)nodeTab.get(indexAAjuster).freq +1;
			}
		}
	}
	//Passer a travers la liste pour les placer en ordre
	private static void quickSort(ArrayList<Node> tableauDeNodes,int debut,int fin)
	{	//http://java2novice.com/java-sorting-algorithms/quick-sort/
        int i = debut;
        int j = fin;
        Node pivot = tableauDeNodes.get(debut+(fin-debut)/2);
        while (i <= j) {

            while ((Integer)tableauDeNodes.get(i).freq < (Integer)pivot.freq) {
                i++;
            }
            while ((Integer)tableauDeNodes.get(j).freq > (Integer)pivot.freq) {
                j--;
            }
            if (i <= j) {
                echanger(tableauDeNodes, i, j);
                //move index to next position on both sides
                i++;
                j--;
            }
        }
        // call quickSort() method recursively
        if (debut < j)
            quickSort(tableauDeNodes, debut, j);
        if (i < fin)
            quickSort(tableauDeNodes, i, fin);
	}
	
	private static void echanger (ArrayList<Node> tableauDeNodes,int indexGrand,int indexPetit) {
		Node tempo = tableauDeNodes.get(indexGrand);
		tableauDeNodes.set(indexGrand, tableauDeNodes.get(indexPetit));
		tableauDeNodes.set(indexPetit, tempo);
	}


	/*--------------------------Arbre---------------------------*/
	
	private void constructHuffmanTree(ArrayList<Node> tableauDeNodes){
		if (tableauDeNodes.size() == 1){
			return;
		}
		else {
			Node newNode = new Node(null,(Integer)tableauDeNodes.get(0).freq + (Integer)tableauDeNodes.get(1).freq);
			newNode.leftChild = tableauDeNodes.get(0);
			newNode.rightChild = tableauDeNodes.get(1);
			tableauDeNodes.remove(0);
			tableauDeNodes.remove(0);
			insertionAndSort(tableauDeNodes,newNode);
			constructHuffmanTree(tableauDeNodes);
		}
	}
	//http://www.algolist.net/Algorithms/Sorting/Insertion_sort
	private void insertionAndSort(ArrayList<Node> tableauDeNodes,Node newNode) {
	      int j;
	      tableauDeNodes.add(newNode);
	      j = tableauDeNodes.size() - 1;
	      while (j > 0 && (Integer) tableauDeNodes.get(j - 1).freq >(Integer) newNode.freq) {
	        	tableauDeNodes.set(j, tableauDeNodes.get(j - 1));
	            j--;
	      }
	      tableauDeNodes.set(j, newNode);
	      System.out.println("================================================================");
	      for(int k=0; k<tableauDeNodes.size(); k++) {
				System.out.println(tableauDeNodes.get(k).clef + " " + tableauDeNodes.get(k).freq);
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
