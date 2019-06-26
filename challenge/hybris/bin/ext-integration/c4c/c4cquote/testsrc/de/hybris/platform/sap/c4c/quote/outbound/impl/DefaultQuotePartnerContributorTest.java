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
package de.hybris.platform.sap.c4c.quote.outbound.impl;

import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.sap.c4c.quote.constants.QuoteCsvColumns;

/**
 * Test for {@DefaultQuotePartnerContributor}
 */
@UnitTest
public class DefaultQuotePartnerContributorTest {
    @InjectMocks
    private DefaultQuotePartnerContributor contributor = new DefaultQuotePartnerContributor();
    @Mock
    private QuoteModel quoteModel;
    @Mock
    private B2BUnitService<B2BUnitModel, ?> b2bUnitService;
    @Mock
    private B2BUnitModel rootUnit;
    @Mock
    private B2BCustomerModel b2buser;
    @Mock
    private B2BUnitModel b2bunit;
    @Mock
    private AddressModel address;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockBasicFields();
    }

    @Test
    public void testGetColumns() {
        Set<String> results = contributor.getColumns();
        assertFalse(results.isEmpty());
    }

    @Test
    public void testCreateRowsWithoutAddress() {

        List<Map<String, Object>> results = contributor.createRows(quoteModel);
        assertFalse(results.isEmpty());
        Assert.assertEquals("SOLDTOUNIT", results.get(2).get(QuoteCsvColumns.PARTNER_CODE));
    }

    @Test
    public void testCreateRowsWithAsdrresWithOutSapCustomer() {
        when(quoteModel.getDeliveryAddress()).thenReturn(address);
        when(quoteModel.getPaymentAddress()).thenReturn(address);
        List<Map<String, Object>> results = contributor.createRows(quoteModel);
        assertFalse(results.isEmpty());
        Assert.assertEquals("SOLDTOUNIT", results.get(2).get(QuoteCsvColumns.PARTNER_CODE));
    }

    @Test
    public void testCreateRowsWithCorrectAddress() {

        when(quoteModel.getDeliveryAddress()).thenReturn(address);
        when(quoteModel.getPaymentAddress()).thenReturn(address);
        when(address.getSapCustomerID()).thenReturn("DummyCustomerID");
        List<Map<String, Object>> results = contributor.createRows(quoteModel);
        assertFalse(results.isEmpty());
        Assert.assertEquals(4, results.size());
        Assert.assertEquals("DummyCustomerID", results.get(2).get(QuoteCsvColumns.PARTNER_CODE));
    }

    private void mockBasicFields() {
        when(quoteModel.getCode()).thenReturn("dummyQuoteCode");
        when(quoteModel.getUser()).thenReturn(b2buser);
        when(b2buser.getDefaultB2BUnit()).thenReturn(b2bunit);
        when(b2bUnitService.getRootUnit(b2bunit)).thenReturn(rootUnit);
        when(rootUnit.getUid()).thenReturn("SOLDTOUNIT");
        when(b2buser.getCustomerID()).thenReturn("CONTACT_PERSON");
    }
}
