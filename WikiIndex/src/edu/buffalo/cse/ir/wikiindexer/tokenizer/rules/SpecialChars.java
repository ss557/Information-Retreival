/**
 * 
 */

package edu.buffalo.cse.ir.wikiindexer.tokenizer.rules;

import java.util.Collection;
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
@RuleClass(className = RULENAMES.SPECIALCHARS)
public class SpecialChars implements TokenizerRule {

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
				if (token != null) {
					if (token.matches("[^A-Za-z0-9 \\.\\?\\!\\-]")) {
						stream.previous();
						stream.remove();
					} else if (token.matches(".*[^A-Za-z0-9 \\.\\?\\!\\-].*")) {
						token = replace(token,"[^A-Za-z0-9 \\.\\?\\!\\-]"," ");
						token = remove_unwantedspace(token);
						if(token.equals("")){
							stream.previous();
							stream.remove();
						}
						if (token.matches(".*[ ]+.*")) {
							String[] tokensbuffer = token.trim().split(" ");
							stream.previous();
							stream.set(tokensbuffer);
							stream.next();
						} else {
							stream.previous();
							stream.set(token);
							stream.next();
						}
					}
				}
			}
			//System.out.println(stream.getAllTokens());
		}
	}

	/**
	 * Method to check if the given token is purely alphabetic in nature
	 * 
	 * @param token
	 *            : The token to be checked
	 * @return true if consists only of characters, false otherwise
	 */
	private String remove_unwantedspace(String token) {
		token = replace(token,"[\\n\\r]", " ");
		token = replace(token,"[ ][ ]+[ ]*", " ");
		// remove leading space
		if (token.matches("[ ]+.*")) {
			token = token.replaceFirst("[ ]", "");
		}
		// trailing space
		if (token.matches(".*[ ]+")) {
			token = token.substring(0, token.length() - 1);
		}
		return token;
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

}
