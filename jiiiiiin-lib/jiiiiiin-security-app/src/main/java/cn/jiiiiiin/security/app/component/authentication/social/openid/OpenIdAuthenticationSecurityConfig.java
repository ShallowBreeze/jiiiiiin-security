/**
 * 
 */
package cn.jiiiiiin.security.app.component.authentication.social.openid;

import lombok.AllArgsConstructor;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.security.SocialUserDetailsService;
import org.springframework.stereotype.Component;

/**
 * @author zhailiang
 *
 */
@Component
@AllArgsConstructor
@ConditionalOnProperty(prefix = "jiiiiiin.security.oauth2", name = "enableAuthorizationServer", matchIfMissing = true)
public class OpenIdAuthenticationSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {
	
	private final AuthenticationSuccessHandler authenticationSuccessHandler;
	
	private final AuthenticationFailureHandler authenticationFailureHandler;
	
	private final SocialUserDetailsService socialUserDetailsService;
	
	private final UsersConnectionRepository usersConnectionRepository;


	@Override
	public void configure(HttpSecurity http) {
		
		val OpenIdAuthenticationFilter = new OpenIdAuthenticationFilter();
		OpenIdAuthenticationFilter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));
		OpenIdAuthenticationFilter.setAuthenticationSuccessHandler(authenticationSuccessHandler);
		OpenIdAuthenticationFilter.setAuthenticationFailureHandler(authenticationFailureHandler);
		
		val openIdAuthenticationProvider = new OpenIdAuthenticationProvider();
		openIdAuthenticationProvider.setUserDetailsService(socialUserDetailsService);
		openIdAuthenticationProvider.setUsersConnectionRepository(usersConnectionRepository);
		
		http.authenticationProvider(openIdAuthenticationProvider)
			.addFilterAfter(OpenIdAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
		
	}

}
