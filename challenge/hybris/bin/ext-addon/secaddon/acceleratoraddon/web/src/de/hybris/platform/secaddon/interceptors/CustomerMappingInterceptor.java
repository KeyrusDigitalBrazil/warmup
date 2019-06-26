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


import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.commercefacades.storesession.StoreSessionFacade;
import de.hybris.platform.secaddon.data.CustomerInfo;
import de.hybris.platform.secaddon.exception.CustomerMappingException;
import de.hybris.platform.secaddon.services.CustomerMappingAdapter;
import de.hybris.platform.servicelayer.session.SessionService;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.List;

import static de.hybris.platform.secaddon.constants.SecaddonConstants.MAPPED_CUSTOMER_ID;

public class CustomerMappingInterceptor extends HandlerInterceptorAdapter
{

    public static final String NOCUSTOMERMAPPING = "nocustomermapping";
    private SessionService sessionService;
    private StoreSessionFacade storeSessionFacade;
    private CustomerFacade customerFacade;
    private CustomerMappingAdapter customerMappingAdapter;
    private static final Logger LOG = Logger.getLogger(CustomerMappingInterceptor.class);

    @Override
    public void postHandle(HttpServletRequest request,
                           HttpServletResponse response, Object handler, ModelAndView model) throws Exception
    {
        tryAddCustomerIdIntoSession(model);
    }

    protected void tryAddCustomerIdIntoSession(ModelAndView model)
    {
        if (noCustomerMappedId())
        {
            try
            {
                addCustomerMappedIdIntoSession();

            } catch (CustomerMappingException exc)
            {
                // exception is logged
                model.addObject(NOCUSTOMERMAPPING, Boolean.TRUE);
                LOG.error("There was a problem while obtaining  Customer  ID", exc);
            }
        }
    }

    protected boolean noCustomerMappedId()
    {
        return sessionService.getAttribute(MAPPED_CUSTOMER_ID) == null;
    }

    protected void addCustomerMappedIdIntoSession()
    {
        String mappedCustomerID = fetchCustomerMappedId(getHybrisCustomerId());
        if (!StringUtils.isBlank(mappedCustomerID))
        {
            sessionService.setAttribute(MAPPED_CUSTOMER_ID, mappedCustomerID);
        }
    }

    protected String getHybrisCustomerId()
    {
        return customerFacade.getCurrentCustomer().getCustomerId();
    }

    protected String fetchCustomerMappedId(String hybrisCustomerID)
    {
        validateHybrisCustomerId(hybrisCustomerID);
        List<CustomerInfo> customerInfos = getCustomerInfos(hybrisCustomerID);
        return customerInfos.stream()
                .findFirst()
                .get()
                .getCustomerNumber();
    }

    protected void validateHybrisCustomerId(String hybrisCustomerID)
    {
        if (hybrisCustomerID == null)
        {
            throw new CustomerMappingException();
        }
    }

    protected List<CustomerInfo> getCustomerInfos( String hybrisCustomerID)
    {
        List<CustomerInfo> customerInfos = customerMappingAdapter.getCustomer(getCurrentLanguage(), hybrisCustomerID).toBlocking().first();
        if (customerInfos.isEmpty())
        {
            throw new CustomerMappingException();
        }
        return customerInfos;
    }

    protected String getCurrentLanguage()
    {
        return storeSessionFacade.getCurrentLanguage().getIsocode();
    }

    @Required
    public void setSessionService(SessionService sessionService)
    {
        this.sessionService = sessionService;
    }

    @Required
    public void setCustomerMappingAdapter(CustomerMappingAdapter customerMappingAdapter)
    {
        this.customerMappingAdapter = customerMappingAdapter;
    }

    @Required
    public void setStoreSessionFacade(StoreSessionFacade storeSessionFacade)
    {
        this.storeSessionFacade = storeSessionFacade;
    }

    @Required
    public void setCustomerFacade(CustomerFacade customerFacade)
    {
        this.customerFacade = customerFacade;
    }
}