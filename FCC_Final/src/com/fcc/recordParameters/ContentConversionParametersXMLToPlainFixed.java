package com.fcc.recordParameters;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.fcc.util.ConversionPropHelper;

public class ContentConversionParametersXMLToPlainFixed extends ContentConversionParametersXMLToPlain {

	ContentConversionParametersXMLToPlainFixed(String fieldSeparator, String[] fixedLengths) {
		super(fieldSeparator, fixedLengths);
	}

	public void setAdditionalParameters(String recordTypeName, ConversionPropHelper param, String encoding) throws Exception {
		super.setAdditionalParameters(recordTypeName, param, encoding);
		// Fixed Length too short handling
		this.fixedLengthTooShortHandling = param.getProperty(recordTypeName + ".fixedLengthTooShortHandling", "error");
	//	Set<Object> st = new HashSet<>(Arrays.asList("Error,Cut,Ignore"));
		Object obj = new HashSet<>(Arrays.asList("Error,Cut,Ignore"));
		 Set<Object> st = (Set<Object>) obj;
		param.checkValidValues(recordTypeName, this.fixedLengthTooShortHandling, st);
			
	}
}
