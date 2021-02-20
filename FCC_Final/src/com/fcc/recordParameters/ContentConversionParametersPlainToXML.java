package com.fcc.recordParameters;


import java.util.*;

import com.fcc.util.ConversionPropHelper;
import com.fcc.util.DataField;
public abstract class ContentConversionParametersPlainToXML {
	public final String fieldSeparator;
	public final String[] fixedLengths;
	public String endSeparator;
	// Plain to XML
	private String keyFieldName;
	protected String keyFieldValue;
	protected String[] fieldNames;
	protected int keyFieldIndex;
	protected int keyFieldStartPosition = 0;
	protected int keyFieldLength = 0;

	protected String missingLastFields;
	protected String additionalLastFields;
	public String parentRecordType;

	ContentConversionParametersPlainToXML(String fieldSeparator, String[] fixedLengths) {
		this.fieldSeparator = fieldSeparator;
		this.fixedLengths = fixedLengths;
	}

public	void setAdditionalParameters(String recordTypeName, String[] recordsetList, ConversionPropHelper param) throws Exception {
		// Parent record type
		this.parentRecordType = param.getProperty(recordTypeName+".parent");
		
		if(this.parentRecordType.equals(recordTypeName)) {
			throw new Exception("Value in parent cannot be the same as substructure name");
		} else if (!this.parentRecordType.equals("Root"))  {
			for(int i = 0; i < recordsetList.length; i++) {
				if(this.parentRecordType.equals(recordsetList[i])) {
					break;
				}
			}
			
		}

		// Field names
		String fieldNamesColumn = recordTypeName + ".fieldNames";
		String tempFieldNames = param.getProperty(fieldNamesColumn);
		this.fieldNames = tempFieldNames.split(",");
		// Validate the field names
		validateFieldNames(recordTypeName, this.fieldNames);
		// Structure deviations
		this.missingLastFields = param.getProperty(recordTypeName + ".missingLastFields", "ignore");
		this.additionalLastFields = param.getProperty(recordTypeName + ".additionalLastFields", "ignore");
	}

 public	abstract String parseKeyFieldValue(String lineInput);

	public abstract String parseKeyFieldValue(String inputLine, int offsetEnd, int offsetStart);

	public abstract DataField[] extractLineContents(String lineInput, boolean trim,boolean trimN, int lineIndex) throws Exception;
	

	protected  DataField createNewField(String fieldName, String fieldValue, boolean trim, boolean trimNum) {
		
		if (trim) {
			fieldValue = fieldValue.trim();
			
			// Use below logic to remove leading zeroes from a Number; it also checks if its an Integer or not
			/*if(fieldValue.matches("-?\\d+")){
			fieldValue=	Integer.valueOf(fieldValue).toString();
			// Decimal Handling
			fieldValue = fieldValue.concat(".03");
			}*/
		}
		if (trimNum) {
			if(fieldValue.matches("-?\\d+")){
				fieldValue=	Integer.valueOf(fieldValue).toString();
			}	
		}
		return new DataField(fieldName, fieldValue);
	}

	protected  void setKeyFieldParameters(String recordTypeName, ConversionPropHelper param, boolean csvMode) throws Exception {
		
		String genericRecordType = param.getProperty("genericRecordType","");
		if (genericRecordType == null || !genericRecordType.equals(recordTypeName)) {
			
			// Key field name and value
			this.keyFieldName = param.getProperty(recordTypeName +".keyFieldName");
			this.keyFieldValue = param.getProperty(recordTypeName + ".keyFieldValue");
		
			// Index and position of key field in record type
			boolean found = false;
		
			for (int i = 0; i < this.fieldNames.length; i++) {
				if (this.fieldNames[i].equals( keyFieldName)) {
					this.keyFieldIndex = i;
				
					found = true;
					if (!csvMode) {
						if(this.fixedLengths[i].equals(""))
						{
							this.keyFieldLength = 0;
						}
						else{
							this.keyFieldLength = Integer.parseInt(this.fixedLengths[i]);
							
							
						}
					//	this.keyFieldLength = Integer.parseInt(this.fixedLengths[i]);
						
					}
					break;
				}
				if (!csvMode) {
					if(this.fixedLengths[i].equals(""))
					{
						this.keyFieldStartPosition = 0;
						
					}
					else{
					
					this.keyFieldStartPosition += Integer.parseInt(this.fixedLengths[i]);
					
					}
				}
			}
			
		}
	}

	private void validateFieldNames(String recordTypeName, String[] fieldNames) throws Exception {
		// No duplicates in field names
		HashSet<String> set = new HashSet<String>();

		for(int i = 0; i < fieldNames.length; i++) {

			if(set.contains(fieldNames[i])) {

				throw new Exception("Duplicate fieldName found in '" + recordTypeName + ".fieldNames': " + fieldNames[i]);

			} else {

				set.add(fieldNames[i]);
		}

	}
}

}