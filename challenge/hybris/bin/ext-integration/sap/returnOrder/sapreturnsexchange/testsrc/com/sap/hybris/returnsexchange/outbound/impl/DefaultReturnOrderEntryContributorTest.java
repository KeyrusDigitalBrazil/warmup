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

import de.hybris.platform.basecommerce.enums.RefundReason;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.returns.model.RefundEntryModel;
import de.hybris.platform.returns.model.ReturnEntryModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.sap.core.configuration.model.SAPConfigurationModel;
import de.hybris.platform.sap.orderexchange.constants.OrderCsvColumns;
import de.hybris.platform.sap.orderexchange.constants.OrderEntryCsvColumns;
import de.hybris.platform.store.BaseStoreModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("javadoc")
public class DefaultReturnOrderEntryContributorTest {
    private static final String LANG_DE = "DE";
    private static final String LANG_EN = "EN";

    private static final long QUANTITY_1000_LONG = 1000;
    private static final int ENTRY_NUMBER_100_INT = 100;
    private static final String PRODUCT_NAME1_DE = "productName1_DE";
    private static final String UNIT_CODE1 = "unitCode1";
    private static final String PRODUCT_CODE1 = "productCode1";

    private static final long QUANTITY_2000_LONG = 1000;
    private static final int ENTRY_NUMBER_200_INT = 100;
    private static final String PRODUCT_NAME2_EN = "productName2_EN";
    private static final String UNIT_CODE2 = "unitCode2";
    private static final String PRODUCT_CODE2 = "productCode2";

    private static final String CODE = "Code";
    private static final String WAREHOUSE_CODE = "WarehouseCode";

    private DefaultReturnOrderEntryContributor cut;

    @Before
    public void setUp() {
        cut = new DefaultReturnOrderEntryContributor();
    }

    @Test
    public void testGetColumns() {
        final Set<String> columns = cut.getColumns();

        assertTrue(columns.contains(OrderCsvColumns.ORDER_ID));
        assertTrue(columns.contains(OrderEntryCsvColumns.ENTRY_NUMBER));
        assertTrue(columns.contains(OrderEntryCsvColumns.QUANTITY));
        assertTrue(columns.contains(OrderEntryCsvColumns.REJECTION_REASON));
        assertTrue(columns.contains(OrderEntryCsvColumns.NAMED_DELIVERY_DATE));
        assertTrue(columns.contains(OrderEntryCsvColumns.ENTRY_UNIT_CODE));
        assertTrue(columns.contains(OrderEntryCsvColumns.PRODUCT_CODE));
    }

    @Test
    public void testCreateRow() {
        final ReturnRequestModel order = new ReturnRequestModel();
        final List<ReturnEntryModel> returnEntries = new ArrayList<ReturnEntryModel>();
        RefundEntryModel entry = new RefundEntryModel();

        final OrderModel preceedingDocument = new OrderModel();
        final BaseStoreModel baseStore = new BaseStoreModel();
        final SAPConfigurationModel sapConfiguration = new SAPConfigurationModel();
        ProductModel product = new ProductModel();
        UnitModel unit = new UnitModel();
        final Locale loc = new Locale(LANG_DE);
        final LanguageModel language_de = new LanguageModel();
        final LanguageModel language_en = new LanguageModel();
        final List<AbstractOrderEntryModel> entries = new ArrayList<AbstractOrderEntryModel>();
        final List<LanguageModel> fallbackLanguages = new ArrayList<LanguageModel>();
        OrderEntryModel orderEntry = new OrderEntryModel();

        final WarehouseModel warehouse = new WarehouseModel();
        warehouse.setCode(WAREHOUSE_CODE);
        final ConsignmentModel consignment = new ConsignmentModel();
        consignment.setWarehouse(warehouse);
        final ConsignmentEntryModel consignmentEntry = new ConsignmentEntryModel();
        final Set<ConsignmentEntryModel> set = new HashSet<>();
        consignmentEntry.setConsignment(consignment);
        set.add(consignmentEntry);
        orderEntry.setConsignmentEntries(set);

        language_de.setIsocode(LANG_DE);
        language_en.setIsocode(LANG_EN);
        fallbackLanguages.add(language_en);
        language_de.setFallbackLanguages(fallbackLanguages);
        order.setCode(CODE);
        preceedingDocument.setLanguage(language_en);

        // Item 100
        unit.setCode(UNIT_CODE1);

        product.setCode(PRODUCT_CODE1);
        product.setName(PRODUCT_NAME1_DE, loc);
        product.setUnit(unit);

        orderEntry.setEntryNumber(ENTRY_NUMBER_100_INT);
        orderEntry.setProduct(product);
        entry.setExpectedQuantity(Long.valueOf(QUANTITY_1000_LONG));
        sapConfiguration.setReturnOrderReason("Poor Quality");
        baseStore.setSAPConfiguration(sapConfiguration);
        preceedingDocument.setStore(baseStore);
        orderEntry.setOrder(preceedingDocument);
        entry.setOrderEntry(orderEntry);
        entries.add(orderEntry);
        entry.setReturnRequest(order);
        returnEntries.add(entry);

        // Item 200
        unit = new UnitModel();
        product = new ProductModel();
        orderEntry = new OrderEntryModel();
        entry = new RefundEntryModel();

        unit.setCode(UNIT_CODE2);

        product.setCode(PRODUCT_CODE2);
        product.setName(PRODUCT_NAME2_EN, loc);
        product.setUnit(unit);

        orderEntry.setEntryNumber(ENTRY_NUMBER_200_INT);
        orderEntry.setProduct(product);
        orderEntry.setConsignmentEntries(set);
        entry.setExpectedQuantity(Long.valueOf(QUANTITY_2000_LONG));
        sapConfiguration.setReturnOrderReason("Poor Quality");
        baseStore.setSAPConfiguration(sapConfiguration);
        preceedingDocument.setStore(baseStore);
        orderEntry.setOrder(preceedingDocument);
        entry.setOrderEntry(orderEntry);
        

        entries.add(orderEntry);
        entry.setReturnRequest(order);
        returnEntries.add(entry);

        preceedingDocument.setEntries(entries);
        order.setOrder(preceedingDocument);
        order.setReturnEntries(returnEntries);

        final List<Map<String, Object>> rows = cut.createRows(order);

        final Map<String, Object> row = rows.get(0);
        assertEquals(CODE, row.get(OrderCsvColumns.ORDER_ID));
        assertEquals(ENTRY_NUMBER_100_INT, row.get(OrderEntryCsvColumns.ENTRY_NUMBER));
        assertEquals(QUANTITY_1000_LONG, row.get(OrderEntryCsvColumns.QUANTITY));
        assertEquals(UNIT_CODE1, row.get(OrderEntryCsvColumns.ENTRY_UNIT_CODE));
        assertEquals(PRODUCT_CODE1, row.get(OrderEntryCsvColumns.PRODUCT_CODE));

        final Map<String, Object> row1 = rows.get(1);
        assertEquals(CODE, row1.get(OrderCsvColumns.ORDER_ID));
        assertEquals(ENTRY_NUMBER_200_INT, row1.get(OrderEntryCsvColumns.ENTRY_NUMBER));
        assertEquals(QUANTITY_2000_LONG, row1.get(OrderEntryCsvColumns.QUANTITY));
        assertEquals(UNIT_CODE2, row1.get(OrderEntryCsvColumns.ENTRY_UNIT_CODE));
        assertEquals(PRODUCT_CODE2, row1.get(OrderEntryCsvColumns.PRODUCT_CODE));
    }

}
