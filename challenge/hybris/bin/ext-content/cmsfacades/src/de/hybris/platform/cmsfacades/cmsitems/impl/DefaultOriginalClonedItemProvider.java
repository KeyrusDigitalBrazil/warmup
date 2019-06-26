/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.cmsfacades.cmsitems.impl;

import static java.util.stream.Collectors.toList;
import static jersey.repackaged.com.google.common.collect.Lists.newArrayList;
import static jersey.repackaged.com.google.common.collect.Sets.newHashSet;

import de.hybris.platform.cmsfacades.cmsitems.OriginalClonedItemProvider;
import de.hybris.platform.cmsfacades.common.populator.LocalizedPopulator;
import de.hybris.platform.cmsfacades.constants.CmsfacadesConstants;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation for {@link OriginalClonedItemProvider}.
 * Stores the {@link ItemModel} instances in a stack on the current Session.
 */
public class DefaultOriginalClonedItemProvider<T extends ItemModel> implements OriginalClonedItemProvider<T>
{
	private SessionService sessionService;
	private ModelService modelService;

	private TypeService typeService;
	private LocalizedPopulator localizedPopulator;

	private static final Set<String> typeBlacklist = newHashSet("GenericItem", "ExtensibleItem", "LocalizableItem", "BridgeAbstraction", "Item");

	@Override
	public void initializeItem(final ItemModel originalItemModel)
	{
		Object value = getSessionService().getAttribute(CmsfacadesConstants.SESSION_ORIGINAL_ITEM_MODEL);
		if (value == null)
		{
			final Deque<ItemModel> stack = new LinkedList<>();
			value = new AtomicReference<>(stack);
			getSessionService().setAttribute(CmsfacadesConstants.SESSION_ORIGINAL_ITEM_MODEL, value);
		}
		final ItemModel clonedItemModel = getModelService().clone(originalItemModel);
		detachAll(clonedItemModel);
		getWrappedStack(value).push(clonedItemModel);
	}

	/**
	 * This method detaches parent itemModel from modelService as well as all child models (available through attributes).
	 * @param itemModel {@link ItemModel} that should be detached.
	 */
	protected void detachAll(final ItemModel itemModel) {
		final Predicate<ComposedTypeModel> isValidType = anotherComposedType -> !typeBlacklist.contains(anotherComposedType
				.getCode());

		final ComposedTypeModel composedType = getTypeService().getComposedTypeForCode(itemModel.getItemtype());
		final List<AttributeDescriptorModel> attributeDescriptorModels = newArrayList(composedType
				.getDeclaredattributedescriptors());

		attributeDescriptorModels.addAll(composedType
				.getAllSuperTypes()
				.stream()
				.filter(isValidType)
				.map(ComposedTypeModel::getDeclaredattributedescriptors)
				.flatMap(Collection::stream)
				.filter(AttributeDescriptorModel::getPartOf) //
				.collect(toList()));

		final List<Object> objects = attributeDescriptorModels.stream() //
				.filter(AttributeDescriptorModel::getPartOf) //
				.filter(AttributeDescriptorModel::getReadable) //
				.map(attributeDescriptorModel -> collectAttributeValues(itemModel, attributeDescriptorModel)) //
				.flatMap(Collection::stream).collect(toList());

		getModelService().detach(itemModel);

		objects.stream().filter(Objects::nonNull).forEach(value ->
		{
			if (value instanceof ItemModel)
			{
				getModelService().detach(value);
				detachAll((ItemModel) value);
			}
		});
	}

	/**
	 * This method collects the value(s) of the attribute identified by the {@link AttributeDescriptorModel} in the provided {@link ItemModel}.
	 * @param itemModel the {@link ItemModel} that contains the values to collect.
	 * @param attributeDescriptorModel the descriptor that identifies the attribute whose values to collect.
	 * @return The collection containing the value(s) of the attribute found in the provided {@link ItemModel}.
	 */
	protected Collection<Object> collectAttributeValues(final ItemModel itemModel,
			final AttributeDescriptorModel attributeDescriptorModel)
	{
		final List<Object> values = new ArrayList<>();
		if (attributeDescriptorModel.getLocalized())
		{
			final Map<String, Object> mapValue = localizedPopulator.populateAsMapOfLanguages(
					locale -> modelService.getAttributeValue(itemModel, attributeDescriptorModel.getQualifier(), locale));
			mapValue.values().stream() //
					.filter(Objects::nonNull) //
					.forEach(o -> {
						if (o instanceof Collection)
						{
							values.addAll((Collection) o);
						}
						else
						{
							values.add(o);
						}
					});
		}
		else
		{
			final Object o = modelService.getAttributeValue(itemModel, attributeDescriptorModel.getQualifier());
			if (o instanceof Collection)
			{
				values.addAll((Collection) o);
			}
			else
			{
				values.add(o);
			}
		}
		return values;
	}

	@Override
	public T getCurrentItem()
	{
		final Object value = getSessionService().getAttribute(CmsfacadesConstants.SESSION_ORIGINAL_ITEM_MODEL);
		if (value == null)
		{
			throw new IllegalStateException("There is no current item model. Please Initialize with #initializeItem before using this method.");
		}
		else
		{
			return (T) getWrappedStack(value).peek();
		}
	}

	@Override
	public void finalizeItem()
	{
		final Object value = getSessionService().getAttribute(CmsfacadesConstants.SESSION_ORIGINAL_ITEM_MODEL);
		if (value == null)
		{
			throw new IllegalStateException("There is no current item model. Please Initialize with #initializeItem before using this method.");
		}
		else
		{
			getWrappedStack(value).pop();
		}
	}

	/**
	 * Values stored in the session service must be wrapped in AtomicReference objects to protect them from being altered during serialization. When values are
	 * read from the session service, they must be unwrapped. Thus, this method is used to retrieve the original value (stack) stored in the AtomicReference wrapper.
	 *
	 * @param rawValue Object retrieved from the session service. The object must be an AtomicReference. Otherwise, an IllegalStateException is thrown.
	 * @return stack stored within the AtomicReference.
	 */
	protected Deque<ItemModel> getWrappedStack(final Object rawValue)
	{
		if(rawValue instanceof AtomicReference)
		{
			final AtomicReference<Deque<ItemModel>> originalValue = (AtomicReference<Deque<ItemModel>>) rawValue;
			return originalValue.get();
		}
		throw new IllegalStateException("Session object for SESSION_ORIGINAL_ITEM_MODEL should hold a reference of AtomicReference object.");
	}

	protected SessionService getSessionService()
	{
		return sessionService;
	}

	@Required
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	protected TypeService getTypeService()
	{
		return typeService;
	}

	@Required
	public void setTypeService(final TypeService typeService)
	{
		this.typeService = typeService;
	}
	protected LocalizedPopulator getLocalizedPopulator()
	{
		return localizedPopulator;
	}

	@Required
	public void setLocalizedPopulator(final LocalizedPopulator localizedPopulator)
	{
		this.localizedPopulator = localizedPopulator;
	}
}
