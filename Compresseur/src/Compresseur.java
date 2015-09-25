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
		decodeText(code);
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
				paddingBits = 8 - sbEncodedTextBytes.length();
				sbEncodedTextBytes.delete(0, sbEncodedTextBytes.length());
			}
		}

		byte[] encodedTextBytes = new byte[stringBits.size()];

		for (int i = 0; i != stringBits.size(); ++i) {
			encodedTextBytes[i] = (byte)Integer.parseInt(stringBits.get(i), 2);
		}
		System.out.println(encodedTextBits);
		return encodedTextBytes;
	}


	//Decoder de bytes en binaire
	private String decodeText(byte[] fichierEncoder) {
		StringBuilder sbEncodedText = new StringBuilder();
		StringBuilder sbText = new StringBuilder();
		String padString = "00000000";
		String encodedTextPart = "";
		String encodedText = "";
		String text = "";

		for (int i =0; i != fichierEncoder.length; ++i) {
			encodedTextPart = Integer.toBinaryString(fichierEncoder[i] & 0xFF);
			if (i != fichierEncoder.length - 1) {
				sbEncodedText.append(padString.substring(encodedTextPart.length()));
			} else {
				sbEncodedText.append(padString.substring(encodedTextPart.length()+this.paddingBits));
			}
			sbEncodedText.append(encodedTextPart);
		}

		encodedText = sbEncodedText.toString();
		/*
		if (tree instanceof Node) {
			Node currentNode = (Node)tree;

			for (int i = 0; i != encodedText.length(); ++i) {
				if (encodedText.charAt(i) == '0') {
					if (currentNode.getLeftBranch() instanceof Node) {
						currentNode = (Node)currentNode.getLeftBranch();
					} else if (currentNode.getLeftBranch() instanceof Leaf) {
						sbText.append(((Leaf)currentNode.getLeftBranch()).getCharacter());
						currentNode = (Node)tree;
					}
				} else if (encodedText.charAt(i) == '1') {
					if (currentNode.getRightBranch() instanceof Node) {
						currentNode = (Node)currentNode.getRightBranch();
					} else if (currentNode.getRightBranch() instanceof Leaf) {
						sbText.append(((Leaf)currentNode.getRightBranch()).getCharacter());
						currentNode = (Node)tree;
					}
				}

			}
		} else if (tree instanceof Leaf) {
			Leaf currentLeaf = (Leaf)tree;

			for (int i = 0; i != encodedText.length(); ++i) {
				sbText.append(currentLeaf.getCharacter());
			}
		}
		*/

		System.out.println(encodedText);
		return "";

		//text = sbText.toString();

		//return text;
	}


}
