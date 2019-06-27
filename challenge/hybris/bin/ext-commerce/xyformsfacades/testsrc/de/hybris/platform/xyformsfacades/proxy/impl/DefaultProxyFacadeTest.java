package de.hybris.platform.xyformsfacades.proxy.impl;

import de.hybris.platform.xyformsservices.proxy.ProxyService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

/**
 * JUnit test suite for {@link DefaultProxyFacade}
 */
public class DefaultProxyFacadeTest
{
	@InjectMocks
	private DefaultProxyFacade proxyFacade;
	@Mock
	private ProxyService proxyService;

	@Mock
	private HttpServletRequest request;
	@Mock
	private HttpServletResponse response;
	private Map<String, String> extraHeaders;

	@Before
	public void setUp() throws MalformedURLException
	{
		proxyFacade = new DefaultProxyFacade();
		MockitoAnnotations.initMocks(this);

		extraHeaders = new HashMap<>();
		extraHeaders.put("header", "value");
		given(request.getRequestURL()).willReturn(new StringBuffer("https://test.url"));
		given(proxyService.rewriteURL("https://test.url", false)).willReturn("https://new.url");
		given(proxyService.getExtraHeaders()).willReturn(extraHeaders);
	}

	@Test
	public void shouldEncodeNamespace() throws Exception
	{
		given(proxyService.extractNamespace(request)).willReturn("uuid_nam3spac3<some#tag>");

		proxyFacade.proxy(request, response);

		verify(proxyService).proxy(eq(request), eq(response), eq("uuid_nam3spac3%3csome%23tag%3e"),
				eq("https://new.url"), eq(false), eq(extraHeaders));
	}

	@Test
	public void shouldNotAffectValidNamespace() throws Exception
	{
		given(proxyService.extractNamespace(request)).willReturn("uuid_nam3spac3");

		proxyFacade.proxy(request, response);

		verify(proxyService).proxy(eq(request), eq(response), eq("uuid_nam3spac3"),
				eq("https://new.url"), eq(false), eq(extraHeaders));
	}

	@Test
	public void shouldAllowNullNamespace() throws Exception
	{
		given(proxyService.extractNamespace(request)).willReturn(null);

		proxyFacade.proxy(request, response);

		verify(proxyService).proxy(eq(request), eq(response), eq(null),
				eq("https://new.url"), eq(false), eq(extraHeaders));
	}
}
