package com.home.callcab.parser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.content.res.Resources;
import android.util.Log;

import com.home.callcab.CallCabApplication;
import com.home.callcab.info.CabVendorInfo;
import com.home.callcab.info.CityInfo;

public class XMLParser {

	public static List<CabVendorInfo> GetCabList(String XmlFileName)
			throws IOException {
		CabVendorInfo vendorInfo;
		Element e;

		final String KEY_VENDOR = "vendor";
		final String KEY_NAME = "name";
		final String KEY_NUMBER = "number";
		final String KEY_ID = "id";
		final String KEY_LOGO = "logo";
		final String KEY_URL = "url";
		final String KEY_USP = "usp";
		final String KEY_IS_OUTSTATION = "outstation";
		final String KEY_IS_LOCAL = "local";
		final String KEY_IS_AIRPORT = "airport";
		final String KEY_PRENUM = "prenum";
		final String KEY_POSTNUM = "postnum";
		

		List<CabVendorInfo> vendorList = new ArrayList<CabVendorInfo>();

		String xml = LoadFile(XmlFileName, false);
		Document doc = getDomElement(xml); // getting DOM element
		NodeList nl = doc.getElementsByTagName(KEY_VENDOR);
		// looping through all item nodes <item>
		for (int i = 0; i < nl.getLength(); i++) {
			vendorInfo = new CabVendorInfo();
			e = (Element) nl.item(i);

			vendorInfo.VendorName = getValue(e, KEY_NAME);
			vendorInfo.PhoneNumber = getValue(e, KEY_NUMBER);
			vendorInfo.Id = getValue(e, KEY_ID);
			vendorInfo.Logo = getValue(e, KEY_LOGO);
			vendorInfo.Url = getValue(e, KEY_URL);
			vendorInfo.Usp = getValue(e, KEY_USP);
			vendorInfo.isAirport = Boolean.parseBoolean(getValue(e, KEY_IS_AIRPORT));
			vendorInfo.isLocal = Boolean.parseBoolean(getValue(e, KEY_IS_LOCAL));
			vendorInfo.isOutstation = Boolean.parseBoolean(getValue(e, KEY_IS_OUTSTATION));
			vendorInfo.PreNum = getValue(e,KEY_PRENUM);
			vendorInfo.PostNum = getValue(e,KEY_POSTNUM);
			
			vendorList.add(vendorInfo);
		}

		return vendorList;
	}

	public static List<CityInfo> GetCityList() throws IOException {
		CityInfo cityInfo;
		Element e;

		final String KEY_CITY = "city";
		final String KEY_CITY_NAME = "cityName";
		final String KEY_CITY_XML = "cityXML";
		final String KEY_CITY_BACKGROUND = "cityBackground";
		
		List<CityInfo> cityList = new ArrayList<CityInfo>();

		String xml = LoadFile("CityList.xml", false);
		Document doc = getDomElement(xml); // getting DOM element
		NodeList nl = doc.getElementsByTagName(KEY_CITY);
		// looping through all item nodes <item>
		for (int i = 0; i < nl.getLength(); i++) {
			cityInfo = new CityInfo();
			e = (Element) nl.item(i);

			cityInfo.CityName = getValue(e, KEY_CITY_NAME);
			cityInfo.CityXMl = getValue(e, KEY_CITY_XML);
			cityInfo.CityBackground = getValue(e, KEY_CITY_BACKGROUND);
			cityList.add(cityInfo);
		}

		return cityList;
	}

	private static String getValue(Element item, String str) {
		NodeList n = item.getElementsByTagName(str);
		return getElementValue(n.item(0));
	}

	private static final String getElementValue(Node elem) {
		Node child;
		if (elem != null) {
			if (elem.hasChildNodes()) {
				for (child = elem.getFirstChild(); child != null; child = child
						.getNextSibling()) {
					if (child.getNodeType() == Node.TEXT_NODE) {
						return child.getNodeValue();
					}
				}
			}
		}
		return "";
	}

	private static Document getDomElement(String xml) {
		Document doc = null;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {

			DocumentBuilder db = dbf.newDocumentBuilder();

			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(xml));
			doc = db.parse(is);

		} catch (ParserConfigurationException e) {
			Log.e("Error: ", e.getMessage());
			return null;
		} catch (SAXException e) {
			Log.e("Error: ", e.getMessage());
			return null;
		} catch (IOException e) {
			Log.e("Error: ", e.getMessage());
			return null;
		}
		// return DOM
		return doc;
	}

	private static String LoadFile(String fileName, boolean loadFromRawFolder)
			throws IOException {
		//Log.d("MK:LoadFile", "start");
		// Create a InputStream to read the file into
		InputStream iS;
		Resources resources = CallCabApplication.getCallCabAppContext()
				.getResources();

		if (loadFromRawFolder) {
			// get the resource id from the file name
			int rID = resources.getIdentifier("com.home.callcab:raw/"
					+ fileName, null, null);
			// get the file as a stream
			iS = resources.openRawResource(rID);
		} else {
			// get the file as a stream
			iS = resources.getAssets().open(fileName);
		}

		//Log.d("MK:LoadFile", "start buffer");
		// create a buffer that has the same size as the InputStream
		byte[] buffer = new byte[iS.available()];
		// read the text file as a stream, into the buffer
		iS.read(buffer);

		//Log.d("MK:LoadFile", "end buffer");
		// create a output stream to write the buffer into
		ByteArrayOutputStream oS = new ByteArrayOutputStream();
		// write this buffer to the output stream
		oS.write(buffer);
		// Close the Input and Output streams
		oS.close();
		iS.close();
		//Log.d("MK:LoadFile", "end");
		// return the output stream as a String
		return oS.toString();
	}
	
}
