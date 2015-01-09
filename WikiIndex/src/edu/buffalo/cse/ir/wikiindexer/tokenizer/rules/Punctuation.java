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
@RuleClass(className = RULENAMES.PUNCTUATION)
public class Punctuation implements TokenizerRule {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerRule#apply(edu.buffalo
	 * .cse.ir.wikiindexer.tokenizer.TokenStream)
	 */
	public void apply(TokenStream stream) throws TokenizerException {
		String token = "";
		if (stream != null) {
			stream.reset();
			while (stream.hasNext()) {
				token = stream.next();
				if (token == null || token.equals("") || token.equals("[ ]+")) {
					stream.previous();
					stream.remove();
				} else {

					if (token.matches(".*[\\.\\!\\,\\?\\,].*")) {
						if (token.matches("[0-9]+[\\.\\!\\,\\?][0-9]+.*")) {
							if(token.matches(".*[\\!\\,\\?]")){
								token = replace(token,"[\\!\\,\\?\\,]","");
							}
						} else if(token.matches("[\\.\\!\\,\\?].*")) {
							
						} else if(token.matches(".*[\\.\\!\\,\\?\\,]")){
							token = replace(token,"[\\.\\!\\,\\?\\,]","");
						}
						else if(token.matches(".*[A-Za-z]+[\\.\\!\\,\\?][A-Za-z]+.*")){
							
						}
						else {
							token = replace(token, "[\\.\\!\\,\\?]+", "");
						}
					} else {
						token = replace(token, "[\\.\\!\\,\\?]", "");
					}
					if (token.matches(".*[ ][ ]+")) {
						token = replace(token, "[ ][ ]+", "");
					}
					// trailing space
					if (token.matches(".*[ ]+")) {
						token = token.substring(0, token.length() - 1);
					}
					stream.previous();
					stream.set(token);
					stream.next();

				}
			}
		}
	}

	private static String replace(String s, String input, String output) {
		StringBuffer sb = new StringBuffer();
		Pattern pattern = Pattern.compile(input);
		Matcher matcher = pattern.matcher(s);
		while (matcher.find()) {
			matcher.appendReplacement(sb, output);
		}
		sb = matcher.appendTail(sb);
		return sb.toString();
	}

	/**
	 * Method to check if the given token is punctuation
	 * 
	 * @param token
	 *            : The token to be checked
	 * @return true if consists only of characters, false otherwise
	 */

}
