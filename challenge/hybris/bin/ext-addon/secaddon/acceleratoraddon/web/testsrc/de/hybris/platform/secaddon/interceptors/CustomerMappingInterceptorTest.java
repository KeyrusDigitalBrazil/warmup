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
package de.hybris.platform.secaddon.interceptors;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.commercefacades.storesession.StoreSessionFacade;
import de.hybris.platform.secaddon.data.CustomerInfo;
import de.hybris.platform.secaddon.exception.CustomerMappingException;
import de.hybris.platform.secaddon.services.CustomerMappingAdapter;
import de.hybris.platform.servicelayer.session.SessionService;
import org.apache.commons.collections.ListUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.servlet.ModelAndView;
import rx.Observable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

import static de.hybris.platform.secaddon.constants.SecaddonConstants.MAPPED_CUSTOMER_ID;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@UnitTest
public class CustomerMappingInterceptorTest
{
    private static final String HYBRIS_CUSTOMER_ID = "hybrisCustomerId";
    private static final String ISO_CODE = "isoCode";
    private static final String CUSTOMER_NUMBER = "customerNumber";
    @Mock
    private SessionService sessionService;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private StoreSessionFacade storeSessionFacade;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private CustomerFacade customerFacade;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private CustomerMappingAdapter customerMappingAdapter;
    @Mock
    HttpServletResponse response;
    @Mock
    HttpServletRequest request;
    @Mock
    Object handler;
    @Mock
    ModelAndView modelAndView;
    @InjectMocks
    CustomerMappingInterceptor customerMappingInterceptor;

    @Before
    public void setUp()
    {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldAddCustomerMappedIdToTheSession() throws Exception
    {
        //given
        given(sessionService.getAttribute(MAPPED_CUSTOMER_ID)).willReturn(null);
        given(customerFacade.getCurrentCustomer().getCustomerId()).willReturn(HYBRIS_CUSTOMER_ID);
        given(storeSessionFacade.getCurrentLanguage().getIsocode()).willReturn(ISO_CODE);
        List<CustomerInfo> customerInfo = Arrays.asList(getCustomerInfo());
        given(customerMappingAdapter.getCustomer(ISO_CODE, HYBRIS_CUSTOMER_ID)).willReturn(Observable.just(customerInfo));

        //when
        customerMappingInterceptor.postHandle(request, response, handler,modelAndView);

        //then
        verify(sessionService).setAttribute(MAPPED_CUSTOMER_ID, CUSTOMER_NUMBER);
    }

    @Test
    public void shouldNotAddCustomerMappedWhenNoMappingFound() throws Exception
    {
        //given
        given(sessionService.getAttribute(MAPPED_CUSTOMER_ID)).willReturn(null);
        given(customerFacade.getCurrentCustomer().getCustomerId()).willReturn(HYBRIS_CUSTOMER_ID);
        given(storeSessionFacade.getCurrentLanguage().getIsocode()).willReturn(ISO_CODE);
        List<CustomerInfo> customerInfo = ListUtils.EMPTY_LIST;
        given(customerMappingAdapter.getCustomer(ISO_CODE, HYBRIS_CUSTOMER_ID)).willReturn(Observable.just(customerInfo));

        //when
        customerMappingInterceptor.postHandle(request, response, handler,modelAndView);

        //then
        verify(sessionService,never()).setAttribute(MAPPED_CUSTOMER_ID, CUSTOMER_NUMBER);
    }

    @Test
    public void shouldNotAddCustomerMappedWhenHybrisCustomerIdNull() throws Exception
    {
        //given
        given(sessionService.getAttribute(MAPPED_CUSTOMER_ID)).willReturn(null);
        given(customerFacade.getCurrentCustomer().getCustomerId()).willReturn(null);

        //when
        customerMappingInterceptor.postHandle(request, response, handler,modelAndView);

        //then
        verify(sessionService,never()).setAttribute(MAPPED_CUSTOMER_ID, CUSTOMER_NUMBER);
    }

    @Test
    public void shouldNoAddCustomerMappedWhenAlreadyInSession() throws Exception
    {
        //given
        given(sessionService.getAttribute(MAPPED_CUSTOMER_ID)).willReturn("customerMappedId");

        //when
        customerMappingInterceptor.postHandle(request, response, handler,modelAndView);

        //then
        verify(sessionService, never()).setAttribute(MAPPED_CUSTOMER_ID, CUSTOMER_NUMBER);
    }

    protected CustomerInfo getCustomerInfo()
    {
        CustomerInfo customerInfo = new CustomerInfo();
        customerInfo.setCustomerNumber(CUSTOMER_NUMBER);
        return customerInfo;
    }
}
