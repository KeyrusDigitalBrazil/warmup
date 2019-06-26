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
package de.hybris.platform.warehousingfacades.returns.impl;


import de.hybris.platform.basecommerce.enums.ReturnStatus;
import de.hybris.platform.ordermanagementfacades.OmsBaseFacade;
import de.hybris.platform.returns.OrderReturnException;
import de.hybris.platform.returns.ReturnActionResponse;
import de.hybris.platform.returns.ReturnCallbackService;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;
import de.hybris.platform.util.localization.Localization;
import de.hybris.platform.warehousingfacades.returns.WarehousingReturnFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateIfSingleResult;


/**
 * Default implementation of {@link WarehousingReturnFacade}.
 */
public class DefaultWarehousingReturnFacade extends OmsBaseFacade implements WarehousingReturnFacade
{
	protected static final Logger LOGGER = LoggerFactory.getLogger(DefaultWarehousingReturnFacade.class);
	private GenericDao<ReturnRequestModel> returnGenericDao;
	private ReturnCallbackService returnCallbackService;

	@Override
	public void acceptGoods(final String code)
	{
		Assert.notNull(code, Localization.getLocalizedString("warehousingfacade.returns.validation.null.code"));
		if (!validateReturnRequestByCode(code))
		{
			throw new IllegalStateException(
					String.format(Localization.getLocalizedString("warehousingfacade.returns.error.wrongstatus"), ReturnStatus.WAIT));
		}
		final ReturnRequestModel returnRequestModel = getReturnRequestModelForCode(code);
		try
		{
			getReturnCallbackService().onReturnReceptionResponse(new ReturnActionResponse(returnRequestModel));
		}
		catch (final OrderReturnException e)  //NOSONAR
		{
			LOGGER.error(
					String.format("Error happened during accept goods for the return request [%s]",
							returnRequestModel.getRMA()));  //NOSONAR
		}
	}

	@Override
	public boolean isAcceptGoodsConfirmable(final String code)
	{
		final ReturnRequestModel returnRequestModel = getReturnRequestModelForCode(code);
		if (returnRequestModel.getStatus().equals(ReturnStatus.WAIT))
		{
			return true;
		}
		else
		{
			LOGGER.error("ReturnRequest can not be approved, as its status is not WAIT. Status: {}", returnRequestModel.getStatus());
			return false;
		}
	}

	/**
	 * Finds {@link ReturnRequestModel} for the given {@link ReturnRequestModel#CODE}
	 *
	 * @param code
	 * 			the return request's code
	 * @return the requested return request for the given code
	 */
	protected ReturnRequestModel getReturnRequestModelForCode(final String code)
	{
		final Map<String, Object> params = new HashMap<>();
		params.put(ReturnRequestModel.CODE, code);
		final List<ReturnRequestModel> returnRequest = getReturnGenericDao().find(params);
		validateIfSingleResult(returnRequest,
				String.format(Localization.getLocalizedString("warehousingfacade.returns.validation.missing.code"), code),
				String.format(Localization.getLocalizedString("warehousingfacade.returns.validation.multiple.code"), code));

		return returnRequest.get(0);
	}

	protected boolean validateReturnRequestByCode(final String code)
	{
		Assert.notNull(code, Localization.getLocalizedString("warehousingfacade.returns.validation.null.code"));
		return isAcceptGoodsConfirmable(code);
	}

	@Required
	public void setReturnCallbackService(final ReturnCallbackService returnCallbackService)
	{
		this.returnCallbackService = returnCallbackService;
	}

	public ReturnCallbackService getReturnCallbackService()
	{
		return returnCallbackService;
	}

	protected GenericDao<ReturnRequestModel> getReturnGenericDao()
	{
		return returnGenericDao;
	}

	@Required
	public void setReturnGenericDao(final GenericDao<ReturnRequestModel> returnGenericDao)
	{
		this.returnGenericDao = returnGenericDao;
	}
}
