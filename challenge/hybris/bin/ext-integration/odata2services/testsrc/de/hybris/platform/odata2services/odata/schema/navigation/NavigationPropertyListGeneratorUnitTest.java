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
package de.hybris.platform.odata2services.odata.schema.navigation;

import static de.hybris.platform.integrationservices.model.BaseMockItemAttributeModelBuilder.oneToOneRelationAttributeBuilder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemAttributeModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.olingo.odata2.api.edm.provider.NavigationProperty;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Sets;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class NavigationPropertyListGeneratorUnitTest
{
	private static final String SOURCE = "Product";
	private static final String UNIT_TARGET = "Unit";

	@InjectMocks
	private NavigationPropertyListGenerator generator;

	@Mock
	private NavigationPropertyGenerator propertyGenerator;

	private final NavigationProperty navigationProperty = new NavigationProperty();

	@Before
	public void setup()
	{
		when(propertyGenerator.generate(any(IntegrationObjectItemAttributeModel.class)))
				.thenReturn(Optional.of(navigationProperty));
	}

	@Test
	public void testGenerateForSimpleComplexAndNoRelationshipAttributes()
	{
		final Set<IntegrationObjectItemAttributeModel> attributes = givenRelationAndNoRelationAttributes();

		final List<NavigationProperty> navigationPropertyList = generator.generate(attributes);

		assertThat(navigationPropertyList).containsExactly(navigationProperty);
	}

	@Test
	public void testGenerateForNoAttributes()
	{
		final List<NavigationProperty> navigationPropertyList = generator.generate(Sets.newHashSet());
		assertThat(navigationPropertyList).hasSize(0);
	}

	@Test
	public void testGenerateForSetWithNullEntry()
	{
		final IntegrationObjectItemAttributeModel attributeDefinitionModel =
				oneToOneRelationAttributeBuilder()
					.withSource(SOURCE)
					.withTarget(UNIT_TARGET)
					.build();
		final Set attributes = Sets.newHashSet(null, attributeDefinitionModel);
		@SuppressWarnings("unchecked") final List<NavigationProperty> navigationPropertyList = generator.generate(attributes);
		assertThat(navigationPropertyList).hasSize(1);
	}

	@Test
	public void testGenerateForSetWithNoRelations()
	{
		final List<NavigationProperty> navigationPropertyList = generator.generate(Sets.newHashSet(givenSimpleAttribute()));
		assertThat(navigationPropertyList).hasSize(0);
	}

	@Test
	public void testGenerateNullParameter()
	{
		assertThatThrownBy(() -> generator.generate(null))
				.isInstanceOf(IllegalArgumentException.class);
	}

	private Set<IntegrationObjectItemAttributeModel> givenRelationAndNoRelationAttributes()
	{
		final IntegrationObjectItemModel mockIntegrationObjectItemModel = mock(IntegrationObjectItemModel.class);
		final Set<IntegrationObjectItemAttributeModel> attributes = Sets.newHashSet(
				givenAssociationAttribute(),
				givenSimpleAttribute());

		when(mockIntegrationObjectItemModel.getAttributes()).thenReturn(attributes);

		return attributes;
	}

	private IntegrationObjectItemAttributeModel givenAssociationAttribute()
	{
		final IntegrationObjectItemAttributeModel mockAssociationAttribute =
				oneToOneRelationAttributeBuilder()
					.withSource(SOURCE)
					.withTarget(UNIT_TARGET)
					.build();
		when(propertyGenerator.generate(mockAssociationAttribute)).thenReturn(Optional.of(navigationProperty));
		return mockAssociationAttribute;
	}

	private IntegrationObjectItemAttributeModel givenSimpleAttribute()
	{
		final IntegrationObjectItemAttributeModel simpleAttributeDefinition = mock(IntegrationObjectItemAttributeModel.class);
		when(propertyGenerator.generate(simpleAttributeDefinition)).thenReturn(Optional.empty());
		return simpleAttributeDefinition;
	}
}
