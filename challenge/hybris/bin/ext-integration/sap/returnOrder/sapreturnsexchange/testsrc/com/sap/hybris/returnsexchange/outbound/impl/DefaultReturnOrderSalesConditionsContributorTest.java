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
package com.sap.hybris.returnsexchange.outbound.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.returns.model.RefundEntryModel;
import de.hybris.platform.returns.model.ReturnEntryModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.sap.orderexchange.constants.OrderCsvColumns;
import de.hybris.platform.sap.orderexchange.constants.SalesConditionCsvColumns;
import de.hybris.platform.sap.sapmodel.model.SAPPricingConditionModel;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.util.DiscountValue;
import de.hybris.platform.util.TaxValue;
import de.hybris.platform.core.PK;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 *
 */
@UnitTest
public class DefaultReturnOrderSalesConditionsContributorTest {

    private static final Double PRICE_QUANTITY = new Double(2.5);
    private static final String EUR = "EUR";
    private static final Double PAYMENT_COST = new Double(4.95);
    private static final Double DELIVERY_COST = new Double(8.95);
    private static final Double DISCOUNT_VALUE = new Double(10.00);
    private static final String PAYMENT_COST_CODE = "paymentCostCode";
    private static final String DELIVERY_COST_CODE = "deliveryCostCode";
    private static final String GROSS_PRICE_CODE = "grossPriceCode";
    private static final String TAX_CODE = "taxCode";
    private static final String DISCOUNT_CODE = "discountCode";
    private static final String CODE = "Code";
    private static final int ENTRY_NUMBER_100_INT = 100;
    private static final long ENTRY_QUANTITY = 2;
    private static final String UNIT_CODE = "unitCode";
    private static final Double TAX_VALUE = new Double(19.0);
    private static final BigDecimal REFUND_AMOUNT = new BigDecimal(25.00);


    private DefaultReturnOrderSalesConditionsContributor cut;
    private OrderModel order;
    @Mock
    private ReturnRequestModel returnrequest;

    private RefundEntryModel returnEntry;

    @Mock
    private OrderEntryModel entry; // Use mock to entry since otherwise
                                   // collections cannot be set!
    @Mock
    private TaxValue tax;

    @Mock
    private DiscountValue discount;

    @Before
    public void setUp() {
        cut = new DefaultReturnOrderSalesConditionsContributor();
        constructOrder();
    }

    private void constructOrder() {
        MockitoAnnotations.initMocks(this);
        Mockito.when(Double.valueOf(tax.getValue())).thenReturn(TAX_VALUE);
        Mockito.when(discount.getCode()).thenReturn(DISCOUNT_CODE);
        Mockito.when(Double.valueOf(discount.getValue())).thenReturn(DISCOUNT_VALUE);
        Mockito.when(Boolean.valueOf(discount.isAbsolute())).thenReturn(Boolean.valueOf(true));

        order = new OrderModel();
        returnEntry = new RefundEntryModel();

        order.setCode(CODE);
        final List<AbstractOrderEntryModel> entries = new ArrayList<AbstractOrderEntryModel>();
        order.setEntries(entries);
        Mockito.when(returnrequest.getOrder()).thenReturn(order);
        Mockito.when(returnrequest.getCode()).thenReturn(order.getCode());

        final List<ReturnEntryModel> returnentries = new ArrayList<ReturnEntryModel>();

        returnEntry.setAmount(REFUND_AMOUNT);
        returnEntry.setOrderEntry(entry);
        returnEntry.setReturnRequest(returnrequest);
        returnentries.add(returnEntry);
        returnrequest.setReturnEntries(returnentries);

        Mockito.when(returnrequest.getReturnEntries()).thenReturn(returnentries);
        final UnitModel unit = new UnitModel();
        unit.setCode(UNIT_CODE);
        final ProductModel product = new ProductModel();
        product.setPriceQuantity(PRICE_QUANTITY);
        final Collection<TaxValue> taxValues = new ArrayList<TaxValue>();
        taxValues.add(tax);
        final List<DiscountValue> discountValues = new ArrayList<DiscountValue>();
        discountValues.add(discount);
        Mockito.when(entry.getEntryNumber()).thenReturn(Integer.valueOf(ENTRY_NUMBER_100_INT));
        Mockito.when(entry.getUnit()).thenReturn(unit);
        Mockito.when(entry.getTaxValues()).thenReturn(taxValues);
        Mockito.when(entry.getDiscountValues()).thenReturn(discountValues);
        Mockito.when(entry.getProduct()).thenReturn(product);
        Mockito.when(entry.getQuantity()).thenReturn(Long.valueOf(ENTRY_QUANTITY));
        Mockito.when(entry.getPk()).thenReturn(PK.fromLong(123));

        entries.add(entry);

        final BaseStoreModel store = new BaseStoreModel();
        order.setStore(store);

        final CurrencyModel currency = new CurrencyModel();
        currency.setIsocode(EUR);
        order.setCurrency(currency);
        order.setPaymentCost(PAYMENT_COST);
        order.setDeliveryCost(DELIVERY_COST);
        cut.setPaymentCosts(PAYMENT_COST_CODE);
        cut.setDeliveryCosts(DELIVERY_COST_CODE);
        cut.setGrossPrice(GROSS_PRICE_CODE);
        cut.setTax1(TAX_CODE);

    }

    @Test
    public void testGetColumns() {
        final Set<String> columns = cut.getColumns();

        assertTrue(columns.contains(OrderCsvColumns.ORDER_ID));
        assertTrue(columns.contains(SalesConditionCsvColumns.CONDITION_ENTRY_NUMBER));
        assertTrue(columns.contains(SalesConditionCsvColumns.CONDITION_CODE));
        assertTrue(columns.contains(SalesConditionCsvColumns.CONDITION_CURRENCY_ISO_CODE));
        assertTrue(columns.contains(SalesConditionCsvColumns.CONDITION_VALUE));
        assertTrue(columns.contains(SalesConditionCsvColumns.ABSOLUTE));
        assertTrue(columns.contains(SalesConditionCsvColumns.CONDITION_UNIT_CODE));
        assertTrue(columns.contains(SalesConditionCsvColumns.CONDITION_PRICE_QUANTITY));
        assertTrue(columns.contains(SalesConditionCsvColumns.CONDITION_COUNTER));
    }

    @Test
    public void testCreateRowsSyncPricing() {
        final Set<SAPPricingConditionModel> sapPricingConditionModelSet = new HashSet<SAPPricingConditionModel>();
        final SAPPricingConditionModel sapPricingConditionModel1 = new SAPPricingConditionModel();
        sapPricingConditionModel1.setConditionCounter("01");
        sapPricingConditionModel1.setConditionType(GROSS_PRICE_CODE);
        sapPricingConditionModel1.setCurrencyKey(EUR);
        sapPricingConditionModel1.setConditionPricingUnit("1");
        sapPricingConditionModel1.setConditionUnit("PCE");
        sapPricingConditionModel1.setConditionRate("1.50");
        sapPricingConditionModel1.setConditionValue("41.12");
        sapPricingConditionModelSet.add(sapPricingConditionModel1);

        Mockito.when(entry.getSapPricingConditions()).thenReturn(sapPricingConditionModelSet);

        final List<Map<String, Object>> rows = cut.createRows(returnrequest);
        assertEquals(1, rows.size());
        final Map<String, Object> row = rows.get(0);
        assertEquals(GROSS_PRICE_CODE, row.get(SalesConditionCsvColumns.CONDITION_CODE));
        assertEquals(CODE, row.get(OrderCsvColumns.ORDER_ID));
        assertEquals(EUR, row.get(SalesConditionCsvColumns.CONDITION_CURRENCY_ISO_CODE));
        assertEquals("01", row.get(SalesConditionCsvColumns.CONDITION_COUNTER));

    }

    @Test
    public void testCreatePaymentCostRow() {
        final List<Map<String, Object>> result = new ArrayList<>();

        cut.createPaymentCostRow(returnrequest, result, entry);

        final Map<String, Object> row = result.get(0);
        assertEquals(CODE, row.get(OrderCsvColumns.ORDER_ID));
        assertEquals(PAYMENT_COST_CODE, row.get(SalesConditionCsvColumns.CONDITION_CODE));
        assertEquals(EUR, row.get(SalesConditionCsvColumns.CONDITION_CURRENCY_ISO_CODE));
        assertEquals(Integer.valueOf(2), row.get(SalesConditionCsvColumns.CONDITION_COUNTER));
        assertEquals(Boolean.TRUE, row.get(SalesConditionCsvColumns.ABSOLUTE));
    }

    @Test
    public void testCreateTaxRows() {
        final List<Map<String, Object>> result = new ArrayList<>();

        cut.createTaxRows(returnrequest, result, entry);

        final Map<String, Object> row = result.get(0);
        assertEquals(CODE, row.get(OrderCsvColumns.ORDER_ID));
        assertEquals(Integer.valueOf(ENTRY_NUMBER_100_INT), row.get(SalesConditionCsvColumns.CONDITION_ENTRY_NUMBER));
        assertEquals(TAX_CODE, row.get(SalesConditionCsvColumns.CONDITION_CODE));
        assertEquals(TAX_VALUE, row.get(SalesConditionCsvColumns.CONDITION_VALUE));
        assertEquals(Boolean.FALSE, row.get(SalesConditionCsvColumns.ABSOLUTE));
    }

    @Test
    public void testCreateGrossPriceRow() {
        final List<Map<String, Object>> result = new ArrayList<>();

        cut.createGrossPriceRow(returnrequest, result, entry);

        final Map<String, Object> row = result.get(0);
        assertEquals(CODE, row.get(OrderCsvColumns.ORDER_ID));
        assertEquals(Integer.valueOf(ENTRY_NUMBER_100_INT), row.get(SalesConditionCsvColumns.CONDITION_ENTRY_NUMBER));
        assertEquals(GROSS_PRICE_CODE, row.get(SalesConditionCsvColumns.CONDITION_CODE));
        assertEquals(REFUND_AMOUNT, row.get(SalesConditionCsvColumns.CONDITION_VALUE));
        assertEquals(UNIT_CODE, row.get(SalesConditionCsvColumns.CONDITION_UNIT_CODE));
        assertEquals(PRICE_QUANTITY, row.get(SalesConditionCsvColumns.CONDITION_PRICE_QUANTITY));
        assertEquals(EUR, row.get(SalesConditionCsvColumns.CONDITION_CURRENCY_ISO_CODE));
        assertEquals(Boolean.TRUE, row.get(SalesConditionCsvColumns.ABSOLUTE));

    }

}
