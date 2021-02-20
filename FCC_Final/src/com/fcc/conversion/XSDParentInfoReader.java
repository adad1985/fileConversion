package com.fcc.conversion;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.*;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.xerces.impl.xs.XMLSchemaLoader;
import org.apache.xerces.impl.xs.XSImplementationImpl;
import org.apache.xerces.xs.XSImplementation;
import org.apache.xerces.xs.XSLoader;
import jlibs.xml.xsd.XSParser;
import org.apache.xerces.xs.XSModel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSInput;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import jlibs.xml.sax.XMLDocument;
import jlibs.xml.xsd.XSInstance;

public class XSDParentInfoReader {

	public static String loadXsdDocument(String fileIn,String Root, String Namespace) throws ClassNotFoundException, InstantiationException, IllegalAccessException, ClassCastException, FileNotFoundException {
		
		
		DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
		DOMImplementationLS impl = (DOMImplementationLS) registry.getDOMImplementation("LS");
		InputStream XSDfileInpPath = new ByteArrayInputStream(fileIn.getBytes());
	//	InputStream XSDfileInpPath = new FileInputStream(fileIn);
		
		LSInput XSDSchemaLS = impl.createLSInput();
		XSDSchemaLS.setByteStream(XSDfileInpPath);
		
		StringWriter writer = new StringWriter();
		
		// Parse the file into an XSModel object
	//	XSModel xsModel = (XSModel) new XSParser().parse(filename);
		XSImplementation implX = new XSImplementationImpl();
	    XSLoader xsLoader = implX.createXSLoader(null);
		
			XSModel xsModel =    xsLoader.load(XSDSchemaLS);
		
	
		
		// Define defaults for the XML generation
		XSInstance instance = new XSInstance();
		instance.minimumElementsGenerated = 1;
		instance.maximumElementsGenerated = 1;
		instance.generateDefaultAttributes = true;
		instance.generateOptionalAttributes = true;
		instance.maximumRecursionDepth = 0;
		instance.generateAllChoices = true;
		instance.showContentModel = true;
		instance.generateOptionalElements = true;
		
        
		QName rootElement = new QName(Namespace,Root);
		QName rootElement2 = new QName(Root);
		XMLDocument sampleXml;
		try {

			final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);

			factory.setValidating(false);
			factory.setIgnoringElementContentWhitespace(true);
			factory.setIgnoringComments(true);

			sampleXml = new XMLDocument(new StreamResult(writer), true, 4, null);
			instance.generate(xsModel, rootElement, sampleXml);

		} catch (TransformerConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return writer.getBuffer().toString();

	}

	private static Document convertStringToXMLDocument(String xmlString) {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder = null;
		try {
			builder = factory.newDocumentBuilder();
			Document doc = builder.parse(new InputSource(new StringReader(xmlString)));
			removeRecursively(doc, Node.COMMENT_NODE, null);
			return doc;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void removeRecursively(Node node, short nodeType, String name) {
		if (node.getNodeType() == nodeType && (name == null || node.getNodeName().equals(name))) {
			node.getParentNode().removeChild(node);
		}

		else {
			NodeList list = node.getChildNodes();
			for (int i = 0; i < list.getLength(); i++) {
				removeRecursively(list.item(i), nodeType, name);
			}
		}
	}

	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, ClassCastException, TransformerException {
		String filename2 = "C:/Users/D072889/Desktop/ICA/XSD_XML/chk3.xsd";
		File file = new File(filename2);
		String strSchema =  "<?xml version=" + "\"1.0\"" + " encoding=" + "\"UTF-8\"" + "?>" + "\n" + "<xs:schema xmlns:xs="
    			+ "\"http://www.w3.org/2001/XMLSchema\"" + 
    			
    			" attributeFormDefault=" + "\"unqualified\""
    			+ " elementFormDefault=" + "\"qualified\"" + ">" + "\n" + "<xs:element name=" + "\"Doc\"" + ">" + "\n"
    			+ "<xs:complexType>" + "\n" + " <xs:sequence>" + "\n" +

    			"<xs:element name=" + "\"Rec\"" + ">" + "\n" + "<xs:complexType>" + "\n" + "<xs:sequence>" + "\n" +

    			"<xs:element name=" + "\"H\"" + ">" + "\n" + "<xs:complexType>" + "\n" + "<xs:sequence>" + "\n"
    			+ "<xs:element name=" + "\"Key\"" + ">" + "\n" + "<xs:simpleType>" + "\n" + "<xs:restriction base="
    			+ "\"xs:string\"" + ">" + "\n" + "<xs:length value=" + "\"1\"" + "/>" + "\n" + "</xs:restriction>"
    			+ "\n" + "</xs:simpleType>" + "\n" + "</xs:element>" + "\n" + "<xs:element name=" + "\"F1\"" + ">"
    			+ "\n" + "<xs:simpleType>" + "\n" + "<xs:restriction base=" + "\"xs:string\"" + ">" + "\n"
    			+ "<xs:length value=" + "\"1\"" + "/>" + "\n" + "</xs:restriction>" + "\n" + "</xs:simpleType>" + "\n"
    			+ "</xs:element>" + "\n" + "<xs:element name=" + "\"I\"" + ">" + "\n" + "<xs:complexType>" + "\n"
    			+ "<xs:sequence>" + "\n" + "<xs:element name=" + "\"Key\"" + ">" + "\n" + "<xs:simpleType>" + "\n"
    			+ "<xs:restriction base=" + "\"xs:string\"" + ">" + "\n" + "<xs:length value=" + "\"1\"" + "/>" + "\n"
    			+ "</xs:restriction>" + "\n" + "</xs:simpleType>" + "\n" + "</xs:element>" + "\n" + "<xs:element name="
    			+ "\"F2\"" + ">" + "\n" + "<xs:simpleType>" + "\n" + "<xs:restriction base=" + "\"xs:string\"" + ">"
    			+ "\n" + "<xs:length value=" + "\"1\"" + "/>" + "\n" + "</xs:restriction>" + "\n" + "</xs:simpleType>"
    			+ "\n" + "</xs:element>" + "\n" + "<xs:element name=" + "\"T\"" + " maxOccurs=" + "\"unbounded\"" + ">"
    			+ "\n" + "<xs:complexType>" + "\n" + "<xs:sequence>" + "\n" + "<xs:element name=" + "\"Key\"" + ">"
    			+ "\n" + "<xs:simpleType>" + "\n" + "<xs:restriction base=" + "\"xs:string\"" + ">" + "\n"
    			+ "<xs:length value=" + "\"1\"" + "/>" + "\n" + "</xs:restriction>" + "\n" + "</xs:simpleType>" + "\n"
    			+ "</xs:element>" + "\n" + "<xs:element name=" + "\"F3\"" + ">" + "\n" + "<xs:simpleType>" + "\n"
    			+ "<xs:restriction base=" + "\"xs:string\"" + ">" + "\n" + "<xs:length value=" + "\"1\"" + "/>" + "\n"
    			+ "</xs:restriction>" + "\n" + "</xs:simpleType>" + "\n" + "</xs:element>" + "\n" + "</xs:sequence>"
    			+ "\n" + "</xs:complexType>" + "\n" + "</xs:element>" + "\n" + "</xs:sequence>" + "\n"
    			+ "</xs:complexType>" + "\n" + "</xs:element>" + "\n" + "</xs:sequence>" + "\n" + "</xs:complexType>"
    			+ "\n" + "</xs:element>" + "\n" + "</xs:sequence>" + "\n" + "</xs:complexType>" + "\n" + "</xs:element>"
    			+ "\n" + "</xs:sequence>" + "\n" + "</xs:complexType>" + "\n" + "</xs:element>" + "\n" + "</xs:schema>";
		
		String recordS = "H,I,T";
		XSDParentInfoReader objP = new XSDParentInfoReader();
		 System.out.println(objP.getParentInfo(strSchema,recordS,"Doc","Rec","" ).toString());
       // System.out.println(objP.getParentInfo(file,recordS,"PEXR2001","IDOC" ).toString());
	}
	
/*	public static String getStringFromDocument(Document doc) throws TransformerException {
	    DOMSource domSource = new DOMSource(doc);
	    StringWriter writer = new StringWriter();
	    StreamResult result = new StreamResult(writer);
	    TransformerFactory tf = TransformerFactory.newInstance();
	    Transformer transformer = tf.newTransformer();
	    transformer.transform(domSource, result);
	    return writer.toString();
	}*/

	public  HashMap<String, Object> getParentInfo(String fileIn, String recordS,String Root, String Header,String Namespace) throws ClassNotFoundException, InstantiationException, IllegalAccessException, ClassCastException, FileNotFoundException, TransformerException {

		String stt = loadXsdDocument(fileIn,Root,Namespace);
	
		Document docO = convertStringToXMLDocument(stt);
		
		HashMap <String,Object> hP = new HashMap<String,Object>();
		
		String[] recStruc = recordS.split(",");

		for (int i = 0; i < recStruc.length; i++) {
			NodeList nn = docO.getElementsByTagNameNS("*",recStruc[i]);
			Element e = (Element) nn.item(0);
			Element eP = (Element) e.getParentNode();
			if(eP.getLocalName().equals(Header)){
				
				hP.put(recStruc[i] + ".parent", "Root");
			}else{
			
			hP.put(recStruc[i] + ".parent", eP.getNodeName());
			}
			
		}
		return hP;
	}
}
