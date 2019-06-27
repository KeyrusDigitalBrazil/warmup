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
package de.hybris.platform.ordermanagementwebservices.util;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.commerceservices.setup.SetupImpexService;
import de.hybris.platform.commercewebservicescommons.dto.order.OrderEntryWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.OrderWsDTO;
import de.hybris.platform.core.Registry;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.oauth2.constants.OAuth2Constants;
import de.hybris.platform.ordermanagementfacades.returns.data.ReturnRequestModificationWsDTO;
import de.hybris.platform.ordermanagementwebservices.constants.OrdermanagementwebservicesConstants;
import de.hybris.platform.ordermanagementwebservices.dto.fraud.FraudReportListWsDTO;
import de.hybris.platform.ordermanagementwebservices.dto.order.CancelReasonListWsDTO;
import de.hybris.platform.ordermanagementwebservices.dto.order.OrderCancelRequestWsDTO;
import de.hybris.platform.ordermanagementwebservices.dto.order.OrderEntrySearchPageWsDTO;
import de.hybris.platform.ordermanagementwebservices.dto.order.OrderRequestWsDTO;
import de.hybris.platform.ordermanagementwebservices.dto.order.OrderSearchPageWsDto;
import de.hybris.platform.ordermanagementwebservices.dto.order.OrderStatusListWsDTO;
import de.hybris.platform.ordermanagementwebservices.dto.returns.CancelReturnRequestWsDTO;
import de.hybris.platform.ordermanagementwebservices.dto.returns.RefundReasonListWsDTO;
import de.hybris.platform.ordermanagementwebservices.dto.returns.ReturnActionListWsDTO;
import de.hybris.platform.ordermanagementwebservices.dto.returns.ReturnEntrySearchPageWsDTO;
import de.hybris.platform.ordermanagementwebservices.dto.returns.ReturnRequestWsDTO;
import de.hybris.platform.ordermanagementwebservices.dto.returns.ReturnSearchPageWsDTO;
import de.hybris.platform.ordermanagementwebservices.dto.returns.ReturnStatusListWsDTO;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.webservicescommons.dto.error.ErrorListWsDTO;
import de.hybris.platform.webservicescommons.dto.error.ErrorWsDTO;
import de.hybris.platform.webservicescommons.testsupport.client.WsRequestBuilder;
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer;

import javax.annotation.Resource;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import java.util.List;
import java.util.Optional;

import org.springframework.test.context.ContextConfiguration;

import static de.hybris.platform.webservicescommons.testsupport.client.WebservicesAssert.assertResponse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


@NeedsEmbeddedServer(webExtensions = { OrdermanagementwebservicesConstants.EXTENSIONNAME, OAuth2Constants.EXTENSIONNAME })
@IntegrationTest
@ContextConfiguration(locations = { "classpath:/ordermanagementwebservices-spring-test.xml" })
public class BaseOrderManagementWebservicesIntegrationTest extends ServicelayerTest
{
	@Resource
	private EnumerationService enumerationService;
	@Resource
	private SetupImpexService setupImpexService;

	protected static final String DEFAULT_FIELDS = "DEFAULT";
	protected static final String DEFAULT_CURRENT_PAGE = "0";
	protected static final String DEFAULT_PAGE_SIZE = "100";

	protected OrderSearchPageWsDto getAllOrderByDefault()
	{
		return getDefaultRestCall("orders", DEFAULT_FIELDS, DEFAULT_CURRENT_PAGE, DEFAULT_PAGE_SIZE, OrderSearchPageWsDto.class);
	}

	protected OrderWsDTO getOrderByCode(final String code)
	{
		return getDefaultRestCall("orders/" + code, "FULL", DEFAULT_CURRENT_PAGE, DEFAULT_PAGE_SIZE, OrderWsDTO.class);
	}

	protected <T> T getOrdersByStatuses(final String orderStatuses, final Class<T> responseType)
	{
		return getDefaultRestCall("orders/status/" + orderStatuses, DEFAULT_FIELDS, DEFAULT_CURRENT_PAGE, DEFAULT_PAGE_SIZE,
				responseType);
	}

	protected OrderEntrySearchPageWsDTO getOrderEntriesForOrderCode(final String code)
	{
		return getDefaultRestCall("orders/" + code + "/entries", "FULL", DEFAULT_CURRENT_PAGE, DEFAULT_PAGE_SIZE,
				OrderEntrySearchPageWsDTO.class);
	}

	protected OrderEntryWsDTO getOrderEntryForOrderCodeAndEntryNumber(final String code, final String entryNumber)
	{
		return getDefaultRestCall("orders/" + code + "/entries/" + entryNumber, "FULL", DEFAULT_CURRENT_PAGE, DEFAULT_PAGE_SIZE,
				OrderEntryWsDTO.class);
	}

	protected OrderStatusListWsDTO getOrderStatusByDefault()
	{
		return getDefaultRestCall("orders/statuses", DEFAULT_FIELDS, DEFAULT_CURRENT_PAGE, DEFAULT_PAGE_SIZE,
				OrderStatusListWsDTO.class);
	}

	protected FraudReportListWsDTO getOrderFraudReports(final String code)
	{
		return getDefaultRestCall("orders/" + code + "/fraud-reports", "FULL", DEFAULT_CURRENT_PAGE, DEFAULT_PAGE_SIZE,
				FraudReportListWsDTO.class);
	}

	protected CancelReasonListWsDTO getOrderCancellationReasons()
	{
		return getDefaultRestCall("orders/cancel-reasons", DEFAULT_FIELDS, DEFAULT_CURRENT_PAGE, DEFAULT_PAGE_SIZE,
				CancelReasonListWsDTO.class);
	}

	protected CancelReasonListWsDTO getReturnCancellationReasons()
	{
		return getDefaultRestCall("returns/cancel-reasons", DEFAULT_FIELDS, DEFAULT_CURRENT_PAGE, DEFAULT_PAGE_SIZE,
				CancelReasonListWsDTO.class);
	}

	protected RefundReasonListWsDTO getRefundReasons()
	{
		return getDefaultRestCall("returns/refund-reasons", DEFAULT_FIELDS, DEFAULT_CURRENT_PAGE, DEFAULT_PAGE_SIZE,
				RefundReasonListWsDTO.class);
	}

	protected ReturnActionListWsDTO getReturnActions()
	{
		return getDefaultRestCall("returns/actions", DEFAULT_FIELDS, DEFAULT_CURRENT_PAGE, DEFAULT_PAGE_SIZE,
				ReturnActionListWsDTO.class);
	}

	protected ReturnStatusListWsDTO getReturnStatuses()
	{
		return getDefaultRestCall("returns/statuses", DEFAULT_FIELDS, DEFAULT_CURRENT_PAGE, DEFAULT_PAGE_SIZE,
				ReturnStatusListWsDTO.class);
	}

	protected ReturnSearchPageWsDTO getReturns()
	{
		return getDefaultRestCall("returns", DEFAULT_FIELDS, DEFAULT_CURRENT_PAGE, DEFAULT_PAGE_SIZE, ReturnSearchPageWsDTO.class);
	}

	protected <T> T getReturnsByStatus(final String statuses, final Class<T> responseType)
	{
		return getDefaultRestCall("returns/status/" + statuses, DEFAULT_FIELDS, DEFAULT_CURRENT_PAGE, DEFAULT_PAGE_SIZE,
				responseType);
	}

	protected ReturnRequestWsDTO getReturnForReturnCode(final String returnCode)
	{
		return getDefaultRestCall("returns/" + returnCode, DEFAULT_FIELDS, DEFAULT_CURRENT_PAGE, DEFAULT_PAGE_SIZE,
				ReturnRequestWsDTO.class);
	}

	protected ReturnEntrySearchPageWsDTO getReturnEntriesForReturnCode(final String returnCode)
	{
		return getDefaultRestCall("returns/" + returnCode + "/entries", DEFAULT_FIELDS, DEFAULT_CURRENT_PAGE, DEFAULT_PAGE_SIZE,
				ReturnEntrySearchPageWsDTO.class);
	}

	protected Response postRequestManualPaymentReversalForReturnRequest(final String returnCode)
	{
		return postEmptyBodyRestCall("returns/" + returnCode + "/manual/reverse-payment");
	}

	protected Response postRequestManualTaxReversalForReturnRequest(final String returnCode)
	{
		return postEmptyBodyRestCall("returns/" + returnCode + "/manual/reverse-tax");
	}

	protected Response postCancelReturnRequestByDefault(final CancelReturnRequestWsDTO cancelReturnRequestWsDTO)
	{
		return postDefaultRestCall("returns/cancel", DEFAULT_FIELDS, cancelReturnRequestWsDTO);
	}

	protected Response postApproveFraudulentOrder(final String code)
	{
		return postEmptyBodyRestCall("orders/" + code + "/fraud-reports/approve");
	}

	protected Response postRejectFraudulentOrder(final String code)
	{
		return postEmptyBodyRestCall("orders/" + code + "/fraud-reports/reject");
	}

	protected Response postCancelOrder(final OrderCancelRequestWsDTO orderCancelRequestWsDTO, final String code)
	{
		return postDefaultRestCall("orders/" + code + "/cancel/", DEFAULT_FIELDS, orderCancelRequestWsDTO);
	}

	protected Response postCreateOrder(final OrderRequestWsDTO orderRequestWsDTO)
	{
		return postDefaultRestCall("orders", DEFAULT_FIELDS, orderRequestWsDTO);
	}

	protected ReturnRequestWsDTO putUpdateReturnByReturnCode(final String code,
			final ReturnRequestModificationWsDTO returnRequestModificationWsDTO)
	{
		return putDefaultRestCall("/returns/" + code, DEFAULT_FIELDS, returnRequestModificationWsDTO, ReturnRequestWsDTO.class);
	}

	protected Response postManualPaymentVoid(final String code)
	{
		return postDefaultRestCall("orders/" + code + "/manual/void-payment", DEFAULT_FIELDS, null);
	}

	protected Response postManualTaxVoid(final String code)
	{
		return postDefaultRestCall("orders/" + code + "/manual/void-tax", DEFAULT_FIELDS, null);
	}

	protected Response postManualTaxCommit(final String code)
	{
		return postDefaultRestCall("orders/" + code + "/manual/commit-tax", DEFAULT_FIELDS, null);
	}

	protected Response postManualTaxRequote(final String code)
	{
		return postDefaultRestCall("orders/" + code + "/manual/requote-tax", DEFAULT_FIELDS, null);
	}

	protected Response postManualPaymentReauth(final String code)
	{
		return postDefaultRestCall("orders/" + code + "/manual/reauth-payment", DEFAULT_FIELDS, null);
	}

	protected Response postManualDeliveryCostCommit(final String code)
	{
		return postDefaultRestCall("orders/" + code + "/manual/delivery-cost-commit", DEFAULT_FIELDS, null);
	}

	/**
	 * Builds a GET rest call.
	 *
	 * @param path
	 * 		the url for the call
	 * @param fields
	 * 		contains pagination information
	 * @param currentPage
	 * 		the current page of the request
	 * @param pageSize
	 * 		total page size
	 * @return
	 */
	protected <T> T getDefaultRestCall(final String path, final String fields, final String currentPage, final String pageSize,
			final Class<T> responseType)
	{
		final Response result = getWsRequestBuilder().path(path).queryParam("fields", fields).queryParam("currentPage", currentPage)
				.queryParam("pageSize", pageSize).build().accept(MediaType.APPLICATION_JSON).get();
		result.bufferEntity();
		return result.readEntity(responseType);
	}

	/**
	 * Builds a POST rest call with the return type <T>.
	 *
	 * @param path
	 * 		the url for the call
	 * @param fields
	 * 		contains pagination information
	 * @param requestBodyWsDTO
	 * 		the dto object sent with the request
	 * @param responseType
	 * 		type of class to return
	 * @param <T>
	 * 		type of the body object
	 * @return the request class to return after the execution of the call
	 */
	protected <T> T postDefaultRestCall(final String path, final String fields, final T requestBodyWsDTO,
			final Class<T> responseType)
	{
		return getWsRequestBuilder().path(path).queryParam("fields", fields).build().accept(MediaType.APPLICATION_JSON)
				.post(Entity.entity(requestBodyWsDTO, MediaType.APPLICATION_JSON), responseType);
	}

	/**
	 * Builds a POST rest call.
	 *
	 * @param path
	 * 		the url for the call
	 * @param fields
	 * 		contains pagination information
	 * @param requestBodyWsDTO
	 * 		the dto object sent with the request
	 * @param <T>
	 * 		type of the body object
	 * @return the result of the call
	 */
	protected <T> Response postDefaultRestCall(final String path, final String fields, final T requestBodyWsDTO)
	{
		final Response result = getWsRequestBuilder().path(path).queryParam("fields", fields).build()
				.accept(MediaType.APPLICATION_JSON).post(Entity.entity(requestBodyWsDTO, MediaType.APPLICATION_JSON));
		result.bufferEntity();
		return result;
	}

	/**
	 * this method is to build the rest call with null body for post with the return type Response
	 *
	 * @param path
	 * @return {@link Response}
	 */
	protected Response postEmptyBodyRestCall(final String path)
	{
		return getWsRequestBuilder().path(path).build().post(Entity.entity(null, MediaType.APPLICATION_JSON));
	}

	/**
	 * Builds a PUT rest call
	 *
	 * @param path
	 * 		the url for the call
	 * @param fields
	 * 		contains pagination information
	 * @param requestBodyWsDTO
	 * 		the current dto which is to be updated
	 * @param responseType
	 * 		type of class to return
	 * @param <T>
	 * @return the request class to return after the execution of the call
	 */
	protected <S, T> T putDefaultRestCall(final String path, final String fields, final S requestBodyWsDTO,
			final Class<T> responseType)
	{
		return getWsRequestBuilder().path(path).queryParam("fields", fields).build().accept(MediaType.APPLICATION_JSON)
				.put(Entity.entity(requestBodyWsDTO, MediaType.APPLICATION_JSON), responseType);
	}

	/**
	 * Validates the first {@link ErrorWsDTO} content of the {@link ErrorListWsDTO} of the bad request
	 *
	 * @param response
	 * 		bad request response
	 * @param errorReason
	 * 		error reason
	 * @param errorSubject
	 * 		error subject
	 * @param ErrorSubjectType
	 * 		error subject type
	 */
	protected void assertBadRequestWithContent(final Response response, final String errorReason, final String errorSubject,
			final String ErrorSubjectType)
	{
		assertResponse(Status.BAD_REQUEST, Optional.empty(), response);
		final ErrorListWsDTO errors = response.readEntity(ErrorListWsDTO.class);
		assertNotNull(errors);
		assertNotNull(errors.getErrors());
		assertEquals(errors.getErrors().size(), 1);
		final ErrorWsDTO error = errors.getErrors().get(0);
		assertEquals(error.getReason(), errorReason);
		assertEquals(error.getSubject(), errorSubject);
		assertEquals(error.getSubjectType(), ErrorSubjectType);
	}

	/**
	 * Retrieves a new unauthenticated {@link WsRequestBuilder} in order to build web requests.
	 *
	 * @return the unauthenticated {@link WsRequestBuilder}
	 */
	protected WsRequestBuilder getWsRequestBuilder()
	{
		return new WsRequestBuilder().extensionName(OrdermanagementwebservicesConstants.EXTENSIONNAME);
	}

	/**
	 * Gets a {@link List} of all extensions loaded in the current setup.
	 *
	 * @return populated {@link List} of all loaded extensions
	 */
	protected List<String> getExtensionNames()
	{
		return Registry.getCurrentTenant().getTenantSpecificExtensionNames();
	}

	protected SetupImpexService getSetupImpexService()
	{
		return setupImpexService;
	}

	public void setSetupImpexService(final SetupImpexService setupImpexService)
	{
		this.setupImpexService = setupImpexService;
	}

	public EnumerationService getEnumerationService()
	{
		return enumerationService;
	}

	public void setEnumerationService(final EnumerationService enumerationService)
	{
		this.enumerationService = enumerationService;
	}
}
