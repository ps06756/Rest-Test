# Rest-Test
Rest-Test is a minimalistic framework for testing Rest API . 
It allows you you to write a test configuration file (Xml) and runs tests based on the configuration file. The framework is easy to use for simple testing. Currently, I have only implemented basic use cases, and plan to introduce sophesticated use cases soon.

### Building the project

Run the following command to compile and build the executable jar file.

``` mvn compile assembly:assembly```

This will download all the dependencies and package the compile source code along with dependencies in an executable jar file.

### Running the project

To run the project, use the following command  

`java -jar {/path/to/Rest-Test.jar} {/path/to/config_file.xml}`

This will start running the tests as specified in the config file. 

### Structure of the config file.

```
<?xml version="1.0" encoding="ISO-8859-1" ?>
<configuration>
	<tests>
		<login>
			<username>ps06756@gmail.com</username>
			<password>helloworld</password>
			<loginUrl>http://edutab.com/edutab/authentication/login</loginUrl>
		</login>
		<test>
			<name>Student ID TEST (Correct)</name>
			<type>GET</type>
			<url>/edutab/student/id</url>
			<host>edutab.com</host>
			<loginRequired>true</loginRequired>
			<key>email</key>
			<value>ps06756@gmail.com</value>
      			<key>studentId</key>
      			<value>1</value>
			<comment>Test which should get ID correctly</comment>
			<queryOrForm>true</queryOrForm>
			<dataType>String</dataType>
			<expectedOutput>1</expectedOutput>
			<expectedStatusCode>200</expectedStatusCode>
			<count>1</count>
		</test>
		<test>
			<name>Student ID TEST (Incorrect)</name>
			<type>GET</type>
			<url>/edutab/student/id</url>
			<host>edutab.com</host>
			<loginRequired>false</loginRequired>
			<key>email</key>
			<value>ps06756@gmail.com</value>
			<comment>Test which should not get ID correctly</comment>
			<queryOrForm>true</queryOrForm>
			<dataType>None</dataType>
			<expectedOutput>1</expectedOutput>
			<expectedStatusCode>401</expectedStatusCode>
			<count>1</count>
		</test>
		<test>
			<name>Student Info (correct Id, without login)</name>
			<type>GET</type>
			<url>/edutab/student/info</url>
			<host>edutab.com</host>
			<loginRequired>false</loginRequired>
			<key>studentId</key>
			<value>1</value>
			<comment>Test which should not get info correctly</comment>
			<queryOrForm>true</queryOrForm>
			<dataType>None</dataType>
			<expectedOutput>{}</expectedOutput>
			<expectedStatusCode>401</expectedStatusCode>
			<count>1</count>
		</test>
		<test>
			<name>Student Info (correct Id, with login)</name>
			<type>GET</type>
			<url>/edutab/student/info</url>
			<host>edutab.com</host>
			<loginRequired>true</loginRequired>
			<key>studentId</key>
			<value>1</value>
			<comment>Test which should get info correctly</comment>
			<queryOrForm>true</queryOrForm>
			<dataType>JSON</dataType>
			<expectedOutput>{"first_name" : "Pratik", "last_name":"Singhal","middle_name":null,"studentId":"1","notification":[]", courseId":"1","email":"ps06756@gmail.com"}</expectedOutput>
			<expectedStatusCode>200</expectedStatusCode>
			<count>1</count>
		</test>
	</tests>
</configuration>
```

The above configuration file mainly contains two things 
1) Login Credentials : Before starting the tests, `Rest-Test` logins to the url you specify in the config file using the credentials you provide. It is equivalent to running the following curl command 

``` curl -X POST -F "email=ps06756@gmail.com" -F "password=helloworld" "http://edutab.com/edutab/authentication/login" -c ~/cookie```

2) Tests : These are the actual tests which will be run. Some of these tests require you to be logged in to the system. This can be specified using the `loginRequired` tag. 
For example, running the first test is equivalent to running the following curl command. 

```curl -X GET "http://edutab.com/edutab/student/id?email=ps06756@gmail.com&studentId=1" -b ~/cookie```

The expected http status code of the above test is 200 and the expected content after executing the above test is 1. 


### Configuration File Parameters

1) name : Name of the test you are running.
2) type : Type of the test you are running ('GET', 'POST', 'PUT', etc)
3) url : Url endpoint at which the request should be made.
4) host : Hostname where the API is hosted.
5) key : Keys of the parameters which you want to send with request.
6) value : Values of the parameters which you want to send with request (corrosponding to the above mentioned keys)
7) comment : Notes related to the test.
8) queryOrForm : Whether the parameters supplied are query parameters or form parameters.
9) dataType : Data type of the expected output from the test ('String', 'JSON' or 'None')
10) expectedOutput : Expected Output from the test (in case data type is 'None' , you can write 2 here)
11) expectedStatusCode : Expected status code from the test.
12) count : No of key value pairs for this test.

Supplying all these parameters for each test is compulsory.




### Found an Issue ? 
If you find a bug in the source code or a mistake in the documentation, you can help us by submitting an issue to our GitHub Repository. Even better you can submit a Pull Request with a fix.

### Got a Question or Problem?
If you have questions related to `Rest-Test` or have any feature requests, you can write to ps06756@gmail.com. 

### Developed by
  
Pratik Singhal (@ps06756)  
(ps06756@gmail.com)

