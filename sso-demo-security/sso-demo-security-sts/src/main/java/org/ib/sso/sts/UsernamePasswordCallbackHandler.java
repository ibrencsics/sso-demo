/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.ib.sso.sts;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import org.apache.ws.security.WSPasswordCallback;

import org.springframework.jdbc.core.JdbcTemplate;

public class UsernamePasswordCallbackHandler implements CallbackHandler {

    private Map<String, String> passwords;
    private JdbcTemplate jdbcTemplate;

    public void setPasswords(Map<String, String> passwords) {
        this.passwords = passwords;
    }

    public Map<String, String> getPasswords() {
        return passwords;
    }
    
    public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemple) {
		this.jdbcTemplate = jdbcTemple;
	}

	
	public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {

        if (getPasswords() == null || getPasswords().size() == 0) {
            return;
        }
        
        // H2
        
//        try {
//			org.h2.Driver.load();
//			
//			Connection conn = DriverManager.getConnection("jdbc:h2:mem:diad", "diaduser", "diadpass");
//			
//			PreparedStatement st = conn.prepareStatement("select * from USER");
//			ResultSet rs = st.executeQuery();
//			
//			while(rs.next()) {
//				System.out.println("----------------------------- " + rs.getString("Name"));
//			}
//			
//			rs.close();
//			conn.close();
//			
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
        
        List<Map<String,Object>> rows = getJdbcTemplate().queryForList("select * from USER");
        for (Map row : rows) {
    		System.out.println("+++++++++++++++++ " + row.get("Name"));
    	}
        

        for (int i = 0; i < callbacks.length; i++) {
            if (callbacks[i] instanceof WSPasswordCallback) { // CXF
                WSPasswordCallback pc = (WSPasswordCallback) callbacks[i];

                String pw = getPasswords().get(pc.getIdentifier());
                pc.setPassword(pw);
            }
        }
    }


}
