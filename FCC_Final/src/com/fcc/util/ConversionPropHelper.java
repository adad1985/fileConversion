package com.fcc.util;

import java.util.Map;
import java.util.Set;

public class ConversionPropHelper {
	final Map<String,Object> properties;

	ConversionPropHelper(Map<String,Object> properties) {
		this.properties = properties;
	}

public	String getProperty(String propertyName, String defaultValue) throws Exception {
		String propertyValue = (String) this.properties.get(propertyName);
		if(propertyValue == null) {
			if(defaultValue != null)
				propertyValue = defaultValue;
			else
				throw new Exception("Mandatory parameter " +propertyName +"is missing");
		}
		return propertyValue;
	}

	public String getProperty(String propertyName) throws Exception {
		return (getProperty(propertyName, ""));
	}

	public int getPropertyAsInt(String propertyName, String defaultValue) throws NumberFormatException, Exception {
		return Integer.parseInt((getProperty(propertyName, defaultValue))) ;
	}

	public int getPropertyAsInt(String propertyName) throws NumberFormatException, Exception {
		return getPropertyAsInt(propertyName, null);
	}

	public	boolean getPropertyAsBoolean(String propertyName, String defaultValue) throws Exception {
		String str = getProperty(propertyName, defaultValue);
		//return getPropertyAsBoolean(str);
		if(str.equalsIgnoreCase("true")) {

			return true;

		} else {

			return false;

		}
	}

	public	boolean getPropertyAsBoolean(String propertyName) throws Exception {
	return	getPropertyAsBoolean(propertyName, null);
	}

	public void checkValidValues(String propertyName, Object propertyValue, Set<Object> validValues) throws Exception {
		String [] strValid = validValues.toString().split(",");
		boolean found = false;
		for(int i = 0;i<strValid.length;i++){
			if(strValid[i].equals(propertyValue)){
				
				found = true;
				break;
			}
		}
		if(!found){
			throw new Exception("Value" +propertyValue+ "not valid for parameter "+propertyName);
		}
		
	}
}