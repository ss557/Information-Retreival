/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.indexer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 THis class is responsible for assigning a partition to a
 *         given term. The static methods imply that all instances of this class
 *         should behave exactly the same. Given a term, irrespective of what
 *         instance is called, the same partition number should be assigned to
 *         it.
 */
public class Partitioner {
	private static final int partitionNumber = 6;
	private static String[] partitionList = { "^[A-Da-d]", "^[f-jF-J]", "^[K-Ok-o]",
			"^[P-Rp-r]", "^[STst]", "^[eEU-Zu-z]" };

	/**
	 * Method to get the total number of partitions THis is a pure design choice
	 * on how many partitions you need and also how they are assigned.
	 * 
	 * @return: Total number of partitions
	 */
	public static int getNumPartitions() {
		return partitionNumber;
	}

	/**
	 * Method to fetch the partition number for the given term. The partition
	 * numbers should be assigned from 0 to N-1 where N is the total number of
	 * partitions.
	 * 
	 * @param term
	 *            : The term to be looked up
	 * @return The assigned partition number for the given term
	 */
	public static int getPartitionNumber(String term) {
		for (int i = 0; i < partitionNumber; i++) {
			Pattern pattern = Pattern
					.compile(partitionList[i]);
			Matcher matcher = pattern.matcher(term.toString());
			if (matcher.find()) {
				return i;
			}
		}
		return -1;
	}
	
}
