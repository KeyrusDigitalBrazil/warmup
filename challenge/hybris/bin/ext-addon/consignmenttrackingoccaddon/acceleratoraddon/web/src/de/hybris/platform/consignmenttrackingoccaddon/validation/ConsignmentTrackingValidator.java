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

package de.hybris.platform.consignmenttrackingoccaddon.validation;

import de.hybris.platform.commercefacades.order.OrderFacade;
import de.hybris.platform.commercefacades.order.data.ConsignmentData;
import de.hybris.platform.consignmenttrackingfacades.ConsignmentTrackingFacade;
import de.hybris.platform.consignmenttrackingoccaddon.constants.ConsignmentErrorConstants;
import de.hybris.platform.consignmenttrackingoccaddon.exceptions.NotShippedException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.webservicescommons.errors.exceptions.NotFoundException;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;


/**
 * validate the consignment data
 */
@Component("consignmentTrackingValidator")
public class ConsignmentTrackingValidator
{
	@Resource
	private OrderFacade orderFacade;

	@Resource
	private ConsignmentTrackingFacade consignmentFacade;

	private static final String STATUS_DISPLAY = "shipped";


	public void checkIfOrderAccessible(final String orderCode)
	{

		try
		{
			orderFacade.getOrderDetailsForCode(orderCode);
		}
		catch (final UnknownIdentifierException e)
		{

			throw new NotFoundException(ConsignmentErrorConstants.ORDER_NOT_FOUND_MESSAGE, ConsignmentErrorConstants.ORDER_NOT_FOUND,
					orderCode);
		}

	}

	public ConsignmentData checkIfConsignmentDataExist(final String orderCode, final String consignmentCode)
	{
		final List<ConsignmentData> consignments = consignmentFacade.getConsignmentsByOrder(orderCode);
		if (CollectionUtils.isEmpty(consignments))
		{

			throw new NotFoundException(ConsignmentErrorConstants.CONSIGNMENT_NOT_FOUND_MESSAGE,
					ConsignmentErrorConstants.CONSIGNMENT_NOT_FOUND, orderCode);
		}

		return consignments.stream().filter(data -> data.getCode().equals(consignmentCode)).findFirst()
				.orElseThrow(() -> new NotFoundException(ConsignmentErrorConstants.CONSIGNMENT_INCORRECT_MESSAGE,
						ConsignmentErrorConstants.CONSIGNMENT_INCORRECT, consignmentCode));


	}


	public void checkIfConsignmentShipped(final ConsignmentData data)
	{
		if(!data.getStatusDisplay().equals(STATUS_DISPLAY)){
			throw new NotShippedException(NotShippedException.NOT_SHIPPED_MESSAGE, NotShippedException.NOT_SHIPPED,
					data.getCode());
		}
	}


}


