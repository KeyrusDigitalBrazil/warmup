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
package de.hybris.platform.odata2services.odata.schema.association;

import static de.hybris.platform.integrationservices.model.BaseMockItemAttributeModelBuilder.complexRelationAttributeBuilder;
import static de.hybris.platform.integrationservices.model.BaseMockItemAttributeModelBuilder.oneToOneRelationAttributeBuilder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.enums.RelationEndCardinalityEnum;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemAttributeModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel;

import java.util.List;
import java.util.Optional;

import org.apache.olingo.odata2.api.edm.provider.Association;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Sets;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AssociationListGeneratorUnitTest
{
	private static final String SOURCE = "Product";
	private static final String UNIT_TARGET = "Unit";
	private static final String CATEGORY_TARGET = "Category";

	@InjectMocks
	private AssociationListGenerator associationListGenerator;
	@Mock
	private AssociationGeneratorRegistry mockAssociationGeneratorRegistry;
	@Mock
	private OneToOneAssociationGenerator mockSimpleAssociationGenerator;
	@Mock
	private AssociationGenerator mockComplexAssociationGenerator;

	@Mock
	private IntegrationObjectItemModel mockIntegrationObject;

	private final Association expectedUnit = new Association().setName(UNIT_TARGET);
	private final Association expectedCatg = new Association().setName(CATEGORY_TARGET);

	@Before
	public void setup()
	{
		when(mockSimpleAssociationGenerator.generate(any(IntegrationObjectItemAttributeModel.class))).thenReturn(expectedUnit);
		when(mockComplexAssociationGenerator.generate(any(IntegrationObjectItemAttributeModel.class))).thenReturn(expectedCatg);
	}

	@Test
	public void testGenerateForBothSimpleAndComplexAssociations()
	{
		givenIntegrationObjectWithSimpleAndComplexRelationAttributes();

		final List<Association> associationList = associationListGenerator.generate(Sets.newHashSet(mockIntegrationObject));

		assertThat(associationList).containsExactlyInAnyOrder(expectedUnit, expectedCatg);
	}

	@Test
	public void testGenerateForNoAssociations()
	{
		final IntegrationObjectItemAttributeModel simpleAttributeDefinition = mock(IntegrationObjectItemAttributeModel.class);
		when(mockIntegrationObject.getAttributes()).thenReturn(Sets.newHashSet(simpleAttributeDefinition));
		when(mockAssociationGeneratorRegistry.getAssociationGenerator(simpleAttributeDefinition)).thenReturn(Optional.empty());

		final List<Association> associationList = associationListGenerator.generate(Sets.newHashSet(mockIntegrationObject));

		assertThat(associationList.size()).isEqualTo(0);
	}

	@Test
	public void testGeneratorForNotApplicableAssociation()
	{
		final IntegrationObjectItemAttributeModel simpleAttributeDefinition = mock(IntegrationObjectItemAttributeModel.class);
		when(mockAssociationGeneratorRegistry.getAssociationGenerator(simpleAttributeDefinition)).thenReturn(Optional.of(mockSimpleAssociationGenerator));
		when(mockSimpleAssociationGenerator.generate(simpleAttributeDefinition)).thenReturn(null);

		final List<Association> associationList = associationListGenerator.generate(Sets.newHashSet(mockIntegrationObject));

		assertThat(associationList.size()).isEqualTo(0);
	}

	@Test
	public void testGenerateNullParameter()
	{
		assertThatThrownBy(() -> associationListGenerator.generate(null))
				.isInstanceOf(IllegalArgumentException.class);
	}

	private void givenIntegrationObjectWithSimpleAndComplexRelationAttributes()
	{
		final IntegrationObjectItemAttributeModel unitAttribute = mockSimpleAssociationAttribute();
		final IntegrationObjectItemAttributeModel catgAttribute = mockComplexAssociationAttribute();

		when(mockIntegrationObject.getAttributes()).thenReturn(Sets.newHashSet(unitAttribute, catgAttribute));
	}

	private IntegrationObjectItemAttributeModel mockSimpleAssociationAttribute()
	{
		final IntegrationObjectItemAttributeModel mockAttributeDefinitionModel =
				oneToOneRelationAttributeBuilder()
					.withSource(SOURCE)
					.withTarget(UNIT_TARGET)
					.build();
		when(mockAssociationGeneratorRegistry.getAssociationGenerator(mockAttributeDefinitionModel)).thenReturn(Optional.of(mockSimpleAssociationGenerator));
		when(mockSimpleAssociationGenerator.isApplicable(mockAttributeDefinitionModel)).thenReturn(true);
		return mockAttributeDefinitionModel;
	}

	private IntegrationObjectItemAttributeModel mockComplexAssociationAttribute()
	{
		final IntegrationObjectItemAttributeModel mockCategoryAttrDefinition =
				complexRelationAttributeBuilder()
					.withSource(SOURCE)
					.withTarget(CATEGORY_TARGET)
					.withSourceCardinality(RelationEndCardinalityEnum.MANY)
					.withTargetCardinality(RelationEndCardinalityEnum.ONE)
					.build();
		when(mockAssociationGeneratorRegistry.getAssociationGenerator(mockCategoryAttrDefinition)).thenReturn(Optional.of(mockComplexAssociationGenerator));
		when(mockComplexAssociationGenerator.isApplicable(mockCategoryAttrDefinition)).thenReturn(true);
		return mockCategoryAttrDefinition;
	}
}
