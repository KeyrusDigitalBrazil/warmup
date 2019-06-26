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

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.ConsignmentData;
import de.hybris.platform.consignmenttrackingfacades.delivery.data.CarrierData;
import de.hybris.platform.consignmenttrackingservices.delivery.data.ConsignmentEventData;
import de.hybris.platform.consignmenttrackingservices.model.CarrierModel;
import de.hybris.platform.consignmenttrackingservices.service.ConsignmentTrackingService;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;



@UnitTest
public class ConsignmentForTrackingPopulatorTest
{

	@Mock
	private Converter<CarrierModel, CarrierData> carrierConverter;

	@Mock
	private ConsignmentTrackingService consignmentTrackingService;

	private ConsignmentForTrackingPopulator poulator;

	private CarrierModel carrierModel;

	private CarrierData carrierData;

	private ConsignmentModel source;

	private ConsignmentData target;

	private AbstractOrderModel order;

	private Date date;

	private List<ConsignmentEventData> events;

	@Before
	public void prepare()
	{
		MockitoAnnotations.initMocks(this);

		date = new Date();

		events = Collections.emptyList();

		carrierModel = new CarrierModel();
		carrierData = new CarrierData();

		order = new AbstractOrderModel();
		order.setDate(date);

		source = new ConsignmentModel();
		source.setCarrierDetails(carrierModel);
		source.setOrder(order);
		source.setShippingDate(date);
		source.setNamedDeliveryDate(date);

		target = new ConsignmentData();

		poulator = new ConsignmentForTrackingPopulator();
		poulator.setCarrierConverter(carrierConverter);
		poulator.setConsignmentTrackingService(consignmentTrackingService);
	}


	@Test
	public void test_populate()
	{
		given(carrierConverter.convert(carrierModel)).willReturn(carrierData);
		given(consignmentTrackingService.getConsignmentEvents(source)).willReturn(events);
		given(consignmentTrackingService.getDeliveryLeadTime(source)).willReturn(3);

		poulator.populate(source, target);

		Assert.assertEquals(carrierData, target.getCarrierDetails());
		Assert.assertEquals(events, target.getTrackingEvents());
		Assert.assertEquals(date, target.getStatusDate());
		Assert.assertEquals(date, target.getTargetShipDate());
		Assert.assertEquals(date, target.getCreateDate());
		
		DateTime expectDate = new DateTime(date);
		expectDate = expectDate.plusDays(3);
		expectDate = expectDate.withZone(DateTimeZone.UTC);
		DateTime actualDate = new DateTime(target.getTargetArrivalDate());
		actualDate = actualDate.withZone(DateTimeZone.UTC);
		Assert.assertEquals(expectDate.getMillis(), actualDate.getMillis());
	}
}
