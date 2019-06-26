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
package de.hybris.platform.sap.sapcpicustomerexchange.service.impl;

import com.sap.hybris.sapcustomerb2c.outbound.CustomerExportService;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundCustomerModel;
import de.hybris.platform.sap.sapcpiadapter.service.SapCpiOutboundService;
import de.hybris.platform.sap.sapcpicustomerexchange.service.SapCpiCustomerConversionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import static de.hybris.platform.sap.sapcpiadapter.service.SapCpiOutboundService.*;

/**
 * SapCpiCustomerOutboundService
 */
public class SapCpiCustomerOutboundService extends CustomerExportService {

  private static final Logger LOG = LoggerFactory.getLogger(SapCpiCustomerOutboundService.class);

  private SapCpiCustomerConversionService sapCpiCustomerConversionService;
  private SapCpiOutboundService sapCpiOutboundService;

  @Override
  public void sendCustomerData(final CustomerModel customerModel, final String baseStoreUid, final String sessionLanguage, final AddressModel addressModel) {

    final SAPCpiOutboundCustomerModel sapCpiOutboundCustomer = getSapCpiCustomerConversionService()
            .convertCustomerToSapCpiCustomer(customerModel, addressModel, baseStoreUid, sessionLanguage);

    if (sapCpiOutboundCustomer == null) {
      final String message = String.format("Could not set complete attributes for customer %s", customerModel.getCustomerID());
      LOG.error(message);
      return;
    }

    getSapCpiOutboundService().sendCustomer(sapCpiOutboundCustomer).subscribe(

            // onNext
            responseEntityMap -> {

              if (isSentSuccessfully(responseEntityMap)) {

                LOG.info(String.format("The customer [%s] has been sent to the SAP backend through SCPI! %n%s",
                        sapCpiOutboundCustomer.getCustomerId(), getPropertyValue(responseEntityMap, RESPONSE_MESSAGE)));

              } else {

                LOG.error(String.format("The customer [%s] has not been sent to the SAP backend! %n%s",
                        sapCpiOutboundCustomer.getCustomerId(), getPropertyValue(responseEntityMap, RESPONSE_MESSAGE)));

              }

            }

            // onError
            , error -> LOG.error(String.format("The customer [%s] has not been sent to the SAP backend through SCPI! %n%s", sapCpiOutboundCustomer.getCustomerId(), error.getMessage()))

    );

  }

  public SapCpiOutboundService getSapCpiOutboundService() {
    return sapCpiOutboundService;
  }

  @Required
  public void setSapCpiOutboundService(SapCpiOutboundService sapCpiOutboundService) {
    this.sapCpiOutboundService = sapCpiOutboundService;
  }

  protected SapCpiCustomerConversionService getSapCpiCustomerConversionService() {
    return sapCpiCustomerConversionService;
  }

  @Required
  public void setSapCpiCustomerConversionService(SapCpiCustomerConversionService sapCpiCustomerConversionService) {
    this.sapCpiCustomerConversionService = sapCpiCustomerConversionService;
  }

}
