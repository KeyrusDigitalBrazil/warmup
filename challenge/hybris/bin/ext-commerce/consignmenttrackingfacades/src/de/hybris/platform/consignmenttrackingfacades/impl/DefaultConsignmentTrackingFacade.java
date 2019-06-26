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
package de.hybris.platform.consignmenttrackingfacades.impl;

import de.hybris.platform.commercefacades.order.data.ConsignmentData;
import de.hybris.platform.consignmenttrackingfacades.ConsignmentTrackingFacade;
import de.hybris.platform.consignmenttrackingservices.service.ConsignmentTrackingService;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * A default implementation of ConsignmentTrackingFacade
 */
public class DefaultConsignmentTrackingFacade implements ConsignmentTrackingFacade
{

	private ConsignmentTrackingService consignmentTrackingService;

	private Converter<ConsignmentModel, ConsignmentData> consignmentConverter;

	@Override
	public Optional<ConsignmentData> getConsignmentByCode(final String orderCode, final String consignmentCode)
	{
		final Optional<ConsignmentModel> consignment = getConsignmentTrackingService().getConsignmentForCode(orderCode,
			consignmentCode);
		return consignment.map(source -> getConsignmentConverter().convert(source));
	}

	@Override
	public List<ConsignmentData> getConsignmentsByOrder(final String orderCode)
	{
		List<ConsignmentData> dataList = new ArrayList<>(0);
		final List<ConsignmentModel> consignments = getConsignmentTrackingService().getConsignmentsForOrder(orderCode);
		if (CollectionUtils.isNotEmpty(consignments))
		{
			dataList = getConsignmentConverter().convertAll(consignments);
		}
		return dataList;
	}


	@Override
	public String getTrackingUrlForConsignmentCode(final String orderCode, final String consignmentCode)
	{
		final Optional<ConsignmentModel> optional = getConsignmentTrackingService().getConsignmentForCode(orderCode,
			consignmentCode);

		return optional.map(x -> getConsignmentTrackingService().getTrackingUrlForConsignment(x)).map(k -> k.toString()).orElse("");
	}

	protected ConsignmentTrackingService getConsignmentTrackingService()
	{
		return consignmentTrackingService;
	}

	@Required
	public void setConsignmentTrackingService(final ConsignmentTrackingService consignmentTrackingService)
	{
		this.consignmentTrackingService = consignmentTrackingService;
	}

	protected Converter<ConsignmentModel, ConsignmentData> getConsignmentConverter()
	{
		return consignmentConverter;
	}

	@Required
	public void setConsignmentConverter(final Converter<ConsignmentModel, ConsignmentData> consignmentConverter)
	{
		this.consignmentConverter = consignmentConverter;
	}

}
