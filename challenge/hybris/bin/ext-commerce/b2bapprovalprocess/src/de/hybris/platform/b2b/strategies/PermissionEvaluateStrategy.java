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
package de.hybris.platform.b2b.strategies;

import de.hybris.platform.b2b.model.B2BPermissionModel;


/**
 * @param <R>
 *           the generic type B2BPermissionResultModel
 * @param <P>
 *           the generic type AbstractOrderModel
 * @param <S>
 *           the generic type B2BCustomerModel
 */
public interface PermissionEvaluateStrategy<R, P, S> extends EvaluateStrategy<R, P, S>
{
	/**
	 * Gets the permission type.
	 * 
	 * @return the permission type
	 */

	public abstract Class<? extends B2BPermissionModel> getPermissionType();
}
