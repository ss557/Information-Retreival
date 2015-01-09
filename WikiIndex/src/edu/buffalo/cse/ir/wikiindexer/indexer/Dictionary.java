/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.indexer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.SortedMap;
import java.util.TreeMap;

import edu.buffalo.cse.ir.wikiindexer.IndexerConstants;

/**
 *
 * An abstract class that represents a dictionary object for a given index
 */
public abstract class Dictionary implements Writeable {
	int id = 0;
	Map<String,Integer> mapDict = new HashMap<String,Integer>();
	INDEXFIELD ind;
	Properties prop;
	
	public Dictionary (Properties props, INDEXFIELD field) {
		ind = field;
		prop = props;
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.ir.wikiindexer.indexer.Writeable#writeToDisk()
	 */
	public void writeToDisk() throws IndexerException {
		try {
			File file = new File(prop.get(IndexerConstants.TEMP_DIR)+"\\DocumentDictionary.txt");
			FileOutputStream f = new FileOutputStream(file);
			ObjectOutputStream s = new ObjectOutputStream(f);
			s.writeObject(mapDict);
			s.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see edu.buffalo.cse.ir.wikiindexer.indexer.Writeable#cleanUp()
	 */
	public void cleanUp() {
		System.out.println("Link count "+ mapDict.size());
		mapDict.clear();
		mapDict = null;

	}
	
	/**
	 * Method to check if the given value exists in the dictionary or not
	 * Unlike the subclassed lookup methods, it only checks if the value exists
	 * and does not change the underlying data structure
	 * @param value: The value to be looked up
	 * @return true if found, false otherwise
	 */
	public boolean exists(String value) {
		if(mapDict.containsKey(value)){
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * MEthod to lookup a given string from the dictionary.
	 * The query string can be an exact match or have wild cards (* and ?)
	 * Must be implemented ONLY AS A BONUS
	 * @param queryStr: The query string to be searched
	 * @return A collection of ordered strings enumerating all matches if found
	 * null if no match is found
	 */
	public Collection<String> query(String queryStr) {
		queryStr = queryStr.replace("*", ".*");
		queryStr = queryStr.replace("?", ".");
		Collection<String> colQuer = new HashSet<String>();
		Boolean preFlag = false;
		for(String str:mapDict.keySet()){
			if(str.matches(queryStr)){
				colQuer.add(str);
				preFlag = true;
			}
		}
		if(preFlag)
			return colQuer;
		else
			return null;
	}
	
	/**
	 * Method to get the total number of terms in the dictionary
	 * @return The size of the dictionary
	 */
	public int getTotalTerms() {
		return mapDict.size();
	}
}
