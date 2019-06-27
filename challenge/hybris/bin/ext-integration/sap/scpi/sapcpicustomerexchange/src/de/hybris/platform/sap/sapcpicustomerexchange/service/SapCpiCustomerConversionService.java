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
package de.hybris.platform.sap.sapcpicustomerexchange.service;

import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundCustomerModel;

/**
 * SapCpiCustomerConversionService
 */
public interface SapCpiCustomerConversionService {

  /**
   * convertCustomerToSapCpiCustomer
   * @param customerModel CustomerModel
   * @param addressModel AddressModel
   * @param baseStoreUid String
   * @param sessionLanguage String
   * @return SAPCpiOutboundCustomerModel
   */
  SAPCpiOutboundCustomerModel convertCustomerToSapCpiCustomer(CustomerModel customerModel, AddressModel addressModel,
                                                              String baseStoreUid, String sessionLanguage);

}
