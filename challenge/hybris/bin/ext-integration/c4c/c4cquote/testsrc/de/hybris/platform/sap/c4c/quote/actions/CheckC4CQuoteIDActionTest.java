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

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.model.process.QuoteProcessModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.order.QuoteService;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction.Transition;

@UnitTest
public class CheckC4CQuoteIDActionTest
{
	@InjectMocks
	private CheckC4CQuoteIDAction checkC4CQuoteIDAction = new CheckC4CQuoteIDAction();	
	@Mock
	private QuoteService quoteService;
		
	@Before
   public void setUp() 
   {
       MockitoAnnotations.initMocks(this);
   }
	
	@Test
   public void testExecuteActionWithC4CQuoteID() 
   {
		QuoteProcessModel processModel = mock(QuoteProcessModel.class);
		QuoteModel quoteModel = mock(QuoteModel.class);
		when(processModel.getQuoteCode()).thenReturn("12345");
		when(quoteService.getCurrentQuoteForCode(Mockito.anyString())).thenReturn(quoteModel);
		when(quoteModel.getC4cQuoteId()).thenReturn("1234");
		Transition result = checkC4CQuoteIDAction.executeAction(processModel);
		
		Assert.assertEquals(Transition.OK, result);
				
	}
	
	@Test
   public void testExecuteActionWithoutC4CQuoteID() 
   {
		QuoteProcessModel processModel = mock(QuoteProcessModel.class);
		QuoteModel quoteModel = mock(QuoteModel.class);
		when(processModel.getQuoteCode()).thenReturn("12345");
		when(quoteService.getCurrentQuoteForCode(Mockito.anyString())).thenReturn(quoteModel);
		when(quoteModel.getC4cQuoteId()).thenReturn(null);
		Transition result = checkC4CQuoteIDAction.executeAction(processModel);
		
		Assert.assertEquals(Transition.NOK, result);
	}
}