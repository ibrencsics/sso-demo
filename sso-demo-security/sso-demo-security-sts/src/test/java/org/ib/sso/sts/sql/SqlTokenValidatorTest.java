package org.ib.sso.sts.sql;

import org.apache.ws.security.message.token.UsernameToken;
import org.apache.ws.security.validate.Validator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:H2Tests.xml")
public class SqlTokenValidatorTest {

	@Autowired
	Validator sqlTokenValidator;
	
	@Test
	public void validateTest() {
		
		// TODO: figure out, how to create a UsernameToken 
	}
}
