Sample Mule Application to Send Error Payload details after making a call to External Service
The Error Payload details can be used to log an incident.

Testing: 
=========

1. Raise Exception: (This will be logged in Incident)
==================
Invoke a HTTP POST on http://localhost:9003/api/create with the below payload
<CreateRequest>
	<email>rahul@gmail.com</email>
	<phone>9879877654</phone>
	<stateName>TE</stateName>
</CreateRequest>

2. Don't Raise Exception (Normal Scenario)
===================
Invoke a HTTP POST on http://localhost:9003/api/create with the below payload
<CreateRequest>
	<name>Rahul</name>
	<email>rahul@gmail.com</email>
	<phone>9879877654</phone>
	<stateName>TE</stateName>
</CreateRequest>