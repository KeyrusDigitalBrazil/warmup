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
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.comments.model.CommentModel;
import de.hybris.platform.core.enums.QuoteState;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.sap.c4c.quote.constants.QuoteCsvColumns;
import de.hybris.platform.sap.c4c.quote.constants.QuoteEntryCsvColumns;
import de.hybris.platform.sap.core.configuration.model.SAPConfigurationModel;
import de.hybris.platform.store.BaseStoreModel;

/**
 * Test for {@DefaultQuoteEntryContributor}
 */
@UnitTest
public class DefaultQuoteEntryContributorTest {

    private static final int VERSION = 5;
	@InjectMocks
    private DefaultQuoteEntryContributor contributor = new DefaultQuoteEntryContributor();
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
    private CommentModel comment;
    @Mock
    private ProductModel product;
    @Mock
    private UnitModel unit;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockBasicQuoteEntryFields();
    }

    @Test
    public void testCreateRowsWithLocale() {

        when(quoteModel.getLocale()).thenReturn("EN");

        when(product.getName(any(Locale.class))).thenReturn(null);

        List<Map<String, Object>> results = contributor.createRows(quoteModel);
        assertFalse(results.isEmpty());
        Assert.assertEquals(1, results.size());
        Assert.assertEquals(DUMMY_TEXT, results.get(0).get(QuoteCsvColumns.QUOTE_ID));
        Assert.assertNotNull(results.get(0).get(QuoteEntryCsvColumns.ENTRY_UNIT_CODE));
        Assert.assertEquals("", results.get(0).get(QuoteEntryCsvColumns.PRODUCT_NAME));
    }

    @Test
    public void testGetColumns() {
        Set<String> results = contributor.getColumns();
        assertFalse(results.isEmpty());
    }

    private void mockBasicQuoteEntryFields() {
        when(quoteModel.getCode()).thenReturn(DUMMY_TEXT);
        when(quoteModel.getCreationtime()).thenReturn(new Date());
        when(quoteModel.getEntries()).thenReturn(Arrays.asList(entry));
        when(quoteModel.getVersion()).thenReturn(new Integer(VERSION));
        when(quoteModel.getState()).thenReturn(QuoteState.CREATED);
        when(entry.getQuantity()).thenReturn(DUMMY_QUANTITY);
        when(entry.getEntryNumber()).thenReturn(10);

        when(product.getCode()).thenReturn(DUMMY_TEXT);
        when(entry.getProduct()).thenReturn(product);
        when(product.getName(any(Locale.class))).thenReturn(DUMMY_TEXT);

        when(entry.getCreationtime()).thenReturn(new Date());

        when(entry.getUnit()).thenReturn(unit);
        when(unit.getCode()).thenReturn(DUMMY_TEXT);

        when(quoteModel.getStore()).thenReturn(baseStoreModel);
        when(baseStoreModel.getSAPConfiguration()).thenReturn(sapConfigurationModel);

        when(baseStoreModel.getDefaultLanguage()).thenReturn(languageModel);
        when(languageModel.getIsocode()).thenReturn("EN");

    }
}
