import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class Compresseur {

	private HashMap<Character, Integer> freqMap = new HashMap<Character, Integer>();
	private HashMap<Character, String> encodedMap = new HashMap<Character, String>();
	private ArrayList<Node> nodeTab = new ArrayList<Node>();
	private int somChar=0;
	private Node racine;
	private Node nodeActive;
	private String texte = "";
	private String ligne;
	private int paddingBits;

	public void readFile(File f){
		BufferedReader br = null;

		try {
			br = new BufferedReader(new FileReader(f));
			while ((ligne = br.readLine()) != null) {
				verfierTable(ligne);
				texte += ligne;
			}
			quickSort(nodeTab,0,nodeTab.size()-1);
			//Sacrer nodeTab dans un autre variable.
			constructHuffmanTree(nodeTab);
			constructEncodedMap(nodeTab.get(0),encodedMap,new String());
			//Rappeler constrcut HuffmanTree
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
		String fileEncoder = "C:\\Users\\Catherine\\Desktop\\encode.txt.huf";
		byte [] code;
		byte [] headerEncoder;

		System.out.println("hashmap avant de cath");
		System.out.println(freqMap);

		code=encoderTexte(texte, encodedMap);
		headerEncoder=encodeHeader(freqMap);
		decodeHeader(headerEncoder);
		decoder(code);

		try
		{
			BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(fileEncoder));
			outputStream.write(code);
			outputStream.close();
		}
		catch(IOException ex)
		{
			ex.printStackTrace();
		}
	}

	void decoder(byte [] code)
	{
		String fileDecoder = "C:\\Users\\Catherine\\Desktop\\decode.txt";
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
	}

	// Début de la référence http://codereview.stackexchange.com/questions/44473/huffman-code-implementation
	public void constructEncodedMap(Node currentNode, HashMap<Character, String> encodedMap, String encodedString)
	{
		if (currentNode.leftChild == null && currentNode.rightChild == null) {
            encodedMap.put((Character)currentNode.clef, encodedString);
            return;
        }    
		constructEncodedMap(currentNode.leftChild, encodedMap, encodedString + '0');
		constructEncodedMap(currentNode.rightChild, encodedMap, encodedString + '1' );
	} //Fin de la référence

	private byte[] encoderTexte(String texte, HashMap<Character, String> mapEncoder) {
		String texteEncodeBinaire = "";
		StringBuffer sousTexteEncodeBianire = new StringBuffer();

		for (int i = 0; i != texte.length(); ++i) {
			sousTexteEncodeBianire.append(mapEncoder.get(texte.charAt(i)));
		}
		texteEncodeBinaire = sousTexteEncodeBianire.toString();
		ArrayList<String> stringBits = new ArrayList<String>();
		StringBuffer textePropreBinaire = new StringBuffer();

		for (int i = 0; i != texteEncodeBinaire.length(); ++i) {
			textePropreBinaire.append(texteEncodeBinaire.charAt(i));

			if (textePropreBinaire.length() == 8 || i == texteEncodeBinaire.length() - 1) {
				stringBits.add(textePropreBinaire.toString());
				this.paddingBits = 8 - textePropreBinaire.length();
				textePropreBinaire.delete(0, textePropreBinaire.length());
			}
		}

		byte[] texteEcnodeFinal = new byte[stringBits.size()];

		for (int i = 0; i != stringBits.size(); ++i) {
			texteEcnodeFinal[i] = (byte)Integer.parseInt(stringBits.get(i), 2);
		}

		return texteEcnodeFinal;
	}

	//Decoder de bytes en binaire
	private String decoderTexte(byte[] fichierEncoder) {
		StringBuilder texteBinaire = new StringBuilder();
		StringBuilder textePropre = new StringBuilder();
		String padString = "00000000";
		String texteFinalEncoder = "";
		String texteFinalBinaire = "";

		//decoder et remettre en binaire
		for (int i =0; i != fichierEncoder.length; ++i) {
			texteFinalEncoder = Integer.toBinaryString(fichierEncoder[i] & 0xFF);
			if (i != fichierEncoder.length - 1) {
				texteBinaire.append(padString.substring(texteFinalEncoder.length()));
			} else {
				texteBinaire.append(padString.substring(texteFinalEncoder.length()+this.paddingBits));
			}
			texteBinaire.append(texteFinalEncoder);
		}

		texteFinalBinaire = texteBinaire.toString();

		nodeActive=nodeTab.get(0);


		for (int i = 0; i != texteFinalBinaire.length(); ++i)
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
	private byte[] encodeHeader(HashMap<Character, Integer> freqMap) {
		//Se faire un array de 256 a 0 pour se faire un array assez gros et controler
		int[] entierHeader = new int[256];
		Arrays.fill(entierHeader, 0);

		//Aller cherhcer chacun des caracteres dans notre table de frequence
		for (Map.Entry<Character, Integer> entry : freqMap.entrySet()) {
			entierHeader[(byte)(char)entry.getKey()] = entry.getValue();
		}

		//Variables pour travailler avec et qui servent de tempo
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


		ArrayList<String> stringBits = new ArrayList<String>();
		StringBuffer sousTexteEncoder = new StringBuffer();

		//Faire des compressions en 8 bit pour encoder le header
		for (int i = 0; i != charDesBytes.length(); ++i) {
			sousTexteEncoder.append(charDesBytes.charAt(i));

			if (sousTexteEncoder.length() == 8 || i == charDesBytes.length() - 1) {
				stringBits.add(sousTexteEncoder.toString());
				sousTexteEncoder.delete(0, sousTexteEncoder.length());
			}
		}

		byte[] bytesDepart = new byte[stringBits.size() + 1];

		for (int i = 0; i != stringBits.size(); ++i) {
			bytesDepart[i + 1] = (byte)Integer.parseInt(stringBits.get(i), 2);
		}

		//Ajout des bit de pading pour avoir une grandeur fixe et pour permettre de bien decoder
		bytesDepart[0] = (byte)this.paddingBits;

		System.out.println("header encode");
		System.out.println(bytesDepart);
		return bytesDepart;
	}




	private HashMap<Character, Integer> decodeHeader(byte[] arbreEncoder) {
		HashMap<Character, Integer> freqMap = new HashMap<Character, Integer>();
		this.paddingBits = arbreEncoder[0] & 0xFF;

		StringBuilder sousHeaderEncoder = new StringBuilder();
		String padString = "00000000";
		String headerEncoderTempo = "";
		String headerEncoder = "";

		for (int i = 1; i != 33; ++i) {
			headerEncoderTempo = Integer.toBinaryString(arbreEncoder[i] & 0xFF);
			sousHeaderEncoder.append(padString.substring(headerEncoderTempo.length()));
			sousHeaderEncoder.append(headerEncoderTempo);
		}

		headerEncoder = sousHeaderEncoder.toString();

		//Variable de 256 pour la frequence comme pour lencodage
		int[] frequence = new int[256];
		//Remplissage de la variable avec du vide
		Arrays.fill(frequence, 0);

		int index =33;


		for (int i = 0; i != headerEncoder.length(); ++i) {
			if (headerEncoder.charAt(i) == '1') {
				StringBuilder sousFrenquenceTempo = new StringBuilder();
				String frenquenceTempo = "";
				frenquenceTempo = Integer.toBinaryString(arbreEncoder[index] & 0xFF);
				sousFrenquenceTempo.append(padString.substring(frenquenceTempo.length()));
				sousFrenquenceTempo.append(frenquenceTempo);
				frenquenceTempo = Integer.toBinaryString(arbreEncoder[index+1] & 0xFF);
				sousFrenquenceTempo.append(padString.substring(frenquenceTempo.length()));
				sousFrenquenceTempo.append(frenquenceTempo);
				frenquenceTempo = Integer.toBinaryString(arbreEncoder[index+2] & 0xFF);
				sousFrenquenceTempo.append(padString.substring(frenquenceTempo.length()));
				sousFrenquenceTempo.append(frenquenceTempo);
				frenquenceTempo = Integer.toBinaryString(arbreEncoder[index+3] & 0xFF);
				sousFrenquenceTempo.append(padString.substring(frenquenceTempo.length()));
				sousFrenquenceTempo.append(frenquenceTempo);
				index += 4;

				freqMap.put((char)i, Integer.parseInt(sousFrenquenceTempo.toString(), 2));
			}
		}
		System.out.println("frequence decoder");
		System.out.println(freqMap);
		return freqMap;
	}

}
