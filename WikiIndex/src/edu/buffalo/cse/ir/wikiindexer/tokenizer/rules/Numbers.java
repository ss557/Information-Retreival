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
@RuleClass(className = RULENAMES.NUMBERS)
public class Numbers implements TokenizerRule {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerRule#apply(edu.buffalo
	 * .cse.ir.wikiindexer.tokenizer.TokenStream)
	 */
	public void apply(TokenStream stream) throws TokenizerException {
		if (stream != null) {
			String token;
			stream.reset();
			while (stream.hasNext()) {
				token = stream.next();
				if (token != null) {
					if (token.matches("[0-9 \\,\\.\\%\\/\\+\\-\\*\\#]*")) {
						token = replace(token,"[0-9\\,\\.]", "");
						// word which contains punctuations are returned, next
						// step is to remove punctuation and store the word in
						// the stream.
						if (token.equals("")) {
							stream.previous();
							stream.remove();
						} else {
							stream.previous();
							stream.set(token);
							stream.next();
						}
					}
					if(token.matches(".*[nN][oO]\\.[0-9 ]*")){
						stream.previous();
						stream.remove();
					}
					token = replace(token,"[ ][ ]+"," ");	
					token = replace(token,"[\\n\\r]"," ");
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
