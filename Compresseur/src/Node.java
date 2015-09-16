/**
 * Created by maaj on 2015-09-16.
 */
public class Node {

    Object clef,freq;


    Node leftChild;
    Node rightChild;

    Node(Object clef,Object freq)
    {
        this.clef=clef;
        this.freq=freq;
    }

    public String toString()
    {
        return clef+ " a une frequence de  "+ freq;
    }

}
