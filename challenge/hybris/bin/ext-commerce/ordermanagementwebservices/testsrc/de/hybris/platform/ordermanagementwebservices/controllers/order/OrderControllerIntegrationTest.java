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
package de.hybris.platform.ordermanagementwebservices.controllers.order;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.basecommerce.enums.FraudStatus;
import de.hybris.platform.commercewebservicescommons.dto.order.CardTypeWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.OrderEntryWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.OrderWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.PaymentDetailsWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.product.PriceWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.user.AddressWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.user.CountryWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.user.RegionWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.user.UserWsDTO;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.oauth2.constants.OAuth2Constants;
import de.hybris.platform.ordermanagementwebservices.constants.OrdermanagementwebservicesConstants;
import de.hybris.platform.ordermanagementwebservices.dto.fraud.FraudReportListWsDTO;
import de.hybris.platform.ordermanagementwebservices.dto.order.CancelReasonListWsDTO;
import de.hybris.platform.ordermanagementwebservices.dto.order.OrderCancelEntryWsDTO;
import de.hybris.platform.ordermanagementwebservices.dto.order.OrderCancelRequestWsDTO;
import de.hybris.platform.ordermanagementwebservices.dto.order.OrderEntryRequestWsDTO;
import de.hybris.platform.ordermanagementwebservices.dto.order.OrderEntrySearchPageWsDTO;
import de.hybris.platform.ordermanagementwebservices.dto.order.OrderRequestWsDTO;
import de.hybris.platform.ordermanagementwebservices.dto.order.OrderSearchPageWsDto;
import de.hybris.platform.ordermanagementwebservices.dto.order.OrderStatusListWsDTO;
import de.hybris.platform.ordermanagementwebservices.dto.payment.PaymentTransactionEntryWsDTO;
import de.hybris.platform.ordermanagementwebservices.dto.payment.PaymentTransactionWsDTO;
import de.hybris.platform.ordermanagementwebservices.util.BaseOrderManagementWebservicesIntegrationTest;
import de.hybris.platform.webservicescommons.dto.error.ErrorListWsDTO;
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer;

import javax.ws.rs.core.Response;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import static de.hybris.platform.webservicescommons.testsupport.client.WebservicesAssert.assertResponse;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


@NeedsEmbeddedServer(webExtensions = { OrdermanagementwebservicesConstants.EXTENSIONNAME, OAuth2Constants.EXTENSIONNAME })
@IntegrationTest
public class OrderControllerIntegrationTest extends BaseOrderManagementWebservicesIntegrationTest
{

	private static final String ORDERCODE = "11146";

	@Before
	public void setup()
	{
		try
		{
			importCsv("/test/OrderTestData_Webservice.csv", "UTF-8");
		}
		catch (final ImpExException e)
		{
			e.printStackTrace();
		}
	}

	@Test
	public void getAllDefaultOrder()
	{
		//When
		final OrderSearchPageWsDto orderSearchPageWsDto = getAllOrderByDefault();
		//then
		assertEquals(9, orderSearchPageWsDto.getOrders().size());
		assertTrue(orderSearchPageWsDto.getOrders().stream().anyMatch(order -> "O-K2010-C0000-001".equals(order.getCode())));
		assertTrue(orderSearchPageWsDto.getOrders().stream().anyMatch(order -> "O-K2010-C0005-001".equals(order.getCode())));
	}

	@Test
	public void getAllOrdersWithoutSnapshotsAfterOrderCancellation()
	{
		//Given
		cancelOrder();

		//When
		final OrderSearchPageWsDto orderSearchPageWsDto = getAllOrderByDefault();

		//Then
		assertEquals(9, orderSearchPageWsDto.getOrders().size());
		assertTrue(orderSearchPageWsDto.getOrders().stream().anyMatch(order -> "O-K2010-C0000-001".equals(order.getCode())));
		assertTrue(orderSearchPageWsDto.getOrders().stream().anyMatch(order -> "O-K2010-C0005-001".equals(order.getCode())));
	}

	@Test
	public void getDefaultOrderByCode()
	{
		//When
		final OrderWsDTO order = getOrderByCode("O-K2010-C0000-001");
		//then
		assertEquals("O-K2010-C0000-001", order.getCode());
	}

	@Test
	public void getOrderEntriesForOrderCode()
	{
		//When
		final OrderEntrySearchPageWsDTO orderEntrySearchPageWsDTO = getOrderEntriesForOrderCode("O-K2010-C0000-001");
		//then
		assertEquals(3, orderEntrySearchPageWsDTO.getOrderEntries().size());
	}

	@Test
	public void getOrderEntryForOrderCodeAndEntryNumber()
	{
		//When
		final OrderEntryWsDTO orderEntryWsDTO = getOrderEntryForOrderCodeAndEntryNumber("O-K2010-C0000-001", "1");
		//then
		assertNotNull(orderEntryWsDTO);
	}

	@Test
	public void getOrdersByStatuses()
	{
		//When
		final OrderSearchPageWsDto orderSearchPageWsDto = getOrdersByStatuses(OrderStatus.WAIT_FRAUD_MANUAL_CHECK.toString(),
				OrderSearchPageWsDto.class);

		//then
		assertEquals(1, orderSearchPageWsDto.getOrders().size());
		assertTrue(orderSearchPageWsDto.getOrders().stream().anyMatch(order -> "O-K2010-C0002-001".equals(order.getCode())));
	}

	@Test
	public void getOrdersByStatuses_WrongStatus()
	{
		//When
		final ErrorListWsDTO errorListWsDTO = getOrdersByStatuses("Wrong_Status", ErrorListWsDTO.class);

		//then
		assertEquals(1, errorListWsDTO.getErrors().size());
	}

	@Test
	public void getAllOrderStatusByDefault()
	{
		//When
		final OrderStatusListWsDTO orderStatusList = getOrderStatusByDefault();
		//then
		assertTrue(orderStatusList.getStatuses().stream().anyMatch(status -> OrderStatus.SUSPENDED.getCode().equals(status)));
	}

	@Test
	public void getOrderFraudReports()
	{
		//When
		final FraudReportListWsDTO fraudReports = getOrderFraudReports("O-K2010-C0000-001");
		//then
		assertEquals(1, fraudReports.getReports().size());
		assertTrue(fraudReports.getReports().stream().anyMatch(report -> FraudStatus.OK.getCode().equals(report.getStatus())));
	}

	@Test
	public void getAllOrderCancellationReasons()
	{
		//When
		final CancelReasonListWsDTO result = getOrderCancellationReasons();
		//then
		assertEquals(6, result.getReasons().size());
	}

	@Test
	public void approveFraudulentOrder()
	{
		//When
		final Response result = postApproveFraudulentOrder("O-K2010-C0002-001");
		//then
		assertEquals(200, result.getStatus());
	}

	@Test
	public void rejectFraudulentOrder()
	{
		//When
		final Response result = postRejectFraudulentOrder("O-K2010-C0002-001");
		//then
		assertEquals(200, result.getStatus());
	}

	@Test
	public void cancelOrder()
	{
		//given
		final OrderCancelEntryWsDTO cancelEntryWsDTO1 = createCancelEntryWsDTO(1L, null, "Other", "0");
		final OrderCancelEntryWsDTO cancelEntryWsDTO2 = createCancelEntryWsDTO(1L, "HOLD", "Other", "1");
		final List<OrderCancelEntryWsDTO> cancelEntriesWsDTO = Arrays.asList(cancelEntryWsDTO1, cancelEntryWsDTO2);
		final OrderCancelRequestWsDTO cancelRequestWsDTO = createOrderCancelRequestWsDTO(cancelEntriesWsDTO, "admin");

		//When
		final Response result = postCancelOrder(cancelRequestWsDTO, "O-K2010-C0001-001");
		//then
		assertEquals(201, result.getStatus());
	}

	@Test
	public void createPickUpOrder()
	{
		//given
		final OrderEntryRequestWsDTO orderEntryRequestWsDTO = createOrderEntryRequestWsDTO("0");
		final List<OrderEntryRequestWsDTO> orderEntryRequestWsDTOList = Arrays.asList(orderEntryRequestWsDTO);
		final OrderRequestWsDTO orderRequestWsDTO = createOrderRequestWsDTO_PickUpOrder(orderEntryRequestWsDTOList);

		//when
		final Response result = postCreateOrder(orderRequestWsDTO);
		final OrderWsDTO orderResponse = result.readEntity(OrderWsDTO.class);
		//then
		assertEquals(201, result.getStatus());
		assertEquals("11146", orderResponse.getCode());
		assertNotNull(orderResponse.getCreated());
	}

	@Test
	public void createOrder_SingleEntry()
	{
		//given
		final OrderEntryRequestWsDTO orderEntryRequestWsDTO = createOrderEntryRequestWsDTO("0");
		final List<OrderEntryRequestWsDTO> orderEntryRequestWsDTOList = Arrays.asList(orderEntryRequestWsDTO);
		final OrderRequestWsDTO orderRequestWsDTO = createOrderRequestWsDTO(orderEntryRequestWsDTOList);

		//when
		final Response result = postCreateOrder(orderRequestWsDTO);
		final OrderWsDTO orderResponse = result.readEntity(OrderWsDTO.class);
		//then
		assertEquals(201, result.getStatus());
		assertEquals("11146", orderResponse.getCode());
		assertNotNull(orderResponse.getCreated());
	}

	@Test
	public void createOrder_MultiEntries()
	{
		//given
		final OrderEntryRequestWsDTO orderEntryRequestWsDTO_0 = createOrderEntryRequestWsDTO("0");
		final OrderEntryRequestWsDTO orderEntryRequestWsDTO_1 = createOrderEntryRequestWsDTO("1");
		List<OrderEntryRequestWsDTO> orderEntryRequestWsDTOList = new ArrayList<>();

		orderEntryRequestWsDTOList.add(orderEntryRequestWsDTO_0);
		orderEntryRequestWsDTOList.add(orderEntryRequestWsDTO_1);
		final OrderRequestWsDTO orderRequestWsDTO = createOrderRequestWsDTO(orderEntryRequestWsDTOList);

		//when
		final Response result = postCreateOrder(orderRequestWsDTO);
		final OrderWsDTO orderResponse = result.readEntity(OrderWsDTO.class);
		//then
		assertEquals(201, result.getStatus());
		assertEquals(ORDERCODE, orderResponse.getCode());
		assertNotNull(orderResponse.getCreated());
	}

	@Test
	public void createOrder_InvalidEntryNumberType_Failure()
	{
		//Given
		final OrderEntryRequestWsDTO orderEntryRequestWsDTO = createOrderEntryRequestWsDTO("0");
		orderEntryRequestWsDTO.setEntryNumber("123abc");
		final List<OrderEntryRequestWsDTO> orderEntryRequestWsDTOList = Arrays.asList(orderEntryRequestWsDTO);
		final OrderRequestWsDTO orderRequestWsDTO = createOrderRequestWsDTO(orderEntryRequestWsDTOList);
		//When
		final Response response = postCreateOrder(orderRequestWsDTO);
		//Verify
		assertBadRequestWithContent(response, "invalid", "entryNumber", "parameter");
	}

	@Test
	public void createCancel_InvalidEntryNumberType_Failure()
	{
		//Given
		final OrderCancelEntryWsDTO cancelEntryWsDTO = createCancelEntryWsDTO(1L, null, "Other", "0");
		cancelEntryWsDTO.setOrderEntryNumber("123abc");
		final List<OrderCancelEntryWsDTO> cancelEntriesWsDTO = Arrays.asList(cancelEntryWsDTO);
		final OrderCancelRequestWsDTO cancelRequestWsDTO = createOrderCancelRequestWsDTO(cancelEntriesWsDTO, "admin");
		//When
		final Response response = postCancelOrder(cancelRequestWsDTO, "O-K2010-C0001-001");
		//Verify
		assertBadRequestWithContent(response, "invalid", "orderEntryNumber", "parameter");
	}

	@Test
	public void manuallyReleasePaymentVoid_success()
	{
		final Response result = postManualPaymentVoid("O-K2010-C0003-001");

		assertResponse(Response.Status.OK, Optional.empty(), result);
	}

	@Test
	public void manuallyReleaseTaxVoid_success()
	{
		final Response result = postManualTaxVoid("O-K2010-C0004-001");

		assertResponse(Response.Status.OK, Optional.empty(), result);
	}

	@Test
	public void manuallyReleaseTaxCommit_success()
	{
		final Response result = postManualTaxCommit("O-K2010-C0006-001");

		assertResponse(Response.Status.OK, Optional.empty(), result);
	}

	@Test
	public void manuallyReleaseTaxRequote_success()
	{
		final Response result = postManualTaxRequote("O-K2010-C0007-001");

		assertResponse(Response.Status.OK, Optional.empty(), result);
	}

	@Test
	public void manuallyReleasePaymentReauth_success()
	{
		final Response result = postManualPaymentReauth("O-K2018-C0008-001");

		assertResponse(Response.Status.OK, Optional.empty(), result);
	}

	@Test
	public void manuallyReleaseDeliveryCostCommit_success()
	{
		final Response result = postManualDeliveryCostCommit("O-K2010-C0006-001");

		assertResponse(Response.Status.OK, Optional.empty(), result);
	}

	/**
	 * Prepares OrderCancelEntryWsDTO from the given params
	 *
	 * @param CancelQuantity
	 * @param notes
	 * @param cancelReason
	 * @param entryNumber
	 * @return orderCancelEntryWsDTO
	 */
	protected OrderCancelEntryWsDTO createCancelEntryWsDTO(final Long CancelQuantity, final String notes,
			final String cancelReason, final String entryNumber)
	{
		final OrderCancelEntryWsDTO orderCancelEntryWsDTO = new OrderCancelEntryWsDTO();
		orderCancelEntryWsDTO.setCancelQuantity(CancelQuantity);
		orderCancelEntryWsDTO.setNotes(notes);
		orderCancelEntryWsDTO.setCancelReason(cancelReason);

		orderCancelEntryWsDTO.setOrderEntryNumber(entryNumber);

		return orderCancelEntryWsDTO;
	}

	/**
	 * Prepares requestbody from the given params for the POST call to create cancellation
	 *
	 * @param orderCancelEntriesWsDTO
	 * @param user
	 * @return orderCancelRequestWsDTO populated from the given params
	 */
	protected OrderCancelRequestWsDTO createOrderCancelRequestWsDTO(final List<OrderCancelEntryWsDTO> orderCancelEntriesWsDTO,
			final String user)
	{
		final OrderCancelRequestWsDTO orderCancelRequestWsDTO = new OrderCancelRequestWsDTO();

		orderCancelRequestWsDTO.setEntries(orderCancelEntriesWsDTO);
		orderCancelRequestWsDTO.setUserId(user);
		return orderCancelRequestWsDTO;
	}


	protected OrderEntryRequestWsDTO createOrderEntryRequestWsDTO(final String entryNumber)
	{
		final OrderEntryRequestWsDTO orderEntryRequestWsDTO = new OrderEntryRequestWsDTO();

		orderEntryRequestWsDTO.setProductCode("testProduct0");
		orderEntryRequestWsDTO.setUnitCode("pieces");
		orderEntryRequestWsDTO.setEntryNumber(entryNumber);

		orderEntryRequestWsDTO.setBasePrice(99.85);
		orderEntryRequestWsDTO.setQuantity(5L);

		final PriceWsDTO totalPrice = new PriceWsDTO();
		totalPrice.setValue(new BigDecimal(499.25));

		orderEntryRequestWsDTO.setTotalPrice(499.25);

		return orderEntryRequestWsDTO;
	}

	protected OrderRequestWsDTO createOrderRequestWsDTO_PickUpOrder(final List<OrderEntryRequestWsDTO> orderEntryRequestWsDTOS)
	{
		final OrderRequestWsDTO orderRequestWsDTO = buildCommonOrderRequestWsDTO();
		orderRequestWsDTO.setUser(buildUserWsDTO());
		orderRequestWsDTO.setDeliveryAddress(buildAddressWsDTO());
		orderRequestWsDTO.setPaymentAddress(buildAddressWsDTO());
		orderRequestWsDTO.setPaymentTransactions(Arrays.asList(buildPaymentTransactionWsDTO()));
		orderRequestWsDTO.setDeliveryModeCode("postService");
		orderRequestWsDTO.setEntries(orderEntryRequestWsDTOS);
		orderEntryRequestWsDTOS.stream().findFirst().get().setDeliveryPointOfService("myShop");
		return orderRequestWsDTO;
	}

	protected OrderRequestWsDTO createOrderRequestWsDTO(final List<OrderEntryRequestWsDTO> orderEntryRequestWsDTOS)
	{
		final OrderRequestWsDTO orderRequestWsDTO = buildCommonOrderRequestWsDTO();
		orderRequestWsDTO.setUser(buildUserWsDTO_WithExistingUSer());
		orderRequestWsDTO.setDeliveryAddress(buildAddressWsDTO());
		orderRequestWsDTO.setPaymentAddress(buildAddressWsDTO());
		orderRequestWsDTO.setDeliveryModeCode("postService");
		orderRequestWsDTO.setPaymentTransactions(Arrays.asList(buildPaymentTransactionWsDTO()));
		orderRequestWsDTO.setEntries(orderEntryRequestWsDTOS);
		return orderRequestWsDTO;
	}

	protected OrderRequestWsDTO buildCommonOrderRequestWsDTO()
	{
		final OrderRequestWsDTO orderRequestWsDTO = new OrderRequestWsDTO();
		orderRequestWsDTO.setExternalOrderCode("11146");
		orderRequestWsDTO.setCalculated(true);
		orderRequestWsDTO.setLanguageIsocode("EN");
		orderRequestWsDTO.setCurrencyIsocode("USD");
		orderRequestWsDTO.setDeliveryCost(11.99);
		orderRequestWsDTO.setStoreUid("electronics");
		orderRequestWsDTO.setSiteUid("electronics");
		orderRequestWsDTO.setSubtotal(499.25);
		orderRequestWsDTO.setTotalPrice(511.24);
		orderRequestWsDTO.setTotalTax(24.34);
		return orderRequestWsDTO;
	}

	protected UserWsDTO buildUserWsDTO()
	{
		UserWsDTO userWsDTO = new UserWsDTO();
		userWsDTO.setUid("cmccauleyhybrisAAA@gmail.com");
		userWsDTO.setFirstName("Sahil");
		userWsDTO.setLastName("Chaudhary");
		return userWsDTO;
	}

	protected UserWsDTO buildUserWsDTO_WithExistingUSer()
	{
		UserWsDTO userWsDTO = new UserWsDTO();
		userWsDTO.setUid("demo");
		return userWsDTO;
	}

	protected AddressWsDTO buildAddressWsDTO()
	{
		AddressWsDTO addressWsDTO = new AddressWsDTO();

		CountryWsDTO countryWsDTO = new CountryWsDTO();
		countryWsDTO.setIsocode("DE");
		countryWsDTO.setName("Deutschland");

		addressWsDTO.setCountry(countryWsDTO);
		addressWsDTO.setFirstName("SahilD");
		addressWsDTO.setLastName("ChaudharyD");
		addressWsDTO.setLine1("New york StreetD");
		addressWsDTO.setLine2("D");
		addressWsDTO.setTown("New York");
		addressWsDTO.setPostalCode("10001");

		RegionWsDTO regionWsDTO = new RegionWsDTO();
		regionWsDTO.setCountryIso("DE");
		regionWsDTO.setIsocode("DE-BW");
		regionWsDTO.setIsocodeShort("NY");
		regionWsDTO.setName("Baden-Württemberg");
		addressWsDTO.setRegion(regionWsDTO);
		return addressWsDTO;
	}

	protected PaymentTransactionWsDTO buildPaymentTransactionWsDTO()
	{
		// "paymentTransactions"
		PaymentTransactionWsDTO paymentTransactionWsDTO = new PaymentTransactionWsDTO();
		paymentTransactionWsDTO.setCode("a@test.com-3f052fda-8ed2-4fa5-8757-465b39d43acd");
		paymentTransactionWsDTO.setCurrencyIsocode("USD");

		// "paymentTransactions.entries"
		PaymentTransactionEntryWsDTO paymentTransactionEntryWsDTO = new PaymentTransactionEntryWsDTO();
		paymentTransactionEntryWsDTO.setAmount(new BigDecimal(511.24));
		paymentTransactionEntryWsDTO.setCode("a@test.com-32483423-a761-4631-abf4-8cf283717f33-AUTHORIZATION-1");
		paymentTransactionEntryWsDTO.setCurrencyIsocode("USD");
		paymentTransactionEntryWsDTO.setRequestId("mock");
		paymentTransactionEntryWsDTO.setRequestToken("1234567890");
		paymentTransactionEntryWsDTO.setSubscriptionID("42bdaa95-a400-4b95-947c-ac29fa7df29c");
		//paymentTransactionEntryWsDTO.setTime(new Date());
		paymentTransactionEntryWsDTO.setTransactionStatus("ACCEPTED");
		paymentTransactionEntryWsDTO.setTransactionStatusDetails("SUCCESFULL");
		paymentTransactionEntryWsDTO.setType("AUTHORIZATION");
		paymentTransactionWsDTO.setEntries(Arrays.asList(paymentTransactionEntryWsDTO));

		// "paymentTransactions.paymentInfo"
		PaymentDetailsWsDTO paymentDetailsWsDTO = new PaymentDetailsWsDTO();
		paymentDetailsWsDTO.setAccountHolderName("Sahil");
		CardTypeWsDTO cardTypeWsDTO = new CardTypeWsDTO();
		cardTypeWsDTO.setCode("VISA");
		paymentDetailsWsDTO.setCardType(cardTypeWsDTO);
		paymentDetailsWsDTO.setCardNumber("************1111");
		paymentDetailsWsDTO.setExpiryMonth("12");
		paymentDetailsWsDTO.setExpiryYear("2026");
		paymentDetailsWsDTO.setSubscriptionId("42bdaa95-a400-4b95-947c-ac29fa7df29c");
		paymentDetailsWsDTO.setSaved(true);
		paymentDetailsWsDTO.setDefaultPayment(true);
		//Using Ship as billing
		paymentDetailsWsDTO.setBillingAddress(buildAddressWsDTO());
		paymentTransactionWsDTO.setPaymentInfo(paymentDetailsWsDTO);

		paymentTransactionWsDTO.setPaymentProvider("Mockup");
		paymentTransactionWsDTO.setPlannedAmount(new BigDecimal(511.24));
		paymentTransactionWsDTO.setRequestId("Mock");
		paymentTransactionWsDTO.setRequestToken("1234567890");

		return paymentTransactionWsDTO;
	}
}

