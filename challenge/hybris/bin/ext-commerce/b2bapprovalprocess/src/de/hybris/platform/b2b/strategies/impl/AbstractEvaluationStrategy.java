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
package de.hybris.platform.b2b.strategies.impl;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.Set;

import org.springframework.beans.factory.annotation.Required;


/**
 * All b2b evaluation strategies should extends from this class.
 */
public abstract class AbstractEvaluationStrategy<M extends ItemModel>
{
	private TypeService typeService;
	private ModelService modelService;

	protected TypeService getTypeService()
	{
		return typeService;
	}

	public abstract Set<M> getTypesToEvaluate(final B2BCustomerModel customer, final AbstractOrderModel order);

	@Required
	public void setTypeService(final TypeService typeService)
	{
		this.typeService = typeService;
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
}
