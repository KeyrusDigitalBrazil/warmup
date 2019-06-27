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
package de.hybris.platform.integrationservices.model;

import static de.hybris.platform.integrationservices.model.BaseMockItemAttributeModelBuilder.complexRelationAttributeBuilder;
import static de.hybris.platform.integrationservices.model.BaseMockItemAttributeModelBuilder.oneToOneRelationAttributeBuilder;
import static de.hybris.platform.integrationservices.model.BaseMockItemAttributeModelBuilder.simpleAttributeBuilder;
import static de.hybris.platform.integrationservices.model.MockIntegrationObjectItemModelBuilder.itemModelBuilder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

import de.hybris.bootstrap.annotations.UnitTest;

import java.util.Collection;

import org.junit.Test;

@UnitTest
public class UniqueAttributesAttributeHandlerUnitTest
{
	private final UniqueAttributesAttributeHandler handler = new UniqueAttributesAttributeHandler();

	@Test
	public void testItemDoesNotHaveAttributes()
	{
		final IntegrationObjectItemModel item = itemModelBuilder().build();

		final Collection<IntegrationObjectItemAttributeModel> unique = handler.get(item);

		assertThat(unique).isEmpty();
	}

	@Test
	public void testItemDoesNotHaveUniqueAttribute()
	{
		final IntegrationObjectItemModel item = itemModelBuilder()
				.withAttribute(simpleAttributeBuilder())
				.withAttribute(oneToOneRelationAttributeBuilder().withTarget("Department"))
				.withAttribute(complexRelationAttributeBuilder().withTarget("Colleagues"))
				.build();

		final Collection<IntegrationObjectItemAttributeModel> unique = handler.get(item);

		assertThat(unique).isEmpty();
	}

	@Test
	public void testItemHasOnlySimpleAttributesUnique()
	{
		final IntegrationObjectItemModel item = itemModelBuilder()
				.withCode("Address")
				.withAttribute(simpleAttributeBuilder().withName("zipCode").unique())
				.withAttribute(simpleAttributeBuilder().withName("country").unique())
				.withAttribute(oneToOneRelationAttributeBuilder().withTarget("County"))
				.withAttribute(complexRelationAttributeBuilder().withTarget("Dwellers"))
				.build();

		final Collection<IntegrationObjectItemAttributeModel> unique = handler.get(item);

		assertThat(unique)
				.extracting("attributeName")
				.containsExactlyInAnyOrder("zipCode", "country");
	}

	@Test
	public void testItemHasOnlyReferenceAttributeUnique()
	{
		final IntegrationObjectItemModel item = itemModelBuilder()
				.withAttribute(oneToOneRelationAttributeBuilder()
						.withName("catalog")
						.unique()
						.withReturnIntegrationObjectItem(itemModelBuilder()
								.withCode("Catalog")
								.withAttribute(simpleAttributeBuilder()
										.withName("id")
										.unique())))
				.withAttribute(oneToOneRelationAttributeBuilder()
						.withName("categories")
						.withReturnIntegrationObjectItem(itemModelBuilder().withAttribute(simpleAttributeBuilder().withName("id"))))
				.build();

		final Collection<IntegrationObjectItemAttributeModel> unique = handler.get(item);

		assertThat(unique)
				.extracting("integrationObjectItem.code", "attributeName")
				.containsExactly(tuple("Catalog", "id"));

	}

	@Test
	public void testItemHasSimpleAndReferenceAttributesUnique()
	{
		final IntegrationObjectItemModel item = itemModelBuilder()
				.withCode("Product")
				.withAttribute(simpleAttributeBuilder().withName("code").unique())
				.withAttribute(complexRelationAttributeBuilder()
						.withName("catalog")
						.unique()
						.withReturnIntegrationObjectItem(
								itemModelBuilder()
										.withCode("CatalogVersion")
										.withAttribute(simpleAttributeBuilder().withName("version").unique())
										.withAttribute(oneToOneRelationAttributeBuilder()
												.withName("catalog")
												.unique()
												.withReturnIntegrationObjectItem(
														itemModelBuilder()
																.withCode("Catalog")
																.withAttribute(simpleAttributeBuilder().withName("id").unique())))))
				.build();

		final Collection<IntegrationObjectItemAttributeModel> unique = handler.get(item);

		assertThat(unique)
				.extracting("integrationObjectItem.code", "attributeName")
				.containsExactlyInAnyOrder(tuple("Product", "code"), tuple("CatalogVersion", "version"), tuple("Catalog", "id"));
	}

	@Test
	public void testItemHasUniqueReferenceAttributeReferencingItsItemType()
	{
		final IntegrationObjectItemModel item = itemModelBuilder()
				.withCode("FaultyItem")
				.withAttribute(complexRelationAttributeBuilder()
						.withName("cyclicKey")
						.unique()
						.withReturnIntegrationObjectItem(itemModelBuilder().withCode("FaultyItem")))
				.build();

		assertThatThrownBy(() -> handler.get(item))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage("Metadata error: key attribute 'cyclicKey' in item type 'FaultyItem' forms a cyclic return type dependency");
	}

	@Test
	public void testItemHasUniqueReferenceAttributeReferencingAnAlreadyProcessedType()
	{
		final IntegrationObjectItemModel item = itemModelBuilder()
				.withCode("A")
				.withAttribute(complexRelationAttributeBuilder()
						.withName("attribute")
						.unique()
						.withReturnIntegrationObjectItem(itemModelBuilder().withCode("B")
																		   .withAttribute(complexRelationAttributeBuilder().withName("cyclic")
																														   .unique()
																														   .withReturnIntegrationObject("A"))))
				.build();

		assertThatThrownBy(() -> handler.get(item))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage("Metadata error: key attribute 'cyclic' in item type 'B' forms a cyclic return type dependency");
	}
}