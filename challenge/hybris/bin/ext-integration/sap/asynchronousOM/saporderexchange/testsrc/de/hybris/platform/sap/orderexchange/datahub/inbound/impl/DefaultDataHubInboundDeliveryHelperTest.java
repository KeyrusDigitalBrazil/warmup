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
package de.hybris.platform.sap.orderexchange.datahub.inbound.impl;


import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.sap.orderexchange.constants.DataHubInboundConstants;
import de.hybris.platform.sap.orderexchange.datahub.inbound.impl.DefaultDataHubInboundDeliveryHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.Assert;
import org.mockito.Mock;
import org.mockito.Mockito;



@SuppressWarnings("javadoc")
@UnitTest
public class DefaultDataHubInboundDeliveryHelperTest
{

	private DefaultDataHubInboundDeliveryHelper classUnderTest;

	private ConsignmentModel mockedConsignmentModel;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private OrderModel mockedOrderModel;

	private ConsignmentEntryModel firstConsignmentEntryModel;
	private ConsignmentEntryModel secondConsignmentEntryModel;

	private OrderEntryModel firstMockedOrderEntryModel;
	private OrderEntryModel secondMockedOrderEntryModel;

	private Set<ConsignmentEntryModel> mockedSet;

	private final String testIssueDate = new String("20170101");

	private Date testDate;

	private Long firstExpectedShippedQuantity;
	private Long secondExpectedShippedQuantity;

	@Before
	public void setUp()
	{
		classUnderTest = new DefaultDataHubInboundDeliveryHelper();
		final SimpleDateFormat dateFormatter = new SimpleDateFormat(DataHubInboundConstants.IDOC_DATE_FORMAT);
		try
		{
			testDate = dateFormatter.parse(testIssueDate);
		}
		catch (final ParseException e)
		{
			throw new IllegalArgumentException("Date " + testIssueDate + " can not be converted to a date", e);
		}

		firstExpectedShippedQuantity = new Long(3);
		secondExpectedShippedQuantity = new Long(42132145);
		mockedConsignmentModel = new ConsignmentModel();
		mockedOrderModel = org.mockito.Mockito.mock(OrderModel.class);

		firstConsignmentEntryModel = Mockito.spy(new ConsignmentEntryModel());
		secondConsignmentEntryModel = Mockito.spy(new ConsignmentEntryModel());

		mockedSet = new HashSet<ConsignmentEntryModel>();
		mockedSet.add(firstConsignmentEntryModel);
		mockedSet.add(secondConsignmentEntryModel);

		firstMockedOrderEntryModel = Mockito.spy(new OrderEntryModel());
		secondMockedOrderEntryModel = Mockito.spy(new OrderEntryModel());

		firstMockedOrderEntryModel.setQuantity(firstExpectedShippedQuantity);
		secondMockedOrderEntryModel.setQuantity(secondExpectedShippedQuantity);

		firstConsignmentEntryModel.setOrderEntry(firstMockedOrderEntryModel);
		secondConsignmentEntryModel.setOrderEntry(secondMockedOrderEntryModel);

		mockedConsignmentModel.setConsignmentEntries(mockedSet);

	}

	@Test
	public void testMapDeliveryToConsignmentMultipleConsignmentEntriesSuccess()
	{
		classUnderTest.mapDeliveryToConsignment(testIssueDate, mockedConsignmentModel, mockedOrderModel);

		org.mockito.Mockito.verify(firstConsignmentEntryModel).setShippedQuantity(firstExpectedShippedQuantity);
		Assert.assertEquals(firstExpectedShippedQuantity, firstConsignmentEntryModel.getShippedQuantity());
		org.mockito.Mockito.verify(secondConsignmentEntryModel).setShippedQuantity(secondExpectedShippedQuantity);
		Assert.assertEquals(Long.valueOf(secondExpectedShippedQuantity), secondConsignmentEntryModel.getShippedQuantity());
		Assert.assertNotEquals(firstExpectedShippedQuantity, secondConsignmentEntryModel.getShippedQuantity());
		Assert.assertNotEquals(secondExpectedShippedQuantity, firstConsignmentEntryModel.getShippedQuantity());

	}

	@Test
	public void testMapDeliveryToConsignmentNullOrderModelSuccess()
	{
		classUnderTest.mapDeliveryToConsignment(testIssueDate, mockedConsignmentModel, null);

		org.mockito.Mockito.verify(firstConsignmentEntryModel).setShippedQuantity(firstExpectedShippedQuantity);
		Assert.assertEquals(firstExpectedShippedQuantity, firstConsignmentEntryModel.getShippedQuantity());
		org.mockito.Mockito.verify(secondConsignmentEntryModel).setShippedQuantity(secondExpectedShippedQuantity);
		Assert.assertEquals(Long.valueOf(secondExpectedShippedQuantity), secondConsignmentEntryModel.getShippedQuantity());
		Assert.assertNotEquals(firstExpectedShippedQuantity, secondConsignmentEntryModel.getShippedQuantity());
		Assert.assertNotEquals(secondExpectedShippedQuantity, firstConsignmentEntryModel.getShippedQuantity());
	}

	@Test
	public void testMapDeliveryToConsignmentEmptyConsignmentThrowNullPointerException()
	{
		thrown.expect(NullPointerException.class);
		mockedConsignmentModel.setConsignmentEntries(null);
		classUnderTest.mapDeliveryToConsignment(testIssueDate, mockedConsignmentModel, mockedOrderModel);

	}

	@Test
	public void testMapDeliveryToConsignmentNullOrderModelAccepted()
	{
		classUnderTest.mapDeliveryToConsignment(testIssueDate, mockedConsignmentModel, null);
		org.mockito.Mockito.verify(firstConsignmentEntryModel).setShippedQuantity(firstExpectedShippedQuantity);
		Assert.assertEquals(firstExpectedShippedQuantity, firstConsignmentEntryModel.getShippedQuantity());
		org.mockito.Mockito.verify(secondConsignmentEntryModel).setShippedQuantity(secondExpectedShippedQuantity);
		Assert.assertEquals(Long.valueOf(secondExpectedShippedQuantity), secondConsignmentEntryModel.getShippedQuantity());
		Assert.assertNotEquals(firstExpectedShippedQuantity, secondConsignmentEntryModel.getShippedQuantity());
		Assert.assertNotEquals(secondExpectedShippedQuantity, firstConsignmentEntryModel.getShippedQuantity());

	}

	@Test
	public void testMapDeliveryToConsignmentInvalidDateThrowsException()
	{
		final String invalidDate = new String("111");
		thrown.expect(IllegalArgumentException.class);
		classUnderTest.mapDeliveryToConsignment(invalidDate, mockedConsignmentModel, mockedOrderModel);
	}

	@Test
	public void testMapDeliveryToConsignmentProperDateSuccess()
	{
		classUnderTest.mapDeliveryToConsignment(testIssueDate, mockedConsignmentModel, mockedOrderModel);
		Assert.assertEquals(testDate.toString(), mockedConsignmentModel.getShippingDate().toString());
	}

}
