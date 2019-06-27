/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.b2bcommercefacades.company.impl;

import de.hybris.platform.b2b.model.B2BBudgetModel;
import de.hybris.platform.b2b.model.B2BCostCenterModel;
import de.hybris.platform.b2b.services.B2BBudgetService;
import de.hybris.platform.b2bcommercefacades.company.B2BBudgetFacade;
import de.hybris.platform.b2bcommercefacades.company.B2BCostCenterFacade;
import de.hybris.platform.b2bcommercefacades.company.converters.populators.B2BBudgetReversePopulator;
import de.hybris.platform.b2bcommercefacades.company.data.B2BBudgetData;
import de.hybris.platform.b2bcommercefacades.company.data.B2BCostCenterData;
import de.hybris.platform.b2bcommercefacades.search.data.BudgetSearchStateData;
import de.hybris.platform.commercefacades.search.data.AutocompleteSuggestionData;
import de.hybris.platform.commercefacades.util.CommerceUtils;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.List;

import org.apache.commons.beanutils.BeanPropertyValueEqualsPredicate;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ListUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of the {@link B2BBudgetFacade} interface.
 */
public class DefaultB2BBudgetFacade implements B2BBudgetFacade
{
	private Converter<B2BBudgetModel, B2BBudgetData> b2BBudgetConverter;
	private Converter<B2BCostCenterModel, B2BCostCenterData> b2BCostCenterConverter;
	private B2BBudgetReversePopulator b2BBudgetReversePopulator;
	private B2BBudgetService b2BBudgetService;
	private ModelService modelService;
	private B2BCostCenterFacade costCenterFacade;

	@Override
	public B2BBudgetData getBudgetDataForCode(final String budgetCode)
	{
		B2BBudgetData b2BBudgetData = null;
		final B2BBudgetModel budgetModel = getB2BBudgetService().getB2BBudgetForCode(budgetCode);
		if (budgetModel != null)
		{
			b2BBudgetData = getB2BBudgetConverter().convert(budgetModel);
			if (CollectionUtils.isNotEmpty(budgetModel.getCostCenters()))
			{
				b2BBudgetData.setCostCenters(Converters.convertAll(budgetModel.getCostCenters(), getB2BCostCenterConverter()));
			}
		}
		return b2BBudgetData;
	}

	@Override
	public void updateBudget(final B2BBudgetData b2BBudgetData)
	{
		final B2BBudgetModel b2BBudgetModel = getB2BBudgetService().getB2BBudgetForCode(b2BBudgetData.getOriginalCode());
		if (b2BBudgetModel != null)
		{
			getB2BBudgetReversePopulator().populate(b2BBudgetData, b2BBudgetModel);
			getModelService().save(b2BBudgetModel);
		}
	}

	@Override
	public void addBudget(final B2BBudgetData b2BBudgetData)
	{
		final B2BBudgetModel b2BBudgetModel = getModelService().create(B2BBudgetModel.class);
		getB2BBudgetReversePopulator().populate(b2BBudgetData, b2BBudgetModel);
		getModelService().save(b2BBudgetModel);
	}

	@Override
	public void enableDisableBudget(final String b2BudgetCode, final boolean active)
	{
		final B2BBudgetModel b2BBudgetModel = getB2BBudgetService().getB2BBudgetForCode(b2BudgetCode);
		if (b2BBudgetModel != null)
		{
			b2BBudgetModel.setActive(Boolean.valueOf(active));
			getModelService().save(b2BBudgetModel);
		}
	}

	@Override
	public SearchPageData<B2BBudgetData> search(final BudgetSearchStateData searchState, final PageableData pageableData)
	{
		SearchPageData<B2BBudgetData> searchPageData = null;

		final SearchPageData<B2BBudgetModel> b2BBudgets = getB2BBudgetService().findPagedBudgets(pageableData);
		searchPageData = CommerceUtils.convertPageData(b2BBudgets, getB2BBudgetConverter());

		if (searchState != null && searchState.getCostCenterCode() != null)
		{
			final B2BCostCenterData costCenter = getCostCenterFacade().getCostCenterDataForCode(searchState.getCostCenterCode());
			for (final B2BBudgetData budgetData : searchPageData.getResults())
			{
				budgetData.setSelected(CollectionUtils.find(costCenter.getB2bBudgetData(),
						new BeanPropertyValueEqualsPredicate(B2BBudgetModel.CODE, budgetData.getCode())) != null);
			}
		}

		return searchPageData;
	}

	@Override
	public List<AutocompleteSuggestionData> autocomplete(final BudgetSearchStateData searchState)
	{
		return ListUtils.EMPTY_LIST;
	}

	protected Converter<B2BBudgetModel, B2BBudgetData> getB2BBudgetConverter()
	{
		return b2BBudgetConverter;
	}

	@Required
	public void setB2BBudgetConverter(final Converter<B2BBudgetModel, B2BBudgetData> b2BBudgetConverter)
	{
		this.b2BBudgetConverter = b2BBudgetConverter;
	}

	protected B2BBudgetService getB2BBudgetService()
	{
		return b2BBudgetService;
	}

	@Required
	public void setB2BBudgetService(final B2BBudgetService b2BBudgetService)
	{
		this.b2BBudgetService = b2BBudgetService;
	}

	protected Converter<B2BCostCenterModel, B2BCostCenterData> getB2BCostCenterConverter()
	{
		return b2BCostCenterConverter;
	}

	@Required
	public void setB2BCostCenterConverter(final Converter<B2BCostCenterModel, B2BCostCenterData> b2BCostCenterConverter)
	{
		this.b2BCostCenterConverter = b2BCostCenterConverter;
	}

	protected B2BBudgetReversePopulator getB2BBudgetReversePopulator()
	{
		return b2BBudgetReversePopulator;
	}

	@Required
	public void setB2BBudgetReversePopulator(final B2BBudgetReversePopulator b2BBudgetReversePopulator)
	{
		this.b2BBudgetReversePopulator = b2BBudgetReversePopulator;
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

	protected B2BCostCenterFacade getCostCenterFacade()
	{
		return costCenterFacade;
	}

	@Required
	public void setCostCenterFacade(final B2BCostCenterFacade costCenterFacade)
	{
		this.costCenterFacade = costCenterFacade;
	}
}
