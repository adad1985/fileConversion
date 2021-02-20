package com.fcc.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlainInputConversion {
	private final List<String> lineContents;

	public PlainInputConversion(InputStream inStream) throws IOException {
		List<String> contents = new ArrayList<String>();
		// TODO - If there is a default endSeparator, then we cannot use
		// LineNumberReader
		LineNumberReader lnr = new LineNumberReader(new InputStreamReader(inStream));
		String lineContent;
		while ((lineContent = lnr.readLine()) != null) {
			contents.add(lineContent);
		}
		lnr.close();
		this.lineContents = contents;
	}

	public PlainInputConversion(String input) {
		String[] lines = input.split("\r\n|\r|\n");
		this.lineContents = Arrays.asList(lines);
	}

	public List<String> getLineContents() {
		return this.lineContents;
	}
}
