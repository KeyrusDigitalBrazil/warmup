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
package de.hybris.platform.cmswebservices.util;

import static de.hybris.platform.cmsfacades.util.models.CatalogVersionModelMother.CatalogVersion.STAGED;
import static de.hybris.platform.cmsfacades.util.models.ContentCatalogModelMother.CatalogTemplate.ID_APPLE;
import static de.hybris.platform.cmswebservices.constants.CmswebservicesConstants.URI_CATALOG_ID;
import static de.hybris.platform.cmswebservices.constants.CmswebservicesConstants.URI_SITE_ID;
import static de.hybris.platform.cmswebservices.constants.CmswebservicesConstants.URI_VERSION_ID;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.basecommerce.util.SpringCustomContextLoader;
import de.hybris.platform.cmsfacades.uniqueidentifier.EncodedItemComposedKey;
import de.hybris.platform.cmsfacades.util.models.SiteModelMother;
import de.hybris.platform.cmswebservices.constants.CmswebservicesConstants;
import de.hybris.platform.core.Registry;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.oauth2.constants.OAuth2Constants;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.webservicescommons.jaxb.Jaxb2HttpMessageConverter;
import de.hybris.platform.webservicescommons.testsupport.client.WsRequestBuilder;
import de.hybris.platform.webservicescommons.testsupport.client.WsSecuredRequestBuilder;
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.junit.Before;
import org.junit.Ignore;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.util.UriTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;


@Ignore("Just a testing base class.")
@NeedsEmbeddedServer(webExtensions =
{ CmswebservicesConstants.EXTENSIONNAME, OAuth2Constants.EXTENSIONNAME })
@IntegrationTest
@ContextConfiguration(locations =
{ "classpath:/cmswebservices-spring-test.xml" })
public class ApiBaseIntegrationTest extends ServicelayerTest
{
	public static final String OAUTH_CLIENT_ID = "trusted_client";
	public static final String OAUTH_CLIENT_PASS = "secret";
	public static final String OAUTH_INVALID_ID = "invalid";
	public static final String OAUTH_INVALID_PASS = "invalid";
	public static String OAUTH_CMSMANAGER_ID = "cmsmanager";
	public static String OAUTH_CMSMANAGER_PASS = "1234";
	public static String OAUTH_CMSTRANSLATOR_ID = "cmstranslator";
	public static String OAUTH_CMSTRANSLATOR_PASS = "1234";
	public static String OAUTH_CMSEDITOR_ID = "cmseditor";
	public static String OAUTH_CMSEDITOR_PASS = "1234";

	public static String OAUTH_MULTICOUNTRY_CMSMANAGER_ID = "multicountrycmsmanager";
	public static String OAUTH_MULTICOUNTRY_CMSMANAGER_PASS = "1234";

	protected static SpringCustomContextLoader springCustomContextLoader = null;

	@Resource(name = "cmsJsonHttpMessageConverter")
	private Jaxb2HttpMessageConverter jsonHttpMessageConverter;

	@Resource
	private ConfigurationService configurationService;

	public ApiBaseIntegrationTest()
	{
		if (springCustomContextLoader == null)
		{
			try
			{
				springCustomContextLoader = new SpringCustomContextLoader(getClass());
				springCustomContextLoader.loadApplicationContexts((GenericApplicationContext) Registry.getCoreApplicationContext());
				springCustomContextLoader
						.loadApplicationContextByConvention((GenericApplicationContext) Registry.getCoreApplicationContext());
			}
			catch (final Exception e)
			{
				throw new RuntimeException(e.getMessage(), e);
			}
		}
	}

	@Before
	public void setUpTestData() throws ImpExException
	{
		importCsv("/cmswebservices/test/impex/essentialTestDataAuth.impex", "utf-8");
		importCsv("/impex/essentialdata-applicable-restriction-types.impex", "utf-8");
	}

	/**
	 * To retrieve a new authenticated <code>WsSecuredRequestBuilder</code> for the CMS Manager user. The request
	 * supports multi-part form data.
	 *
	 * @return authenticated <code>WsSecuredRequestBuilder</code>
	 */
	protected WsSecuredRequestBuilder getCmsManagerWsSecuredRequestBuilder()
	{
		return getWsSecuredRequestBuilder(OAUTH_CMSMANAGER_ID, OAUTH_CMSMANAGER_PASS);
	}

	/**
	 * To retrieve a new authenticated <code>WsSecuredRequestBuilder</code> for the CMS Manager user. The request
	 * supports multi-part form data.
	 *
	 * @return authenticated <code>WsSecuredRequestBuilder</code>
	 */
	protected WsSecuredRequestBuilder getCmsTranslatorWsSecuredRequestBuilder()
	{
		return getWsSecuredRequestBuilder(OAUTH_CMSTRANSLATOR_ID, OAUTH_CMSTRANSLATOR_PASS);
	}

	/**
	 * To retrieve a new authenticated <code>WsSecuredRequestBuilder</code> for the CMS Editor user. The request supports
	 * multi-part form data.
	 *
	 * @return authenticated <code>WsSecuredRequestBuilder</code>
	 */
	protected WsSecuredRequestBuilder getCmsEditorWsSecuredRequestBuilder()
	{
		return getWsSecuredRequestBuilder(OAUTH_CMSEDITOR_ID, OAUTH_CMSEDITOR_PASS);
	}

	/**
	 * To retrieve a new authenticated <code>WsSecuredRequestBuilder</code> for the CMS Manager user in the multi country
	 * setup. The request supports multi-part form data.
	 *
	 * @return authenticated <code>WsSecuredRequestBuilder</code>
	 */
	protected WsSecuredRequestBuilder getMultiCountryCmsManagerWsSecuredRequestBuilder()
	{
		return getWsSecuredRequestBuilder(OAUTH_MULTICOUNTRY_CMSMANAGER_ID, OAUTH_MULTICOUNTRY_CMSMANAGER_PASS);
	}

	/**
	 * To retrieve a new unauthenticated <code>WsRequestBuilder</code>
	 *
	 * @return unauthenticated <code>WsRequestBuilder</code>
	 */
	protected WsRequestBuilder getWsRequestBuilder()
	{
		return new WsRequestBuilder() //
				.extensionName(CmswebservicesConstants.EXTENSIONNAME);
	}

	/**
	 * To retrieve a new authenticated <code>WsSecuredRequestBuilder</code> for the given username and password. The
	 * request supports multi-part form data.
	 *
	 * @return authenticated <code>WsSecuredRequestBuilder</code>
	 */
	protected WsSecuredRequestBuilder getWsSecuredRequestBuilder(final String username, final String password)
	{
		return new WsSecuredRequestBuilder() //
				.registerConfig(MultiPartFeature.class) // add support for multi-part form data
				.extensionName(CmswebservicesConstants.EXTENSIONNAME) //
				.client(OAUTH_CLIENT_ID, OAUTH_CLIENT_PASS) //
				.resourceOwner(username, password) //
				.grantResourceOwnerPasswordCredentials();
	}

	/**
	 * This is used to convert the given input object into a json object and to return the string representation of the
	 * json object.
	 *
	 * This is useful when dealing with objects extending an Abstract type and the controller's method definition is tied
	 * to the Abstract type. The configuration of the <code>jsonHttpMessageConverter</code> handles the actual object
	 * conversion logic.
	 *
	 * @param input
	 *           - the object to be converted to json
	 * @param classType
	 *           - the class type of the input object
	 * @return json string representation of the input object
	 * @throws JAXBException
	 */
	protected String marshallDto(final Object input, final Class<?> classType) throws JAXBException
	{
		final Marshaller marshaller = jsonHttpMessageConverter.createMarshaller(classType);
		final StringWriter writer = new StringWriter();
		marshaller.marshal(input, writer);
		return writer.toString();
	}

	/**
	 * Replace all placeholders in the URI with their respective values provided in the map of variables. When no values
	 * are provided for site, catalog and catalog version, the default values are: <br>
	 * <ul>
	 * <li>site: <code>electronics</code>
	 * <li>catalog: <code>Apple's Content Catalog</code>
	 * <li>catalog version: <code>staged</code>
	 * </ul>
	 *
	 * @param uri
	 *           - with placeholders to be replaced by values in the map of variables
	 * @param variables
	 *           - to replace placeholders in the URI
	 * @return URI where placeholders will be populated by the values contained in the map of variables
	 */
	protected String replaceUriVariablesWithDefaults(final String uri, final Map<String, String> variables)
	{
		if (!variables.containsKey(URI_CATALOG_ID))
		{
			variables.put(URI_CATALOG_ID, ID_APPLE.name());
		}

		if (!variables.containsKey(URI_VERSION_ID))
		{
			variables.put(URI_VERSION_ID, STAGED.getVersion());
		}

		if (!variables.containsKey(URI_SITE_ID))
		{
			variables.put(URI_SITE_ID, SiteModelMother.ELECTRONICS);
		}

		return new UriTemplate(uri).expand(variables).toASCIIString();
	}

	/**
	 * Convert a JSON string representing a key/value object, into a real java Map<String, Object>
	 *
	 * @param json
	 *           A JSON string representing a key/value object
	 * @return A map of string key, and object values
	 * @throws RuntimeException
	 *            if there is a serialization issue
	 */
	protected Map<String, Object> jsonToMap(final String json)
	{
		try
		{
			return new ObjectMapper().readValue(json, HashMap.class);
		}
		catch (final IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	/**
	 * Returns the unique identifier using the encoded compose key class. See more details here
	 * {@link EncodedItemComposedKey}.
	 *
	 * @param catalogId
	 *           the catalog id of the item model
	 * @param catalogVersion
	 *           the catalog version of the item model
	 * @param uid
	 *           the uid of the item model
	 * @return the encoded unique identifier.
	 * @see EncodedItemComposedKey
	 */
	protected String getUuid(final String catalogId, final String catalogVersion, final String uid)
	{
		final EncodedItemComposedKey itemComposedKey = new EncodedItemComposedKey();
		itemComposedKey.setCatalogId(catalogId);
		itemComposedKey.setCatalogVersion(catalogVersion);
		itemComposedKey.setItemId(uid);

		return itemComposedKey.toEncoded();
	}

}
