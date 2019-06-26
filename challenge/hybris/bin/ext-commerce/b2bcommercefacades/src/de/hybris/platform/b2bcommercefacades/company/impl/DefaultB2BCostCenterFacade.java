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

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.b2b.company.B2BCommerceCostCenterService;
import de.hybris.platform.b2b.model.B2BBudgetModel;
import de.hybris.platform.b2b.model.B2BCostCenterModel;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.services.B2BBudgetService;
import de.hybris.platform.b2b.services.B2BCostCenterService;
import de.hybris.platform.b2bcommercefacades.company.B2BCostCenterFacade;
import de.hybris.platform.b2bcommercefacades.company.data.B2BCostCenterData;
import de.hybris.platform.b2bcommercefacades.company.data.B2BSelectionData;
import de.hybris.platform.b2bcommercefacades.company.util.B2BCompanyUtils;
import de.hybris.platform.commercefacades.search.data.AutocompleteSuggestionData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link B2BCostCenterFacade}
 */
public class DefaultB2BCostCenterFacade implements B2BCostCenterFacade
{
	private B2BCommerceCostCenterService b2bCommerceCostCenterService;
	private B2BCostCenterService<B2BCostCenterModel, B2BCustomerModel> b2bCostCenterService;
	private Converter<B2BCostCenterModel, B2BCostCenterData> b2bCostCenterConverter;
	private Converter<B2BCostCenterData, B2BCostCenterModel> b2bCostCenterReverseConverter;
	private B2BBudgetService b2bBudgetService;
	private ModelService modelService;

	@Override
	public List<? extends B2BCostCenterData> getCostCenters()
	{
		return Converters.convertAll(getB2bCostCenterService().getAllCostCenters(), getB2bCostCenterConverter());
	}

	@Override
	public List<? extends B2BCostCenterData> getActiveCostCenters()
	{
		final Collection costCenters = CollectionUtils.select(getB2bCostCenterService().getAllCostCenters(), new Predicate()
		{
			@Override
			public boolean evaluate(final Object object)
			{
				return ((B2BCostCenterModel) object).getActive().booleanValue();
			}
		});
		return Converters.convertAll(costCenters, getB2bCostCenterConverter());
	}

	@Override
	public B2BCostCenterData getCostCenterDataForCode(final String costCenterCode)
	{
		validateParameterNotNullStandardMessage("costCenterCode", costCenterCode);
		final B2BCostCenterModel b2bCostCenterModel = getB2bCommerceCostCenterService().getCostCenterForCode(costCenterCode);
		return getB2bCostCenterConverter().convert(b2bCostCenterModel);
	}

	@Override
	public SearchPageData<B2BCostCenterData> search(final SearchStateData searchState, final PageableData pageableData)
	{
		final SearchPageData<B2BCostCenterModel> costCenters = getB2bCommerceCostCenterService().getPagedCostCenters(pageableData);
		final SearchPageData<B2BCostCenterData> costCentersPageData = convertPageData(costCenters, getB2bCostCenterConverter());

		return costCentersPageData;
	}

	protected <S, T> SearchPageData<T> convertPageData(final SearchPageData<S> source, final Converter<S, T> converter)
	{
		final SearchPageData<T> result = new SearchPageData<T>();
		result.setPagination(source.getPagination());
		result.setSorts(source.getSorts());
		result.setResults(Converters.convertAll(source.getResults(), converter));
		return result;
	}

	@Override
	public List<AutocompleteSuggestionData> autocomplete(final SearchStateData searchState)
	{
		// YTODO Auto-generated method stub
		return null; // NOSONAR
	}

	@Override
	public void updateCostCenter(final B2BCostCenterData b2BCostCenterData)
	{
		validateParameterNotNullStandardMessage("b2BCostCenterData", b2BCostCenterData);

		final B2BCostCenterModel b2BCostCenterModel = getB2bCommerceCostCenterService()
				.getCostCenterForCode(b2BCostCenterData.getOriginalCode());
		if (b2BCostCenterModel != null)
		{
			getB2bCostCenterReverseConverter().convert(b2BCostCenterData, b2BCostCenterModel);
			getModelService().save(b2BCostCenterModel);
		}
	}

	@Override
	public void addCostCenter(final B2BCostCenterData b2BCostCenterData)
	{
		final B2BCostCenterModel b2BCostCenterModel = modelService.create(B2BCostCenterModel.class);
		getB2bCostCenterReverseConverter().convert(b2BCostCenterData, b2BCostCenterModel);
		getModelService().save(b2BCostCenterModel);

	}

	@Override
	public void enableDisableCostCenter(final String costCenterCode, final boolean active)
	{
		final B2BCostCenterModel b2BCostCenterModel = getB2bCommerceCostCenterService().getCostCenterForCode(costCenterCode);
		if (b2BCostCenterModel != null && b2BCostCenterModel.getUnit() != null
				&& Boolean.TRUE.equals(b2BCostCenterModel.getUnit().getActive()))
		{
			b2BCostCenterModel.setActive(Boolean.valueOf(active));
			getModelService().save(b2BCostCenterModel);
		}
	}

	@Override
	public B2BSelectionData selectBudgetForCostCenter(final String costCenterCode, final String budgetCode)

	{
		final B2BCostCenterModel b2BCostCenterModel = getB2bCommerceCostCenterService().getCostCenterForCode(costCenterCode);
		final Set<B2BBudgetModel> budgetModelSet = new HashSet<B2BBudgetModel>(b2BCostCenterModel.getBudgets());
		final B2BBudgetModel b2BBudgetModel = getB2bBudgetService().getB2BBudgetForCode(budgetCode);
		budgetModelSet.add(b2BBudgetModel);
		b2BCostCenterModel.setBudgets(budgetModelSet);
		getModelService().save(b2BCostCenterModel);

		return B2BCompanyUtils.createB2BSelectionData(b2BBudgetModel.getCode(), true, b2BBudgetModel.getActive().booleanValue());

	}

	@Override
	public B2BSelectionData deSelectBudgetForCostCenter(final String costCenterCode, final String budgetCode)

	{
		final B2BCostCenterModel b2BCostCenterModel = getB2bCommerceCostCenterService().getCostCenterForCode(costCenterCode);
		final Set<B2BBudgetModel> budgetModelSet = new HashSet<B2BBudgetModel>(b2BCostCenterModel.getBudgets());
		final B2BBudgetModel b2BBudgetModel = getB2bBudgetService().getB2BBudgetForCode(budgetCode);
		if (b2BBudgetModel != null)
		{
			budgetModelSet.remove(b2BBudgetModel);
		}

		b2BCostCenterModel.setBudgets(budgetModelSet);
		getModelService().save(b2BCostCenterModel);

		if (b2BBudgetModel != null)
		{
			return B2BCompanyUtils.createB2BSelectionData(b2BBudgetModel.getCode(), false,
					b2BBudgetModel.getActive().booleanValue());
		}

		throw new IllegalStateException("The b2BBudgetModel must be found in the system");
	}

	protected B2BCommerceCostCenterService getB2bCommerceCostCenterService()
	{
		return b2bCommerceCostCenterService;
	}

	@Required
	public void setB2bCommerceCostCenterService(final B2BCommerceCostCenterService b2bCommerceCostCenterService)
	{
		this.b2bCommerceCostCenterService = b2bCommerceCostCenterService;
	}

	protected B2BCostCenterService<B2BCostCenterModel, B2BCustomerModel> getB2bCostCenterService()
	{
		return b2bCostCenterService;
	}

	@Required
	public void setB2bCostCenterService(final B2BCostCenterService<B2BCostCenterModel, B2BCustomerModel> b2bCostCenterService)
	{
		this.b2bCostCenterService = b2bCostCenterService;
	}

	protected B2BBudgetService getB2bBudgetService()
	{
		return b2bBudgetService;
	}

	@Required
	public void setB2bBudgetService(final B2BBudgetService b2bBudgetService)
	{
		this.b2bBudgetService = b2bBudgetService;
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

	protected Converter<B2BCostCenterModel, B2BCostCenterData> getB2bCostCenterConverter()
	{
		return b2bCostCenterConverter;
	}

	@Required
	public void setB2bCostCenterConverter(final Converter<B2BCostCenterModel, B2BCostCenterData> b2bCostCenterConverter)
	{
		this.b2bCostCenterConverter = b2bCostCenterConverter;
	}

	protected Converter<B2BCostCenterData, B2BCostCenterModel> getB2bCostCenterReverseConverter()
	{
		return b2bCostCenterReverseConverter;
	}

	@Required
	public void setB2bCostCenterReverseConverter(
			final Converter<B2BCostCenterData, B2BCostCenterModel> b2bCostCenterReverseConverter)
	{
		this.b2bCostCenterReverseConverter = b2bCostCenterReverseConverter;
	}
}