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

package de.hybris.platform.sap.sapcpicustomerexchangeb2b.outbound.services;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundB2BCustomerModel;

/**
 * Convert Hybris B2B Customer to SCPI B2B Customer.
 */
public interface SapCpiB2BCustomerConversionService {

  /**
   * Convert Hybris B2B Customer to SCPI B2B Customers.
   *
   * @param b2bCustomerModel B2BCustomerModel
   * @param sessionLanguage String
   * @return SAPCpiOutboundB2BCustomerModel
   */
  SAPCpiOutboundB2BCustomerModel convertB2BCustomerToSapCpiBb2BCustomer(B2BCustomerModel b2bCustomerModel, String sessionLanguage);

}
