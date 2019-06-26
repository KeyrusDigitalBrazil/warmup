/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.sap.saprevenuecloudorder.service.impl;

import java.util.Map;

import org.springframework.http.ResponseEntity;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.outboundservices.facade.OutboundServiceFacade;
import de.hybris.platform.sap.saprevenuecloudorder.service.SapRevenueCloudOrderOutboundService;
import rx.Observable;

/**
 *  Service to send subscription order to Revenue Cloud via CPI.
 */

public class DefaultSapRevenueCloudOrderOutboundService implements SapRevenueCloudOrderOutboundService{

	private SapRevenueCloudOrderConversionService sapRevenueCloudOrderConversionService;
	private OutboundServiceFacade outboundServiceFacade;
	private static final String OUTBOUND_ORDER_OBJECT = "OutboundOMMOrderOMSOrder";
	private static final String OUTBOUND_ORDER_DESTINATION = "scpiRevenuecloudOrderDestination";
	
	@Override
	public Observable<ResponseEntity<Map>> sendOrder(OrderModel orderModel) {
		return getOutboundServiceFacade().send(getSapRevenueCloudOrderConversionService().convertOrderToSapCpiOrder(orderModel)
				, OUTBOUND_ORDER_OBJECT, OUTBOUND_ORDER_DESTINATION);	
	}

	/**
	 * @return the outboundServiceFacade
	 */
	public OutboundServiceFacade getOutboundServiceFacade() {
		return outboundServiceFacade;
	}

	/**
	 * @param outboundServiceFacade the outboundServiceFacade to set
	 */
	public void setOutboundServiceFacade(OutboundServiceFacade outboundServiceFacade) {
		this.outboundServiceFacade = outboundServiceFacade;
	}

	/**
	 * @return the sapRevenueCloudOrderConversionService
	 */
	public SapRevenueCloudOrderConversionService getSapRevenueCloudOrderConversionService() {
		return sapRevenueCloudOrderConversionService;
	}

	/**
	 * @param sapRevenueCloudOrderConversionService the sapRevenueCloudOrderConversionService to set
	 */
	public void setSapRevenueCloudOrderConversionService(
			SapRevenueCloudOrderConversionService sapRevenueCloudOrderConversionService) {
		this.sapRevenueCloudOrderConversionService = sapRevenueCloudOrderConversionService;
	}	
}
