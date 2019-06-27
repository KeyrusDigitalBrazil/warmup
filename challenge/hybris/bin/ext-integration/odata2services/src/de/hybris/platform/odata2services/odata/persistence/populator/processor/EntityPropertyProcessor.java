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

import static de.hybris.platform.integrationservices.constants.IntegrationservicesConstants.INTEGRATION_KEY_PROPERTY_NAME;
import static de.hybris.platform.odata2services.constants.Odata2servicesConstants.ENUM_PROPERTY_NAME;
import static de.hybris.platform.odata2services.odata.EdmAnnotationUtils.isAutoCreate;
import static de.hybris.platform.odata2services.odata.EdmAnnotationUtils.isPartOf;
import static de.hybris.platform.odata2services.odata.persistence.StorageRequest.storageRequestBuilder;

import de.hybris.platform.core.HybrisEnumValue;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.enumeration.EnumerationValueModel;
import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor;
import de.hybris.platform.odata2services.odata.EdmAnnotationUtils;
import de.hybris.platform.odata2services.odata.persistence.ConversionOptions;
import de.hybris.platform.odata2services.odata.persistence.ItemConversionRequest;
import de.hybris.platform.odata2services.odata.persistence.ModelEntityService;
import de.hybris.platform.odata2services.odata.persistence.StorageRequest;
import de.hybris.platform.odata2services.odata.persistence.creation.CreateItemStrategy;
import de.hybris.platform.odata2services.odata.persistence.creation.NeverCreateItemStrategy;

import org.apache.olingo.odata2.api.edm.EdmAnnotatable;
import org.apache.olingo.odata2.api.edm.EdmEntitySet;
import org.apache.olingo.odata2.api.edm.EdmEntityType;
import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.edm.EdmTyped;
import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.apache.olingo.odata2.core.ep.entry.EntryMetadataImpl;
import org.apache.olingo.odata2.core.ep.entry.MediaMetadataImpl;
import org.apache.olingo.odata2.core.ep.entry.ODataEntryImpl;
import org.apache.olingo.odata2.core.uri.ExpandSelectTreeNodeImpl;
import org.springframework.beans.factory.annotation.Required;

import com.google.common.collect.Maps;

public class EntityPropertyProcessor extends AbstractPropertyProcessor
{
	private ModelEntityService modelEntityService;
	private CreateItemStrategy createItemStrategy;

	@Override
	protected boolean isApplicable(final TypeAttributeDescriptor typeAttributeDescriptor)
	{
		return !typeAttributeDescriptor.isCollection() && !typeAttributeDescriptor.isPrimitive();
	}

	@Override
	protected void processItemInternal(final ItemModel item, final String entryPropertyName, final Object value,
			final StorageRequest request) throws EdmException
	{
		final ODataEntry oDataEntry = (ODataEntry) value;
		final EdmEntitySet relatedEntitySet = request.getEntitySetReferencedByProperty(entryPropertyName);
		final String integrationKey = getModelEntityService().addIntegrationKeyToODataEntry(relatedEntitySet, oDataEntry);

		final StorageRequest innerStorageRequest = storageRequestBuilder().from(request)
				.withEntitySet(relatedEntitySet)
				.withODataEntry(oDataEntry)
				.withIntegrationKey(integrationKey)
				.build();
		final ItemModel relatedItem = getModelEntityService()
				.createOrUpdateItem(innerStorageRequest, determineCreateStrategy(request.getEntityType(), entryPropertyName));

		final EdmAnnotatable property = (EdmAnnotatable) request.getEntityType().getProperty(entryPropertyName);

		if (isPartOf(property) && getModelService().isNew(item))
		{
			relatedItem.setOwner(item);
		}

		final String integrationObjectItemCode = request.getEntityType().getName();
		final String itemPropertyName = getIntegrationObjectService()
				.findItemAttributeName(request.getIntegrationObjectCode(), integrationObjectItemCode, entryPropertyName);

		if (relatedItem instanceof EnumerationValueModel)
		{
			final Object relatedItemValue = getModelService().get(relatedItem.getPk());
			getModelService().setAttributeValue(item, itemPropertyName, relatedItemValue);
		}
		else
		{
			if (isItemPropertySettable(item, entryPropertyName, request))
			{
				getModelService().setAttributeValue(item, itemPropertyName, relatedItem);
			}
		}
	}

	@Override
	protected void processEntityInternal(final ODataEntry oDataEntry, final String propertyName, final Object value,
			final ItemConversionRequest conversionRequest) throws EdmException
	{
		if (value instanceof EnumerationValueModel)
		{
			oDataEntry.getProperties().putIfAbsent(propertyName, ((EnumerationValueModel) value).getCode());
		}
		else if (value instanceof HybrisEnumValue)
		{
			final HybrisEnumValue enumValue = ((HybrisEnumValue) value);

			final ODataEntry entry = new ODataEntryImpl(Maps.newHashMap(), new MediaMetadataImpl(),
					new EntryMetadataImpl(), new ExpandSelectTreeNodeImpl());

			final String enumValueCode = enumValue.getCode();
			entry.getProperties().put(INTEGRATION_KEY_PROPERTY_NAME, enumValueCode);

			entry.getProperties().putIfAbsent(ENUM_PROPERTY_NAME, enumValueCode);
			oDataEntry.getProperties().putIfAbsent(propertyName, entry);
		}
		else if (value != null)
		{
			final ItemConversionRequest subRequest = conversionRequest.propertyConversionRequest(propertyName, (ItemModel) value);
			final ODataEntry entry = getModelEntityService().getODataEntry(subRequest);
			oDataEntry.getProperties().putIfAbsent(propertyName, entry);
		}
	}

	@Override
	protected boolean shouldPropertyBeConverted(final ItemConversionRequest request, final String propertyName) throws EdmException
	{
		final ConversionOptions options = request.getOptions();
		final EdmTyped np = request.getEntityType().getProperty(propertyName);

		return (request.getConversionLevel() == 0 && (!options.isNavigationSegmentPresent() || options.isNextNavigationSegment(propertyName)))
				|| EdmAnnotationUtils.isKeyProperty(np);
	}

	protected CreateItemStrategy determineCreateStrategy(final EdmEntityType entityType, final String propertyName)
			throws EdmException
	{
		final EdmAnnotatable property = (EdmAnnotatable) entityType.getProperty(propertyName);
		return isAutoCreate(property) || isPartOf(property) ? getCreateItemStrategy() : new NeverCreateItemStrategy();
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
