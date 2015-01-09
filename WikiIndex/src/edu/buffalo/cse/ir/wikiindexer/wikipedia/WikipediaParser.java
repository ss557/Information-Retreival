/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.wikipedia;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 This class implements Wikipedia markup processing. Wikipedia
 *         markup details are presented here:
 *         http://en.wikipedia.org/wiki/Help:Wiki_markup It is expected that all
 *         methods marked "todo" will be implemented by students. All methods
 *         are static as the class is not expected to maintain any state.
 */
public class WikipediaParser {

	private static WikipediaDocument wikiD;
	private static boolean isCategory = false;

	/* TODO */
	/**
	 * Method to parse section titles or headings. Refer:
	 * http://en.wikipedia.org/wiki/Help:Wiki_markup#Sections
	 * 
	 * @param titleStr
	 *            : The string to be parsed
	 * @return The parsed string with the markup removed
	 */

	public static String parseSectionTitle(String titleStr) {
		if (titleStr == null) {
			return null;
		}
		StringBuffer sb = new StringBuffer();
		// parseText = parseSectionTitle(parseText);
		Pattern pattern = Pattern
				.compile("(\\=\\=+)([A-Za-z0-9 \\(\\)\\,\\-\\'\\:\\|\\.\\;\\#\\*]*)(\\=\\=+)");
		Matcher matcher = pattern.matcher(titleStr);
		while (matcher.find()) {
			String matched = matcher.group(2);
			matched = parseSectionTitle(matched);
			String qs = Matcher.quoteReplacement(matched);
			matcher.appendReplacement(sb, qs);
		}
		sb = matcher.appendTail(sb);
		titleStr = sb.toString();
		if (titleStr.matches("[ ]+.*")) {
			titleStr = titleStr.replaceFirst("[ ]", "");
		}
		if (titleStr.matches(".*[ ]+")) {
			titleStr = titleStr.substring(0, titleStr.length() - 1);
		}
		return titleStr;
	}

	public static void parseSection(String titleStr) {
		int i = 0;
		titleStr = titleStr.replaceAll("[\\=][\\=]+", "==");
		if (titleStr.contains("^[\\=\\=].*")) {

		} else {
			titleStr = "Default==" + titleStr;
		}
		String[] section_buffer = titleStr.split("==");
		int no_of_sections = section_buffer.length;
		String[][] array_section = new String[200][2];
		int cnt = 0;
		for (i = 0; i < no_of_sections - 1; i = i + 2) {
			array_section[cnt][0] = section_buffer[(i)];
			array_section[cnt][1] = section_buffer[(i + 1)];
			if (array_section[cnt][1].contains("[ ]+.*")) {
				array_section[cnt][1] = array_section[i][1].replaceFirst("[ ]",
						"");
			}
			// trailing space
			if (array_section[cnt][1].contains(".*[ ]+")) {
				array_section[cnt][1] = array_section[i][1].substring(0,
						array_section[i][1].length() - 1);
			}
			if (array_section[cnt][0].contains("[ ]+.*")) {
				array_section[cnt][0] = array_section[i][0].replaceFirst("[ ]",
						"");
			}
			if (array_section[cnt][0].contains(".*[ ]+")) {
				array_section[cnt][0] = array_section[i][0].substring(0,
						array_section[i][0].length() - 1);
			}

			wikiD.addSection(array_section[cnt][0], array_section[cnt][1]);
			cnt++;
		}
	}

	public static WikipediaDocument parseText(String text,
			WikipediaDocument wikiDoc) {
		wikiD = wikiDoc;

		String parseText = parseTextFormatting(text);
		parseText = parseLinkText(parseText);
		parseText = parseListItem(parseText);
		parseText = parseTemplates(parseText);
		parseText = parseTagFormatting(parseText);
		parseSection(parseText);
		parseText = parseSectionTitle(parseText);
		// System.out.println("After Everything" + parseText);
		return wikiD;
	}

	/* TODO */
	/**
	 * Method to parse list items (ordered, unordered and definition lists).
	 * Refer: http://en.wikipedia.org/wiki/Help:Wiki_markup#Lists
	 * 
	 * @param itemText
	 *            : The string to be parsed
	 * @return The parsed string with markup removed
	 */
	public static String parseListItem(String itemText) {
		if (itemText == null) {
			return null;
		}
		StringBuffer sb = new StringBuffer();
		Pattern pattern = Pattern.compile("([\\*\\#\\:]+[ ])(.*?)");
		Matcher matcher = pattern.matcher(itemText);
		while (matcher.find()) {
			String matched = matcher.group(2);
			String qs = Matcher.quoteReplacement(matched);
			matcher.appendReplacement(sb, qs);
		}
		sb = matcher.appendTail(sb);
		return sb.toString();
	}

	/* TODO */
	/**
	 * Method to parse text formatting: bold and italics. Refer:
	 * http://en.wikipedia.org/wiki/Help:Wiki_markup#Text_formatting first point
	 * 
	 * @param text
	 *            : The text to be parsed
	 * @return The parsed text with the markup removed
	 */
	public static String parseTextFormatting(String text) {
		if (text == null) {
			return null;
		}
		text = text.replaceAll("[\\'][\\']+", "");
		return text;
	}

	/* TODO */
	/**
	 * Method to parse *any* HTML style tags like: <xyz ...> </xyz> For most
	 * cases, simply removing the tags should work.
	 * 
	 * @param text
	 *            : The text to be parsed
	 * @return The parsed text with the markup removed.
	 */
	public static String parseTagFormatting(String text) {
		if (text == null) {
			return null;
		}
		text = text.replaceAll("&lt;", "<");
		text = text.replaceAll("&gt;", ">");
		StringBuffer sb = new StringBuffer();
		Pattern pattern = Pattern
				.compile("(\\<)([A-Za-z0-9 \\\n\\\r\\{\\}\\[\\]\\(\\)\\,\\-\\'\\:\\|\\.\\;\\#\\*\\!\\/\\_\\=\\\"\\&\\/]*)(\\>)");
		Matcher matcher = pattern.matcher(text.toString());
		while (matcher.find()) {
			matcher.appendReplacement(sb, "");
		}
		sb = matcher.appendTail(sb);
		text = sb.toString();
		if (text.matches("[ ].*")) {
			text = text.replaceFirst("[ ]", "");
		}
		if (text.matches(".*[ ]")) {
			text = text.substring(0, text.length() - 1);
		}
		text = text.replaceAll("[ ][ ]+", " ");
		return text;

	}

	/* TODO */
	/**
	 * Method to parse wikipedia templates. These are *any* {{xyz}} tags For
	 * most cases, simply removing the tags should work.
	 * 
	 * @param text
	 *            : The text to be parsed
	 * @return The parsed text with the markup removed
	 */
	public static String parseTemplates(String text) {
		StringBuffer sb = new StringBuffer();
		Pattern pattern = Pattern
				.compile("(\\{\\{)([A-Za-z0-9 \\t\\n\\[\\]\\(\\)\\,\\_\\-\\'\\:\\|\\.\\;\\#\\*\\&\\\"\\@\\!\\%\\^\\=\\/\\<\\>]*)(\\}\\})");
		Matcher matcher = pattern.matcher(text.toString());
		while (matcher.find()) {
			matcher.appendReplacement(sb, "");
		}
		sb = matcher.appendTail(sb);
		StringBuffer sb1 = new StringBuffer();
		for (int i = 0; i < sb.length(); i++) {
			if (sb.charAt(i) == '{' && sb.charAt(i + 1) == '{') {
				while (sb.charAt(i) != '}') {
					i++;
				}
				i++;
			} else {
				sb1.append(sb.charAt(i));
			}

		}
		return sb1.toString();
	}

	/* TODO */
	/**
	 * Method to parse links and URLs. Refer:
	 * http://en.wikipedia.org/wiki/Help:Wiki_markup#Links_and_URLs
	 * 
	 * @param text
	 *            : The text to be parsed
	 * @return An array containing two elements as follows - The 0th element is
	 *         the parsed text as visible to the user on the page The 1st
	 *         element is the link url
	 */
	public static String[] parseLinks(String text) {
		String text_shown = "";
		String url = null;
		String[] return_token = new String[2];
		return_token[0] = new String();
		return_token[1] = new String();
		Boolean flag_text = false;
		Boolean flag_url = true;
		StringBuffer sb = new StringBuffer();
		String sb2 = new String();
		if ((text == null)) {
			return_token[0] = text;
			return_token[1] = text;
		} else {
			Pattern pattern = Pattern.compile("(.*)(\\[\\[.*?\\]\\])(.*)");
			Matcher matcher = pattern.matcher(text);
			String matched = new String();
			if (matcher.find()) {
				matched = matcher.group(2);
			}
			sb = matcher.appendTail(sb);
			sb2 = sb.toString().replaceAll("(\\[\\[*).*(\\]*\\])",
					"Aparsed textA");
			if (matched.equals("") || matched == null) {

			} else {
				text = matched;
			}
			if (text.matches("(\\[\\[).*(\\]\\])")) {
				text = text.replaceAll("(\\[\\[)|(\\]\\])", "");
				if (text.matches(".*[\\|].+")) {
					flag_text = true;
					text_shown = text.replaceAll(".*[\\|]", "");
					text = text.replaceAll("[\\|].*", "");
				}
				if (text.matches(".*[\\:].*")) {
					if (text.matches(".*[\\#].*")) {
					} else if (text.matches(".*[\\|]")) {
						text = text.replaceAll("(Wikipedia:|Wiktionary:)", "");
					} else if (text.matches("(Category:).*")) {
						text = text.replaceAll("Category:", "");
						isCategory = true;
					}
					if (!flag_text) {
						text_shown = text;
					}
					flag_url = false;
				}
				if (isRegexokay(text, flag_text) == 1) {
					if (flag_url) {
						url = text.replaceAll(" ", "_");
						url = url.replaceAll("[\\|]", "");
					}
					if (flag_text == false) {
						text_shown = text.replaceAll("[ ]*"
								+ "[\\(].*[\\)]+[ ]*[\\|]", "");
					}
				} else if (isRegexokay(text, flag_text) == 2) {
					if (flag_url) {
						url = text.replaceAll(" ", "_");
						url = url.replaceAll("[\\|]", "");
					}
					if (flag_text == false) {
						text_shown = text.replaceAll("[\\,].*", "");
					}
				} else if (isRegexokay(text, flag_text) == 3) {
					if (flag_text == false) {
						if (text.matches("(File:)" + ".*"))
							text_shown = "";
						else {
							if (text.matches(".*[\\|]"))
								text_shown = text.replaceAll("[\\|]", "");
							if (text.matches("[\\:].*"))
								text_shown = text.replaceAll("(:Category)",
										"Category");
						}
					}
				} else if (isRegexokay(text, flag_text) == 4) {
					text_shown = text.replaceAll("[\\|]", "");
				} else if (isRegexokay(text, flag_text) == 0) {
					if (!flag_text) {
						text_shown = text;
					}
					if (flag_url)
						url = text;
				}
			} else if (text.matches("(\\[\\[*).*(\\]*\\])")) {
				text_shown = text.replaceAll("(\\[)|((http://)"
						+ ".*[\\.]+[a-zA-Z0-9\\/\\-\\_\\?\\&\\#\\$]*)[ ]*", "");
				text_shown = text_shown.replaceAll("(\\])", "");
			}
		}
		if (text != null) {
			if (sb2 == null || sb2.equals("Aparsed textA")) {
				return_token[0] = text_shown;
			} else {
				sb2 = sb2.replaceFirst("Aparsed textA", text_shown);
				sb2 = sb2.replaceAll("(<.*>)", "");
				return_token[0] = sb2;
			}
		} else
			return_token[0] = "";
		if (url == null) {
			return_token[1] = "";
		} else {
			if (url.contains(" ")) {
				url = url.replaceAll(" ", "_");
			}
			return_token[1] = url.substring(0, 1).toUpperCase()
					+ url.substring(1);
		}
		// System.out.println("text="+return_token[0]);
		// System.out.println("url="+return_token[1]);
		return return_token;
	}

	public static String parseLinkText(String text) {
		StringBuffer sb = new StringBuffer();
		Pattern pattern = Pattern.compile("(\\[\\[*.*?\\]*\\])");
		Matcher matcher = pattern.matcher(text.toString());
		while (matcher.find()) {
			String matched = matcher.group(1);
			String[] pLinks = parseLinks(matched);
			if (isCategory) {
				wikiD.addCategory(pLinks[0]);
				isCategory = false;
			} else {
				wikiD.addLink(pLinks[1]);
			}
			String qs = Matcher.quoteReplacement(pLinks[0]);
			matcher.appendReplacement(sb, qs);
		}
		sb = matcher.appendTail(sb);
		return sb.toString();
	}

	private static int isRegexokay(String token, Boolean flg) {
		if (flg) {
			if (token.matches(".*[\\,].*")) {
				return 2;
			} else if (token.matches(".*[\\(].*[\\)].*")) {
				return 1;
			} else if (token.matches(".*[\\:].*")) {
				return 3;
			}
		} else {
			if (token.matches(".*[\\,].*[\\|]")) {
				return 2;
			} else if (token.matches(".*[\\(].*[\\)].*[\\|]")) {
				return 1;
			} else if (token.matches(".*[\\:].*")) {
				return 3;
			} else if (token.matches(".*[\\|]")) {
				return 4;
			}
		}
		return 0;
	}
}
