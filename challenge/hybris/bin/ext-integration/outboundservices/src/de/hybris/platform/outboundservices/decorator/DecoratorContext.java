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

import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import de.hybris.platform.core.model.ItemModel;

import org.springframework.util.StringUtils;

import com.google.common.base.Preconditions;

public class DecoratorContext
{
	private ItemModel itemModel;
	private String integrationObjectCode;
	private String integrationObjectItemCode;
	private ConsumedDestinationModel destinationModel;

	protected DecoratorContext()
	{
		// empty constructor
	}

	public static DecoratorContextBuilder decoratorContextBuilder()
	{
		return new DecoratorContextBuilder();
	}

	public ItemModel getItemModel()
	{
		return itemModel;
	}

	public String getIntegrationObjectCode()
	{
		return integrationObjectCode;
	}

	public String getIntegrationObjectItemCode()
	{
		return integrationObjectItemCode;
	}

	public ConsumedDestinationModel getDestinationModel()
	{
		return destinationModel;
	}

	public static class DecoratorContextBuilder
	{
		private DecoratorContext decoratorContext;

		protected DecoratorContextBuilder()
		{
			this.decoratorContext = new DecoratorContext();
		}

		public DecoratorContextBuilder withItemModel(final ItemModel itemModel)
		{
			this.decoratorContext.itemModel = itemModel;
			return this;
		}

		public DecoratorContextBuilder withIntegrationObjectCode(final String integrationObjectCode)
		{
			this.decoratorContext.integrationObjectCode = integrationObjectCode;
			return this;
		}

		public DecoratorContextBuilder withDestinationModel(final ConsumedDestinationModel destinationModel)
		{
			this.decoratorContext.destinationModel = destinationModel;
			return this;
		}

		public DecoratorContextBuilder withIntegrationObjectItemCode(final String integrationObjectItemCode)
		{
			this.decoratorContext.integrationObjectItemCode = integrationObjectItemCode;
			return this;
		}

		public DecoratorContext build()
		{
			Preconditions.checkArgument(decoratorContext.destinationModel != null, "destinationModel cannot be null");
			Preconditions.checkArgument(! StringUtils.isEmpty(decoratorContext.integrationObjectCode), "integrationObjectCode cannot be null or empty");
			Preconditions.checkArgument(decoratorContext.itemModel != null, "itemModel cannot be null");

			return decoratorContext;
		}
	}
}
