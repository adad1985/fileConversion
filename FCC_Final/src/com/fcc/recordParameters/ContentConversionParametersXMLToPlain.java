package com.fcc.recordParameters;

import com.fcc.util.ConversionPropHelper;
import com.fcc.util.DefaultRecordSeparator;

public abstract class ContentConversionParametersXMLToPlain {
	public final String fieldSeparator;
	public final String[] fixedLengths;
	public String endSeparator;
	// XML to Plain
	public String fixedLengthTooShortHandling;

	public ContentConversionParametersXMLToPlain(String fieldSeparator, String[] fixedLengths) {
		this.fieldSeparator = fieldSeparator;
		this.fixedLengths = fixedLengths;
	}

	public void setAdditionalParameters(String recordTypeName, ConversionPropHelper param, String encoding) throws Exception {
		// End Separator
		String tempEndSeparator = param.getProperty(recordTypeName+".endSeparator", "");
		if (tempEndSeparator == null) {
			tempEndSeparator = DefaultRecordSeparator.newLine;
		} else {
			DefaultRecordSeparator sep = new DefaultRecordSeparator(tempEndSeparator, encoding);
			tempEndSeparator = sep.toString();
		}
		this.endSeparator = tempEndSeparator;
	}
}
