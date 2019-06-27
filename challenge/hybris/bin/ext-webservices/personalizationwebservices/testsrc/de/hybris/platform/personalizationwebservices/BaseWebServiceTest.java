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
package de.hybris.platform.personalizationwebservices;

import de.hybris.platform.oauth2.constants.OAuth2Constants;
import de.hybris.platform.personalizationwebservices.constants.PersonalizationwebservicesConstants;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.impex.impl.ClasspathImpExResource;
import de.hybris.platform.webservicescommons.jaxb.Jaxb2HttpMessageConverter;
import de.hybris.platform.webservicescommons.testsupport.client.WsRequestBuilder;
import de.hybris.platform.webservicescommons.testsupport.client.WsSecuredRequestBuilder;
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer;

import java.io.InputStream;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.Resource;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.junit.Assert;
import org.junit.Before;


@NeedsEmbeddedServer(webExtensions =
{ PersonalizationwebservicesConstants.EXTENSIONNAME, OAuth2Constants.EXTENSIONNAME })
public abstract class BaseWebServiceTest extends ServicelayerTest
{
	protected static final String VERSION = "/v1";
	protected static final String SEGMENT_ENDPOINT = "segments";
	protected static final String CUSTOMIZATION_ENDPOINT = "customizations";
	protected static final String VARIATION_ENDPOINT = "variations";
	protected static final String ACTION_ENDPOINT = "actions";
	protected static final String CUSTOMERSEGMENTATION_ENDPOINT = "customersegmentations";
	protected static final String TRIGGER_ENDPOINT = "triggers";

	protected static final String SEGMENT = "segment1";
	protected static final String CUSTOMIZATION = "customization1";
	protected static final String NONEXISTING_CUSTOMIZATION = "nonexistingcustomization";

	protected static final String VARIATION = "variation1";
	protected static final String NONEXISTING_VARIATION = "nonexistingvariation";

	protected static final String ACTION = "action1";
	protected static final String NON_EXISTINGACTION = "nonexistingaction";
	protected static final String TRIGGER = "trigger1";
	protected static final String CONTAINER = "container1";

	protected static final String INCORRECT_ID = "incorrectId";
	protected static final String FIELDS = "fields";

	protected static final String CMSMANAGER_USERNAME = "cxmanager";
	protected static final String CMSMANAGER_READ_ONLY_USERNAME = "cxmanagerreadonly";
	protected static final String CMSMANAGER_NOACCESS_USERNAME = "cxmanagernoaccess";
	protected static final String CMSMANAGER_PASSWORD = "12341234";

	protected static final String CUSTOMER_USERNAME = "customer1@hybris.com";
	protected static final String CUSTOMER_PASSWORD = "12341234";

	protected static final String ADMIN_USERNAME = "admin";
	protected static final String ADMIN_PASSWORD = "nimda";

	protected static final String CLIENT_ID = "mobile_android";
	protected static final String CLIENT_SECRET = "secret";

	@Resource(name = "jsonHttpMessageConverter")
	private Jaxb2HttpMessageConverter defaultJsonHttpMessageConverter;

	@Before
	public void setUp() throws Exception
	{
		createCoreData();
		createDefaultCatalog();
		importData(new ClasspathImpExResource("/personalizationwebservices/test/webcontext_testdata.impex", "UTF-8"));
	}

	public WsRequestBuilder getWsRequestBuilder()
	{
		return new WsRequestBuilder().extensionName(PersonalizationwebservicesConstants.EXTENSIONNAME);
	}


	public WsSecuredRequestBuilder getWsSecuredRequestBuilder(final String user, final String pwd)
	{
		return new WsSecuredRequestBuilder()//
				.extensionName(PersonalizationwebservicesConstants.EXTENSIONNAME)//
				.client(CLIENT_ID, CLIENT_SECRET)//
				.resourceOwner(user, pwd)//
				.grantResourceOwnerPasswordCredentials();
	}

	public WsSecuredRequestBuilder getWsSecuredRequestBuilderForCmsManager()
	{
		return new WsSecuredRequestBuilder()//
				.extensionName(PersonalizationwebservicesConstants.EXTENSIONNAME)//
				.client(CLIENT_ID, CLIENT_SECRET)//
				.resourceOwner(CMSMANAGER_USERNAME, CMSMANAGER_PASSWORD)//
				.grantResourceOwnerPasswordCredentials();
	}


	public WsSecuredRequestBuilder getWsSecuredRequestBuilderForAdmin()
	{
		return new WsSecuredRequestBuilder()//
				.extensionName(PersonalizationwebservicesConstants.EXTENSIONNAME)//
				.client(CLIENT_ID, CLIENT_SECRET)//
				.resourceOwner(ADMIN_USERNAME, ADMIN_PASSWORD)//
				.grantResourceOwnerPasswordCredentials();
	}

	public WsSecuredRequestBuilder getWsSecuredRequestBuilderForCustomer()
	{
		return new WsSecuredRequestBuilder()//
				.extensionName(PersonalizationwebservicesConstants.EXTENSIONNAME)//
				.client(CLIENT_ID, CLIENT_SECRET)//
				.resourceOwner(CUSTOMER_USERNAME, CUSTOMER_PASSWORD)//
				.grantResourceOwnerPasswordCredentials();
	}

	public <C> C unmarshallResult(final Response result, final Class<C> c) throws JAXBException
	{

		final Unmarshaller unmarshaller = defaultJsonHttpMessageConverter.createUnmarshaller(c);
		final StreamSource source = new StreamSource(result.readEntity(InputStream.class));
		final C entity = unmarshaller.unmarshal(source, c).getValue();
		return entity;
	}

	public String marshallDto(final Object input, final Class<?> c) throws JAXBException
	{
		final Marshaller marshaller = defaultJsonHttpMessageConverter.createMarshaller(c);
		final StringWriter writer = new StringWriter();
		marshaller.marshal(input, writer);
		return writer.toString();
	}

	public void assertDateTimeAlmostEqual(final Date expected, final Date actual)
	{
		assertDateTimeAlmostEqual(null, expected, actual);
	}

	public void assertDateTimeAlmostEqual(final String message, final Date expected, final Date actual)
	{
		final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
		final String e = f.format(expected);
		final String a = f.format(actual);
		Assert.assertEquals(message, e, a);
	}


}
