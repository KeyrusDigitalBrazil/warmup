/**
 *
 */
package de.hybris.platform.gigya.gigyaloginaddon.strategies;

import de.hybris.platform.acceleratorstorefrontcommons.security.AutoLoginStrategy;
import de.hybris.platform.acceleratorstorefrontcommons.security.GUIDCookieStrategy;
import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.user.UserManager;

import java.util.Collections;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.WebAuthenticationDetails;


public class DefaultGigyaAutoLoginStrategy implements AutoLoginStrategy
{

	private static final Logger LOG = Logger.getLogger(DefaultGigyaAutoLoginStrategy.class);

	private CustomerFacade customerFacade;
	private GUIDCookieStrategy guidCookieStrategy;
	private RememberMeServices rememberMeServices;

	@Override
	public void login(final String username, final String password, final HttpServletRequest request,
			final HttpServletResponse response)
	{
		final UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, null);
		final WebAuthenticationDetails webAuthenticationDetails = new WebAuthenticationDetails(request);
		token.setDetails(webAuthenticationDetails);
		try
		{
			final Authentication authentication = new UsernamePasswordAuthenticationToken(username, null,
					Collections.singletonList(new SimpleGrantedAuthority("ROLE_CUSTOMERGROUP")));
			SecurityContextHolder.getContext().setAuthentication(authentication);
			JaloSession.getCurrentSession().setUser(UserManager.getInstance().getUserByLogin(username));
			getCustomerFacade().loginSuccess();
			getGuidCookieStrategy().setCookie(request, response);
			getRememberMeServices().loginSuccess(request, response, token);
		}
		catch (final Exception e)
		{
			SecurityContextHolder.getContext().setAuthentication(null);
			LOG.error("Failure during login", e);
		}
	}

	public CustomerFacade getCustomerFacade()
	{
		return customerFacade;
	}

	@Required
	public void setCustomerFacade(final CustomerFacade customerFacade)
	{
		this.customerFacade = customerFacade;
	}

	public GUIDCookieStrategy getGuidCookieStrategy()
	{
		return guidCookieStrategy;
	}

	@Required
	public void setGuidCookieStrategy(final GUIDCookieStrategy guidCookieStrategy)
	{
		this.guidCookieStrategy = guidCookieStrategy;
	}

	public RememberMeServices getRememberMeServices()
	{
		return rememberMeServices;
	}

	@Required
	public void setRememberMeServices(final RememberMeServices rememberMeServices)
	{
		this.rememberMeServices = rememberMeServices;
	}

}
