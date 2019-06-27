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
package de.hybris.platform.consignmenttrackingfacades.populators;

import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.commercefacades.order.data.ConsignmentData;
import de.hybris.platform.consignmenttrackingfacades.delivery.data.CarrierData;
import de.hybris.platform.consignmenttrackingservices.delivery.data.ConsignmentEventData;
import de.hybris.platform.consignmenttrackingservices.model.CarrierModel;
import de.hybris.platform.consignmenttrackingservices.service.ConsignmentTrackingService;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Required;


/**
 * A implementation of consignment populator for tracking
 */
public class ConsignmentForTrackingPopulator implements Populator<ConsignmentModel, ConsignmentData>
{

	private Converter<CarrierModel, CarrierData> carrierConverter;

	private ConsignmentTrackingService consignmentTrackingService;


	@Override
	public void populate(final ConsignmentModel source, final ConsignmentData target)
	{
		final CarrierModel carrierModel = source.getCarrierDetails();
		if (carrierModel != null)
		{
			target.setCarrierDetails(getCarrierConverter().convert(carrierModel));
		}

		final List<ConsignmentEventData> events = getConsignmentTrackingService().getConsignmentEvents(source);
		if (StringUtils.isNotBlank(source.getTrackingID()) && source.getCarrierDetails() != null
				&& CollectionUtils.isNotEmpty(events))
		{
			target.setTrackingEvents(sortEvents(events));
		}
		else
		{
			target.setTrackingEvents(Collections.emptyList());
		}

		target.setTargetArrivalDate(getTargetArrivalDate(source, events));
		target.setStatusDate(source.getShippingDate());
		target.setCreateDate(source.getOrder().getDate());
		target.setTargetShipDate(source.getShippingDate());
	}

	/**
	 * Sort the events according to date
	 *
	 * @param events
	 *           The event object to be sorted.
	 * @return The sorted result
	 */
	protected List<ConsignmentEventData> sortEvents(List<ConsignmentEventData> events)
	{
		return events.stream().sorted(Comparator.comparing(ConsignmentEventData::getEventDate).reversed())
				.collect(Collectors.toList());
	}

	/**
	 * get target arrival date
	 *
	 * @param consignment
	 *           the specific consignment
	 * @return target arrival date or now if NamedDeliveryDate is null otherwise
	 */
	protected Date getTargetArrivalDate(final ConsignmentModel consignment, final List<ConsignmentEventData> events)
	{
		// set as end date if delivery's already finish
		final ConsignmentStatus[] endStatus =
		{ ConsignmentStatus.CANCELLED, ConsignmentStatus.DELIVERY_COMPLETED, ConsignmentStatus.DELIVERY_REJECTED };
		if(ArrayUtils.contains(endStatus, consignment.getStatus())&&CollectionUtils.isNotEmpty(events))
		{
			return events.get(events.size() - 1).getEventDate();
		}

		// otherwise get planning arrival date
		final long namedDeliveryTime = consignment.getNamedDeliveryDate() == null ? new Date().getTime() : consignment
				.getNamedDeliveryDate().getTime();
		final int deliveryLeadTime = getConsignmentTrackingService().getDeliveryLeadTime(consignment);
		final DateTime targetArrivalDate = new DateTime(namedDeliveryTime).plusDays(deliveryLeadTime);
		return targetArrivalDate.toDate();
	}


	protected Converter<CarrierModel, CarrierData> getCarrierConverter()
	{
		return carrierConverter;
	}

	@Required
	public void setCarrierConverter(final Converter<CarrierModel, CarrierData> carrierConverter)
	{
		this.carrierConverter = carrierConverter;
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

}
