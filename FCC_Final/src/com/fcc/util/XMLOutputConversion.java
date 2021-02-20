package com.fcc.util;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLOutputConversion {
	private final Document doc;
	private Node rootNode;
	private int indentFactor = 0;
    private String namespaceInd = "";
	private boolean escapeInvalidNameStartChar = true;
	private boolean mangleInvalidNameChar = false;
	private Element el;
	private Element root;

	public XMLOutputConversion(String rootName, String namespace) throws ParserConfigurationException {
		DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		
		this.doc = docBuilder.newDocument();
		if (rootName.contains("/")) {
			String[] rootA = rootName.split("/");
			// Doc,Rec,Pec

			if (!namespace.isEmpty()) {
				namespaceInd = namespace;
				root = doc.createElementNS(namespace, "ns:" + rootA[0]);
				this.doc.appendChild(root);

			}
			else{
				
				root = doc.createElement(rootA[0]);
				
			this.doc.appendChild(root);
				
			}
			
			for (int i = 1; i < rootA.length - 1; i++) {
				
				if (!namespace.isEmpty()) {
					el = doc.createElementNS(namespace, "ns:" + rootA[i]);
					root.appendChild(el);
					root = el;
				} else {
					
					el = doc.createElement(rootA[i]);
					this.doc.appendChild(el);
				}

			}
			if (!namespace.isEmpty()) {
				this.rootNode = this.doc.createElementNS(namespace, "ns:" + rootA[rootA.length - 1]);
				if (rootA.length > 2) {
					el.appendChild(this.rootNode);
				} else
					root.appendChild(this.rootNode);
			} else {
				this.rootNode = this.doc.createElement(rootA[rootA.length - 1]);
				if (rootA.length > 2) {
					el.appendChild(this.rootNode);
				} else {
					
					root.appendChild(this.rootNode);

				}
			}

		} else {
			if (!namespace.isEmpty()) {
				this.rootNode = this.doc.createElementNS(namespace, "ns:" + rootName);

			} else {
				this.rootNode = this.doc.createElement(rootName);
			}
			this.doc.appendChild(this.rootNode);
		}

	}

	public XMLOutputConversion(String rootName) throws ParserConfigurationException {
		this(rootName, "");
	}

	public XMLOutputConversion(Document document) {
		this.doc = document;
		this.rootNode = this.doc.getDocumentElement();
	}

	public void setIndentFactor(int indentFactor) {
		this.indentFactor = indentFactor;
	}

	public boolean isEscapeInvalidNameStartChar() {
		return this.escapeInvalidNameStartChar;
	}

	public void setEscapeInvalidNameStartChar(boolean escapeInvalidNameStartChar) {
		this.escapeInvalidNameStartChar = escapeInvalidNameStartChar;
	}

	public boolean isMangleInvalidNameChar() {
		return this.mangleInvalidNameChar;
	}

	public void setMangleInvalidNameChar(boolean mangleInvalidNameChar) {
		this.mangleInvalidNameChar = mangleInvalidNameChar;
	}

	public ByteArrayOutputStream generateDOMOutput(List<DataField> fieldList) throws TransformerException {
		constructDOMContent(this.rootNode, fieldList);
		
		//TRY
		/*Element grp = this.doc.createElement("GS");
		Element par =  this.doc.getElementById("I");
		while (par.getChildNodes().getLength()>0) {
			grp.appendChild((Node) par.getChildNodes());
		}*/
		/*
			Element elP = this.doc.createElement("ns:GS");
			NodeList nodesW = this.doc.getElementsByTagName("ns:T");
			nodesW.item(0).getParentNode().appendChild(elP);
			*/
		//TRY
		
		return convertDOMtoBAOS();
	}

	public void generateDOMOutput(List<DataField> fieldList, OutputStream outStream) throws TransformerException {
		
		constructDOMContent(this.rootNode, fieldList);
		convertDOMtoOutputStream(outStream);
		
	}

	private void convertDOMtoOutputStream(OutputStream outStream) throws TransformerException {
		// Transform the DOM to OutputStream
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		if (this.indentFactor > 0) {
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount",
					Integer.toString(this.indentFactor));
		}
		transformer.transform(new DOMSource(this.doc), new StreamResult(outStream));
	}

	private ByteArrayOutputStream convertDOMtoBAOS() throws TransformerException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		convertDOMtoOutputStream(baos);
		return baos;
	}

	private void constructDOMContent(Node parentNode, List<DataField> fieldList) throws TransformerException {
		/*if(parentNode.getNodeName().contains("T")){
			Element elP = this.doc.createElement("ns:GS");
			parentNode = elP.appendChild(parentNode);
		}*/
		// Go through each entry of the List
		
		
		// TRY GRP
		/*
		// Here check for all segments which are to be grouped under the same group
		if(parentNode.getNodeName().contains("T")){
			// Here give the name of the corresponding group, say ns:GS
			if(((Element) parentNode.getParentNode()).getElementsByTagName("ns:GS").getLength() < 1){
				Element elP = this.doc.createElement("ns:GS");
				parentNode.getParentNode().appendChild(elP);
			elP.appendChild(parentNode);
			}
			
			else  {
			
			NodeList nl = parentNode.getParentNode().getChildNodes();
			for(int h = 0;h<nl.getLength();h++){
				
				Node ne = nl.item(h);
			
				if(ne.getNodeName().equals("ns:GS")){
					ne.appendChild(parentNode);
					
					break;
				}
				
			}
			
			}
			
		}*/
		
		
		// TRY GRP
		
		
		for (int i = 0; i < fieldList.size(); i++) {
			DataField field = fieldList.get(i);
			constructDOMContent(parentNode, field.fieldName, field.fieldContent);
			
		}
		
		
		
	}

	private void constructDOMContent(Node parentNode, String keyName, Object[] contents) throws TransformerException {
		// Go through each item of the array
		for (Object entry : contents) {
			constructDOMContent(parentNode, keyName, entry);
			
		}
		
	}

	@SuppressWarnings("unchecked")
	private void constructDOMContent(Node parentNode, String keyName, Object fieldContent) throws TransformerException {
		
		
		
		
		if (fieldContent instanceof Object[]) {
			constructDOMContent(parentNode, keyName, (Object[]) fieldContent);
			
		} else if (fieldContent instanceof List<?>) {
			
			
			Node node = addElementToNode(parentNode, keyName);
			
		
			
			constructDOMContent(node, (List<DataField>) fieldContent);
		} else if (fieldContent == null) {
			
		} else {
			addElementToNode(parentNode, keyName, fieldContent.toString());
			
		}
		
	}

	private Node addElementToNode(Node parentNode, String elementName) throws TransformerException {
		try {
			Node element = null;
			if (this.escapeInvalidNameStartChar || this.mangleInvalidNameChar) {
				element = this.doc.createElement(generateElementName(elementName));
			} else {
				element = this.doc.createElement(elementName);
			}
			parentNode.appendChild(element);
			return element;
		} catch (DOMException e) {
			throw new TransformerException("Invalid character in XML element name: " + elementName, e);
		}
	}

	private Node addElementToNode(Node parentNode, String elementName, String elementTextValue)
			throws TransformerException {
		Node element = addElementToNode(parentNode, elementName);
		if (elementTextValue != null) {
			element.appendChild(this.doc.createTextNode(elementTextValue));
		}
		return element;
	}

	private String generateElementName(String elementName) {
		
		
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < elementName.length(); i++) {
			char c = elementName.charAt(i);
			// First char of XML element
			if (i == 0 && this.escapeInvalidNameStartChar) {
				if (Character.isDigit(c)) {
					sb.append("__").append(c);
				} else if (!XMLChar.isNameStart(c)) {
					String hex = String.format("%04x", (int) c);
					sb.append("__u" + hex);
				} else {
					sb.append(c);
				}
			} else if (this.mangleInvalidNameChar) {
				if (!XMLChar.isName(c)) {
					String hex = String.format("%04x", (int) c);
					sb.append("__u" + hex);
				} else {
					sb.append(c);
				}
			} else {
				sb.append(c);
			}
		}
		if(namespaceInd != null && namespaceInd.trim().length()>0){
			sb.insert(0,"ns:" );
		}
		return sb.toString();
	}
}
