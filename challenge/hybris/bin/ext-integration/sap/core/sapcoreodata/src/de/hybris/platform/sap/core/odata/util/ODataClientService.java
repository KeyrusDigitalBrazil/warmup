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
package de.hybris.platform.sap.core.odata.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.olingo.odata2.api.ODataCallback;
import org.apache.olingo.odata2.api.commons.HttpStatusCodes;
import org.apache.olingo.odata2.api.edm.Edm;
import org.apache.olingo.odata2.api.edm.EdmEntityContainer;
import org.apache.olingo.odata2.api.edm.EdmEntitySet;
import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.ep.EntityProvider;
import org.apache.olingo.odata2.api.ep.EntityProviderException;
import org.apache.olingo.odata2.api.ep.EntityProviderReadProperties;
import org.apache.olingo.odata2.api.ep.EntityProviderWriteProperties;
import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.apache.olingo.odata2.api.ep.feed.ODataFeed;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.apache.olingo.odata2.api.processor.ODataResponse;
import org.apache.olingo.odata2.api.uri.ExpandSelectTreeNode;

/**
 * Establish HTTP Connection and read an ODataEntry or an ODataFeed. This is based on the Apache Olingo
 * @deprecated Since 6.4, replace with extension sapymktcommon
 */
@Deprecated
public class ODataClientService
{
	private static final char SEPARATOR = '/';

	private static final String HTTP_METHOD_PUT = "PUT";
	private static final String HTTP_METHOD_POST = "POST";
	private static final String HTTP_METHOD_GET = "GET";
	private static final String HTTP_METHOD_HEAD = "HEAD";
	private static final String HTTP_HEADER_CONTENT_TYPE = "Content-Type";
	private static final String HTTP_HEADER_ACCEPT = "Accept";
	private static final String COOKIE = "cookie";

	private static final String APPLICATION_XML = "application/xml";
	private int connectionTimeout;
	private int readTimeout;

	private static final Logger LOG = Logger.getLogger(ODataClientService.class.getName());

	private HttpURLConnection initializeConnection(final String absoluteUri, final String contentType, final String httpMethod,
			final String user, final String password) throws IOException
	{
		final URL url = new URL(absoluteUri + "?saml2=disabled");

		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		
		connection.setRequestProperty(HTTP_HEADER_ACCEPT, contentType);
		connection.setRequestMethod(httpMethod); // throws
		connection.setConnectTimeout(connectionTimeout);
		connection.setReadTimeout(readTimeout);
	
		if (HTTP_METHOD_POST.equals(httpMethod) || HTTP_METHOD_PUT.equals(httpMethod))
		{
			connection.setDoOutput(true);
			connection.setRequestProperty(HTTP_HEADER_CONTENT_TYPE, contentType);
		}

		if (user != null)
		{
			final String authorization = "Basic " + new String(Base64.encodeBase64((user + ":" + password).getBytes()));
			connection.setRequestProperty("Authorization", authorization);
		}
		return connection;
	}

	private InputStream execute(final String relativeUri, final String contentType, final String httpMethod, final String user,
			final String password) throws IOException
	{
		final HttpURLConnection connection = initializeConnection(relativeUri, contentType, httpMethod, user, password);

		connection.connect();

		final HttpStatusCodes httpStatusCode = HttpStatusCodes.fromStatusCode(connection.getResponseCode());
		if (400 <= httpStatusCode.getStatusCode() && httpStatusCode.getStatusCode() <= 599)
		{
			throw new IOException("Http Connection failed with status " + httpStatusCode.getStatusCode() + " "
					+ httpStatusCode);
		}
		return connection.getInputStream();
	}

	/**
	 * Read a single entry (ODataEntry)
	 *
	 * @param serviceUri
	 * @param contentType
	 * @param entitySetName
	 * @param select
	 * @param filter
	 * @param expand
	 * @param keyValue
	 * @param user
	 * @param password
	 * @param client
	 * @return ODE
	 * @throws ODataException
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	public ODataEntry readEntry(final String serviceUri, final String contentType, final String entitySetName,
			final String select, final String filter, final String expand, final String keyValue, final String user,
			final String password, final String client) throws ODataException, URISyntaxException, IOException
	{
		final String absoluteUri = this.createUri(serviceUri, entitySetName, keyValue, expand, select, filter, null, client);
		final Edm edm = this.readEdm(serviceUri, user, password);
		final EdmEntityContainer entityContainer = edm.getDefaultEntityContainer();
		try(InputStream content = execute(absoluteUri, APPLICATION_XML, HTTP_METHOD_GET, user, password)){
			return EntityProvider.readEntry(contentType, entityContainer.getEntitySet(entitySetName), content,
					EntityProviderReadProperties.init().build());
		}
	}

	/**
	 * Read a data feed (ODataFeed)
	 *
	 * @param serviceUri
	 * @param contentType
	 * @param entitySetName
	 * @param user
	 * @param password
	 * @param client
	 * @return feed
	 * @throws ODataException
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	public ODataFeed readFeed(final String serviceUri, final String contentType, final String entitySetName, final String user,
			final String password, final String client) throws ODataException, URISyntaxException, IOException
	{
		return readFeed(serviceUri, contentType, entitySetName, null, null, null, null, user, password, client);
	}

	/**
	 * Read a data feed (ODataFeed)
	 *
	 * @param serviceUri
	 * @param contentType
	 * @param entitySetName
	 * @param expand
	 * @param select
	 * @param filter
	 * @param orderby
	 * @param user
	 * @param password
	 * @param client
	 * @return oDF
	 * @throws ODataException
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	public ODataFeed readFeed(final String serviceUri, final String contentType, final String entitySetName, final String expand,
			final String select, final String filter, final String orderby, final String user, final String password,
			final String client) throws ODataException, URISyntaxException, IOException
	{
		final String absoluteUri = createUri(serviceUri, entitySetName, null, expand, select, filter, orderby, client);
		final Edm edm = this.readEdm(serviceUri, user, password);
		final EdmEntityContainer entityContainer = edm.getDefaultEntityContainer();
		try(InputStream content = execute(absoluteUri, contentType, HTTP_METHOD_GET, user, password)){
   		return EntityProvider.readFeed(contentType, entityContainer.getEntitySet(entitySetName), content,
   				EntityProviderReadProperties.init().build());
		}
	}

	private Edm readEdm(final String serviceUrl, final String user, final String password) throws ODataException, IOException
	{
		try(InputStream content = execute(serviceUrl + SEPARATOR + "$metadata", APPLICATION_XML, HTTP_METHOD_GET, user, password))
		{
			return EntityProvider.readMetadata(content, false);
		}
	}

	private String createUri(final String serviceUri, final String entitySetName, final String id, final String expand,
			final String select, final String filter, final String orderby, final String client) throws URISyntaxException
	{
		UriBuilder uriBuilder = UriBuilder.serviceUri(serviceUri, entitySetName, id);
		uriBuilder.addQuery("$expand", expand);
		uriBuilder.addQuery("$select", select);
		uriBuilder.addQuery("$filter", filter);
		uriBuilder.addQuery("$orderby", orderby);
		// if a sap client has been specified in the HTTP Destination URL in backoffice
		uriBuilder.addQuery("sap-client", client);
		uriBuilder.addQuery("saml2", "disabled");
		return new URI(null, uriBuilder.build(), null).toASCIIString();
	}

	/**
	 * UriBuilder Class
	 *
	 */
	private static class UriBuilder
	{
		private final StringBuilder uri;
		private final StringBuilder query;

		private UriBuilder(final String serviceUri, final String entitySetName)
		{
			this.uri = new StringBuilder(serviceUri).append(SEPARATOR).append(entitySetName);
			this.query = new StringBuilder();
		}

		public static UriBuilder serviceUri(final String serviceUri, final String entitySetName, final String id)
		{
			final UriBuilder b = new UriBuilder(serviceUri, entitySetName);
			if(StringUtils.isNotEmpty(id))
			{
				b.uri.append("(").append(id).append(")");
			}
			return b;
		}

		public void addQuery(final String queryParameter, final String value)
		{
			if (StringUtils.isNotEmpty(value))
			{
				query.append(query.length() == 0 ? "/?" : "&").append(queryParameter).append("=").append(value);
			}
		}

		public String build()
		{
			return uri.append(query).toString();
		}
	}

	/**
	 * Perform a deep entity insert operation
	 * 
	 * @param serviceUri
	 * @param entitySetName
	 * @param data
	 * @param contentType
	 * @param httpMethod
	 * @param user
	 * @param password
	 * @param client
	 * @param entities
	 * @param headerValues
	 * @param myCallback
	 * @return ODataEntry
	 * @throws EdmException
	 * @throws IOException
	 * @throws EntityProviderException
	 * @throws URISyntaxException
	 */
	@SuppressWarnings("unchecked")
	public ODataEntry writeEntity(final String serviceUri, final String entitySetName, final Map<String, Object> data,
			final String contentType, final String httpMethod, final String user, final String password, final String client,
			final List<String> entities, final Map<String, Object> headerValues, final MyCallback myCallback) throws EdmException,
			IOException, EntityProviderException, URISyntaxException
	{
		final String absoluteUri = this.createUri(serviceUri, entitySetName, null, null, null, null, null, client);
		
		final EdmEntitySet entitySet = ((Edm) headerValues.get("edm")).getDefaultEntityContainer().getEntitySet(entitySetName);

		final URI serviceRoot = URI.create(serviceUri + "?saml2=disabled");

		final DataStore dataStore = new DataStore(data);
		final Map<String, ODataCallback> callbacks = new HashMap<String, ODataCallback>();
		myCallback.setDataStore(dataStore);
		myCallback.setServiceRoot(serviceRoot);
		for (final String entity : entities)
		{
			callbacks.put(entity, myCallback);
		}

		final ExpandSelectTreeNode expandSelectTreeNode = ExpandSelectTreeNode.entitySet(entitySet)
				.expandedLinks(entities).selectedProperties(Collections.emptyList()).build();

		final EntityProviderWriteProperties properties = EntityProviderWriteProperties.serviceRoot(serviceRoot)
				.omitJsonWrapper(true).responsePayload(false).expandSelectTree(expandSelectTreeNode).callbacks(callbacks).build();

		// serialize data into ODataResponse object
		final ODataResponse response = EntityProvider.writeEntry(contentType, entitySet, data, properties);
		final ByteArrayOutputStream array = bufferStream((InputStream) response.getEntity());

		final String csrfToken = (String) headerValues.get("csrfToken");

		final List<String> cookies = (List<String>) headerValues.get(COOKIE);

		final HttpURLConnection connection = initializeBatchConnection(absoluteUri, contentType, httpMethod, user, password,
				csrfToken, cookies);
		 
		try
		{
			try(OutputStream outputStream = connection.getOutputStream()){
   			outputStream.write(array.toByteArray());
			}
   		final HttpStatusCodes statusCode = HttpStatusCodes.fromStatusCode(connection.getResponseCode());
   		if (statusCode == HttpStatusCodes.CREATED)
   		{  
   			try(InputStream content = connection.getInputStream()){
      			final ByteArrayOutputStream byteArrayOutputStream = bufferStream(content);
        			final InputStream inputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
      			return EntityProvider.readEntry(contentType, entitySet, inputStream, EntityProviderReadProperties.init().build());
   			}
   		}
		}
		finally
		{
			connection.disconnect();
		}
   	return null;
	}
	
	/**
	 * Perform a POST using a URL with parameters, without a payload
	 * (useful for Function Imports)
	 * 
	 * @param urlWithParams
	 * @param contentType
	 * @param entitySetName
	 * @param httpMethod
	 * @param user
	 * @param password
	 * @param client
	 * @param headerValues
	 * @return
	 * @throws URISyntaxException
	 * @throws IOException
	 * @throws ODataException
	 */
	@SuppressWarnings("unchecked")
	public boolean post(String urlWithParams, String contentType, String entitySetName, String httpMethod, String user, 
			String password, String client,Map<String, Object> headerValues) throws URISyntaxException, IOException, ODataException
	{
		String csrfToken = null;
		if (headerValues.containsKey("csrfToken") && headerValues.get("csrfToken") != null)
		{
			csrfToken = headerValues.get("csrfToken").toString();
		}
		
		List<String> cookies = null;
		if (headerValues.containsKey(COOKIE) && headerValues.get(COOKIE) != null)
      {
            cookies = (List<String>) headerValues.get(COOKIE);
      }
	   
	   final HttpURLConnection connection = initializeBatchConnection(urlWithParams + "&saml2=disabled", contentType, httpMethod, user, password,
				csrfToken, cookies);
	      
		HttpStatusCodes statusCode = HttpStatusCodes.fromStatusCode(connection.getResponseCode());
		return statusCode == HttpStatusCodes.OK;
   }

	protected ByteArrayOutputStream bufferStream(final InputStream stream)
	{
		final byte[] buffer = new byte[16384];
		int bytesRead;
		try (final ByteArrayOutputStream output = new ByteArrayOutputStream())
		{
			while ((bytesRead = stream.read(buffer)) != -1)
			{
				output.write(buffer, 0, bytesRead);
			}
			return output;
		}
		catch(IOException e)
		{
			LOG.error("Error occurred while reading stream", e);
			return null;
		}
		
	}

	/**
	 * @param serviceUri
	 * @param contentType
	 * @param httpMethod
	 * @param user
	 * @param password
	 * @return headerValues
	 * @throws IOException 
	 */
	public Map<String, Object> getCSRFAndCookie(final String serviceUri, final String contentType, final String httpMethod,
			final String user, final String password)
	{

		final Map<String, Object> headerValues = new HashMap<String, Object>();
		String csrfToken = null;
		List<String> cookies = null;
		HttpURLConnection connection = null;

		try
		{
			connection = initializeConnection(serviceUri + "/$metadata", contentType, httpMethod, user, password);
			connection.setRequestProperty("x-csrf-token", "fetch");
			connection.connect();

			try(final InputStream content = connection.getInputStream()){
				Edm edm = EntityProvider.readMetadata(content, false);
				headerValues.put("edm", edm);
			}

			if (HttpURLConnection.HTTP_OK == connection.getResponseCode())
			{
				csrfToken = connection.getHeaderField("x-csrf-token");
				cookies = connection.getHeaderFields().entrySet().stream() //
						.filter(e -> "Set-Cookie".equalsIgnoreCase(e.getKey())) //
						.map(Entry::getValue) //
						.flatMap(List::stream) //
						.collect(Collectors.toList());

				if (cookies.isEmpty())
				{
					LOG.warn("No cookies retrieved while reading metadata");
				}
			}
			connection.disconnect();
		}
		catch (final IOException | EntityProviderException e)
		{
			LOG.error("Error occurred while getting CSRF token and cookie", e);
		}
		finally
		{
			if (connection != null)
			{
				connection.disconnect();
			}
		}
		headerValues.put("csrfToken", csrfToken);
		headerValues.put(COOKIE, cookies);
		return headerValues;
	}

	private HttpsURLConnection initializeBatchConnection(final String absoluteUri, final String contentType,
			final String httpMethod, final String user, final String password, final String csrfToken, final List<String> cookies)
			throws IOException
	{
		final HttpsURLConnection connection = (HttpsURLConnection) new URL(absoluteUri).openConnection();
		connection.setRequestProperty(HTTP_HEADER_ACCEPT, contentType);
		connection.setRequestProperty("x-requested-with", "xmlhttprequest");
		connection.setRequestMethod(httpMethod);
		connection.setConnectTimeout(connectionTimeout);
		connection.setReadTimeout(readTimeout);

		if (httpMethod.equals(HTTP_METHOD_POST) || httpMethod.equals(HTTP_METHOD_PUT))
		{
			connection.setDoOutput(true);
			connection.setRequestProperty(HTTP_HEADER_CONTENT_TYPE, contentType);
		}

		connection.setRequestProperty("x-csrf-token", csrfToken);
		for (String ncookie : cookies) 
      {
          connection.addRequestProperty("Cookie", ncookie.split(";", 2)[0]);
      }
		return connection;
	}

	/**
	 * @return connectionTimeout
	 */
	public int getConnectionTimeout()
	{
		return connectionTimeout;
	}

	/**
	 * @param connectionTimeout
	 */
	public void setConnectionTimeout(final int connectionTimeout)
	{
		this.connectionTimeout = connectionTimeout;
	}

	/**
	 * @return readTimeout
	 */
	public int getReadTimeout()
	{
		return readTimeout;
	}

	/**
	 * @param readTimeout
	 */
	public void setReadTimeout(final int readTimeout)
	{
		this.readTimeout = readTimeout;
	}

	/**
	 * Check the backend system is responding
	 * 
	 * @param absoluteUri
	 * @param applicationJson
	 * @param httpMethodHead
	 * @param user
	 * @param password
	 * @return
	 */
	public boolean pingRemoteSystem(String absoluteUri, String applicationJson, String httpMethodHead, String user, String password)
	{
		try
		{
		    HttpURLConnection connection = this.initializeConnection(absoluteUri, APPLICATION_XML, HTTP_METHOD_HEAD, user, password);
		    return connection.getContentLength() > 0;	
		}
		catch(Exception e)
		{
			LOG.error("Error checking backend system status: " + e.getMessage(), e);
			return false;
		}
	}

}
