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
package de.hybris.platform.commerceservices.search.solrfacetsearch.provider.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.product.ProductConfigurableChecker;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.provider.FieldNameProvider;
import de.hybris.platform.solrfacetsearch.provider.FieldValue;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class ProductConfiguratorTypeProviderTest
{
	private static final String FIELD_NAME = "configuratorType";
	private static final String CONFIGURATOR_TYPE = "my configurator";
	private final ProductConfiguratorTypeProvider classUnderTest = new ProductConfiguratorTypeProvider();
	@Mock
	private IndexConfig indexConfig;
	@Mock
	private IndexedProperty indexedProperty;
	@Mock
	private ProductModel productModel;
	@Mock
	private ProductConfigurableChecker productConfigurableChecker;
	@Mock
	private FieldNameProvider fieldNameProvider;
	@Mock
	private CartModel cartModel;

	@Before
	public void initialize()
	{
		MockitoAnnotations.initMocks(this);
		Mockito.when(fieldNameProvider.getFieldNames(indexedProperty, null)).thenReturn(Arrays.asList(FIELD_NAME));
		Mockito.when(productConfigurableChecker.getFirstConfiguratorType(Mockito.any())).thenReturn(CONFIGURATOR_TYPE);
		classUnderTest.setProductConfigurableChecker(productConfigurableChecker);
		classUnderTest.setFieldNameProvider(fieldNameProvider);
	}

	@Test
	public void testGetFieldValues() throws FieldValueProviderException
	{
		final Collection<FieldValue> fieldValues = classUnderTest.getFieldValues(indexConfig, indexedProperty, productModel);
		assertNotNull(fieldValues);
		assertEquals(1, fieldValues.size());
		final FieldValue fieldValue = fieldValues.iterator().next();
		assertEquals(FIELD_NAME, fieldValue.getFieldName());
		assertEquals(CONFIGURATOR_TYPE, fieldValue.getValue());
	}

	@Test(expected = FieldValueProviderException.class)
	public void testGetFieldValuesWrongModel() throws FieldValueProviderException
	{
		classUnderTest.getFieldValues(indexConfig, indexedProperty, cartModel);
	}

	@Test(expected = FieldValueProviderException.class)
	public void testGetFieldValuesNoModelAtAll() throws FieldValueProviderException
	{
		classUnderTest.getFieldValues(indexConfig, indexedProperty, CONFIGURATOR_TYPE);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetFieldValuesNull() throws FieldValueProviderException
	{
		classUnderTest.getFieldValues(indexConfig, indexedProperty, null);
	}

}
