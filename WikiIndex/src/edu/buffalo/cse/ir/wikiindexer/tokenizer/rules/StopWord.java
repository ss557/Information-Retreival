/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.tokenizer.rules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerException;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.TokenizerRule.RULENAMES;

/**
 * An implementation of the Porter stemmer for English THis is from the author's
 * website directly Wrapped in the framework class
 * 
 
 * 
 */
// example of annotation, for classes you write annotate accordingly
@RuleClass(className = RULENAMES.STOPWORDS)
public class StopWord implements TokenizerRule {
	String[] stopWordlist = { "a", "all", "am", "an", "and", "any", "are",
			"as", "at", "be", "been", "but", "by", "cannot", "could",
			"did", "do", "does", "for", "from", "had", "has", "have", "he",
			"her", "here", "him", "his", "how", "if", "in", "into", "is",
			"it", "no", "nor", "not", "of", "off", "on", "or", "she",
			"should", "so", "than", "that", "the", "their", "them", "then",
			"there", "these", "they", "this", "those", "to", "too", "very",
			"was", "we", "were", "what", "when", "where", "which", "while",
			"who", "whom", "why", "with", "would", "you","s","us" };
	  
	   
      List<String> wordList = Arrays.asList(stopWordlist); 
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerRule#apply(edu.buffalo
	 * .cse.ir.wikiindexer.tokenizer.TokenStream)
	 */
	public void apply(TokenStream stream) throws TokenizerException {
		if (stream != null) {
			stream.reset();
			String token;
			while (stream.hasNext()) {
				token = stream.next();
				if (token == null) {
				} else {
					token = replace(token,"[\\n\\r]", "");
					token = replace(token,"[ ][ ]*", " ");
					String[] tokens = token.split(" ");
					token = "";
					for (int i = 0; i < tokens.length; i++) {
						String temp = isStopWord(tokens[i]);
						if (tokens[i].equals(temp))
							token += " " + temp;
					}
					if (token.matches(".*[ ][ ]+")) {
						token = replace(token,"[ ][ ]+", "");
					}
					if (token.matches("[ ]+.*")) {
						token = token.replaceFirst("[ ]", "");
					}
					// trailing space
					if (token.matches(".*[ ]+")) {
						token = token.substring(0, token.length() - 1);
					}
				}
				if (token != "") {
					stream.previous();
					stream.set(token);
					stream.next();
				} else {
					stream.previous();
					stream.remove();
				}
			}
		}
	}

	private static String replace(String s,String input,String output){
		StringBuffer sb = new StringBuffer();
		Pattern pattern = Pattern
				.compile(input);
		Matcher matcher = pattern.matcher(s);
		while (matcher.find()) {
			matcher.appendReplacement(sb,output);
		}
		sb = matcher.appendTail(sb);
		return sb.toString();
	}
	/*
	 * Method to check if the given token is purely alphabetic in nature
	 * 
	 * @param token: The token to be checked
	 * 
	 * @return true if consists only of characters, false otherwise
	 */
	@SuppressWarnings("unused")
	private String isStopWord(String token) {
		String flag = token;
		if (wordList.contains(token.toLowerCase()))
				flag = "";
		return flag;
	}
}
