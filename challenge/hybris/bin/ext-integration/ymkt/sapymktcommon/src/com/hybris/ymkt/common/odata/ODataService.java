/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.hybris.ymkt.common.odata;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.TreeMap;
import java.util.zip.GZIPInputStream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.apache.olingo.odata2.api.edm.Edm;
import org.apache.olingo.odata2.api.edm.EdmElement;
import org.apache.olingo.odata2.api.edm.EdmEntitySet;
import org.apache.olingo.odata2.api.edm.EdmEntityType;
import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.edm.EdmFunctionImport;
import org.apache.olingo.odata2.api.edm.EdmLiteralKind;
import org.apache.olingo.odata2.api.edm.EdmMultiplicity;
import org.apache.olingo.odata2.api.edm.EdmNavigationProperty;
import org.apache.olingo.odata2.api.edm.EdmParameter;
import org.apache.olingo.odata2.api.edm.EdmSimpleType;
import org.apache.olingo.odata2.api.edm.EdmTyped;
import org.apache.olingo.odata2.api.ep.EntityProviderException;
import org.apache.olingo.odata2.core.edm.provider.EdmImplProv;
import org.apache.olingo.odata2.core.edm.provider.EdmxProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.hybris.ymkt.common.http.HttpURLConnectionRequest;
import com.hybris.ymkt.common.http.HttpURLConnectionResponse;
import com.hybris.ymkt.common.http.HttpURLConnectionService;


/**
 * This bean shall be instantiated for each OData service as separate $metadata are used by OData services.
 *
 */
public class ODataService
{
	protected static class TokenCache
	{
		protected List<String> setCookies;
		protected List<String> xcsrfTokens;
	}

	private static final Logger LOG = LoggerFactory.getLogger(ODataService.class);

	protected static final String X_CSRF_TOKEN = "x-csrf-token";

	protected volatile EdmxProvider edmxProvider;
	protected HttpURLConnectionService httpURLConnectionService;
	protected ODataConvertEdmService oDataConvertEdmService;
	protected String password;
	protected Proxy proxy = Proxy.NO_PROXY;
	protected String rootUrl;
	protected String sapClient;
	protected volatile TokenCache token; // Multi-thread shared cookie(http session) & CSRF token
	protected String user;

	/**
	 * Read {@link InputStream} and return a byte array.
	 *
	 * @param stream
	 *           {@link InputStream} to read.
	 * @return All bytes that could be read from the {@link InputStream}
	 * @throws IOException
	 *            from {@link InputStream#read(byte[])}
	 * @see HttpURLConnectionService#bufferStream(InputStream)
	 */
	@Nonnull
	public byte[] bufferStream(final InputStream stream) throws IOException
	{
		return this.httpURLConnectionService.bufferStream(stream);
	}

	private String getBasicAuth()
	{
		if (this.user == null || this.password == null)
		{
			return null;
		}

		final byte[] userAndPassword = (this.user + ':' + this.password).getBytes(StandardCharsets.UTF_8);
		return "Basic " + Base64.getEncoder().encodeToString(userAndPassword);
	}

	protected void checkStatus(final HttpURLConnectionResponse response) throws IOException
	{
		if (response.getIOException() != null)
		{
			throw new IOException("IOException occured", response.getIOException());
		}
		final int responseCode = response.getResponseCode();
		if (responseCode >= HttpURLConnection.HTTP_BAD_REQUEST)
		{
			final byte[] payloadError = response.getPayloadError();
			final String errorMessage = new String(payloadError, StandardCharsets.UTF_8);
			throw new IOException("HTTP error (" + responseCode + ") : " + errorMessage);
		}
	}

	@Nonnull
	protected JsonObject convertMapToJSON(final EdmEntityType entityType, final Map<?, ?> data) throws IOException, EdmException
	{
		final JsonObject jsonObject = new JsonObject();

		final List<Object> allProperties = new ArrayList<Object>();
		allProperties.addAll(entityType.getPropertyNames());
		allProperties.addAll(entityType.getNavigationPropertyNames());

		// TreeMap for pretty JSON ordered by the $metadata
		final Map<Object, Object> dataSorted = new TreeMap<>(Comparator.comparingInt(allProperties::indexOf));
		dataSorted.putAll(data);

		for (final Entry<Object, Object> entry : dataSorted.entrySet())
		{
			final String key = (String) entry.getKey();
			final Object value = entry.getValue();
			final EdmTyped property = entityType.getProperty(key);

			if (property instanceof EdmElement)
			{
				jsonObject.add(key, this.convertObjectToJsonElement(value, (EdmElement) property));
			}
			else if (property instanceof EdmNavigationProperty)
			{
				jsonObject.add(key, this.convertObjectToJsonElement(value, (EdmNavigationProperty) property));
			}
			else
			{
				LOG.warn("Ignoring property='{}'({}) as it does not exists in entity='{}'", key, property, entityType.getName());
			}
		}
		return jsonObject;
	}

	/**
	 * Convert an entity map into a JSON payload reading to be transfered.
	 *
	 * @param entitySetName
	 *           &lt;EntitySet <b>Name</b>&gt value found in the DefaultEntityContainer of the OData service.
	 * @param data
	 *           {@link Map} containing the &lt;Property/NavigationProperty <b>Name</b>&gt and value to be converted.
	 * @return byte array of the JSON payload.
	 * @throws IOException
	 *            if an I/O error occurs.
	 * @see EdmLiteralKind#JSON
	 */
	@Nonnull
	public byte[] convertMapToJSONPayload(final String entitySetName, final Map<String, Object> data) throws IOException
	{
		try
		{
			final EdmEntityType entityType = this.getEntitySet(entitySetName).getEntityType();
			return this.convertMapToJSONString(entityType, data).getBytes(StandardCharsets.UTF_8);
		}
		catch (final EdmException e)
		{
			throw new IllegalStateException("Error converting entitySetName=" + entitySetName + " data=" + data, e);
		}
	}

	/**
	 * Convert an entity map into a JSON String.
	 *
	 * @param entityType
	 *           {@link EdmEntityType} of the OData entity to be converted.
	 * @param data
	 *           {@link Map} containing the &lt;Parameter&gt <b>Name</b> and value to be converted.
	 * @return String of the JSON payload.
	 * @throws IOException
	 *            if an I/O error occurs.
	 * @see EdmLiteralKind#JSON
	 */
	@Nonnull
	public String convertMapToJSONString(final EdmEntityType entityType, final Map<String, Object> data) throws IOException
	{
		try
		{
			final JsonObject jsonObject = this.convertMapToJSON(entityType, data);

			if (LOG.isDebugEnabled())
			{
				final Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
				LOG.debug("{}", gson.toJson(jsonObject));
			}
			return jsonObject.toString();
		}
		catch (final EdmException e)
		{
			throw new IllegalStateException("Error converting entityType=" + entityType + " data=" + data, e);
		}
	}

	/**
	 * Build properly formated String value arguments for OData Function Import.
	 *
	 * @param functionImportName
	 *           Attribute <b>Name</b> value found in the &lt;FunctionImport&gt; of the OData service.
	 * @param data
	 *           {@link Map} containing the &lt;Parameter&gt <b>Name</b> and value to be converted.
	 * @return {@link Map} containing the &lt;Parameter&gt <b>Name</b> and converted value.
	 * @throws IOException
	 *            if an I/O error occurs.
	 * @see EdmLiteralKind#URI
	 */
	public Map<String, String> convertMapToURIParameters(final String functionImportName, final Map<String, Object> data)
			throws IOException
	{
		try
		{
			final Map<String, EdmFunctionImport> functionImportMap = new HashMap<>();
			for (final EdmFunctionImport f : this.getEdm().getFunctionImports())
			{
				functionImportMap.put(f.getName(), f);
			}

			final EdmFunctionImport functionImport = functionImportMap.get(functionImportName);
			if (functionImport == null)
			{
				throw new IllegalStateException("FunctionImport '" + functionImportName + "' could not be found. Only functions "
						+ functionImportMap.keySet() + " exists at address " + this.rootUrl);
			}

			final Map<String, String> parameters = new LinkedHashMap<>();
			for (final Entry<String, Object> e : data.entrySet())
			{
				final String key = e.getKey();
				final EdmParameter parameter = functionImport.getParameter(key);
				if (parameter == null)
				{
					throw new IllegalStateException(
							"Parameter '" + key + "' does not exists in FunctionImport '" + functionImportName + "'.");
				}
				parameters.put(key, this.convertObjectToString(e.getValue(), EdmLiteralKind.URI, parameter));
			}
			return parameters;
		}
		catch (final EdmException e)
		{
			throw new IllegalStateException("Error converting data=" + data, e);
		}
	}

	@Nonnull
	protected JsonElement convertObjectToJsonElement(final Object value, final EdmElement element) throws EdmException
	{
		final String string = this.convertObjectToString(value, EdmLiteralKind.JSON, element);
		if (string == null)
		{
			return JsonNull.INSTANCE;
		}

		// http://www.odata.org/documentation/odata-version-2-0/json-format/
		switch (element.getType().getName())
		{
			case "Boolean":
				return new JsonPrimitive(Boolean.valueOf(string));
			case "Int16":
			case "Int32":
				return new JsonPrimitive(Integer.valueOf(string));
			default:
				return new JsonPrimitive(string);
		}
	}

	protected JsonElement convertObjectToJsonElement(final Object value, final EdmNavigationProperty navigation)
			throws EdmException, IOException
	{
		final EdmMultiplicity multiplicity = navigation.getRelationship().getEnd2().getMultiplicity();
		final EdmEntityType entityType2 = navigation.getRelationship().getEnd2().getEntityType();

		final List<JsonObject> objects = new ArrayList<>();
		if (value instanceof Collection<?>)
		{
			for (final Object map : (Collection<?>) value)
			{
				objects.add(this.convertMapToJSON(entityType2, (Map<?, ?>) map));
			}
		}
		else if (value instanceof Map<?, ?>)
		{
			objects.add(this.convertMapToJSON(entityType2, (Map<?, ?>) value));
		}
		else
		{
			throw new IllegalStateException("Invalid NavigationProperty name = " + navigation.getName() + " object class="
					+ value.getClass() + " value=" + value);
		}

		return EdmMultiplicity.MANY == multiplicity ? //
				objects.stream().collect(JsonArray::new, JsonArray::add, JsonArray::addAll) : //
				objects.get(0);
	}

	@Nullable
	protected String convertObjectToString(final Object value, final EdmLiteralKind literalKind, final EdmElement element)
			throws EdmException
	{
		try
		{
			final EdmSimpleType type = (EdmSimpleType) element.getType();
			final Object newValue = this.oDataConvertEdmService.convertEdm(value, element);
			return type.valueToString(newValue, literalKind, element.getFacets());
		}
		catch (final EdmException e)
		{
			LOG.error("Error converting attribute '{}' using value '{}'", element.getName(), value);
			throw e;
		}
	}

	/**
	 * Create $batch part URL accessing entity by keys for Addressing Entries.
	 *
	 * @param entitySetName
	 *           &lt;EntitySet <b>Name</b>&gt value found in the DefaultEntityContainer of the OData service.
	 * @param data
	 *           {@link Map} containing the &lt;Property/NavigationProperty <b>Name</b>&gt and value to be converted.
	 * @return resource path
	 * @throws IOException
	 *            if an I/O error occurs.
	 */
	public String createResourcePath(final String entitySetName, final Map<String, Object> data) throws IOException
	{
		try
		{
			final EdmEntityType entityType = this.getEntitySet(entitySetName).getEntityType();
			final List<String> keyPropertyNames = entityType.getKeyPropertyNames();
			if (!data.keySet().containsAll(keyPropertyNames))
			{
				throw new IllegalStateException("Missing keyPredicates " + keyPropertyNames + " -> " + data);
			}

			final Map<String, String> parameters = new HashMap<>();
			for (final String key : keyPropertyNames)
			{
				final EdmElement element = (EdmElement) entityType.getProperty(key);
				final Object value = data.get(key);
				parameters.put(key, this.convertObjectToString(value, EdmLiteralKind.URI, element));
			}

			final StringBuilder resourcePath = new StringBuilder("./").append(entitySetName).append('(');

			if (parameters.size() == 1)
			{
				resourcePath.append(parameters.values().iterator().next());
			}
			else
			{
				for (final String key : keyPropertyNames)
				{
					final String value = parameters.get(key);
					resourcePath.append(key);
					resourcePath.append('=');
					resourcePath.append(value);
					resourcePath.append(',');
				}
				resourcePath.setLength(resourcePath.length() - 1); // Remove last ','
			}
			return resourcePath.append(')').toString();
		}
		catch (final EdmException | UnsupportedEncodingException e)
		{
			throw new IllegalStateException("Error converting data=" + data, e);
		}
	}

	/**
	 * Create an absolute URL accessing the OData service at the resource path provided.
	 *
	 * @param resourcePath
	 *           Resource path of the OData service.
	 * @return {@link URL}
	 */
	@Nonnull
	public URL createURL(final String resourcePath)
	{
		return this.createURL(resourcePath, Collections.emptyMap());
	}

	/**
	 * Create an absolute URL accessing the OData service at the resourcePath provided using OData query options.
	 *
	 * @param resourcePath
	 *           Resource path of the OData service.
	 * @param queryOptions
	 *           OData Query String Options.
	 * @return {@link URL}
	 */
	@Nonnull
	public URL createURL(final String resourcePath, final Map<String, String> queryOptions)
	{
		final StringBuilder query = this.getCustomQueryOptions();
		try
		{
			for (final Entry<String, String> e : queryOptions.entrySet())
			{
				query.append('&');
				query.append(URLEncoder.encode(e.getKey(), "UTF-8"));
				query.append('=');
				query.append(URLEncoder.encode(e.getValue(), "UTF-8"));
			}
			return new URL(this.rootUrl + resourcePath + query);
		}
		catch (MalformedURLException | UnsupportedEncodingException e)
		{
			throw new IllegalStateException("Invalid URL built using rootUrl=" + this.rootUrl + " resourcePath=" + resourcePath
					+ " query=" + query + " queryOptions=" + queryOptions, e);
		}
	}

	/**
	 * Create an absolute URL accessing the OData service at the resourcePath provided using OData query options.
	 *
	 * @param resourcePath
	 *           Resource path of the OData service.
	 * @param queryOptions
	 *           OData Query String Options.
	 * @return {@link URL}
	 */
	@Nonnull
	public URL createURL(final String resourcePath, final String... queryOptions)
	{
		final Map<String, String> queryOptionsMap = new LinkedHashMap<>();
		for (int i = 0; i < queryOptions.length; i += 2)
		{
			queryOptionsMap.put(queryOptions[i], queryOptions[i + 1]);
		}
		return this.createURL(resourcePath, queryOptionsMap);
	}

	@Nonnull
	protected byte[] decompressIfGZIP(final byte[] payload) throws IOException
	{
		if (payload.length < 2 || payload[0] != 31 || payload[1] != -117)
		{
			return payload;
		}

		final ByteArrayInputStream bais = new ByteArrayInputStream(payload);
		try (final GZIPInputStream gis = new GZIPInputStream(bais))
		{
			return this.bufferStream(gis);
		}
	}

	/**
	 * Execute the request with Authorization : "Basic BASE64user:pass".<br>
	 * Using this method will create a new http session (unnecessary cost) on the yMKT side.<br>
	 * It is preferable to use {@link #executeWithRetry(HttpURLConnectionRequest)} for most execution scenarios.
	 *
	 * @param request
	 *           {@link HttpURLConnectionRequest} to execute.
	 * @return {@link HttpURLConnectionResponse} executed.
	 * @throws IOException
	 *            If the HTTP request was not successful.
	 * @see URLConnection#setRequestProperty
	 */
	@Nonnull
	protected HttpURLConnectionResponse executeWithBasicAuth(final HttpURLConnectionRequest request) throws IOException
	{
		if (this.getBasicAuth() != null)
		{
			request.getRequestProperties().put(HttpHeaders.AUTHORIZATION, this.getBasicAuth());
		}

		request.setProxy(this.proxy);
		final HttpURLConnectionResponse response = this.httpURLConnectionService.execute(request);

		this.checkStatus(response);

		return response;
	}

	/**
	 * Execute the request with "cookie" & "x-csrf-token".<br>
	 * If the existing session and/or token expired, those are refreshed and the request is re-tried.
	 *
	 * @param request
	 *           {@link HttpURLConnectionRequest} to execute.
	 * @return {@link HttpURLConnectionResponse} executed.
	 * @throws IOException
	 *            If the HTTP request was not successful.
	 */
	@Nonnull
	public HttpURLConnectionResponse executeWithRetry(final HttpURLConnectionRequest request) throws IOException
	{
		final TokenCache tokenCache = this.getTokenCache();
		request.getRequestProperties().put(X_CSRF_TOKEN, String.join("; ", tokenCache.xcsrfTokens));
		request.getRequestProperties().put(HttpHeaders.COOKIE, String.join("; ", tokenCache.setCookies));

		// All OData call accept gzip encoding.
		request.getRequestProperties().put(HttpHeaders.ACCEPT_ENCODING, "gzip");

		request.setProxy(this.proxy);
		HttpURLConnectionResponse response = this.httpURLConnectionService.execute(request);

		// Expired http session is reported as error 401
		// Invalid csrf token is reported as error 403
		// HCI convert both 401 & 403 as error 500
		if (response.getResponseCode() == HttpURLConnection.HTTP_UNAUTHORIZED || //
				response.getResponseCode() == HttpURLConnection.HTTP_FORBIDDEN || //
				response.getResponseCode() == HttpURLConnection.HTTP_INTERNAL_ERROR)
		{
			final TokenCache retryTokenCache = this.refreshToken(tokenCache);
			request.getRequestProperties().put(X_CSRF_TOKEN, String.join("; ", retryTokenCache.xcsrfTokens));
			request.getRequestProperties().put(HttpHeaders.COOKIE, String.join("; ", retryTokenCache.setCookies));

			response = this.httpURLConnectionService.execute(request);
		}

		response.setPayload(this.decompressIfGZIP(response.getPayload()));
		response.setPayloadError(this.decompressIfGZIP(response.getPayloadError()));

		this.checkStatus(response);

		return response;
	}

	/**
	 * Create an {@link ODataFilterBuilder} starting on the entitySetName provided.
	 *
	 * @param entitySetName
	 *           &lt;EntitySet <b>Name</b>&gt value found in the DefaultEntityContainer of the OData service.
	 * @return {@link ODataFilterBuilder}
	 * @throws IOException
	 *            if an I/O error occurs.
	 */
	public ODataFilterBuilder filter(final String entitySetName) throws IOException
	{
		final EdmEntitySet entitySet = this.getEntitySet(entitySetName);
		return ODataFilterBuilder.of(entitySet, oDataConvertEdmService);
	}


	@Nonnull
	protected StringBuilder getCustomQueryOptions()
	{
		final StringBuilder customQueryOptions = new StringBuilder("?saml2=disabled");
		if (this.sapClient != null && !this.sapClient.isEmpty())
		{
			customQueryOptions.append("&sap-client=").append(this.sapClient);
		}
		return customQueryOptions;
	}

	/**
	 * Provide buffered olingo's {@link Edm} of the OData service.
	 *
	 * @return And instance of {@link Edm} <code>$metadata</code> for the service end point set by
	 *         {@link #setRootUrl(String)}.<br>
	 *         This resource is requested once for the entire lifecycle of the bean. This resource is also synchronized.
	 *         <br>
	 *         Each Thread has its own instance of Edm.
	 * @throws IOException
	 *            If Edm could not be returned.
	 */
	@Nonnull
	public Edm getEdm() throws IOException
	{
		if (this.edmxProvider != null)
		{
			return new EdmImplProv(this.edmxProvider);
		}
		synchronized (this)
		{
			if (this.edmxProvider != null)
			{
				return new EdmImplProv(this.edmxProvider);
			}

			final HttpURLConnectionRequest request = new HttpURLConnectionRequest("GET", this.createURL("$metadata"));
			request.getRequestProperties().put(HttpHeaders.ACCEPT, MediaType.APPLICATION_XML);

			final HttpURLConnectionResponse response = this.executeWithRetry(request);

			try
			{
				final ByteArrayInputStream in = new ByteArrayInputStream(response.getPayload());
				this.edmxProvider = new EdmxProvider().parse(in, false);
				return new EdmImplProv(this.edmxProvider);
			}
			catch (final EntityProviderException e)
			{
				throw new IllegalStateException("Invalid Edm $metadata at " + request.getURL(), e);
			}
		}
	}

	/**
	 * Simple wrapping with improved error handling of :<br>
	 * <code>this.getEdm().getDefaultEntityContainer().getEntitySet(entitySetName);</code>
	 *
	 * @param entitySetName
	 *           &lt;EntitySet <b>Name</b>&gt value found in the DefaultEntityContainer of the OData service.
	 * @return {@link EdmEntitySet}
	 * @throws IOException
	 *            In case of communication errors.
	 */
	public EdmEntitySet getEntitySet(final String entitySetName) throws IOException
	{
		try
		{
			final EdmEntitySet entitySet = this.getEdm().getDefaultEntityContainer().getEntitySet(entitySetName);
			if (entitySet == null)
			{
				final List<String> entitySetNames = new ArrayList<>();
				for (final EdmEntitySet es : this.getEdm().getDefaultEntityContainer().getEntitySets())
				{
					entitySetNames.add(es.getName());
				}

				throw new IllegalStateException("EntitySetName='" + entitySetName + "' does not exists in EntitySets="
						+ entitySetNames + " at " + this.rootUrl);
			}
			return entitySet;
		}
		catch (final EdmException e)
		{
			throw new IllegalStateException("Error read entitySetName=" + entitySetName, e);
		}
	}

	@Nonnull
	protected TokenCache getTokenCache() throws IOException
	{
		if (this.token == null)
		{
			this.refreshToken(null);
		}
		return this.token;
	}

	@Nonnull
	protected TokenCache refreshToken(final TokenCache token) throws IOException
	{
		if (!Objects.equals(this.token, token))
		{
			return this.token;
		}

		// Double check locking
		synchronized (this)
		{
			if (!Objects.equals(this.token, token))
			{
				return this.token;
			}

			final HttpURLConnectionRequest request = new HttpURLConnectionRequest("GET", this.createURL(""));
			request.getRequestProperties().put(X_CSRF_TOKEN, "Fetch");

			final HttpURLConnectionResponse response = this.executeWithBasicAuth(request);

			final TokenCache newToken = new TokenCache();
			newToken.setCookies = response.getHeaderField(HttpHeaders.SET_COOKIE);
			newToken.xcsrfTokens = response.getHeaderField(X_CSRF_TOKEN);

			if (newToken.setCookies.isEmpty() || newToken.xcsrfTokens.isEmpty())
			{
				throw new IllegalStateException("Invalid setCookies=" + newToken.setCookies + " or xcsrfToken=" + newToken.xcsrfTokens
						+ " from headers : " + response.getHeaderFields());
			}

			this.token = newToken;
		}

		return this.token;
	}

	@Required
	public void setHttpURLConnectionService(final HttpURLConnectionService httpURLConnectionService)
	{
		this.httpURLConnectionService = httpURLConnectionService;
	}

	@Required
	public void setODataConvertEdmService(final ODataConvertEdmService oDataConvertEdmService)
	{
		this.oDataConvertEdmService = oDataConvertEdmService;
	}

	/**
	 * @param password
	 *           Password used to login with {@link #setUser(String)} at the {@link #setRootUrl(String)} address.
	 */
	public void setPassword(final String password)
	{
		this.password = password;
	}

	public void setProxy(final String proxy) throws MalformedURLException
	{
		LOG.debug("proxy={}", proxy);
		if (proxy == null || proxy.isEmpty())
		{
			return;
		}
		final URL proxyURL = new URL(proxy);
		final String hostname = proxyURL.getHost();
		final int port = proxyURL.getPort() == -1 ? 8080 : proxyURL.getPort();
		this.proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(hostname, port));
	}

	/**
	 * @param rootUrl
	 *           OData service root URL.<br>
	 *           Sample : <code>https://localhost:50100/sap/opu/odata/sap/PROD_RECO_RUNTIME_SRV/</code> <br>
	 *           <code>localhost</code> should be replaced with the proper SAP Marketing server host name.
	 */
	@Required
	public void setRootUrl(final String rootUrl)
	{
		LOG.debug("rootUrl={}", rootUrl);
		this.rootUrl = rootUrl.endsWith("/") ? rootUrl : rootUrl.concat("/"); // Always append '/'.
	}

	/**
	 * @param sapClient
	 *           the sapClient to set
	 */
	public void setSapClient(final String sapClient)
	{
		LOG.debug("sapClient={}", sapClient);
		this.sapClient = sapClient;
	}

	/**
	 * @param user
	 *           Username used to login at the {@link #setRootUrl(String)} address.
	 */
	public void setUser(final String user)
	{
		this.user = user;
	}
}
