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

package de.hybris.platform.ordermanagementwebservices.controllers.returns;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.basecommerce.enums.ReturnStatus;
import de.hybris.platform.commercewebservicescommons.dto.order.OrderEntryWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.OrderWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.product.PriceWsDTO;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.oauth2.constants.OAuth2Constants;
import de.hybris.platform.ordermanagementfacades.returns.data.ReturnEntryModificationWsDTO;
import de.hybris.platform.ordermanagementfacades.returns.data.ReturnRequestModificationWsDTO;
import de.hybris.platform.ordermanagementwebservices.constants.OrdermanagementwebservicesConstants;
import de.hybris.platform.ordermanagementwebservices.dto.order.CancelReasonListWsDTO;
import de.hybris.platform.ordermanagementwebservices.dto.returns.CancelReturnRequestWsDTO;
import de.hybris.platform.ordermanagementwebservices.dto.returns.RefundReasonListWsDTO;
import de.hybris.platform.ordermanagementwebservices.dto.returns.ReturnActionListWsDTO;
import de.hybris.platform.ordermanagementwebservices.dto.returns.ReturnEntrySearchPageWsDTO;
import de.hybris.platform.ordermanagementwebservices.dto.returns.ReturnEntryWsDTO;
import de.hybris.platform.ordermanagementwebservices.dto.returns.ReturnRequestWsDTO;
import de.hybris.platform.ordermanagementwebservices.dto.returns.ReturnSearchPageWsDTO;
import de.hybris.platform.ordermanagementwebservices.dto.returns.ReturnStatusListWsDTO;
import de.hybris.platform.ordermanagementwebservices.util.BaseOrderManagementWebservicesIntegrationTest;
import de.hybris.platform.webservicescommons.dto.error.ErrorListWsDTO;
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import static de.hybris.platform.webservicescommons.testsupport.client.WebservicesAssert.assertResponse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


@NeedsEmbeddedServer(webExtensions = { OrdermanagementwebservicesConstants.EXTENSIONNAME, OAuth2Constants.EXTENSIONNAME })
@IntegrationTest
public class ReturnsControllerIntegrationTest extends BaseOrderManagementWebservicesIntegrationTest
{
	private static final String ORDER_CODE_1 = "O-K2010-C0000-001";
	private static final String DAMAGED_IN_TRANSIT_REFUND_REASON = "DamagedInTransit";
	private static final String OTHER_CANCEL_REASON = "Other";
	private static final String HOLD_ACTION = "Hold";
	private static final String INVALID_REFUND_REASON = "InvalidRefundReason";
	private static final String INVALID_CANCEL_REASON = "InvalidCancelReason";
	private static final String INVALID_ACTION = "InvalidAction1";
	private static final String RETURNS = "returns";
	private static final Double DEFAULT_REFUND_AMOUNT = 60.30;
	final List<ReturnEntryModificationWsDTO> returnEntryModificationWsDTOList = new ArrayList<>();

	@Before
	public void setup()
	{
		final List<String> extensionNames = getExtensionNames();

		if (extensionNames.contains("yacceleratorordermanagement"))
		{
			getSetupImpexService().importImpexFile("/impex/projectdata-dynamic-business-process-order.impex", true);
			getSetupImpexService().importImpexFile("/impex/projectdata-dynamic-business-process-consignment.impex", true);
			getSetupImpexService().importImpexFile("/impex/projectdata-dynamic-business-process-return.impex", true);
			getSetupImpexService().importImpexFile("/impex/projectdata-dynamic-business-process-sendReturnLabelEmail.impex", true);
		}
		try
		{
			importCsv("/test/OrderTestData.csv", "UTF-8");
		}
		catch (final ImpExException e)
		{
			e.printStackTrace();
		}
	}

	@Test
	public void getAllReturnCancellationReasons()
	{
		//When
		final CancelReasonListWsDTO result = getReturnCancellationReasons();
		//then
		assertEquals(6, result.getReasons().size());
	}

	@Test
	public void getAllRefundReasons()
	{
		//When
		final RefundReasonListWsDTO result = getRefundReasons();

		//Then
		assertEquals(11, result.getRefundReasons().size());
	}

	@Test
	public void getAllReturnActions()
	{
		//When
		final ReturnActionListWsDTO result = getReturnActions();

		//Then
		assertEquals(2, result.getReturnActions().size());
		assertTrue(result.getReturnActions().containsAll(Arrays.asList("IMMEDIATE", "HOLD")));
	}

	@Test
	public void getAllReturnStatuses()
	{
		//When
		final ReturnStatusListWsDTO result = getReturnStatuses();

		//Then
		assertEquals(14, result.getStatuses().size());
	}

	@Test
	public void getAllReturns()
	{
		//Given
		final ReturnRequestWsDTO returnRequest = createDefaultReturn();

		//When
		final ReturnSearchPageWsDTO result = getReturns();

		//Then
		assertEquals(1, result.getReturns().size());
		assertEquals(returnRequest.getRma(), result.getReturns().get(0).getRma());
		assertEquals(returnRequest.getStatus(), result.getReturns().get(0).getStatus());
	}

	@Test
	public void getAllReturnsByStatus_Success()
	{
		//Given
		final ReturnRequestWsDTO returnRequest = createDefaultReturn();

		//When
		final ReturnSearchPageWsDTO result = getReturnsByStatus(ReturnStatus.APPROVAL_PENDING.getCode(),
				ReturnSearchPageWsDTO.class);

		//Then
		assertEquals(1, result.getReturns().size());
		assertEquals(returnRequest.getRma(), result.getReturns().get(0).getRma());
		assertEquals(returnRequest.getStatus(), result.getReturns().get(0).getStatus());
	}

	@Test
	public void getAllReturnsByStatus_FailureWrongStatus()
	{
		//When
		final ErrorListWsDTO result = getReturnsByStatus("WRONG_STATUS", ErrorListWsDTO.class);

		//Then
		assertEquals(1, result.getErrors().size());
	}

	@Test
	public void getReturnForCode()
	{
		//Given
		final ReturnRequestWsDTO returnRequest = createDefaultReturn();

		//When
		final ReturnRequestWsDTO result = getReturnForReturnCode(returnRequest.getCode());

		//Then
		assertEquals(returnRequest.getRma(), result.getRma());
		assertEquals(returnRequest.getStatus(), result.getStatus());
	}

	@Test
	public void getAllReturnEntriesForOrderCode()
	{
		//Given
		final ReturnRequestWsDTO returnRequest = createDefaultReturn();

		//When
		final ReturnEntrySearchPageWsDTO result = getReturnEntriesForReturnCode(returnRequest.getCode());

		//Then
		assertEquals(2, result.getReturnEntries().size());
	}

	@Test
	public void testPostRequestManualPaymentReversalForReturnRequest()
	{
		//Given
		final ReturnRequestWsDTO returnRequest = createDefaultReturn();

		//When
		final Response result = postRequestManualPaymentReversalForReturnRequest(returnRequest.getRma());

		//Then
		assertEquals(400, result.getStatus());
	}

	@Test
	public void testPostRequestManualTaxReversalForReturnRequest()
	{
		//Given
		final ReturnRequestWsDTO returnRequest = createDefaultReturn();

		//When
		final Response result = postRequestManualTaxReversalForReturnRequest(returnRequest.getRma());

		//Then
		assertEquals(400, result.getStatus());
	}

	@Test
	public void testPostReturnRequest_ValidQtyReturn_Success()
	{
		//Given
		final ReturnEntryWsDTO returnEntryWsDTO1 = createReturnEntryWsDTO(1L, HOLD_ACTION, null, DAMAGED_IN_TRANSIT_REFUND_REASON,
				0, DEFAULT_REFUND_AMOUNT);
		final ReturnEntryWsDTO returnEntryWsDTO2 = createReturnEntryWsDTO(1L, HOLD_ACTION, null, DAMAGED_IN_TRANSIT_REFUND_REASON,
				1, DEFAULT_REFUND_AMOUNT);
		final List<ReturnEntryWsDTO> returnEntriesWsDTO = Arrays.asList(returnEntryWsDTO1, returnEntryWsDTO2);
		final ReturnRequestWsDTO returnRequestWsDTO = createReturnRequestWsDTO(returnEntriesWsDTO, ORDER_CODE_1, Boolean.FALSE);

		//When
		final ReturnRequestWsDTO createdReturnRequest = postDefaultRestCall(RETURNS, DEFAULT_FIELDS, returnRequestWsDTO,
				ReturnRequestWsDTO.class);
		//then
		assertNotNull(createdReturnRequest.getRma());
		assertEquals(ReturnStatus.APPROVAL_PENDING.toString(), createdReturnRequest.getStatus());
	}

	@Test
	public void testPostReturnRequest_InvalidRefundReason_Failure()
	{
		//Given
		final ReturnEntryWsDTO returnEntryWsDTO1 = createReturnEntryWsDTO(1L, HOLD_ACTION, null, DAMAGED_IN_TRANSIT_REFUND_REASON,
				0, DEFAULT_REFUND_AMOUNT);
		final ReturnEntryWsDTO returnEntryWsDTO2 = createReturnEntryWsDTO(1L, HOLD_ACTION, null, INVALID_REFUND_REASON, 1,
				DEFAULT_REFUND_AMOUNT);
		final List<ReturnEntryWsDTO> returnEntriesWsDTO = Arrays.asList(returnEntryWsDTO1, returnEntryWsDTO2);
		final ReturnRequestWsDTO returnRequestWsDTO = createReturnRequestWsDTO(returnEntriesWsDTO, ORDER_CODE_1, Boolean.FALSE);
		// When
		final Response response = postDefaultRestCall(RETURNS, DEFAULT_FIELDS, returnRequestWsDTO);
		//Then
		assertBadRequestWithContent(response, "invalid", "refundReason", "parameter");
	}

	@Test
	public void testPostReturnRequest_InvalidHoldAction_Failure()
	{
		//Given
		final ReturnEntryWsDTO returnEntryWsDTO1 = createReturnEntryWsDTO(1L, HOLD_ACTION, null, DAMAGED_IN_TRANSIT_REFUND_REASON,
				0, DEFAULT_REFUND_AMOUNT);
		final ReturnEntryWsDTO returnEntryWsDTO2 = createReturnEntryWsDTO(1L, INVALID_ACTION, null,
				DAMAGED_IN_TRANSIT_REFUND_REASON, 1, DEFAULT_REFUND_AMOUNT);
		final List<ReturnEntryWsDTO> returnEntriesWsDTO = Arrays.asList(returnEntryWsDTO1, returnEntryWsDTO2);
		final ReturnRequestWsDTO returnRequestWsDTO = createReturnRequestWsDTO(returnEntriesWsDTO, ORDER_CODE_1, Boolean.FALSE);
		// When
		final Response response = postDefaultRestCall(RETURNS, DEFAULT_FIELDS, returnRequestWsDTO);
		//Then
		assertBadRequestWithContent(response, "invalid", "action", "parameter");
	}

	@Test
	public void testPostReturnRequest_NegativeRefundAmount_Failure()
	{
		//Given
		final ReturnEntryWsDTO returnEntryWsDTO1 = createReturnEntryWsDTO(1L, HOLD_ACTION, null, DAMAGED_IN_TRANSIT_REFUND_REASON,
				0, -10.0);
		final List<ReturnEntryWsDTO> returnEntriesWsDTO = Arrays.asList(returnEntryWsDTO1);
		final ReturnRequestWsDTO returnRequestWsDTO = createReturnRequestWsDTO(returnEntriesWsDTO, ORDER_CODE_1, Boolean.FALSE);
		// When
		final Response response = postDefaultRestCall(RETURNS, DEFAULT_FIELDS, returnRequestWsDTO);
		//Then
		assertBadRequestWithContent(response, "invalid", "PriceWsDTO", "parameter");
	}

	@Test
	public void testPostReturnRequest_ZeroRefundAmount_Failure()
	{
		//Given
		final ReturnEntryWsDTO returnEntryWsDTO1 = createReturnEntryWsDTO(1L, HOLD_ACTION, null, DAMAGED_IN_TRANSIT_REFUND_REASON,
				0, 0.0);
		final List<ReturnEntryWsDTO> returnEntriesWsDTO = Arrays.asList(returnEntryWsDTO1);
		final ReturnRequestWsDTO returnRequestWsDTO = createReturnRequestWsDTO(returnEntriesWsDTO, ORDER_CODE_1, Boolean.FALSE);
		// When
		final Response response = postDefaultRestCall(RETURNS, DEFAULT_FIELDS, returnRequestWsDTO);
		//Then
		assertBadRequestWithContent(response, "invalid", "PriceWsDTO", "parameter");
	}

	@Test
	public void testPostReturnRequest_NullRefundAmount_Failure()
	{
		//Given
		final ReturnEntryWsDTO returnEntryWsDTO1 = createReturnEntryWsDTO(1L, HOLD_ACTION, null, DAMAGED_IN_TRANSIT_REFUND_REASON,
				0, null);
		final List<ReturnEntryWsDTO> returnEntriesWsDTO = Arrays.asList(returnEntryWsDTO1);
		final ReturnRequestWsDTO returnRequestWsDTO = createReturnRequestWsDTO(returnEntriesWsDTO, ORDER_CODE_1, Boolean.FALSE);
		// When
		final Response response = postDefaultRestCall(RETURNS, DEFAULT_FIELDS, returnRequestWsDTO);
		//Then
		assertBadRequestWithContent(response, "invalid", "PriceWsDTO", "parameter");
	}

	@Test
	public void testApproveReturnRequest_Success()
	{
		//Given
		final ReturnEntryWsDTO returnEntryWsDTO1 = createReturnEntryWsDTO(1L, HOLD_ACTION, null, DAMAGED_IN_TRANSIT_REFUND_REASON,
				0, DEFAULT_REFUND_AMOUNT);
		final ReturnEntryWsDTO returnEntryWsDTO2 = createReturnEntryWsDTO(1L, HOLD_ACTION, null, DAMAGED_IN_TRANSIT_REFUND_REASON,
				1, DEFAULT_REFUND_AMOUNT);
		final List<ReturnEntryWsDTO> returnEntriesWsDTO = Arrays.asList(returnEntryWsDTO1, returnEntryWsDTO2);
		final ReturnRequestWsDTO returnRequestWsDTO = createReturnRequestWsDTO(returnEntriesWsDTO, ORDER_CODE_1, Boolean.FALSE);
		final ReturnRequestWsDTO createdReturnRequest = postDefaultRestCall(RETURNS, DEFAULT_FIELDS, returnRequestWsDTO,
				ReturnRequestWsDTO.class);
		//When
		final Response response = postEmptyBodyRestCall(RETURNS + "/" + createdReturnRequest.getRma() + "/approve");
		response.bufferEntity();

		//then
		assertResponse(Status.OK, Optional.empty(), response);

	}

	@Test
	public void testCancelReturnRequest_Success()
	{
		//Given
		final ReturnEntryWsDTO returnEntryWsDTO1 = createReturnEntryWsDTO(1L, HOLD_ACTION, null, DAMAGED_IN_TRANSIT_REFUND_REASON,
				0, DEFAULT_REFUND_AMOUNT);
		final ReturnEntryWsDTO returnEntryWsDTO2 = createReturnEntryWsDTO(1L, HOLD_ACTION, null, DAMAGED_IN_TRANSIT_REFUND_REASON,
				1, DEFAULT_REFUND_AMOUNT);
		final List<ReturnEntryWsDTO> returnEntriesWsDTO = Arrays.asList(returnEntryWsDTO1, returnEntryWsDTO2);
		final ReturnRequestWsDTO returnRequestWsDTO = createReturnRequestWsDTO(returnEntriesWsDTO, ORDER_CODE_1, Boolean.FALSE);
		final ReturnRequestWsDTO createdReturnRequest = postDefaultRestCall(RETURNS, DEFAULT_FIELDS, returnRequestWsDTO,
				ReturnRequestWsDTO.class);
		//When
		final Response response = postCancelReturnRequestByDefault(
				createCancelReturnRequestWsDTO(createdReturnRequest.getRma(), OTHER_CANCEL_REASON, "This is a test"));
		//Then
		assertResponse(Status.OK, Optional.empty(), response);
	}

	@Test
	public void testUpdateReturnRequest_Success()
	{
		//Given
		final ReturnRequestWsDTO createdReturnRequest = createDefaultReturn();
		//When
		returnEntryModificationWsDTOList.add(createReturnEntryModificationWsDTO("testProduct0", new BigDecimal("100.12")));


		final ReturnRequestWsDTO response = putUpdateReturnByReturnCode(createdReturnRequest.getRma(),
				createReturnRequestModificationWsDTO(returnEntryModificationWsDTOList, false));
		//Then
		assertEquals(response.getRma(), createdReturnRequest.getRma());
		assertEquals(false, response.getRefundDeliveryCost());
		assertEquals(response.getReturnEntries().get(0).getRefundAmount().getValue(), new BigDecimal("100.12000000"));
	}

	@Test
	public void testCancelReturnRequest_InvalidReason_Failure()
	{
		//Given
		final ReturnEntryWsDTO returnEntryWsDTO1 = createReturnEntryWsDTO(1L, HOLD_ACTION, null, DAMAGED_IN_TRANSIT_REFUND_REASON,
				0, DEFAULT_REFUND_AMOUNT);
		final ReturnEntryWsDTO returnEntryWsDTO2 = createReturnEntryWsDTO(1L, HOLD_ACTION, null, DAMAGED_IN_TRANSIT_REFUND_REASON,
				1, DEFAULT_REFUND_AMOUNT);
		final List<ReturnEntryWsDTO> returnEntriesWsDTO = Arrays.asList(returnEntryWsDTO1, returnEntryWsDTO2);
		final ReturnRequestWsDTO returnRequestWsDTO = createReturnRequestWsDTO(returnEntriesWsDTO, ORDER_CODE_1, Boolean.FALSE);
		final ReturnRequestWsDTO createdReturnRequest = postDefaultRestCall(RETURNS, DEFAULT_FIELDS, returnRequestWsDTO,
				ReturnRequestWsDTO.class);
		//When
		final Response response = postCancelReturnRequestByDefault(
				createCancelReturnRequestWsDTO(createdReturnRequest.getRma(), INVALID_CANCEL_REASON, "This is a test"));

		//Then
		assertBadRequestWithContent(response, "invalid", "cancelReason", "parameter");
	}

	@Test
	public void testReturnRequestDuplicateEntries_Failure()
	{
		//Given
		final ReturnEntryWsDTO returnEntryWsDTO1 = createReturnEntryWsDTO(1L, HOLD_ACTION, null, DAMAGED_IN_TRANSIT_REFUND_REASON,
				0, DEFAULT_REFUND_AMOUNT);
		final ReturnEntryWsDTO returnEntryWsDTO2 = createReturnEntryWsDTO(1L, HOLD_ACTION, null, DAMAGED_IN_TRANSIT_REFUND_REASON,
				0, DEFAULT_REFUND_AMOUNT);
		final List<ReturnEntryWsDTO> returnEntriesWsDTO = Arrays.asList(returnEntryWsDTO1, returnEntryWsDTO2);
		final ReturnRequestWsDTO returnRequestWsDTO = createReturnRequestWsDTO(returnEntriesWsDTO, ORDER_CODE_1, Boolean.FALSE);

		//When
		final Response response = postDefaultRestCall(RETURNS, DEFAULT_FIELDS, returnRequestWsDTO);

		//then
		assertResponse(Status.BAD_REQUEST, Optional.empty(), response);
	}

	@Test
	public void testReturnRequestInvalidReturnQuantity_Failure()
	{
		//Given
		final ReturnEntryWsDTO returnEntryWsDTO1 = createReturnEntryWsDTO(10L, HOLD_ACTION, null, DAMAGED_IN_TRANSIT_REFUND_REASON,
				0, DEFAULT_REFUND_AMOUNT);
		final List<ReturnEntryWsDTO> returnEntriesWsDTO = Arrays.asList(returnEntryWsDTO1);
		final ReturnRequestWsDTO returnRequestWsDTO = createReturnRequestWsDTO(returnEntriesWsDTO, ORDER_CODE_1, Boolean.FALSE);

		//When
		final Response response = postDefaultRestCall(RETURNS, DEFAULT_FIELDS, returnRequestWsDTO);

		//then
		assertResponse(Status.BAD_REQUEST, Optional.empty(), response);
	}

	/**
	 * Prepares requestbody from the given params for the POST call to create return
	 *
	 * @param returnEntriesWsDTO
	 * @param orderCode
	 * @param refundDeliveryCost
	 * @return returnRequestWsDTO populated from the given params
	 */
	protected ReturnRequestWsDTO createReturnRequestWsDTO(final List<ReturnEntryWsDTO> returnEntriesWsDTO, final String orderCode,
			final Boolean refundDeliveryCost)
	{
		final ReturnRequestWsDTO returnRequestWsDTO = new ReturnRequestWsDTO();

		final OrderWsDTO orderWsDTO = new OrderWsDTO();
		orderWsDTO.setCode(orderCode);

		returnRequestWsDTO.setOrder(orderWsDTO);
		returnRequestWsDTO.setReturnEntries(returnEntriesWsDTO);
		returnRequestWsDTO.setRefundDeliveryCost(refundDeliveryCost);

		return returnRequestWsDTO;
	}

	/**
	 * Prepares requestbody from the given params for the ReturnEntryModificationWsDTO
	 *
	 * @param returnEntryModificationWsDTOs
	 * 		list of {@link ReturnEntryModificationWsDTO}
	 * @param refundDeliveryCost
	 * 		true if add deliver cost
	 * @return {@link ReturnRequestModificationWsDTO}
	 */
	protected ReturnRequestModificationWsDTO createReturnRequestModificationWsDTO(
			final List<ReturnEntryModificationWsDTO> returnEntryModificationWsDTOs, final Boolean refundDeliveryCost)
	{
		final ReturnRequestModificationWsDTO returnRequestModificationWsDTO = new ReturnRequestModificationWsDTO();
		returnRequestModificationWsDTO.setReturnEntries(returnEntryModificationWsDTOs);
		returnRequestModificationWsDTO.setRefundDeliveryCost(refundDeliveryCost);
		return returnRequestModificationWsDTO;
	}

	/**
	 * Prepares requestbody from the given params for the ReturnEntryModificationWsDTO
	 *
	 * @param productCode
	 * 		product code
	 * @param amount
	 * 		total amount for the return entry
	 * @return {@link ReturnEntryModificationWsDTO}
	 */
	protected ReturnEntryModificationWsDTO createReturnEntryModificationWsDTO(final String productCode, final BigDecimal amount)
	{
		final ReturnEntryModificationWsDTO returnEntryModificationWsDTO = new ReturnEntryModificationWsDTO();
		returnEntryModificationWsDTO.setProductCode(productCode);
		returnEntryModificationWsDTO.setRefundAmount(amount);
		return returnEntryModificationWsDTO;
	}

	/**
	 * create default return using rest call
	 *
	 * @return {@link ReturnRequestWsDTO}
	 */
	protected ReturnRequestWsDTO createDefaultReturn()
	{
		//Given
		final ReturnEntryWsDTO returnEntryWsDTO1 = createReturnEntryWsDTO(1L, "HOLD", null, "DamagedInTransit", 0,
				DEFAULT_REFUND_AMOUNT);
		final ReturnEntryWsDTO returnEntryWsDTO2 = createReturnEntryWsDTO(1L, "HOLD", null, "DamagedInTransit", 1,
				DEFAULT_REFUND_AMOUNT);
		final List<ReturnEntryWsDTO> returnEntriesWsDTO = Arrays.asList(returnEntryWsDTO1, returnEntryWsDTO2);
		final ReturnRequestWsDTO returnRequestWsDTO = createReturnRequestWsDTO(returnEntriesWsDTO, "O-K2010-C0000-001",
				Boolean.FALSE);
		return postDefaultRestCall(RETURNS, DEFAULT_FIELDS, returnRequestWsDTO, ReturnRequestWsDTO.class);
	}

	/**
	 * Prepares a request body dto from the given params for the POST call to cancel a return request.
	 *
	 * @param code
	 * 		the RMA code
	 * @param reason
	 * 		the reason for cancellation
	 * @param notes
	 * 		the notes for the cancellaiton
	 * @return {@link de.hybris.platform.ordermanagementwebservices.dto.returns.CancelReturnRequestWsDTO} populated from the params
	 */
	protected CancelReturnRequestWsDTO createCancelReturnRequestWsDTO(final String code, final String reason, final String notes)
	{
		final CancelReturnRequestWsDTO cancelReturnRequestWsDTO = new CancelReturnRequestWsDTO();
		cancelReturnRequestWsDTO.setCode(code);
		cancelReturnRequestWsDTO.setCancelReason(reason);
		cancelReturnRequestWsDTO.setNotes(notes);

		return cancelReturnRequestWsDTO;
	}

	/**
	 * Prepares ReturnEntryWsDTO from the given params
	 *
	 * @param expectedQuantity
	 * @param action
	 * @param notes
	 * @param refundReason
	 * @param entryNumber
	 * @param refundAmount
	 * @return returnEntryWsDTO
	 */
	protected ReturnEntryWsDTO createReturnEntryWsDTO(final Long expectedQuantity, final String action, final String notes,
			final String refundReason, final Integer entryNumber, final Double refundAmount)
	{
		final ReturnEntryWsDTO returnEntryWsDTO = new ReturnEntryWsDTO();
		returnEntryWsDTO.setExpectedQuantity(expectedQuantity);
		returnEntryWsDTO.setAction(action);
		returnEntryWsDTO.setNotes(notes);
		returnEntryWsDTO.setRefundReason(refundReason);
		final PriceWsDTO refundAmountDto = new PriceWsDTO();
		refundAmountDto.setValue(refundAmount != null ? BigDecimal.valueOf(refundAmount) : null);
		returnEntryWsDTO.setRefundAmount(refundAmountDto);

		final OrderEntryWsDTO orderEntryWsDTO = new OrderEntryWsDTO();
		orderEntryWsDTO.setEntryNumber(entryNumber);
		returnEntryWsDTO.setOrderEntry(orderEntryWsDTO);

		return returnEntryWsDTO;
	}
}
