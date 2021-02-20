package com.fcc.util;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class PlainOutputConversion {

	public String generateLineText(List<DataField> childFields, String fieldSeparator, String[] fixedLengths,
			String endSeparator, String fixedLengthTooShortHandling) throws Exception {
		
	//	if (fixedLengths == null || fixedLengths.length == 0) {
		if (fieldSeparator != null && fieldSeparator.length() > 0) {
			return generateDelimitedLine(childFields, fieldSeparator, endSeparator);
		} else {
			return generateFixedLengthLine(childFields, fixedLengths, endSeparator, fixedLengthTooShortHandling);
		}
	}

	private String generateDelimitedLine(List<DataField> childFields, String fieldSeparator, String endSeparator)
			throws IOException {
		StringBuilder sb = new StringBuilder();
		int leafFieldCount = 0;
		// Process all child elements that are fields
		for (DataField childField : childFields) {
			Object fieldContent = childField.fieldContent;
			if (fieldContent instanceof String) {
				if (leafFieldCount == 0) {
					sb.append(fieldContent);
				} else {
					sb.append(fieldSeparator).append(fieldContent);
				}
				leafFieldCount++;
			}
		}
		if (leafFieldCount > 0) {
			sb.append(endSeparator);
		}
		return sb.toString();
	}

	private String generateFixedLengthLine(List<DataField> childFields, String[] fixedLengths, String endSeparator,
			String fixedLengthTooShortHandling) throws Exception {
		StringBuilder sb = new StringBuilder();
		int leafFieldCount = 0;
		// Process all child elements that are fields
		for (DataField childField : childFields) {
			Object fieldContent = childField.fieldContent;
			int fieldLength = 0 ;
			if (fieldContent instanceof String) {
				
				if(fixedLengths[leafFieldCount].contains("N")){
					// For eg 10N
					
				 fieldLength = Integer.parseInt(fixedLengths[leafFieldCount].split("N")[0]);
				}
				
				else {
					 fieldLength = Integer.parseInt(fixedLengths[leafFieldCount].split("S")[1]);
					
				}
				//leafFieldCount++;
				String fieldValue = (String) fieldContent;
				
				// Handle case if field value is longer than configured length
				if (fieldValue.length() > fieldLength) {
					
					if (fixedLengthTooShortHandling.equalsIgnoreCase("cut")) {
						fieldValue = fieldValue.substring(0, fieldLength);
					} else if (fixedLengthTooShortHandling.equalsIgnoreCase("ignore")) {
						// Do nothing
					} else {
						// Default is error
						throw new Exception("Field value '" + fieldValue + "' longer than allowed length "
								+ fieldLength + " for field '" + childField.fieldName + "'");
					}
				}
			
				if(fixedLengths[leafFieldCount].contains("N")){
					sb.append(padNumeric(fieldValue, fixedLengths[leafFieldCount].split("N")[0]));
				}
				
				else{
					sb.append(padRight(fieldValue, fixedLengths[leafFieldCount].split("S")[0]));
				//sb.append(padRight(fieldValue, fieldLength));
				}
				leafFieldCount++;
			}
		}
		if (leafFieldCount > 0) {
			sb.append(endSeparator);
		}
		return sb.toString();
	}

	

	private Object padNumeric(String fieldValue, String fixedLengths) {
		String len = fixedLengths.split("N")[0];
		String pad = "";
		if(Integer.parseInt(len)>fieldValue.length())
		{
			for(int i = 0; i<Integer.parseInt(len)-fieldValue.length();i++){
				pad = "0"+pad;
			}
			
		}
		return (pad+fieldValue);
		//return String.format("%0"+len+"d",fieldValue);
	}

	private String padRight(String input, String width) {
		int wid = Integer.valueOf(width);
		return String.format("%1$-" + wid + "s", input);
	}
}
