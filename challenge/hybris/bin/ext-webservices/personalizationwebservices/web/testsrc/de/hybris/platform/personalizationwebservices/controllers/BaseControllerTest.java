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
package de.hybris.platform.personalizationwebservices.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.impex.impl.ClasspathImpExResource;

import java.net.URI;

import org.junit.Before;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.util.UriComponentsBuilder;


@IntegrationTest
@SuppressWarnings("rawtypes")
public abstract class BaseControllerTest extends ServicelayerTransactionalTest
{
	private static final String BASE_LOCATION = "http://test.local:9090/";

	protected static final String VERSION = "v1";
	protected static final String CATALOG = "testCatalog";
	protected static final String CATALOG_VERSION = "Online";

	private UriComponentsBuilder uriComponentsBuilder;

	@Before
	public void initialize() throws Exception
	{
		uriComponentsBuilder = new UriComponentBuilderStub();

		createCoreData();
		createDefaultCatalog();
		importData(new ClasspathImpExResource("/personalizationwebservices/test/webcontext_testdata.impex", "UTF-8"));
	}

	protected void assertLocationWithCatalog(final String expected, final ResponseEntity actual)
	{
		assertLocation(VERSION + "/catalogs/" + CATALOG + "/catalogVersions/" + CATALOG_VERSION + "/" + expected, actual);
	}

	protected void assertLocation(final String expected, final ResponseEntity actual)
	{
		final URI location = actual.getHeaders().getLocation();
		assertNotNull(location);
		assertEquals(BASE_LOCATION + expected, location.toString());
	}

	protected BindingResult getBindingResult(final Object dto)
	{
		return new BeanPropertyBindingResult(dto, "item");
	}

	protected UriComponentsBuilder getUriComponentsBuilder()
	{
		return uriComponentsBuilder;
	}

	protected void setUriComponentsBuilder(final UriComponentsBuilder uriComponentsBuilder)
	{
		this.uriComponentsBuilder = uriComponentsBuilder;
	}

}

