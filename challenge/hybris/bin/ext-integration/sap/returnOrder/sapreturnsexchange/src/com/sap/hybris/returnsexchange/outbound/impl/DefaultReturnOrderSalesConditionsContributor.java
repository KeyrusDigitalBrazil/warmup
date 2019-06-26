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

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.promotionengineservices.model.AbstractRuleBasedPromotionActionModel;
import de.hybris.platform.promotions.model.AbstractPromotionActionModel;
import de.hybris.platform.returns.model.RefundEntryModel;
import de.hybris.platform.returns.model.ReturnEntryModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.ruleengineservices.model.AbstractRuleModel;
import de.hybris.platform.ruleengineservices.rule.services.RuleService;
import de.hybris.platform.sap.core.configuration.model.SAPConfigurationModel;
import de.hybris.platform.sap.orderexchange.constants.OrderCsvColumns;
import de.hybris.platform.sap.orderexchange.constants.SalesConditionCsvColumns;
import de.hybris.platform.sap.orderexchange.outbound.RawItemContributor;
import de.hybris.platform.sap.orderexchange.outbound.impl.DefaultSalesConditionsContributor;
import de.hybris.platform.sap.sapmodel.model.SAPPricingConditionModel;
import de.hybris.platform.util.DiscountValue;
import de.hybris.platform.util.TaxValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.sap.hybris.returnsexchange.constants.ReturnOrderEntryCsvColumns;

public class DefaultReturnOrderSalesConditionsContributor implements RawItemContributor<ReturnRequestModel> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultSalesConditionsContributor.class);
    // Header conditions
    private static final int CONDITION_COUNTER_DELIVERY_COST = 1;
    private static final int CONDITION_COUNTER_PAYMENT_COST = 2;
    // reserve condition counter 15-25 for order discount rows
    private static final int CONDITION_COUNTER_START_ORDER_DISCOUNT = 15;
    private static final int CONDITION_COUNTER_START_TAX = 25;

    // Item conditions
    private static final int CONDITION_COUNTER_GROSS_PRICE = 3;
    // reserve condition counter 5-15 for product discount rows
    private static final int CONDITION_COUNTER_START_PRODUCT_DISCOUNT = 5;
    public static final String PROMOTION_DISCOUNT_CODE_PREFIX = "Action";

    private String tax1;
    private String grossPrice;
    private String deliveryCosts;
    private String paymentCosts;

    private int conditionCounterDeliveryCost = CONDITION_COUNTER_DELIVERY_COST;
    private int conditionCounterPaymentCost = CONDITION_COUNTER_PAYMENT_COST;
    private int conditionCounterTax = CONDITION_COUNTER_START_TAX;
    private int conditionCounterGrossPrice = CONDITION_COUNTER_GROSS_PRICE;
    private int conditionCounterStartProductDiscount = CONDITION_COUNTER_START_PRODUCT_DISCOUNT;
    private int conditionCounterStartOrderDiscount = CONDITION_COUNTER_START_ORDER_DISCOUNT;

    private RuleService ruleService;

    public RuleService getRuleService() {
        return ruleService;
    }

    @Required
    public void setRuleService(final RuleService ruleService) {
        this.ruleService = ruleService;
    }

    @Override
    public Set<String> getColumns() {
        return new HashSet<>(Arrays.asList(OrderCsvColumns.ORDER_ID, SalesConditionCsvColumns.CONDITION_ENTRY_NUMBER,
                SalesConditionCsvColumns.CONDITION_CODE, SalesConditionCsvColumns.CONDITION_CURRENCY_ISO_CODE,
                SalesConditionCsvColumns.CONDITION_VALUE, SalesConditionCsvColumns.ABSOLUTE,
                SalesConditionCsvColumns.CONDITION_UNIT_CODE, SalesConditionCsvColumns.CONDITION_PRICE_QUANTITY,
                SalesConditionCsvColumns.CONDITION_COUNTER, ReturnOrderEntryCsvColumns.REFUND_DELIVERY_COST,
                ReturnOrderEntryCsvColumns.SHIPPING_CONDITION_TYPE));
    }

    protected void setConditionTypes(final OrderModel order) {
        final SAPConfigurationModel sapConfiguration = order.getStore().getSAPConfiguration();
        if (sapConfiguration != null) {
            setGrossPrice(sapConfiguration.getSaporderexchange_itemPriceConditionType());
            setDeliveryCosts(sapConfiguration.getSaporderexchange_deliveryCostConditionType());
            setPaymentCosts(sapConfiguration.getSaporderexchange_paymentCostConditionType());
        }
    }

    @Override
    public List<Map<String, Object>> createRows(final ReturnRequestModel returnModel) {
        final OrderModel order = returnModel.getOrder();
        final List<AbstractOrderEntryModel> entries = order.getEntries();
        return syncPricingInactive(entries) ? createRowsHybrisPricing(returnModel, entries)
                : createRowsSyncPricing(returnModel, entries);
    }

    protected boolean syncPricingInactive(final List<AbstractOrderEntryModel> entries) {
        return entries.get(0).getSapPricingConditions() == null || entries.get(0).getSapPricingConditions().isEmpty();
    }

    private List<Map<String, Object>> createRowsHybrisPricing(final ReturnRequestModel returnReq,
            final List<AbstractOrderEntryModel> entries) {
        final OrderModel order = returnReq.getOrder();
        final List<Map<String, Object>> result = new ArrayList<>();
        setConditionTypes(order);
        final int totalEntries = entries.size();

        List<AbstractOrderEntryModel> preparedEntryList = null;
        if (entries.size() != returnReq.getReturnEntries().size()) {

            preparedEntryList = prepareReturnOrderEntries(entries, returnReq.getReturnEntries());
        }

        createOrderDiscountRows(returnReq, result);
        for (final AbstractOrderEntryModel entry : null != preparedEntryList ? preparedEntryList : entries) {
            createGrossPriceRow(returnReq, result, entry);
            createTaxRows(returnReq, result, entry);
            createProductDiscountRows(returnReq, result, entry); 
            createDeliveryCostRow(returnReq, result, entry, totalEntries);
            createPaymentCostRow(returnReq, result, entry);
        }

        return result;
    }

    private List<Map<String, Object>> createRowsSyncPricing(final ReturnRequestModel returnReq,
            final List<AbstractOrderEntryModel> entries) {
        final List<Map<String, Object>> result = new ArrayList<>();

        List<AbstractOrderEntryModel> preparedEntryList = null;
        if (entries.size() != returnReq.getReturnEntries().size()) {

            preparedEntryList = prepareReturnOrderEntries(entries, returnReq.getReturnEntries());
        }

        for (final AbstractOrderEntryModel entry : null != preparedEntryList ? preparedEntryList : entries) {
            final Iterator<SAPPricingConditionModel> it = entry.getSapPricingConditions().iterator();
            while (it.hasNext()) {
                final SAPPricingConditionModel condition = it.next();
                final Map<String, Object> row = new HashMap<>();

                row.put(OrderCsvColumns.ORDER_ID, returnReq.getCode());
                row.put(SalesConditionCsvColumns.CONDITION_ENTRY_NUMBER, entry.getEntryNumber());

                row.put(SalesConditionCsvColumns.CONDITION_CODE, condition.getConditionType());
                row.put(SalesConditionCsvColumns.CONDITION_VALUE, condition.getConditionRate());
                row.put(SalesConditionCsvColumns.CONDITION_UNIT_CODE, condition.getConditionUnit());
                row.put(SalesConditionCsvColumns.CONDITION_PRICE_QUANTITY, condition.getConditionPricingUnit());
                row.put(SalesConditionCsvColumns.CONDITION_CURRENCY_ISO_CODE, condition.getCurrencyKey());
                row.put(SalesConditionCsvColumns.CONDITION_COUNTER, condition.getConditionCounter());
                row.put(ReturnOrderEntryCsvColumns.REFUND_DELIVERY_COST, returnReq.getRefundDeliveryCost());
                row.put(ReturnOrderEntryCsvColumns.SHIPPING_CONDITION_TYPE, deliveryCosts);
                result.add(row);
            }
        }
        return result;
    }

    protected void createPaymentCostRow(final ReturnRequestModel returnReq, final List<Map<String, Object>> result,
            final AbstractOrderEntryModel entry) {
        final Map<String, Object> row = new HashMap<>();
        row.put(OrderCsvColumns.ORDER_ID, returnReq.getCode());
        row.put(SalesConditionCsvColumns.CONDITION_ENTRY_NUMBER, entry.getEntryNumber());
        row.put(SalesConditionCsvColumns.CONDITION_CODE, paymentCosts);
        row.put(SalesConditionCsvColumns.CONDITION_VALUE, returnReq.getOrder().getPaymentCost());
        row.put(SalesConditionCsvColumns.CONDITION_CURRENCY_ISO_CODE, returnReq.getOrder().getCurrency().getIsocode());
        row.put(SalesConditionCsvColumns.CONDITION_COUNTER, getConditionCounterPaymentCost());
        row.put(SalesConditionCsvColumns.ABSOLUTE, Boolean.TRUE);
        row.put(ReturnOrderEntryCsvColumns.REFUND_DELIVERY_COST, returnReq.getRefundDeliveryCost());
        result.add(row);
    }

    protected void createDeliveryCostRow(final ReturnRequestModel returnReq, final List<Map<String, Object>> result,
            final AbstractOrderEntryModel entry, final int totalEntries) {
        final double itemcost = returnReq.getOrder().getDeliveryCost() / totalEntries;
        final Map<String, Object> row = new HashMap<>();
        row.put(OrderCsvColumns.ORDER_ID, returnReq.getCode());
        row.put(SalesConditionCsvColumns.CONDITION_ENTRY_NUMBER, entry.getEntryNumber());
        row.put(SalesConditionCsvColumns.CONDITION_CODE, deliveryCosts);
        row.put(SalesConditionCsvColumns.CONDITION_VALUE, itemcost);
        row.put(SalesConditionCsvColumns.CONDITION_CURRENCY_ISO_CODE, returnReq.getOrder().getCurrency().getIsocode());
        row.put(SalesConditionCsvColumns.CONDITION_COUNTER, getConditionCounterDeliveryCost());
        row.put(SalesConditionCsvColumns.ABSOLUTE, Boolean.TRUE);
        row.put(ReturnOrderEntryCsvColumns.REFUND_DELIVERY_COST, returnReq.getRefundDeliveryCost());
        row.put(ReturnOrderEntryCsvColumns.SHIPPING_CONDITION_TYPE, deliveryCosts);
        result.add(row);
    }

    public static List<DiscountValue> safe(final List<DiscountValue> other) {
        return other == null ? Collections.emptyList() : other;
    }

    public static <T> Iterable<T> emptyIfNull(final Iterable<T> iterable) {
        return iterable == null ? Collections.<T> emptyList() : iterable;
    }

    protected void createOrderDiscountRows(final ReturnRequestModel returnReq, final List<Map<String, Object>> result) {
    	//Provide your implementation to send Order Discount rows to ERP
    }

    protected void createProductDiscountRows(final ReturnRequestModel returnReq, final List<Map<String, Object>> result,
            final AbstractOrderEntryModel entry) {
    	//Provide your implementation to send Product Discount rows to ERP
    }

    protected void createTaxRows(final ReturnRequestModel returnReq, final List<Map<String, Object>> result,
            final AbstractOrderEntryModel entry) {
        final Iterator<TaxValue> taxIterator = entry.getTaxValues().iterator();
        while (taxIterator.hasNext()) {
            final TaxValue next = taxIterator.next();
            final Map<String, Object> row = new HashMap<>();
            row.put(OrderCsvColumns.ORDER_ID, returnReq.getCode());
            row.put(SalesConditionCsvColumns.CONDITION_ENTRY_NUMBER, entry.getEntryNumber());
            row.put(SalesConditionCsvColumns.CONDITION_CODE, tax1);
            row.put(SalesConditionCsvColumns.CONDITION_VALUE, next.getValue());
            row.put(SalesConditionCsvColumns.CONDITION_COUNTER, getConditionCounterTax());

            if (next.isAbsolute()) {
                row.put(SalesConditionCsvColumns.ABSOLUTE, Boolean.TRUE);
                row.put(SalesConditionCsvColumns.CONDITION_CURRENCY_ISO_CODE,
                        returnReq.getOrder().getCurrency().getIsocode());
                row.put(SalesConditionCsvColumns.CONDITION_UNIT_CODE, entry.getUnit().getCode());
                row.put(SalesConditionCsvColumns.CONDITION_PRICE_QUANTITY, entry.getProduct().getPriceQuantity());
            } else {
                row.put(SalesConditionCsvColumns.ABSOLUTE, Boolean.FALSE);
            }
            row.put(ReturnOrderEntryCsvColumns.REFUND_DELIVERY_COST, returnReq.getRefundDeliveryCost());

            result.add(row);
            break; // Currently only the first entry is used
        }
    }

    protected void createGrossPriceRow(final ReturnRequestModel returnReq, final List<Map<String, Object>> result,
            final AbstractOrderEntryModel entry) {
        final Map<String, Object> row = new HashMap<>();
        row.put(OrderCsvColumns.ORDER_ID, returnReq.getCode());
        row.put(SalesConditionCsvColumns.CONDITION_ENTRY_NUMBER, entry.getEntryNumber());
        row.put(SalesConditionCsvColumns.CONDITION_CODE, grossPrice);
        for (ReturnEntryModel returnEntry : returnReq.getReturnEntries()) {
        	
        	RefundEntryModel refundEntry = (RefundEntryModel) returnEntry;
        	if(entry.getPk().equals(returnEntry.getOrderEntry().getPk())){
        		row.put(SalesConditionCsvColumns.CONDITION_VALUE,refundEntry.getAmount());
        	}
		}
        
        row.put(SalesConditionCsvColumns.CONDITION_UNIT_CODE, entry.getUnit().getCode());
        row.put(SalesConditionCsvColumns.CONDITION_PRICE_QUANTITY, entry.getProduct().getPriceQuantity());
        row.put(SalesConditionCsvColumns.CONDITION_CURRENCY_ISO_CODE, returnReq.getOrder().getCurrency().getIsocode());
        row.put(SalesConditionCsvColumns.ABSOLUTE, Boolean.TRUE);
        row.put(SalesConditionCsvColumns.CONDITION_COUNTER, getConditionCounterGrossPrice());
        row.put(ReturnOrderEntryCsvColumns.REFUND_DELIVERY_COST, returnReq.getRefundDeliveryCost());

        result.add(row);
    }

    // determine sap code corresponding to hybris promotion code
    protected String determinePromotionDiscountCode(final OrderModel order, final DiscountValue discountValue) {

        final AbstractPromotionActionModel abstractAction = order.getAllPromotionResults().stream()
                .flatMap(pr -> pr.getActions().stream())
                .filter(action -> action.getGuid().equals(discountValue.getCode())).collect(Collectors.toList())
                .stream().map(Optional::ofNullable).findFirst().flatMap(Function.identity()).orElse(null);

        if ( abstractAction instanceof AbstractRuleBasedPromotionActionModel) {

            final AbstractRuleModel rule = getRuleService()
                    .getRuleForCode(((AbstractRuleBasedPromotionActionModel) abstractAction).getRule().getCode());

            if (rule != null) {

                if (rule.getSapConditionType() != null) {
                    return rule.getSapConditionType();
                } else {
                	LOGGER.warn("The promotion rule with code {0} is missing the SAP Condition Type; therefore, the promotion discount has not been sent to SAP-ERP!", rule.getCode()); 
                }
                return null;
            }

        }

        LOGGER.warn("The promotion rule with discount value {0} is not configured properly; therefore, the promotion discount has not been sent to SAP-ERP!",
        		discountValue);
        return null;

    }

    private List<AbstractOrderEntryModel> prepareReturnOrderEntries(final List<AbstractOrderEntryModel> entries,
            final List<ReturnEntryModel> returnEntries) {
        final List<AbstractOrderEntryModel> productList = new ArrayList<AbstractOrderEntryModel>(returnEntries.size());
        for (final AbstractOrderEntryModel orderEntry : entries) {
            for (final ReturnEntryModel returnEntry : returnEntries) {
                if (orderEntry.getProduct().getCode()
                        .equalsIgnoreCase(returnEntry.getOrderEntry().getProduct().getCode())) {
                    productList.add(orderEntry);
                }
            }
        }
        return productList;
    }

    @SuppressWarnings("javadoc")
    @Required
    public void setTax1(final String tax1) {
        this.tax1 = tax1;
    }

    @SuppressWarnings("javadoc")
    public void setGrossPrice(final String grossPrice) {
        this.grossPrice = grossPrice;
    }

    @SuppressWarnings("javadoc")
    public void setDeliveryCosts(final String deliveryCosts) {
        this.deliveryCosts = deliveryCosts;
    }

    @SuppressWarnings("javadoc")
    public void setPaymentCosts(final String paymentCosts) {
        this.paymentCosts = paymentCosts;
    }

    @SuppressWarnings("javadoc")
    public int getConditionCounterDeliveryCost() {
        return conditionCounterDeliveryCost;
    }

    @SuppressWarnings("javadoc")
    public void setConditionCounterDeliveryCost(final int conditionCounterDeliveryCost) {
        this.conditionCounterDeliveryCost = conditionCounterDeliveryCost;
    }

    @SuppressWarnings("javadoc")
    public int getConditionCounterPaymentCost() {
        return conditionCounterPaymentCost;
    }

    @SuppressWarnings("javadoc")
    public void setConditionCounterPaymentCost(final int conditionCounterPaymentCost) {
        this.conditionCounterPaymentCost = conditionCounterPaymentCost;
    }

    @SuppressWarnings("javadoc")
    public int getConditionCounterTax() {
        return conditionCounterTax;
    }

    @SuppressWarnings("javadoc")
    public void setConditionCounterTax(final int conditionCounterTax) {
        this.conditionCounterTax = conditionCounterTax;
    }

    @SuppressWarnings("javadoc")
    public int getConditionCounterStartProductDiscount() {
        return conditionCounterStartProductDiscount;
    }

    @SuppressWarnings("javadoc")
    public void setConditionCounterStartProductDiscount(final int conditionCounterStartProductDiscount) {
        this.conditionCounterStartProductDiscount = conditionCounterStartProductDiscount;
    }

    @SuppressWarnings("javadoc")
    public int getConditionCounterStartOrderDiscount() {
        return conditionCounterStartOrderDiscount;
    }

    @SuppressWarnings("javadoc")
    public void setConditionCounterStartOrderDiscount(final int conditionCounterStartOrderDiscount) {
        this.conditionCounterStartOrderDiscount = conditionCounterStartOrderDiscount;
    }

    @SuppressWarnings("javadoc")
    public int getConditionCounterGrossPrice() {
        return conditionCounterGrossPrice;
    }

    @SuppressWarnings("javadoc")
    public void setConditionCounterGrossPrice(final int conditionCounterGrossPrice) {
        this.conditionCounterGrossPrice = conditionCounterGrossPrice;
    }

    @SuppressWarnings("javadoc")
    public String getGrossPrice() {
        return grossPrice;
    }

    @SuppressWarnings("javadoc")
    public String getTax1() {
        return tax1;
    }
}
