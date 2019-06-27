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
 */
package de.hybris.platform.warehousingwebservices.warehousingwebservices;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.commercewebservicescommons.dto.order.ConsignmentWsDTO;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.oauth2.constants.OAuth2Constants;
import de.hybris.platform.ordermanagementwebservices.dto.payment.PaymentTransactionEntryWsDTO;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.dto.TransactionStatusDetails;
import de.hybris.platform.warehousing.util.models.Warehouses;
import de.hybris.platform.warehousingwebservices.constants.WarehousingwebservicesConstants;
import de.hybris.platform.warehousingwebservices.dto.order.ConsignmentCodesWsDTO;
import de.hybris.platform.warehousingwebservices.dto.order.ConsignmentEntrySearchPageWsDto;
import de.hybris.platform.warehousingwebservices.dto.order.ConsignmentReallocationWsDTO;
import de.hybris.platform.warehousingwebservices.dto.order.ConsignmentSearchPageWsDto;
import de.hybris.platform.warehousingwebservices.dto.order.ConsignmentStatusListWsDTO;
import de.hybris.platform.warehousingwebservices.dto.order.DeclineReasonListWsDTO;
import de.hybris.platform.warehousingwebservices.dto.order.PackagingInfoWsDTO;
import de.hybris.platform.warehousingwebservices.dto.store.WarehouseSearchPageWsDto;
import de.hybris.platform.warehousingwebservices.warehousingwebservices.util.BaseWarehousingWebservicesIntegrationTest;
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;

import static de.hybris.platform.webservicescommons.testsupport.client.WebservicesAssert.assertResponse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


@NeedsEmbeddedServer(webExtensions = { WarehousingwebservicesConstants.EXTENSIONNAME, OAuth2Constants.EXTENSIONNAME })
@IntegrationTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class ConsignmentsControllerIntegrationTest extends BaseWarehousingWebservicesIntegrationTest
{
	protected static final String CONSIGNMENT_0 = "con_0";
	protected static final String CONSIGNMENT_1 = "con_1";

	private OrderModel shippedOrder;

	@Before
	public void setup()
	{
		super.setup();
		shippedOrder = createShippedConsignmentAndOrder();
	}

	public void cleanUp()
	{
		cleanUpData();
	}

	@Test
	public void getAllConsignments()
	{
		//when
		final ConsignmentSearchPageWsDto result = getAllConsignmentsByDefault();
		//then
		assertEquals(2, result.getConsignments().size());
		assertEquals(CONSIGNMENT_0, result.getConsignments().get(0).getCode());
		assertEquals(CONSIGNMENT_1, result.getConsignments().get(1).getCode());
	}

	@Test
	public void getConsignmentForCode()
	{
		//When
		final ConsignmentWsDTO result = getConsignmentsForCodeByDefault(CONSIGNMENT_0);
		//then
		assertEquals(CONSIGNMENT_0, result.getCode());
	}

	@Test
	public void getConsignmentStatus()
	{
		//When
		final ConsignmentStatusListWsDTO result = getConsignmentStatusByDefault();
		final List<ConsignmentStatus> consignmentStatusList = getEnumerationService()
				.getEnumerationValues(ConsignmentStatus._TYPECODE);

		//then
		assertEquals(14, result.getStatuses().size());
		assertTrue(consignmentStatusList.stream()
				.anyMatch(consignmentStatus -> result.getStatuses().contains(consignmentStatus.getCode())));
	}

	@Test
	public void getDeclineReasons()
	{
		//When
		final DeclineReasonListWsDTO result = getDeclineReasonsByDefault();
		//then
		assertEquals(6, result.getReasons().size());
	}

	@Test
	public void getConsignmentEntries()
	{
		//When
		final ConsignmentEntrySearchPageWsDto result = getConsignmentEntriesByDefault(CONSIGNMENT_0);
		//then
		assertEquals(1, result.getConsignmentEntries().size());
	}

	@Test
	public void getSourcingLocations()
	{
		//When
		final WarehouseSearchPageWsDto result = getSourcingLocationsByDefault(CONSIGNMENT_0);
		//then
		assertEquals(1, result.getWarehouses().size());
		assertEquals(Warehouses.CODE_BOSTON, result.getWarehouses().get(0).getCode());
	}

	@Test
	public void getPackagingInfo()
	{
		//When
		final PackagingInfoWsDTO result = getPackagingInfoByDefault(CONSIGNMENT_0);
		//then
		assertEquals("0", result.getHeight());
		assertEquals("0", result.getInsuredValue());
		assertEquals("0", result.getLength());
		assertEquals("0", result.getWidth());
		assertEquals("0", result.getGrossWeight());
		assertEquals("kg", result.getWeightUnit());
		assertEquals("cm", result.getDimensionUnit());
	}

	@Test
	public void updatePackagingInfo()
	{
		//When
		final ConsignmentWsDTO result = updatePackagingInfoByDefault(CONSIGNMENT_0,
				createPackagingInfo("1", "2", "3", "4", "5", "in", "lb"));
		//then
		assertEquals("1", result.getPackagingInfo().getWidth());
		assertEquals("2", result.getPackagingInfo().getHeight());
		assertEquals("3", result.getPackagingInfo().getLength());
		assertEquals("4", result.getPackagingInfo().getGrossWeight());
		assertEquals("5", result.getPackagingInfo().getInsuredValue());
		assertEquals("in", result.getPackagingInfo().getDimensionUnit());
		assertEquals("lb", result.getPackagingInfo().getWeightUnit());
	}

	@Test
	public void reallocateConsignment()
	{
		//Given
		setOrderAndConsignmentStatusToReady();
		final ConsignmentReallocationWsDTO consignmentReallocationWsDTO = createConsignmentReallocationWsDTO();

		//When
		final Response result = postDefaultRestCall("consignments/con_0/reallocate", DEFAULT_FIELDS, consignmentReallocationWsDTO);

		//then
		assertResponse(Status.OK, Optional.empty(), result);
	}

	@Test
	public void pickConsignmentDefault() throws ImpExException
	{
		//Given
		importEmailContentCsv();
		setOrderAndConsignmentStatusToReady();

		//When
		final Response result = postPickConsignmentDefault(CONSIGNMENT_0);

		//Then
		assertResponse(Status.OK, Optional.empty(), result);
	}

	public void pickConsignmentWhenPrintSlipIsFalse() throws ImpExException
	{
		//Given
		importEmailContentCsv();
		setOrderAndConsignmentStatusToReady();

		//When
		final Response result = postPickConsignmentWithPrintSlip(CONSIGNMENT_0, "false");

		//Then
		assertResponse(Status.OK, Optional.empty(), result);
	}

	@Test
	public void packConsignmentDefault() throws ImpExException
	{
		//Given
		importEmailContentCsv();
		setOrderAndConsignmentStatusToReady();

		//When
		final Response result = postPackConsignmentDefault(CONSIGNMENT_0);

		//Then
		assertResponse(Status.OK, Optional.empty(), result);
	}

	public void packConsignmentWhenPrintSlipIsFalse() throws ImpExException
	{
		//Given
		importEmailContentCsv();
		setOrderAndConsignmentStatusToReady();

		//When
		final Response result = postPackConsignmentWithPrintSlip(CONSIGNMENT_0, "false");

		//Then
		assertResponse(Status.OK, Optional.empty(), result);
	}

	@Test
	public void pickShippedConsignmentDefault() throws ImpExException
	{
		//Given
		importEmailContentCsv();

		//When
		final Response result = postPickConsignmentDefault(CONSIGNMENT_0);

		//Then
		assertResponse(Status.OK, Optional.empty(), result);
	}

	@Test
	public void pickShippedConsignmentWhenPrintSlipIsTrue() throws ImpExException
	{
		//Given
		importEmailContentCsv();

		//When
		final Response result = postPickConsignmentWithPrintSlip(CONSIGNMENT_0, "true");

		//Then
		assertResponse(Status.OK, Optional.empty(), result);
	}

	@Test
	public void pickShippedConsignmentWhenPrintSlipIsFalse() throws ImpExException
	{
		//Given
		importEmailContentCsv();

		//When
		final Response result = postPickConsignmentWithPrintSlip(CONSIGNMENT_0, "false");

		//Then
		assertResponse(Status.BAD_REQUEST, Optional.empty(), result);
	}

	@Test
	public void packShippedConsignmentDefault() throws ImpExException
	{
		//Given
		importEmailContentCsv();

		//When
		final Response result = postPackConsignmentDefault(CONSIGNMENT_0);

		//Then
		assertResponse(Status.OK, Optional.empty(), result);
	}

	@Test
	public void packShippedConsignmentWhenPrintSlipIsTrue() throws ImpExException
	{
		//Given
		importEmailContentCsv();

		//When
		final Response result = postPackConsignmentWithPrintSlip(CONSIGNMENT_0, "true");

		//Then
		assertResponse(Status.OK, Optional.empty(), result);
	}

	@Test
	public void packShippedConsignmentWhenPrintSlipIsFalse() throws ImpExException
	{
		//Given
		importEmailContentCsv();

		//When
		final Response result = postPackConsignmentWithPrintSlip(CONSIGNMENT_0, "false");

		//Then
		assertResponse(Status.BAD_REQUEST, Optional.empty(), result);
	}

	@Test
	public void getExportForm() throws ImpExException
	{
		//Given
		importEmailContentCsv();

		//When
		final Response result = getEmptyRestCall("consignments/con_0/export-form");

		//Then
		assertResponse(Status.OK, Optional.empty(), result);
	}

	@Test
	public void getShippingLabel() throws ImpExException
	{
		//Given
		importEmailContentCsv();

		//When
		final Response result = getEmptyRestCall("consignments/con_0/shipping-label");

		//Then
		assertResponse(Status.OK, Optional.empty(), result);
	}

	@Test
	public void getReturnShippingLabel() throws ImpExException
	{
		//Given
		importEmailContentCsv();

		//When
		final Response result = getEmptyRestCall("consignments/con_0/return-shipping-label");

		//Then
		assertResponse(Status.OK, Optional.empty(), result);
	}

	@Test
	public void getReturnForm() throws ImpExException
	{
		//Given
		importEmailContentCsv();

		//When
		final Response result = getEmptyRestCall("consignments/con_0/return-form");

		//Then
		assertResponse(Status.OK, Optional.empty(), result);
	}

	@Test
	public void consolidatedPickSlip() throws ImpExException
	{
		//Given
		importEmailContentCsv();
		final ConsignmentCodesWsDTO consignmentCodes = new ConsignmentCodesWsDTO();
		consignmentCodes.setCodes(Arrays.asList(CONSIGNMENT_0, CONSIGNMENT_1));

		//When
		final Response result = postBodyRestCall("consignments/consolidated-pick", consignmentCodes);

		//Then
		assertResponse(Status.OK, Optional.empty(), result);
	}

	@Test
	public void postTakePayment()
	{
		//Given
		setOrderAndConsignmentStatusToReady();

		//When
		final PaymentTransactionEntryWsDTO result = postTakePaymentRestCall(CONSIGNMENT_0);

		//Then
		assertEquals(TransactionStatus.ACCEPTED.name(), result.getTransactionStatus());
		assertEquals(TransactionStatusDetails.SUCCESFULL.name(), result.getTransactionStatusDetails());
	}

	@Test
	public void manuallyReleasePaymentCaptureConsignment()
	{
		//Given
		setOrderAndConsignmentStatus(OrderStatus.PAYMENT_NOT_CAPTURED, ConsignmentStatus.PAYMENT_NOT_CAPTURED);

		//When
		final Response result = manuallyReleasePaymentCaptureRestCall(CONSIGNMENT_0);

		//Then
		assertResponse(Status.OK, Optional.empty(), result);
	}

	@Test
	public void manuallyReleasePaymentCaptureConsignmentWithInvalidCode()
	{
		//Given
		final String consignmentCode = "InvalidCode";

		//When
		final Response result = manuallyReleasePaymentCaptureRestCall(consignmentCode);

		//Then
		assertResponse(Status.BAD_REQUEST, Optional.empty(), result);
	}

	@Test
	public void manuallyReleasePaymentCaptureConsignmentWithInvalidStatus()
	{
		//When
		final Response result = manuallyReleasePaymentCaptureRestCall(CONSIGNMENT_0);

		//Then
		assertResponse(Status.BAD_REQUEST, Optional.empty(), result);
	}

	@Test
	public void manuallyReleaseTaxCommitConsignment()
	{
		//Given
		setOrderAndConsignmentStatus(OrderStatus.READY, ConsignmentStatus.TAX_NOT_COMMITTED);

		//When
		final Response result = manuallyReleaseTaxCommitRestCall(CONSIGNMENT_0);

		//Then
		assertResponse(Status.OK, Optional.empty(), result);
	}

	@Test
	public void manuallyReleaseTaxCommitConsignmentWithInvalidCode()
	{
		//Given
		final String consignmentCode = "InvalidCode";

		//When
		final Response result = manuallyReleaseTaxCommitRestCall(consignmentCode);

		//Then
		assertResponse(Status.BAD_REQUEST, Optional.empty(), result);
	}

	@Test
	public void manuallyReleaseTaxCommitConsignmentWithInvalidStatus()
	{
		//When
		final Response result = manuallyReleasePaymentCaptureRestCall(CONSIGNMENT_0);

		//Then
		assertResponse(Status.BAD_REQUEST, Optional.empty(), result);
	}

	/**
	 * Imports email content
	 */
	protected void importEmailContentCsv() throws ImpExException
	{
		importCsv("/warehousingwebservices/test/impex/email-content.impex", "utf-8");
		importCsv("/warehousingwebservices/test/impex/email-content_en.impex", "utf-8");
	}

	/**
	 * Calls setOrderandConsignmentStatus to set the status of the shipped {@link OrderModel}
	 * and all the {@link de.hybris.platform.ordersplitting.model.ConsignmentModel} to Ready
	 */
	protected void setOrderAndConsignmentStatusToReady()
	{
		setOrderAndConsignmentStatus(OrderStatus.READY, ConsignmentStatus.READY);
	}

	/**
	 * Sets the status of the {@link OrderModel} and all the {@link de.hybris.platform.ordersplitting.model.ConsignmentModel}
	 *
	 * @param orderStatus
	 * 		The {@link OrderStatus} to set for the {@link OrderModel}
	 * @param consignmentStatus
	 * 		The {@link ConsignmentStatus} to set for all the {@link de.hybris.platform.ordersplitting.model.ConsignmentModel}
	 */
	protected void setOrderAndConsignmentStatus(final OrderStatus orderStatus, final ConsignmentStatus consignmentStatus)
	{
		shippedOrder.setStatus(orderStatus);
		shippedOrder.getConsignments().forEach(consignment -> {
			consignment.setStatus(consignmentStatus);
			getModelService().save(consignment);
		});
		getModelService().save(shippedOrder);
	}
}
