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

import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;

import org.apache.commons.beanutils.ConversionException;
import org.json.JSONObject;




class Tests {

	public static ArrayList<String> failedTests = new ArrayList<String>();

	public static void main(String[] args) throws Exception {
			
		String filePath = null;
		if (args.length != 1) {
			System.out.println("Please supply exactly one command line arguement!");
			return;
		}
		else {
			filePath = args[0];
		}

		BasicCookieStore cookieStore = new BasicCookieStore();
		ArrayList<Test> testList = new ArrayList<Test>();
		Configurations configuration = new Configurations();

		try(CloseableHttpClient client = HttpClients.createDefault(); CloseableHttpClient loginClient = HttpClients.custom().setDefaultCookieStore(cookieStore).build()) {


			XMLConfiguration xmlConfiguration = configuration.xml(filePath);
			Static.globalLogin = xmlConfiguration.getBoolean("globalLogin");
			System.out.println("globalLogin = " + Static.globalLogin);
			List<String> testName = xmlConfiguration.getList(String.class, "tests.test.name");
			List<String> testTypes = xmlConfiguration.getList(String.class, "tests.test.type");
			List<String> testHost = xmlConfiguration.getList(String.class, "tests.test.host");
			List<String> testUrl = xmlConfiguration.getList(String.class, "tests.test.url");
			List<Boolean> testLoginRequired = xmlConfiguration.getList(Boolean.class, "tests.test.loginRequired");
			List<String> keys = xmlConfiguration.getList(String.class, "tests.test.key");
			List<String> values = xmlConfiguration.getList(String.class, "tests.test.value");
			List<String> comments = xmlConfiguration.getList(String.class, "tests.test.comment");
			List<Boolean> queryOrForms = xmlConfiguration.getList(Boolean.class, "tests.test.queryOrForm");
			List<String> dataTypes = xmlConfiguration.getList(String.class, "tests.test.dataType");
			List<String> expectedOutput = xmlConfiguration.getList(String.class, "tests.test.expectedOutput");
			List<Integer> expectedStatusCode = xmlConfiguration.getList(Integer.class, "tests.test.expectedStatusCode");
			List<Integer> paramsCount = xmlConfiguration.getList(Integer.class, "tests.test.count");

			String usernameKey = xmlConfiguration.getString("tests.login.usernameKey");
			String passwordKey = xmlConfiguration.getString("tests.login.passwordKey");
			String username = xmlConfiguration.getString("tests.login.username");
			String password = xmlConfiguration.getString("tests.login.password");
			String loginUrl = xmlConfiguration.getString("tests.login.loginUrl");

			if (Static.globalLogin) {
				int loginCode = Tests.makeLoginAttempt(loginClient, loginUrl, username, password, usernameKey, passwordKey);
				if (loginCode != 200) {
					System.out.println("Login failed! status_code = " + loginCode + " ");
					return;
				}
			}

			for(int i = 0; i < testLoginRequired.size(); i++) {
				if (Static.globalLogin == false && testLoginRequired.get(i) == true) {
					System.out.println("Invalid configuration! globalLoginRequired is false, while loginRequired for some tests is true");	
					return;
				}
			}

			int cnt = 0;
			for(int i = 0; i < testName.size(); i++) {

				List<String> key = new ArrayList<String>(), value = new ArrayList<String>();
				for(int j = 0; j < paramsCount.get(i); j++) {
					key.add(keys.get(cnt));
					value.add(values.get(cnt));
					cnt++;
				}
				testList.add(new Test(testName.get(i), testTypes.get(i), testHost.get(i), testUrl.get(i), testLoginRequired.get(i), comments.get(i), queryOrForms.get(i),  key, value, dataTypes.get(i), expectedOutput.get(i), expectedStatusCode.get(i)));
			}

			for(int i = 0; i < testList.size(); i++) {
				boolean err = false;
				System.out.println(Color.ANSI_GREEN + Color.WHITE_BACKGROUND + " Running test " + (i + 1) + " " + testList.get(i).name + Color.ANSI_RESET);
				if (testList.get(i).loginRequired == false) {
					err = testList.get(i).runTest(client);
				}
				else {
					err = testList.get(i).runTest(loginClient);
				}

				if (err == false) {
					System.out.println(Color.ANSI_RED + Color.WHITE_BACKGROUND + "Test failed! " + Color.ANSI_RESET);
					return;
				}
				else {
					System.out.println(Color.ANSI_GREEN + Color.WHITE_BACKGROUND + "Test passed " + Color.ANSI_RESET);
				}
			}
		}
		catch( IOException | ConfigurationException | ConversionException  ex) {
			System.out.println(ex.getMessage());
		}
	}

	public static int makeLoginAttempt(CloseableHttpClient client , String url, String email, String password, String usernameKey, String passwordKey) throws UnsupportedEncodingException, IOException {

		HttpPost request = new HttpPost(url);
		HttpEntity entity = null;

		if (email != null && password != null)
			entity = MultipartEntityBuilder.create().addTextBody(usernameKey, email).addTextBody(passwordKey, password).build();

		if (entity != null)
			request.setEntity(entity);
		HttpResponse response = client.execute(request);
		int statusCode =  response.getStatusLine().getStatusCode();
		request.releaseConnection();
		return statusCode;
	}

}
