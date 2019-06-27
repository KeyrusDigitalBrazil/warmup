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
package de.hybris.platform.assistedserviceservices.exception;


/**
 * Exception for the {@link AssistedServiceFacade} which is used when a cart is binded to a emulated customer.
 */
public class AssistedServiceCustomerLoginDisabledException extends AssistedServiceException{
    public AssistedServiceCustomerLoginDisabledException(String message) {
        super(message);
    }

    public AssistedServiceCustomerLoginDisabledException(String message, Throwable e) {
        super(message, e);
    }

    @Override
    public String getMessageCode()
    {
        return "asm.emulate.error.customer_login_disabled";
    }

    @Override
    public String getAlertClass()
    {
        return ASM_ALERT_CUSTOMER;
    }
}
