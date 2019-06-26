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

import static de.hybris.platform.odata2services.odata.persistence.ItemLookupRequest.itemLookupRequestBuilder;

import de.hybris.platform.core.model.ItemModel;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import org.apache.olingo.odata2.api.edm.EdmException;

import com.google.common.base.Preconditions;

/**
 * Request which contains an item for persistence.
 */
public class StorageRequest extends CrudRequest
{
	private String postPersistHook = "";
	private String prePersistHook = "";
	private Locale contentLocale;
	private Map<String, Map<String, ItemModel>> items = new HashMap<>();

	private StorageRequest()
	{
		// private constructor
	}

	public static StorageRequestBuilder storageRequestBuilder()
	{
		return new StorageRequestBuilder(new StorageRequest());
	}

	public String getPrePersistHook()
	{
		return prePersistHook;
	}

	public String getPostPersistHook()
	{
		return postPersistHook;
	}

	protected void setPostPersistHook(final String postPersistHook)
	{
		this.postPersistHook = postPersistHook;
	}

	protected void setPrePersistHook(final String prePersistHook)
	{
		this.prePersistHook = prePersistHook;
	}

	/**
	 * Creates an {@link ItemLookupRequest} from this {@link StorageRequest}
	 * @return the newly constructed ItemLookupRequest
	 * @throws EdmException if encounters an OData problem
	 */
	public ItemLookupRequest toLookupRequest() throws EdmException
	{
		return itemLookupRequestBuilder()
				.withIntegrationKey(getIntegrationKey())
				.withAcceptLocale(getAcceptLocale())
				.withEntitySet(getEntitySet())
				.withIntegrationObject(getIntegrationObjectCode())
				.withODataEntry(getODataEntry())
				.withServiceRoot(getServiceRoot())
				.withContentType(getContentType())
				.withRequestUri(getRequestUri())
				.build();
	}

	public Locale getContentLocale()
	{
		return contentLocale;
	}

	protected void setContentLocale(final Locale contentLocale)
	{
		this.contentLocale = contentLocale;
	}

	private Map<String, Map<String, ItemModel>> getItems()
	{
		return items;
	}

	public Optional<ItemModel> getContextItem() throws EdmException
	{
		final String type = this.getEntityType().getName();
		if(getItems().get(type) != null)
		{
			final ItemModel item = getItems().get(type).get(this.getIntegrationKey());
			if(item != null)
			{
				return Optional.of(item);
			}
		}
		return Optional.empty();
	}

	public void putItem(final ItemModel item) throws EdmException
	{
		final String type = this.getEntityType().getName();

		final Map<String, ItemModel> existingItemsForType = this.getItems().get(type);
		if(existingItemsForType != null)
		{
			existingItemsForType.put(this.getIntegrationKey(), item);
		}
		else
		{
			final HashMap<String, ItemModel> newEntry = new HashMap<>();
			newEntry.put(this.getIntegrationKey(), item);
			this.getItems().put(type, newEntry);
		}
	}

	public static class StorageRequestBuilder extends DataRequestBuilder<StorageRequestBuilder, StorageRequest>
	{
		StorageRequestBuilder(final StorageRequest storageRequest)
		{
			super(storageRequest);
		}

		public StorageRequestBuilder withPrePersistHook(final String hook)
		{
			request().setPrePersistHook(hook);
			return this;
		}

		public StorageRequestBuilder withPostPersistHook(final String hook)
		{
			request().setPostPersistHook(hook);
			return this;
		}

		public StorageRequestBuilder withContentLocale(final Locale locale)
		{
			Preconditions.checkArgument(locale != null, "Content Locale must be provided");
			request().setContentLocale(locale);
			return this;
		}

		private StorageRequestBuilder withItems(final Map<String, Map<String, ItemModel>> items)
		{
			Preconditions.checkArgument(items != null, "ItemModels must be provided.");
			request().items = items;
			return myself();
		}

		@Override
		public StorageRequestBuilder from(final StorageRequest request)
		{
			return super.from(request)
					.withPrePersistHook(request.getPrePersistHook())
					.withPostPersistHook(request.getPostPersistHook())
					.withContentLocale(request.getContentLocale())
					.withItems(request.getItems());
		}
	}
}
