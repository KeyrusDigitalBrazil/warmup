/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.warehousingfacades.returns;

/**
 * API for return facade in warehousing
 */
public interface WarehousingReturnFacade
{
	/**
	 * API to accept the goods for a {@link de.hybris.platform.returns.model.ReturnRequestModel}
	 *
	 * @param code
	 * 			the RMA number for the return request to be confirmed
	 */
	void acceptGoods(String code);

	/**
	 * Checks if the accept goods is possible for the given {@link de.hybris.platform.returns.model.ReturnRequestModel}
	 *
	 * @param code
	 * 			the RMA number for the return request to be confirmed
	 * @return boolean to indicate if accept goods confirmation of the given return request is possible
	 */
	boolean isAcceptGoodsConfirmable(String code);
}
