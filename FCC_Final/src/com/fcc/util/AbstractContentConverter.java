package com.fcc.util;

import java.util.Map;

public abstract class AbstractContentConverter {
	protected final Object body;
	protected final Map<String,Object> properties;

	protected final ConversionPropHelper ph;


	public AbstractContentConverter(Object body, Map<String, Object> properties) {
		this.body = body;
		this.properties = properties;		
		this.ph = new ConversionPropHelper(properties);
		
	}

	public abstract void getParameters();

	public abstract void parseInput();

	public abstract Object generateOutput();
}