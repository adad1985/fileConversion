package com.fcc.conversion;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSInput;
import org.xml.sax.SAXException;

public class XSDInformationExtractor {

	public LinkedHashMap<String, Object> getFCCParam(String fileIn, String pathToElement, String convType,String prefixNumSegment,String suffixNumSegment, int keyFieldIndex) throws Exception {

		
		LinkedHashMap<String, Object> hash = new LinkedHashMap<String, Object>();
		String intSuffix = "";
		try {
			if(convType.equals("XMLToPlain")){
				
				intSuffix = "N";
			}
			// parse the document
			hash.put("documentName", pathToElement.substring(1, pathToElement.length()));
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			docBuilderFactory.setNamespaceAware(true);
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();

			InputStream XSDfileInpPath = new ByteArrayInputStream(fileIn.getBytes());
		//	InputStream XSDfileInpPath = new FileInputStream(fileIn);
		//	DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
			//DOMImplementationLS impl = (DOMImplementationLS) registry.getDOMImplementation("LS");
			//LSInput XSDSchemaLS = impl.createLSInput();
			//XSDSchemaLS.setByteStream(XSDfileInpPath);
			
			
			Document doc = (Document) docBuilder.parse(XSDfileInpPath);
			NodeList list = ((org.w3c.dom.Document) doc).getElementsByTagNameNS("*", "element");
			NodeList listSch = ((org.w3c.dom.Document) doc).getElementsByTagNameNS("*", "schema");
			String PathG = pathToElement;

			String Root = "";
			String Header = "";
			String Namespace = "";
			String[] pathA = pathToElement.split("/");

			if (pathA.length > 2) {

				PathG = pathA[pathA.length - 1];
				Root = pathA[1];
				Header = PathG;
			} else {
				Root = pathA[1];
				Header = pathA[1];
			}
		//	Namespace = (String) list.item(0).getNamespaceURI();
			Element eS = (Element)listSch.item(0);
			
		//System.out.println(eS.getAttribute("xmlns:target"));
		Namespace = eS.getAttribute("targetNamespace");
		
			XPath xPath = XPathFactory.newInstance().newXPath();
			
			NodeList nodes = (NodeList) xPath.evaluate(
					"//*[local-name()='element'] [@*[local-name() ='name' and .= '" + Header + "']]//*", doc,
					XPathConstants.NODESET);

			
			
			
			StringBuilder sSim = new StringBuilder();
			StringBuilder sSimL = new StringBuilder();
			StringBuilder sCom = new StringBuilder();
			
			String sComs = "";

			for (int i = 0; i < nodes.getLength(); i++) {
				
				Element first = (Element) nodes.item(i);
				
				
				for (int e1 = 0; e1 < first.getChildNodes().getLength(); e1++) {
					if (first.getChildNodes().item(e1).getNodeType() == Node.ELEMENT_NODE
							&& first.getChildNodes().item(e1).getNodeName().contains("complexType")) {
						String nm = first.getAttribute("name");
						
						/*Element par = (Element)first.getParentNode().getParentNode().getParentNode();
						if(par.getAttribute("name").equals(Header)){
							hash.put(nm + ".parent", "Root");
						}
							
						else{
						hash.put(nm + ".parent", par.getAttribute("name"));
						}*/

						
						sCom.append(",");
						sCom.append(nm);
						
						sSim.append("SPLIT");
						sSimL.append("SPLIT");
						
					}
					if (first.getChildNodes().item(e1).getNodeType() == Node.ELEMENT_NODE
							&& first.getChildNodes().item(e1).getNodeName().contains("simpleType")) {
						String nm2 = first.getAttribute("name");
					
						
						sSim.append(",");
						sSim.append(nm2);

						Element nl = (Element) first.getChildNodes().item(e1);
						sSimL.append(",");
						try{
							String xsdType = nl.getElementsByTagNameNS("*", "restriction").item(0).getAttributes().getNamedItem("base").getNodeValue();
							String numLen = "";
							String len = "";
							String minLen = "";
							String maxLen = "";
							String frac = "";
							
							if 	(xsdType.contains("int") || xsdType.contains("decimal")){
							
							//	sSimL.append(nl.getElementsByTagNameNS("*", "totalDigits").item(0).getAttributes().getNamedItem("value").getNodeValue()+intSuffix);
								numLen = nl.getElementsByTagNameNS("*", "totalDigits").item(0).getAttributes().getNamedItem("value").getNodeValue()+intSuffix;
								
									 frac = nl.getElementsByTagNameNS("*", "fractionDigits").item(0).getAttributes().getNamedItem("value").getNodeValue()+intSuffix;
							
									 if(convType.equals("XMLToPlain")){
									numLen = numLen + frac;
								}
								
								sSimL.append(numLen);
							}
							
							else if (xsdType.contains("date") ){
								
								len = "10";
								sSimL.append(len + "S"+ len);
								
							}
							else if (xsdType.contains("time") ){
								
								len = "8";
								sSimL.append(len + "S"+ len);
								
							}
							else if (xsdType.contains("dateTime") ){
								
								len = "19";
								sSimL.append(len + "S"+ len);
								
							}
							else{
								try{
								//sSimL.append(nl.getElementsByTagNameNS("*", "length").item(0).getAttributes().getNamedItem("value").getNodeValue());
							len = nl.getElementsByTagNameNS("*", "length").item(0).getAttributes().getNamedItem("value").getNodeValue();
							if(convType.equals("XMLToPlain")){
							sSimL.append(len + "S"+ len);
							}
							else{
								sSimL.append(len);
							}
								}
								catch(NullPointerException e){
								//	sSimL.append(nl.getElementsByTagNameNS("*", "maxLength").item(0).getAttributes().getNamedItem("value").getNodeValue());
								
								minLen = nl.getElementsByTagNameNS("*", "minLength").item(0).getAttributes().getNamedItem("value").getNodeValue();
								maxLen = nl.getElementsByTagNameNS("*", "maxLength").item(0).getAttributes().getNamedItem("value").getNodeValue();
								if(convType.equals("XMLToPlain")){
								sSimL.append(minLen + "S"+ maxLen);
								}
								else{
									sSimL.append(maxLen);
								}
								}
							}
						}
						catch(Exception e){
							continue;
						}
						
					}

				}

			}

			if (sCom.toString().startsWith(",")) {
				sComs = sCom.substring(1);

				hash.put("recordsetStructure", sComs);
				
				XSDParentInfoReader xobP = new XSDParentInfoReader();
				HashMap<String, Object> hP = xobP.getParentInfo(fileIn, sComs, Root, Header,Namespace);
				hash.putAll(hP);
			}

			hash.put("documentNamespace", Namespace);
			String[] arr = sSim.toString().split("SPLIT,");
			String[] arrC = sCom.toString().split(",");
			String[] arrL = sSimL.toString().split("SPLIT,");
			
			
			for (int u = 1; u < arr.length; u++) {
				hash.put(arrC[u] + ".fieldNames", arr[u]);

				hash.put(arrC[u] + ".keyFieldName", arr[u].split(",")[keyFieldIndex]);
				hash.put(arrC[u] + ".fieldFixedLengths", arrL[u]);
				String recordT;
				//if(prefixNumSegment != null && !prefixNumSegment.isEmpty()  && arrC[u].substring(0, 1).equals(prefixNumSegment) && !arrC[u].substring(1).matches(".*[a-zA-Z]+.*")){

				//	if(prefixNumSegment != null && !prefixNumSegment.isEmpty()  && arrC[u].substring(0, prefixNumSegment.length()).equals(prefixNumSegment) && !arrC[u].substring(prefixNumSegment.length()).matches(".*[a-zA-Z]+.*")){

				if((prefixNumSegment != null && !prefixNumSegment.isEmpty()) || (suffixNumSegment != null && !suffixNumSegment.isEmpty()) )
				
				{
						// Change substring here
					String rec = "";
					if(prefixNumSegment != null && prefixNumSegment.length()>0){
						
						rec = arrC[u].substring(prefixNumSegment.length());	
					}
					
					if(suffixNumSegment != null && suffixNumSegment.length()>0){
						
						rec = rec.substring(0,rec.lastIndexOf(suffixNumSegment));	
					}
					recordT = rec;	
					
					}
					else {
						recordT = arrC[u];
					}
					hash.put(arrC[u] + ".keyFieldValue", recordT);
			//	hash.put(arrC[u] + ".keyFieldValue", arrC[u]);
				
			}
			return hash;
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException ed) {
			ed.printStackTrace();
		} catch (ArrayIndexOutOfBoundsException eA) {

		//	throw new Exception(
			//		"Issue while extracting FCC Information from XSD. Ensure that the XSD is valid & contains information related to field lengths");

		}

		return hash;

	}

	public static void main(String args[]) throws Exception {
		String docName = "/Doc/Rec";
		String XSDLoc = "C:/Users/D072889/Desktop/ICA/XSD_XML/FullTesting/XSDRec.xsd";
		String strSchema =  "<?xml version=" + "\"1.0\"" + " encoding=" + "\"UTF-8\"" + "?>" + "\n" + "<xs:schema xmlns:xs="
    			+ "\"http://www.w3.org/2001/XMLSchema\"" + " attributeFormDefault=" + "\"unqualified\""
    			+ " elementFormDefault=" + "\"qualified\"" + ">" + "\n" + "<xs:element name=" + "\"Doc\"" + ">" + "\n"
    			+ "<xs:complexType>" + "\n" + " <xs:sequence>" + "\n" +

    			"<xs:element name=" + "\"Rec\"" + ">" + "\n" + "<xs:complexType>" + "\n" + "<xs:sequence>" + "\n" +

    			"<xs:element name=" + "\"H\"" + ">" + "\n" + "<xs:complexType>" + "\n" + "<xs:sequence>" + "\n"
    			+ "<xs:element name=" + "\"Key\"" + ">" + "\n" + "<xs:simpleType>" + "\n" + "<xs:restriction base="
    			+ "\"xs:string\"" + ">" + "\n" + "<xs:length value=" + "\"1\"" + "/>" + "\n" + "</xs:restriction>"
    			+ "\n" + "</xs:simpleType>" + "\n" + "</xs:element>" + "\n" + "<xs:element name=" + "\"F1\"" + ">"
    			+ "\n" + "<xs:simpleType>" + "\n" + "<xs:restriction base=" + "\"xs:int\"" + ">" + "\n"
    			+ "<xs:totalDigits value=" + "\"1\"" + "/>" + "\n" + "</xs:restriction>" + "\n" + "</xs:simpleType>" + "\n"
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
		// LSInput XSDSchemaPath = impl.createLSInput();
		//InputStream io = new FileInputStream((new File(XSDLoc)));
		 // BuildMyString.com generated code. Please enjoy your string responsibly.

		String sb = "	" +
		"<xs:schema attributeFormDefault=\"unqualified\" elementFormDefault=\"qualified\"" +
		"    xmlns:xs=\"http://www.w3.org/2001/XMLSchema\">" +
		"    <xs:element name=\"PEXR2001\">" +
		"        <xs:complexType>" +
		"            <xs:sequence>" +
		"                <xs:element name=\"IDOC\">" +
		"                    <xs:complexType>" +
		"                        <xs:sequence>" +
		"                            <xs:element name=\"E1IDKU1\">" +
		"                                <xs:complexType>" +
		"                                    <xs:sequence>" +
		"                                        <xs:element name=\"BGMTYP\">" +
		"                                            <xs:simpleType>" +
		"                                                <xs:restriction base=\"xs:decimal\">" +
		"                                                    <xs:totalDigits value=\"10\"/>" +
		"													 <xs:fractionDigits value=\"0\"/>" +	
		"                                                </xs:restriction>" +
		"                                            </xs:simpleType>" +
		"                                        </xs:element>" +
		"                                        <xs:element name=\"BGMNAME\">" +
		"                                            <xs:simpleType>" +
		"                                                <xs:restriction base=\"xs:string\">" +
		"                                                    <xs:length value=\"11\"/>" +
		"                                                </xs:restriction>" +
		"                                            </xs:simpleType>" +
		"                                        </xs:element>" +
		"                                        <xs:element name=\"BGMREF\">" +
		"                                            <xs:simpleType>" +
		"                                                <xs:restriction base=\"xs:string\">" +
		"                                                    <xs:length value=\"12\"/>" +
		"                                                </xs:restriction>" +
		"                                            </xs:simpleType>" +
		"                                        </xs:element>" +
		"                                        <xs:element name=\"BGMLEV\">" +
		"                                            <xs:simpleType>" +
		"                                                <xs:restriction base=\"xs:string\">" +
		"                                                    <xs:length value=\"13\"/>" +
		"                                                </xs:restriction>" +
		"                                            </xs:simpleType>" +
		"                                        </xs:element>" +
		"                                    </xs:sequence>" +
		"                                </xs:complexType>" +
		"                            </xs:element>" +
		"                            <xs:element name=\"E1EDK03\" maxOccurs=\"unbounded\" minOccurs=\"0\">" +
		"                                <xs:complexType>" +
		"                                    <xs:sequence>" +
		"                                        <xs:element name=\"IDDAT\">" +
		"                                            <xs:simpleType>" +
		"                                                <xs:restriction base=\"xs:string\">" +
		"                                                    <xs:length value=\"10\"/>" +
		"                                                </xs:restriction>" +
		"                                            </xs:simpleType>" +
		"                                        </xs:element>" +
		"                                        <xs:element name=\"DATUM\">" +
		"                                            <xs:simpleType>" +
		"                                                <xs:restriction base=\"xs:string\">" +
		"                                                    <xs:length value=\"12\"/>" +
		"                                                </xs:restriction>" +
		"                                            </xs:simpleType>" +
		"                                        </xs:element>" +
		"                                    </xs:sequence>" +
		"                                </xs:complexType>" +
		"                            </xs:element>" +
		"                            <xs:element name=\"E1EDK02\">" +
		"                                <xs:complexType>" +
		"                                    <xs:sequence>" +
		"                                        <xs:element name=\"QUALF\">" +
		"                                            <xs:simpleType>" +
		"                                                <xs:restriction base=\"xs:string\">" +
		"                                                    <xs:length value=\"4\"/>" +
		"                                                </xs:restriction>" +
		"                                            </xs:simpleType>" +
		"                                        </xs:element>" +
		"                                        <xs:element name=\"BELNR\">" +
		"                                            <xs:simpleType>" +
		"                                                <xs:restriction base=\"xs:string\">" +
		"                                                    <xs:length value=\"16\"/>" +
		"                                                </xs:restriction>" +
		"                                            </xs:simpleType>" +
		"                                        </xs:element>" +
		"                                        <xs:element name=\"POSNR\">" +
		"                                            <xs:simpleType>" +
		"                                                <xs:restriction base=\"xs:string\">" +
		"                                                    <xs:length value=\"6\"/>" +
		"                                                </xs:restriction>" +
		"                                            </xs:simpleType>" +
		"                                        </xs:element>" +
		"                                    </xs:sequence>" +
		"                                </xs:complexType>" +
		"                            </xs:element>" +
		"                            <xs:element name=\"E1IDBW1\">" +
		"                                <xs:complexType>" +
		"                                    <xs:sequence>" +
		"                                        <xs:element name=\"INPWEGEB\">" +
		"                                            <xs:simpleType>" +
		"                                                <xs:restriction base=\"xs:string\">" +
		"                                                    <xs:length value=\"20\"/>" +
		"                                                </xs:restriction>" +
		"                                            </xs:simpleType>" +
		"                                        </xs:element>" +
		"                                        <xs:element name=\"INPWEEMP\">" +
		"                                            <xs:simpleType>" +
		"                                                <xs:restriction base=\"xs:string\">" +
		"                                                    <xs:length value=\"12\"/>" +
		"                                                </xs:restriction>" +
		"                                            </xs:simpleType>" +
		"                                        </xs:element>" +
		"                                        <xs:element name=\"E1EDM03\">" +
		"                                            <xs:complexType>" +
		"                                                <xs:sequence>" +
		"                                                    <xs:element name=\"IDDAT\">" +
		"                                                        <xs:simpleType>" +
		"                                                            <xs:restriction base=\"xs:string\">" +
		"                                                                <xs:length value=\"10\"/>" +
		"                                                            </xs:restriction>" +
		"                                                        </xs:simpleType>" +
		"                                                    </xs:element>" +
		"                                                    <xs:element name=\"DATUM\">" +
		"                                                        <xs:simpleType>" +
		"                                                            <xs:restriction base=\"xs:string\">" +
		"                                                                <xs:length value=\"12\"/>" +
		"                                                            </xs:restriction>" +
		"                                                        </xs:simpleType>" +
		"                                                    </xs:element>" +
		"                                                </xs:sequence>" +
		"                                            </xs:complexType>" +
		"                                        </xs:element>" +
		"                                    </xs:sequence>" +
		"                                </xs:complexType>" +
		"                            </xs:element>" +
		"                            <xs:element name=\"E1IDPU1\" maxOccurs=\"unbounded\" minOccurs=\"0\">" +
		"                                <xs:complexType>" +
		"                                    <xs:sequence>" +
		"                                        <xs:element name=\"DOCNAME\">" +
		"                                            <xs:simpleType>" +
		"                                                <xs:restriction base=\"xs:string\">" +
		"                                                    <xs:length value=\"12\"/>" +
		"                                                </xs:restriction>" +
		"                                            </xs:simpleType>" +
		"                                        </xs:element>" +
		"                                        <xs:element name=\"DOCNUMMR\">" +
		"                                            <xs:simpleType>" +
		"                                                <xs:restriction base=\"xs:string\">" +
		"                                                    <xs:length value=\"12\"/>" +
		"                                                </xs:restriction>" +
		"                                            </xs:simpleType>" +
		"                                        </xs:element>" +
		"                                        <xs:element name=\"E1IDPU5\" maxOccurs=\"unbounded\" minOccurs=\"0\">" +
		"                                            <xs:complexType>" +
		"                                                <xs:sequence>" +
		"                                                    <xs:element name=\"CUXDATUM\">" +
		"                                                        <xs:simpleType>" +
		"                                                            <xs:restriction base=\"xs:string\">" +
		"                                                                <xs:length value=\"12\"/>" +
		"                                                            </xs:restriction>" +
		"                                                        </xs:simpleType>" +
		"                                                    </xs:element>" +
		"                                                    <xs:element name=\"CUXZEIT\">" +
		"                                                        <xs:simpleType>" +
		"                                                            <xs:restriction base=\"xs:string\">" +
		"                                                                <xs:length value=\"12\"/>" +
		"                                                            </xs:restriction>" +
		"                                                        </xs:simpleType>" +
		"                                                    </xs:element>" +
		"                                                    <xs:element name=\"CUXKURS_M\">" +
		"                                                        <xs:simpleType>" +
		"                                                            <xs:restriction base=\"xs:string\">" +
		"                                                                <xs:length value=\"12\"/>" +
		"                                                            </xs:restriction>" +
		"                                                        </xs:simpleType>" +
		"                                                    </xs:element>" +
		"                                                    <xs:element name=\"E1EDU03\">" +
		"                                                        <xs:complexType>" +
		"                                                            <xs:sequence>" +
		"                                                                <xs:element name=\"IDDAT\">" +
		"                                                                    <xs:simpleType>" +
		"                                                                        <xs:restriction base=\"xs:string\">" +
		"                                                                            <xs:length value=\"10\"/>" +
		"                                                                        </xs:restriction>" +
		"                                                                    </xs:simpleType>" +
		"                                                                </xs:element>" +
		"                                                                <xs:element name=\"DATUM\">" +
		"                                                                    <xs:simpleType>" +
		"                                                                        <xs:restriction base=\"xs:string\">" +
		"                                                                            <xs:length value=\"10\"/>" +
		"                                                                        </xs:restriction>" +
		"                                                                    </xs:simpleType>" +
		"                                                                </xs:element>" +
		"                                                                <xs:element name=\"UZEIT\">" +
		"                                                                    <xs:simpleType>" +
		"                                                                        <xs:restriction base=\"xs:string\">" +
		"                                                                            <xs:length value=\"9\"/>" +
		"                                                                        </xs:restriction>" +
		"                                                                    </xs:simpleType>" +
		"                                                                </xs:element>" +
		"                                                            </xs:sequence>" +
		"                                                        </xs:complexType>" +
		"                                                    </xs:element>" +
		"                                                </xs:sequence>" +
		"                                            </xs:complexType>" +
		"                                        </xs:element>" +
		"                                        <xs:element name=\"E1EDP03\" maxOccurs=\"unbounded\" minOccurs=\"0\">" +
		"                                            <xs:complexType>" +
		"                                                <xs:sequence>" +
		"                                                    <xs:element name=\"IDDAT\">" +
		"                                                        <xs:simpleType>" +
		"                                                            <xs:restriction base=\"xs:string\">" +
		"                                                                <xs:length value=\"10\"/>" +
		"                                                            </xs:restriction>" +
		"                                                        </xs:simpleType>" +
		"                                                    </xs:element>" +
		"                                                    <xs:element name=\"DATUM\">" +
		"                                                        <xs:simpleType>" +
		"                                                            <xs:restriction base=\"xs:string\">" +
		"                                                                <xs:length value=\"10\"/>" +
		"                                                            </xs:restriction>" +
		"                                                        </xs:simpleType>" +
		"                                                    </xs:element>" +
		"                                                    <xs:element name=\"UZEIT\">" +
		"                                                        <xs:simpleType>" +
		"                                                            <xs:restriction base=\"xs:string\">" +
		"                                                                <xs:length value=\"9\"/>" +
		"                                                            </xs:restriction>" +
		"                                                        </xs:simpleType>" +
		"                                                    </xs:element>" +
		"                                                </xs:sequence>" +
		"                                            </xs:complexType>" +
		"                                        </xs:element>" +
		"                                        <xs:element name=\"E1EDP02\" maxOccurs=\"unbounded\" minOccurs=\"0\">" +
		"                                            <xs:complexType>" +
		"                                                <xs:sequence>" +
		"                                                    <xs:element name=\"UZEIT\">" +
		"                                                        <xs:simpleType>" +
		"                                                            <xs:restriction base=\"xs:string\">" +
		"                                                                <xs:length value=\"10\"/>" +
		"                                                            </xs:restriction>" +
		"                                                        </xs:simpleType>" +
		"                                                    </xs:element>" +
		"                                                    <xs:element name=\"BSARK\">" +
		"                                                        <xs:simpleType>" +
		"                                                            <xs:restriction base=\"xs:string\">" +
		"                                                                <xs:length value=\"10\"/>" +
		"                                                            </xs:restriction>" +
		"                                                        </xs:simpleType>" +
		"                                                    </xs:element>" +
		"                                                    <xs:element name=\"IHREZ\">" +
		"                                                        <xs:simpleType>" +
		"                                                            <xs:restriction base=\"xs:string\">" +
		"                                                                <xs:length value=\"12\"/>" +
		"                                                            </xs:restriction>" +
		"                                                        </xs:simpleType>" +
		"                                                    </xs:element>" +
		"                                                </xs:sequence>" +
		"                                            </xs:complexType>" +
		"                                        </xs:element>" +
		"                                        <xs:element name=\"E1IDPU2\">" +
		"                                            <xs:complexType>" +
		"                                                <xs:sequence>" +
		"                                                    <xs:element name=\"AJTGRUND\">" +
		"                                                        <xs:simpleType>" +
		"                                                            <xs:restriction base=\"xs:string\">" +
		"                                                                <xs:length value=\"12\"/>" +
		"                                                            </xs:restriction>" +
		"                                                        </xs:simpleType>" +
		"                                                    </xs:element>" +
		"                                                    <xs:element name=\"AJTRZEIL\">" +
		"                                                        <xs:simpleType>" +
		"                                                            <xs:restriction base=\"xs:string\">" +
		"                                                                <xs:length value=\"12\"/>" +
		"                                                            </xs:restriction>" +
		"                                                        </xs:simpleType>" +
		"                                                    </xs:element>" +
		"                                                </xs:sequence>" +
		"                                            </xs:complexType>" +
		"                                        </xs:element>" +
		"                                    </xs:sequence>" +
		"                                </xs:complexType>" +
		"                            </xs:element>" +
		"                        </xs:sequence>" +
		"                    </xs:complexType>" +
		"                </xs:element>						" +
		"            </xs:sequence>" +
		"        </xs:complexType>" +
		"    </xs:element>               " +
		"</xs:schema>";

		 // BuildMyString.com generated code. Please enjoy your string responsibly.

		String jorgSch = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><!--Copyright statement for Type System VDA Flat: Verband der Automobilindustrie e.V. (VDA) Germany " +
		"				Reprint, also in extracts, is only permitted, if the source is stated.--><!--Copyright statement for ISO Codelists: Copyright (c) 2017, ISO" +
		"All ISO content is copyright protected. The copyright is owned by ISO. Any use of the content, including copying of it in whole or in part, for example to another Internet site, is prohibited and would require written permission from ISO." +
		"All ISO publications are also protected by copyright. The copyright ownership of ISO is clearly indicated on every ISO publication. Any unauthorized use such as copying, scanning or distribution is prohibited." +
		"Requests for permission should be addressed to the ISO Central Secretariat or directly through the ISO member in your country." +
		"See more: https://www.iso.org/privacy-and-copyright.html--><!--Copyright statement for UN/CEFACT Codelists: Copyright (c) United Nations 2000-2008. All rights reserved. None of the materials provided on this web site may be used, reproduced or transmitted, in whole or in part, in any form or by any means, electronic or mechanical, including photocopying, recording or the use of any information storage and retrieval system, except as provided for in the Terms and Conditions of Use of United Nations Web Sites, without permission in writing from the publisher. To request such permission and for further enquiries, contact the Secretary of the Publications Board, United Nations, New York, NY, 10017, USA (pubboard@un.org; Telephone: (+1) 212-963-4664; Facsimile: (+1) 212-963-0077). See also: http://www.unece.org/legal_notice/copyrightnotice.html--><xsd:schema xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:vc=\"http://www.w3.org/2007/XMLSchema-versioning\" xmlns:hci=\"http://sap.com/hci\" xmlns:ica=\"http://www.sap.com/ica/metameta_schema/metainfo\" elementFormDefault=\"qualified\" vc:minVersion=\"1.1\"><xsd:annotation><xsd:appinfo><hci:resourceinfo xmlns:hci=\"http://www.w3.org/2001/XMLSchema\"><hci:info key=\"typeSystem\" value=\"VDA_Flat\"/><hci:info key=\"typeSystemVersion\" value=\"All_de\"/><hci:info key=\"category\" value=\"MIG\"/><hci:info key=\"rootNode\" value=\"M_VDA4905_V4\"/><hci:info key=\"responsibleAgencyName\" value=\"Verband der Automobilindustrie E.V.\"/><hci:info key=\"responsibleAgencyCode\" value=\"204\"/><hci:info key=\"messageType\" value=\"VDA4905_V4\"/><hci:info key=\"messageName\" value=\"Lieferabruf\"/><hci:info key=\"migName\" value=\"E2ETest MIG18A - VDA_Flat VDA4905_V4 (pls do not change!!)\"/><hci:info key=\"migID\" value=\"5deb976fb0f74dcfafdcd9ffc4711fc3\"/><hci:info key=\"migVersion\" value=\"1.0\"/><hci:info key=\"migStatus\" value=\"Draft\"/></hci:resourceinfo></xsd:appinfo></xsd:annotation><xsd:element name=\"M_VDA4905_V4\"><xsd:complexType><xsd:sequence><xsd:element maxOccurs=\"1\" minOccurs=\"1\" name=\"S_511\"><xsd:complexType><xsd:sequence><xsd:element fixed=\"511\" maxOccurs=\"1\" minOccurs=\"1\" name=\"D_511_01\"><xsd:simpleType><xsd:restriction base=\"xsd:decimal\"><xsd:fractionDigits value=\"0\"/><xsd:totalDigits value=\"3\"/></xsd:restriction></xsd:simpleType></xsd:element><xsd:element fixed=\"02\" maxOccurs=\"1\" minOccurs=\"1\" name=\"D_511_02\"><xsd:simpleType><xsd:restriction base=\"xsd:decimal\"><xsd:fractionDigits value=\"0\"/><xsd:totalDigits value=\"2\"/></xsd:restriction></xsd:simpleType></xsd:element><xsd:element maxOccurs=\"1\" minOccurs=\"1\" name=\"D_511_03\"><xsd:simpleType><xsd:restriction base=\"xsd:string\"><xsd:minLength value=\"9\"/><xsd:maxLength value=\"9\"/></xsd:restriction></xsd:simpleType></xsd:element><xsd:element maxOccurs=\"1\" minOccurs=\"1\" name=\"D_511_04\"><xsd:simpleType><xsd:restriction base=\"xsd:string\"><xsd:minLength value=\"9\"/><xsd:maxLength value=\"9\"/></xsd:restriction></xsd:simpleType></xsd:element><xsd:element maxOccurs=\"1\" minOccurs=\"1\" name=\"D_511_05\"><xsd:simpleType><xsd:restriction base=\"xsd:decimal\"><xsd:fractionDigits value=\"0\"/><xsd:totalDigits value=\"5\"/></xsd:restriction></xsd:simpleType></xsd:element><xsd:element maxOccurs=\"1\" minOccurs=\"1\" name=\"D_511_06\"><xsd:simpleType><xsd:restriction base=\"xsd:decimal\"><xsd:fractionDigits value=\"0\"/><xsd:totalDigits value=\"5\"/></xsd:restriction></xsd:simpleType></xsd:element><xsd:element maxOccurs=\"1\" minOccurs=\"1\" name=\"D_511_07\"><xsd:simpleType><xsd:restriction base=\"xsd:decimal\"><xsd:fractionDigits value=\"0\"/><xsd:totalDigits value=\"6\"/></xsd:restriction></xsd:simpleType></xsd:element></xsd:sequence></xsd:complexType></xsd:element><xsd:element maxOccurs=\"unbounded\" minOccurs=\"1\" name=\"G_SG1\"><xsd:complexType><xsd:sequence><xsd:element maxOccurs=\"1\" minOccurs=\"1\" name=\"S_512\"><xsd:complexType><xsd:sequence><xsd:element fixed=\"512\" maxOccurs=\"1\" minOccurs=\"1\" name=\"D_512_01\"><xsd:simpleType><xsd:restriction base=\"xsd:decimal\"><xsd:fractionDigits value=\"0\"/><xsd:totalDigits value=\"3\"/></xsd:restriction></xsd:simpleType></xsd:element><xsd:element fixed=\"01\" maxOccurs=\"1\" minOccurs=\"1\" name=\"D_512_02\"><xsd:simpleType><xsd:restriction base=\"xsd:decimal\"><xsd:fractionDigits value=\"0\"/><xsd:totalDigits value=\"2\"/></xsd:restriction></xsd:simpleType></xsd:element><xsd:element maxOccurs=\"1\" minOccurs=\"1\" name=\"D_512_03\"><xsd:simpleType><xsd:restriction base=\"xsd:string\"><xsd:minLength value=\"3\"/><xsd:maxLength value=\"3\"/></xsd:restriction></xsd:simpleType></xsd:element><xsd:element maxOccurs=\"1\" minOccurs=\"1\" name=\"D_512_04\"><xsd:simpleType><xsd:restriction base=\"xsd:decimal\"><xsd:fractionDigits value=\"0\"/><xsd:totalDigits value=\"9\"/></xsd:restriction></xsd:simpleType></xsd:element><xsd:element maxOccurs=\"1\" minOccurs=\"1\" name=\"D_512_05\"><xsd:simpleType><xsd:restriction base=\"xsd:decimal\"><xsd:fractionDigits value=\"0\"/><xsd:totalDigits value=\"6\"/></xsd:restriction></xsd:simpleType></xsd:element><xsd:element maxOccurs=\"1\" minOccurs=\"1\" name=\"D_512_06\"><xsd:simpleType><xsd:restriction base=\"xsd:decimal\"><xsd:fractionDigits value=\"0\"/><xsd:totalDigits value=\"9\"/></xsd:restriction></xsd:simpleType></xsd:element><xsd:element maxOccurs=\"1\" minOccurs=\"1\" name=\"D_512_07\"><xsd:simpleType><xsd:restriction base=\"xsd:decimal\"><xsd:fractionDigits value=\"0\"/><xsd:totalDigits value=\"6\"/></xsd:restriction></xsd:simpleType></xsd:element><xsd:element maxOccurs=\"1\" minOccurs=\"1\" name=\"D_512_08\"><xsd:simpleType><xsd:restriction base=\"xsd:string\"><xsd:minLength value=\"0\"/><xsd:maxLength value=\"22\"/></xsd:restriction></xsd:simpleType></xsd:element><xsd:element maxOccurs=\"1\" minOccurs=\"0\" name=\"D_512_10\"><xsd:simpleType><xsd:restriction base=\"xsd:string\"><xsd:minLength value=\"0\"/><xsd:maxLength value=\"12\"/></xsd:restriction></xsd:simpleType></xsd:element><xsd:element maxOccurs=\"1\" minOccurs=\"1\" name=\"D_512_13\"><xsd:simpleType><xsd:restriction base=\"xsd:string\"><xsd:enumeration value=\"KG\"/><xsd:enumeration value=\"L\"/><xsd:enumeration value=\"ST\"/></xsd:restriction></xsd:simpleType></xsd:element><xsd:element maxOccurs=\"1\" minOccurs=\"1\" name=\"D_512_14\"><xsd:simpleType><xsd:restriction base=\"xsd:string\"><xsd:enumeration value=\"L\"/></xsd:restriction></xsd:simpleType></xsd:element><xsd:element maxOccurs=\"1\" minOccurs=\"1\" name=\"D_512_17\"><xsd:simpleType><xsd:restriction base=\"xsd:string\"><xsd:enumeration value=\"E\"/></xsd:restriction></xsd:simpleType></xsd:element></xsd:sequence></xsd:complexType></xsd:element><xsd:element maxOccurs=\"1\" minOccurs=\"1\" name=\"S_513\"><xsd:complexType><xsd:sequence><xsd:element fixed=\"513\" maxOccurs=\"1\" minOccurs=\"1\" name=\"D_513_01\"><xsd:simpleType><xsd:restriction base=\"xsd:decimal\"><xsd:fractionDigits value=\"0\"/><xsd:totalDigits value=\"3\"/></xsd:restriction></xsd:simpleType></xsd:element><xsd:element fixed=\"01\" maxOccurs=\"1\" minOccurs=\"1\" name=\"D_513_02\"><xsd:simpleType><xsd:restriction base=\"xsd:decimal\"><xsd:fractionDigits value=\"0\"/><xsd:totalDigits value=\"2\"/></xsd:restriction></xsd:simpleType></xsd:element><xsd:element maxOccurs=\"1\" minOccurs=\"1\" name=\"D_513_03\"><xsd:simpleType><xsd:restriction base=\"xsd:decimal\"><xsd:fractionDigits value=\"0\"/><xsd:totalDigits value=\"6\"/></xsd:restriction></xsd:simpleType></xsd:element><xsd:element maxOccurs=\"1\" minOccurs=\"1\" name=\"D_513_08\"><xsd:simpleType><xsd:restriction base=\"xsd:decimal\"><xsd:fractionDigits value=\"0\"/><xsd:totalDigits value=\"6\"/></xsd:restriction></xsd:simpleType></xsd:element><xsd:element maxOccurs=\"1\" minOccurs=\"1\" name=\"D_513_09\"><xsd:simpleType><xsd:restriction base=\"xsd:decimal\"><xsd:fractionDigits value=\"0\"/><xsd:totalDigits value=\"9\"/></xsd:restriction></xsd:simpleType></xsd:element></xsd:sequence></xsd:complexType></xsd:element></xsd:sequence></xsd:complexType></xsd:element><xsd:element maxOccurs=\"1\" minOccurs=\"1\" name=\"S_519\"><xsd:complexType><xsd:sequence><xsd:element fixed=\"519\" maxOccurs=\"1\" minOccurs=\"1\" name=\"D_519_01\"><xsd:simpleType><xsd:restriction base=\"xsd:decimal\"><xsd:fractionDigits value=\"0\"/><xsd:totalDigits value=\"3\"/></xsd:restriction></xsd:simpleType></xsd:element><xsd:element fixed=\"03\" maxOccurs=\"1\" minOccurs=\"1\" name=\"D_519_02\"><xsd:simpleType><xsd:restriction base=\"xsd:decimal\"><xsd:fractionDigits value=\"0\"/><xsd:totalDigits value=\"2\"/></xsd:restriction></xsd:simpleType></xsd:element><xsd:element maxOccurs=\"1\" minOccurs=\"1\" name=\"D_519_03\"><xsd:simpleType><xsd:restriction base=\"xsd:decimal\"><xsd:fractionDigits value=\"0\"/><xsd:totalDigits value=\"7\"/></xsd:restriction></xsd:simpleType></xsd:element><xsd:element maxOccurs=\"1\" minOccurs=\"1\" name=\"D_519_04\"><xsd:simpleType><xsd:restriction base=\"xsd:decimal\"><xsd:fractionDigits value=\"0\"/><xsd:totalDigits value=\"7\"/></xsd:restriction></xsd:simpleType></xsd:element><xsd:element maxOccurs=\"1\" minOccurs=\"1\" name=\"D_519_05\"><xsd:simpleType><xsd:restriction base=\"xsd:decimal\"><xsd:fractionDigits value=\"0\"/><xsd:totalDigits value=\"7\"/></xsd:restriction></xsd:simpleType></xsd:element><xsd:element maxOccurs=\"1\" minOccurs=\"1\" name=\"D_519_06\"><xsd:simpleType><xsd:restriction base=\"xsd:decimal\"><xsd:fractionDigits value=\"0\"/><xsd:totalDigits value=\"7\"/></xsd:restriction></xsd:simpleType></xsd:element><xsd:element maxOccurs=\"1\" minOccurs=\"1\" name=\"D_519_07\"><xsd:simpleType><xsd:restriction base=\"xsd:decimal\"><xsd:fractionDigits value=\"0\"/><xsd:totalDigits value=\"7\"/></xsd:restriction></xsd:simpleType></xsd:element><xsd:element maxOccurs=\"1\" minOccurs=\"1\" name=\"D_519_08\"><xsd:simpleType><xsd:restriction base=\"xsd:decimal\"><xsd:fractionDigits value=\"0\"/><xsd:totalDigits value=\"7\"/></xsd:restriction></xsd:simpleType></xsd:element><xsd:element maxOccurs=\"1\" minOccurs=\"1\" name=\"D_519_09\"><xsd:simpleType><xsd:restriction base=\"xsd:decimal\"><xsd:fractionDigits value=\"0\"/><xsd:totalDigits value=\"7\"/></xsd:restriction></xsd:simpleType></xsd:element><xsd:element maxOccurs=\"1\" minOccurs=\"1\" name=\"D_519_10\"><xsd:simpleType><xsd:restriction base=\"xsd:decimal\"><xsd:fractionDigits value=\"0\"/><xsd:totalDigits value=\"7\"/></xsd:restriction></xsd:simpleType></xsd:element></xsd:sequence></xsd:complexType></xsd:element></xsd:sequence></xsd:complexType></xsd:element></xsd:schema>";

		XSDInformationExtractor xObj = new XSDInformationExtractor();
		File fileI = new File(XSDLoc);
	//	HashMap<String, Object> hS = xObj.getFCCParam(jorgSch, "/M_VDA4905_V4","XMLToPlain","");
		HashMap<String, Object> hS2 = xObj.getFCCParam(sb, "/PEXR2001/IDOC","XMLToPlain","","",0);
		//LinkedHashMap<String, Object> hS2 = xObj.getFCCParam(strSchema, docName,"XMLToPlain","");
		
		System.out.println(hS2);
	}

}
