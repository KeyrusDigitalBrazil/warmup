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

import static com.sap.hybris.sapcustomerb2c.constants.Sapcustomerb2cConstants.ADDRESSUSAGE_DE;
import static com.sap.hybris.sapcustomerb2c.constants.Sapcustomerb2cConstants.OBJTYPE_KNA1;

import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.sap.core.configuration.global.dao.SAPGlobalConfigurationDAO;
import de.hybris.platform.sap.sapcpiadapter.data.SapCpiConfig;
import de.hybris.platform.sap.sapcpiadapter.data.SapCpiCustomer;
import de.hybris.platform.sap.sapcpiadapter.data.SapCpiTargetSystem;
import de.hybris.platform.sap.sapcpicustomerexchange.service.SapCpiCustomerService;
import de.hybris.platform.sap.sapmodel.model.SAPLogicalSystemModel;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.sap.hybris.sapcustomerb2c.constants.Sapcustomerb2cConstants;
import com.sap.hybris.sapcustomerb2c.outbound.CustomerExportService;

/**
 * SapCpiCustomerExportService
 */
public class SapCpiCustomerExportService extends CustomerExportService {

  private static final Logger LOG = LoggerFactory.getLogger(DefaultSapCpiCustomerService.class);
  private SapCpiCustomerService sapCpiCustomerService;
  private SAPGlobalConfigurationDAO globalConfigurationDAO = null;

  @Override
  public void sendCustomerData(final CustomerModel customerModel, final String baseStoreUid, final String sessionLanguage, final AddressModel addressModel) {

    final SapCpiCustomer customer = createCustomer(customerModel, baseStoreUid, sessionLanguage, addressModel);

    if (customer == null) {
      final String message = String.format("Could not set complete attributes for customer %s", customerModel.getCustomerID());
      LOG.error(message);
      return;
    }

    getSapCpiCustomerService().createCustomer(customer)
            .subscribe(err -> LOG.error(err.toString()),
                    () -> {
                      final String message = String.format("Customer %s sent successfully.", customer.getCustomerID());
                      LOG.info(message);
                    });
  }

  protected SapCpiCustomer createCustomer(final CustomerModel customerModel, final String baseStoreUid,
                                          final String sessionLanguage, final AddressModel addressModel) {
    final SapCpiCustomer customer = new SapCpiCustomer();

    final Set<SAPLogicalSystemModel> logicalSystems = getGlobalConfigurationDAO().getSAPGlobalConfiguration().getSapLogicalSystemGlobalConfig();

    if (logicalSystems == null || logicalSystems.isEmpty()) {
      LOG.error("No Logical system is maintained in backoffice");

      return null;
    }

    final SAPLogicalSystemModel logicalSystem = logicalSystems.stream().filter(ls -> ls.isDefaultLogicalSystem()).findFirst().orElse(null);

    if (logicalSystem == null) {
      LOG.error("No Default Logical system is maintained in backoffice ");

      return null;
    }

    // configuration
    final SapCpiConfig config = new SapCpiConfig();
    final SapCpiTargetSystem target = new SapCpiTargetSystem();
    target.setReceiverName(logicalSystem.getSapLogicalSystemName());
    target.setReceiverPort(logicalSystem.getSapLogicalSystemName());
    target.setSenderName(logicalSystem.getSenderName());
    target.setSenderPort(logicalSystem.getSenderPort());
    target.setUrl(logicalSystem.getSapHTTPDestination().getTargetURL());
    target.setUsername(logicalSystem.getSapHTTPDestination().getUserid());

    config.setSapCpiTargetSystem(target);
    customer.setSapCpiConfig(config);

    // customer
    final String[] names = getCustomerNameStrategy().splitName(customerModel.getName());
    customer.setUid(customerModel.getUid());
    customer.setCustomerID(customerModel.getCustomerID());
    customer.setContactID(customerModel.getSapContactID());
    customer.setFirstName(names[0]);
    customer.setLastName(names[1]);
    customer.setSessionLanguage(sessionLanguage);
    customer.setTitle(customerModel.getTitle().getName());
    customer.setBaseStore(baseStoreUid);
    customer.setObjType(OBJTYPE_KNA1);
    customer.setAddressUsage(ADDRESSUSAGE_DE);

    if (addressModel == null) {
      customer.setCountry(Sapcustomerb2cConstants.COUNTRY_DE);

      return customer;
    }

    //address
    final String countryIsoCode = addressModel.getCountry() != null ? addressModel.getCountry().getIsocode() : null;
    customer.setCountry(countryIsoCode);
    customer.setStreet(addressModel.getStreetname());
    customer.setPhone(addressModel.getPhone1());
    customer.setFax(addressModel.getFax());
    customer.setTown(addressModel.getTown());
    customer.setPostalCode(addressModel.getPostalcode());
    customer.setStreetNumber(addressModel.getStreetnumber());

    final String regionIsoCode = addressModel.getRegion() != null ? addressModel.getRegion().getIsocodeShort() : null;
    customer.setRegion(regionIsoCode);

    return customer;

  }

  protected SAPGlobalConfigurationDAO getGlobalConfigurationDAO() {
    return globalConfigurationDAO;
  }

  @Required
  public void setGlobalConfigurationDAO(final SAPGlobalConfigurationDAO globalConfigurationDAO) {
    this.globalConfigurationDAO = globalConfigurationDAO;
  }

  protected SapCpiCustomerService getSapCpiCustomerService() {
    return sapCpiCustomerService;
  }

  @Required
  public void setSapCpiCustomerService(final SapCpiCustomerService sapCpiCustomerService) {
    this.sapCpiCustomerService = sapCpiCustomerService;
  }

}
