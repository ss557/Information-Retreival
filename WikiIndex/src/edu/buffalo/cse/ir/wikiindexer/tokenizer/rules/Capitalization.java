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
@RuleClass(className = RULENAMES.CAPITALIZATION)
public class Capitalization implements TokenizerRule {

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
			boolean Flag_previnitcap = false, Flag_endofline = true;
			String token;
			while (stream.hasNext()) {
				token = stream.next();
				if (token != null) {
					token = replace(token,"[ ][ ]*", " ");
					if (token.matches("([\\S]*[\\s]*]*[ ]*[a-zA-Z]+[A-Z][a-z]*[ ]*[\\S]*[\\s]*]*)")) {
						Flag_previnitcap = false;
					} else if (token.matches("[A-Z]+")) {
						Flag_previnitcap = false;
					} else if (token.matches("[A-Z][a-z \\'\\.]+")) {

						if (Flag_previnitcap) {
							//stream.previous();
							//stream.mergeWithPrevious();
							//System.out.println("here");
						} else if (Flag_endofline) {
							token = token.toLowerCase();
						} else {
							Flag_previnitcap = true;
						}
					} else {
						Flag_previnitcap = false;
						token = token.toLowerCase();
					}

					if (token.matches(".*[\\.\\?\\!]"))
						Flag_endofline = true;
					else
						Flag_endofline = false;
				}
				stream.previous();
				stream.set(token);
				stream.next();
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
