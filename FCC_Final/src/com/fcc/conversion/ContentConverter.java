package com.fcc.conversion;

import java.io.File;
import java.io.FileWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.io.ByteArrayInputStream;
import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.fcc.conversion.XSDInformationExtractor;

public class ContentConverter {
public static String fileConversionOutput(String XSDfileIn, String docName, String convType, String strInp,String prefixNumSegment, String suffixNumSegment){
//	File fileIn = new File(XSDLoc);
	byte[] encoded;
	//String str = "";
	String st = "";
	XSDInformationExtractor xObj = new XSDInformationExtractor();
	
	try {
		
	
		HashMap<String, Object> hS = xObj.getFCCParam(XSDfileIn, docName,convType,prefixNumSegment,suffixNumSegment,0);
	
		hS.put("offsetStart", "0");
		hS.put("offsetEnd", "3");
		hS.put("trimContents","true");
		hS.put("trimNum","true");
		String stRec = (String) hS.get("recordsetStructure");
		//hS.put( "defaultFieldSeparator", ",");
		
		String[] stRecords = stRec.split(",");
		for (int i = 0; i < stRecords.length; i++) {
			hS.put(stRecords[i] + ".fixedLengthTooShortHandling", "Cut");
			hS.put(stRecords[i] + ".endSeparator", "\n");
		
	//		hS.put(stRecords[i] + ".fieldSeparator", ",");
			
		}
		if(convType.equals("PlainToXML")){
		PlainToXMLConversion obj = new PlainToXMLConversion(strInp, hS);
		obj.getParameters();
		obj.parseInput();

		byte[] bo = (byte[]) (obj.generateOutput());
		 st = new String(bo);
		}
		
		else if(convType.equals("XMLToPlain")){
		XMLToPlainConversion obj = new XMLToPlainConversion(strInp, hS);
		obj.getParameters();
		obj.parseInput();

		byte[] bo = (byte[]) (obj.generateOutput());
		 st = new String(bo);
		}
		
		
	}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	return st;
	// Below codeline replaces last CRLF from the flat output
	//	return st.replaceAll("\r\n$", "");

}
	
static boolean validateAgainstXSD(String xml, String xsd)
{
    try
    {
    	InputStream xmlS = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
    	InputStream xsdS = new ByteArrayInputStream(xsd.getBytes(StandardCharsets.UTF_8));
        SchemaFactory factory = 
            SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = factory.newSchema(new StreamSource(xsdS));
        Validator validator = schema.newValidator();
        
      
        
        validator.validate(new StreamSource(xmlS));
        return true;
    }
 
	 catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		 return false;
	}
}
	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, ClassCastException, IOException {
		
		String strSchemaO =  "<?xml version=" + "\"1.0\"" + " encoding=" + "\"UTF-8\"" + "?>" + "\n" + "<xs:schema xmlns:xs="
    			+ "\"http://www.w3.org/2001/XMLSchema\"" + " attributeFormDefault=" + "\"unqualified\""
    			+ " elementFormDefault=" + "\"qualified\"" + ">" + "\n" + "<xs:element name=" + "\"Doc\"" + ">" + "\n"
    			+ "<xs:complexType>" + "\n" + " <xs:sequence>" + "\n" +

    			"<xs:element name=" + "\"Rec\"" + ">" + "\n" + "<xs:complexType>" + "\n" + "<xs:sequence>" + "\n" +

    			"<xs:element name=" + "\"H\"" + ">" + "\n" + "<xs:complexType>" + "\n" + "<xs:sequence>" + "\n"
    			+ "<xs:element name=" + "\"Key\"" + ">" + "\n" + "<xs:simpleType>" + "\n" + "<xs:restriction base="
    			+ "\"xs:string\"" + ">" + "\n" + "<xs:length value=" + "\"1\"" + "/>" + "\n" + "</xs:restriction>"
    			+ "\n" + "</xs:simpleType>" + "\n" + "</xs:element>" + "\n" + "<xs:element name=" + "\"F1\"" + ">"
    			+ "\n" + "<xs:simpleType>" + "\n" + "<xs:restriction base=" + "\"xs:int\"" + ">" + "\n"
    			+ "<xs:totalDigits value=" + "\"3\"" + "/>" + "\n" + "</xs:restriction>" + "\n" + "</xs:simpleType>" + "\n"
    			+ "</xs:element>" + "\n" + "<xs:element name=" + "\"I\"" + ">" + "\n" + "<xs:complexType>" + "\n"
    			+ "<xs:sequence>" + "\n" + "<xs:element name=" + "\"Key\"" + ">" + "\n" + "<xs:simpleType>" + "\n"
    			+ "<xs:restriction base=" + "\"xs:string\"" + ">" + "\n" + "<xs:length value=" + "\"1\"" + "/>" + "\n"
    			+ "</xs:restriction>" + "\n" + "</xs:simpleType>" + "\n" + "</xs:element>" + "\n" + "<xs:element name="
    			+ "\"F2\"" + ">" + "\n" + "<xs:simpleType>" + "\n" + "<xs:restriction base=" + "\"xs:string\"" + ">"
    			+ "\n" + "<xs:length value=" + "\"2\"" + "/>" + "\n" + "</xs:restriction>" + "\n" + "</xs:simpleType>"
    			+ "\n" + "</xs:element>" + "\n" + "<xs:element name=" + "\"T\"" + " maxOccurs=" + "\"1\"" + ">"
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
		
		String strSchema =  "<?xml version=" + "\"1.0\"" + " encoding=" + "\"UTF-8\"" + "?>" + "\n" + "<xsd:schema xmlns:xsd="
    			+ "\"http://www.w3.org/2001/XMLSchema\"" 
    			+" targetNamespace="
    			+ "\"http://www.example.org/Test\""
    			+" attributeFormDefault=" + "\"unqualified\""
    			+ " elementFormDefault=" + "\"qualified\"" + ">" + "\n" + "<xsd:element name=" + "\"Doc\"" + ">" + "\n"
    			+ "<xsd:complexType>" + "\n" + " <xsd:sequence>" + "\n" +

    			"<xsd:element name=" + "\"Rec\"" + ">" + "\n" + "<xsd:complexType>" + "\n" + "<xsd:sequence>" + "\n" +

    			"<xsd:element name=" + "\"H\"" + ">" + "\n" + "<xsd:complexType>" + "\n" + "<xsd:sequence>" + "\n"
    			+ "<xsd:element name=" + "\"Key\"" + ">" + "\n" + "<xsd:simpleType>" + "\n" + "<xsd:restriction base="
    			+ "\"xsd:string\"" + ">" + "\n" + "<xsd:length value=" + "\"1\"" + "/>" + "\n" + "</xsd:restriction>"
    			+ "\n" + "</xsd:simpleType>" + "\n" + "</xsd:element>" + "\n" + "<xsd:element name=" + "\"F1\"" + ">"
    			+ "\n" + "<xsd:simpleType>" + "\n" + "<xsd:restriction base=" + "\"xsd:int\"" + ">" + "\n"
    			+ "<xsd:totalDigits value=" + "\"3\"" + "/>" + "\n" + "</xsd:restriction>" + "\n" + "</xsd:simpleType>" + "\n"
    			+ "</xsd:element>" + "\n" + "<xsd:element name=" + "\"I\"" + ">" + "\n" + "<xsd:complexType>" + "\n"
    			+ "<xsd:sequence>" + "\n" + "<xsd:element name=" + "\"Key\"" + ">" + "\n" + "<xsd:simpleType>" + "\n"
    			+ "<xsd:restriction base=" + "\"xsd:string\"" + ">" + "\n" + "<xsd:length value=" + "\"1\"" + "/>" + "\n"
    			+ "</xsd:restriction>" + "\n" + "</xsd:simpleType>" + "\n" + "</xsd:element>" + "\n" + "<xsd:element name="
    			+ "\"F2\"" + ">" + "\n" + "<xsd:simpleType>" + "\n" + "<xsd:restriction base=" + "\"xsd:string\"" + ">"
    			+ "\n" + "<xsd:length value=" + "\"2\"" + "/>" + "\n" + "</xsd:restriction>" + "\n" + "</xsd:simpleType>"
    			+ "\n" + "</xsd:element>" + "\n" + "<xsd:element name=" + "\"T\"" + " maxOccurs=" + "\"1\"" + ">"
    			+ "\n" + "<xsd:complexType>" + "\n" + "<xsd:sequence>" + "\n" + "<xsd:element name=" + "\"Key\"" + ">"
    			+ "\n" + "<xsd:simpleType>" + "\n" + "<xsd:restriction base=" + "\"xsd:string\"" + ">" + "\n"
    			+ "<xsd:length value=" + "\"1\"" + "/>" + "\n" + "</xsd:restriction>" + "\n" + "</xsd:simpleType>" + "\n"
    			+ "</xsd:element>" + "\n" + "<xsd:element name=" + "\"F3\"" + ">" + "\n" + "<xsd:simpleType>" + "\n"
    			+ "<xsd:restriction base=" + "\"xsd:string\"" + ">" + "\n" + "<xsd:length value=" + "\"1\"" + "/>" + "\n"
    			+ "</xsd:restriction>" + "\n" + "</xsd:simpleType>" + "\n" + "</xsd:element>" + "\n" + "</xsd:sequence>"
    			+ "\n" + "</xsd:complexType>" + "\n" + "</xsd:element>" + "\n" + "</xsd:sequence>" + "\n"
    			+ "</xsd:complexType>" + "\n" + "</xsd:element>" + "\n" + "</xsd:sequence>" + "\n" + "</xsd:complexType>"
    			+ "\n" + "</xsd:element>" + "\n" + "</xsd:sequence>" + "\n" + "</xsd:complexType>" + "\n" + "</xsd:element>"
    			+ "\n" + "</xsd:sequence>" + "\n" + "</xsd:complexType>" + "\n" + "</xsd:element>" + "\n" + "</xsd:schema>";
		
		String strSchema2 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
"<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" attributeFormDefault=\"unqualified\" elementFormDefault=\"qualified\">" +
"<xs:element name=\"Doc\">" +
"<xs:complexType>" +
" <xs:sequence>" +
"<xs:element name=\"Rec\">" +
"<xs:complexType>" +
"<xs:sequence>" +
"<xs:element name=\"H\">" +
"<xs:complexType>" +
"<xs:sequence>" +
"<xs:element name=\"Key\">" +
"<xs:simpleType>" +
"<xs:restriction base=\"xs:string\">" +
"</xs:restriction>" +
"</xs:simpleType>" +
"</xs:element>" +
"<xs:element name=\"F1\">" +
"<xs:simpleType>" +
"<xs:restriction base=\"xs:string\">" +

"</xs:restriction>" +
"</xs:simpleType>" +
"</xs:element>" +
"<xs:element name=\"I\">" +
"<xs:complexType>" +
"<xs:sequence>" +
"<xs:element name=\"Key\">" +
"<xs:simpleType>" +
"<xs:restriction base=\"xs:string\">" +
"</xs:restriction>" +
"</xs:simpleType>" +
"</xs:element>" +
"<xs:element name=\"F2\">" +
"<xs:simpleType>" +
"<xs:restriction base=\"xs:string\">" +
"</xs:restriction>" +
"</xs:simpleType>" +
"</xs:element>" +
"<xs:element name=\"T\" maxOccurs=\"unbounded\">" +
"<xs:complexType>" +
"<xs:sequence>" +
"<xs:element name=\"Key\">" +
"<xs:simpleType>" +
"<xs:restriction base=\"xs:string\">" +
"</xs:restriction>" +
"</xs:simpleType>" +
"</xs:element>" +
"<xs:element name=\"F3\">" +
"<xs:simpleType>" +
"<xs:restriction base=\"xs:string\">" +
"</xs:restriction>" +
"</xs:simpleType>" +
"</xs:element>" +
"</xs:sequence>" +
"</xs:complexType>" +
"</xs:element>" +
"</xs:sequence>" +
"</xs:complexType>" +
"</xs:element>" +
"</xs:sequence>" +
"</xs:complexType>" +
"</xs:element>" +
"</xs:sequence>" +
"</xs:complexType>" +
"</xs:element>" +
"</xs:sequence>" +
"</xs:complexType>" +
"</xs:element>" +
"</xs:schema>";

	
		
		
		String inputMessage = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><ns:Doc xmlns:ns=\"http://www.w3.org/2001/XMLSchema\"><ns:Rec><H><Key>H</Key><F1>1</F1><I><Key>I</Key><F2>2</F2><T><Key>T</Key><F3>3</F3></T><T><Key>T</Key><F3>4</F3></T></I></H></ns:Rec></ns:Doc>";
		String flatFile = "9110110999    16617425 0003100032100730100730314685                                                                             "+"\n"+
				"9120200000000019004533410072600000000521221000000000000001056182978      60                                                     "+"\n"+
				"9120200000000019004533510072600000002094401000000000000001056181978      60                                                     "+	"\n"+	
				"9120200000000019004533310072600000011665571000000000000001056180978      60                                                     "+"\n"+
				"919010000001000000300000012000000142811900000000000000000001428119                                                              ";
		String flatFileD = "911,01,00999,1,7425,00031,000321,007301,007303,1" +"\n"+
"912,02,00000000,01,90045334,100726,0000000052122,1,0000000000000,01056182,978,,,"+"\n"+
"912,02,00000000,01,90045335,100726,0000000209440,1,0000000000000,01056181,978,,,"+"\n"+
"912,02,00000000,01,90045333,100726,0000001166557,1,0000000000000,01056180,978,,,"+"\n"+
"919,01,0000001,0000003,0000001,2,0000001428119,0000000000000,0000001428119,";


		
		String inputMessageF = "H,1"+ "\n"+ "I,2"+ "\n" + "T,3"+ "\n"+ "T,9"+ "\n"+ "T,4";
		
		String inputMessageFF = "H  4"+ "\n"+ "I08"+ "\n"+ "T9"+ "\n"+ "T6";
	
		String xmlFile = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
				"<ns:VDA490700>xmlns:ns=\"http://sap.com/converter/test\">" +
				
				 "<S911><SATZART>911</SATZART><VNR>01</VNR> <KUNDNR>10999</KUNDNR><LIEFNR>1</LIEFNR><UEBNRALT>7425 </UEBNRALT><UEBNRNEU>00031</UEBNRNEU><UEBDAT>000321</UEBDAT><ZAHLDAT>007301</ZAHLDAT><ZAHLANTNR>007303</ZAHLANTNR><LEER>1</LEER></S911><S912><SATZART>912</SATZART><VNR>02</VNR><LIEFSCHNR>00000000</LIEFSCHNR><BELEGART>01</BELEGART><RECHNR>90045334</RECHNR><RECHNRDAT>100726</RECHNRDAT><RECHNBTRG>0000000052122</RECHNBTRG><VORZ>1</VORZ><SKTOBTRG>0000000000000</SKTOBTRG><RECHNABLNR>01056182</RECHNABLNR><WAEHREINH>978</WAEHREINH> <WERKKUNDE>   </WERKKUNDE></S912><S912><SATZART>912</SATZART><VNR>02</VNR><LIEFSCHNR>00000000</LIEFSCHNR><BELEGART>01</BELEGART><RECHNR>90045335</RECHNR><RECHNRDAT>100726</RECHNRDAT><RECHNBTRG>0000000209440</RECHNBTRG><VORZ>1</VORZ><SKTOBTRG>0000000000000</SKTOBTRG><RECHNABLNR>01056181</RECHNABLNR><WAEHREINH>978</WAEHREINH><ZAHLANZ> </ZAHLANZ><WERKKUNDE>   </WERKKUNDE><LEER> </LEER></S912><S912><SATZART>912</SATZART><VNR>02</VNR><LIEFSCHNR>00000000</LIEFSCHNR><BELEGART>01</BELEGART><RECHNR>90045333</RECHNR><RECHNRDAT>100726</RECHNRDAT><RECHNBTRG>0000001166557</RECHNBTRG><VORZ>1</VORZ><SKTOBTRG>0000000000000</SKTOBTRG><RECHNABLNR>01056180</RECHNABLNR><WAEHREINH>978</WAEHREINH><ZAHLANZ> </ZAHLANZ><WERKKUNDE>   </WERKKUNDE><LEER> </LEER></S912><S919><SATZART>919</SATZART><VNR>01</VNR><ZAEHL911>0000001</ZAEHL911><ZAEHL912>0000003</ZAEHL912><ZAEHL919>0000001</ZAEHL919><ZAHLART>2</ZAHLART><SUMZAHLBTRG>0000001428119</SUMZAHLBTRG><SKTOENDWERT>0000000000000</SKTOENDWERT><SUMRECHBTRG>0000001428119</SUMRECHBTRG><LEER></LEER></S919></ns:VDA490700>";
		String strSchema23 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
				"   <xs:schema attributeFormDefault=\"unqualified\" elementFormDefault=\"qualified\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\">" +
				"         <!-- XML Schema Generated from XML Document on Mon Jun 03 2019 09:42:47 GMT+0200 (W. Europe Daylight Time) -->" +
				"         <!-- with XmlGrid.net Free Online Service http://xmlgrid.net -->" +
				"         <xs:element name=\"VDA490700\">" +
				"               <xs:complexType>" +
				"                     <xs:sequence>" +
				"                           <xs:element name=\"S911\">" +
				"                                 <xs:complexType>" +
				"                                       <xs:sequence>" +
				"                                             <xs:element  name=\"SATZART\">" +
				"											  <xs:simpleType>" +
				"                           <xs:restriction base=\"xs:string\">" +
				"                              <xs:length value=\"3\" />" +
				"                           </xs:restriction>" +
				"                        </xs:simpleType>" +
				"											 </xs:element>" +
				"                                             <xs:element  name=\"VNR\">" +
				"											  <xs:simpleType>" +
				"                           <xs:restriction base=\"xs:string\">" +
				"                              <xs:length value=\"2\" />" +
				"                           </xs:restriction>" +
				"                        </xs:simpleType>" +
				"											 </xs:element>" +
				"                                             <xs:element  name=\"KUNDNR\">" +
				"											  <xs:simpleType>" +
				"                           <xs:restriction base=\"xs:string\">" +
				"                              <xs:length value=\"5\" />" +
				"                           </xs:restriction>" +
				"                        </xs:simpleType>" +
				"											 </xs:element>" +
				"                                             <xs:element  name=\"LIEFNR\">" +
				"											  <xs:simpleType>" +
				"                           <xs:restriction base=\"xs:string\">" +
				"                              <xs:length value=\"8\" />" +
				"                           </xs:restriction>" +
				"                        </xs:simpleType>" +
				"											 </xs:element>" +
				"                                             <xs:element  name=\"UEBNRALT\">" +
				"											  <xs:simpleType>" +
				"                           <xs:restriction base=\"xs:string\">" +
				"                              <xs:length value=\"5\" />" +
				"                           </xs:restriction>" +
				"                        </xs:simpleType>" +
				"											 </xs:element>" +
				"                                             <xs:element  name=\"UEBNRNEU\">" +
				"											  <xs:simpleType>" +
				"                           <xs:restriction base=\"xs:string\">" +
				"                              <xs:length value=\"5\" />" +
				"                           </xs:restriction>" +
				"                        </xs:simpleType>" +
				"											 </xs:element>" +
				"                                             <xs:element  name=\"UEBDAT\">" +
				"											  <xs:simpleType>" +
				"                           <xs:restriction base=\"xs:string\">" +
				"                              <xs:length value=\"6\" />" +
				"                           </xs:restriction>" +
				"                        </xs:simpleType>" +
				"											 </xs:element>" +
				"                                             <xs:element  name=\"ZAHLDAT\">" +
				"											  <xs:simpleType>" +
				"                           <xs:restriction base=\"xs:string\">" +
				"                              <xs:length value=\"6\" />" +
				"                           </xs:restriction>" +
				"                        </xs:simpleType>" +
				"											 </xs:element>" +
				"                                             <xs:element  name=\"ZAHLANTNR\">" +
				"											  <xs:simpleType>" +
				"                           <xs:restriction base=\"xs:string\">" +
				"                              <xs:length value=\"6\" />" +
				"                           </xs:restriction>" +
				"                        </xs:simpleType>" +
				"											 </xs:element>" +
				"                                             <xs:element name=\"LEER\">" +
				"											  <xs:simpleType>" +
				"                           <xs:restriction base=\"xs:string\">" +
				"                              <xs:length value=\"1\" />" +
				"                           </xs:restriction>" +
				"                        </xs:simpleType>" +
				"											 </xs:element>" +
				"                                       </xs:sequence>" +
				"                                 </xs:complexType>" +
				"                           </xs:element>" +
				"                           <xs:element name=\"S912\" maxOccurs=\"unbounded\">" +
				"                                 <xs:complexType>" +
				"                                       <xs:sequence>" +
				"                                             <xs:element  name=\"SATZART\">" +
				"											  <xs:simpleType>" +
				"                           <xs:restriction base=\"xs:string\">" +
				"                              <xs:length value=\"3\" />" +
				"                           </xs:restriction>" +
				"                        </xs:simpleType>" +
				"											 </xs:element>" +
				"                                             <xs:element  name=\"VNR\">" +
				"											  <xs:simpleType>" +
				"                           <xs:restriction base=\"xs:string\">" +
				"                              <xs:length value=\"2\" />" +
				"                           </xs:restriction>" +
				"                        </xs:simpleType>" +
				"											 </xs:element>" +
				"                                             <xs:element  name=\"LIEFSCHNR\">" +
				"											  <xs:simpleType>" +
				"                           <xs:restriction base=\"xs:string\">" +
				"                              <xs:length value=\"8\" />" +
				"                           </xs:restriction>" +
				"                        </xs:simpleType>" +
				"											 </xs:element>" +
				"                                             <xs:element  name=\"BELEGART\">" +
				"											  <xs:simpleType>" +
				"                           <xs:restriction base=\"xs:string\">" +
				"                              <xs:length value=\"2\" />" +
				"                           </xs:restriction>" +
				"                        </xs:simpleType>" +
				"											 </xs:element>" +
				"                                             <xs:element  name=\"RECHNR\">" +
				"											  <xs:simpleType>" +
				"                           <xs:restriction base=\"xs:string\">" +
				"                              <xs:length value=\"8\" />" +
				"                           </xs:restriction>" +
				"                        </xs:simpleType>" +
				"											 </xs:element>" +
				"                                             <xs:element  name=\"RECHNRDAT\">" +
				"											  <xs:simpleType>" +
				"                           <xs:restriction base=\"xs:string\">" +
				"                              <xs:length value=\"6\" />" +
				"                           </xs:restriction>" +
				"                        </xs:simpleType>" +
				"											 </xs:element>" +
				"                                             <xs:element  name=\"RECHNBTRG\"><xs:simpleType>" +
				"                           <xs:restriction base=\"xs:string\">" +
				"                              <xs:length value=\"13\" />" +
				"                           </xs:restriction>" +
				"                        </xs:simpleType></xs:element>" +
				"                                             <xs:element  name=\"VORZ\">" +
				"											 <xs:simpleType>" +
				"                           <xs:restriction base=\"xs:string\">" +
				"                              <xs:length value=\"1\" />" +
				"                           </xs:restriction>" +
				"                        </xs:simpleType>" +
				"											 </xs:element>" +
				"                                             <xs:element  name=\"SKTOBTRG\">" +
				"											 			 <xs:simpleType>" +
				"                           <xs:restriction base=\"xs:string\">" +
				"                              <xs:length value=\"13\" />" +
				"                           </xs:restriction>" +
				"                        </xs:simpleType>" +
				"											 </xs:element>" +
				"                                             <xs:element  name=\"RECHNABLNR\">" +
				"											 			 <xs:simpleType>" +
				"                           <xs:restriction base=\"xs:string\">" +
				"                              <xs:length value=\"8\" />" +
				"                           </xs:restriction>" +
				"                        </xs:simpleType>" +
				"											 </xs:element>" +
				"                                             <xs:element  name=\"WAEHREINH\">" +
				"											 			 <xs:simpleType>" +
				"                           <xs:restriction base=\"xs:string\">" +
				"                              <xs:length value=\"3\" />" +
				"                           </xs:restriction>" +
				"                        </xs:simpleType>" +
				"											 </xs:element>" +
				"                                             <xs:element name=\"ZAHLANZ\">" +
				"											 			 <xs:simpleType>" +
				"                           <xs:restriction base=\"xs:string\">" +
				"                              <xs:length value=\"1\" />" +
				"                           </xs:restriction>" +
				"                        </xs:simpleType>" +
				"											 </xs:element>" +
				"                                             <xs:element  name=\"WERKKUNDE\">" +
				"											 			 <xs:simpleType>" +
				"                           <xs:restriction base=\"xs:string\">" +
				"                              <xs:length value=\"3\" />" +
				"                           </xs:restriction>" +
				"                        </xs:simpleType>" +
				"											 </xs:element>" +
				"                                             <xs:element name=\"LEER\">" +
				"											 			 <xs:simpleType>" +
				"                           <xs:restriction base=\"xs:string\">" +
				"                              <xs:length value=\"1\" />" +
				"                           </xs:restriction>" +
				"                        </xs:simpleType>" +
				"											 </xs:element>" +
				"                                       </xs:sequence>" +
				"                                 </xs:complexType>" +
				"                           </xs:element>" +
				"                           <xs:element name=\"S919\">" +
				"                                 <xs:complexType>" +
				"                                       <xs:sequence>" +
				"                                             <xs:element  name=\"SATZART\">" +
				"											 			 <xs:simpleType>" +
				"                           <xs:restriction base=\"xs:string\">" +
				"                              <xs:length value=\"3\" />" +
				"                           </xs:restriction>" +
				"                        </xs:simpleType>" +
				"											 </xs:element>" +
				"                                             <xs:element  name=\"VNR\">" +
				"											 			 <xs:simpleType>" +
				"                           <xs:restriction base=\"xs:string\">" +
				"                              <xs:length value=\"2\" />" +
				"                           </xs:restriction>" +
				"                        </xs:simpleType>" +
				"											 </xs:element>" +
				"                                             <xs:element  name=\"ZAEHL911\">			 <xs:simpleType>" +
				"                           <xs:restriction base=\"xs:string\">" +
				"                              <xs:length value=\"7\" />" +
				"                           </xs:restriction>" +
				"                        </xs:simpleType></xs:element>" +
				"                                             <xs:element  name=\"ZAEHL912\">" +
				"											 			 <xs:simpleType>" +
				"                           <xs:restriction base=\"xs:string\">" +
				"                              <xs:length value=\"7\" />" +
				"                           </xs:restriction>" +
				"                        </xs:simpleType>" +
				"											 </xs:element>" +
				"                                             <xs:element  name=\"ZAEHL919\">" +
				"											 			 <xs:simpleType>" +
				"                           <xs:restriction base=\"xs:string\">" +
				"                              <xs:length value=\"7\" />" +
				"                           </xs:restriction>" +
				"                        </xs:simpleType>" +
				"											 </xs:element>" +
				"                                             <xs:element  name=\"ZAHLART\">" +
				"											 			 <xs:simpleType>" +
				"                           <xs:restriction base=\"xs:string\">" +
				"                              <xs:length value=\"1\" />" +
				"                           </xs:restriction>" +
				"                        </xs:simpleType>" +
				"											 </xs:element>" +
				"                                             <xs:element  name=\"SUMZAHLBTRG\">" +
				"											 <xs:simpleType>" +
				"                           <xs:restriction base=\"xs:string\">" +
				"                              <xs:length value=\"13\" />" +
				"                           </xs:restriction>" +
				"                        </xs:simpleType>" +
				"											 </xs:element>" +
				"                                             <xs:element  name=\"SKTOENDWERT\">" +
				"											 <xs:simpleType>" +
				"                           <xs:restriction base=\"xs:string\">" +
				"                              <xs:length value=\"13\" />" +
				"                           </xs:restriction>" +
				"                        </xs:simpleType>" +
				"											 </xs:element>" +
				"                                             <xs:element  name=\"SUMRECHBTRG\">" +
				"											 <xs:simpleType>" +
				"                           <xs:restriction base=\"xs:string\">" +
				"                              <xs:length value=\"13\" />" +
				"                           </xs:restriction>" +
				"                        </xs:simpleType>" +
				"											 </xs:element>" +
				"                                             <xs:element name=\"LEER\">" +
				"											 <xs:simpleType>" +
			"<xs:restriction base=\"xs:decimal\">" +
						"                                                    <xs:totalDigits value=\"1\"/>" +
						"													 <xs:fractionDigits value=\"0\"/>" +	
						"                                                </xs:restriction>" +
				"                        </xs:simpleType>" +
				"											 </xs:element>" +
				"                                       </xs:sequence>" +
				"                                 </xs:complexType>" +
				"                           </xs:element>" +
				"                     </xs:sequence>" +
				"                     " +
				"               </xs:complexType>" +
				"         </xs:element>" +
				"   </xs:schema>" +
				" ";
		
		
	//	String strF2 = fileConversionOutput(strSchema, "/Doc/Rec", "XMLToPlain", inputMessage,"","");
		
	//	String strF2 = fileConversionOutput(strSchema, "/Doc/Rec", "PlainToXML", inputMessageFF,"","");
		
		String strF2 = fileConversionOutput(strSchema23, "/VDA490700", "PlainToXML", flatFile, "S","");
		
		// CSV
	//	String strF2 = fileConversionOutput(strSchema23, "/VDA490700", "XMLToPlain", xmlFile, "S","");
	//	String strF2 = fileConversionOutput(strSchema23, "/VDA490700", "PlainToXML", flatFileD, "S","");
		System.out.println(strF2);
		 
		
	//	System.out.println(FileConverter.validateAgainstXSD(strF, strSchema));
		
		
	}

}
