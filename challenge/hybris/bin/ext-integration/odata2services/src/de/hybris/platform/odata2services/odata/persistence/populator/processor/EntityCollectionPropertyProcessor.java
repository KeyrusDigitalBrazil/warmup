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
package de.hybris.platform.odata2services.odata.persistence.populator.processor;

import static de.hybris.platform.odata2services.odata.EdmAnnotationUtils.isAutoCreate;
import static de.hybris.platform.odata2services.odata.EdmAnnotationUtils.isPartOf;
import static de.hybris.platform.odata2services.odata.persistence.ConversionOptions.conversionOptionsBuilder;
import static de.hybris.platform.odata2services.odata.persistence.ItemConversionRequest.itemConversionRequestBuilder;
import static de.hybris.platform.odata2services.odata.persistence.StorageRequest.storageRequestBuilder;

import de.hybris.platform.core.enums.TypeOfCollectionEnum;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.CollectionTypeModel;
import de.hybris.platform.core.model.type.TypeModel;
import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor;
import de.hybris.platform.odata2services.odata.persistence.ConversionOptions;
import de.hybris.platform.odata2services.odata.persistence.InvalidDataException;
import de.hybris.platform.odata2services.odata.persistence.ItemConversionRequest;
import de.hybris.platform.odata2services.odata.persistence.ModelEntityService;
import de.hybris.platform.odata2services.odata.persistence.StorageRequest;
import de.hybris.platform.odata2services.odata.persistence.creation.CreateItemStrategy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.olingo.odata2.api.edm.EdmAnnotatable;
import org.apache.olingo.odata2.api.edm.EdmEntitySet;
import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.apache.olingo.odata2.api.ep.feed.ODataFeed;
import org.apache.olingo.odata2.api.uri.NavigationSegment;
import org.springframework.beans.factory.annotation.Required;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class EntityCollectionPropertyProcessor extends AbstractCollectionPropertyProcessor
{
	private ModelEntityService modelEntityService;
	private CreateItemStrategy createItemStrategy;

	@Override
	protected boolean isApplicable(final TypeAttributeDescriptor typeAttributeDescriptor)
	{
		return typeAttributeDescriptor.isCollection() && !typeAttributeDescriptor.isPrimitive();
	}

	@Override
	protected void processItemInternal(final ItemModel item, final String entryPropertyName, final Object value,
			final StorageRequest request) throws EdmException
	{
		Preconditions.checkArgument(ODataFeed.class.isAssignableFrom(value.getClass()));
		final List<ODataEntry> collectionEntries = ((ODataFeed) value).getEntries();

		final Collection<ItemModel> newCollection =
				getNewCollectionEntries(request, collectionEntries, entryPropertyName, item);

		final AttributeDescriptorModel attributeDescriptor = getAttributeDescriptor(item, entryPropertyName, request);
		final String itemAttributeName = attributeDescriptor.getQualifier();

		final Collection<ItemModel> collection = getNewCollectionFor(attributeDescriptor);
		if (getModelService().getAttributeValue(item, itemAttributeName) != null)
		{
			collection.addAll(getModelService().getAttributeValue(item, itemAttributeName));
		}
		collection.addAll(newCollection.stream()
				.filter(v -> !collection.contains(v))
				.collect(Collectors.toList()));

		getModelService().setAttributeValue(item, itemAttributeName, collection);
	}

	protected Collection<ItemModel> getNewCollectionFor(final AttributeDescriptorModel attr)
	{
		final TypeModel type = attr.getAttributeType();
		return type instanceof CollectionTypeModel && ((CollectionTypeModel) type).getTypeOfCollection() == TypeOfCollectionEnum.SET
				? Sets.newLinkedHashSet()
				: Lists.newArrayList();
	}

	protected Collection<ItemModel> getNewCollectionEntries(final StorageRequest request,
			final List<ODataEntry> entries, final String propertyName, final ItemModel parent) throws EdmException
	{
		final EdmAnnotatable property = (EdmAnnotatable) request.getEntityType().getProperty(propertyName);
		final boolean partOf = isPartOf(property);
		final EdmEntitySet relatedEntitySet = request.getEntitySetReferencedByProperty(propertyName);

		final Collection<ItemModel> collectionItems = Lists.newLinkedList();
		for (final ODataEntry oDataEntry : entries)
		{
			final String integrationKey = getModelEntityService().addIntegrationKeyToODataEntry(relatedEntitySet, oDataEntry);

			final StorageRequest innerStorageRequest = storageRequestBuilder().from(request)
					.withEntitySet(relatedEntitySet)
					.withODataEntry(oDataEntry)
					.withIntegrationKey(integrationKey)
					.build();
			final ItemModel item = getModelEntityService()
					.createOrUpdateItem(innerStorageRequest, getCreateItemStrategy());

			if (!partOf && !isAutoCreate(property) && getModelService().isNew(item))
			{
				throw new InvalidDataException("missing_nav_property", "Required NavigationProperty for EntityType [" +
						request.getEntityType() + "] does not exist in the System.");
			}

			if (partOf && getModelService().isNew(item))
			{
				item.setOwner(parent);
			}
			collectionItems.add(item);
		}
		return collectionItems;
	}

	@Override
	protected List<ODataEntry> deriveDataFeedEntries(final ItemConversionRequest request, final String propertyName, final Object value) throws EdmException
	{
		final EdmEntitySet relatedEntitySet = request.getEntitySetReferencedByProperty(propertyName);

		final List<NavigationSegment> navigationSegments = request.getOptions().getNavigationSegments();

		final List<NavigationSegment> ns = navigationSegments.isEmpty() ?
				Collections.emptyList() : navigationSegments.subList(1, navigationSegments.size());

		final ConversionOptions options = conversionOptionsBuilder().from(request.getOptions())
				.withNavigationSegments(ns)
				.withIncludeCollections(false)
				.build();

		final ItemConversionRequest relatedRequest = itemConversionRequestBuilder().from(request)
				.withOptions(options)
				.withEntitySet(relatedEntitySet).build();

		return getListOfODataEntries((Collection<?>) value, relatedRequest);
	}

	protected List<ODataEntry> getListOfODataEntries(final Collection<?> values, final ItemConversionRequest request)
			throws EdmException
	{
		final List<ODataEntry> list = new ArrayList<>(values.size());
		for (final Object item : values)
		{
			if (item instanceof ItemModel)
			{
				final ItemConversionRequest newRequest = itemConversionRequestBuilder().from(request)
						.withItemModel((ItemModel) item)
						.build();
				final ODataEntry newEntry = modelEntityService.getODataEntry(newRequest);
				list.add(newEntry);
			}
		}
		return list;
	}

	protected ModelEntityService getModelEntityService()
	{
		return modelEntityService;
	}

	@Required
	public void setModelEntityService(final ModelEntityService modelEntityService)
	{
		this.modelEntityService = modelEntityService;
	}

	protected CreateItemStrategy getCreateItemStrategy()
	{
		return createItemStrategy;
	}

	@Required
	public void setCreateItemStrategy(final CreateItemStrategy createItemStrategy)
	{
		this.createItemStrategy = createItemStrategy;
	}
}
