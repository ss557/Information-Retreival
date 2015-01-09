/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.indexer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Properties;
import java.util.SortedMap;
import java.util.TreeMap;

import edu.buffalo.cse.ir.wikiindexer.IndexerConstants;

/**
 This class is used to introspect a given index The
 *         expectation is the class should be able to read the index and all
 *         associated dictionaries.
 */
public class IndexReader {
	public LinkedList<HashMap<String, HashMap<String, Integer>>> readTermList = new LinkedList<HashMap<String, HashMap<String, Integer>>>();
	public ListIterator<HashMap<String, HashMap<String, Integer>>> listIter = readTermList
			.listIterator();
	public HashMap<String, HashMap<String, Integer>> writeMap = new HashMap<String, HashMap<String, Integer>>();
	Properties properties = new Properties();
	private Properties prop;
	private INDEXFIELD indKey, indVal;
	private Boolean isFrd;
	private int pNum;

	/**
	 * Constructor to create an instance
	 * 
	 * @param props
	 *            : The properties file
	 * @param field
	 *            : The index field whose index is to be read
	 */
	public IndexReader(Properties props, INDEXFIELD field) {
		indKey = field;
		prop = props;
		try {
			if (indKey == INDEXFIELD.TERM) {
				HashMap<String, HashMap<String, Integer>> readSingleTerm = new HashMap<String, HashMap<String, Integer>>();
				for (int i = 0; i < Partitioner.getNumPartitions(); i++) {
					 File file = new File(prop.get(IndexerConstants.TEMP_DIR)
					 + "\\writer" + indKey + i + ".txt");
					FileInputStream f = new FileInputStream(file);
					ObjectInputStream s = new ObjectInputStream(f);
					readSingleTerm = (HashMap<String, HashMap<String, Integer>>) s
							.readObject();
					listIter.add(readSingleTerm);
					System.out.println(readSingleTerm.size());
					s.close();
				}
				listIter = readTermList.listIterator();
			} else {
				 File file = new File(prop.get(IndexerConstants.TEMP_DIR)
				 + "\\writer" + indKey +"0"+ ".txt");
				
				FileInputStream f = new FileInputStream(file);
				ObjectInputStream s = new ObjectInputStream(f);
				writeMap = (HashMap<String, HashMap<String, Integer>>) s
						.readObject();
				s.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Method to get the total number of terms in the key dictionary
	 * 
	 * @return The total number of terms as above
	 */
	public int getTotalKeyTerms() {
		int size = 0;
		if (indKey == INDEXFIELD.TERM) {
			while (listIter.hasNext()) {
				size = size + listIter.next().size();
			}
		} else {
			size = writeMap.size();
		}
		return size;
	}

	/**
	 * Method to get the total number of terms in the value dictionary
	 * 
	 * @return The total number of terms as above
	 */
	public int getTotalValueTerms() {
		int size = 0;
		if (indKey == INDEXFIELD.TERM) {
			while (listIter.hasNext()) {
				size = size + listIter.next().size();
			}
		} else {
			size = writeMap.size();
		}
		return size;
	}

	/**
	 * Method to retrieve the postings list for a given dictionary term
	 * 
	 * @param key
	 *            : The dictionary term to be queried
	 * @return The postings list with the value term as the key and the number
	 *         of occurrences as value. An ordering is not expected on the map
	 */
	public Map<String, Integer> getPostings(String key) {
		HashMap<String, HashMap<String, Integer>> postList = new HashMap<String, HashMap<String, Integer>>();
		if (indKey == INDEXFIELD.TERM) {
			while (listIter.hasNext()) {
				postList = listIter.next();
				if (postList.containsKey(key)) {
					listIter = readTermList.listIterator();
					return postList.get(key);
				}
			}
			readTermList.listIterator();
			return null;
		} else {
			if (writeMap.containsKey(key)) {
				return writeMap.get(key);
			} else {
				return null;
			}
		}
	}

	/**
	 * Method to get the top k key terms from the given index The top here
	 * refers to the largest size of postings.
	 * 
	 * @param k
	 *            : The number of postings list requested
	 * @return An ordered collection of dictionary terms that satisfy the
	 *         requirement If k is more than the total size of the index, return
	 *         the full index and don't pad the collection. Return null in case
	 *         of an error or invalid inputs
	 */

	/* Referred StackOverflow for the below logic */
	class compareSecond implements Comparator<String> {
		Map<String, Integer> sec;
		public compareSecond(Map<String, Integer> sec) {
			this.sec = sec;
		}
		public int compare(String a, String b) {
			if (sec.get(a) >= sec.get(b)) {
				return -1;
			} else {
				return 1;
			} // returning 0 would merge keys
		}
	}

	public Collection<String> getTopK(int k) {
		HashMap<String, Integer> sm = new HashMap<String, Integer>();
		if (indKey == INDEXFIELD.TERM) {
			while (listIter.hasNext()) {
				for (Map.Entry<String, HashMap<String, Integer>> s : listIter
						.next().entrySet()) {
					HashMap<String, Integer> a = s.getValue();
					sm.put(s.getKey(), a.size());
				}
			}
		} else {
			for (Map.Entry<String, HashMap<String, Integer>> s : writeMap
					.entrySet()) {
				HashMap<String, Integer> a = s.getValue();
				for (Map.Entry<String, Integer> m : a.entrySet()) {
					sm.put(s.getKey(), a.size());
				}
			}
		}
		compareSecond bvc = new compareSecond(sm);
		TreeMap<String, Integer> sorted_map = new TreeMap<String, Integer>(bvc);
		sorted_map.putAll(sm);
		System.out.println("results: " + sorted_map);

		Collection<String> colTop = sorted_map.keySet();
		Iterator<String> colI = colTop.iterator();
		Collection<String> colTopK = new HashSet<String>();
		int count = 0;
		while (colI.hasNext() && count < k) {
			colTopK.add((String) colI.next());
			count++;
		}
		return colTopK;
	}

	/**
	 * Method to execute a boolean AND query on the index
	 * 
	 * @param terms
	 *            The terms to be queried on
	 * @return An ordered map containing the results of the query The key is the
	 *         value field of the dictionary and the value is the sum of
	 *         occurrences across the different postings. The value with the
	 *         highest cumulative count should be the first entry in the map.
	 */
	public Map<String, Integer> query(String... terms) {
		String[] ter = terms;
		List<Map<String, Integer>> sm = new LinkedList<Map<String, Integer>>();
		Map<String, Integer> s = new HashMap<String, Integer>();
		for (int i = 0; i < ter.length; i++) {
			s = getPostings(ter[0]);
			sm.add(s);
		}
		Iterator<Map<String, Integer>> iL = sm.iterator();
		s = new HashMap<String, Integer>();
		iL = sm.iterator();
		s = iL.next();
		Map<String, Integer> result = new HashMap<String, Integer>();
		while (iL.hasNext()) {
			for (Map.Entry<String, Integer> match : iL.next()
					.entrySet()) {
				if(s.containsKey(match.getKey())){
					if(result.containsKey(match.getKey())){
						result.put(match.getKey(), result.get(match.getKey())+match.getValue());
					} else {
						result.put(match.getKey(), match.getValue());
					}
					
				}
			}
		}
				
		return result;
	}
}
