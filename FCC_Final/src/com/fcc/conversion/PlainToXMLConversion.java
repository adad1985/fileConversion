package com.fcc.conversion;

import com.fcc.recordParameters.ContentConversionParameters;
import com.fcc.recordParameters.ContentConversionParametersPlainToXML;
import com.fcc.recordParameters.ContentConversionParametersPlainToXMLFixed;
import com.fcc.util.AbstractContentConverter;
import com.fcc.util.DataField;
import com.fcc.util.PlainInputConversion;
import com.fcc.util.XMLOutputConversion;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

public class PlainToXMLConversion extends AbstractContentConverter {
	PlainInputConversion plainIn;
	XMLOutputConversion domOut;
	String documentName;
	String documentNamespace;
	int indentFactor;
	String recordsetStructure;
	final Map<String, ContentConversionParametersPlainToXML> recordTypes;
	String encoding;
	List<DataField> nestedContents;
	int rowOffset;
	boolean trimContents;
	boolean trimNum;
	String genericRecordType;
	String offsetEnd;
	String offsetStart;
	public PlainToXMLConversion(Object body, Map<String,Object> properties) {
		super(body, properties);
		this.recordTypes = new HashMap<String, ContentConversionParametersPlainToXML>();
	}

	@Override
		public 	void getParameters() {
		try {
			
			this.offsetEnd = this.ph.getProperty("offsetEnd","3");
			this.offsetStart = this.ph.getProperty("offsetStart","0");
			this.encoding = this.ph.getProperty("encoding", "UTF-8");
			this.documentName = this.ph.getProperty("documentName");
			this.documentNamespace = this.ph.getProperty("documentNamespace");
			this.indentFactor = this.ph.getPropertyAsInt("indentFactor", "0");
			this.recordsetStructure = this.ph.getProperty("recordsetStructure");
			this.rowOffset = this.ph.getPropertyAsInt("rowOffset", "0");
			this.trimContents = this.ph.getPropertyAsBoolean("trimContents");
			this.trimNum = this.ph.getPropertyAsBoolean("trimNum");
			this.genericRecordType = this.ph.getProperty("genericRecordType", "");
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		// Get the parameters for each substructure type
		String[] recordsetList = this.recordsetStructure.split(",");
		for (String recordTypeName : recordsetList) {
			
			if (recordTypeName.equals("Root")) {
				try {
					throw new Exception("Root is a reserved name and not allowed in parameter recordsetStructure");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (!this.recordTypes.containsKey(recordTypeName)) {
				
				ContentConversionParametersPlainToXML rtp = null;
				try {
					rtp = (ContentConversionParametersPlainToXML) ContentConversionParameters
							.newInstance()
							.newParameter(recordTypeName, recordsetList, this.encoding, this.ph, "plain2xml");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					rtp.setAdditionalParameters(recordTypeName, recordsetList, this.ph);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				this.recordTypes.put(recordTypeName, rtp);
			}
		}
	}

	@Override
	public void parseInput() {
		// Parse input plain text contents
		InputStream is = new ByteArrayInputStream(this.body.toString().getBytes());
		
		try {
			this.plainIn = new PlainInputConversion(is);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			this.nestedContents = generateNestedContents();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public byte[] generateOutput() {
		// Create output converter and generate output DOM
		try {
			
			this.domOut = new XMLOutputConversion(this.documentName, this.documentNamespace);
		} catch (ParserConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// Generate OutputStream from DOM
		if (this.indentFactor > 0) {
			this.domOut.setIndentFactor(this.indentFactor);
		}
		ByteArrayOutputStream baos = null;
		try {
		
			baos = this.domOut.generateDOMOutput(this.nestedContents);
			

		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return baos.toByteArray();
			}

	private ArrayList<DataField> generateNestedContents() throws Exception  {
		ArrayList<DataField> nestedContents = new ArrayList<DataField>();	
		
		// Stack is used to track the depth of the traversal of the hierarchy
		ArrayList<DataField> depthStack = new ArrayList<DataField>(this.recordTypes.size());
		depthStack.add(new DataField("Root:0", nestedContents));

		
		// Get the raw line contents and process them line by line
		ArrayList<String> rawLineContents = (ArrayList<String>) this.plainIn.getLineContents();			
		for (int i = this.rowOffset; i < rawLineContents.size(); i++) {			
			String currentLine = rawLineContents.get(i);
			
			// Determine record type for line
			String lineRecordType = determineRecordType(currentLine, i);
			
			// Extract the content of line into node containing field-value pairs
		//	System.out.println(lineRecordType+"A"+currentLine);
			ArrayList<DataField> lineNode = extractLineToFieldList(lineRecordType, currentLine, i);
			// Get the parent node for current line from stack
			
	
		
			
			ArrayList<DataField> parentNode = getParentNode(depthStack, lineRecordType, i+1, lineNode);
			// Add the line node contents to the parent node
			
			parentNode.add(new DataField(lineRecordType, lineNode));
			
		}
	
		return nestedContents;
	}
	/*public ArrayList<Field> generateNestedContents()  {

		ArrayList<Field> nestedContents = new ArrayList<Field>();		

		// Stack is used to track the depth of the traversal of the hierarchy

		ArrayList<Field> depthStack = new ArrayList<Field>(this.recordTypes.size());

		depthStack.add(new Field("Root:0", nestedContents));
		// Get the raw line contents and process them line by line

		ArrayList<String> rawLineContents = (ArrayList<String>) this.plainIn.getLineContents();
		try{

		for (int i = this.rowOffset; i < rawLineContents.size(); i++) {			

			String currentLine = rawLineContents.get(i);
           //System.out.println(currentLine+ " BB");
			// Determine record type for line

			String lineRecordType = determineRecordType(currentLine, i);
            
			// Extract the content of line into node containing field-value pairs

			ArrayList<Field> lineNode = extractLineToFieldList(lineRecordType, currentLine, i);
			
		System.out.println(lineNode.size() + " " +lineRecordType + " "+depthStack.size() + "Check ParentInp");
			// Get the parent node for current line from stack

			ArrayList<Field> parentNode = getParentNode(depthStack, lineRecordType, i+1, lineNode);

			// Add the line node contents to the parent node

			parentNode.add(new Field(lineRecordType, lineNode));

		}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		System.out.println(nestedContents.size()+ " Nested Size");

		return nestedContents;

	}*/
	public String determineRecordType(String inputLine, int lineIndex) throws Exception{
		// Loop through all record sets and parse to figure out key value
		String keyN = "";
		String keyValue = "";
		
		for (String keyName : this.recordTypes.keySet()) {
			
			ContentConversionParametersPlainToXML recordType = (ContentConversionParametersPlainToXML) this.recordTypes.get(keyName);
		/*	
			if(this.offsetEnd.trim().equals(""))
			{
			 
			 keyValue = recordType.parseKeyFieldValue(inputLine);
			 if (keyValue.length()>0) {
			 keyN = keyName;
			 }
			}		
			else{*/
			
				
				keyValue = recordType.parseKeyFieldValue(inputLine,Integer.parseInt(this.offsetEnd),Integer.parseInt(this.offsetStart));
				if (keyValue.length()>0) {
				keyN = keyName;
				}
		
		//	}
			
			
			
			
		}
	
		
		
		return keyN;
	}



	private ArrayList<DataField> extractLineToFieldList(String lineRecordType, String lineInput, int lineIndex) {
		ArrayList<DataField> fieldList = new ArrayList<DataField>();
		// Extract the fields of the current line based on the line's record
		// type
		DataField[] currentLineFields = null;
		
		try {
		//	System.out.println(this.recordTypes.get(lineRecordType).parentRecordType);
			
			currentLineFields = this.recordTypes.get(lineRecordType).extractLineContents(lineInput,
					this.trimContents, this.trimNum,lineIndex);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			
			e.printStackTrace();
		}
		for (DataField field : currentLineFields) {
			if(field.fieldContent.toString().length()>0){
			fieldList.add(new DataField(field.fieldName, field.fieldContent));
			}
			else{
				
			}
		}
		return fieldList;
	}

	@SuppressWarnings("unchecked")
	private ArrayList<DataField> getParentNode(ArrayList<DataField> stack, String lineRecordType, int lineNo, ArrayList<DataField> lineNode) throws Exception {

		boolean found = false;
		
		ArrayList<DataField> parentNode = null;

	

		while(!found && stack.size()!= 0) {

			DataField currentStackLevel = stack.get(stack.size()-1); // Always get the last item

			String parentRecordType = this.recordTypes.get(lineRecordType).parentRecordType;
			
			String[] stackKey = currentStackLevel.fieldName.split(":");
		
			// If the stack key matches the line's parent type, then get the parent node from the stack

			if (parentRecordType.equals(stackKey[0])) {
			
					parentNode =  (ArrayList<DataField>) currentStackLevel.fieldContent;
					
				
					
			//	parentNode =  (ArrayList<Field>) currentStackLevel.fieldContent;
				
				// Add the current line to the bottom of the stack
				
				/*if (parentRecordType.equals(stackKey[0])||parentRecordType.equals("GS")) {
					if(parentRecordType.equals("GS")){
						Field fG = new Field("GS","");
						ArrayList<Field> arrGrp = new ArrayList<Field>();
						arrGrp.add(fG);
						parentNode =arrGrp;
						parentNode =  (ArrayList<Field>) currentStackLevel.fieldContent;
						System.out.println(parentNode.get(1).fieldContent);
					}else{
					parentNode =  (ArrayList<Field>) currentStackLevel.fieldContent;
					}*/
			
				stack.add(new DataField(lineRecordType+":" + lineNo, lineNode));
				
				found = true;

				

			} 
			
			
			
			else {

				stack.remove(stack.size()-1);

			}

		}

	if (parentNode == null) {

			throw new Exception("Cannot find parent for line " + lineNo + ": Record Type = " + lineRecordType );

		}

		return parentNode;
		

	}

}