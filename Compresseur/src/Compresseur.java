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
			constructHuffmanTree(nodeTab);
			constructEncodedMap(nodeTab.get(0),encodedMap,new String());
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
		String fileEncoder = "C:\\Users\\Maaj\\Desktop\\encode.txt.huf";
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
		String fileDecoder = "C:\\Users\\Maaj\\Desktop\\decode.txt";
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
	//http://codereview.stackexchange.com/questions/44473/huffman-code-implementation
	public void constructEncodedMap(Node currentNode, HashMap<Character, String> encodedMap, String encodedString)
	{
		if (currentNode.leftChild == null && currentNode.rightChild == null) {
            encodedMap.put((Character)currentNode.clef, encodedString);
            return;
        }    
		constructEncodedMap(currentNode.leftChild, encodedMap, encodedString + '0');
		constructEncodedMap(currentNode.rightChild, encodedMap, encodedString + '1' );
	}

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


	private byte[] encodeHeader(HashMap<Character, Integer> freqMap) {
		int[] headerInt = new int[256];
		Arrays.fill(headerInt, 0);

		for (Map.Entry<Character, Integer> entry : freqMap.entrySet()) {
			headerInt[(byte)(char)entry.getKey()] = entry.getValue();
		}

		String charInFileBits = "";
		String padString = "00000000000000000000000000000000";
		StringBuilder sbCharInFileBits = new StringBuilder();
		StringBuilder sbCharFrequencyInFileBits = new StringBuilder();
		String frequencyBits = "";

		for (int i = 0; i != headerInt.length; ++i) {
			if (headerInt[i] == 0) {
				sbCharInFileBits.append('0');
			} else {
				sbCharInFileBits.append('1');
				frequencyBits = Integer.toBinaryString(headerInt[i]);
				sbCharFrequencyInFileBits.append(padString.substring(frequencyBits.length()));
				sbCharFrequencyInFileBits.append(frequencyBits);
			}
		}

		charInFileBits = sbCharInFileBits.append(sbCharFrequencyInFileBits.toString()).toString();

		ArrayList<String> stringBits = new ArrayList<String>();
		StringBuffer sbEncodedTextBytes = new StringBuffer();

		for (int i = 0; i != charInFileBits.length(); ++i) {
			sbEncodedTextBytes.append(charInFileBits.charAt(i));

			if (sbEncodedTextBytes.length() == 8 || i == charInFileBits.length() - 1) {
				stringBits.add(sbEncodedTextBytes.toString());
				sbEncodedTextBytes.delete(0, sbEncodedTextBytes.length());
			}
		}

		byte[] headerBytes = new byte[stringBits.size() + 1];

		for (int i = 0; i != stringBits.size(); ++i) {
			headerBytes[i + 1] = (byte)Integer.parseInt(stringBits.get(i), 2);
		}

		headerBytes[0] = (byte)this.paddingBits;

		System.out.println("header encode");
		System.out.println(headerBytes);
		return headerBytes;
	}




	private HashMap<Character, Integer> decodeHeader(byte[] arbreEncoder) {
		HashMap<Character, Integer> frequencyTable = new HashMap<Character, Integer>();
		this.paddingBits = arbreEncoder[0] & 0xFF;

		StringBuilder sbEncodedHeader = new StringBuilder();
		String padString = "00000000";
		String encodedHeaderPart = "";
		String encodedHeader = "";

		for (int i = 1; i != 33; ++i) {
			encodedHeaderPart = Integer.toBinaryString(arbreEncoder[i] & 0xFF);
			sbEncodedHeader.append(padString.substring(encodedHeaderPart.length()));
			sbEncodedHeader.append(encodedHeaderPart);
		}

		encodedHeader = sbEncodedHeader.toString();

		int[] frequencies = new int[256];
		Arrays.fill(frequencies, 0);
		int currentIndex = 33;


		for (int i = 0; i != encodedHeader.length(); ++i) {
			if (encodedHeader.charAt(i) == '1') {
				StringBuilder sbEncodedFrequencies = new StringBuilder();
				String frequencyPart = "";
				frequencyPart = Integer.toBinaryString(arbreEncoder[currentIndex] & 0xFF);
				sbEncodedFrequencies.append(padString.substring(frequencyPart.length()));
				sbEncodedFrequencies.append(frequencyPart);
				frequencyPart = Integer.toBinaryString(arbreEncoder[currentIndex+1] & 0xFF);
				sbEncodedFrequencies.append(padString.substring(frequencyPart.length()));
				sbEncodedFrequencies.append(frequencyPart);
				frequencyPart = Integer.toBinaryString(arbreEncoder[currentIndex+2] & 0xFF);
				sbEncodedFrequencies.append(padString.substring(frequencyPart.length()));
				sbEncodedFrequencies.append(frequencyPart);
				frequencyPart = Integer.toBinaryString(arbreEncoder[currentIndex+3] & 0xFF);
				sbEncodedFrequencies.append(padString.substring(frequencyPart.length()));
				sbEncodedFrequencies.append(frequencyPart);
				currentIndex += 4;

				frequencyTable.put((char)i, Integer.parseInt(sbEncodedFrequencies.toString(), 2));
			}
		}
		System.out.println("frequence decoder");
		System.out.println(frequencyTable);
		return frequencyTable;
		/*
		this.encodedFileTextBeginIndex = currentIndex;



		MapValueComparator mapValueComparator = new MapValueComparator(frequencyTable);
		HashMap<Character, Integer> sortedFrequencyTable = new HashMap<Character, Integer>(mapValueComparator);
		sortedFrequencyTable.putAll(frequencyTable);

		return sortedFrequencyTable;*/
	}

}
