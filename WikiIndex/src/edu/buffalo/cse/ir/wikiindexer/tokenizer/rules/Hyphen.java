/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.tokenizer.rules;

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
@RuleClass(className = RULENAMES.HYPHEN)
public class Hyphen implements TokenizerRule {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerRule#apply(edu.buffalo
	 * .cse.ir.wikiindexer.tokenizer.TokenStream)
	 */
	public void apply(TokenStream stream) throws TokenizerException {
		if (stream != null) {
			String Token;
			stream.reset();
			while (stream.hasNext()) {
				Token = stream.next();
				if (Token != null) {
					if (Token.matches("[ ]*[\\-]+[ ]*")) {
						stream.previous();
						stream.remove();
						continue;
					}
					if (Token.matches("([A-Za-z]*[0-9]+[A-Za-z0-9]*[\\-]+[0-9]*[A-Za-z]+[A-Za-z0-9]*)|([0-9]*[A-Za-z]+[A-Za-z0-9]*[\\-]+[A-Za-z]*[0-9]+[A-Za-z0-9]*)")) {

					} else if (Token.matches("[A-Za-z ]+[\\-]+[A-Za-z ]+")) {
						Token = replace(Token,"[ ]*[\\-][ ]*", " ");
						Token = replace(Token,"[\\-].*[\\-]*", "");
						stream.previous();
						stream.set(Token);
						stream.next();
					} else if (Token
							.matches("([^A-Za-z]*[0-9]+[^A-Za-z]*[0-9]*[\\-]+[0-9]*[^A-Za-z]+[^A-Za-z]*[0-9]*)|([0-9]*[^A-Za-z]+[^A-Za-z]*[0-9]*[\\-]+[^A-Za-z]*[0-9]+[^A-Za-z]*[0-9]*)")) {

					} else if (Token.matches("[\\-]*[A-Za-z]+[\\-]*")) {
						Token = replace(Token,"[ ]*[\\-]+[ ]*", "");
						if (Token.equals("")) {
							stream.previous();
							stream.remove();
						} else {
							stream.previous();
							stream.set(Token);
							stream.next();
						}
						// delete the token
					}
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
	/**
	 * Method to check if the given token is purely alphabetic in nature
	 * 
	 * @param token
	 *            : The token to be checked
	 * @return true if consists only of characters, false otherwise
	 */
}
