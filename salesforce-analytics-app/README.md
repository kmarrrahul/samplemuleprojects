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
Scenario 2 : Creating a DataSet with a Primary Key and uploading records in the DataSet.
Scenario 3 : Overwriting the dataset with new set of Data.
Scenario 4 : Updating (Upserting) the records in the Dataset.
Scenario 5 : Deleting records in the DataSet

Scenario 1: Creating a new DataSet/Adding to an existing DataSet and uploading records in the DataSet.

This is a sample MetaData File which I have created for our example: 

Uploading Record In Batches :

Parameters for "Create Data Set" operation - For this operation we will need the Edgemart Container - Salesforce ID/DeveloperName of the APP in which we want to create the DataSet i have used "SharedApp", MetaData File. The APPEND sub-Operation is used here.
When creating a new dataset then the name of the dataset provided in "Create Data Set" operation should be unique accross the organization.
When appending on a existing dataset then the same name needs to be used. if the same name is not provided then a different Dataset would be created.
Parameters for "Create Data Set" operation - The Salesforce ID resulting from "Create Data Set" operation
Parameters for "start-data-processing" operation - The Salesforce ID resulting from "Create Data Set" operation


Below is the snippet of the flow.

    <sub-flow name="salesforce-analytics-batchappend-Sub_Flow">
        <set-variable variableName="dataSetContainerName" value="${dataSetContainerName}" doc:name="Variable : DataSetContainerName" doc:description="DataSet Container Name - Salesforce ID or Developer Name of the App in which Dataset is to be created"/>
        
        <enricher source="#[payload]" target="#[flowVars.datasetname]" doc:name="Message Enricher" doc:description="Get the Salesforce ID of the Dataset Created in a variable.">
            <sfdc-analytics:create-data-set config-ref="Salesforce_Analytics_Cloud__Basic_authentication" operation="APPEND" description="Sample data Set" label="Data Set 2" dataSetName="demodataset2" edgemartContainer="#[flowVars.dataSetContainerName]" type="metadata\sampledataforWave.json" doc:name="Salesforce Analytics Cloud : Create DataSet"/>
        </enricher>
        <dw:transform-message doc:name="Create Sample Data for DataSet">
            <dw:set-payload><![CDATA[%dw 1.0
%output application/java
%var sampleSize = 10000
---

(1 to sampleSize) map {
	"Field1" : "Field1Value" ++ "$",
	"Field2" : "Field2Value" ++ "$",
	"Field3" : now as :date,
	"Field4" : "Field4Value" ++ "$",
	"Field5" : "Field5Value" ++ "$"
}]]></dw:set-payload>
        </dw:transform-message>
        <batch:execute name="salesforce-analytics-appBatch" doc:name="Batch Execute"/>
    </sub-flow>
	
    <batch:job name="salesforce-analytics-appBatch">
        <batch:process-records>
            <batch:step name="Batch_Step">
                <batch:commit size="1000" doc:name="Batch Commit">
                    <sfdc-analytics:upload-external-data config-ref="Salesforce_Analytics_Cloud__Basic_authentication" type="metadata\sampledataforWave.json" dataSetId="#[flowVars.datasetname]" doc:name="Salesforce Analytics Cloud : Upload Data Part">
                        <sfdc-analytics:payload ref="#[payload]"/>
                    </sfdc-analytics:upload-external-data>
                </batch:commit>
            </batch:step>
        </batch:process-records>
        <batch:on-complete>
            <sfdc-analytics:start-data-processing config-ref="Salesforce_Analytics_Cloud__Basic_authentication" dataSetId="#[flowVars.datasetname]" doc:name="Salesforce Analytics Cloud : Trigger Data Processing" doc:description="Trigger the processing of data which was uploaded in Parts till now.
On the Data processing is triggered the status can be monitored in Data Manager"/>
        </batch:on-complete>
    </batch:job>	


The Edgemart Conatiner which is have used "SharedAPP". The SharedApp's Developer name is configured in a System Property, which is collected in a variable. After that Create Data Set operation is invoked inside a Message Enricher in order to collect the Salesforce ID of the InsightsExternalData object in a variable which we will need in later opertions for uploading data and data processing. I have used a Transform Message component to generate some random data, but in actual use cases we would be passing/Transforming the data from some other source(s). Make sure that the actual data being passed is aligned prperly to the MetaData JSON. For example : Notice that for the  Field of type Date in Metadata JSON I have passed a dataweave :date Object (java.util.Date), similarly For Text types i have passed a String.

The transformed data is passed on to the Batch Job which has only one Batch Step with Batch Commit which contains the Salesforce connector with "Upload External Data" operation. So In mutiple batches various Data Parts is created associated to the same parent record. The size of the data part is controlled by the Batch Commit Size. Though it is configured to 1000 here which can be customized to some other value as per requirement, keeping in mind that the maximum size of the InsightsExternalDataPart which is 10 MB. After batch job is complete, the data processing is triggered in the on-complete phase. 


Uploading Records In one Go : It will create just One Data Part  on InsightsExternalData record. "Upload External Data into new Dataset and Start Processing" is used for this. Configuration of parameter's Edgemart Container, Operation, Type is same as the when uploading in batches. Here just the Payload is prepared and the Connector is invoked, the rest will be taken care by the Connector. 

When creating a new dataset then the name of the dataset provided in "Upload External Data into new Dataset and Start Processing" operation should be unique accross the organization.
When appending on a existing dataset then the same name needs to be used. If the same name is not provided then a different Dataset would be created.

Below is the snippet of the flow.

    <sub-flow name="salesforce-analytics-append-dataset-Sub_Flow">
        <set-variable variableName="dataSetContainerName" value="${dataSetContainerName}" doc:name="Variable : DataSetContainerName" doc:description="DataSet Container Name - Salesforce ID or Developer Name of the App in which Dataset is to be created"/>       
        <dw:transform-message doc:name="Create Sample Data for DataSet">
            <dw:set-payload><![CDATA[%dw 1.0
%output application/java
%var sampleSize = 1000
---

(1 to sampleSize) map {
	"Field1" : "Field1Value" ++ "$",
	"Field2" : "Field2Value" ++ "$",
	"Field3" : now as :date,
	"Field4" : "Field4Value" ++ "$",
	"Field5" : "Field5Value" ++ "$"
}]]></dw:set-payload>
        </dw:transform-message>
        
        <sfdc-analytics:upload-external-data-into-new-data-set-and-start-processing config-ref="Salesforce_Analytics_Cloud__Basic_authentication" type="metadata\sampledataforWave.json" operation="APPEND" description="Sample Data Set 1" label="Data Set 1" dataSetName="demodataset1" edgemartContainer="#[flowVars.dataSetContainerName]" doc:name="Salesforce Analytics Cloud : Create,Upload and Start Processing">
            <sfdc-analytics:payload ref="#[payload]"/>
        </sfdc-analytics:upload-external-data-into-new-data-set-and-start-processing>  
    </sub-flow>


Note : For this post is applicable for Mule Runtime 3.x The Salesforce Analytics Connector 2.2.x is used. From Mule 4 this is replaced by Salesforce Analytics Module which i will be covering in a later post.
