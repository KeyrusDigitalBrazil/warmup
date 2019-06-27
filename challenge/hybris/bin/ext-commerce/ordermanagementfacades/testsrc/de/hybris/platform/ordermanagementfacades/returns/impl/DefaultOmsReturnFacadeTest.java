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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.enums.ReturnStatus;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.ordermanagementfacades.returns.data.CancelReturnRequestData;
import de.hybris.platform.ordermanagementfacades.returns.data.ReturnEntryData;
import de.hybris.platform.ordermanagementfacades.returns.data.ReturnEntryModificationData;
import de.hybris.platform.ordermanagementfacades.returns.data.ReturnRequestData;
import de.hybris.platform.ordermanagementfacades.returns.data.ReturnRequestModificationData;
import de.hybris.platform.returns.OrderReturnException;
import de.hybris.platform.returns.ReturnCallbackService;
import de.hybris.platform.returns.ReturnService;
import de.hybris.platform.returns.dao.ReturnRequestDao;
import de.hybris.platform.returns.model.RefundEntryModel;
import de.hybris.platform.returns.model.ReturnEntryModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.util.DiscountValue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultOmsReturnFacadeTest
{
	@InjectMocks
	private DefaultOmsReturnFacade omsReturnFacade;
	@Mock
	private CancelReturnRequestData cancelReturnRequestData;
	@Mock
	private ReturnEntryModificationData returnEntryModificationData;
	@Mock
	private ReturnEntryModificationData returnEntryModificationData2;
	@Mock
	private ReturnRequestModificationData returnRequestModificationData;
	@Mock
	private GenericDao<ReturnRequestModel> returnGenericDao;
	@Mock
	private List<ReturnEntryModel> returnEntryModelList;
	@Mock
	private ReturnCallbackService returnCallbackService;
	@Mock
	private ReturnEntryModel returnEntryModel;
	@Mock
	private ReturnRequestModel returnRequestModel;
	@Mock
	private ReturnService returnService;
	@Mock
	private OrderModel orderModel;
	@Mock
	private Converter<ReturnRequestModel, ReturnRequestData> returnConverter;
	@Mock
	private ReturnRequestData returnRequestData;
	@Mock
	private ModelService modelService;
	@Mock
	private RefundEntryModel refundEntryModel;
	@Mock
	private RefundEntryModel refundEntryModel2;
	@Mock
	private ReturnRequestModel previousReturn;
	@Mock
	private ReturnRequestDao returnRequestDao;
	@Mock
	private OrderEntryModel orderEntryModel;
	@Mock
	private OrderEntryModel orderEntryModel2;
	@Mock
	private ProductModel productModel;
	@Mock
	private DeliveryModeModel deliveryModeModel;
	@Mock
	private DeliveryModeModel deliveryModeModel2;
	@Mock
	private ReturnEntryData returnEntryData;
	@Mock
	private ReturnEntryData returnEntryData2;
	@Mock
	private OrderEntryData orderEntryData;
	@Mock
	private OrderEntryData orderEntryData2;

	private static final String ORDER_ID = "111";
	private static final String RETURNREQUEST_RMA = "1112";
	private static final String RETURN_REQUEST1 = "RETURN_REQUEST1";
	private static final String RETURN_REQUEST2 = "RETURN_REQUEST2";
	private static final String ORDER_CODE = "ORDER_CODE1";
	private static final String PRODUCT_CODE = "PRODUCT1";
	private static final String PRODUCT_CODE2 = "PRODUCT2";
	private static final String DELIVERY_MODECODE = "MODE1";
	private static final String DELIVERY_MODECODE2 = "MODE2";
	final List<ReturnEntryModificationData> returnEntryModificationDataList = new ArrayList<>();
	final List<ReturnRequestModel> returnRequestModelList = new ArrayList<>();
	final Set<ReturnStatus> invalidReturnStatusForRefundDeliveryCost = new HashSet<>();

	@Before
	public void setUp()
	{
		final List<ReturnRequestModel> returnResultSet = Arrays.asList(returnRequestModel);
		omsReturnFacade.setReturnGenericDao(returnGenericDao);
		omsReturnFacade.setReturnConverter(returnConverter);
		doNothing().when(modelService).save(any());
		when(returnConverter.convert(returnRequestModel)).thenReturn(returnRequestData);
		when(returnGenericDao.find(anyMap())).thenReturn(returnResultSet);
		when(orderModel.getCode()).thenReturn(ORDER_ID);
		invalidReturnStatusForRefundDeliveryCost.add(ReturnStatus.CANCELED);
		omsReturnFacade.setInvalidReturnStatusForRefundDeliveryCost(invalidReturnStatusForRefundDeliveryCost);

		when(returnRequestModificationData.getReturnEntries()).thenReturn(null);
		when(returnRequestModificationData.getRefundDeliveryCost()).thenReturn(true);

		when(returnEntryModificationData.getProductCode()).thenReturn(PRODUCT_CODE);

		returnEntryModificationDataList.add(returnEntryModificationData);
		returnEntryModificationDataList.add(returnEntryModificationData2);
		when(returnEntryModificationData.getProductCode()).thenReturn(PRODUCT_CODE);
		when(returnEntryModificationData2.getProductCode()).thenReturn(PRODUCT_CODE2);
		when(returnEntryModificationData.getDeliveryModeCode()).thenReturn(null);
		when(returnEntryModificationData2.getDeliveryModeCode()).thenReturn(null);

		when(returnRequestModel.getStatus()).thenReturn(ReturnStatus.APPROVAL_PENDING);
		when(returnRequestModel.getOrder()).thenReturn(orderModel);
		when(returnRequestModel.getRMA()).thenReturn(RETURN_REQUEST1);

		returnRequestModelList.add(previousReturn);

		when(previousReturn.getStatus()).thenReturn(ReturnStatus.COMPLETED);
		when(previousReturn.getRMA()).thenReturn(RETURN_REQUEST1);
		when(previousReturn.getRefundDeliveryCost()).thenReturn(false);

		when(returnService.getReturnRequests(ORDER_ID)).thenReturn(returnRequestModelList);

		when(refundEntryModel.getOrderEntry()).thenReturn(orderEntryModel);

		when(orderEntryModel.getProduct()).thenReturn(productModel);
		when(orderEntryModel.getDeliveryMode()).thenReturn(deliveryModeModel);
		when(productModel.getCode()).thenReturn(PRODUCT_CODE);
		when(deliveryModeModel.getCode()).thenReturn(DELIVERY_MODECODE);

		// Prepare the Complete Return
		when(returnRequestData.getReturnEntries()).thenReturn(Arrays.asList(returnEntryData, returnEntryData2));
		when(orderModel.getEntries()).thenReturn(Arrays.asList(orderEntryModel, orderEntryModel2));

		// Entry 0
		when(returnEntryData.getExpectedQuantity()).thenReturn(1L);
		when(returnEntryData.getOrderEntry()).thenReturn(orderEntryData);
		when(orderEntryData.getEntryNumber()).thenReturn(0);

		when(orderEntryModel.getQuantity()).thenReturn(1L);
		when(orderEntryModel.getEntryNumber()).thenReturn(0);

		// Entry 1
		when(returnEntryData2.getExpectedQuantity()).thenReturn(2L);
		when(returnEntryData2.getOrderEntry()).thenReturn(orderEntryData2);
		when(orderEntryData2.getEntryNumber()).thenReturn(1);

		when(orderEntryModel2.getQuantity()).thenReturn(2L);
		when(orderEntryModel2.getEntryNumber()).thenReturn(1);
	}

	@Test
	public void shouldReversePayment() throws OrderReturnException
	{
		//Given
		when(returnRequestModel.getStatus()).thenReturn(ReturnStatus.PAYMENT_REVERSAL_FAILED);
		when(returnGenericDao.find(anyMap())).thenReturn(Arrays.asList(returnRequestModel));

		//When
		omsReturnFacade.requestManualPaymentReversalForReturnRequest(anyString());

		//Then
		verify(returnService).requestManualPaymentReversalForReturnRequest(any(ReturnRequestModel.class));
	}

	@Test(expected = IllegalStateException.class)
	public void shouldNotReversePaymentWrongStatus() throws OrderReturnException
	{
		//Given
		when(returnRequestModel.getStatus()).thenReturn(ReturnStatus.COMPLETED);
		when(returnGenericDao.find(anyMap())).thenReturn(Arrays.asList(returnRequestModel));

		//When
		omsReturnFacade.requestManualPaymentReversalForReturnRequest(anyString());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotReversePaymentNullCode() throws OrderReturnException
	{
		//When
		omsReturnFacade.requestManualPaymentReversalForReturnRequest(null);
	}

	@Test
	public void shouldReverseTax() throws OrderReturnException
	{
		//Given
		when(returnRequestModel.getStatus()).thenReturn(ReturnStatus.TAX_REVERSAL_FAILED);
		when(returnGenericDao.find(anyMap())).thenReturn(Arrays.asList(returnRequestModel));

		//When
		omsReturnFacade.requestManualTaxReversalForReturnRequest(anyString());

		//Then
		verify(returnService).requestManualTaxReversalForReturnRequest(any(ReturnRequestModel.class));
	}

	@Test(expected = IllegalStateException.class)
	public void shouldNotReverseTaxWrongStatus() throws OrderReturnException
	{
		//Given
		when(returnRequestModel.getStatus()).thenReturn(ReturnStatus.COMPLETED);
		when(returnGenericDao.find(anyMap())).thenReturn(Arrays.asList(returnRequestModel));

		//When
		omsReturnFacade.requestManualTaxReversalForReturnRequest(anyString());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotReverseTaxNullCode() throws OrderReturnException
	{
		//When
		omsReturnFacade.requestManualTaxReversalForReturnRequest(null);
	}

	@Test
	public void shouldExecuteOnReturnCancelResponse() throws OrderReturnException
	{
		//Given
		omsReturnFacade.setReturnGenericDao(returnGenericDao);
		final List<ReturnRequestModel> returnResultSet = Arrays.asList(returnRequestModel);
		when(returnGenericDao.find(anyMap())).thenReturn(returnResultSet);
		returnEntryModelList = Arrays.asList(returnEntryModel);
		when(returnRequestModel.getStatus()).thenReturn(ReturnStatus.APPROVAL_PENDING);
		when(returnRequestModel.getReturnEntries()).thenReturn(returnEntryModelList);

		//When
		omsReturnFacade.cancelReturnRequest(cancelReturnRequestData);

		//Then
		verify(returnCallbackService).onReturnCancelResponse(any());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldUpdateReturnRequestFail_returnRequestModificationDataNull()
	{
		omsReturnFacade.updateReturnRequest(ORDER_ID, null);
	}

	@Test(expected = IllegalStateException.class)
	public void shouldUpdateReturnRequestFail_DeliverCostFalse()
	{
		//When
		when(previousReturn.getRefundDeliveryCost()).thenReturn(true);
		when(returnRequestModel.getRMA()).thenReturn(RETURN_REQUEST2);
		//Then
		omsReturnFacade.updateReturnRequest(RETURNREQUEST_RMA, returnRequestModificationData);
	}

	@Test
	public void shouldUpdateReturnRequestSuccess_RefundDeliverCostPossible_PreviousDeliverCostNull()
	{
		//given
		when(previousReturn.getRefundDeliveryCost()).thenReturn(null);
		//when
		omsReturnFacade.updateReturnRequest(RETURNREQUEST_RMA, returnRequestModificationData);
		//then
		verify(modelService, atLeast(1)).save(returnRequestModel);
		verify(returnConverter, times(1)).convert(returnRequestModel);

	}

	@Test
	public void shouldUpdateReturnRequestSuccess_RefundDeliverCostPossible_PreviousDeliverCostTrueButCancelled()
	{
		//give
		when(previousReturn.getRefundDeliveryCost()).thenReturn(true);
		when(previousReturn.getStatus()).thenReturn(ReturnStatus.CANCELED);
		//when
		omsReturnFacade.updateReturnRequest(RETURNREQUEST_RMA, returnRequestModificationData);
		//then
		verify(modelService, atLeast(1)).save(returnRequestModel);
		verify(returnConverter, times(1)).convert(returnRequestModel);
	}

	@Test
	public void shouldUpdateReturnRequestSuccess_RefundDeliverCostPossible_WithoutPreviousReturnRequest()
	{
		//given
		when(returnService.getReturnRequests(ORDER_ID)).thenReturn(new ArrayList<ReturnRequestModel>());
		//when
		omsReturnFacade.updateReturnRequest(RETURNREQUEST_RMA, returnRequestModificationData);
		//then
		verify(modelService, atLeast(1)).save(returnRequestModel);
		verify(returnConverter, times(1)).convert(returnRequestModel);
	}

	@Test
	public void shouldUpdateReturnRequestSuccess_SetDeliverCostFalse()
	{
		when(returnRequestModificationData.getRefundDeliveryCost()).thenReturn(false);
		omsReturnFacade.updateReturnRequest(RETURNREQUEST_RMA, returnRequestModificationData);
		verify(modelService, atLeast(1)).save(returnRequestModel);
		verify(returnConverter, times(1)).convert(returnRequestModel);
	}

	@Test
	public void shouldUpdateReturnRequestSuccess_DeliverCostTrue()
	{
		omsReturnFacade.updateReturnRequest(RETURNREQUEST_RMA, returnRequestModificationData);
		verify(modelService, atLeast(1)).save(returnRequestModel);
		verify(returnConverter, times(1)).convert(returnRequestModel);
	}

	@Test
	public void shouldUpdateReturnRequestSuccess_DeliverCostNull()
	{
		when(returnRequestModificationData.getRefundDeliveryCost()).thenReturn(null);
		omsReturnFacade.updateReturnRequest(RETURNREQUEST_RMA, returnRequestModificationData);
		verify(modelService, times(0)).save(returnRequestModel);
		verify(returnConverter, times(1)).convert(returnRequestModel);
	}

	@Test
	public void shouldUpdateReturnRequestSuccess_CustomAmount()
	{
		returnEntryModificationDataList.remove(1);
		when(returnRequestModificationData.getReturnEntries()).thenReturn(returnEntryModificationDataList);
		returnEntryModelList = Arrays.asList(refundEntryModel);
		when(returnRequestModel.getReturnEntries()).thenReturn(returnEntryModelList);
		when(refundEntryModel.getAmount()).thenReturn(new BigDecimal("100.12"));
		omsReturnFacade.updateReturnRequest(RETURNREQUEST_RMA, returnRequestModificationData);
		verify(returnRequestModel, times(1)).setSubtotal(new BigDecimal("100.12"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldUpdateReturnRequestFail_DuplicatedEntry()
	{
		when(returnEntryModificationData2.getProductCode()).thenReturn(PRODUCT_CODE);
		when(returnRequestModificationData.getReturnEntries()).thenReturn(returnEntryModificationDataList);
		returnEntryModelList = Arrays.asList(refundEntryModel);
		when(returnRequestModel.getReturnEntries()).thenReturn(returnEntryModelList);
		when(refundEntryModel.getAmount()).thenReturn(new BigDecimal("100.12"));
		omsReturnFacade.updateReturnRequest(RETURNREQUEST_RMA, returnRequestModificationData);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldUpdateReturnRequestFail_EntryDuplicated_DeliveryModeNull()
	{
		//given
		when(returnEntryModificationData2.getProductCode()).thenReturn(PRODUCT_CODE);
		when(returnRequestModificationData.getReturnEntries()).thenReturn(returnEntryModificationDataList);
		when(returnEntryModificationData.getDeliveryModeCode()).thenReturn(DELIVERY_MODECODE);
		when(returnEntryModificationData2.getDeliveryModeCode()).thenReturn(null);
		returnEntryModelList = Arrays.asList(refundEntryModel);
		when(returnRequestModel.getReturnEntries()).thenReturn(returnEntryModelList);
		when(refundEntryModel.getAmount()).thenReturn(new BigDecimal("100.12"));
		omsReturnFacade.updateReturnRequest(RETURNREQUEST_RMA, returnRequestModificationData);
		verify(returnRequestModel, times(1)).setSubtotal(new BigDecimal("100.12"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldUpdateReturnRequestFail_EntryDataDuplicated()
	{
		when(returnEntryModificationData2.getProductCode()).thenReturn(PRODUCT_CODE);
		when(returnRequestModificationData.getReturnEntries()).thenReturn(returnEntryModificationDataList);
		returnEntryModelList = Arrays.asList(refundEntryModel);
		when(returnRequestModel.getReturnEntries()).thenReturn(returnEntryModelList);
		when(refundEntryModel.getAmount()).thenReturn(new BigDecimal("100.12"));
		omsReturnFacade.updateReturnRequest(RETURNREQUEST_RMA, returnRequestModificationData);
		verify(returnRequestModel, times(1)).setSubtotal(new BigDecimal("100.12"));
	}

	@Test
	public void shouldUpdateReturnRequestSuccess_EntryDataProductDuplicated_NotDeliveryCode()
	{
		when(returnEntryModificationData2.getProductCode()).thenReturn(PRODUCT_CODE);
		when(deliveryModeModel.getCode()).thenReturn(DELIVERY_MODECODE).thenReturn(DELIVERY_MODECODE2);
		when(returnEntryModificationData.getDeliveryModeCode()).thenReturn(DELIVERY_MODECODE);
		when(returnEntryModificationData2.getDeliveryModeCode()).thenReturn(DELIVERY_MODECODE2);
		when(returnRequestModificationData.getReturnEntries()).thenReturn(returnEntryModificationDataList);
		returnEntryModelList = Arrays.asList(refundEntryModel);
		when(returnRequestModel.getReturnEntries()).thenReturn(returnEntryModelList);
		when(refundEntryModel.getAmount()).thenReturn(new BigDecimal("100.12"));
		omsReturnFacade.updateReturnRequest(RETURNREQUEST_RMA, returnRequestModificationData);
		verify(returnRequestModel, times(1)).setSubtotal(new BigDecimal("100.12"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldUpdateReturnRequestFail_EntryDataProductDuplicated_OneDeliveryCodeNull()
	{
		when(returnEntryModificationData2.getProductCode()).thenReturn(PRODUCT_CODE);
		when(deliveryModeModel.getCode()).thenReturn(DELIVERY_MODECODE).thenReturn(DELIVERY_MODECODE2);
		when(returnEntryModificationData.getDeliveryModeCode()).thenReturn(DELIVERY_MODECODE);
		when(returnEntryModificationData2.getDeliveryModeCode()).thenReturn(null);
		when(returnRequestModificationData.getReturnEntries()).thenReturn(returnEntryModificationDataList);
		returnEntryModelList = Arrays.asList(refundEntryModel);
		when(returnRequestModel.getReturnEntries()).thenReturn(returnEntryModelList);
		when(refundEntryModel.getAmount()).thenReturn(new BigDecimal("100.12"));
		omsReturnFacade.updateReturnRequest(RETURNREQUEST_RMA, returnRequestModificationData);
		verify(returnRequestModel, times(1)).setSubtotal(new BigDecimal("100.12"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldUpdateReturnRequestFail_CannotFindEntryModel()
	{
		when(returnEntryModificationData2.getProductCode()).thenReturn("11111");
		when(deliveryModeModel.getCode()).thenReturn(DELIVERY_MODECODE).thenReturn(PRODUCT_CODE);
		when(returnRequestModificationData.getReturnEntries()).thenReturn(returnEntryModificationDataList);
		returnEntryModelList = Arrays.asList(refundEntryModel);
		when(returnRequestModel.getReturnEntries()).thenReturn(returnEntryModelList);
		when(refundEntryModel.getAmount()).thenReturn(new BigDecimal("100.12"));
		omsReturnFacade.updateReturnRequest(RETURNREQUEST_RMA, returnRequestModificationData);
		verify(returnRequestModel, times(1)).setSubtotal(new BigDecimal("100.12"));
	}

	@Test(expected = AmbiguousIdentifierException.class)
	public void shouldUpdateReturnRequestFail_FindDuplicatedEntryModel()
	{
		when(returnRequestModificationData.getReturnEntries()).thenReturn(returnEntryModificationDataList);
		returnEntryModelList = Arrays.asList(refundEntryModel, refundEntryModel);
		when(returnRequestModel.getReturnEntries()).thenReturn(returnEntryModelList);
		when(refundEntryModel.getAmount()).thenReturn(new BigDecimal("100.12"));
		omsReturnFacade.updateReturnRequest(RETURNREQUEST_RMA, returnRequestModificationData);
		verify(returnRequestModel, times(1)).setSubtotal(new BigDecimal("100.12"));
	}

	@Test
	public void shouldUpdateReturnRequestFail_InvalidDeliveryModeCode()
	{
		returnEntryModificationDataList.remove(1);
		when(returnRequestModificationData.getReturnEntries()).thenReturn(returnEntryModificationDataList);
		returnEntryModelList = Arrays.asList(refundEntryModel);
		when(returnEntryModificationData2.getDeliveryModeCode()).thenReturn("INVALID");
		when(returnRequestModel.getReturnEntries()).thenReturn(returnEntryModelList);
		when(refundEntryModel.getAmount()).thenReturn(new BigDecimal("100.12"));
		omsReturnFacade.updateReturnRequest(RETURNREQUEST_RMA, returnRequestModificationData);
		verify(returnRequestModel, times(1)).setSubtotal(new BigDecimal("100.12"));
	}

	@Test
	public void isCompleteReturn()
	{
		assertTrue(omsReturnFacade.isCompleteReturn(orderModel, returnRequestData));
	}

	@Test
	public void isPartial_partialReturn_entryQty()
	{
		when(orderModel.getEntries()).thenReturn(Arrays.asList(orderEntryModel)); // One less entry.
		assertFalse(omsReturnFacade.isCompleteReturn(orderModel, returnRequestData));
	}

	@Test
	public void isPartial_partialReturn_productQty()
	{
		when(returnEntryData2.getExpectedQuantity()).thenReturn(1L);
		assertFalse(omsReturnFacade.isCompleteReturn(orderModel, returnRequestData));
	}

	@Test
	public void totalPriceUsed_refundEntryAmount_completeReturn()
	{
		when(orderEntryModel.getTotalPrice()).thenReturn(10D);
		final BigDecimal amount = omsReturnFacade.calculateRefundEntryAmount(orderEntryModel, 5L, true);

		assertEquals(BigDecimal.valueOf(10.0), amount);
	}

	@Test
	public void zeroSet_refundEntryAmount_partialReturnDiscounts()
	{
		when(orderEntryModel.getDiscountValues()).thenReturn(Arrays.asList(new DiscountValue("", 0D, false, "")));
		final BigDecimal amount = omsReturnFacade.calculateRefundEntryAmount(orderEntryModel, 1L, false);

		assertEquals(BigDecimal.ZERO, amount);
	}

	@Test
	public void basePriceMultiplied_refundEntryAmount_partialReturnNoDiscounts()
	{
		when(orderEntryModel.getDiscountValues()).thenReturn(Collections.emptyList());
		when(orderEntryModel.getBasePrice()).thenReturn(2.50);
		final BigDecimal amount = omsReturnFacade.calculateRefundEntryAmount(orderEntryModel, 5L, false);
		assertEquals(new BigDecimal(2.50 * 5).setScale(1, BigDecimal.ROUND_DOWN), amount);
	}

	@Test
	public void recalculateSubtotal_completeReturn()
	{
		when(orderModel.getSubtotal()).thenReturn(10D);
		final BigDecimal amount = omsReturnFacade.recalculateSubtotal(returnRequestModel, true);

		assertEquals(BigDecimal.valueOf(10.0), amount);
	}

	@Test
	public void recalculateSubtotal_partialReturn()
	{
		when(returnRequestModel.getReturnEntries())
				.thenReturn(Arrays.asList(refundEntryModel, refundEntryModel2, returnEntryModel));
		when(refundEntryModel.getAmount()).thenReturn(new BigDecimal(5));
		when(refundEntryModel2.getAmount()).thenReturn(new BigDecimal(5));
		final BigDecimal amount = omsReturnFacade.recalculateSubtotal(returnRequestModel, false);

		assertEquals(amount, BigDecimal.TEN);
	}

	@Test
	public void canRefundDeliveryCost_NoReturnRequests()
	{
		when(returnService.getReturnRequests(ORDER_ID)).thenReturn(Collections.emptyList());
		assertTrue(omsReturnFacade.canRefundDeliveryCost(ORDER_ID, true));
	}

	@Test
	public void cannotRefundDeliveryCost_DeliveryCostNotRequested()
	{
		assertFalse(omsReturnFacade.canRefundDeliveryCost(ORDER_ID, false));
	}

	@Test
	public void cannotRefundDeliveryCost_RequestRequest_RefundDeliveryCostTrue_NotCanceled()
	{
		when(previousReturn.getRefundDeliveryCost()).thenReturn(true);
		when(previousReturn.getStatus()).thenReturn(ReturnStatus.COMPLETED);

		assertFalse(omsReturnFacade.canRefundDeliveryCost(ORDER_ID, true));
	}

	@Test
	public void canRefundDeliveryCost_RequestRequest_RefundDeliveryCostTrue_Canceled()
	{
		when(previousReturn.getRefundDeliveryCost()).thenReturn(true);
		when(previousReturn.getStatus()).thenReturn(ReturnStatus.CANCELED);

		assertTrue(omsReturnFacade.canRefundDeliveryCost(ORDER_ID, true));
	}

	@Test
	public void canRefundDeliveryCost_RequestRequest_RefundDeliveryCostFalse_Canceled()
	{
		when(previousReturn.getRefundDeliveryCost()).thenReturn(false);
		when(previousReturn.getStatus()).thenReturn(ReturnStatus.CANCELED);

		assertTrue(omsReturnFacade.canRefundDeliveryCost(ORDER_ID, true));
	}

	@Test
	public void canRefundDeliveryCost_RequestRequest_RefundDeliveryCostFalse_NotCanceled()
	{
		when(previousReturn.getRefundDeliveryCost()).thenReturn(false);
		when(previousReturn.getStatus()).thenReturn(ReturnStatus.COMPLETED);

		assertTrue(omsReturnFacade.canRefundDeliveryCost(ORDER_ID, true));
	}

}
