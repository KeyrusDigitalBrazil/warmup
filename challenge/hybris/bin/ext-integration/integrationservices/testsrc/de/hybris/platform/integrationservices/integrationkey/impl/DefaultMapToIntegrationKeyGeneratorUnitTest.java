/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.integrationservices.integrationkey.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.integrationservices.integrationkey.IntegrationKeyMetadataGenerator;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemAttributeModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultMapToIntegrationKeyGeneratorUnitTest
{
	private static final String PRODUCT_ENTITY_NAME = "Product";
	private static final String PRODUCT_CODE_PROPERTY = "code";
	private static final String PRODUCT_NAME_PROPERTY = "name";
	private static final String PRODUCT_CATALOGVERSION_PROPERTY = "catalogVersion";
	private static final String PRODUCT_CODE_VALUE = "product|code|value";

	private static final String CATALOGVERSION_ENTITY_NAME = "CatalogVersion";
	private static final String CATALOGVERSION_VERSION_PROPERTY = "version";
	private static final String CATALOGVERSION_CATALOG_PROPERTY = "catalog";

	private static final String CATALOG_ENTITY_NAME = "Catalog";
	private static final String CATALOG_ID_PROPERTY = "id";

	@Mock
	private IntegrationKeyMetadataGenerator integrationKeyMetadataGenerator;
	@InjectMocks
	private DefaultMapToIntegrationKeyGenerator<IntegrationObjectItemModel, Map<String, Object>> integrationKeyGenerator;

	@Before
	public void setUp()
	{
		integrationKeyGenerator.setIntegrationKeyMetadataGenerator(integrationKeyMetadataGenerator);
		integrationKeyGenerator.setEncoding("UTF-8");
	}

	@Test
	public void testCalculateIntegrationKeyForNullType()
	{
		assertThatThrownBy(() -> integrationKeyGenerator.generate(null, Maps.newHashMap()))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void testCalculateIntegrationKeyForNullEntry()
	{
		assertThatThrownBy(() -> integrationKeyGenerator.generate(mock(IntegrationObjectItemModel.class), null))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void testCalculateIntegrationKeyValueKeyWhenGivenEntityWithSimpleKey()
	{
		final IntegrationObjectItemModel type = givenTypeWithKey(PRODUCT_ENTITY_NAME, aliasReferenceFor(PRODUCT_ENTITY_NAME, PRODUCT_CODE_PROPERTY));
		final Map<String, Object> entry = ImmutableMap.of(PRODUCT_CODE_PROPERTY, PRODUCT_CODE_VALUE);

		assertThat(integrationKeyGenerator.generate(type, entry))
				.isEqualTo(encode(PRODUCT_CODE_VALUE));
	}

	@Test
	public void testCalculateIntegrationKeyWhenGivenEntityWithSimpleKey_date()
	{
		final Date now = new Date();
		final IntegrationObjectItemModel type = givenTypeWithKey(PRODUCT_ENTITY_NAME, aliasReferenceFor(PRODUCT_ENTITY_NAME, "date"));
		final Map<String, Object> entry = ImmutableMap.of("date", "/Date(" + now.getTime() + ")/");

		assertThat(integrationKeyGenerator.generate(type, entry))
				.isEqualTo(String.valueOf(now.getTime()));
	}

	@Test
	public void testCalculateIntegrationKeyWhenGivenEntityWithSimpleKey_encodeProblem() throws UnsupportedEncodingException
	{
		integrationKeyGenerator.setEncoding("SOME_WEIRD_ENCODING");

		final IntegrationObjectItemModel type = givenTypeWithKey(PRODUCT_ENTITY_NAME, aliasReferenceFor(PRODUCT_ENTITY_NAME, PRODUCT_CODE_PROPERTY));
		final Map<String, Object> entry = ImmutableMap.of(PRODUCT_CODE_PROPERTY, PRODUCT_CODE_VALUE);

		assertThat(integrationKeyGenerator.generate(type, entry))
				.isEqualTo(PRODUCT_CODE_VALUE);
	}

	@Test
	public void testCalculateIntegrationKeyWhenGivenEntityWithTwoSimpleKeys()
	{
		final String productNameValue = "name|Value";

		final IntegrationObjectItemModel type = givenTypeWithKey(PRODUCT_ENTITY_NAME, aliasReferenceFor(PRODUCT_ENTITY_NAME, PRODUCT_CODE_PROPERTY) + "|" + aliasReferenceFor(PRODUCT_ENTITY_NAME, PRODUCT_NAME_PROPERTY));
		final Map<String, Object> entry = ImmutableMap.of(PRODUCT_CODE_PROPERTY, PRODUCT_CODE_VALUE, PRODUCT_NAME_PROPERTY, productNameValue);

		assertThat(integrationKeyGenerator.generate(type, entry))
				.isEqualTo(encode(PRODUCT_CODE_VALUE) + "|" + encode(productNameValue));
	}

	@Test
	public void testCalculateIntegrationKeyWhenGivenEntityWithSimpleKeyAndNavigationKey()
	{
		final String catalogCodeValue = "the|catalog|Value";
		final String productCodeValue = "some|product|code";

		final IntegrationObjectItemModel catalogVersionType =
				givenTypeWithKey(CATALOGVERSION_ENTITY_NAME, aliasReferenceFor(CATALOGVERSION_ENTITY_NAME, CATALOGVERSION_VERSION_PROPERTY));
		final IntegrationObjectItemModel productType =
				givenTypeWithKey(PRODUCT_ENTITY_NAME, aliasReferenceFor(PRODUCT_ENTITY_NAME, PRODUCT_CODE_PROPERTY) + "|" + aliasReferenceFor(CATALOGVERSION_ENTITY_NAME, CATALOGVERSION_VERSION_PROPERTY));

		final Map<String, Object> catalogVersionEntry = ImmutableMap.of(CATALOGVERSION_VERSION_PROPERTY, catalogCodeValue);
		final Map<String, Object> productEntry = ImmutableMap.of(PRODUCT_CODE_PROPERTY, productCodeValue, PRODUCT_CATALOGVERSION_PROPERTY, catalogVersionEntry);

		givenAttributeForType(productType, PRODUCT_CATALOGVERSION_PROPERTY, catalogVersionType);

		assertThat(integrationKeyGenerator.generate(productType, productEntry))
				.isEqualTo(encode(productCodeValue) + "|" + encode(catalogCodeValue));
	}

	@Test
	public void testCalculateIntegrationKeyWhenGivenEntityWithNullValues()
	{
		final String catalogCodeValue = null;
		final String productCodeValue = "some|product|code";

		final IntegrationObjectItemModel catalogVersionType =
				givenTypeWithKey(CATALOGVERSION_ENTITY_NAME, aliasReferenceFor(CATALOGVERSION_ENTITY_NAME, CATALOGVERSION_VERSION_PROPERTY));
		final IntegrationObjectItemModel productType =
				givenTypeWithKey(PRODUCT_ENTITY_NAME, aliasReferenceFor(PRODUCT_ENTITY_NAME, PRODUCT_CODE_PROPERTY) + "|" + aliasReferenceFor(CATALOGVERSION_ENTITY_NAME, CATALOGVERSION_VERSION_PROPERTY));

		final Map<String, Object> catalogVersionEntry = new HashMap<>();
		catalogVersionEntry.put(CATALOGVERSION_VERSION_PROPERTY, catalogCodeValue);
		final Map<String, Object> productEntry =  new HashMap<>();
		productEntry.put(PRODUCT_CODE_PROPERTY, productCodeValue);
		productEntry.put(PRODUCT_CATALOGVERSION_PROPERTY, catalogVersionEntry);

		givenAttributeForType(productType, PRODUCT_CATALOGVERSION_PROPERTY, catalogVersionType);

		assertThat(integrationKeyGenerator.generate(productType, productEntry))
				.isEqualTo(encode(productCodeValue) + "|" + "null");
	}

	@Test
	public void testCalculateIntegrationKeyWhenItemElementsStructuredInDifferentOrderThanKeyElements()
	{
		final String catalogVersionVersionValue = "the|catalogVersion|value";
		final String catalogIdValue = "the|catalogId|value";

		final IntegrationObjectItemModel catalogVersionType = givenTypeWithKey(CATALOGVERSION_ENTITY_NAME, aliasReferenceFor(CATALOGVERSION_ENTITY_NAME, CATALOGVERSION_VERSION_PROPERTY));
		final IntegrationObjectItemModel productType = givenTypeWithKey(PRODUCT_ENTITY_NAME, aliasReferenceFor(PRODUCT_ENTITY_NAME, PRODUCT_CODE_PROPERTY) + "|" + aliasReferenceFor(CATALOG_ENTITY_NAME, CATALOG_ID_PROPERTY) + "|" + aliasReferenceFor(CATALOGVERSION_ENTITY_NAME, CATALOGVERSION_VERSION_PROPERTY));
		final IntegrationObjectItemModel catalogType = givenTypeWithKey(CATALOG_ENTITY_NAME, aliasReferenceFor(CATALOG_ENTITY_NAME, CATALOG_ID_PROPERTY));

		final Map<String, Object> catalogEntry = ImmutableMap.of(CATALOG_ID_PROPERTY, catalogIdValue);
		final Map<String, Object> catalogVersionEntry = ImmutableMap.of(CATALOGVERSION_VERSION_PROPERTY, catalogVersionVersionValue, CATALOGVERSION_CATALOG_PROPERTY, catalogEntry);
		final Map<String, Object> productEntry = ImmutableMap.of(PRODUCT_CODE_PROPERTY, PRODUCT_CODE_VALUE, PRODUCT_CATALOGVERSION_PROPERTY, catalogVersionEntry);

		givenAttributeForType(productType, PRODUCT_CATALOGVERSION_PROPERTY, catalogVersionType);
		givenAttributeForType(catalogVersionType, CATALOGVERSION_CATALOG_PROPERTY, catalogType);

		assertThat(integrationKeyGenerator.generate(productType, productEntry))
				.isEqualTo(encode(PRODUCT_CODE_VALUE) + "|" + encode(catalogIdValue) + "|" + encode(catalogVersionVersionValue));
	}

	private String aliasReferenceFor(final String entityName, final String propertyName)
	{
		return entityName + "_" + propertyName;
	}

	private IntegrationObjectItemModel givenTypeWithKey(final String typeName, final String aliasString)
	{
		final IntegrationObjectItemModel type = mock(IntegrationObjectItemModel.class);
		when(type.getCode()).thenReturn(typeName);

		when(integrationKeyMetadataGenerator.generateKeyMetadata(type)).thenReturn(aliasString);
		return type;
	}

	private void givenAttributeForType(final IntegrationObjectItemModel type, final String attributeName, final IntegrationObjectItemModel value)
	{
		Set<IntegrationObjectItemAttributeModel> attributes = type.getAttributes();
		if (attributes == null || attributes.isEmpty())
		{
			attributes = Sets.newLinkedHashSet();
			when(type.getAttributes()).thenReturn(attributes);
		}
		final IntegrationObjectItemAttributeModel attributeModel = mock(IntegrationObjectItemAttributeModel.class);
		when(attributeModel.getAttributeName()).thenReturn(attributeName);
		when(attributeModel.getReturnIntegrationObjectItem()).thenReturn(value);
		attributes.add(attributeModel);
	}

	private static String encode(final String value)
	{
		try
		{
			return URLEncoder.encode(value, "UTF-8");
		}
		catch (final UnsupportedEncodingException e)
		{
			throw new RuntimeException(e);
		}
	}
}