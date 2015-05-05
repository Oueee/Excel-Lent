package excel;


import java.util.Map;
import java.util.TreeMap;
import java.util.ArrayList;
import java.util.List;


public class Box {

  public List<TreeMap<String, Integer>> l = new ArrayList<TreeMap<String, Integer>>(3);

	public int nCds = 0;
	public int nCdsNot = 0;
  public int nbNucleotides = 0;

  public Box(List<TreeMap<String, Integer>> l,
             int noCdsTraitees,
           	 int noCdsNonTraitees,
             int nbNucleotides)
  {
    this.l = l;
    this.nCds = nCds;
    this.nCdsNot = nCdsNot;
    this.nbNucleotides = nbNucleotides;
  }

  public void add(Box b) {
    this.nCds += b.nCds;
    this.nCdsNot += b.nCdsNot;
    this.nbNucleotides += b.nbNucleotides;
  }
}
