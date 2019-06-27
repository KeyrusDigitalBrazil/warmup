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
package de.hybris.platform.odata2services.odata.persistence.impl;

import static de.hybris.platform.integrationservices.constants.IntegrationservicesConstants.INTEGRATION_KEY_PROPERTY_NAME;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.integrationservices.integrationkey.IntegrationKeyGenerator;
import de.hybris.platform.odata2services.odata.persistence.ItemConversionRequest;
import de.hybris.platform.odata2services.odata.persistence.ItemLookupRequest;
import de.hybris.platform.odata2services.odata.persistence.ModelEntityService;
import de.hybris.platform.odata2services.odata.persistence.StorageRequest;
import de.hybris.platform.odata2services.odata.persistence.creation.CreateItemStrategy;
import de.hybris.platform.odata2services.odata.persistence.lookup.ItemLookupResult;
import de.hybris.platform.odata2services.odata.persistence.lookup.ItemLookupStrategy;
import de.hybris.platform.odata2services.odata.persistence.populator.EntityModelPopulator;
import de.hybris.platform.odata2services.odata.persistence.validator.CreateItemValidator;

import java.util.List;

import org.apache.olingo.odata2.api.edm.EdmEntitySet;
import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.apache.olingo.odata2.core.ep.entry.EntryMetadataImpl;
import org.apache.olingo.odata2.core.ep.entry.MediaMetadataImpl;
import org.apache.olingo.odata2.core.ep.entry.ODataEntryImpl;
import org.apache.olingo.odata2.core.uri.ExpandSelectTreeNodeImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.google.common.collect.Maps;

/**
 * Default implementation for {@link ModelEntityService}
 */
public class DefaultModelEntityService implements ModelEntityService
{
	private static final Logger LOG = LoggerFactory.getLogger(DefaultModelEntityService.class);

	private EntityModelPopulator entityModelPopulator;
	private ItemLookupStrategy itemLookupStrategy;
	private List<CreateItemValidator> createItemValidators;
	private IntegrationKeyGenerator<EdmEntitySet, ODataEntry> integrationKeyGenerator;

	@Override
	public ItemModel createOrUpdateItem(final StorageRequest request, final CreateItemStrategy createItemStrategy) throws EdmException
	{
		final EdmEntitySet edmEntitySet = request.getEntitySet();
		final ODataEntry oDataEntry = request.getODataEntry();

		LOG.trace("Converting ODataEntry To ItemModel: {}", oDataEntry);

		final ItemModel item = getItem(request, createItemStrategy);

		populateItem(request, item);
		addIntegrationKeyToODataEntry(edmEntitySet, oDataEntry);

		return item;
	}

	protected ItemModel getItem(final StorageRequest request, final CreateItemStrategy createItemStrategy) throws EdmException
	{
		if(request.getContextItem().isPresent())
		{
			return request.getContextItem().get();
		}
		else
		{
			ItemModel item = lookup(request.toLookupRequest());
			if (item == null)
			{
				item = createItem(request, createItemStrategy);
			}
			request.putItem(item);
			return item;
		}
	}

	@Override
	public ODataEntry getODataEntry(final ItemConversionRequest conversionRequest) throws EdmException
	{
		final ODataEntry entry = new ODataEntryImpl(Maps.newHashMap(), new MediaMetadataImpl(),
				new EntryMetadataImpl(), new ExpandSelectTreeNodeImpl());

		entityModelPopulator.populateEntity(entry, conversionRequest);
		addIntegrationKeyToODataEntry(conversionRequest.getEntitySet(), entry);

		return entry;
	}

	@Override
	public String addIntegrationKeyToODataEntry(final EdmEntitySet entitySet,
			final ODataEntry oDataEntry)
	{
		final String integrationKey = getIntegrationKeyGenerator().generate(entitySet, oDataEntry);
		oDataEntry.getProperties().put(INTEGRATION_KEY_PROPERTY_NAME, integrationKey);
		return integrationKey;
	}

	@Override
	public ItemModel lookup(final ItemLookupRequest lookupRequest) throws EdmException
	{
		for (final CreateItemValidator validator : createItemValidators)
		{
			validator.beforeItemLookup(lookupRequest.getEntityType(), lookupRequest.getODataEntry());
		}

		return getItemLookupStrategy().lookup(lookupRequest);
	}

	@Override
	public ItemLookupResult<ItemModel> lookupItems(final ItemLookupRequest lookupRequest) throws EdmException
	{
		return getItemLookupStrategy().lookupItems(lookupRequest);
	}

	@Override
	public int count(final ItemLookupRequest lookupRequest) throws EdmException
	{
		return getItemLookupStrategy().count(lookupRequest);
	}

	protected void populateItem(final StorageRequest request, final ItemModel item) throws EdmException
	{
		for (final CreateItemValidator validator : createItemValidators)
		{
			validator.beforePopulateItem(request.getEntityType(), request.getODataEntry());
		}

		entityModelPopulator.populateItem(item, request);
	}

	protected ItemModel createItem(final StorageRequest request,
			final CreateItemStrategy createItemStrategy) throws EdmException
	{
		for (final CreateItemValidator validator : createItemValidators)
		{
			validator.beforeCreateItem(request.getEntityType(), request.getODataEntry());
		}

		return createItemStrategy.createItem(request);
	}

	protected ItemLookupStrategy getItemLookupStrategy()
	{
		return itemLookupStrategy;
	}

	@Required
	public void setItemLookupStrategy(final ItemLookupStrategy itemLookupStrategy)
	{
		this.itemLookupStrategy = itemLookupStrategy;
	}

	protected EntityModelPopulator getEntityModelPopulator()
	{
		return entityModelPopulator;
	}

	@Required
	public void setEntityModelPopulator(final EntityModelPopulator entityModelPopulator)
	{
		this.entityModelPopulator = entityModelPopulator;
	}

	protected List<CreateItemValidator> getCreateItemValidators()
	{
		return createItemValidators;
	}

	@Required
	public void setCreateItemValidators(final List<CreateItemValidator> createItemValidators)
	{
		this.createItemValidators = createItemValidators;
	}

	protected IntegrationKeyGenerator<EdmEntitySet, ODataEntry> getIntegrationKeyGenerator()
	{
		return integrationKeyGenerator;
	}

	public void setIntegrationKeyGenerator(final IntegrationKeyGenerator<EdmEntitySet, ODataEntry> integrationKeyGenerator)
	{
		this.integrationKeyGenerator = integrationKeyGenerator;
	}
}
