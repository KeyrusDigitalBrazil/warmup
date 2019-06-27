/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.commercefacades.util.builder;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.CommerceCartMetadata;

import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CommerceCartMetadataBuilderTest
{
	private CommerceCartMetadataBuilder commerceCartMetadataBuilder = new CommerceCartMetadataBuilder();

	@Test
	public void shouldBuildEmptyCommerceCartMetadata()
	{
		final CommerceCartMetadata cartMetadata = commerceCartMetadataBuilder.build();

		Assert.assertNotNull("Should return cart metadata", cartMetadata);
		Assert.assertEquals("Name should be empty optional", Optional.empty(), cartMetadata.getName());
		Assert.assertEquals("Description should be empty optional", Optional.empty(), cartMetadata.getDescription());
		Assert.assertEquals("Expiration time should be empty optional", Optional.empty(), cartMetadata.getExpirationTime());
	}
}
