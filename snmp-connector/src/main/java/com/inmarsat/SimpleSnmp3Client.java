package com.inmarsat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.ScopedPDU;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.TransportMapping;
import org.snmp4j.UserTarget;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.event.ResponseListener;
import org.snmp4j.mp.MPv3;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.AuthSHA;
import org.snmp4j.security.PrivAES;
import org.snmp4j.security.PrivAES128;
import org.snmp4j.security.PrivDES;
import org.snmp4j.security.SecurityLevel;
import org.snmp4j.security.SecurityModels;
import org.snmp4j.security.SecurityProtocols;
import org.snmp4j.security.TSM;
import org.snmp4j.security.USM;
import org.snmp4j.security.UsmUser;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.DefaultPDUFactory;
import org.snmp4j.util.TableEvent;
import org.snmp4j.util.TableUtils;


/**
 * Simplest client possible
 * 
 * @author johanrask
 *
 */
public class SimpleSnmp3Client {

	private String address;

	private Snmp snmp;
	
	private OctetString securityName;
			
	public SimpleSnmp3Client(String address) {
		super();
		this.address = address;
		try {
			start();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	// Since snmp4j relies on asynch req/resp we need a listener
	// for responses which should be closed
	public void stop() throws IOException {
		snmp.close();
	}

	private void start() throws IOException {
		TransportMapping<? extends Address> transport = new DefaultUdpTransportMapping();
		snmp = new Snmp(transport);
		
		OctetString localEngineId = new OctetString(MPv3.createLocalEngineID());
		USM usm = new USM(SecurityProtocols.getInstance(), localEngineId, 0);
		SecurityModels.getInstance().addSecurityModel(usm);
		
		securityName = new OctetString("condor2");
		OID authProtocol = AuthSHA.ID;
		OID privProtocol = PrivAES128.ID;
		OctetString authPassphrase = new OctetString("B@rf0l0mew$NMP");
		OctetString privPassphrase = new OctetString("C@ptL0n3Starr!");
		
		snmp.getUSM().addUser(securityName, new UsmUser(securityName, authProtocol, authPassphrase, privProtocol, privPassphrase));
		SecurityModels.getInstance().addSecurityModel(new TSM(localEngineId, false));

		// Do not forget this line!
		transport.listen();
	}
	
	public String getAsString(OID oid) throws IOException {
		ResponseEvent event = get(new OID[]{oid});
		return event.getResponse().get(0).getVariable().toString();
	}
	
	
	public void getAsString(OID oids,ResponseListener listener) {
		try {
			snmp.send(getPDU(new OID[]{oids}), getTarget(),null, listener);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	
	private PDU getPDU(OID oids[]) {
		PDU pdu = new ScopedPDU();
		for (OID oid : oids) {
			pdu.add(new VariableBinding(oid));
		}
	 	   
		pdu.setType(PDU.GET);
		return pdu;
	}
	
	public ResponseEvent get(OID oids[]) throws IOException {
	   ResponseEvent event = snmp.send(getPDU(oids), getTarget(), null);
	   if(event != null) {
		   return event;
	   }
	   throw new RuntimeException("GET timed out");	  
	}
	
	private Target getTarget() {
		Address targetAddress = GenericAddress.parse(address);
		UserTarget target = new UserTarget();
		target.setAddress(targetAddress);	
		target.setRetries(2);
		target.setTimeout(1500);
		target.setVersion(SnmpConstants.version3);
		target.setSecurityLevel(SecurityLevel.AUTH_PRIV);
		target.setSecurityName(securityName);
		return target;
	}

	/**
	 * Normally this would return domain objects or something else than this...
	 */
	public List<List<String>> getTableAsStrings(OID[] oids) {
		TableUtils tUtils = new TableUtils(snmp, new DefaultPDUFactory());
		
		@SuppressWarnings("unchecked") 
			List<TableEvent> events = tUtils.getTable(getTarget(), oids, null, null);
		
		List<List<String>> list = new ArrayList<List<String>>();
		for (TableEvent event : events) {
			if(event.isError()) {
				throw new RuntimeException(event.getErrorMessage());
			}
			List<String> strList = new ArrayList<String>();
			list.add(strList);
			for(VariableBinding vb: event.getColumns()) {
				strList.add(vb.getVariable().toString());
			}
		}
		return list;
	}
	
	public static String extractSingleString(ResponseEvent event) {
		return event.getResponse().get(0).getVariable().toString();
	}
}
