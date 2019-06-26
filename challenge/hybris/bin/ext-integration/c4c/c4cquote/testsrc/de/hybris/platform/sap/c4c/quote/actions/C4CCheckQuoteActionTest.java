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
package de.hybris.platform.sap.c4c.quote.actions;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.model.process.QuoteProcessModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.order.QuoteService;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction.Transition;
import de.hybris.platform.sap.orderexchange.outbound.SendToDataHubHelper;
import de.hybris.platform.sap.orderexchange.outbound.SendToDataHubResult;
import de.hybris.platform.task.RetryLaterException;

/**
 * Test for {@C4CCheckQuoteAction}
 */
@UnitTest
public class C4CCheckQuoteActionTest {
    @InjectMocks
    private C4CCheckQuoteAction action = new C4CCheckQuoteAction();
    @Mock
    private SendToDataHubHelper<QuoteModel> sendQuoteToDataHubHelper;
    @Mock
    private QuoteService quoteService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testExecuteActionWithoutQuoteCode() throws RetryLaterException, Exception {
        QuoteProcessModel process = mock(QuoteProcessModel.class);
        Transition result = action.executeAction(process);
        Assert.assertEquals(Transition.NOK, result);

    }

    @Test
    public void testExecuteActionWithQuoteCode() throws RetryLaterException, Exception {
        QuoteProcessModel process = mock(QuoteProcessModel.class);
        when(process.getQuoteCode()).thenReturn("TestCode");
        QuoteModel model = mock(QuoteModel.class);
        when(quoteService.getCurrentQuoteForCode("TestCode")).thenReturn(model);
        SendToDataHubResult sendToDatahubResult = mock(SendToDataHubResult.class);
        when(sendQuoteToDataHubHelper.createAndSendRawItem(model)).thenReturn(sendToDatahubResult);
        when(sendToDatahubResult.isSuccess()).thenReturn(Boolean.TRUE);
        Transition result = action.executeAction(process);
        Assert.assertEquals(Transition.OK, result);

    }

    @Test
    public void testExecuteActionWithDatahubFail() throws RetryLaterException, Exception {
        QuoteProcessModel process = mock(QuoteProcessModel.class);
        when(process.getQuoteCode()).thenReturn("TestCode");
        QuoteModel model = mock(QuoteModel.class);
        when(quoteService.getCurrentQuoteForCode("TestCode")).thenReturn(model);
        SendToDataHubResult sendToDatahubResult = mock(SendToDataHubResult.class);
        when(sendQuoteToDataHubHelper.createAndSendRawItem(model)).thenReturn(sendToDatahubResult);
        when(sendToDatahubResult.isSuccess()).thenReturn(Boolean.FALSE);
        Transition result = action.executeAction(process);
        Assert.assertEquals(Transition.NOK, result);

    }
}
