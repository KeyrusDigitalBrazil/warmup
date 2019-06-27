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
package de.hybris.platform.cmsfacades.util.models;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Required;


 public abstract class AbstractModelMother<T extends ItemModel>
{
	private ModelService modelService;
	private FlexibleSearchService flexibleSearchService;


	protected T getFromCollectionOrSaveAndReturn(final Supplier<Collection<T>> getter, final Supplier<T> creator)
	{
		return getModelFromCollection(getter).orElseGet(() -> saveModel(creator));
	}

	protected T saveModel(final Supplier<T> creator)
	{
		T model = creator.get();
		getModelService().save(model);
		return model;
	}

	protected Optional<T> getModelFromCollection(final Supplier<Collection<T>> getter)
	{
		if (getter == null)
		{
			return Optional.empty();
		}
		return getter.get().stream().findFirst();
	}

	protected T getOrSaveAndReturn(final Supplier<T> getter, final Supplier<T> creator)
	{
		T model = null;
		try
		{
			model = getter.get();
			if (model == null)
			{
				throw new ModelNotFoundException("DAO returned null.");
			}
		}
		catch (final ModelNotFoundException | UnknownIdentifierException e)
		{
			model = creator.get();
			getModelService().save(model);
		}
		return model;
	}

	public ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	public FlexibleSearchService getFlexibleSearchService()
	{
		return flexibleSearchService;
	}

	@Required
	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
	{
		this.flexibleSearchService = flexibleSearchService;
	}
}