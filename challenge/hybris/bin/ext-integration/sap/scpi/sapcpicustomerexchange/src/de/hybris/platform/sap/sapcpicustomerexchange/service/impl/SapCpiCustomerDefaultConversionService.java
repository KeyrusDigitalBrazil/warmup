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

import com.sap.hybris.sapcustomerb2c.constants.Sapcustomerb2cConstants;
import de.hybris.platform.commerceservices.strategies.CustomerNameStrategy;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.sap.core.configuration.global.dao.SAPGlobalConfigurationDAO;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundConfigModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundCustomerModel;
import de.hybris.platform.sap.sapcpicustomerexchange.service.SapCpiCustomerConversionService;
import de.hybris.platform.sap.sapmodel.model.SAPLogicalSystemModel;
import de.hybris.platform.servicelayer.model.ModelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import java.util.Set;

import static com.sap.hybris.sapcustomerb2c.constants.Sapcustomerb2cConstants.ADDRESSUSAGE_DE;
import static com.sap.hybris.sapcustomerb2c.constants.Sapcustomerb2cConstants.OBJTYPE_KNA1;

/**
 * SapCpiCustomerDefaultConversionService
 */
public class SapCpiCustomerDefaultConversionService implements SapCpiCustomerConversionService {

  private static final Logger LOG = LoggerFactory.getLogger(SapCpiCustomerDefaultConversionService.class);

  private SAPGlobalConfigurationDAO globalConfigurationDAO;
  private ModelService modelService;
  private CustomerNameStrategy customerNameStrategy;

  @Override
  public SAPCpiOutboundCustomerModel convertCustomerToSapCpiCustomer(CustomerModel customerModel, AddressModel addressModel, String baseStoreUid, String sessionLanguage) {

    final SAPCpiOutboundCustomerModel sapCpiOutboundCustomer = getModelService().create(SAPCpiOutboundCustomerModel.class);

    final Set<SAPLogicalSystemModel> logicalSystems = getGlobalConfigurationDAO().getSAPGlobalConfiguration().getSapLogicalSystemGlobalConfig();

    if (logicalSystems == null || logicalSystems.isEmpty()) {

      LOG.error("No Logical system is maintained in back-office");
      return null;

    }

    final SAPLogicalSystemModel logicalSystem = logicalSystems.stream().filter(ls -> ls.isDefaultLogicalSystem()).findFirst().orElse(null);

    if (logicalSystem == null) {
      LOG.error("No Default Logical system is maintained in back-office ");

      return null;
    }

    // Configuration
    final SAPCpiOutboundConfigModel config = getModelService().create(SAPCpiOutboundConfigModel.class);

    config.setReceiverName(logicalSystem.getSapLogicalSystemName());
    config.setReceiverPort(logicalSystem.getSapLogicalSystemName());
    config.setSenderName(logicalSystem.getSenderName());
    config.setSenderPort(logicalSystem.getSenderPort());
    config.setUrl(logicalSystem.getSapHTTPDestination().getTargetURL());
    config.setUsername(logicalSystem.getSapHTTPDestination().getUserid());

    sapCpiOutboundCustomer.setSapCpiConfig(config);

    // Customer
    final String[] names = getCustomerNameStrategy().splitName(customerModel.getName());
    sapCpiOutboundCustomer.setUid(customerModel.getUid());
    sapCpiOutboundCustomer.setCustomerId(customerModel.getCustomerID());
    sapCpiOutboundCustomer.setContactId(customerModel.getSapContactID());
    sapCpiOutboundCustomer.setFirstName(names[0]);
    sapCpiOutboundCustomer.setLastName(names[1]);
    sapCpiOutboundCustomer.setSessionLanguage(sessionLanguage);
    sapCpiOutboundCustomer.setTitle(customerModel.getTitle().getName());
    sapCpiOutboundCustomer.setBaseStore(baseStoreUid);
    sapCpiOutboundCustomer.setObjType(OBJTYPE_KNA1);
    sapCpiOutboundCustomer.setAddressUsage(ADDRESSUSAGE_DE);

    if (addressModel == null) {
      sapCpiOutboundCustomer.setCountry(Sapcustomerb2cConstants.COUNTRY_DE);
      return sapCpiOutboundCustomer;
    }

    // Address
    final String countryIsoCode = addressModel.getCountry() != null ? addressModel.getCountry().getIsocode() : null;
    sapCpiOutboundCustomer.setCountry(countryIsoCode);
    sapCpiOutboundCustomer.setStreet(addressModel.getStreetname());
    sapCpiOutboundCustomer.setPhone(addressModel.getPhone1());
    sapCpiOutboundCustomer.setFax(addressModel.getFax());
    sapCpiOutboundCustomer.setTown(addressModel.getTown());
    sapCpiOutboundCustomer.setPostalCode(addressModel.getPostalcode());
    sapCpiOutboundCustomer.setStreetNumber(addressModel.getStreetnumber());

    final String regionIsoCode = addressModel.getRegion() != null ? addressModel.getRegion().getIsocodeShort() : null;
    sapCpiOutboundCustomer.setRegion(regionIsoCode);

    return sapCpiOutboundCustomer;

  }

  protected SAPGlobalConfigurationDAO getGlobalConfigurationDAO() {
    return globalConfigurationDAO;
  }

  @Required
  public void setGlobalConfigurationDAO(SAPGlobalConfigurationDAO globalConfigurationDAO) {
    this.globalConfigurationDAO = globalConfigurationDAO;
  }

  protected ModelService getModelService() {
    return modelService;
  }

  @Required
  public void setModelService(ModelService modelService) {
    this.modelService = modelService;
  }

  protected CustomerNameStrategy getCustomerNameStrategy() {
    return customerNameStrategy;
  }

  @Required
  public void setCustomerNameStrategy(CustomerNameStrategy customerNameStrategy) {
    this.customerNameStrategy = customerNameStrategy;
  }

}
