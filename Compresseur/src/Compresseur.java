import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;



public class Compresseur {

	private HashMap<Character, Integer> freqMap = new HashMap<Character, Integer>();
	private HashMap<Character, String> encodedMap = new HashMap<Character, String>();
	private ArrayList<Node> nodeTab = new ArrayList<Node>();
	private ArrayList<Node> nodeTabTempo;
	private int somChar=0;
	private Node nodeActive;
	private StringBuilder sousTexte=new StringBuilder();
	private String texte="";
	private String ligne;
	private int paddingBits;
	private byte [] code;
	private int indexLecture;
	private Boolean debugMode=false;


	public void readFile(File f){
		BufferedReader br = null;
		try {
			//trouver l'extension du fichier
			String fichier = f.getPath().substring(f.getPath().lastIndexOf("\\"));
			String extension = fichier.substring(fichier.indexOf("."));

			//Verifier le type de fichier
			if(extension.equals(".txt") || extension.equals(".png"))
			{
				indexLecture=0;
				//Lire le fichier
				br = new BufferedReader(new FileReader(f));
				while ((ligne = br.readLine()) != null) {
					verfierTable(ligne);
					sousTexte.append(ligne);
					//Ajouter les espaces
					//sousTexte.append(System.lineSeparator());
				}
				texte=sousTexte.toString();


				quickSort(nodeTab,0,nodeTab.size()-1);

				//Copie du nodeTab pour faire d<autre traitement dessus
				this.nodeTabTempo= new ArrayList<Node>(nodeTab);
				//Creation de l'arbre
				constructHuffmanTree(nodeTab);
				constructEncodedMap(nodeTab.get(0),encodedMap,new String());
				encoder();
			}
			else if (extension.equals(".txt.huf"))
			{
				byte[] contenuEncoder = new byte[(int)f.length()];

				//Pour le fichier en bianire
				FileInputStream fichierDecode;
				try {
					fichierDecode = new FileInputStream(f);
					fichierDecode.read(contenuEncoder);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				//Decode le fichier
				decoder(contenuEncoder);
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

	void encoder()
	{
		//Fichier hardcoder....cause!!
		String fileEncoder = "C:\\Users\\Maaj\\Desktop\\encode.txt.huf";

		byte [] headerEncoder=null;

		//Encodage du header
		if(!debugMode) {
			headerEncoder = encodeHeader(nodeTabTempo);
		}
		//Code est le fichier encoder avec le map
		code=encoderTexte(texte, encodedMap);

		//Ecireture dans le fichier
		try
		{
			BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(fileEncoder));
			if(!debugMode) {
				outputStream.write(headerEncoder);
			}
			outputStream.write(code);
			outputStream.close();
		}
		catch(IOException ex)
		{
			ex.printStackTrace();
		}
	}

	void decoder(byte[] code)
	{
		String fileDecoder = "C:\\Users\\Maaj\\Desktop\\decode.txt";
		//String fileDecoder = "C:\\Users\\Maaj\\Desktop\\decode.png";

		//Decodage du header et du code en utilisant le code lu
		if(!debugMode) {
			ArrayList headerDecoder = decodeHeader(code);
		}
		String textePropre=decoderTexte(code);

		try
		{
			BufferedWriter outWriter = new BufferedWriter(new FileWriter(fileDecoder));
			outWriter.write(textePropre);
			outWriter.close();
		}
		catch(IOException ex)
		{
			ex.printStackTrace();
		}
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

	//Permet seulement de voir les elements pour une question de debuggage
	public void verifierChaine(ArrayList liste)
	{
		for (Object element:liste)
		{
			System.out.println(element);
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

	//Changement entre les nodes
	private static void echanger (ArrayList<Node> tableauDeNodes,int indexGrand,int indexPetit) {
		Node tempo = tableauDeNodes.get(indexGrand);
		tableauDeNodes.set(indexGrand, tableauDeNodes.get(indexPetit));
		tableauDeNodes.set(indexPetit, tempo);
	}

	/*--------------------------Arbre---------------------------*/

	//COnstruire l'arbre
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
	}

	// D�but de la r�f�rence http://codereview.stackexchange.com/questions/44473/huffman-code-implementation
	public void constructEncodedMap(Node currentNode, HashMap<Character, String> encodedMap, String encodedString)
	{
		if (currentNode.leftChild == null && currentNode.rightChild == null) {
            encodedMap.put((Character)currentNode.clef, encodedString);
            return;
        }    
		constructEncodedMap(currentNode.leftChild, encodedMap, encodedString + '0');
		constructEncodedMap(currentNode.rightChild, encodedMap, encodedString + '1' );
	} //Fin de la r�f�rence

	//Encoder le texte sans le header
	private byte[] encoderTexte(String texte, HashMap<Character, String> mapEncoder) {
		String texteEncodeBinaire = "";
		StringBuffer sousTexteEncodeBianire = new StringBuffer();

		//Lire chacun des caracteres jusqua la fin du texte
		for (int i = 0; i != texte.length(); i++) {
			sousTexteEncodeBianire.append(mapEncoder.get(texte.charAt(i)));
		}
		//Faire une grande chaine et le mettre dans le string
		texteEncodeBinaire = sousTexteEncodeBianire.toString();
		//Setting pour meme technique d'insertion
		ArrayList<String> stringBits = new ArrayList<String>();
		StringBuffer textePropreBinaire = new StringBuffer();

		for (int i = 0; i != texteEncodeBinaire.length(); ++i) {
			textePropreBinaire.append(texteEncodeBinaire.charAt(i));

			if (textePropreBinaire.length() == 8 || i == texteEncodeBinaire.length() - 1) {
				stringBits.add(textePropreBinaire.toString());
				//Ajout de bit pour faire des code de meme longeur pour lencodage
				this.paddingBits = 8 - textePropreBinaire.length();
				textePropreBinaire.delete(0, textePropreBinaire.length());
			}
		}

		//Donner le size egal a notre texte(sauver de lespace)
		byte[] texteEcnodeFinal = new byte[stringBits.size()];

		//Encodage final encoder
		for (int i = 0; i != stringBits.size(); ++i) {
			texteEcnodeFinal[i] = (byte)Integer.parseInt(stringBits.get(i), 2);
		}

		return texteEcnodeFinal;
	}

	//Decoder de bytes en texte
	private String decoderTexte(byte[] fichierEncoder) {
		//meme principe de recipient avant de mettre dans le string directement
		StringBuilder texteBinaire = new StringBuilder();
		StringBuilder textePropre = new StringBuilder();
		String chainePadding = "00000000";
		String texteFinalEncoder = "";
		String texteFinalBinaire = "";

		//decoder et remettre en binaire
		for (int i = this.indexLecture; i != fichierEncoder.length; i++) {
			texteFinalEncoder = Integer.toBinaryString(fichierEncoder[i] & 0xFF);
			//Verification de la longeur des string de byte pour decoder, sinon ajout avec le padding
			if (i != fichierEncoder.length - 1) {
				texteBinaire.append(chainePadding.substring(texteFinalEncoder.length()));
			} else {
				texteBinaire.append(chainePadding.substring(texteFinalEncoder.length()+this.paddingBits));
			}
			texteBinaire.append(texteFinalEncoder);
		}
		texteFinalBinaire = texteBinaire.toString();

		//Mettre la node active au debut de larbre
		nodeActive=nodeTab.get(0);

		for (int i = 0; i != texteFinalBinaire.length(); i++)
		{
			//Aller a gauche si 0, aller a droite si 1
			if (texteFinalBinaire.charAt(i) == '0') {
				if (nodeActive.leftChild instanceof Node) {
					nodeActive = nodeActive.leftChild;
				}
				if (nodeActive.leftChild ==null) {
					textePropre.append(nodeActive.clef);
					nodeActive=nodeTab.get(0);
				}
			} else if (texteFinalBinaire.charAt(i) == '1') {
				if (nodeActive.rightChild instanceof Node) {
					nodeActive = nodeActive.rightChild;
				}
				if (nodeActive.rightChild ==null) {
					textePropre.append(nodeActive.clef);
					nodeActive=nodeTab.get(0);
				}
			}
		}
		return textePropre.toString();
	}


	//Ecnoder le header avec la table de frequence
	private byte[] encodeHeader(ArrayList<Node> nodeTab) {
		//Se faire un array de 256 a 0 pour se faire un array assez gros et controler
		int[] entierHeader = new int[256];
		Arrays.fill(entierHeader, 0);

		//On passe a travers la liste et on associe la frequence au code binaire de la lettre.
		for (Node entrer : nodeTab) {
			entierHeader[entrer.clef.toString().getBytes()[0]]=(Integer)entrer.freq;
		}

		//M[eme principe de recipient encore avec la padding de bits pou l<ajustement en 16 bit
		String charDesBytes = "";
		StringBuilder sousCharDesBytes = new StringBuilder();
		String bufferString = "00000000000000000000000000000000";
		String frequence = "";
		StringBuilder sousFredMap = new StringBuilder();

		//Passage en binaire des frequences
		for (int i = 0; i != entierHeader.length; i++) {
			if (entierHeader[i] == 0) {
				sousCharDesBytes.append('0');
			} else {
				sousCharDesBytes.append('1');
				frequence = Integer.toBinaryString(entierHeader[i]);
				sousFredMap.append(bufferString.substring(frequence.length()));
				sousFredMap.append(frequence);
			}
		}
		charDesBytes = sousCharDesBytes.append(sousFredMap.toString()).toString();

		//Liste des chaine de bits
		ArrayList<String> stringBits = new ArrayList<String>();
		StringBuffer sousTexteEncoder = new StringBuffer();

		//Faire des compressions en 8 bit pour encoder le header
		for (int i = 0; i != charDesBytes.length(); i++) {
			sousTexteEncoder.append(charDesBytes.charAt(i));
			//Verification de la longeur pour un bon encodage
			if (sousTexteEncoder.length() == 8 || i == charDesBytes.length() - 1) {
				stringBits.add(sousTexteEncoder.toString());
				sousTexteEncoder.delete(0, sousTexteEncoder.length());
			}
		}

		//La longeur pour pouvoir boucler jusqu'a la fin
		byte[] bytesDepart = new byte[stringBits.size() + 1];

		//Ajouter les nombres en byte de base deux dans le chaine de byte
		for (int i = 0; i != stringBits.size(); i++) {
			bytesDepart[i + 1] = (byte)Integer.parseInt(stringBits.get(i), 2);
		}

		//Mettre notre padding dans un indice pour le reutiliser
		bytesDepart[0] = (byte)this.paddingBits;

		return bytesDepart;
	}

	//Decode le header dans le fichier
	private ArrayList<Node> decodeHeader(byte[] texteEncoder) {
		//Array pour refaire un tableau de fr/quence pour refaire l'arbre
		ArrayList<Node> nodeFreq=new ArrayList<Node>();
		//Aller chercher le type de padding mis dans le texte pour l'utiliser
		this.paddingBits = texteEncoder[0] & 0xFF;

		//Principe de recipient again
		StringBuilder sousHeaderEncoder = new StringBuilder();
		String chainePadding = "00000000";
		String headerEncoderTempo = "";
		String headerEncoder = "";

		for (int i = 1; i != 33; i++) {
			headerEncoderTempo = Integer.toBinaryString(texteEncoder[i] & 0xFF);
			sousHeaderEncoder.append(chainePadding.substring(headerEncoderTempo.length()));
			sousHeaderEncoder.append(headerEncoderTempo);
		}

		headerEncoder = sousHeaderEncoder.toString();

		//Variable de 256 pour la frequence comme pour lencodage
		int[] frequence = new int[256];
		//Remplissage de la variable avec du vide
		Arrays.fill(frequence, 0);

		//Mettre l'index de depart comme pour la boucle
		int index =33;

		//Par chacun des caractres dans le header encoder
		for (int i = 0; i != headerEncoder.length(); i++) {
			if (headerEncoder.charAt(i) == '1') {
				//Principe de recipeint encore
				StringBuilder sousFrenquenceTempo = new StringBuilder();
				String frenquenceTempo = "";

				frenquenceTempo = Integer.toBinaryString(texteEncoder[index] & 0xFF);
				sousFrenquenceTempo.append(chainePadding.substring(frenquenceTempo.length()));
				sousFrenquenceTempo.append(frenquenceTempo);

				frenquenceTempo = Integer.toBinaryString(texteEncoder[index+1] & 0xFF);
				sousFrenquenceTempo.append(chainePadding.substring(frenquenceTempo.length()));
				sousFrenquenceTempo.append(frenquenceTempo);

				frenquenceTempo = Integer.toBinaryString(texteEncoder[index+2] & 0xFF);
				sousFrenquenceTempo.append(chainePadding.substring(frenquenceTempo.length()));
				sousFrenquenceTempo.append(frenquenceTempo);

				frenquenceTempo = Integer.toBinaryString(texteEncoder[index+3] & 0xFF);
				sousFrenquenceTempo.append(chainePadding.substring(frenquenceTempo.length()));
				sousFrenquenceTempo.append(frenquenceTempo);

				index += 4;

				//On obtient le charactere et la frequence,pour chacun on fait des node et on le retransforme en decimal
				nodeFreq.add(new Node((char)i, Integer.parseInt(sousFrenquenceTempo.toString(), 2)));
			}
		}

		//Pour ne pas relire le header en decodant le texte, on me l'index a un point a commencer
		this.indexLecture = index;

		//Refaire les etapes pour bien faire l'arbre
		quickSort(nodeFreq, 0, nodeFreq.size() - 1);
		encodedMap=new HashMap<Character, String>();
		constructHuffmanTree(nodeFreq);
		constructEncodedMap(nodeFreq.get(0),encodedMap,new String());

		nodeTab= new ArrayList<Node>(nodeFreq);
		return nodeFreq;
	}
}
