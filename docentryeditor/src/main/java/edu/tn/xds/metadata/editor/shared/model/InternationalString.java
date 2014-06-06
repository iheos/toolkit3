package edu.tn.xds.metadata.editor.shared.model;

import java.io.Serializable;

import javax.annotation.MatchesPattern;
import javax.validation.constraints.NotNull;

/**
 * <p>
 * <b> This class represents an InternationalString. </b>
 * </p>
 * 
 * 
 * <p>
 * It contains a toXML method to return the InternationalString in XML format.<br>
 * This class also contains getters/setters.</br> In addition, it has verify
 * method to check its syntax.
 * </p>
 * 
 * <p>
 * <b>See below each method mentioned above.</b> <br>
 * {@link #verify() method verify}</br> {@link #toXML() method toXML} <br>
 * </p>
 * 
 * 
 * @see DocumentModel class DocumentModel
 * @see ModelElement class ModelElement
 */
public class InternationalString implements ModelElement, Serializable {

	private static final long serialVersionUID = -25172629647304781L;
	/**
	 * <p>
	 * 
	 * <b>LanguageCode langCode</b> - the language code of the
	 * InternationalString [Mandatory].<br>
	 * Type: {@link String256}</br>
	 * </p>
	 * 
	 * 
	 * @see LanguageCode
	 * @see InternationalString
	 */
	@NotNull
	@MatchesPattern(value = "[a-z]{2}-[A-Z]{2}")
	LanguageCode langCode;

	/**
	 * <b>String256</b> - the value of the InternationalString [Mandatory].<br>
	 * Type: {@link String256}</br>
	 * 
	 * @see String256 class String256
	 * @see InternationalString
	 */
	@NotNull
	String256 value;

	public InternationalString() {
		// langCode = new String256();
		value = new String256();
	}

	public LanguageCode getLangCode() {
		return langCode;
	}

	public void setLangCode(LanguageCode langCode) {
		this.langCode = langCode;
	}

	public String256 getValue() {
		return value;
	}

	public void setValue(String256 value) {
		this.value = value;
	}

	/**
	 * 
	 * <p>
	 * <b>Method toXML</b> <br>
	 * This method will be called to build a XML file by the
	 * {@link DocumentModel} with the information taken from the local
	 * InternationalString.</br>
	 * </p>
	 * 
	 * @return String which contains the InternationalString in XML format
	 * 
	 * @see InternationalString class InternationalString
	 */
	public String toXML() {
		String answer = "\t\t<internationalstring>\n\t\t\t<language>" + langCode.toString() + "</language>\n\t\t\t<information>" + value.toString()
				+ "</information>\n\t\t</internationalstring>\n";
		return answer;
	}

	/**
	 * <p>
	 * <b>Method verify</b> <br>
	 * This method will be called to check whether the syntax's
	 * {@link InternationalString} is correct </br>
	 * </p>
	 * 
	 * @return boolean true if the syntax is correct, else return false
	 * @throws String256Exception
	 *             if there is a String256 with more than 256 characters
	 * 
	 * @see InternationalString class InternationalString
	 * 
	 */
	@Override
	public boolean verify() throws String256Exception {
		boolean answer = true;
		// answer = langCode.verify();
		answer = value.verify();
		return answer;
	}
}
