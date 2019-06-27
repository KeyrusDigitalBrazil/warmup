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


import static de.hybris.platform.integrationservices.model.BaseMockItemAttributeModelBuilder.collectionAttributeBuilder;
import static de.hybris.platform.integrationservices.model.BaseMockItemAttributeModelBuilder.oneToOneRelationAttributeBuilder;
import static de.hybris.platform.integrationservices.model.BaseMockItemAttributeModelBuilder.simpleAttributeBuilder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemAttributeModel;

import org.apache.olingo.odata2.api.edm.EdmMultiplicity;
import org.apache.olingo.odata2.api.edm.FullQualifiedName;
import org.apache.olingo.odata2.api.edm.provider.Association;
import org.apache.olingo.odata2.api.edm.provider.AssociationEnd;
import org.junit.Test;

@UnitTest
public class CollectionAssociationGeneratorUnitTest
{
	private static final String CATEGORY = "Category";
	private static final String SOURCE = "MyB2BUnit";
	private static final String TARGET = "MyAddress";
	private static final String NAVIGATION_PROPERTY = "MyNavigationProperty";
	private final CollectionAssociationGenerator generator = new CollectionAssociationGenerator();

	@Test
	public void testGenerate()
	{
		final IntegrationObjectItemAttributeModel mockCollectionAttributeDefinitionModel =
				collectionAttributeBuilder()
					.withSource(SOURCE)
					.withTarget(TARGET)
					.withName(NAVIGATION_PROPERTY)
					.build();

		final Association association = generator.generate(mockCollectionAttributeDefinitionModel);

		assertThat(association).isEqualToComparingFieldByFieldRecursively(expectedAssociation());
	}

	@Test
	public void testGenerateForCollectionAttributeBuilderWhereSourceTypeIsSameAsTargetType()
	{
		final IntegrationObjectItemAttributeModel mockCollectionAttributeDefinitionModel =
				collectionAttributeBuilder()
						.withSource(CATEGORY)
						.withTarget(CATEGORY)
						.withName(NAVIGATION_PROPERTY)
						.build();

		final Association association = generator.generate(mockCollectionAttributeDefinitionModel);

		assertThat(association.getEnd1()).isEqualToComparingFieldByField(expectedComplexAssociationEnd(CATEGORY, EdmMultiplicity.ZERO_TO_ONE));
		assertThat(association.getEnd2()).isEqualToComparingFieldByField(expectedComplexAssociationEnd(CATEGORY, NAVIGATION_PROPERTY, EdmMultiplicity.MANY));
	}

	@Test
	public void testIsApplicableWhenFalse()
	{
		final IntegrationObjectItemAttributeModel relation = oneToOneRelationAttributeBuilder().build();
		final IntegrationObjectItemAttributeModel simple = simpleAttributeBuilder().build();

		assertThat(generator.isApplicable(relation)).as("Applicable to one-to-one relation").isFalse();
		assertThat(generator.isApplicable(simple)).as("Applicable to simple attribute").isFalse();
	}

	@Test
	public void testIsApplicableWhenTrue()
	{
		final IntegrationObjectItemAttributeModel model = collectionAttributeBuilder().build();
		assertThat(generator.isApplicable(model)).isTrue();
	}

	@Test
	public void testIsApplicableNullAttributeDefinition()
	{
		assertThatThrownBy(() -> generator.isApplicable(null)).isInstanceOf(IllegalArgumentException.class);
	}

	private Association expectedAssociation()
	{
		return new Association().setName("FK_" + SOURCE + "_" + NAVIGATION_PROPERTY)
				.setEnd1(expectedComplexAssociationEnd(SOURCE, EdmMultiplicity.ZERO_TO_ONE))
				.setEnd2(expectedComplexAssociationEnd(TARGET, EdmMultiplicity.MANY));
	}

	private static AssociationEnd expectedComplexAssociationEnd(final String type, final EdmMultiplicity multiplicity)
	{
		return expectedComplexAssociationEnd(type, type, multiplicity);
	}

	private static AssociationEnd expectedComplexAssociationEnd(final String type, final String role, final EdmMultiplicity multiplicity)
	{
		return new AssociationEnd()
				.setType(new FullQualifiedName("HybrisCommerceOData", type))
				.setRole(role)
				.setMultiplicity(multiplicity);
	}
}