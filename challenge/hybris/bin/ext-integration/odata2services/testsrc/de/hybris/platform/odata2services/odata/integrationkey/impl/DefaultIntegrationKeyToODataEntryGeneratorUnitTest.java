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

import static de.hybris.platform.integrationservices.constants.IntegrationservicesConstants.INTEGRATION_KEY_PROPERTY_NAME;
import static de.hybris.platform.odata2services.constants.Odata2servicesConstants.ALIAS_ANNOTATION_ATTR_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.odata2services.odata.persistence.exception.InvalidIntegrationKeyException;
import de.hybris.platform.odata2services.odata.persistence.exception.MissingKeyException;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.olingo.odata2.api.edm.EdmAnnotationAttribute;
import org.apache.olingo.odata2.api.edm.EdmAnnotations;
import org.apache.olingo.odata2.api.edm.EdmEntitySet;
import org.apache.olingo.odata2.api.edm.EdmEntityType;
import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.edm.EdmNavigationProperty;
import org.apache.olingo.odata2.api.edm.EdmProperty;
import org.apache.olingo.odata2.api.edm.EdmType;
import org.apache.olingo.odata2.api.edm.provider.AnnotationAttribute;
import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.apache.olingo.odata2.core.edm.EdmBoolean;
import org.apache.olingo.odata2.core.edm.EdmByte;
import org.apache.olingo.odata2.core.edm.EdmDateTime;
import org.apache.olingo.odata2.core.edm.EdmDateTimeOffset;
import org.apache.olingo.odata2.core.edm.EdmDecimal;
import org.apache.olingo.odata2.core.edm.EdmDouble;
import org.apache.olingo.odata2.core.edm.EdmGuid;
import org.apache.olingo.odata2.core.edm.EdmInt16;
import org.apache.olingo.odata2.core.edm.EdmInt32;
import org.apache.olingo.odata2.core.edm.EdmInt64;
import org.apache.olingo.odata2.core.edm.EdmString;
import org.apache.olingo.odata2.core.edm.EdmTime;
import org.apache.olingo.odata2.core.edm.provider.EdmAnnotationsImplProv;
import org.apache.olingo.odata2.core.edm.provider.EdmSimplePropertyImplProv;
import org.assertj.core.util.DateUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultIntegrationKeyToODataEntryGeneratorUnitTest
{
	private static final String PRODUCT_ENTITY_NAME = "Product";
	private static final String PRODUCT_CODE_PROPERTY = "code";
	private static final String PRODUCT_NAME_PROPERTY = "name";
	private static final String PRODUCT_CATALOGVERSION_PROPERTY = "catalogVersion";

	private static final String CATALOGVERSION_ENTITY_NAME = "CatalogVersion";
	private static final String CATALOGVERSION_CODE2_PROPERTY = "code2";
	private static final String CATALOGVERSION_VERSION_PROPERTY = "version";
	private static final String CATALOGVERSION_CATALOG_PROPERTY = "catalog";

	private static final String CATALOG_ENTITY_NAME = "Catalog";
	private static final String CATALOG_ID_PROPERTY = "id";

	private final DefaultIntegrationKeyToODataEntryGenerator integrationKeyGenerator = new DefaultIntegrationKeyToODataEntryGenerator();

	@Before
	public void setUp()
	{
		this.integrationKeyGenerator.setEncoding("UTF-8");
	}

	@Test
	public void testCalculateODataEntryForNullEntitySet()
	{
		assertThatThrownBy(() -> integrationKeyGenerator.generate(null, "IntegrationKey"))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Cannot calculate ODataEntry for null edm entity set");
	}

	@Test
	public void testCalculateODataEntryForNullIntegrationKeyValue()
	{
		assertThatThrownBy(() -> integrationKeyGenerator.generate(mock(EdmEntitySet.class), null))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Cannot calculate ODataEntry for null integrationKey");
	}

	@Test
	public void testCalculateODataEntryForEmptyIntegrationKeyValue() throws EdmException
	{
		final EdmEntitySet productEntitySet = givenEntitySetForTypeWithKey(PRODUCT_ENTITY_NAME, "");

		assertThatThrownBy(() -> integrationKeyGenerator.generate(productEntitySet, "someCode"))
				.isInstanceOf(MissingKeyException.class)
				.hasMessage("Error while retrieving the integration key for the entity type [Product].")
				.hasFieldOrPropertyWithValue("errorCode", "missing_key");
	}

	@Test
	public void testCalculateODataEntryInvalidNumberOfIntegrationKeyValue() throws EdmException
	{
		final EdmEntitySet productEntitySet = givenEntitySetForTypeWithKey(PRODUCT_ENTITY_NAME,
				aliasReferenceFor(PRODUCT_ENTITY_NAME, PRODUCT_CODE_PROPERTY));

		// Based on productEntitySet it should expect only one
		assertThatThrownBy(() -> integrationKeyGenerator.generate(productEntitySet, "IntegrationKey1|IntegrationKey2"))
				.isInstanceOf(InvalidIntegrationKeyException.class)
				.hasMessage(
						"The integration key [IntegrationKey1|IntegrationKey2] is invalid. Please consult the IntegrationKey definition of [Product] for configuration details.");
	}

	@Test
	public void testCalculateODataEntryForSingleIntegrationKeyValue() throws EdmException
	{
		final EdmEntitySet productEntitySet = givenEntitySetForTypeWithKey(PRODUCT_ENTITY_NAME,
				aliasReferenceFor(PRODUCT_ENTITY_NAME, PRODUCT_CODE_PROPERTY));
		givenPropertiesForEntitySet(productEntitySet, PRODUCT_CODE_PROPERTY);

		final String productCode = "product%7Ccode";
		final ODataEntry oDataEntry = integrationKeyGenerator.generate(productEntitySet, productCode);

		assertThat(oDataEntry.getProperties()).hasSize(2)
											  .contains(entry(PRODUCT_CODE_PROPERTY, "product|code"))
											  .contains(entry(INTEGRATION_KEY_PROPERTY_NAME, productCode));
	}

	@Test
	public void testCalculateODataEntryForSingleIntegrationKeyValue_decodeProblem() throws EdmException, UnsupportedEncodingException
	{
		integrationKeyGenerator.setEncoding("SOME_WEIRD_ENCODING");

		final EdmEntitySet productEntitySet = givenEntitySetForTypeWithKey(PRODUCT_ENTITY_NAME,
				aliasReferenceFor(PRODUCT_ENTITY_NAME, PRODUCT_CODE_PROPERTY));
		givenPropertiesForEntitySet(productEntitySet, PRODUCT_CODE_PROPERTY);

		final String productCode = "product%7Ccode";
		final ODataEntry oDataEntry = integrationKeyGenerator.generate(productEntitySet, productCode);

		assertThat(oDataEntry.getProperties()).hasSize(2)
											  .contains(entry(PRODUCT_CODE_PROPERTY, productCode))
											  .contains(entry(INTEGRATION_KEY_PROPERTY_NAME, productCode));
	}

	@Test
	public void testCalculateODataEntryForMultipleIntegrationKeyValue() throws EdmException
	{
		final EdmEntitySet productEntitySet = givenEntitySetForTypeWithKey(PRODUCT_ENTITY_NAME,
				aliasReferenceFor(PRODUCT_ENTITY_NAME, PRODUCT_CODE_PROPERTY) + "|" +
				aliasReferenceFor(PRODUCT_ENTITY_NAME, PRODUCT_NAME_PROPERTY) + "|" +
				aliasReferenceFor(CATALOGVERSION_ENTITY_NAME, CATALOGVERSION_VERSION_PROPERTY) + "|" +
				aliasReferenceFor(CATALOGVERSION_ENTITY_NAME, CATALOGVERSION_CODE2_PROPERTY) + "|" +
				aliasReferenceFor(CATALOG_ENTITY_NAME, CATALOG_ID_PROPERTY));

		final EdmEntitySet catalogVersionEntitySet = givenEntitySetForTypeWithKey(CATALOGVERSION_ENTITY_NAME,
				aliasReferenceFor(CATALOGVERSION_ENTITY_NAME, CATALOGVERSION_VERSION_PROPERTY) + "|" +
				aliasReferenceFor(CATALOGVERSION_ENTITY_NAME, CATALOGVERSION_CODE2_PROPERTY) + "|" +
				aliasReferenceFor(CATALOG_ENTITY_NAME, CATALOG_ID_PROPERTY));

		final EdmEntitySet catalogEntitySet = givenEntitySetForTypeWithKey(CATALOG_ENTITY_NAME,
				aliasReferenceFor(CATALOG_ENTITY_NAME, CATALOG_ID_PROPERTY));

		givenNavigationPropertyForEntitySet(CATALOGVERSION_ENTITY_NAME, PRODUCT_CATALOGVERSION_PROPERTY, catalogVersionEntitySet, productEntitySet);

		givenNavigationPropertyForEntitySet(CATALOG_ENTITY_NAME, CATALOGVERSION_CATALOG_PROPERTY, catalogEntitySet, catalogVersionEntitySet);

		givenPropertiesForEntitySet(productEntitySet, PRODUCT_CODE_PROPERTY, PRODUCT_NAME_PROPERTY);
		givenPropertiesForEntitySet(catalogVersionEntitySet, CATALOGVERSION_VERSION_PROPERTY, CATALOGVERSION_CODE2_PROPERTY);
		givenPropertiesForEntitySet(catalogEntitySet, CATALOG_ID_PROPERTY);

		final String integrationKey = "product%7Ccode|some%7Cname|default%7Cvalue|some%7CcatalogVersion%7ccode2|online%7Cvalue";
		final ODataEntry oDataEntry = integrationKeyGenerator.generate(productEntitySet, integrationKey);

		assertThat(oDataEntry.getProperties()).hasSize(4)
											  .contains(entry(PRODUCT_CODE_PROPERTY, "product|code"))
											  .contains(entry(PRODUCT_NAME_PROPERTY, "some|name"))
											  .contains(entry(INTEGRATION_KEY_PROPERTY_NAME, integrationKey))
											  .containsKey(PRODUCT_CATALOGVERSION_PROPERTY);

		final ODataEntry catalogVersionEntry = (ODataEntry) oDataEntry.getProperties().get(PRODUCT_CATALOGVERSION_PROPERTY);
		assertThat(catalogVersionEntry.getProperties()).hasSize(3)
													   .contains(entry(CATALOGVERSION_VERSION_PROPERTY, "default|value"))
													   .contains(entry(CATALOGVERSION_CODE2_PROPERTY, "some|catalogVersion|code2"))
													   .containsKey(CATALOGVERSION_CATALOG_PROPERTY);

		final ODataEntry catalogEntry = (ODataEntry) catalogVersionEntry.getProperties().get(CATALOGVERSION_CATALOG_PROPERTY);

		assertThat(catalogEntry.getProperties())
				.contains(entry(CATALOG_ID_PROPERTY, "online|value"));
	}

	@Test
	public void testCalculateODataEntry_stringProperty() throws EdmException
	{
		final EdmEntitySet productEntitySet = givenEntitySetForTypeWithKey(PRODUCT_ENTITY_NAME,
				aliasReferenceFor(PRODUCT_ENTITY_NAME, "mystring"));

		givenPropertiesForEntitySet2(productEntitySet, Pair.of("mystring", new EdmString()));

		final String integrationKey = "code";
		final ODataEntry oDataEntry = integrationKeyGenerator.generate(productEntitySet, integrationKey);

		assertThat(oDataEntry.getProperties()).hasSize(2)
											  .contains(entry("mystring", "code"))
											  .contains(entry(INTEGRATION_KEY_PROPERTY_NAME, integrationKey));
	}

	@Test
	public void testCalculateODataEntry_booleanProperty() throws EdmException
	{
		final EdmEntitySet productEntitySet = givenEntitySetForTypeWithKey(PRODUCT_ENTITY_NAME,
				aliasReferenceFor(PRODUCT_ENTITY_NAME, "myboolean"));

		givenPropertiesForEntitySet2(productEntitySet, Pair.of("myboolean", new EdmBoolean()));

		final String integrationKey = "false";
		final ODataEntry oDataEntry = integrationKeyGenerator.generate(productEntitySet, integrationKey);

		assertThat(oDataEntry.getProperties()).hasSize(2)
											  .contains(entry("myboolean", false))
											  .contains(entry(INTEGRATION_KEY_PROPERTY_NAME, integrationKey));
	}

	@Test
	public void testCalculateODataEntry_dateTimeProperty() throws EdmException
	{
		final EdmEntitySet productEntitySet = givenEntitySetForTypeWithKey(PRODUCT_ENTITY_NAME,
						aliasReferenceFor(PRODUCT_ENTITY_NAME, "mydatetime"));

		givenPropertiesForEntitySet2(productEntitySet, Pair.of("mydatetime", new EdmDateTime()));

		final String integrationKey = "0";
		final ODataEntry oDataEntry = integrationKeyGenerator.generate(productEntitySet, integrationKey);

		assertThat(oDataEntry.getProperties()).hasSize(2)
											  .contains(entry("mydatetime", DateUtil.toCalendar(new Date(0))))
											  .contains(entry(INTEGRATION_KEY_PROPERTY_NAME, integrationKey));
	}

	@Test
	public void testCalculateODataEntry_dateTimeOffsetProperty() throws EdmException
	{
		final EdmEntitySet productEntitySet = givenEntitySetForTypeWithKey(PRODUCT_ENTITY_NAME,
						aliasReferenceFor(PRODUCT_ENTITY_NAME, "mydatetimeoffset"));

		givenPropertiesForEntitySet2(productEntitySet, Pair.of("mydatetimeoffset", new EdmDateTimeOffset()));

		final String integrationKey = "1";
		final ODataEntry oDataEntry = integrationKeyGenerator.generate(productEntitySet, integrationKey);

		assertThat(oDataEntry.getProperties()).hasSize(2)
											  .contains(entry("mydatetimeoffset", DateUtil.toCalendar(new Date(1))))
											  .contains(entry(INTEGRATION_KEY_PROPERTY_NAME, integrationKey));
	}

	@Test
	public void testCalculateODataEntry_timeProperty() throws EdmException
	{
		final EdmEntitySet productEntitySet = givenEntitySetForTypeWithKey(PRODUCT_ENTITY_NAME,
						aliasReferenceFor(PRODUCT_ENTITY_NAME, "mytime"));

		givenPropertiesForEntitySet2(productEntitySet, Pair.of("mytime", new EdmTime()));

		final String integrationKey = "2";
		final ODataEntry oDataEntry = integrationKeyGenerator.generate(productEntitySet, integrationKey);

		assertThat(oDataEntry.getProperties()).hasSize(2)
											  .contains(entry("mytime", DateUtil.toCalendar(new Date(2))))
											  .contains(entry(INTEGRATION_KEY_PROPERTY_NAME, integrationKey));
	}

	@Test
	public void testCalculateODataEntry_guidProperty() throws EdmException
	{
		final EdmEntitySet productEntitySet = givenEntitySetForTypeWithKey(PRODUCT_ENTITY_NAME,
						aliasReferenceFor(PRODUCT_ENTITY_NAME, "myuuid"));

		givenPropertiesForEntitySet2(productEntitySet, Pair.of("myuuid", new EdmGuid()));

		final String integrationKey = "4082356e-c6e4-4098-97ed-369eeb6385fd";
		final ODataEntry oDataEntry = integrationKeyGenerator.generate(productEntitySet, integrationKey);

		assertThat(oDataEntry.getProperties()).hasSize(2)
											  .contains(entry("myuuid", UUID.fromString("4082356e-c6e4-4098-97ed-369eeb6385fd")))
											  .contains(entry(INTEGRATION_KEY_PROPERTY_NAME, integrationKey));
	}

	@Test
	public void testCalculateODataEntry_byteProperty() throws EdmException
	{
		final EdmEntitySet productEntitySet = givenEntitySetForTypeWithKey(PRODUCT_ENTITY_NAME,
						aliasReferenceFor(PRODUCT_ENTITY_NAME, "mybyte"));

		givenPropertiesForEntitySet2(productEntitySet, Pair.of("mybyte", new EdmByte()));

		final String integrationKey = "32";
		final ODataEntry oDataEntry = integrationKeyGenerator.generate(productEntitySet, integrationKey);

		assertThat(oDataEntry.getProperties()).hasSize(2)
											  .contains(entry("mybyte", (byte)32))
											  .contains(entry(INTEGRATION_KEY_PROPERTY_NAME, integrationKey));
	}

	@Test
	public void testCalculateODataEntry_decimalProperty() throws EdmException
	{
		final EdmEntitySet productEntitySet = givenEntitySetForTypeWithKey(PRODUCT_ENTITY_NAME,
						aliasReferenceFor(PRODUCT_ENTITY_NAME, "mydecimal"));

		givenPropertiesForEntitySet2(productEntitySet, Pair.of("mydecimal", new EdmDecimal()));

		final String integrationKey = "1.1";
		final ODataEntry oDataEntry = integrationKeyGenerator.generate(productEntitySet, integrationKey);

		assertThat(oDataEntry.getProperties()).hasSize(2)
											  .contains(entry("mydecimal", 1.1D))
											  .contains(entry(INTEGRATION_KEY_PROPERTY_NAME, integrationKey));
	}

	@Test
	public void testCalculateODataEntry_doubleProperty() throws EdmException
	{
		final EdmEntitySet productEntitySet = givenEntitySetForTypeWithKey(PRODUCT_ENTITY_NAME,
						aliasReferenceFor(PRODUCT_ENTITY_NAME, "mydouble"));

		givenPropertiesForEntitySet2(productEntitySet, Pair.of("mydouble", new EdmDouble()));

		final String integrationKey = "1.2";
		final ODataEntry oDataEntry = integrationKeyGenerator.generate(productEntitySet, integrationKey);

		assertThat(oDataEntry.getProperties()).hasSize(2)
											  .contains(entry("mydouble", 1.2D))
											  .contains(entry(INTEGRATION_KEY_PROPERTY_NAME, integrationKey));
	}

	@Test
	public void testCalculateODataEntry_int16Property() throws EdmException
	{
		final EdmEntitySet productEntitySet = givenEntitySetForTypeWithKey(PRODUCT_ENTITY_NAME,
						aliasReferenceFor(PRODUCT_ENTITY_NAME, "myint16"));

		givenPropertiesForEntitySet2(productEntitySet, Pair.of("myint16", new EdmInt16()));

		final String integrationKey = "16";
		final ODataEntry oDataEntry = integrationKeyGenerator.generate(productEntitySet, integrationKey);

		assertThat(oDataEntry.getProperties()).hasSize(2)
											  .contains(entry("myint16", 16L))
											  .contains(entry(INTEGRATION_KEY_PROPERTY_NAME, integrationKey));
	}

	@Test
	public void testCalculateODataEntry_int32Property() throws EdmException
	{
		final EdmEntitySet productEntitySet = givenEntitySetForTypeWithKey(PRODUCT_ENTITY_NAME,
						aliasReferenceFor(PRODUCT_ENTITY_NAME, "myint32"));

		givenPropertiesForEntitySet2(productEntitySet, Pair.of("myint32", new EdmInt32()));

		final String integrationKey = "32";
		final ODataEntry oDataEntry = integrationKeyGenerator.generate(productEntitySet, integrationKey);

		assertThat(oDataEntry.getProperties()).hasSize(2)
											  .contains(entry("myint32", 32L))
											  .contains(entry(INTEGRATION_KEY_PROPERTY_NAME, integrationKey));
	}

	@Test
	public void testCalculateODataEntry_int64Property() throws EdmException
	{
		final EdmEntitySet productEntitySet = givenEntitySetForTypeWithKey(PRODUCT_ENTITY_NAME,
						aliasReferenceFor(PRODUCT_ENTITY_NAME, "myint64"));

		givenPropertiesForEntitySet2(productEntitySet, Pair.of("myint64", new EdmInt64()));

		final String integrationKey = "64";
		final ODataEntry oDataEntry = integrationKeyGenerator.generate(productEntitySet, integrationKey);

		assertThat(oDataEntry.getProperties()).hasSize(2)
											  .contains(entry("myint64", 64L))
											  .contains(entry(INTEGRATION_KEY_PROPERTY_NAME, integrationKey));
	}

	@Test
	public void testCalculateODataEntryForMissingKeyFromEntitySet() throws EdmException
	{
		final EdmEntitySet entitySet = mock(EdmEntitySet.class);
		final EdmEntityType type = mock(EdmEntityType.class);
		when(entitySet.getEntityType()).thenReturn(type);
		when(type.getName()).thenReturn("ExceptionType");


		final EdmProperty simpleKeyProperty = mock(EdmSimplePropertyImplProv.class);
		when(type.getKeyProperties()).thenReturn(Collections.singletonList(simpleKeyProperty));
		final EdmAnnotationsImplProv simplePropertyEdmAnnotations = mock(EdmAnnotationsImplProv.class);

		when(simpleKeyProperty.getAnnotations()).thenReturn(simplePropertyEdmAnnotations);
		when(simpleKeyProperty.isSimple()).thenReturn(true);
		when(simplePropertyEdmAnnotations.getAnnotationAttributes()).thenReturn(Collections.emptyList());

		assertThatThrownBy(() -> integrationKeyGenerator.generate(entitySet, "IntegrationKey"))
				.isInstanceOf(MissingKeyException.class)
				.hasMessage("Error while retrieving the integration key for the entity type [ExceptionType].");
	}

	private void givenNavigationPropertyForEntitySet(final String navigationPropertyToRoleType, final String navigationPropertyName, final EdmEntitySet relatedEntitySet, final EdmEntitySet entitySet) throws EdmException
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
	}

	private void givenPropertiesForEntitySet(final EdmEntitySet entitySet, final String... properties) throws EdmException
	{
		givenPropertiesForEntitySet2(entitySet, Stream.of(properties).map(p -> Pair.of(p, mock(EdmType.class))).toArray(Pair[]::new));
	}

	@SafeVarargs
	private final void givenPropertiesForEntitySet2(final EdmEntitySet entitySet, final Pair<String, EdmType>... properties) throws EdmException
	{
		for(final Pair<String, EdmType> property : properties)
		{
			final EdmProperty edmProperty = mock(EdmProperty.class);
			when(entitySet.getEntityType().getProperty(property.getLeft())).thenReturn(edmProperty);
			when(edmProperty.getName()).thenReturn(property.getLeft());
			when(edmProperty.getType()).thenReturn(property.getRight());
		}
		when(entitySet.getEntityType().getPropertyNames()).thenReturn(Stream.of(properties).map(Pair::getLeft).collect(Collectors.toList()));
	}

	private String aliasReferenceFor(final String entityName, final String propertyName)
	{
		return entityName + "_" + propertyName;
	}

	private EdmEntitySet givenEntitySetForTypeWithKey(final String typeName, final String aliasString) throws EdmException
	{
		final EdmEntityType type = givenEdmEntityTypeExists(typeName);
		final EdmEntitySet entitySet = mock(EdmEntitySet.class);
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

		when(aliasAttribute.getName()).thenReturn(ALIAS_ANNOTATION_ATTR_NAME);
		when(aliasAttribute.getText()).thenReturn(aliasString);
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
}