Sample Mule Application to Upload Data to Salesforce Analytics
Upload is demonstrated in two ways :

1. Creating, Uploading  One Data part for the entire Data Set and Trigger Processing.

2. Creating DataSet, Uploading multiple Data Part in Batches, Trigger Processing

Reference :  

https://docs.mulesoft.com/connectors/salesforce-analytics-connector-reference

External Data API - https://developer.salesforce.com/docs/atlas.en-us.bi_dev_guide_ext_data.meta/bi_dev_guide_ext_data/
Analytics External Data Format Reference (For Building Metadata JSON) : https://resources.docs.salesforce.com/214/latest/en-us/sfdc/pdf/bi_dev_guide_ext_data_format.pdf

Analytics Glossary : https://help.salesforce.com/articleView?id=bi_glossary.htm&type=5



External Data operations on Salesforce Analytics Using Mulesoft Salesforce Analytics Connector Part 1

In this series in will be explaining how to play with Salesforce Analytics Connector in order to Create DataSet and Upsert,Delete,Overwrite Records in the Salesforce Analytics DataSet

The External Data API enables you to upload external data files to Analytics. While the External Data API can upload .csv files, the Mulesoft connector is designed just like any other Anypoint Connector in which we will be supplying the data in the generic format List<Map>, The Connector internally will take care of the necessary transformation to CSV, thus making our life easy. With the Connector the Metadata JSON is mandatory, since it uses the MetaData JSON for Validation.

DataSet : A Dataset contains a set of source data, specially formatted and optimized for interactive exploration. All DataSet's will have a unique name in the Salesforce organization.

The InsightsExternalData Object : The InsightsExternalData object is used with the InsightsExternalDataPart object, which holds the parts of the data to be uploaded. Together, they provide a programmatic way to upload a large file in parts and trigger a dataflow into a dataset.

The InsightsExternalDataPart Object : The InsightsExternalDataPart object works with the InsightsExternalData object. After you insert a row into the InsightsExternalData object, you can create part objects to split up your data into parts. If your initial data file is larger than 10 MB, split your file into parts that are smaller than 10 MB

MetaData JSON File : A metadata file is a JSON file that describes the structure of an external data. We can define the schema of the data with it. We can assign different data types like Text,Date etc to the differnt fields depending on the type of Data. Depending on the datatype's we need to provide the additional attibutes. For example : For a Date we need to provide the Format,firstDayOfWeek,fiscalMonthOffset,isYearEndFiscalYear etc attributes. For operations like Upsert, Delete on the Dataset we have to specify a Primary Key which needs to be defined in the metadata file. Please refer : Analytics External Data Format Reference (For Building Metadata JSON) : https://resources.docs.salesforce.com/214/latest/en-us/sfdc/pdf/bi_dev_guide_ext_data_format.pdf
A MetaData JSON is required for almost all operations in the Connector and is required to be configured in the Connector Configuration itself.

Edgemart Container : Edgemart Container is the APP (for e.g. SharedApp) which owns the dataset. When using the Connector for creating or updating the dataset we need to provide the Salesforce ID or the Developer Name of the App. Make sure you have the necessary permission to the APP before Creating/Updating the dataset otherwise the operation would fail because of Insufficient privilages. 
If you are not sure what your APP is you can view all the APP's using this SOQL Query, which you can run in Developer Console.
SELECT Id,DeveloperName,Name, AccessType,CreatedDate,Type FROM Folder where Type = 'Insights'

DataSet ID : Salesforce ID of the Dataset to be operated on. A Create Data Set operation using the connector will return the Data Set ID as payload which can be used later in subsequent operations.

DataSet Operations available in Connector:

1. Create Data Set : Creates a new InsightsExternalData record into Salesforce Analytics Cloud system and returns the identifier (Salesforce ID of the created record) of created entity within the Salesforce Analytics Cloud system. This opeartion will just create the Dataset skeleton in Analytics, and will also upload the Metadata JSON which acts as "Schema/Structure" for the Dataset. Keep in mind that a InsightsExternalData record doesn't represent the Data Set itself, even though InsightsExternalData record is essential for a DataSet creation and is required to channel data into the Dataset.

Type : Provide the path of the MetaData JSON File. All the records Data Parts uploaded later should be aligned properly to the MetaData JSON. 

Operation : Even though the connector operation is named as "Create DataSet" we can actually perform APPEND, OVERWRITE, UPSERT, DELETE by selecting the appropriate operation

APPEND : Append all data to the dataset. Creates a dataset if it doesn’t exist. If the dataset or rows contain a unique identifier, the append operation is not allowed.
OVERWRITE : Create a dataset with the given data, and replace the dataset if it exists.
UPSERT : Insert or update rows in the dataset. Creates a dataset if it doesn’t exist. The rows to upsert must contain one (and only one) field with a unique identifier. We have to set one of the field with "isUniqueId" attribute set to true in the Metadata JSON.
DELETE : Delete the rows from the dataset. The rows to delete must contain one (and only one) field with a unique identifier.
DELETE can be used only over a existing DataSet
APPEND,UPSERT,OVERWRITE can be used to create a new DataSet and also modify it later.

Edgemart Container - We need to provide the Salesforce ID or the Developer Name of the App in which the DataSet needs to be created. 
a. If this parameter is omitted when you’re creating a dataset, the name of the user’s private app is used.
b. If this parameter is omitted for an existing dataset, the system resolves the app name.
c. If this parameter is specified for an existing dataset, the name is required to match the name of the current app that contains the dataset.


2. Upload External Data : Inserts records into a data set at an ID obtained from the Salesforce Analytics Cloud system. We will have to pass the Dataset Salesforce ID which we obtained from "Create Data Set" operation. We will be passing the chunked content in this operation itself. We have to make sure that the data getting passed through this operation aligns to the MetaData JSON.  This operation will create a Data Part (InsightsExternalDataPart) in the Salesforce Analytics Cloud system using the content passed. InsightsExternalDataPart record created is a child of InsightsExternalData record which got created in "Create Data Set" operation. We will also need to configure the path of the MetaData JSON File using the "Type" dropdown

3. Start Data Processing: This operation tells the Salesforce Analytics Cloud system to start processing the Data parts uploaded so far into a data set. We have to provide the data set Salesforce Id obtained from "Create Data Set" operation. After this operation is executed from the Connector. A "JOB" will be created in the Analytics Cloud System. This Job will take care of actual channeling of records into the DataSet. 

Additonal Info : The status of the Job can be monitored using the Data Monitor under Jobs tab (Analytics Studio -> Settings Button -> Data Monitor). 
We can Monitor the Job if it is  Success/Failure/Completed with Errors etc using the Data Monitor, also we will be able to see metrics like number of records success or failed.

4. Upload External Data into new Dataset and Start Processing: This will Create a data set, upload data into it, and tell the Salesforce Analytics Cloud system to start processing the uploaded data. It is the Combination of all the operations above and can be used if we are planning to send data in One Part itself. 



We will be covering these scenarios in this series.

Scenario 1 : Creating a new DataSet/Adding to an existing DataSet and uploading records in the DataSet. (In Batches and in one go)
Scenario 2 : Overwriting the dataset with new set of Data
Scenario 3 : Creating a DataSet with a Primary Key and uploading records in the DataSet.
Scenario 4 : Updating (Upserting) the records in the Dataset.
Scenario 5 : Deleting records in the DataSet