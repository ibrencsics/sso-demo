package org.ib.sso.sts.sql;

import org.apache.ws.security.WSSecurityException;
import org.apache.ws.security.handler.RequestData;
import org.apache.ws.security.message.token.UsernameToken;
import org.apache.ws.security.validate.Credential;
import org.apache.ws.security.validate.Validator;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

public class SqlTokenValidator implements Validator {

	private JdbcTemplate jdbcTemplate;
	
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}


	/**
	 * In this case we only check if the user is existing in the database
	 * We expect no password
	 */
	
	@Override
	public Credential validate(Credential credential, RequestData data)
			throws WSSecurityException {
		
		if (credential == null || credential.getUsernametoken() == null) {
            throw new WSSecurityException(WSSecurityException.FAILURE, "noCredential");
        }
		
		UsernameToken usernameToken = credential.getUsernametoken();
		
		int userId=0;
		
		try {
			userId = getJdbcTemplate().queryForInt("select id from User where name=?", new Object[] {usernameToken.getName()});
		} catch (EmptyResultDataAccessException ex) {
			throw new WSSecurityException(WSSecurityException.FAILED_AUTHENTICATION);
		}
		
		if (userId==0)
			throw new WSSecurityException(WSSecurityException.FAILED_AUTHENTICATION);
		
		
		return credential;
	} 

}
