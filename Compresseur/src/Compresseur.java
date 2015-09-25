import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;


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

		code=encodeText(texte,encodedMap);
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

		String textePropre=decodeText(code);
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


	private byte[] encodeText(String text, HashMap<Character, String> charCodes) {
		String encodedTextBits = "";
		StringBuffer sbEncodedTextBits = new StringBuffer();

		for (int i = 0; i != text.length(); ++i) {
			sbEncodedTextBits.append(charCodes.get(text.charAt(i)));
		}
		encodedTextBits = sbEncodedTextBits.toString();

		ArrayList<String> stringBits = new ArrayList<String>();
		StringBuffer sbEncodedTextBytes = new StringBuffer();

		for (int i = 0; i != encodedTextBits.length(); ++i) {
			sbEncodedTextBytes.append(encodedTextBits.charAt(i));

			if (sbEncodedTextBytes.length() == 8 || i == encodedTextBits.length() - 1) {
				stringBits.add(sbEncodedTextBytes.toString());
				this.paddingBits = 8 - sbEncodedTextBytes.length();
				sbEncodedTextBytes.delete(0, sbEncodedTextBytes.length());
			}
		}

		byte[] encodedTextBytes = new byte[stringBits.size()];

		for (int i = 0; i != stringBits.size(); ++i) {
			encodedTextBytes[i] = (byte)Integer.parseInt(stringBits.get(i), 2);
		}
		//System.out.println(encodedTextBits);
		return encodedTextBytes;
	}


	//Decoder de bytes en binaire
	private String decodeText(byte[] fichierEncoder) {
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



		//for (int i = 0; i != encodedText.length(); ++i)
		for (int i = 0; i <=20; ++i)
		{
			if (texteFinalBinaire.charAt(i) == '0') {
				if (nodeActive.leftChild instanceof Node) {
					nodeActive = nodeActive.leftChild;
				} else if (nodeActive.leftChild ==null) {
					textePropre.append(nodeActive.clef);
					System.out.println("gauche");
					System.out.println(nodeActive.clef);
					nodeActive=nodeTab.get(0);
				}

			} else if (texteFinalBinaire.charAt(i) == '1') {
				if (nodeActive.rightChild instanceof Node) {
					nodeActive = nodeActive.rightChild;
				} else if (nodeActive.rightChild ==null) {
					textePropre.append(nodeActive.clef);
					System.out.println("droite");
					System.out.println(nodeActive.clef);
					nodeActive=nodeTab.get(0);
				}
			}

		}


		return textePropre.toString();
	}


}
