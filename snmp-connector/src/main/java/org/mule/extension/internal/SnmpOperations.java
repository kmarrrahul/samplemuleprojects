package org.mule.extension.internal;

import static org.mule.runtime.extension.api.annotation.param.MediaType.ANY;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.mule.runtime.extension.api.annotation.param.MediaType;
import org.snmp4j.PDU;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.VariableBinding;

import com.inmarsat.SimpleSnmp2cClient;

import org.mule.runtime.extension.api.annotation.param.Config;
import org.mule.runtime.extension.api.annotation.param.Connection;


/**
 * This class is a container for operations, every public method in this class will be taken as an extension operation.
 */
public class SnmpOperations {

  /**
   * Example of an operation that uses the configuration and a connection instance to perform some action.
   */
  @MediaType(value = ANY, strict = false)
  public String retrieveInfo(@Config SnmpConfiguration configuration, @Connection SnmpConnection connection){
    return "Using Configuration [" + configuration.getHost() + ":" + configuration.getPort() + "] with Connection id [" + connection.getId() + "]";
  }

  /**
   * Example of a simple operation that receives a string parameter and returns a new string message that will be set on the payload.
   */
  @MediaType(value = ANY, strict = false)
  public String sayHi(String person) {
    return buildHelloMessage(person);
  }

  /**
   * Private Methods are not exposed as operations
   */
  private String buildHelloMessage(String person) {
    return "Hello " + person + "!!!";
  }

  @MediaType(value = ANY, strict = false)
  public Map<String,String> getValues2C(String host, String port, List<String> oids) throws IOException {
    
	  return getValues2Cimpl(host,port,oids);
  }  
  
  private Map<String, String> getValues2Cimpl(String host, String port, List<String> oidsList) throws IOException
  {
	  Map<String,String> oidResultMap = null;
	  SimpleSnmp2cClient client = new SimpleSnmp2cClient("udp:"+host+"/"+port);
	  
	OID[] oids = new OID[oidsList.size()];
	for (int i=0; i< oids.length; i++) 
		oids[i] = new OID(oidsList.get(i));
	  
	ResponseEvent responseEvent = client.get(oids);
	if(responseEvent != null)
	{
		PDU pdu = responseEvent.getResponse();
		Vector bindings = pdu.getVariableBindings();
		oidResultMap = new HashMap<String,String>(bindings.size());
		for (int i=0; i<bindings.size(); i++) {
			VariableBinding item = (VariableBinding) bindings.get(i);
			String value = "" + item.getOid().getValue()[0];
			for (int ii=1; ii<item.getOid().getValue().length; ii++) {
				value = value + "." + item.getOid().getValue()[ii];
			}
			oidResultMap.put(value, item.getVariable().toString());
		}
	}
	else {
		oidResultMap = new HashMap<String,String>();
	}
	  return oidResultMap;
  }
  
}
