/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.acceleratorfacades.flow;

import de.hybris.platform.acceleratorfacades.order.AcceleratorCheckoutFacade;
import de.hybris.platform.acceleratorservices.enums.CheckoutPciOptionEnum;


/**
 * CheckoutFlowFacade interface extends the {@link AcceleratorCheckoutFacade}.
 *
 * @since 4.6
 * @spring.bean checkoutFacade
 */
public interface CheckoutFlowFacade extends AcceleratorCheckoutFacade
{

	/**
	 * Gets the subscription pci option
	 *
	 * @return the pci option
	 */
	CheckoutPciOptionEnum getSubscriptionPciOption();
}
