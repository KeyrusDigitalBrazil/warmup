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
package de.hybris.platform.assistedserviceservices.impl;

import de.hybris.platform.assistedserviceservices.AssistedServiceService;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.customersupport.CommerceCustomerSupportService;
import org.springframework.beans.factory.annotation.Required;


/**
 * This implementation returns actual data from {@link de.hybris.platform.assistedserviceservices.impl.DefaultAssistedServiceService}.
 *
 */
public class DefaultCommerceCustomerSupportService implements CommerceCustomerSupportService
{
    private AssistedServiceService assistedServiceService;

    @Override
    public boolean isCustomerSupportAgentActive()
    {
        return getAgent() != null;
    }

    @Override
    public UserModel getEmulatedCustomer()
    {
        return getAssistedServiceService().getAsmSession() != null ? getAssistedServiceService().getAsmSession()
                .getEmulatedCustomer() : null;
    }

    @Override
    public UserModel getAgent()
    {
        return getAssistedServiceService().getAsmSession() != null ? getAssistedServiceService().getAsmSession().getAgent() : null;
    }

    protected AssistedServiceService getAssistedServiceService()
    {
        return assistedServiceService;
    }

    @Required
    public void setAssistedServiceService(final AssistedServiceService assistedServiceService)
    {
        this.assistedServiceService = assistedServiceService;
    }

}
