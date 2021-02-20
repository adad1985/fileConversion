package com.fcc.recordParameters;


import com.fcc.util.ConversionPropHelper;
import com.fcc.util.DefaultRecordSeparator;

public class ContentConversionParameters {

	// Private constructor
	private ContentConversionParameters() {
	}

	public static ContentConversionParameters newInstance() {
		return new ContentConversionParameters();
	}

	public Object newParameter(String recordTypeName, String[] recordsetList, String encoding, ConversionPropHelper param, String convType) throws Exception {
		// Set parameter values for the record type
		// 1 - Field Separator
		String defaultFieldSeparator = param.getProperty("defaultFieldSeparator");

		String fieldSeparatorName = recordTypeName + ".fieldSeparator";

		String fieldSeparator; 

		if (defaultFieldSeparator != null) {

			fieldSeparator = param.getProperty(fieldSeparatorName, defaultFieldSeparator);

		} else {

			fieldSeparator = param.getProperty(fieldSeparatorName);

		}

		if (fieldSeparator != null) {

			DefaultRecordSeparator sep = new DefaultRecordSeparator(fieldSeparator, encoding);

			fieldSeparator = sep.toString();

		}
		// 2 - Fixed Lengths
		String fieldFixedLengthsName = recordTypeName + ".fieldFixedLengths";
		String tempFixedLengths = param.getProperty(fieldFixedLengthsName, "");
		String[] fixedLengths;
		if (tempFixedLengths == null) {
			fixedLengths = null;
		} else {
			fixedLengths = tempFixedLengths.split(",");
			
		}

		// Validate the parameter values
		if (fieldSeparator == null && fixedLengths == null) {
			throw new Exception("Either fixed lengths or field separators or default field separator must be populated");
		} else if (fieldSeparator.length()>0 && fixedLengths.length>0) {
		
		//	throw new Exception("Either fixed lengths or field separators should be provided, not both");
		}

		
		
		if(convType.equals("xml2plain")) {

			if (fieldSeparator != null && fieldSeparator.length()>0 ) {

				return new ContentConversionParametersXMLToPlainDelimited(fieldSeparator, null);

			} else {
			
				return new ContentConversionParametersXMLToPlainFixed(fieldSeparator, fixedLengths);

			}

		} else if(convType.equals("plain2xml")) {

			if (fieldSeparator != null  && fieldSeparator.length()>0) {

				return new ContentConversionParametersPlainToXMLDelimited(fieldSeparator, fixedLengths);

			} else {
				
				return new ContentConversionParametersPlainToXMLFixed(fieldSeparator, fixedLengths);

			}
		}
		
		else{
				throw new Exception("Conversion type not supported; Supported Conversion types:plain2xml OR  xml2plain");
		}
		
	}
}
