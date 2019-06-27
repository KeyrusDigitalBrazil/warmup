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
package de.hybris.platform.outboundservices.decorator;

import static de.hybris.platform.outboundservices.decorator.DecoratorContext.decoratorContextBuilder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import de.hybris.platform.core.model.ItemModel;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DecoratorContextBuilderUnitTest
{
	private static final String INTEGRATION_OBJECT = "IntegrationObjectCode";
	private static final String INTEGRATION_OBJECT_ITEM = "IntegrationObjectItemCode";

	@Mock
	private ItemModel itemModel;
	@Mock
	private ConsumedDestinationModel destinationModel;

	@Test
	public void testBuild()
	{
		final DecoratorContext context = decoratorContextBuilder().withDestinationModel(destinationModel)
																  .withIntegrationObjectCode(INTEGRATION_OBJECT)
																  .withIntegrationObjectItemCode(INTEGRATION_OBJECT_ITEM)
																  .withItemModel(itemModel)
																  .build();

		assertThat(context).isNotNull()
						   .hasFieldOrPropertyWithValue("itemModel", itemModel)
						   .hasFieldOrPropertyWithValue("destinationModel", destinationModel)
						   .hasFieldOrPropertyWithValue("integrationObjectItemCode", INTEGRATION_OBJECT_ITEM)
						   .hasFieldOrPropertyWithValue("integrationObjectCode", INTEGRATION_OBJECT);
	}

	@Test
	public void testBuild_noItemModel()
	{
		assertThatThrownBy(() -> decoratorContextBuilder().withDestinationModel(destinationModel)
														  .withIntegrationObjectCode(INTEGRATION_OBJECT)
														  .withIntegrationObjectItemCode(INTEGRATION_OBJECT_ITEM)
														  .build())
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("itemModel cannot be null");
	}

	@Test
	public void testBuild_noDestinationModel()
	{
		assertThatThrownBy(() -> decoratorContextBuilder().withIntegrationObjectCode(INTEGRATION_OBJECT)
														  .withItemModel(itemModel)
														  .withIntegrationObjectItemCode(INTEGRATION_OBJECT_ITEM)
														  .build())
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("destinationModel cannot be null");
	}

	@Test
	public void testBuild_noIntegrationObjectCode()
	{
		assertThatThrownBy(() -> decoratorContextBuilder().withDestinationModel(destinationModel)
														  .withItemModel(itemModel)
														  .withIntegrationObjectItemCode(INTEGRATION_OBJECT_ITEM)
														  .build())
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("integrationObjectCode cannot be null or empty");
	}

	@Test
	public void testBuild_noIntegrationObjectItemCode()
	{
		final DecoratorContext context =  decoratorContextBuilder().withIntegrationObjectCode(INTEGRATION_OBJECT)
																   .withDestinationModel(destinationModel)
																   .withItemModel(itemModel)
																   .build();

		assertThat(context).isNotNull()
						   .hasFieldOrPropertyWithValue("itemModel", itemModel)
						   .hasFieldOrPropertyWithValue("destinationModel", destinationModel)
						   .hasFieldOrPropertyWithValue("integrationObjectItemCode", null)
						   .hasFieldOrPropertyWithValue("integrationObjectCode", INTEGRATION_OBJECT);
	}
}
