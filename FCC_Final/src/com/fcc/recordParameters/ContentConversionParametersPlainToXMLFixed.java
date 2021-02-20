package com.fcc.recordParameters;

import java.util.*;

import com.fcc.util.ConversionPropHelper;
import com.fcc.util.DataField;

public class ContentConversionParametersPlainToXMLFixed extends ContentConversionParametersPlainToXML {

	ContentConversionParametersPlainToXMLFixed(String fieldSeparator, String[] fixedLengths) {
		super(fieldSeparator, fixedLengths);
	}

	public void setAdditionalParameters(String recordTypeName, String[] recordsetList, ConversionPropHelper param) throws Exception {
		super.setAdditionalParameters(recordTypeName, recordsetList, param);
		if (this.fieldNames.length != this.fixedLengths.length) {
			throw new Exception("No. of fields in fieldNames and fieldFixedLengths do not match for record type ="+ recordTypeName);
		}
		setKeyFieldParameters(recordTypeName, param, false);
	}

	public String parseKeyFieldValue(String lineInput) {
		String currentLineKeyFieldValue = "" ;
		String valueAtKeyFieldPosition = dynamicSubstring(lineInput, this.keyFieldStartPosition, this.keyFieldLength);
		if (valueAtKeyFieldPosition.trim().equals(this.keyFieldValue)) {
			
			currentLineKeyFieldValue = this.keyFieldValue;
		}
		
		return currentLineKeyFieldValue;
	}
	
	public String parseKeyFieldValue(String lineInput, int offsetEnd,int offsetStart) {
		
		String currentLineKeyFieldValue = "" ;
		String valueAtKeyFieldPosition = dynamicSubstring(lineInput, offsetStart,offsetEnd);
		if (valueAtKeyFieldPosition.trim().equals(this.keyFieldValue)) {
			
			currentLineKeyFieldValue = this.keyFieldValue;
		}
		
		return currentLineKeyFieldValue;
	}

public	DataField[] extractLineContents(String lineInput, boolean trim,boolean trimNum, int lineIndex) throws Exception {
		ArrayList<DataField> fields = new ArrayList<DataField>();
		int start = 0;
		for (int i = 0; i < this.fieldNames.length; i++) {
			// Use below logic to trim based on XSD type/User Choice
			String trimS = fixedLengths[i].replaceAll("[^\\D.]", "");
			if(trimS != null & trimS.length()>0){
				trim = Boolean.valueOf(trimS);
				
				}
			int length = Integer.parseInt(this.fixedLengths[i].replaceAll("[^\\d.]", ""));	
			String content = dynamicSubstring(lineInput, start, length);

			if (lineInput.length() < start) {
				if(this.missingLastFields.equalsIgnoreCase("error")) {
					throw new Exception("Line"+ (lineIndex+1) +"has less fields than configured");
				} else if(this.missingLastFields.equalsIgnoreCase("add")) {
					fields.add(createNewField(this.fieldNames[i], content, trim,trimNum));
				}
			} else {
				fields.add(createNewField(this.fieldNames[i], content, trim,trimNum));
				
			}
			// Set start location for next field
			start += length;

			// After the last configured field, check if there are any more
			// content in the input
			if(i == this.fieldNames.length - 1 && lineInput.length() > start && 

					this.additionalLastFields.equalsIgnoreCase("error")) {

				throw new Exception("Line " + (lineIndex+1) + " has more fields than configured");

			}

		}
		return fields.toArray(new DataField[fields.size()]);
	}

	private String dynamicSubstring(String input, int start, int length) {

		int startPos = start;

		int endPos = start + length - 1;

		String output = "";

	

		if ( startPos < 0 ) {

			// (1) Start position is before start of input, return empty string

		} else if ( startPos >= 0 && startPos < input.length() ) {

			if ( endPos < input.length() ) {

				// (2) Start & end positions are before end of input, return the partial substring

				output = input.substring( startPos, endPos + 1 );

			} else if ( endPos >= input.length() ) {

				// (3) Start position is before start of input but end position is after end of input, return from start till end of input

				output = input.substring( startPos, input.length() );

			}

		} else if ( startPos >= input.length() ) {

			// (4) Start position is after end of input, return empty string

		}

		return output;

	}

}