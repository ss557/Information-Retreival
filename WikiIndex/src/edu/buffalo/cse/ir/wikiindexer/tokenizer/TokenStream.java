/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.tokenizer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

/**
 * This class represents a stream of tokens as the name suggests.
 * It wraps the token stream and provides utility methods to manipulate it
 *
 */
public class TokenStream implements Iterator<String>{
	List<String> tWords = new LinkedList<String>();
	ListIterator<String> tIter = tWords.listIterator();;
	/**
	 * Default constructor
	 * @param bldr: THe stringbuilder to seed the stream
	 */
	public TokenStream(StringBuilder bldr) {
		if(bldr == null || bldr.toString() == "") {
			return;
		}
		String tStream = bldr.toString();
		append(tStream);
	}
	
	/**
	 * Overloaded constructor
	 * @param bldr: THe stringbuilder to seed the stream
	 */
	public TokenStream(String string) {
		if(string == null || string ==""){
			tIter = tWords.listIterator();
			return;
		}
		append(string);
	}
	
	/**
	 * Method to append tokens to the stream
	 * @param tokens: The tokens to be appended
	 */
	public void append(String... tokens) {
		if(tokens == null){
			return;
		}
		if(tokens.length == 0){
			return;
		}
		seekEnd();
		for(int i=0;i<tokens.length;i++){
			if(tokens[i] == null || tokens[i] == ""){
				continue;
			}
			tIter.add(tokens[i]);
		}
		reset();
	}
	
	/**
	 * Method to retrieve a map of token to count mapping
	 * This map should contain the unique set of tokens as keys
	 * The values should be the number of occurrences of the token in the given stream
	 * @return The map as described above, no restrictions on ordering applicable
	 */
	public Map<String, Integer> getTokenMap() {
		if(tWords.size() == 0){
			return null;
		}
		Map<String,Integer> mToken = new HashMap<String,Integer>();
		reset();
		while(tIter.hasNext()){
			String iNext = tIter.next(); 
			if(mToken.containsKey(iNext)){
				mToken.put(iNext, mToken.get(iNext)+1);
			} else {
				mToken.put(iNext,1);
			}
		}
		//System.out.println(mToken);
		reset();
		return mToken;
	}
	
	/**
	 * Method to get the underlying token stream as a collection of tokens
	 * @return A collection containing the ordered tokens as wrapped by this stream
	 * Each token must be a separate element within the collection.
	 * Operations on the returned collection should NOT affect the token stream
	 */
	public Collection<String> getAllTokens() {
		if(tWords.isEmpty()){
			return null;
		}
		Collection<String> cWords = tWords;
		return cWords;
	}
	
	/**
	 * Method to query for the given token within the stream
	 * @param token: The token to be queried
	 * @return: THe number of times it occurs within the stream, 0 if not found
	 */
	public int query(String token) {
		Map<String,Integer> mToken = new HashMap<String,Integer>();
		reset();
		while(tIter.hasNext()){
			String iNext = tIter.next(); 
			
			if(mToken.containsKey(iNext)){
				mToken.put(iNext, mToken.get(iNext)+1);
			} else {
				mToken.put(iNext,1);
			}
		}
		reset();
		if(mToken.containsKey(token)){
			return mToken.get(token);
		} else {
			return 0;
		}
	}
	
	/**
	 * Iterator method: Method to check if the stream has any more tokens
	 * @return true if a token exists to iterate over, false otherwise
	 */
	public boolean hasNext() {
		
			return tIter.hasNext();
		
	}
	/**
	 * Iterator method: Method to check if the stream has any more tokens
	 * @return true if a token exists to iterate over, false otherwise
	 */
	public boolean hasPrevious() {
		return tIter.hasPrevious();
		
	}
	
	/**
	 * Iterator method: Method to get the next token from the stream
	 * Callers must call the set method to modify the token, changing the value
	 * of the token returned by this method must not alter the stream
	 * @return The next token from the stream, null if at the end
	 */
	public String next() {
		if(tIter.hasNext())
			return tIter.next();
		return null;
	}
	
	/**
	 * Iterator method: Method to get the previous token from the stream
	 * Callers must call the set method to modify the token, changing the value
	 * of the token returned by this method must not alter the stream
	 * @return The next token from the stream, null if at the end
	 */
	public String previous() {
		if(tIter.hasPrevious())
			return tIter.previous();
		return null;
	}
	
	/**
	 * Iterator method: Method to remove the current token from the stream
	 */
	public void remove() {
		if(!tWords.isEmpty() && tIter.hasNext()){
			tIter.next();
			tIter.remove();
		}
	}
	
	/**
	 * Method to merge the current token with the previous token, assumes whitespace
	 * separator between tokens when merged. The token iterator should now point
	 * to the newly merged token (i.e. the previous one)
	 * @return true if the merge succeeded, false otherwise
	 */
	public boolean mergeWithPrevious() {
		if(tIter.hasPrevious()){
			String current = previous();
			tIter.remove();
			String newWord;
			if(tIter.hasNext()){
				newWord = current + " " + next();
				tIter.remove();
				tIter.add(newWord);
				previous();
				return true;
			}
			else {
				newWord = current;
				tIter.add(newWord);
				previous();
				return false;
			}
		}
		return false;
	}
	
	/**
	 * Method to merge the current token with the next token, assumes whitespace
	 * separator between tokens when merged. The token iterator should now point
	 * to the newly merged token (i.e. the current one)
	 * @return true if the merge succeeded, false otherwise
	 */
	public boolean mergeWithNext() {
		if(tIter.hasNext()){
			String current = next();
			tIter.remove();
			String newWord;
			if(tIter.hasNext()){
				newWord = current + " " + next();
				tIter.remove();
				tIter.add(newWord);
				previous();
				return true;
			}
			else {
				newWord = current;
				tIter.add(newWord);
				previous();
				return false;
			}
		}
		return false;
	}
	
	/**
	 * Method to replace the current token with the given tokens
	 * The stream should be manipulated accordingly based upon the number of tokens set
	 * It is expected that remove will be called to delete a token instead of passing
	 * null or an empty string here.
	 * The iterator should point to the last set token, i.e, last token in the passed array.
	 * @param newValue: The array of new values with every new token as a separate element within the array
	 */
	public void set(String... newValue) {
		if(newValue == null || newValue[0] == null || newValue[0].equals("")|| newValue.length == 0 || tWords == null || tWords.size() ==0){
			return;
		}
		if(tIter.hasNext()){
			tIter.remove();
			for (int i=0;i<newValue.length;i++){
				tIter.add(newValue[i]);
			}
			
			tIter.previous();
		}
	}
	
	/**
	 * Iterator method: Method to reset the iterator to the start of the stream
	 * next must be called to get a token
	 */
	public void reset() {
		while(tIter.hasPrevious()){
			tIter.previous();
		}
	}
	
	/**
	 * Iterator method: Method to set the iterator to beyond the last token in the stream
	 * previous must be called to get a token
	 */
	public void seekEnd() {
		while(tIter.hasNext()){
			tIter.next();
		}
	}
	
	/**
	 * Method to merge this stream with another stream
	 * @param other: The stream to be merged
	 */
	public void merge(TokenStream other) {
		if(other == null || other.tWords.size() == 0){
			return;
		}
		seekEnd();
		while(other.hasNext()){
			String next = other.next();
			tIter.add(next);
		}
		reset();
	}

}
