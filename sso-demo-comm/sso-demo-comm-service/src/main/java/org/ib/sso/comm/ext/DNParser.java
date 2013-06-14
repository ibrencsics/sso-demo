package org.ib.sso.comm.ext;

public class DNParser {

	public static String getCN(String dn) {
		
		if (dn==null || dn.equals(""))
			return null;
		
		String[] tokens = dn.split(",");
		
		for (String token : tokens) {
			if (token.startsWith("CN")) {
				return token.substring(token.indexOf("=")+1, token.length());
			}
		}
		
		return null;
	}
}
