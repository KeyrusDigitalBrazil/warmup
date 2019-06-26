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

import static de.hybris.platform.integrationservices.model.BaseMockItemAttributeModelBuilder.simpleAttributeBuilder;
import static de.hybris.platform.integrationservices.model.MockIntegrationObjectItemModelBuilder.itemModelBuilder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel;

import org.junit.Test;

@UnitTest
public class DefaultAlphabeticalIntegrationKeyGeneratorUnitTest
{
	private final DefaultAlphabeticalIntegrationKeyMetadataGenerator integrationKeyGenerator = new DefaultAlphabeticalIntegrationKeyMetadataGenerator();

	@Test
	public void testGenerateKeyMetadataForNull()
	{
		assertThatThrownBy(() -> integrationKeyGenerator.generateKeyMetadata(null)).isInstanceOf( IllegalArgumentException.class);
	}

	@Test
	public void testGenerateKeyMetadataNoUniqueAttributes()
	{
		final IntegrationObjectItemModel item = itemModelBuilder()
				.withCode("ItemWithNoUniqueProperties")
				.withAttribute(simpleAttributeBuilder().withName("name"))
				.build();

		assertThat(integrationKeyGenerator.generateKeyMetadata(item)).isEqualTo("");
	}

	@Test
	public void testGenerateKeyMetadataSimple()
	{
		final IntegrationObjectItemModel item = itemModelBuilder()
				.withCode("MyItem")
				.withUniqueAttribute(simpleAttributeBuilder().withName("code").withIntegrationObjectItemCode("MyItem"))
				.build();

		final String keyMetadata = integrationKeyGenerator.generateKeyMetadata(item);
		assertThat(keyMetadata)
				.isNotNull()
				.isEqualTo("MyItem_code");
	}

	@Test
	public void testKeyGenerateMetadataKeyWithTwoAttributesInSameType()
	{
		final IntegrationObjectItemModel item = itemModelBuilder()
				.withCode("Type")
				.withUniqueAttribute(simpleAttributeBuilder().withName("attr1").withIntegrationObjectItemCode("Type"))
				.withUniqueAttribute(simpleAttributeBuilder().withName("attr2").withIntegrationObjectItemCode("Type"))
				.build();

		final String keyMetadata = integrationKeyGenerator.generateKeyMetadata(item);
		assertThat(keyMetadata)
				.isNotNull()
				.isEqualTo("Type_attr1|Type_attr2");
	}

	@Test
	public void testGenerateKeyMetadataMultipleTypes()
	{
		final IntegrationObjectItemModel item = itemModelBuilder()
				.withCode("TypeA")
				.withUniqueAttribute(simpleAttributeBuilder().withName("attrA").withIntegrationObjectItemCode("TypeA"))
				.withUniqueAttribute(simpleAttributeBuilder().withName("attrB").withIntegrationObjectItemCode("TypeB"))
				.withUniqueAttribute(simpleAttributeBuilder().withName("attrC").withIntegrationObjectItemCode("TypeC"))
				.build();

		final String keyMetadata = integrationKeyGenerator.generateKeyMetadata(item);
		assertThat(keyMetadata)
				.isNotNull()
				.isEqualTo("TypeA_attrA|TypeB_attrB|TypeC_attrC");
	}

	@Test
	public void testDuplicateUniqueAttributesAppearOnlyOnce()
	{
		final IntegrationObjectItemModel item = itemModelBuilder()
				.withCode("TypeA")
				.withUniqueAttribute(simpleAttributeBuilder().withName("attrC").withIntegrationObjectItemCode("TypeC"))
				.withUniqueAttribute(simpleAttributeBuilder().withName("attrC").withIntegrationObjectItemCode("TypeC"))
				.withUniqueAttribute(simpleAttributeBuilder().withName("attrB").withIntegrationObjectItemCode("TypeB"))
				.build();

		final String keyMetadata = integrationKeyGenerator.generateKeyMetadata(item);
		assertThat(keyMetadata)
				.isNotNull()
				.isEqualTo("TypeB_attrB|TypeC_attrC");
	}
}