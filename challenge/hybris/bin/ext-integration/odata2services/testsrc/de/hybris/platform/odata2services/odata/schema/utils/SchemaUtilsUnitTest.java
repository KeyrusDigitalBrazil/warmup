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

package de.hybris.platform.odata2services.odata.schema.utils;

import static de.hybris.platform.integrationservices.model.BaseMockAttributeDescriptorModelBuilder.attributeDescriptor;
import static de.hybris.platform.integrationservices.model.BaseMockItemAttributeModelBuilder.simpleAttributeBuilder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemAttributeModel;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.olingo.odata2.api.edm.FullQualifiedName;
import org.junit.Test;

@UnitTest
public class SchemaUtilsUnitTest
{
	@Test
	public void testFullyQualified()
	{
		assertThatThrownBy(() -> SchemaUtils.fullyQualified(null)).isInstanceOf(IllegalArgumentException.class);
		assertThatThrownBy(() -> SchemaUtils.fullyQualified("")).isInstanceOf(IllegalArgumentException.class);
		assertThatThrownBy(() -> SchemaUtils.fullyQualified(" ")).isInstanceOf(IllegalArgumentException.class);
		assertThat(SchemaUtils.fullyQualified("name")).isEqualTo("HybrisCommerceOData.name");
	}

	@Test
	public void testToFullyQualified()
	{
		assertThatThrownBy(() -> SchemaUtils.toFullQualifiedName(null)).isInstanceOf(IllegalArgumentException.class);
		assertThatThrownBy(() -> SchemaUtils.toFullQualifiedName("")).isInstanceOf(IllegalArgumentException.class);
		assertThatThrownBy(() -> SchemaUtils.toFullQualifiedName(" ")).isInstanceOf(IllegalArgumentException.class);
		assertThat(SchemaUtils.toFullQualifiedName("name")).isEqualTo(new FullQualifiedName("HybrisCommerceOData", "name"));
	}

	@Test
	public void testRemoveDuplicates()
	{
		final List<String> list = Arrays.asList("a", "b", "c", "a");
		assertThat(SchemaUtils.removeDuplicates(list, a -> a)).containsExactlyInAnyOrder("a", "b", "c");
	}
	
	@Test
	public void testFindFirstLocalizedAttributeWhenThereAreLocalizedAttributes()
	{
		final IntegrationObjectItemAttributeModel model = simpleAttributeBuilder()
				.withAttributeDescriptor(attributeDescriptor().withLocalized(true)).build();

		assertThat(SchemaUtils.findFirstLocalizedAttribute(Collections.singleton(model))).isPresent();
	}

	@Test
	public void testFindFirstLocalizedAttributeWhenThereAreNoLocalizedAttributes()
	{
		final IntegrationObjectItemAttributeModel model = simpleAttributeBuilder()
				.withAttributeDescriptor(attributeDescriptor().withLocalized(false)).build();

		assertThat(SchemaUtils.findFirstLocalizedAttribute(Collections.singleton(model))).isEmpty();
	}


}