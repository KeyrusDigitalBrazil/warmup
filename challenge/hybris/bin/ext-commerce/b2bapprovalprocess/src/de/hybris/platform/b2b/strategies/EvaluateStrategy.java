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


/**
 * @param <R>
 *           the generic type B2B*ResultModel
 * @param <P>
 *           the generic type AbstractOrderModel
 * @param <S>
 *           the generic type B2BCustomerModel
 */
public interface EvaluateStrategy<R, P, S>
{

	/**
	 * Evaluate.
	 * 
	 * @param order
	 *           the AbstractOrderModel
	 * @param customer
	 *           the B2BCustomerModel
	 * @return the B2BPermissionResultModel
	 */
	public abstract R evaluate(P order, S customer);

}
