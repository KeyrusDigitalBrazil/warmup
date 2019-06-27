package com.sap.hybris.sapquoteintegration.outbound.hook;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.sap.hybris.sapquoteintegration.events.SapCpiQuoteOrderPlacedEvent;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.commerceservices.service.data.CommerceOrderResult;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.servicelayer.event.EventService;

@UnitTest
public class SapCpiQuoteOrderMethodHookTest {

    @InjectMocks
    private SapCpiQuoteOrderMethodHook placeQuoteOrderMethodHook = new SapCpiQuoteOrderMethodHook();

    @Mock
    private EventService eventService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testAfterPlaceOrder() {
        doNothing().when(eventService).publishEvent(Mockito.anyObject());

        QuoteModel quote = new QuoteModel();
        quote.setCode("123456");
        OrderModel order = new OrderModel();
        order.setCode("12345");
        order.setQuoteReference(quote);

        CommerceOrderResult orderResult = new CommerceOrderResult();
        orderResult.setOrder(order);
        CommerceCheckoutParameter commerceCheckoutParameter = new CommerceCheckoutParameter();

        placeQuoteOrderMethodHook.afterPlaceOrder(commerceCheckoutParameter, orderResult);

        verify(eventService, times(1)).publishEvent(Mockito.any(SapCpiQuoteOrderPlacedEvent.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAfterPlaceOrderWithOrderNull() {

        CommerceOrderResult orderResult = new CommerceOrderResult();
        orderResult.setOrder(null);
        CommerceCheckoutParameter commerceCheckoutParameter = new CommerceCheckoutParameter();

        placeQuoteOrderMethodHook.afterPlaceOrder(commerceCheckoutParameter, orderResult);

        verify(eventService, times(0)).publishEvent(Mockito.any(SapCpiQuoteOrderPlacedEvent.class));
    }

}
