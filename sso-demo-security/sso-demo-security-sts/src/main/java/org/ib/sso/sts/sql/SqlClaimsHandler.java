package org.ib.sso.sts.sql;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.cxf.sts.claims.Claim;
import org.apache.cxf.sts.claims.ClaimCollection;
import org.apache.cxf.sts.claims.ClaimsHandler;
import org.apache.cxf.sts.claims.ClaimsParameters;
import org.apache.cxf.sts.claims.RequestClaim;
import org.apache.cxf.sts.claims.RequestClaimCollection;
import org.springframework.jdbc.core.JdbcTemplate;

public class SqlClaimsHandler implements ClaimsHandler {
	
	public static final URI ROLE = 
	        URI.create("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/role");
	
	private JdbcTemplate jdbcTemplate;
	
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	
	@Override
	public List<URI> getSupportedClaimTypes() {

		return Arrays.asList(new URI[] {ROLE});
	}

	@Override
	public ClaimCollection retrieveClaimValues(RequestClaimCollection requestClaims,
			ClaimsParameters parameters) {
		
		if (parameters.getPrincipal() == null) {
            return new ClaimCollection();
        }

        if (requestClaims == null || requestClaims.size() == 0) {
            return new ClaimCollection();
        }
        
		List<Map<String,Object>> rows = getJdbcTemplate().queryForList(
				"select r.name from Role r " +
				"inner join R_User_Role ur on r.id=ur.role_id " +
				"inner join User u on u.id=ur.user_id " +
				"where u.name=?", new Object[] { parameters.getPrincipal().getName() });
        
		ClaimCollection claimCollection = new ClaimCollection();
		
		RequestClaim requestClaim=null;
		
		for (RequestClaim iRequestClaim : requestClaims) {
			if (iRequestClaim.getClaimType().equals(SqlClaimsHandler.ROLE)) {
				requestClaim = iRequestClaim;
				break;
			}
		}
		
		if (requestClaim==null) {
			return new ClaimCollection();
		}
		
		for (Map row : rows) {
			Claim claim = new Claim();
			claim.setClaimType(requestClaim.getClaimType());
            claim.setIssuer("Test Issuer");
            claim.setOriginalIssuer("Original Issuer");
            claim.addValue(row.get("name").toString());
            claimCollection.add(claim);
    	}
		
		return claimCollection;
	}

}
