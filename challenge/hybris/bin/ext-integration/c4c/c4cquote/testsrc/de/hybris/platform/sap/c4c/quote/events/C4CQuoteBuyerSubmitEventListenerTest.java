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
package de.hybris.platform.sap.c4c.quote.events;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.enums.QuoteUserType;
import de.hybris.platform.commerceservices.model.process.QuoteProcessModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.store.BaseStoreModel;

/**
 * Test for {@C4CQuoteBuyerSubmitEventListener}
 */
@UnitTest
public class C4CQuoteBuyerSubmitEventListenerTest {

    @InjectMocks
    private C4CQuoteBuyerSubmitEventListener listner = new C4CQuoteBuyerSubmitEventListener();
    @Mock
    private BusinessProcessService businessProcessService;
    @Mock
    private ModelService modelService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testOnEvent() {
   	 C4CQuoteBuyerSubmitEvent event = mock(C4CQuoteBuyerSubmitEvent.class);

        QuoteUserType quoteUserType = mock(QuoteUserType.class);
        when(event.getQuoteUserType()).thenReturn(quoteUserType);

        QuoteModel quote = mock(QuoteModel.class);
        when(event.getQuote()).thenReturn(quote);
        when(quote.getCode()).thenReturn("DummyCode");

        QuoteProcessModel quoteBuyerProcessModel = mock(QuoteProcessModel.class);
        when(businessProcessService.createProcess(anyString(), anyString(), any(Map.class)))
                .thenReturn(quoteBuyerProcessModel);

        BaseStoreModel store = mock(BaseStoreModel.class);
        when(quote.getStore()).thenReturn(store);
        when(store.getUid()).thenReturn("DummyStoreUID");
        try {
            listner.onEvent(event);
        } catch (Exception e) {
            Assert.fail("Exception Occured");
        }
    }
}
