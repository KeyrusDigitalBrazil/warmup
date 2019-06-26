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
package de.hybris.platform.odata2services.odata.persistence;

import de.hybris.platform.core.model.ItemModel;

import org.apache.olingo.odata2.api.edm.EdmException;

import com.google.common.base.Preconditions;

public class ItemConversionRequest extends AbstractRequest
{
	private ItemModel itemModel;
	private int conversionLevel;
	private ConversionOptions options;

	protected ItemConversionRequest()
	{
		options = new ConversionOptions();
	}

	public static ItemConversionRequestBuilder itemConversionRequestBuilder()
	{
		return new ItemConversionRequestBuilder(new ItemConversionRequest());
	}

	public ItemModel getItemModel()
	{
		return itemModel;
	}

	public ConversionOptions getOptions()
	{
		return options;
	}

	protected void setItemModel(final ItemModel itemModel)
	{
		this.itemModel = itemModel;
	}

	protected void setOptions(final ConversionOptions options)
	{
		this.options = options;
	}

	public ItemConversionRequest propertyConversionRequest(final String propertyName, final ItemModel item) throws EdmException
	{
		final ItemConversionRequest subrequest = itemConversionRequestBuilder().from(this)
				.withOptions(getOptions().navigate(propertyName))
				.withEntitySet(getEntitySetReferencedByProperty(propertyName))
				.withItemModel(item)
				.build();
		subrequest.conversionLevel = conversionLevel + 1;
		return subrequest;
	}

	/**
	 * Determines how deep the item being converted by this request is located in the object graph. Every time
	 * {@link #propertyConversionRequest(String, ItemModel)} is called it increases the conversion level (the distance of the
	 * resulting request from the very original conversion request).
	 * @return number of times {@link #propertyConversionRequest(String, ItemModel)} was called from the original request to get
	 * this request or 0, if this request is the original request. For example, if an item {@code Product} has property "catalog",
	 * which refers to {@code Catalog} item, then conversion request for {@code Product} item will have conversion level 0,
	 * conversion request for {@code Catalog} item will have conversion level 1, and so on for any subsequent item referenced from
	 * the {@code Catalog}.
	 */
	public int getConversionLevel()
	{
		return conversionLevel;
	}

	public static class ItemConversionRequestBuilder extends AbstractRequestBuilder<ItemConversionRequestBuilder, ItemConversionRequest>
	{
		ItemConversionRequestBuilder(final ItemConversionRequest conversionRequest)
		{
			super(conversionRequest);
		}

		public ItemConversionRequestBuilder withItemModel(final ItemModel itemModel)
		{
			request().setItemModel(itemModel);
			return myself();
		}

		public ItemConversionRequestBuilder withOptions(final ConversionOptions options)
		{
			request().setOptions(options);
			return myself();
		}

		@Override
		public ItemConversionRequestBuilder from(final ItemConversionRequest request)
		{
			withItemModel(request.getItemModel());
			withOptions(request.getOptions());

			return super.from(request);
		}

		@Override
		protected void assertValidValues() throws EdmException
		{
			super.assertValidValues();
			Preconditions.checkArgument(request().getIntegrationObjectCode() != null, "IntegrationObject must be provided");
			Preconditions.checkArgument(request().getItemModel() != null, "itemModel cannot be null");
			Preconditions.checkArgument(request().getOptions() != null, "ConversionOptions must be provided");
		}
	}
}
