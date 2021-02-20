package com.fcc.conversion;

import com.fcc.recordParameters.ContentConversionParameters;
import com.fcc.recordParameters.ContentConversionParametersXMLToPlain;
import com.fcc.util.AbstractContentConverter;
import com.fcc.util.DataField;
import com.fcc.util.PlainOutputConversion;
import com.fcc.util.XMLElementContainer;
import com.fcc.util.XMLInputConversion;

import java.util.*;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import java.io.*;

public class XMLToPlainConversion extends AbstractContentConverter {
	XMLInputConversion domIn;
	PlainOutputConversion plainOut;
	XMLElementContainer rootXML;
	String encoding;
	String recordsetStructure;
	final Map<String, ContentConversionParametersXMLToPlain> recordTypes;

	XMLToPlainConversion(Object body, Map<String,Object> properties) {
		super(body, properties);
		this.recordTypes = new HashMap<String, ContentConversionParametersXMLToPlain>();
	}

	@Override
public	void getParameters(){
		try {
			this.encoding = this.ph.getProperty("encoding", "UTF-8");
			this.recordsetStructure = this.ph.getProperty("recordsetStructure");
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		

		String[] recordsetList = this.recordsetStructure.split(",");
		for (String recordTypeName : recordsetList) {
			if (!this.recordTypes.containsKey(recordTypeName)) {
				ContentConversionParametersXMLToPlain rtp = null;
				try {
					rtp = (ContentConversionParametersXMLToPlain) ContentConversionParameters
							.newInstance()
							.newParameter(recordTypeName, recordsetList, this.encoding, this.ph, "xml2plain");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					rtp.setAdditionalParameters(recordTypeName, this.ph, this.encoding);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				this.recordTypes.put(recordTypeName, rtp);
			}
		}
	}

	@Override
public	void parseInput() {
		// Parse input XML contents
      InputStream is = new ByteArrayInputStream(this.body.toString().getBytes());
		
		
		try {
			this.domIn = new XMLInputConversion(is);
		} catch (ParserConfigurationException | SAXException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.rootXML = this.domIn.extractDOMContent();
	}

	@Override
	public byte[]  generateOutput() {
		// Create output converter and generate output flat content
		this.plainOut = new PlainOutputConversion();

		String output = "";
		try {
			output = constructTextfromXML(this.rootXML, true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return output.getBytes();
	}

	private String constructTextfromXML(XMLElementContainer element, boolean isRoot)  {
		StringBuilder sb = new StringBuilder();
	//	System.out.println(element.getElementName());
		// First, construct output for current element's child fields
		if (!isRoot && this.recordsetStructure.contains(element.getElementName())) {
			try {
				
				sb.append(generateRowTextForElement(element));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// Then recursively process child elements that are segments
	
		for (DataField childField : element.getChildFields()) {
		
			Object fieldContent = childField.fieldContent;
			if (fieldContent instanceof XMLElementContainer) {
				sb.append(constructTextfromXML((XMLElementContainer) fieldContent, false));
			}
		}
		
		return sb.toString();
	}

	private String generateRowTextForElement(XMLElementContainer element) throws Exception  {

		ArrayList<DataField> childFields = (ArrayList<DataField>) element.getChildFields();
		String segmentName = element.getElementName();
		// Below code adds the missing fields from XSD, in case a field is missing in XML
		String [] fldCh = this.ph.getProperty(segmentName+".fieldNames").split(",");
		ArrayList<String> fieldLst = new ArrayList<String>();
		for(int h = 0;h<childFields.size();h++){
			fieldLst.add(childFields.get(h).fieldName);
		}
		
	    for(int i = 0;i<fldCh.length;i++){
	    	String fieldName = fldCh[i];
	    	if(!fieldLst.contains(fieldName))
	    		
	    	{	DataField rep = new DataField(fieldName, "");
	    	childFields.add(i, rep);
	   
	    	}    	
	    	
	    }
	    
	 // Above code adds the missing fields from XSD, in case a field is missing in XML
		ContentConversionParametersXMLToPlain rtp = this.recordTypes.get(segmentName);
		
		return  this.plainOut.generateLineText(childFields, rtp.fieldSeparator, rtp.fixedLengths, rtp.endSeparator,
				rtp.fixedLengthTooShortHandling);
	}
}
