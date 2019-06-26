/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.webservicescommons.webservices;

import de.hybris.bootstrap.config.ExtensionInfo;
import de.hybris.platform.core.Registry;
import de.hybris.platform.embeddedserver.api.EmbeddedServer;
import de.hybris.platform.embeddedserver.api.EmbeddedServerBuilder;
import de.hybris.platform.embeddedserver.api.EmbeddedServerBuilderContext;
import de.hybris.platform.embeddedserver.base.EmbeddedExtension;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.util.Utilities;
import de.hybris.platform.webservicescommons.cache.TenantAwareEhCacheManagerFactoryBean;
import de.hybris.platform.webservicescommons.testsupport.client.DummyHostnameVerifier;
import de.hybris.platform.webservicescommons.testsupport.client.DummyTrustManager;
import de.hybris.platform.webservicescommons.testsupport.client.WebservicesAssert;
import de.hybris.platform.webservicescommons.testsupport.client.WsRequestBuilder;
import de.hybris.platform.webservicescommons.testsupport.client.WsSecuredRequestBuilder;
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.fest.util.Arrays;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientResponse;
import org.glassfish.jersey.filter.LoggingFilter;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Ignore;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;


/**
 * Deprecated, use {@link NeedsEmbeddedServer} annotation to run embedded server on test class and
 * {@link WsSecuredRequestBuilder} or {@link WsRequestBuilder} to construct
 * {@link javax.ws.rs.client.Invocation.Builder} that you need to access your web services
 *
 */
@Deprecated
public abstract class AbstractWebServicesTest extends ServicelayerTest
{
	public static final String WS_REQUIRED_CHANNEL_PROPERTY = "webservicescommons.required.channel";
	public static final String EMBEDDEDSERVER_HTTP_PORT_CONFIG_KEY = "embeddedserver.http.port";
	public static final String EMBEDDEDSERVER_HTTPS_PORT_CONFIG_KEY = "embeddedserver.ssl.port";

	public static final String WS_TEST_OAUTH2_TOKEN_ENDPOINT_PATH_KEY = "webservices.test.oauth2.endpoint";
	public static final String WS_TEST_OAUTH2_CLIENT_ID_KEY = "webservices.test.oauth2.clientid";
	public static final String WS_TEST_OAUTH2_CLIENT_SECRET_KEY = "webservices.test.oauth2.clientsecret";
	public static final String WS_TEST_OAUTH2_CUSTOMER_USERNAME_KEY = "webservices.test.oauth2.customerusername";
	public static final String WS_TEST_OAUTH2_CUSTOMER_PASSWORD_KEY = "webservices.test.oauth2.customerpassword";
	protected static final String HEADER_AUTH_KEY = "Authorization";
	protected static final String HEADER_AUTH_VALUE_PREFIX = "Bearer";
	private static final Logger LOG = Logger.getLogger(AbstractWebServicesTest.class);
	private static final String GRANT_TYPE_CLIENT_CRIDENTIALS = "client_credentials";
	private static final String GRANT_TYPE_PASSWORD = "password";
	private static EmbeddedServer embeddedServer;
	private static String originalCacheSuffix;
	private static final ObjectMapper jsonMapper = new ObjectMapper();

	protected WebTarget webResource;

	protected Client jerseyClient;

	@Resource
	private EmbeddedServerBuilder embeddedServerBuilder;

	private int embeddedTomcatPort = 0;
	private boolean useSSL;
	private final String oauthEndpointPath;
	private final String oauthClientId;
	private final String oauthClientSecret;
	private final String oauthCustomerName;
	private final String oauthCustomerPassword;

	public AbstractWebServicesTest()
	{
		super();
		this.useSSL = Registry.getCurrentTenant().getConfig().getString(WS_REQUIRED_CHANNEL_PROPERTY, "https").equals("https")
				? true : false;
		final int embeddedTomcatHttpPort = Registry.getCurrentTenant().getConfig().getInt(EMBEDDEDSERVER_HTTP_PORT_CONFIG_KEY,
				8001);
		final int embeddedTomcatHttpsPort = Registry.getCurrentTenant().getConfig().getInt(EMBEDDEDSERVER_HTTPS_PORT_CONFIG_KEY,
				8002);
		this.embeddedTomcatPort = useSSL ? embeddedTomcatHttpsPort : embeddedTomcatHttpPort;

		this.oauthEndpointPath = Registry.getCurrentTenant().getConfig().getString(WS_TEST_OAUTH2_TOKEN_ENDPOINT_PATH_KEY,
				"/oauth/token");
		this.oauthClientId = Registry.getCurrentTenant().getConfig().getString(WS_TEST_OAUTH2_CLIENT_ID_KEY, "mobile_android");
		this.oauthClientSecret = Registry.getCurrentTenant().getConfig().getString(WS_TEST_OAUTH2_CLIENT_SECRET_KEY, "secret");
		this.oauthCustomerName = Registry.getCurrentTenant().getConfig().getString(WS_TEST_OAUTH2_CUSTOMER_USERNAME_KEY,
				"testoauthcustomer");
		this.oauthCustomerPassword = Registry.getCurrentTenant().getConfig().getString(WS_TEST_OAUTH2_CUSTOMER_PASSWORD_KEY,
				"1234");
	}

	@AfterClass
	public static void stopEmbeddedTomcat()
	{
		try
		{
			if (embeddedServer != null)
			{
				embeddedServer.stop();
			}
		}
		finally
		{
			embeddedServer = null;
			restoreOriginalCacheSuffix();
		}
	}

	private static void restoreOriginalCacheSuffix()
	{
		if (originalCacheSuffix == null)
		{
			Registry.getCurrentTenant().getConfig().removeParameter(TenantAwareEhCacheManagerFactoryBean.CACHE_SUFFIX_PROPERTY);
		}
		else
		{
			Registry.getCurrentTenant().getConfig().setParameter(TenantAwareEhCacheManagerFactoryBean.CACHE_SUFFIX_PROPERTY,
					originalCacheSuffix);
			originalCacheSuffix = null;
		}
	}

	protected String getWsVersionForExtensionInfo(final ExtensionInfo extensionInfo)
	{
		return Utilities.getWebroot(extensionInfo.getName());
	}

	@Before
	public void setUp() throws Exception
	{
		ensureEmbeddedServerIsRunning();
		final JacksonJsonProvider provider = new JacksonJaxbJsonProvider()
				.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		final ClientConfig config = new ClientConfig(provider);

		//Jersey needs its own logger to setup logging entity
		final java.util.logging.Logger loggerForJerseyLoggingFilter = java.util.logging.Logger
				.getLogger(AbstractWebServicesTest.class.getName());
		config.register(new LoggingFilter(loggerForJerseyLoggingFilter, true));

		jerseyClient = createClient(config);
		webResource = setupWebResource(getExtensionInfo());

		ensureWebappIsRunning();
	}

	/**
	 * Method creates client which accepts all hostname and trust all certificates - because of that it is suitable only
	 * for tests
	 *
	 * @param config
	 *           client config
	 * @return created client
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 */
	protected Client createClient(final ClientConfig config) throws NoSuchAlgorithmException, KeyManagementException
	{
		final TrustManager[] trustAllCerts = Arrays.array(new DummyTrustManager());
		final SSLContext sc = SSLContext.getInstance("TLSv1");
		System.setProperty("https.protocols", "TLSv1");
		sc.init(null, trustAllCerts, new java.security.SecureRandom());

		return ClientBuilder.newBuilder().withConfig(config).hostnameVerifier(new DummyHostnameVerifier()).sslContext(sc).build();
	}

	protected WebTarget setupWebResource(final ExtensionInfo extensionInfo)
	{
		LOG.info("Using internal embedded server with master tenant and port " + embeddedTomcatPort);
		final String fromUri = useSSL ? "https://localhost/" : "http://localhost/";

		return jerseyClient.target(
				UriBuilder.fromUri(fromUri).port(embeddedTomcatPort).path(getWsVersionForExtensionInfo(extensionInfo)).build());
	}

	public void ensureEmbeddedServerIsRunning()
	{
		if (embeddedServer == null)
		{
			beforeEmbeddedServerCreation();
			embeddedServer = createEmbeddedServer();
			embeddedServer.start();
		}
	}

	protected EmbeddedServer createEmbeddedServer()
	{
		final String wsVersion = getWsVersionForExtensionInfo(getExtensionInfo());
		EmbeddedServerBuilderContext embeddedServerCtx;

		embeddedServerCtx = getEmbeddedServerBuilder().needEmbeddedServer()
				.withApplication(new EmbeddedExtension(getExtensionInfo()).withContext(wsVersion));

		//check if authorization extension name was specified
		if (getAuthorizationExtensionInfo() != null)
		{
			final String authExtWsVersion = getWsVersionForExtensionInfo(getAuthorizationExtensionInfo());
			embeddedServerCtx = embeddedServerCtx
					.withApplication(new EmbeddedExtension(getAuthorizationExtensionInfo()).withContext(authExtWsVersion));

		}
		return embeddedServerCtx.build();
	}

	protected void beforeEmbeddedServerCreation()
	{
		setCacheSuffix();
	}

	private void setCacheSuffix()
	{
		originalCacheSuffix = Registry.getCurrentTenant().getConfig()
				.getString(TenantAwareEhCacheManagerFactoryBean.CACHE_SUFFIX_PROPERTY, null);
		Registry.getCurrentTenant().getConfig().setParameter(TenantAwareEhCacheManagerFactoryBean.CACHE_SUFFIX_PROPERTY,
				"testCache");

	}

	protected EmbeddedServerBuilder getEmbeddedServerBuilder()
	{
		return embeddedServerBuilder;
	}

	protected void ensureWebappIsRunning()
	{
		webResource.request().head();
	}

	public abstract String getExtensionName();

	public ExtensionInfo getExtensionInfo()
	{
		if (StringUtils.isEmpty(getExtensionName()))
		{
			final String msg = "Empty extension name!";
			throw new IllegalArgumentException(msg);
		}
		final ExtensionInfo extensionInfo = Utilities.getExtensionInfo(getExtensionName());
		if (!StringUtils.isEmpty(getAuthorizationExtensionName()))
		{
			extensionInfo.getRequiredExtensionInfos().add(Utilities.getExtensionInfo(getAuthorizationExtensionName()));
		}
		return extensionInfo;
	}

	protected String getAuthorizationExtensionName()
	{
		return "";
	}

	protected ExtensionInfo getAuthorizationExtensionInfo()
	{
		return Utilities.getExtensionInfo(getAuthorizationExtensionName());
	}

	protected String getWebRoot(final ExtensionInfo extensionInfo)
	{
		String webRoot = null;
		if (extensionInfo.getWebModule() != null)
		{
			webRoot = extensionInfo.getWebModule().getWebRoot();
		}
		return webRoot;
	}

	protected Builder authorizeClientUsingClientCredentials(final WebTarget webResource, final String clientId,
			final String clientSecret)
	{
		checkAuthorizationExtensionInfo();
		final String token = getOAuth2TokenUsingClientCredentials(setupWebResource(getAuthorizationExtensionInfo()), clientId,
				clientSecret);
		return addAuthorizationHeader(webResource, token);
	}

	protected Builder authorizeClientUsingClientCredentials(final WebTarget webResource)
	{
		return authorizeClientUsingClientCredentials(webResource, oauthClientId, oauthClientSecret);
	}


	protected Builder authorizeClientUsingResourceOwnerPassword(final WebTarget webResource, final String customerName,
			final String customerPassword, final String clientId, final String clientSecret)
	{
		checkAuthorizationExtensionInfo();
		final String token = getOAuth2TokenUsingResourceOwnerPassword(setupWebResource(getAuthorizationExtensionInfo()), clientId,
				clientSecret, customerName, customerPassword);
		return addAuthorizationHeader(webResource, token);
	}

	protected Builder authorizeClientUsingResourceOwnerPassword(final WebTarget webResource, final String customerName,
			final String customerPassword)
	{
		return authorizeClientUsingResourceOwnerPassword(webResource, customerName, customerPassword, oauthClientId,
				oauthClientSecret);
	}

	protected Builder authorizeClientUsingResourceOwnerPassword(final WebTarget webResource)
	{
		return authorizeClientUsingResourceOwnerPassword(webResource, oauthCustomerName, oauthCustomerPassword);
	}

	private void checkAuthorizationExtensionInfo()
	{
		if (StringUtils.isEmpty(getAuthorizationExtensionName()))
		{
			final String msg = "No Authorization Extension name specified!";
			throw new IllegalArgumentException(msg);
		}
	}

	protected String getOAuth2TokenUsingClientCredentials(final WebTarget webResource, final String clientID,
			final String clientSecret)
	{
		try
		{
			final Response result = webResource.path(oauthEndpointPath).queryParam("grant_type", GRANT_TYPE_CLIENT_CRIDENTIALS)
					.queryParam("client_id", clientID).queryParam("client_secret", clientSecret).request()
					.post(Entity.entity(null, MediaType.APPLICATION_JSON));
			result.bufferEntity();

			if (result.hasEntity())
			{
				return getTokenFromJsonStr(result.readEntity(String.class));
			}
			else
			{
				LOG.error("Empty response body!!");
				return null;
			}
		}
		catch (final IOException ex)
		{
			LOG.error("Error during authorizing REST client client credentials!!", ex);
			return null;
		}
	}

	protected String getOAuth2TokenUsingResourceOwnerPassword(final WebTarget webResource, final String clientID,
			final String clientSecret, final String customerName, final String customerPassword)
	{
		try
		{
			final Response result = webResource.path(oauthEndpointPath).queryParam("grant_type", GRANT_TYPE_PASSWORD)
					.queryParam("username", customerName).queryParam("password", customerPassword).queryParam("client_id", clientID)
					.queryParam("client_secret", clientSecret).request().post(Entity.entity(null, MediaType.APPLICATION_JSON));
			result.bufferEntity();

			if (result.hasEntity())
			{
				return getTokenFromJsonStr(result.readEntity(String.class));
			}
			else
			{
				LOG.error("Empty response body!!");
				return null;
			}
		}
		catch (final IOException ex)
		{
			LOG.error("Error during authorizing REST client using Resource owner password!!", ex);
			return null;
		}
	}

	protected Builder addAuthorizationHeader(final WebTarget webResource, final String token)
	{
		if (!StringUtils.isEmpty(token))
		{
			final StringBuilder sb = new StringBuilder(HEADER_AUTH_VALUE_PREFIX).append(" ").append(token);
			return webResource.request().header(HEADER_AUTH_KEY, sb.toString());
		}
		return webResource.request();
	}

	public static String getTokenFromJsonStr(final String jsonStr) throws IOException
	{
		final Map<String, String> map = jsonMapper.readValue(jsonStr, new TypeReference<HashMap<String, String>>()
		{/* empty */});
		return map.get("access_token");
	}

	public static <T> T jsonToObj(final String jsonStr, final Class<T> c) throws IOException
	{
		return jsonMapper.readValue(jsonStr, c);
	}

	public static String objToJson(final Object obj) throws IOException
	{
		return jsonMapper.writeValueAsString(obj);
	}

	/**
	 * Tests whether the response has status OK. Expects correct status (200), content type of 'application/xml'
	 *
	 * @param response
	 *           {@link ClientResponse}
	 * @param expectEmptyBody
	 *           true - the body is checked for null value
	 */
	protected void assertOk(final Response response, final boolean expectEmptyBody)
	{
		WebservicesAssert.assertOk(response, expectEmptyBody);
	}

	/**
	 * Tests whether resource was successfully created. Expects correct status (201), content type of 'application/xml'
	 *
	 * @param response
	 *           {@link ClientResponse}
	 * @param expectEmptyBody
	 *           true - the body is checked for null value
	 */
	protected void assertCreated(final Response response, final boolean expectEmptyBody)
	{
		WebservicesAssert.assertCreated(response, expectEmptyBody);
	}

	/**
	 * Tests whether the response has status FORBIDDEN. Expects correct status (403), content type of 'application/xml'
	 *
	 * @param response
	 *           {@link ClientResponse}
	 * @param expectEmptyBody
	 *           true - the body is checked for null value
	 */
	protected void assertForbidden(final Response response, final boolean expectEmptyBody)
	{
		WebservicesAssert.assertForbidden(response, expectEmptyBody);
	}

	/**
	 * Tests whether the response has status BAD_REQUEST. Expects correct status (400), content type of 'application/xml'
	 *
	 * @param response
	 *           {@link ClientResponse}
	 * @param expectEmptyBody
	 *           true - the body is checked for null value
	 */
	protected void assertBadRequest(final Response response, final boolean expectEmptyBody)
	{
		WebservicesAssert.assertBadRequest(response, expectEmptyBody);
	}

	/**
	 * Tests whether the response has status UNAUTHORIZED. Expects correct status (401), content type of
	 * 'application/xml'
	 *
	 * @param response
	 *           {@link ClientResponse}
	 * @param expectEmptyBody
	 *           true - the body is checked for null value
	 */
	protected void assertUnauthorized(final Response response, final boolean expectEmptyBody)
	{
		WebservicesAssert.assertUnauthorized(response, expectEmptyBody);
	}

	/**
	 * Tests whether the response status has expected value.
	 *
	 * @param response
	 *           {@link ClientResponse}
	 * @param expectEmptyBody
	 *           true - the body is checked for null value
	 */
	protected void assertResponseStatus(final Status responseStatus, final Response response, final boolean expectEmptyBody)
	{
		WebservicesAssert.assertResponseStatus(responseStatus, response, expectEmptyBody);
	}

	protected void assertResponseStatus(final Status status, final Response response)
	{
		WebservicesAssert.assertResponseStatus(status, response);
	}

	public void setUseSSL(final boolean useSSL)
	{
		this.useSSL = useSSL;
	}

	public boolean isUseSSL()
	{
		return useSSL;
	}
}
