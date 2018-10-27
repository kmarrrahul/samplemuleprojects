package org.mule.extension.internal;

import org.mule.runtime.extension.api.annotation.Operations;
import org.mule.runtime.extension.api.annotation.connectivity.ConnectionProviders;
import org.mule.runtime.extension.api.annotation.param.Parameter;

/**
 * This class represents an extension configuration, values set in this class are commonly used across multiple
 * operations since they represent something core from the extension.
 */
@Operations(SnmpOperations.class)
@ConnectionProviders(SnmpConnectionProvider.class)
public class SnmpConfiguration {

  @Parameter
  private String host;
  
  @Parameter
  private String port;

public String getHost() {
	return host;
}

public String getPort() {
	return port;
}


}
