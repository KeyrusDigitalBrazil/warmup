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
package de.hybris.platform.commerceservices.organization.strategies.impl;

import de.hybris.platform.commerceservices.model.OrgUnitModel;
import de.hybris.platform.commerceservices.organization.daos.OrgUnitDao;
import de.hybris.platform.commerceservices.organization.strategies.OrgUnitActivationStrategy;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections.Closure;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of the {@link OrgUnitActivationStrategy} interface.
 */
public class DefaultOrgUnitActivationStrategy implements OrgUnitActivationStrategy<OrgUnitModel>
{
	private ModelService modelService;
	private OrgUnitDao orgUnitDao;

	@Override
	public void activateUnit(final OrgUnitModel unit)
	{
		toggleUnit(unit, true);
		getModelService().save(unit);
	}

	@Override
	public void deactivateUnit(final OrgUnitModel unit)
	{
		processBranch(getBranch(unit), branchUnit -> toggleUnit((OrgUnitModel) branchUnit, false));
	}

	protected void processBranch(final Set<OrgUnitModel> branchUnits, final Closure closure)
	{
		CollectionUtils.forAllDo(branchUnits, closure);
		getModelService().saveAll(branchUnits);
	}

	protected void toggleUnit(final OrgUnitModel unit, final boolean activate)
	{
		unit.setActive(Boolean.valueOf(activate));
	}

	protected Set<OrgUnitModel> getBranch(final OrgUnitModel node)
	{
		final Set<OrgUnitModel> units = new HashSet<>();

		units.add(node);

		final SearchPageData<OrgUnitModel> searchResult = getOrgUnitDao().findMembersOfType(node, OrgUnitModel.class,
				createPageableData());

		for (final OrgUnitModel childUnit : searchResult.getResults())
		{
			units.addAll(getBranch(childUnit));
		}

		return units;
	}

	protected PageableData createPageableData()
	{
		final PageableData pageableData = new PageableData();
		pageableData.setCurrentPage(0);
		pageableData.setPageSize(-1);
		return pageableData;
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

	protected OrgUnitDao getOrgUnitDao()
	{
		return orgUnitDao;
	}

	@Required
	public void setOrgUnitDao(final OrgUnitDao orgUnitDao)
	{
		this.orgUnitDao = orgUnitDao;
	}

}
