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
package com.sap.hybris.sec.eventpublisher.b2b.populator;

import com.sap.hybris.sec.eventpublisher.populator.SECAddressPopulator;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.user.AddressModel;


/**
 *
 */
public class SECB2BAddressPopulator extends SECAddressPopulator
{
        protected String getModelId(final AddressModel customerAddressModel){
                String hybrisCustomerId = null;
                ItemModel owner = customerAddressModel.getOwner();
                if(owner instanceof B2BUnitModel || owner instanceof B2BCustomerModel){
                        hybrisCustomerId = owner.getPk().toString();
                }else {
                	   return super.getModelId(customerAddressModel);
                }
                return hybrisCustomerId;
        }

        protected boolean isDefaultAddress(final AddressModel customerAddressModel){
                return false;
        }
}
