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

import static de.hybris.platform.integrationservices.model.BaseMockAttributeDescriptorModelBuilder.attributeDescriptor;
import static de.hybris.platform.integrationservices.model.BaseMockItemAttributeModelBuilder.complexRelationAttributeBuilder;
import static de.hybris.platform.integrationservices.model.BaseMockItemAttributeModelBuilder.simpleAttributeBuilder;
import static de.hybris.platform.integrationservices.model.MockIntegrationObjectItemModelBuilder.itemModelBuilder;
import static de.hybris.platform.odata2services.odata.schema.utils.SchemaUtils.buildAssociationName;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.enums.RelationEndCardinalityEnum;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemAttributeModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel;
import de.hybris.platform.integrationservices.model.MockComplexRelationItemAttributeModelBuilder;
import de.hybris.platform.integrationservices.model.MockRelationAttributeDescriptorModelBuilder;
import de.hybris.platform.odata2services.odata.schema.utils.SchemaUtils;

import org.apache.olingo.odata2.api.edm.EdmMultiplicity;
import org.apache.olingo.odata2.api.edm.FullQualifiedName;
import org.apache.olingo.odata2.api.edm.provider.Association;
import org.apache.olingo.odata2.api.edm.provider.AssociationEnd;
import org.junit.Test;

@UnitTest
public class ComplexRelationCollectionAssociationGeneratorUnitTest
{
	private static final String SOURCE = "MyCatalog";
	private static final String TARGET = "MyCatalogVersion";
	private static final String NAVIGATION_PROPERTY = "catalogVersion";

	private final CollectionAssociationGenerator generator = new CollectionAssociationGenerator();

	@Test
	public void testGenerateWhenIsSourceTrueThenSourceAndTargetCardinalityAreNotReversed()
	{
		final IntegrationObjectItemAttributeModel mockAttributeDefinitionModel = relationAttribute(true)
						.withSourceCardinality(RelationEndCardinalityEnum.MANY)
						.withTargetCardinality(RelationEndCardinalityEnum.ONE)
						.withIsSource(true)
						.build();

		final Association association = generator.generate(mockAttributeDefinitionModel);

		assertThat(association)
				.isEqualToComparingFieldByFieldRecursively(expectedAssociation(EdmMultiplicity.MANY, EdmMultiplicity.ZERO_TO_ONE));
	}

	@Test
	public void testGenerateWhenIsSourceFalseThenSourceAndTargetCardinalityAreReversed()
	{
		final IntegrationObjectItemAttributeModel mockAttributeDefinitionModel = relationAttribute(false)
						.withSourceCardinality(RelationEndCardinalityEnum.MANY)
						.withTargetCardinality(RelationEndCardinalityEnum.ONE)
						.withIsSource(false)
						.build();

		final Association association = generator.generate(mockAttributeDefinitionModel);

		assertThat(association)
				.isEqualToComparingFieldByFieldRecursively(expectedAssociation(EdmMultiplicity.ZERO_TO_ONE, EdmMultiplicity.MANY));
	}

	@Test
	public void testGenerateForNotApplicableAttribute()
	{
		final IntegrationObjectItemAttributeModel mockAttributeDefinitionModel = simpleAttributeBuilder()
				.withAttributeDescriptor(attributeDescriptor())
				.build();

		assertThat(generator.isApplicable(mockAttributeDefinitionModel)).isFalse();
	}

	@Test
	public void testIsApplicableNullAttributeDefinition()
	{
		assertThatThrownBy(() -> generator.isApplicable(null)).isInstanceOf(IllegalArgumentException.class);
	}

	private Association expectedAssociation(final EdmMultiplicity source, final EdmMultiplicity target)
	{
		return new Association().setName(buildAssociationName(SOURCE, NAVIGATION_PROPERTY))
				.setEnd1(expectedComplexAssociationEnd(SOURCE, source))
				.setEnd2(expectedComplexAssociationEnd(TARGET, target));
	}

	private static AssociationEnd expectedComplexAssociationEnd(final String code, final EdmMultiplicity multiplicity)
	{
		return new AssociationEnd()
				.setType(new FullQualifiedName(SchemaUtils.NAMESPACE, code))
				.setRole(code)
				.setMultiplicity(multiplicity);
	}

	private static MockComplexRelationItemAttributeModelBuilder relationAttribute(final boolean isSource)
	{
		final IntegrationObjectItemModel targetItem = itemModelBuilder()
				.withCode(TARGET)
				.withAttribute(complexRelationAttributeBuilder().withName("catalog").withIsSource(isSource))
				.build();
		return complexRelationAttributeBuilder()
				.withName(NAVIGATION_PROPERTY)
				.withSource(SOURCE)
				.withReturnIntegrationObjectItem(targetItem)
				.withTargetAttribute(MockRelationAttributeDescriptorModelBuilder.relationAttribute().withQualifier("catalog"));
	}
}