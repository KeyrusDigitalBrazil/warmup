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
import static de.hybris.platform.odata2services.constants.Odata2servicesConstants.LOCALIZED_ATTRIBUTE_NAME;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel;
import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor;
import de.hybris.platform.integrationservices.model.TypeDescriptor;
import de.hybris.platform.integrationservices.model.impl.ItemTypeDescriptor;
import de.hybris.platform.integrationservices.service.IntegrationObjectService;
import de.hybris.platform.odata2services.odata.persistence.AbstractRequest;
import de.hybris.platform.odata2services.odata.persistence.ItemConversionRequest;
import de.hybris.platform.odata2services.odata.persistence.StorageRequest;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.collections4.ListUtils;
import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.springframework.beans.factory.annotation.Required;

public abstract class AbstractPropertyProcessor implements PropertyProcessor
{
	private ModelService modelService;
	private IntegrationObjectService integrationObjectService;
	private TypeService typeService;

	@Override
	public void processItem(final ItemModel item, final StorageRequest request) throws EdmException
	{
		final Map<String, Object> properties = request.getODataEntry().getProperties();

		for (final Map.Entry<String, Object> entry : properties.entrySet())
		{
			final String propertyName = entry.getKey();
			final Object propertyValue = entry.getValue();

			if (isItemPropertySettable(item, propertyName, request))
			{
				final Optional<TypeAttributeDescriptor> attributeDescriptor = getPropertyType(item, request, propertyName);

				if (isPropertySupported(attributeDescriptor, propertyName))
				{
					processItemInternal(item, propertyName, propertyValue, request);
				}
			}
		}
	}

	private Optional<TypeAttributeDescriptor> getPropertyType(final ItemModel item, final AbstractRequest request, final String propertyName)
	{
		final String itemTypeCode = item.getItemtype();
		final String integrationObjectCode = request.getIntegrationObjectCode();
		try
		{
			final IntegrationObjectItemModel integrationObjectItem = getIntegrationObjectService().findIntegrationObjectItemByTypeCode(integrationObjectCode, itemTypeCode);
			return findTypeDescriptorAttributeForItem(integrationObjectItem, propertyName);
		}
		catch (final ModelNotFoundException e)
		{
			return Optional.empty();
		}
	}

	protected Optional<TypeAttributeDescriptor> findTypeDescriptorAttributeForItem(final IntegrationObjectItemModel integrationObjectItem, final String integrationItemAttributeName)
	{
		final TypeDescriptor itemTypeDescriptor = ItemTypeDescriptor.create(integrationObjectItem);
		return itemTypeDescriptor.getAttribute(integrationItemAttributeName);
	}

	@Override
	public void processEntity(final ODataEntry oDataEntry, final ItemConversionRequest conversionRequest) throws EdmException
	{
		final List<String> propertyNames = ListUtils.union(conversionRequest.getEntityType().getPropertyNames(),
				conversionRequest.getEntityType().getNavigationPropertyNames());

		for (final String propertyName : propertyNames)
		{
			if (isPropertySupported(propertyName))
			{
				final Optional<TypeAttributeDescriptor> attributeDescriptor = getPropertyType(conversionRequest.getItemModel(), conversionRequest, propertyName);
				if (isPropertySupported(attributeDescriptor, propertyName) && shouldPropertyBeConverted(conversionRequest, propertyName))
				{
					final Object propertyValue = readPropertyValue(conversionRequest, propertyName);
					processEntityInternal(oDataEntry, propertyName, propertyValue, conversionRequest);
				}
			}
		}
	}

	protected Object readPropertyValue(final ItemConversionRequest request, final String propertyName) throws EdmException
	{
		final String integrationObjectItemCode = request.getEntityType().getName();
		final String itemPropertyName = getIntegrationObjectService()
				.findItemAttributeName(request.getIntegrationObjectCode(), integrationObjectItemCode, propertyName);

		final ItemModel itemModel = request.getItemModel();
		final AttributeDescriptorModel attributeDescriptor = getAttributeDescriptor(itemModel, itemPropertyName);
		return attributeDescriptor.getLocalized()
				? getModelService().getAttributeValue(itemModel, attributeDescriptor.getQualifier(), request.getAcceptLocale())
				: getModelService().getAttributeValue(itemModel, attributeDescriptor.getQualifier());
	}

	protected abstract boolean shouldPropertyBeConverted(final ItemConversionRequest conversionRequest, final String propertyName) throws EdmException;

	protected boolean isItemPropertySettable(final ItemModel item, final String propertyName, final StorageRequest request) throws EdmException
	{
		if (isPropertySupported(propertyName))
		{
			final AttributeDescriptorModel attributeDescriptor = getAttributeDescriptor(item, propertyName, request);
			return getModelService().isNew(item) || attributeDescriptor.getWritable();
		}
		return false;
	}

	protected AttributeDescriptorModel getAttributeDescriptor(final ItemModel item, final String propertyName, final StorageRequest request) throws EdmException
	{
		final String integrationObjectItemCode = request.getEntityType().getName();
		final String itemPropertyName = getIntegrationObjectService()
				.findItemAttributeName(request.getIntegrationObjectCode(), integrationObjectItemCode, propertyName);
		return getAttributeDescriptor(item, itemPropertyName);
	}

	private AttributeDescriptorModel getAttributeDescriptor(final ItemModel item, final String propertyName)
	{
		return getTypeService().getAttributeDescriptor(item.getItemtype(), propertyName);
	}

	private boolean isPropertySupported(final String propertyName)
	{
		return !INTEGRATION_KEY_PROPERTY_NAME.equals(propertyName) && !LOCALIZED_ATTRIBUTE_NAME.equals(propertyName);
	}

	protected boolean isPropertySupported(final Optional<TypeAttributeDescriptor> attributeDescriptor, final String propertyName)
	{
		return attributeDescriptor.filter(this::isApplicable).isPresent();
	}

	protected abstract boolean isApplicable(final TypeAttributeDescriptor typeAttributeDescriptor);

	protected abstract void processItemInternal(final ItemModel item, final String entryPropertyName, final Object value,
			final StorageRequest request) throws EdmException;

	protected abstract void processEntityInternal(final ODataEntry oDataEntry, final String propertyName, final Object value,
			final ItemConversionRequest request) throws EdmException;

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	protected IntegrationObjectService getIntegrationObjectService()
	{
		return integrationObjectService;
	}

	@Required
	public void setIntegrationObjectService(final IntegrationObjectService integrationObjectService)
	{
		this.integrationObjectService = integrationObjectService;
	}

	public TypeService getTypeService()
	{
		return typeService;
	}

	@Required
	public void setTypeService(final TypeService typeService)
	{
		this.typeService = typeService;
	}
}