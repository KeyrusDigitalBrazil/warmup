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

import java.util.Arrays;
import java.util.Date;
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
import de.hybris.platform.core.enums.QuoteState;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.sap.c4c.quote.constants.QuoteCsvColumns;
import de.hybris.platform.sap.core.configuration.model.SAPConfigurationModel;
import de.hybris.platform.store.BaseStoreModel;

/**
 * Test for {@DefaultQuoteContributor}
 */
@UnitTest
public class DefaultQuoteContributorTest {

    @InjectMocks
    private DefaultQuoteContributor contributor = new DefaultQuoteContributor();
    private final static String DUMMY_TEXT = "dummyText";
    private final static Long DUMMY_QUANTITY = 2L;
    @Mock
    private QuoteModel quoteModel;
    @Mock
    private BaseStoreModel baseStoreModel;
    @Mock
    private SAPConfigurationModel sapConfigurationModel;
    @Mock
    private LanguageModel languageModel;
    @Mock
    private AbstractOrderEntryModel entry;
    @Mock
    private CurrencyModel currency;
    

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockBasicQuoteFields();
    }

    @Test
    public void testCreateRows() {
        List<Map<String, Object>> results = contributor.createRows(quoteModel);
        assertFalse(results.isEmpty());
        Assert.assertEquals(1, results.size());
        Assert.assertEquals(DUMMY_TEXT, results.get(0).get(QuoteCsvColumns.QUOTE_ID));
    }

    @Test
    public void testGetColumns() {
        Set<String> results = contributor.getColumns();
        assertFalse(results.isEmpty());
    }

    private void mockBasicQuoteFields() {
        when(quoteModel.getCode()).thenReturn(DUMMY_TEXT);
        when(quoteModel.getDate()).thenReturn(new Date());
        when(quoteModel.getStore()).thenReturn(baseStoreModel);
        when(baseStoreModel.getSAPConfiguration()).thenReturn(sapConfigurationModel);
        when(sapConfigurationModel.getQuoteType()).thenReturn(DUMMY_TEXT);
        when(quoteModel.getStore().getSAPConfiguration().getQuoteType()).thenReturn(DUMMY_TEXT);
        when(quoteModel.getName()).thenReturn(DUMMY_TEXT);
        when(quoteModel.getDescription()).thenReturn(DUMMY_TEXT);
        when(quoteModel.getCurrency()).thenReturn(currency);
        when(currency.getSapCode()).thenReturn("USD");
        when(quoteModel.getEntries()).thenReturn(Arrays.asList(entry));
        when(entry.getQuantity()).thenReturn(DUMMY_QUANTITY);

        when(baseStoreModel.getUid()).thenReturn(DUMMY_TEXT);
        when(baseStoreModel.getDefaultLanguage()).thenReturn(languageModel);
        when(languageModel.getIsocode()).thenReturn(DUMMY_TEXT);


    }
}
