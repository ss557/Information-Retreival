/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.indexer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.Writer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import edu.buffalo.cse.ir.wikiindexer.IndexerConstants;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.Tokenizer;

/**
 This class is used to write an index to the disk
 * 
 */
public class IndexWriter implements Writeable {
	private HashMap<String, HashMap<String, Integer>> writeMap = new HashMap<String, HashMap<String, Integer>>();
	Properties properties = new Properties();
	private Properties prop;
	private INDEXFIELD indKey, indVal;
	private Boolean isFrd;
	private int pNum;

	/**
	 * Constructor that assumes the underlying index is inverted Every index
	 * (inverted or forward), has a key field and the value field The key field
	 * is the field on which the postings are aggregated The value field is the
	 * field whose postings we are accumulating For term index for example: Key:
	 * Term (or term id) - referenced by TERM INDEXFIELD Value: Document (or
	 * document id) - referenced by LINK INDEXFIELD
	 * 
	 * @param props
	 *            : The Properties file
	 * @param keyField
	 *            : The index field that is the key for this index
	 * @param valueField
	 *            : The index field that is the value for this index
	 */
	public IndexWriter(Properties props, INDEXFIELD keyField,
			INDEXFIELD valueField) {
		this(props, keyField, valueField, false);
	}

	/**
	 * Overloaded constructor that allows specifying the index type as inverted
	 * or forward Every index (inverted or forward), has a key field and the
	 * value field The key field is the field on which the postings are
	 * aggregated The value field is the field whose postings we are
	 * accumulating For term index for example: Key: Term (or term id) -
	 * referenced by TERM INDEXFIELD Value: Document (or document id) -
	 * referenced by LINK INDEXFIELD
	 * 
	 * @param props
	 *            : The Properties file
	 * @param keyField
	 *            : The index field that is the key for this index
	 * @param valueField
	 *            : The index field that is the value for this index
	 * @param isForward
	 *            : true if the index is a forward index, false if inverted
	 */
	public IndexWriter(Properties props, INDEXFIELD keyField,
			INDEXFIELD valueField, boolean isForward) {
		prop = props;
		indKey = keyField;
		indVal = valueField;
		isFrd = isForward;
	}

	/**
	 * Method to make the writer self aware of the current partition it is
	 * handling Applicable only for distributed indexes.
	 * 
	 * @param pnum
	 *            : The partition number
	 */
	public void setPartitionNumber(int pnum) {
		pNum = pnum;
	}

	/**
	 * Method to add a given key - value mapping to the index
	 * 
	 * @param keyId
	 *            : The id for the key field, pre-converted
	 * @param valueId
	 *            : The id for the value field, pre-converted
	 * @param numOccurances
	 *            : Number of times the value field is referenced by the key
	 *            field. Ignore if a forward index
	 * @throws IndexerException
	 *             : If any exception occurs while indexing
	 */
	public void addToIndex(int keyId, int valueId, int numOccurances)
			throws IndexerException {
		String key = Integer.toString(keyId);
		String value = Integer.toString(valueId);
		//System.out.println("int addtoindex");
		if (writeMap.containsKey(key)) {
			if (writeMap.get(key).containsKey(valueId)) {

			} else {
				writeMap.get(key).put(value, numOccurances);
			}
		} else {
			HashMap<String, Integer> hm = new HashMap<String, Integer>();
			hm.put(value, numOccurances);
			writeMap.put(key, hm);
		}
	}

	/**
	 * Method to add a given key - value mapping to the index
	 * 
	 * @param keyId
	 *            : The id for the key field, pre-converted
	 * @param value
	 *            : The value for the value field
	 * @param numOccurances
	 *            : Number of times the value field is referenced by the key
	 *            field. Ignore if a forward index
	 * @throws IndexerException
	 *             : If any exception occurs while indexing
	 */
	public void addToIndex(int keyId, String value, int numOccurances)
			throws IndexerException {
		// TODO: Implement this method
	}

	/**
	 * Method to add a given key - value mapping to the index
	 * 
	 * @param key
	 *            : The key for the key field
	 * @param valueId
	 *            : The id for the value field, pre-converted
	 * @param numOccurances
	 *            : Number of times the value field is referenced by the key
	 *            field. Ignore if a forward index
	 * @throws IndexerException
	 *             : If any exception occurs while indexing
	 */
	public void addToIndex(String key, int valueId, int numOccurances)
			throws IndexerException {
		String value = Integer.toString(valueId);
		//System.out.println(key);
		
		if (writeMap.containsKey(key)) {
			if (writeMap.get(key).containsKey(valueId)) {

			} else {
				writeMap.get(key).put(value, numOccurances);
			}
		} else {
			HashMap<String, Integer> hm = new HashMap<String, Integer>();
			hm.put(value, numOccurances);
			writeMap.put(key, hm);
		}
	}

	/**
	 * Method to add a given key - value mapping to the index
	 * 
	 * @param key
	 *            : The key for the key field
	 * @param value
	 *            : The value for the value field
	 * @param numOccurances
	 *            : Number of times the value field is referenced by the key
	 *            field. Ignore if a forward index
	 * @throws IndexerException
	 *             : If any exception occurs while indexing
	 */
	public void addToIndex(String key, String value, int numOccurances)
			throws IndexerException {
		// TODO: Implement this method
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.buffalo.cse.ir.wikiindexer.indexer.Writeable#writeToDisk()
	 */
	public void writeToDisk() throws IndexerException {
		try {
			File file = new File(prop.get(IndexerConstants.TEMP_DIR)+"\\writer"
							+ indKey + pNum + ".txt");
			FileOutputStream f = new FileOutputStream(file);
			ObjectOutputStream s = new ObjectOutputStream(f);
			s.writeObject(writeMap);
			s.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.buffalo.cse.ir.wikiindexer.indexer.Writeable#cleanUp()
	 */
	public void cleanUp() {
		/*for (Map.Entry<String, HashMap<String, Integer>> s : writeMap
				.entrySet()) {
			System.out.println(s.getKey());
			HashMap<String, Integer> a = s.getValue();
			for (Map.Entry<String, Integer> token : a.entrySet()) {
				System.out.println(token.getKey());
				System.out.println(token.getValue());
			}
		}*/
		System.out.println(this.indKey);
		System.out.println("End of bucket:" + pNum);
		System.out.println(writeMap.size());
		writeMap.clear();
		writeMap = null;
	}

}
