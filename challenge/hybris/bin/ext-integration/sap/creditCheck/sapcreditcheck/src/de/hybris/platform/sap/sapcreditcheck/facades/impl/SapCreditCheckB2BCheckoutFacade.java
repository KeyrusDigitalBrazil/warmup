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
package de.hybris.platform.sap.sapcreditcheck.facades.impl;

import static de.hybris.platform.util.localization.Localization.getLocalizedString;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import de.hybris.platform.b2bacceleratorfacades.checkout.data.PlaceOrderData;
import de.hybris.platform.b2bacceleratorfacades.exception.EntityValidationException;
import de.hybris.platform.b2bacceleratorfacades.order.impl.DefaultB2BCheckoutFacade;
import de.hybris.platform.commercefacades.order.data.AbstractOrderData;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.sap.sapcreditcheck.constants.SapcreditcheckConstants;
import de.hybris.platform.sap.sapcreditcheck.service.SapCreditCheckService;

/**
 * SapCreditCheckB2BOrderFacade for saporder related facilities
 */
public class SapCreditCheckB2BCheckoutFacade extends DefaultB2BCheckoutFacade {

	static final private Logger sapLogger = Logger.getLogger(SapCreditCheckB2BCheckoutFacade.class.getName());
	private SapCreditCheckService sapCreditCheckService;// NOPMD

	public SapCreditCheckService getSapCreditCheckService() {
		return sapCreditCheckService;
	}

	@Required
	public void setSapCreditCheckService(SapCreditCheckService sapCreditCheckService) {
		this.sapCreditCheckService = sapCreditCheckService;
	}

	@Override
	public <T extends AbstractOrderData> T placeOrder(PlaceOrderData placeOrderData) throws InvalidCartException {
		CartData cartData = getCheckoutCart();
		Boolean creditExceeded = false;
		
		try {
			
			creditExceeded = getSapCreditCheckService().checkCreditLimitExceeded(cartData);
						
		} catch (Exception ex) {
		        sapLogger.info(ex);
			sapLogger.error(String.format("An exception was thrown while checking the credit limit for the order %s from the ERP backend! ", cartData.getCode()) + ex.getMessage());			
			throw new EntityValidationException(getLocalizedString(SapcreditcheckConstants.CART_CHECKOUT_TECHNICAL_ERROR));
			
		}
		
		if (creditExceeded) {
			throw new EntityValidationException(getLocalizedString(SapcreditcheckConstants.CART_CHECKOUT_CREDITCHECK_EXCEEDED));
		}

		return super.placeOrder(placeOrderData);
	}

}
