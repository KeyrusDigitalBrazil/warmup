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
 *
 */
package de.hybris.platform.ordermanagementfacades.returns.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.basecommerce.enums.CancelReason;
import de.hybris.platform.basecommerce.enums.RefundReason;
import de.hybris.platform.basecommerce.enums.ReturnAction;
import de.hybris.platform.basecommerce.enums.ReturnStatus;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.ordermanagementfacades.BaseOrdermanagementFacadeIntegrationTest;
import de.hybris.platform.ordermanagementfacades.returns.OmsReturnFacade;
import de.hybris.platform.ordermanagementfacades.returns.data.CancelReturnRequestData;
import de.hybris.platform.ordermanagementfacades.returns.data.ReturnEntryData;
import de.hybris.platform.ordermanagementfacades.returns.data.ReturnRequestData;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import javax.annotation.Resource;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;


@IntegrationTest
public class DefaultOmsReturnFacadeIntegrationTest extends BaseOrdermanagementFacadeIntegrationTest
{
	@Resource
	private OmsReturnFacade omsReturnFacade;

	@Before
	public void setUp()
	{
		// implement here code executed before each test
	}

	@Test
	public void testGetReturnStatuses()
	{
		final List<ReturnStatus> returnStatuses = omsReturnFacade.getReturnStatuses();
		assertNotNull(returnStatuses);
	}

	@Test
	public void testGetRefundReasons()
	{
		final List<RefundReason> returnReasons = omsReturnFacade.getRefundReasons();
		assertNotNull(returnReasons);
	}

	@Test
	public void testGetReturnActions()
	{
		final List<ReturnAction> returnActions = omsReturnFacade.getReturnActions();
		assertNotNull(returnActions);
	}

	@Test
	public void testCreateReturnRequest_ValidQtyReturn_Success()
	{
		//Given
		final ReturnEntryData returnEntryData1 = createReturnEntryData(1L, "HOLD", null, "DamagedInTransit", 0);
		final ReturnEntryData returnEntryData2 = createReturnEntryData(1L, "HOLD", null, "DamagedInTransit", 1);
		final List<ReturnEntryData> returnEntryDatas = Arrays.asList(returnEntryData1, returnEntryData2);
		final ReturnRequestData returnRequestData = createReturnRequestData(returnEntryDatas, "O-K2010-C0000-001", Boolean.FALSE);

		//When
		final ReturnRequestData createdReturnRequest = omsReturnFacade.createReturnRequest(returnRequestData);

		//then
		Assert.assertNotNull(createdReturnRequest.getCode());
		Assert.assertEquals(2, createdReturnRequest.getReturnEntries().size());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateReturnRequest_InValidQtyReturn_Failure()
	{
		//Given
		final ReturnEntryData returnEntryData1 = createReturnEntryData(100L, "HOLD", null, "DamagedInTransit", 0);
		final List<ReturnEntryData> returnEntryDatas = Arrays.asList(returnEntryData1);
		final ReturnRequestData returnRequestData = createReturnRequestData(returnEntryDatas, "O-K2010-C0000-001", Boolean.FALSE);

		//When
		omsReturnFacade.createReturnRequest(returnRequestData);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateReturnRequest_InValidQtyReturn_Failure1()
	{
		//Given
		final ReturnEntryData returnEntryData1 = createReturnEntryData(100L, "HOLD", null, "DamagedInTransit", 0);
		final List<ReturnEntryData> returnEntryDatas = Arrays.asList(returnEntryData1);
		final ReturnRequestData returnRequestData = createReturnRequestData(returnEntryDatas, "O-K2010-C0000-001", Boolean.FALSE);

		//When
		omsReturnFacade.createReturnRequest(returnRequestData);
	}

	@Test(expected = UnknownIdentifierException.class)
	public void testCreateReturnRequest_InValidOrderEntryNumber_Failure()
	{
		//Given
		final ReturnEntryData returnEntryData1 = createReturnEntryData(1L, "HOLD", null, "DamagedInTransit", 10);
		final List<ReturnEntryData> returnEntryDatas = Arrays.asList(returnEntryData1);
		final ReturnRequestData returnRequestData = createReturnRequestData(returnEntryDatas, "O-K2010-C0000-001", Boolean.FALSE);

		//When
		omsReturnFacade.createReturnRequest(returnRequestData);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateReturnRequest_NullOrder_Failure()
	{
		//Given
		final ReturnEntryData returnEntryData1 = createReturnEntryData(1L, "HOLD", null, "DamagedInTransit", 0);
		final List<ReturnEntryData> returnEntryDatas = Arrays.asList(returnEntryData1);
		final ReturnRequestData returnRequestData = createReturnRequestData(returnEntryDatas, null, Boolean.FALSE);

		//When
		omsReturnFacade.createReturnRequest(returnRequestData);
	}

	/**
	 * Throws IllegalArgumentException, as the {@link de.hybris.platform.returns.impl.DefaultReturnService} does not
	 * set any {@link ReturnStatus} on the newly created {@link de.hybris.platform.returns.model.ReturnRequestModel}.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testCancelReturnRequest_Success()
	{
		//Given
		final ReturnEntryData returnEntryData1 = createReturnEntryData(1L, "HOLD", null, "DamagedInTransit", 0);
		final ReturnEntryData returnEntryData2 = createReturnEntryData(1L, "HOLD", null, "DamagedInTransit", 1);
		final List<ReturnEntryData> returnEntryDatas = Arrays.asList(returnEntryData1, returnEntryData2);
		final ReturnRequestData returnRequestData = createReturnRequestData(returnEntryDatas, "O-K2010-C0000-001", Boolean.FALSE);
		final ReturnRequestData createdReturnRequest = omsReturnFacade.createReturnRequest(returnRequestData);

		//When
		omsReturnFacade.cancelReturnRequest(
				createCancelReturnRequestData(createdReturnRequest.getCode(), CancelReason.OTHER, "successful test"));
	}


	@Test(expected = IllegalArgumentException.class)
	public void testCancelReturnRequest_NullRMA_Failure()
	{
		//When
		omsReturnFacade.cancelReturnRequest(createCancelReturnRequestData(null, CancelReason.OTHER, "null rma"));
	}

	@Test(expected = UnknownIdentifierException.class)
	public void testCancelReturnRequest_InvalidRMA_Failure()
	{
		//When
		omsReturnFacade.cancelReturnRequest(createCancelReturnRequestData("invalid", CancelReason.OTHER, "invalid rma"));
	}

	@Test(expected = UnknownIdentifierException.class)
	public void testCancelReturnRequest_EmptyRMA_Failure()
	{
		//When
		omsReturnFacade.cancelReturnRequest(createCancelReturnRequestData("", CancelReason.OTHER, "empty rma"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCancelReturnRequest_InvalidStatus_Failure()
	{
		//Given
		final ReturnEntryData returnEntryData1 = createReturnEntryData(1L, "HOLD", null, "DamagedInTransit", 0);
		final ReturnEntryData returnEntryData2 = createReturnEntryData(1L, "HOLD", null, "DamagedInTransit", 1);
		final List<ReturnEntryData> returnEntryDatas = Arrays.asList(returnEntryData1, returnEntryData2);
		final ReturnRequestData returnRequestData = createReturnRequestData(returnEntryDatas, "O-K2010-C0000-001", Boolean.FALSE);
		final ReturnRequestData createdReturnRequest = omsReturnFacade.createReturnRequest(returnRequestData);
		omsReturnFacade.cancelReturnRequest(
				createCancelReturnRequestData(createdReturnRequest.getCode(), CancelReason.OTHER, "successful test"));

		//When
		omsReturnFacade.cancelReturnRequest(
				createCancelReturnRequestData(createdReturnRequest.getCode(), CancelReason.OTHER, "wrong return status"));
	}

	/**
	 * Prepares returnRequestData from the given params to create returnRequest
	 *
	 * @param returnEntryDatas
	 * @param orderCode
	 * @param refundDeliveryCost
	 * @return returnRequestData populated from the given params
	 */
	protected ReturnRequestData createReturnRequestData(final List<ReturnEntryData> returnEntryDatas, final String orderCode,
			final Boolean refundDeliveryCost)
	{
		final ReturnRequestData returnRequestData = new ReturnRequestData();

		final OrderData orderData = new OrderData();
		orderData.setCode(orderCode);

		returnRequestData.setOrder(orderData);
		returnRequestData.setReturnEntries(returnEntryDatas);
		returnRequestData.setRefundDeliveryCost(refundDeliveryCost);

		return returnRequestData;
	}

	/**
	 * Prepares ReturnEntryData from the given params
	 *
	 * @param expectedQuantity
	 * @param action
	 * @param notes
	 * @param refundReason
	 * @param entryNumber
	 * @return returnEntryData
	 */
	protected ReturnEntryData createReturnEntryData(final Long expectedQuantity, final String action, final String notes,
			final String refundReason, final Integer entryNumber)
	{
		final ReturnEntryData returnEntryData = new ReturnEntryData();
		returnEntryData.setExpectedQuantity(expectedQuantity);
		returnEntryData.setAction(ReturnAction.valueOf(action));
		returnEntryData.setNotes(notes);
		returnEntryData.setRefundReason(RefundReason.valueOf(refundReason));

		final OrderEntryData orderEntryData = new OrderEntryData();
		orderEntryData.setEntryNumber(entryNumber);
		returnEntryData.setOrderEntry(orderEntryData);

		return returnEntryData;
	}

	/**
	 * Prepares {@link de.hybris.platform.ordermanagementfacades.returns.data.CancelReturnRequestData} from the given params.
	 *
	 * @param code
	 * 		the RMA code
	 * @param reason
	 * 		the reason for cancellation
	 * @param notes
	 * 		the notes for the cancellation
	 * @return {@link de.hybris.platform.ordermanagementfacades.returns.data.CancelReturnRequestData} populated from the params
	 */
	protected CancelReturnRequestData createCancelReturnRequestData(final String code, final CancelReason reason,
			final String notes)
	{
		final CancelReturnRequestData cancelReturnRequestData = new CancelReturnRequestData();
		cancelReturnRequestData.setCode(code);
		cancelReturnRequestData.setCancelReason(reason);
		cancelReturnRequestData.setNotes(notes);

		return cancelReturnRequestData;
	}
}
