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
package de.hybris.platform.odata2services.odata.integrationkey.impl;

import static de.hybris.platform.odata2services.constants.Odata2servicesConstants.ALIAS_ANNOTATION_ATTR_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.integrationservices.integrationkey.IntegrationKeyCalculationException;
import de.hybris.platform.odata2services.odata.persistence.InvalidDataException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.olingo.odata2.api.edm.EdmAnnotationAttribute;
import org.apache.olingo.odata2.api.edm.EdmAnnotations;
import org.apache.olingo.odata2.api.edm.EdmEntitySet;
import org.apache.olingo.odata2.api.edm.EdmEntityType;
import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.edm.EdmNavigationProperty;
import org.apache.olingo.odata2.api.edm.EdmProperty;
import org.apache.olingo.odata2.api.edm.provider.AnnotationAttribute;
import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.apache.olingo.odata2.core.edm.provider.EdmSimplePropertyImplProv;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.ImmutableMap;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultODataEntryToIntegrationKeyGeneratorUnitTest
{
	private static final String PRODUCT_ENTITY_NAME = "Product";
	private static final String PRODUCT_CODE_PROPERTY = "code";
	private static final String PRODUCT_NAME_PROPERTY = "name";
	private static final String PRODUCT_CODE_VALUE = "product|code|value";

	private static final String CATALOGVERSION_ENTITY_NAME = "CatalogVersion";
	private static final String CATALOGVERSION_VERSION_PROPERTY = "version";
	private static final String CATALOGVERSION_CATALOG_PROPERTY = "catalog";

	private static final String CATALOG_ENTITY_NAME = "Catalog";
	private static final String CATALOG_ID_PROPERTY = "id";

	@InjectMocks
	private DefaultODataEntryToIntegrationKeyGenerator integrationKeyGenerator;

	@Before
	public void setUp()
	{
		integrationKeyGenerator.setEncoding("UTF-8");
	}

	@Test
	public void testCalculateIntegrationKeyForNullEntitySet()
	{
		assertThatThrownBy(() -> integrationKeyGenerator.generate(null, mock(ODataEntry.class)))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Cannot calculate integration key value for null edm entity set");
	}

	@Test
	public void testCalculateIntegrationKeyForNullODataEntry() throws EdmException
	{
		final EdmEntitySet productEntitySet = givenEntitySetForTypeWithKey(PRODUCT_ENTITY_NAME, aliasReferenceFor(PRODUCT_ENTITY_NAME, PRODUCT_CODE_PROPERTY));
		assertThatThrownBy(() -> integrationKeyGenerator.generate(productEntitySet, null))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Cannot calculate integration key value for null oDataEntry");
	}

	@Test
	public void testCalculateIntegrationKeyForNullEntitySetEntityType()
	{
		assertThatThrownBy(() -> integrationKeyGenerator.generate(mock(EdmEntitySet.class), mock(ODataEntry.class)))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Cannot calculate integration key value for null entity type");
	}

	@Test
	public void testCalculateIntegrationKeyForEmptyAlias() throws EdmException
	{
		final EdmEntitySet productEntitySet = givenEntitySetForTypeWithKey(PRODUCT_ENTITY_NAME, "");

		assertThat(integrationKeyGenerator.generate(productEntitySet, mock(ODataEntry.class)))
				.isEmpty();
	}

	@Test
	public void testCalculateIntegrationKey_whenEdmExceptionIsThrown() throws EdmException
	{
		final EdmEntityType entityType = givenEdmEntityTypeExists("SomeType");
		final EdmEntitySet productEntitySet = mock(EdmEntitySet.class);
		when(productEntitySet.getEntityType()).thenReturn(entityType);
		doThrow(EdmException.class).when(entityType).getKeyProperties();

		assertThatThrownBy(() -> integrationKeyGenerator.generate(productEntitySet, mock(ODataEntry.class)))
				.isInstanceOf(IntegrationKeyCalculationException.class)
				.hasMessage("An exception occurred while calculating the integrationKey")
				.hasCauseInstanceOf(EdmException.class);
	}

	@Test
	public void testCalculateIntegrationKey_withException_toRole() throws EdmException
	{
		final String catalogVersionVersionValue = "the_catalogVersion_value";

		final EdmEntitySet catalogVersionEntitySet = givenEntitySetForTypeWithKey(CATALOGVERSION_ENTITY_NAME, aliasReferenceFor(CATALOGVERSION_ENTITY_NAME, CATALOGVERSION_VERSION_PROPERTY));
		final EdmEntitySet productEntitySet = givenEntitySetForTypeWithKey(PRODUCT_ENTITY_NAME, aliasReferenceFor(PRODUCT_ENTITY_NAME, PRODUCT_CODE_PROPERTY) + "|" + aliasReferenceFor(CATALOGVERSION_ENTITY_NAME, CATALOGVERSION_VERSION_PROPERTY));

		final ODataEntry catalogVersionEntry = givenODataEntryFromPost(ImmutableMap.of(CATALOGVERSION_VERSION_PROPERTY, catalogVersionVersionValue));
		final ODataEntry productODataEntry = givenODataEntryFromPost(ImmutableMap.of(PRODUCT_CODE_PROPERTY, PRODUCT_CODE_VALUE, CATALOGVERSION_ENTITY_NAME, catalogVersionEntry));

		final EdmNavigationProperty navigationProperty =
				givenNavigationPropertyForEntitySet(CATALOGVERSION_ENTITY_NAME, CATALOGVERSION_ENTITY_NAME, catalogVersionEntitySet, productEntitySet);
		;

		doThrow(EdmException.class).when(navigationProperty).getToRole();

		assertThatThrownBy(() -> integrationKeyGenerator.generate(productEntitySet, productODataEntry))
				.isInstanceOf(IntegrationKeyCalculationException.class)
				.hasMessage("An exception occurred while calculating the integrationKey")
				.hasCauseInstanceOf(EdmException.class);
	}

	@Test
	public void testCalculateIntegrationKey_withException_getNavigationPropertyName() throws EdmException
	{
		final String catalogVersionVersionValue = "the_catalogVersion_value";

		final EdmEntitySet catalogVersionEntitySet = givenEntitySetForTypeWithKey(CATALOGVERSION_ENTITY_NAME, aliasReferenceFor(CATALOGVERSION_ENTITY_NAME, CATALOGVERSION_VERSION_PROPERTY));
		final EdmEntitySet productEntitySet = givenEntitySetForTypeWithKey(PRODUCT_ENTITY_NAME, aliasReferenceFor(PRODUCT_ENTITY_NAME, PRODUCT_CODE_PROPERTY) + "|" + aliasReferenceFor(CATALOGVERSION_ENTITY_NAME, CATALOGVERSION_VERSION_PROPERTY));

		final ODataEntry catalogVersionEntry = givenODataEntryFromPost(ImmutableMap.of(CATALOGVERSION_VERSION_PROPERTY, catalogVersionVersionValue));
		final ODataEntry productODataEntry = givenODataEntryFromPost(ImmutableMap.of(PRODUCT_CODE_PROPERTY, PRODUCT_CODE_VALUE, CATALOGVERSION_ENTITY_NAME, catalogVersionEntry));

		final EdmNavigationProperty navigationProperty =
				givenNavigationPropertyForEntitySet(CATALOGVERSION_ENTITY_NAME, CATALOGVERSION_ENTITY_NAME, catalogVersionEntitySet, productEntitySet);
		;

		doThrow(EdmException.class).when(navigationProperty).getName();

		assertThatThrownBy(() -> integrationKeyGenerator.generate(productEntitySet, productODataEntry))
				.isInstanceOf(IntegrationKeyCalculationException.class)
				.hasMessage("An exception occurred while calculating the integrationKey")
				.hasCauseInstanceOf(EdmException.class);
	}

	@Test
	public void testCalculateIntegrationKey_withException_getNavigationProperty() throws EdmException
	{
		final String catalogVersionVersionValue = "the_catalogVersion_value";

		final EdmEntitySet catalogVersionEntitySet = givenEntitySetForTypeWithKey(CATALOGVERSION_ENTITY_NAME, aliasReferenceFor(CATALOGVERSION_ENTITY_NAME, CATALOGVERSION_VERSION_PROPERTY));
		final EdmEntitySet productEntitySet = givenEntitySetForTypeWithKey(PRODUCT_ENTITY_NAME, aliasReferenceFor(PRODUCT_ENTITY_NAME, PRODUCT_CODE_PROPERTY) + "|" + aliasReferenceFor(CATALOGVERSION_ENTITY_NAME, CATALOGVERSION_VERSION_PROPERTY));

		final ODataEntry catalogVersionEntry = givenODataEntryFromPost(ImmutableMap.of(CATALOGVERSION_VERSION_PROPERTY, catalogVersionVersionValue));
		final ODataEntry productODataEntry = givenODataEntryFromPost(ImmutableMap.of(PRODUCT_CODE_PROPERTY, PRODUCT_CODE_VALUE, CATALOGVERSION_ENTITY_NAME, catalogVersionEntry));

		givenNavigationPropertyForEntitySet(CATALOGVERSION_ENTITY_NAME, CATALOGVERSION_ENTITY_NAME, catalogVersionEntitySet, productEntitySet);
		;

		final EdmEntityType edmEntityType = productEntitySet.getEntityType();
		doThrow(EdmException.class).when(edmEntityType).getProperty(any());

		assertThatThrownBy(() -> integrationKeyGenerator.generate(productEntitySet, productODataEntry))
				.isInstanceOf(IntegrationKeyCalculationException.class)
				.hasMessage("An exception occurred while calculating the integrationKey")
				.hasCauseInstanceOf(EdmException.class);
	}

	@Test
	public void testCalculateIntegrationKey_withException_getTypeCode() throws EdmException
	{
		final String catalogVersionVersionValue = "the_catalogVersion_value";

		final EdmEntitySet catalogVersionEntitySet = givenEntitySetForTypeWithKey(CATALOGVERSION_ENTITY_NAME, aliasReferenceFor(CATALOGVERSION_ENTITY_NAME, CATALOGVERSION_VERSION_PROPERTY));
		final EdmEntitySet productEntitySet = givenEntitySetForTypeWithKey(PRODUCT_ENTITY_NAME, aliasReferenceFor(PRODUCT_ENTITY_NAME, PRODUCT_CODE_PROPERTY) + "|" + aliasReferenceFor(CATALOGVERSION_ENTITY_NAME, CATALOGVERSION_VERSION_PROPERTY));

		final ODataEntry catalogVersionEntry = givenODataEntryFromPost(ImmutableMap.of(CATALOGVERSION_VERSION_PROPERTY, catalogVersionVersionValue));
		final ODataEntry productODataEntry = givenODataEntryFromPost(ImmutableMap.of(PRODUCT_CODE_PROPERTY, PRODUCT_CODE_VALUE, CATALOGVERSION_ENTITY_NAME, catalogVersionEntry));

		givenNavigationPropertyForEntitySet(CATALOGVERSION_ENTITY_NAME, CATALOGVERSION_ENTITY_NAME, catalogVersionEntitySet, productEntitySet);
		;

		final EdmEntityType edmEntityType = productEntitySet.getEntityType();
		doThrow(EdmException.class).when(edmEntityType).getName();

		assertThatThrownBy(() -> integrationKeyGenerator.generate(productEntitySet, productODataEntry))
				.isInstanceOf(IntegrationKeyCalculationException.class)
				.hasMessage("An exception occurred while calculating the integrationKey")
				.hasCauseInstanceOf(EdmException.class);
	}

	@Test
	public void testNoKeyDefinedOnType() throws EdmException
	{
		final EdmEntityType entityType = givenEdmEntityTypeExists("SomeType");
		final EdmEntitySet productEntitySet = mock(EdmEntitySet.class);
		when(productEntitySet.getEntityType()).thenReturn(entityType);
		when(entityType.getKeyProperties()).thenReturn(Collections.emptyList());

		assertThatThrownBy(() -> integrationKeyGenerator.generate(productEntitySet, mock(ODataEntry.class)))
				.isInstanceOf(InvalidDataException.class);
	}

	@Test
	public void testCalculateIntegrationKeyWhenGivenEntityWithSimpleKey() throws EdmException
	{
		final EdmEntitySet productEntitySet = givenEntitySetForTypeWithKey(PRODUCT_ENTITY_NAME, aliasReferenceFor(PRODUCT_ENTITY_NAME, PRODUCT_CODE_PROPERTY));
		final ODataEntry oDataEntry = givenODataEntryFromPost(ImmutableMap.of(PRODUCT_CODE_PROPERTY, PRODUCT_CODE_VALUE));

		assertThat(integrationKeyGenerator.generate(productEntitySet, oDataEntry))
				.isEqualTo(encode(PRODUCT_CODE_VALUE));
	}

	@Test
	public void testCalculateIntegrationKeyWithNoAliasDefined() throws EdmException
	{
		final EdmEntitySet entitySet = givenEntitySetForTypeWithKey(PRODUCT_ENTITY_NAME, aliasReferenceFor(PRODUCT_ENTITY_NAME, PRODUCT_CODE_PROPERTY));
		final EdmEntityType entityType = entitySet.getEntityType();
		final EdmProperty property = entityType.getKeyProperties().get(0);
		when(property.getAnnotations()).thenReturn(mock(EdmAnnotations.class));

		assertThat(integrationKeyGenerator.generate(entitySet, mock(ODataEntry.class)))
				.isEmpty();
	}

	@Test
	public void testCalculateIntegrationKeyWhenGivenEntityWithSimpleKey_calendar() throws EdmException
	{
		final Date now = new Date();
		final EdmEntitySet productEntitySet = givenEntitySetForTypeWithKey(PRODUCT_ENTITY_NAME, aliasReferenceFor(PRODUCT_ENTITY_NAME, "date"));
		final ODataEntry oDataEntry = givenODataEntryFromPost(ImmutableMap.of("date", DateUtils.toCalendar(now)));

		assertThat(integrationKeyGenerator.generate(productEntitySet, oDataEntry))
				.isEqualTo(String.valueOf(now.getTime()));
	}

	@Test
	public void testCalculateIntegrationKeyWhenGivenEntityWithSimpleKey_encodeProblem() throws EdmException, UnsupportedEncodingException
	{
		integrationKeyGenerator.setEncoding("SOME_WEIRD_ENCODING");

		final EdmEntitySet productEntitySet = givenEntitySetForTypeWithKey(PRODUCT_ENTITY_NAME, aliasReferenceFor(PRODUCT_ENTITY_NAME, PRODUCT_CODE_PROPERTY));
		final ODataEntry oDataEntry = givenODataEntryFromPost(ImmutableMap.of(PRODUCT_CODE_PROPERTY, PRODUCT_CODE_VALUE));

		assertThat(integrationKeyGenerator.generate(productEntitySet, oDataEntry))
				.isEqualTo(PRODUCT_CODE_VALUE);
	}

	@Test
	public void testCalculateIntegrationKeyWhenGivenEntityWithTwoSimpleKeys() throws EdmException
	{
		final String productNameValue = "name|Value";

		final EdmEntitySet productEntitySet = givenEntitySetForTypeWithKey(PRODUCT_ENTITY_NAME, aliasReferenceFor(PRODUCT_ENTITY_NAME, PRODUCT_CODE_PROPERTY) + "|" + aliasReferenceFor(PRODUCT_ENTITY_NAME, PRODUCT_NAME_PROPERTY));
		final ODataEntry oDataEntry = givenODataEntryFromPost(ImmutableMap.of(PRODUCT_CODE_PROPERTY, PRODUCT_CODE_VALUE, PRODUCT_NAME_PROPERTY, productNameValue));

		assertThat(integrationKeyGenerator.generate(productEntitySet, oDataEntry))
				.isEqualTo(encode(PRODUCT_CODE_VALUE) + "|" + encode(productNameValue));
	}

	@Test
	public void testCalculateIntegrationKeyWhenGivenEntityWithTwoSimpleKeys_OneWithNullValue() throws EdmException
	{
		final String productNameValue = null;

		final EdmEntitySet productEntitySet = givenEntitySetForTypeWithKey(PRODUCT_ENTITY_NAME, aliasReferenceFor(PRODUCT_ENTITY_NAME, PRODUCT_CODE_PROPERTY) + "|" + aliasReferenceFor(PRODUCT_ENTITY_NAME, PRODUCT_NAME_PROPERTY));
		final Map<String, Object> properties = new HashMap<>();
		properties.put(PRODUCT_CODE_PROPERTY, PRODUCT_CODE_VALUE);
		properties.put(PRODUCT_NAME_PROPERTY, productNameValue);
		final ODataEntry oDataEntry = givenODataEntryFromPost(properties);

		assertThat(integrationKeyGenerator.generate(productEntitySet, oDataEntry))
				.isEqualTo(encode(PRODUCT_CODE_VALUE) + "|" + "null");
	}

	@Test
	public void testCalculateIntegrationKeyWhenGivenEntityWithSimpleKeyAndNavigationKey() throws EdmException
	{
		final String catalogCodeValue = "the|catalog|Value";
		final String productCodeValue = "some|product|code";

		final EdmEntitySet catalogVersionEntitySet =
				givenEntitySetForTypeWithKey(CATALOGVERSION_ENTITY_NAME, aliasReferenceFor(CATALOGVERSION_ENTITY_NAME, CATALOGVERSION_VERSION_PROPERTY));
		final EdmEntitySet productEntitySet =
				givenEntitySetForTypeWithKey(PRODUCT_ENTITY_NAME, aliasReferenceFor(PRODUCT_ENTITY_NAME, PRODUCT_CODE_PROPERTY) + "|" + aliasReferenceFor(CATALOGVERSION_ENTITY_NAME, CATALOGVERSION_VERSION_PROPERTY));

		final ODataEntry catalogVersionEntry = givenODataEntryFromPost(ImmutableMap.of(CATALOGVERSION_VERSION_PROPERTY, catalogCodeValue));
		final ODataEntry productODataEntry = givenODataEntryFromPost(ImmutableMap.of(PRODUCT_CODE_PROPERTY, productCodeValue, CATALOGVERSION_ENTITY_NAME, catalogVersionEntry));

		givenNavigationPropertyForEntitySet(CATALOGVERSION_ENTITY_NAME, CATALOGVERSION_ENTITY_NAME, catalogVersionEntitySet, productEntitySet);
		when(catalogVersionEntitySet.getEntityType().getNavigationPropertyNames()).thenReturn(Collections.emptyList());

		assertThat(integrationKeyGenerator.generate(productEntitySet, productODataEntry))
				.isEqualTo(encode(productCodeValue) + "|" + encode(catalogCodeValue));
	}

	@Test
	public void testCalculateIntegrationKeyWhenMoreDefinedInSchemaThanPosted() throws EdmException
	{
		final EdmEntityType type = givenEdmEntityTypeExists(PRODUCT_ENTITY_NAME);
		final EdmEntitySet productEntitySet = mock(EdmEntitySet.class);
		when(productEntitySet.getEntityType()).thenReturn(type);

		final EdmProperty simpleKeyProperty = givenKeyPropertyWithAliasStringExists(aliasReferenceFor(PRODUCT_ENTITY_NAME, PRODUCT_CODE_PROPERTY));
		when(type.getKeyProperties()).thenReturn(Collections.singletonList(simpleKeyProperty));

		givenNavigationPropertyForEntitySet("NavProperty", "anyString", mock(EdmEntitySet.class), productEntitySet);

		final ODataEntry oDataEntry = givenODataEntryFromPost(ImmutableMap.of(PRODUCT_CODE_PROPERTY, PRODUCT_CODE_VALUE));

		assertThat(integrationKeyGenerator.generate(productEntitySet, oDataEntry))
				.isEqualTo(encode(PRODUCT_CODE_VALUE));
	}

	@Test
	public void testCalculateIntegrationKeyWhenItemElementsStructuredInDifferentOrderThanKeyElements() throws EdmException
	{
		//Alias = Product_code|Catalog_id|CatalogVersion_catalog
		final String catalogVersionVersionValue = "the|catalogVersion|value";

		final String catalogIdValue = "the|catalogId|value";

		final EdmEntitySet catalogVersionEntitySet = givenEntitySetForTypeWithKey(CATALOGVERSION_ENTITY_NAME, aliasReferenceFor(CATALOGVERSION_ENTITY_NAME, CATALOGVERSION_VERSION_PROPERTY));
		final EdmEntitySet productEntitySet = givenEntitySetForTypeWithKey(PRODUCT_ENTITY_NAME, aliasReferenceFor(PRODUCT_ENTITY_NAME, PRODUCT_CODE_PROPERTY) + "|" + aliasReferenceFor(CATALOG_ENTITY_NAME, CATALOG_ID_PROPERTY) + "|" + aliasReferenceFor(CATALOGVERSION_ENTITY_NAME, CATALOGVERSION_VERSION_PROPERTY));
		final EdmEntitySet catalogEntitySet = givenEntitySetForTypeWithKey(CATALOG_ENTITY_NAME, aliasReferenceFor(CATALOG_ENTITY_NAME, CATALOG_ID_PROPERTY));

		final ODataEntry catalogEntry = givenODataEntryFromPost(ImmutableMap.of(CATALOG_ID_PROPERTY, catalogIdValue));
		final ODataEntry catalogVersionEntry = givenODataEntryFromPost(ImmutableMap.of(CATALOGVERSION_VERSION_PROPERTY, catalogVersionVersionValue, CATALOGVERSION_CATALOG_PROPERTY, catalogEntry));
		final ODataEntry productODataEntry = givenODataEntryFromPost(ImmutableMap.of(PRODUCT_CODE_PROPERTY, PRODUCT_CODE_VALUE, CATALOGVERSION_ENTITY_NAME, catalogVersionEntry));

		givenNavigationPropertyForEntitySet(CATALOGVERSION_ENTITY_NAME, CATALOGVERSION_ENTITY_NAME, catalogVersionEntitySet, productEntitySet);
		givenNavigationPropertyForEntitySet(CATALOG_ENTITY_NAME, CATALOGVERSION_CATALOG_PROPERTY, catalogEntitySet, catalogVersionEntitySet);

		assertThat(integrationKeyGenerator.generate(productEntitySet, productODataEntry))
				.isEqualTo(encode(PRODUCT_CODE_VALUE) + "|" + encode(catalogIdValue) + "|" + encode(catalogVersionVersionValue));
	}

	@Test
	public void testCalculateIntegrationKeyWhenAnEntityTypeHasTheSameValueMultipleTimes() throws EdmException
	{
		final EdmEntitySet productEntitySet = givenEntitySetForTypeWithKey(PRODUCT_ENTITY_NAME, aliasReferenceFor(PRODUCT_ENTITY_NAME, PRODUCT_CODE_PROPERTY) + "|" + aliasReferenceFor(PRODUCT_ENTITY_NAME, PRODUCT_NAME_PROPERTY));
		final ODataEntry productODataEntry = givenODataEntryFromPost(ImmutableMap.of(PRODUCT_CODE_PROPERTY, PRODUCT_CODE_VALUE, PRODUCT_NAME_PROPERTY, PRODUCT_CODE_VALUE));

		assertThat(integrationKeyGenerator.generate(productEntitySet, productODataEntry))
				.isEqualTo(encode(PRODUCT_CODE_VALUE) + "|" + encode(PRODUCT_CODE_VALUE));
	}

	private EdmNavigationProperty givenNavigationPropertyForEntitySet(final String navigationPropertyToRoleType, final String navigationPropertyName, final EdmEntitySet relatedEntitySet, final EdmEntitySet entitySet) throws EdmException
	{
		final EdmNavigationProperty edmNavigationProperty = mock(EdmNavigationProperty.class);
		when(entitySet.getEntityType().getNavigationPropertyNames()).thenReturn(Collections.singletonList(navigationPropertyName));
		when(entitySet.getEntityType().getProperty(navigationPropertyName)).thenReturn(edmNavigationProperty);
		when(entitySet.getRelatedEntitySet(edmNavigationProperty)).thenReturn(relatedEntitySet);
		when(edmNavigationProperty.getToRole()).thenReturn(navigationPropertyToRoleType);
		when(edmNavigationProperty.getName()).thenReturn(navigationPropertyName);
		final EdmAnnotations annotations = mock(EdmAnnotations.class);
		when(edmNavigationProperty.getAnnotations()).thenReturn(annotations);
		final EdmAnnotationAttribute attribute = mock(EdmAnnotationAttribute.class);
		when(annotations.getAnnotationAttributes()).thenReturn(Collections.singletonList(attribute));
		when(attribute.getName()).thenReturn("s:IsUnique");
		when(attribute.getText()).thenReturn("true");

		return edmNavigationProperty;
	}

	private ODataEntry givenODataEntryFromPost(final Map<String, Object> properties)
	{
		final ODataEntry entry = mock(ODataEntry.class);
		when(entry.getProperties()).thenReturn(properties);
		return entry;
	}

	private String aliasReferenceFor(final String entityName, final String propertyName)
	{
		return entityName + "_" + propertyName;
	}

	private EdmEntitySet givenEntitySetForTypeWithKey(final String typeName, final String aliasString) throws EdmException
	{
		final EdmEntitySet entitySet = mock(EdmEntitySet.class);
		final EdmEntityType type = givenEdmEntityTypeExists(typeName);
		when(entitySet.getEntityType()).thenReturn(type);

		final EdmProperty simpleKeyProperty = givenKeyPropertyWithAliasStringExists(aliasString);
		when(type.getKeyProperties()).thenReturn(Collections.singletonList(simpleKeyProperty));

		return entitySet;
	}

	private EdmProperty givenKeyPropertyWithAliasStringExists(final String aliasString) throws EdmException
	{
		final EdmProperty simpleKeyProperty = mock(EdmSimplePropertyImplProv.class);
		final EdmAnnotations simplePropertyEdmAnnotations = mock(EdmAnnotations.class);
		final AnnotationAttribute aliasAttribute = mock(AnnotationAttribute.class);

		when(aliasAttribute.getText()).thenReturn(aliasString);
		when(aliasAttribute.getName()).thenReturn(ALIAS_ANNOTATION_ATTR_NAME);
		when(simpleKeyProperty.getAnnotations()).thenReturn(simplePropertyEdmAnnotations);
		when(simpleKeyProperty.isSimple()).thenReturn(true);
		when(simplePropertyEdmAnnotations.getAnnotationAttributes()).thenReturn(Collections.singletonList(aliasAttribute));
		return simpleKeyProperty;
	}

	private EdmEntityType givenEdmEntityTypeExists(final String typeName) throws EdmException
	{
		final EdmEntityType entityType = mock(EdmEntityType.class);
		when(entityType.getName()).thenReturn(typeName);
		return entityType;
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