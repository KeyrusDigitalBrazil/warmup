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
package de.hybris.platform.b2b.services.impl;

import de.hybris.platform.b2b.dao.impl.BaseDao;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BReportingSetModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BReportingService;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.HashSet;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of the {@link B2BReportingService}
 *
 * @spring.bean b2bReportingService
 */
public class DefaultB2BReportingService implements B2BReportingService
{

	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(DefaultB2BReportingService.class);

	private BaseDao baseDao;
	private ModelService modelService;
	private B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService;

	/**
	 * @deprecated Since 4.4. Use {@link #getReportingSetForCode(String)} instead
	 */
	@Deprecated
	@Override
	public B2BReportingSetModel findReportingSetByCode(final String code)
	{
		return getReportingSetForCode(code);
	}

	@Override
	public B2BReportingSetModel getReportingSetForCode(final String code)
	{
		return getBaseDao().findFirstByAttribute(B2BReportingSetModel.CODE, code, B2BReportingSetModel.class);

	}

	/**
	 * @deprecated Since 4.4. Use {@link #getReportingSetForB2BUnit(B2BUnitModel)} instead
	 */
	@Deprecated
	@Override
	public B2BReportingSetModel findReportingSetForB2BUnit(final B2BUnitModel unit)
	{
		return getReportingSetForB2BUnit(unit);
	}

	@Override
	public B2BReportingSetModel getReportingSetForB2BUnit(final B2BUnitModel unit)
	{
		return getReportingSetForCode(unit.getUid());
	}

	@Override
	public B2BReportingSetModel setReportSetForUnit(final B2BUnitModel unit)
	{
		B2BReportingSetModel reportingSetModel = getReportingSetForB2BUnit(unit);
		if (reportingSetModel == null)
		{
			reportingSetModel = getModelService().create(B2BReportingSetModel.class);
			reportingSetModel.setCode(unit.getUid());
		}
		reportingSetModel.setReportingEntries(new HashSet<ItemModel>(getB2bUnitService().getBranch(unit)));
		getModelService().save(reportingSetModel);
		return reportingSetModel;
	}

	@Override
	public void setReportingOrganizationForUnit(final B2BUnitModel unit)
	{
		unit.setReportingOrganization(getB2bUnitService().getRootUnit(unit));
	}

	@Override
	public void updateReportingSetForUnitAndParents(final B2BUnitModel unit)
	{
		final List<B2BUnitModel> allParents = getB2bUnitService().getAllParents(unit);
		for (final B2BUnitModel b2bUnitModel : allParents)
		{
			this.setReportSetForUnit(b2bUnitModel);
		}
	}

	@Required
	public void setB2bUnitService(final B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService)
	{
		this.b2bUnitService = b2bUnitService;
	}

	@Required
	public void setBaseDao(final BaseDao baseDao)
	{
		this.baseDao = baseDao;
	}

	protected BaseDao getBaseDao()
	{
		return baseDao;
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

	protected B2BUnitService<B2BUnitModel, B2BCustomerModel> getB2bUnitService()
	{
		return b2bUnitService;
	}

}
