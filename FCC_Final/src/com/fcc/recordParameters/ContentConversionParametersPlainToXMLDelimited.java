package com.fcc.recordParameters;

import java.util.ArrayList;

import com.fcc.util.ConversionPropHelper;
import com.fcc.util.DataField;







public class ContentConversionParametersPlainToXMLDelimited extends ContentConversionParametersPlainToXML {

	private boolean enclosureConversion;

	private String enclBegin;

	private String enclEnd;

	private String enclBeginEsc;

	private String enclEndEsc;



	public ContentConversionParametersPlainToXMLDelimited(String fieldSeparator, String[] fixedLengths) {

		super(fieldSeparator, fixedLengths);

	}



	public void setAdditionalParameters(String recordTypeName, String[] recordsetList, ConversionPropHelper param) throws Exception  {

		super.setAdditionalParameters(recordTypeName, recordsetList, param);

		setKeyFieldParameters(recordTypeName, param, true);

		// Enclosure signs

		this.enclBegin = param.getProperty(recordTypeName + ".enclosureSignBegin", "");

		this.enclEnd = param.getProperty(recordTypeName + ".enclosureSignEnd", this.enclBegin);

		this.enclBeginEsc = param.getProperty(recordTypeName + ".enclosureSignBeginEscape", "");

		this.enclEndEsc = param.getProperty(recordTypeName + ".enclosureSignEndEscape", this.enclBeginEsc);

		this.enclosureConversion = param.getPropertyAsBoolean(recordTypeName + ".enclosureConversion", "Y");

	}



	public String parseKeyFieldValue(String lineInput) {

		String currentLineKeyFieldValue = "";

		String[] inputFieldContents = splitLineBySeparator(lineInput);

		if (this.keyFieldIndex <= inputFieldContents.length) {
		
			if (inputFieldContents[this.keyFieldIndex].equals(this.keyFieldValue)) {
				
				currentLineKeyFieldValue = this.keyFieldValue;

			}

		}
		
		return currentLineKeyFieldValue;

	}

	public String parseKeyFieldValue(String lineInput, int offsetEnd,int offsetStart) {

		String currentLineKeyFieldValue = "";

		String[] inputFieldContents = splitLineBySeparator(lineInput);

		if (this.keyFieldIndex <= inputFieldContents.length) {
		
			if (inputFieldContents[this.keyFieldIndex].equals(this.keyFieldValue)) {
				
				currentLineKeyFieldValue = this.keyFieldValue;

			}

		}
		
		return currentLineKeyFieldValue;

	}

	public DataField[] extractLineContents(String lineInput, boolean trim, boolean trimNum, int lineIndex) throws Exception  {

		ArrayList<DataField> fields = new ArrayList<DataField>();



		String[] inputFieldContents = splitLineBySeparator(lineInput);

		int outputSize = inputFieldContents.length; // Use length of input line for default 'ignore' or anything else

		// Content has less fields than specified in configuration

		if(inputFieldContents.length < this.fieldNames.length) {				

			if(this.missingLastFields.equalsIgnoreCase("add")) {

				outputSize = this.fieldNames.length;

			} else if(this.missingLastFields.equalsIgnoreCase("error")) {

				throw new Exception("Line " + (lineIndex+1) + " has less fields than configured");

			}

			// Content has more fields than specified in configuration	

		} else if (inputFieldContents.length > this.fieldNames.length) {

			outputSize = this.fieldNames.length; // Default to length of configuration fields

			if(this.additionalLastFields.equalsIgnoreCase("error")) {

				throw new Exception("Line " + (lineIndex+1) + " has more fields than configured");

			}

		}

		for (int i = 0; i < outputSize; i++ ) {

			String content = "";

			if(i < inputFieldContents.length) {

				content = (inputFieldContents[i] == null) ? "" : inputFieldContents[i];

			}
			String trimS = fixedLengths[i].replaceAll("[^\\D.]", "");
			
			if(trimS != null & trimS.length()>0){
			trim = Boolean.valueOf(trimS);
			
			}

			fields.add(createNewField(this.fieldNames[i], content, trim,trimNum));

		}

		return fields.toArray(new DataField[fields.size()]);

	}



	private String[] splitLineBySeparator(String input) {

		// Split input with enclosure signs and escapes

		ArrayList<String> contents = new ArrayList<String>();

	/*	StringTokenizer tokenizer = new StringTokenizer(input, this.fieldSeparator, this.enclBegin, this.enclEnd, this.enclBeginEsc, this.enclEndEsc, true);
		
		for(int i = 0; i < tokenizer.countTokens(); i++) {	
 			
			String fieldContent = (String)tokenizer.nextElement();
			*/
			
		String [] tok = input.split(this.fieldSeparator);
		for(int i = 0; i < tok.length; i++) {
 			String fieldContent = tok[i];
			
			
			
			
			// If the token field content is not a separator, then store it in the output array

			if(!fieldContent.equalsIgnoreCase(this.fieldSeparator)) {
				contents.add(fieldContent);
				
			/*	if(this.enclosureConversion) {

					contents.add(tokenizer.convertEncls(fieldContent));

				} else {
					
					contents.add(fieldContent);

				}*/

			}

		}

		return contents.toArray(new String[contents.size()]);

	}

}
