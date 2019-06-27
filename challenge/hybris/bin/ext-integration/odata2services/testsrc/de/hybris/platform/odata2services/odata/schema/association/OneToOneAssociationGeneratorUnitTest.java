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
import de.hybris.platform.odata2services.odata.schema.utils.SchemaUtils;

import org.apache.commons.lang.StringUtils;
import org.apache.olingo.odata2.api.edm.EdmMultiplicity;
import org.apache.olingo.odata2.api.edm.FullQualifiedName;
import org.apache.olingo.odata2.api.edm.provider.Association;
import org.apache.olingo.odata2.api.edm.provider.AssociationEnd;
import org.junit.Test;

@UnitTest
public class OneToOneAssociationGeneratorUnitTest
{
	private static final String SOURCE = "MyProduct";
	private static final String TARGET = "MyUnit";
	private static final String NAVIGATION_PROPERTY = "navigationProperty";
	private static final String CATEGORY = "Category";

	private final OneToOneAssociationGenerator generator = new OneToOneAssociationGenerator();

	@Test
	public void testIsApplicableNullAttributeModel()
	{
		assertThatThrownBy(() -> generator.isApplicable(null)).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void testIsApplicableNullAttributeDescriptor()
	{
		assertThat(generator.isApplicable(simpleAttributeBuilder().build()))
				.isFalse();
	}

	@Test
	public void testIsApplicableCollectionType()
	{
		final IntegrationObjectItemAttributeModel mockAttributeDefinitionModel =
				collectionAttributeBuilder()
						.withSource(SOURCE)
						.withTarget("MyCollection")
						.build();

		assertThat(generator.isApplicable(mockAttributeDefinitionModel))
				.isFalse();
	}

	@Test
	public void testIsApplicableOneToOne()
	{
		final IntegrationObjectItemAttributeModel mockAttributeDefinitionModel =
				oneToOneRelationAttributeBuilder()
						.withSource(SOURCE)
						.withTarget(TARGET)
						.build();

		assertThat(generator.isApplicable(mockAttributeDefinitionModel))
				.isTrue();
	}

	@Test
	public void testGenerate()
	{
		final IntegrationObjectItemAttributeModel mockAttributeDefinitionModel =
				oneToOneRelationAttributeBuilder()
						.withSource(SOURCE)
						.withTarget(TARGET)
						.withName(NAVIGATION_PROPERTY)
						.build();
		final Association association = generator.generate(mockAttributeDefinitionModel);
		assertThat(association).isEqualToComparingFieldByFieldRecursively(expectedAssociation());
	}

	@Test
	public void testGenerateAssociationFromCategoryToCategory()
	{
		final IntegrationObjectItemAttributeModel mockAttributeDefinitionModel =
				oneToOneRelationAttributeBuilder()
						.withSource(CATEGORY)
						.withTarget(CATEGORY)
						.withName(NAVIGATION_PROPERTY)
						.build();
		final Association association = generator.generate(mockAttributeDefinitionModel);

		assertThat(association.getEnd1()).isEqualToComparingFieldByField(expectedSimpleAssociationEnd(CATEGORY));
		assertThat(association.getEnd2()).isEqualToComparingFieldByField(expectedSimpleAssociationEnd(CATEGORY, StringUtils.capitalize(NAVIGATION_PROPERTY)));
	}

	private Association expectedAssociation()
	{
		return new Association().setName("FK_" + SOURCE + "_" + NAVIGATION_PROPERTY)
				.setEnd1(expectedSimpleAssociationEnd(SOURCE))
				.setEnd2(expectedSimpleAssociationEnd(TARGET));
	}

	private static AssociationEnd expectedSimpleAssociationEnd(final String type)
	{
		return expectedSimpleAssociationEnd(type, type);
	}

	private static AssociationEnd expectedSimpleAssociationEnd(final String type, final String role)
	{
		return new AssociationEnd()
				.setType(new FullQualifiedName(SchemaUtils.NAMESPACE, type))
				.setRole(role)
				.setMultiplicity(EdmMultiplicity.ZERO_TO_ONE);
	}
}