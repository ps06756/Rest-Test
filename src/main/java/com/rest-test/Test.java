package com.restTest;

import org.apache.http.*;
import org.apache.http.cookie.*;
import org.apache.http.impl.client.*;
import org.apache.http.client.methods.*;
import org.apache.http.client.entity.*;
import org.apache.http.message.*;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.client.utils.*;
 

import java.io.*;
import java.net.*;

import java.util.*;
import java.lang.StringBuffer;
import org.json.JSONObject;

class Test {
	public String host, url, type, comment, dataType, expectedOutput, name;
	public List<String> key, values;
	public boolean loginRequired, queryOrForm;
	public int expectedStatusCode;

	public Test(String name, String type, String host, String url, boolean loginRequired, String comment, boolean queryOrForm, List<String> key, List<String> values, String dataType, String expectedOutput, int expectedStatusCode) {

		this.name = name;
		this.type = type;
		this.url = url;
		this.host = host;
		this.loginRequired = loginRequired;
		this.key = key;
		this.values = values;
		this.comment = comment;
		this.queryOrForm = queryOrForm;
		this.dataType = dataType;
		this.expectedOutput = expectedOutput;
		this.expectedStatusCode = expectedStatusCode;
	}

	public boolean runTest(CloseableHttpClient client) {
		if (type.equals("GET") && queryOrForm == true) {
			return runHttpGetQueryTest(client);
		}
		//else if (type == "POST" && queryOrForm == true) {
		//	return runHttpPostQueryTest();
		//}
		else {
			System.out.println("Method not implemented!!");
			return false;
		}
	}

	public boolean runHttpGetQueryTest(CloseableHttpClient client) {

		try {
			URIBuilder builder = new URIBuilder();
			builder = builder.setScheme("http").setHost(host).setPath(url);

			for(int i = 0; i < key.size(); i++) {
				builder = builder.setParameter(key.get(i), values.get(i));
			}

			String url = builder.build().toString();
			HttpGet httpRequest = new HttpGet(url);

			HttpResponse response = client.execute(httpRequest);
			int statusCode = response.getStatusLine().getStatusCode();
			BufferedReader rd = new BufferedReader(new InputStreamReader (response.getEntity().getContent()));
			StringBuffer sb = new StringBuffer();
			String output = null;
			while ((output = rd.readLine()) != null) {
				sb.append(output);
			}
			output = sb.toString();

			// Test failed!!
			if (statusCode != this.expectedStatusCode) {
				System.out.println(Color.ANSI_RED + Color.WHITE_BACKGROUND + "statusCode = " + statusCode + ", Expected Status Code = " + this.expectedStatusCode + Color.ANSI_RESET);
				return false;
			}

			if (this.dataType.equals("String")) {
				if (!output.equals(new String(expectedOutput))) {
					System.out.println(Color.ANSI_RED + Color.WHITE_BACKGROUND + "Output = " + output + ", Expected Output =  " + expectedOutput + Color.ANSI_RESET);
					return false;
				}
				else {
					return true;
				}
			}
			else if (this.dataType.equals("None")) {
				return true;
			}
			else if (this.dataType.equals("JSON")) {
				JSONObject obj1 = new JSONObject(output), obj2 = new JSONObject(expectedOutput);
				if (obj1.similar(obj2))
					return true;
				else {
					System.out.println(Color.ANSI_RED + Color.WHITE_BACKGROUND + "JSON Output = " + obj1 + ", Expected Output = " + obj2 + Color.ANSI_RESET);
					return false;
				}
			}
			else {
				System.out.println("Not implemented!");
				return false;
			}
		}
		catch( URISyntaxException | IOException ex) {
			return false;
		}
	}
}
