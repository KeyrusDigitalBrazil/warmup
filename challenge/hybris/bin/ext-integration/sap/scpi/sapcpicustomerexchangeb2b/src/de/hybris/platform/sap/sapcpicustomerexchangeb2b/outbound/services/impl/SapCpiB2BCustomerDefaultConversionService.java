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

package de.hybris.platform.sap.sapcpicustomerexchangeb2b.outbound.services.impl;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.commerceservices.strategies.CustomerNameStrategy;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.sap.core.configuration.global.dao.SAPGlobalConfigurationDAO;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundB2BContactModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundB2BCustomerModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundConfigModel;
import de.hybris.platform.sap.sapcpicustomerexchangeb2b.outbound.services.SapCpiB2BCustomerConversionService;
import de.hybris.platform.sap.sapmodel.model.SAPLogicalSystemModel;
import de.hybris.platform.store.services.BaseStoreService;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Required;

import java.util.*;
import java.util.stream.Collectors;

import static com.sap.hybris.sapcustomerb2b.constants.Sapcustomerb2bConstants.*;

/**
 * Class to convert Hybris B2B Customer to SCPI B2B Customer.
 */
public class SapCpiB2BCustomerDefaultConversionService implements SapCpiB2BCustomerConversionService {

  private static final String OBJECT_TYPE = "KNVK";

  private CustomerNameStrategy customerNameStrategy;
  private B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService;
  private BaseStoreService baseStoreService;
  private SAPGlobalConfigurationDAO globalConfigurationDAO;

  @Override
  public SAPCpiOutboundB2BCustomerModel convertB2BCustomerToSapCpiBb2BCustomer(B2BCustomerModel b2bCustomerModel, String sessionLanguage) {

    SAPCpiOutboundB2BCustomerModel sapCpiOutboundB2BCustomer = new SAPCpiOutboundB2BCustomerModel();

    // Hybris B2B Unit Maps To SAP B2B Customer
    final B2BUnitModel rootB2BUnit = getB2bUnitService().getRootUnit(b2bCustomerModel.getDefaultB2BUnit());
    sapCpiOutboundB2BCustomer.setUid(rootB2BUnit.getUid());
    sapCpiOutboundB2BCustomer.setAddressUUID(readSapAddressUUID(rootB2BUnit));
    mapOutboundDestination(sapCpiOutboundB2BCustomer);

    // Hybris B2B Customers Maps To SAP B2B Contacts
    Set<SAPCpiOutboundB2BContactModel> sapCpiOutboundB2BContacts = new HashSet<>();
    final Set<B2BCustomerModel> b2bCustomers = new HashSet<>();
    b2bCustomers.addAll(getB2bUnitService().getB2BCustomers(rootB2BUnit));
    getB2bUnitService().getB2BUnits(rootB2BUnit).forEach(subB2BUnit -> b2bCustomers.addAll(getB2bUnitService().getB2BCustomers(subB2BUnit)));

    b2bCustomers.forEach(b2bCustomer -> sapCpiOutboundB2BContacts.add(convertB2BContactToSapCpiBb2BContact(rootB2BUnit, b2bCustomer, sessionLanguage)));
    sapCpiOutboundB2BCustomer.setSapCpiOutboundB2BContacts(sapCpiOutboundB2BContacts);

    return sapCpiOutboundB2BCustomer;

  }

  protected String readSapAddressUUID(B2BUnitModel rootB2BUnit) {

    return rootB2BUnit.getAddresses() != null ? rootB2BUnit.getAddresses()
            .stream()
            .findFirst()
            .map(AddressModel::getSapAddressUUID)
            .orElse(null) : null;

  }

  protected SAPCpiOutboundB2BContactModel convertB2BContactToSapCpiBb2BContact(B2BUnitModel b2bUnitModel, B2BCustomerModel b2bCustomerModel, String sessionLanguage) {

    final SAPCpiOutboundB2BContactModel sapCpiOutboundB2BContact = new SAPCpiOutboundB2BContactModel();
    sapCpiOutboundB2BContact.setUid(b2bUnitModel.getUid());

    mapB2BContactInfo(b2bCustomerModel, sessionLanguage, sapCpiOutboundB2BContact);

    return sapCpiOutboundB2BContact;

  }

  protected void mapOutboundDestination(SAPCpiOutboundB2BCustomerModel sapCpiOutboundB2BCustomer) {

    final SAPCpiOutboundConfigModel sapCpiOutboundConfig = new SAPCpiOutboundConfigModel();
    final SAPLogicalSystemModel logicalSystem = readLogicalSystem();

    sapCpiOutboundConfig.setSenderName(logicalSystem.getSenderName());
    sapCpiOutboundConfig.setSenderPort(logicalSystem.getSenderPort());
    sapCpiOutboundConfig.setReceiverName(logicalSystem.getSapLogicalSystemName());
    sapCpiOutboundConfig.setReceiverPort(logicalSystem.getSapLogicalSystemName());
    sapCpiOutboundConfig.setUrl(logicalSystem.getSapHTTPDestination().getTargetURL());
    sapCpiOutboundConfig.setUsername(logicalSystem.getSapHTTPDestination().getUserid());
    sapCpiOutboundConfig.setClient(logicalSystem.getSapHTTPDestination().getTargetURL().split("sap-client=")[1].substring(0, 3));

    sapCpiOutboundB2BCustomer.setSapCpiConfig(sapCpiOutboundConfig);

  }

  protected void mapB2BContactInfo(B2BCustomerModel b2bCustomer, String sessionLanguage, SAPCpiOutboundB2BContactModel sapCpiOutboundB2BContact) {

    if (Objects.isNull(b2bCustomer.getDefaultB2BUnit())) {
      return;
    }

    sapCpiOutboundB2BContact.setEmail(b2bCustomer.getEmail());
    sapCpiOutboundB2BContact.setCustomerId(b2bCustomer.getCustomerID());
    sapCpiOutboundB2BContact.setSessionLanguage(sessionLanguage);
    sapCpiOutboundB2BContact.setObjType(OBJECT_TYPE);

    final String[] names = getCustomerNameStrategy().splitName(b2bCustomer.getName());
    sapCpiOutboundB2BContact.setFirstName(names[0]);
    sapCpiOutboundB2BContact.setLastName(names[1]);

    sapCpiOutboundB2BContact.setDefaultB2BUnit(b2bCustomer.getDefaultB2BUnit().getUid().split("_")[0]);
    sapCpiOutboundB2BContact.setTitle(b2bCustomer.getTitle() != null ? b2bCustomer.getTitle().getCode() : null);
    sapCpiOutboundB2BContact.setGroups(mapB2BCustomerFunction(b2bCustomer.getGroups()));

    mapB2BContactAddress(b2bCustomer, sapCpiOutboundB2BContact);

  }

  protected void mapB2BContactAddress(B2BCustomerModel b2bCustomer, SAPCpiOutboundB2BContactModel sapCpiOutboundB2BContact) {

    final AddressModel defaultShipmentAddress = b2bCustomer.getDefaultShipmentAddress();

    if (defaultShipmentAddress != null) {
      sapCpiOutboundB2BContact.setCountry(defaultShipmentAddress.getCountry() != null ? defaultShipmentAddress.getCountry().getIsocode() : Strings.EMPTY);
      sapCpiOutboundB2BContact.setPhone(defaultShipmentAddress.getPhone1());
    } else {
      sapCpiOutboundB2BContact.setCountry(Strings.EMPTY);
      sapCpiOutboundB2BContact.setPhone(Strings.EMPTY);
    }

  }

  protected SAPLogicalSystemModel readLogicalSystem() {

    final Set<SAPLogicalSystemModel> logicalSystems = getGlobalConfigurationDAO().getSAPGlobalConfiguration().getSapLogicalSystemGlobalConfig();
    Objects.requireNonNull(logicalSystems, "The B2B customer cannot be sent to SCPI. There is no SAP logical system maintained in the back office!");

    return logicalSystems.stream().
            filter(SAPLogicalSystemModel::isDefaultLogicalSystem)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("The B2B customer cannot be sent to SCPI. There is no default SAP logical system maintained in the back office!"));

  }

  private String mapB2BCustomerFunction(final Set<PrincipalGroupModel> groups) {

    final List<String> groupUIDs = groups.stream().map(PrincipalGroupModel::getUid).collect(Collectors.toList());

    if (groupUIDs.containsAll(Arrays.asList(B2BADMINGROUP, B2BCUSTOMERGROUP)))
      return HEADOFPURCHASING;
    else if (groupUIDs.contains(B2BADMINGROUP))
      return EXECUTIVEBOARD;
    else
      return groupUIDs.contains(B2BCUSTOMERGROUP) ? BUYER :
              Strings.EMPTY;

  }


  protected CustomerNameStrategy getCustomerNameStrategy() {
    return customerNameStrategy;
  }

  @Required
  public void setCustomerNameStrategy(CustomerNameStrategy customerNameStrategy) {
    this.customerNameStrategy = customerNameStrategy;
  }

  protected B2BUnitService<B2BUnitModel, B2BCustomerModel> getB2bUnitService() {
    return b2bUnitService;
  }

  @Required
  public void setB2bUnitService(B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService) {
    this.b2bUnitService = b2bUnitService;
  }

  protected BaseStoreService getBaseStoreService() {
    return baseStoreService;
  }

  @Required
  public void setBaseStoreService(BaseStoreService baseStoreService) {
    this.baseStoreService = baseStoreService;
  }

  protected SAPGlobalConfigurationDAO getGlobalConfigurationDAO() {
    return globalConfigurationDAO;
  }

  @Required
  public void setGlobalConfigurationDAO(SAPGlobalConfigurationDAO globalConfigurationDAO) {
    this.globalConfigurationDAO = globalConfigurationDAO;
  }

}
