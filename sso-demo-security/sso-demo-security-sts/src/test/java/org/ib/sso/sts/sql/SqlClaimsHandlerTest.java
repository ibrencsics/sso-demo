package org.ib.sso.sts.sql;

import java.net.URI;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.apache.cxf.sts.claims.Claim;
import org.apache.cxf.sts.claims.ClaimCollection;
import org.apache.cxf.sts.claims.ClaimsHandler;
import org.apache.cxf.sts.claims.ClaimsParameters;
import org.apache.cxf.sts.claims.RequestClaim;
import org.apache.cxf.sts.claims.RequestClaimCollection;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:H2Tests.xml")
public class SqlClaimsHandlerTest {

	@Autowired
	ClaimsHandler claimsHandler;
	
	@Test
	public void getSupportedClaimTypesTest() {
		List<URI> supportedClaims = claimsHandler.getSupportedClaimTypes();
		assertNotNull(supportedClaims);
		assertEquals(supportedClaims.size(), 1);
		assertEquals(supportedClaims.get(0), SqlClaimsHandler.ROLE);
	}
	
	@Test
	public void retrieveClaimValuesTest() {
		
		RequestClaimCollection claims = new RequestClaimCollection();
		RequestClaim claim = new RequestClaim();
		claim.setClaimType(SqlClaimsHandler.ROLE);
		claims.add(claim);
		
		ClaimsParameters parameters = new ClaimsParameters();
		Principal principal = new Principal() {
			@Override
			public String getName() {
				return "Libri";
			}
		};
		parameters.setPrincipal(principal);
		
		ClaimCollection claimCollection = claimsHandler.retrieveClaimValues(claims, parameters);
		assertNotNull(claimCollection);
		assertTrue(claimCollection.size() > 0);
		
		List<String> claimList = new ArrayList<String>();
		
		for (Claim iClaim : claimCollection) {
			claimList.addAll(iClaim.getValues());
		}
		
		assertEquals(claimList.toString(), "[Role 1, Role 2]");
	}
}
