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
package de.hybris.platform.b2b.dao;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.workflow.model.WorkflowModel;



/**
 * The Interface B2BWorkflowDao.
 * 
 * @spring.bean b2bWorkflowDao
 */
public interface B2BWorkflowDao
{

	/**
	 * @deprecated As of hybris 4.4, replaced by {@link #findWorkflowByOrder(OrderModel)}
	 */
	@Deprecated
	public abstract WorkflowModel findWorkflowForOrder(final OrderModel order);

	/**
	 * Find the workflow of an order.
	 * 
	 * @param order
	 *           the order
	 * @return the WorkflowModel
	 */
	public abstract WorkflowModel findWorkflowByOrder(final OrderModel order);

}
