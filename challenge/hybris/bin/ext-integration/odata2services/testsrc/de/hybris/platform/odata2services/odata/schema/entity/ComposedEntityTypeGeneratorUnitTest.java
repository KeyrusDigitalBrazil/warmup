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
package de.hybris.platform.odata2services.odata.schema.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemAttributeModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel;
import de.hybris.platform.odata2services.odata.schema.KeyGenerator;
import de.hybris.platform.odata2services.odata.schema.navigation.NavigationPropertyListGeneratorRegistry;
import de.hybris.platform.odata2services.odata.schema.property.AbstractPropertyListGenerator;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.olingo.odata2.api.edm.provider.EntityType;
import org.apache.olingo.odata2.api.edm.provider.Key;
import org.apache.olingo.odata2.api.edm.provider.NavigationProperty;
import org.apache.olingo.odata2.api.edm.provider.Property;
import org.apache.olingo.odata2.api.edm.provider.PropertyRef;
import org.apache.olingo.odata2.api.edm.provider.SimpleProperty;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ComposedEntityTypeGeneratorUnitTest
{
	private static final String CODE_1 = "code1";
	private static final String ATTRIBUTE_1 = "attribute1";
	private static final String NAME_FIELD = "name";

	@Mock
	private AbstractPropertyListGenerator propertyListGenerator;
	@Mock
	private NavigationPropertyListGeneratorRegistry navPropertyListGeneratorRegistry;
	@Mock
	private KeyGenerator keyGenerator;

	@InjectMocks
	private ComposedEntityTypeGenerator composedEntityTypeGenerator;

	private static IntegrationObjectItemModel createIntegrationObjectItemModel(final String code, final String... attributeNames)
	{
		final Set<IntegrationObjectItemAttributeModel> ioadmSet = Stream.of(attributeNames)
				.map(ComposedEntityTypeGeneratorUnitTest::createIntegrationObjectItemAttributeModel)
				.collect(Collectors.toSet());

		final IntegrationObjectItemModel iodm = new IntegrationObjectItemModel();
		iodm.setAttributes(ioadmSet);
		iodm.setCode(code);
		return iodm;
	}

	private static IntegrationObjectItemAttributeModel createIntegrationObjectItemAttributeModel(final String attributeName)
	{
		final IntegrationObjectItemAttributeModel integrationObjectAttributeDefinitionModel = new IntegrationObjectItemAttributeModel();
		integrationObjectAttributeDefinitionModel.setAttributeName(attributeName);
		return integrationObjectAttributeDefinitionModel;
	}

	@Test
	public void testGenerateAllFieldsPresent()
	{
		final List<Property> expectedPropertyList =
				givenExpectedPropertyListWithNames("property1", "property2");

		final List<NavigationProperty> expectedNavigationPropertyList =
				givenExpectedNavigationPropertyListWithNames("navigationProperty1", "navigationProperty2");

		final Key expectedKey = givenExpectedKeyWithPropertyRefNames("propertyRef1");
		final IntegrationObjectItemModel iodm = createIntegrationObjectItemModel(CODE_1, ATTRIBUTE_1);

		final List<EntityType> entityTypes = composedEntityTypeGenerator.generate(iodm);

		assertThat(entityTypes).hasSize(1);
		final EntityType entityType = entityTypes.get(0);
		assertThat(entityType.getName()).isEqualTo(CODE_1);
		assertThat(entityType.getProperties()).usingElementComparatorOnFields(NAME_FIELD).isEqualTo(expectedPropertyList);
		assertThat(entityType.getNavigationProperties()).usingElementComparatorOnFields(NAME_FIELD).isEqualTo(expectedNavigationPropertyList);
		assertThat(entityType.getKey()).isEqualToComparingFieldByField(expectedKey);
	}

	@Test
	public void testGenerateKeyGenerationThrowsException()
	{
		when(keyGenerator.generate(any())).thenThrow(IllegalArgumentException.class);

		final IntegrationObjectItemModel iodm = createIntegrationObjectItemModel(CODE_1, ATTRIBUTE_1);

		assertThatThrownBy(() -> composedEntityTypeGenerator.generate(iodm)).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void testGenerateNoKeyGenerated()
	{
		when(keyGenerator.generate(any())).thenReturn(Optional.empty());

		final IntegrationObjectItemModel iodm = createIntegrationObjectItemModel(CODE_1, ATTRIBUTE_1);

		final List<EntityType> entityTypes = composedEntityTypeGenerator.generate(iodm);
		assertThat(entityTypes).hasSize(1);
		assertThat(entityTypes.get(0).getKey()).isNull();
	}

	@Test
	public void testGenerateNullIntegrationObjectItemModel()
	{
		assertThatThrownBy(() -> composedEntityTypeGenerator.generate(null))
				.isInstanceOf(IllegalArgumentException.class);
	}

	private Key givenExpectedKeyWithPropertyRefNames(final String... propertyRefNames)
	{
		final Key expectedKey = createKeyWithPropertyRefNames(propertyRefNames);
		when(keyGenerator.generate(any())).thenReturn(Optional.of(expectedKey));
		return expectedKey;
	}

	private List<NavigationProperty> givenExpectedNavigationPropertyListWithNames(final String... propertyNames)
	{
		final List<NavigationProperty> navigationPropertyList = createNavigationPropertyList(propertyNames);
		when(navPropertyListGeneratorRegistry.generate(any())).thenReturn(navigationPropertyList);
		return navigationPropertyList;
	}

	private List<Property> givenExpectedPropertyListWithNames(final String... navigationPropertyNames)
	{
		final List<Property> simplePropertyList = createSimplePropertyList(navigationPropertyNames);
		when(propertyListGenerator.generate(any())).thenReturn(simplePropertyList);
		return simplePropertyList;
	}

	private Key createKeyWithPropertyRefNames(final String... propertyRefNames)
	{
		final List<PropertyRef> propertyRefList = Stream.of(propertyRefNames)
				.map(this::createPropertyRef)
				.collect(Collectors.toList());
		return new Key().setKeys(propertyRefList);
	}

	private PropertyRef createPropertyRef(final String propertyRefName)
	{
		final PropertyRef propertyRef = new PropertyRef();
		propertyRef.setName(propertyRefName);
		return propertyRef;
	}

	private List<Property> createSimplePropertyList(final String... propertyNames)
	{
		return Stream.of(propertyNames).map(this::createSimpleProperty).collect(Collectors.toList());
	}

	private Property createSimpleProperty(final String propertyName)
	{
		return new SimpleProperty().setName(propertyName);
	}

	private List<NavigationProperty> createNavigationPropertyList(final String... propertyNames)
	{
		return Stream.of(propertyNames).map(this::createNavigationProperty).collect(Collectors.toList());
	}

	private NavigationProperty createNavigationProperty(final String propertyName)
	{
		return new NavigationProperty().setName(propertyName);
	}
}