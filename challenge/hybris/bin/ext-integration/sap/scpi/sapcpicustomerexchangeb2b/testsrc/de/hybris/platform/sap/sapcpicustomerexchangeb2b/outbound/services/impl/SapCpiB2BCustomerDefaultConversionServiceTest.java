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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.model.B2BUserGroupModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.commerceservices.strategies.CustomerNameStrategy;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.TitleModel;
import de.hybris.platform.sap.core.configuration.global.dao.SAPGlobalConfigurationDAO;
import de.hybris.platform.sap.core.configuration.model.SAPGlobalConfigurationModel;
import de.hybris.platform.sap.core.configuration.model.SAPHTTPDestinationModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundB2BContactModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundB2BCustomerModel;
import de.hybris.platform.sap.sapmodel.model.SAPLogicalSystemModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static com.sap.hybris.sapcustomerb2b.constants.Sapcustomerb2bConstants.B2BADMINGROUP;
import static com.sap.hybris.sapcustomerb2b.constants.Sapcustomerb2bConstants.B2BCUSTOMERGROUP;
import static org.junit.Assert.assertEquals;
import static org.powermock.api.mockito.PowerMockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class SapCpiB2BCustomerDefaultConversionServiceTest {

  @Mock
  private B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService;

  @Mock
  private SAPGlobalConfigurationDAO globalConfigurationDAO;

  @Mock
  private CustomerNameStrategy customerNameStrategy;

  @InjectMocks
  private SapCpiB2BCustomerDefaultConversionService sapCpiB2BCustomerDefaultConversionService;

  private B2BCustomerModel b2bCustomerModel;


  @Test
  public void convertB2BCustomerToSapCpiBb2BCustomer() {

    SAPCpiOutboundB2BCustomerModel sapCpiOutboundB2BCustomerModel = sapCpiB2BCustomerDefaultConversionService.convertB2BCustomerToSapCpiBb2BCustomer(b2bCustomerModel, "en");

    assertEquals("0000000359", sapCpiOutboundB2BCustomerModel.getUid());

    assertEquals("QE6CLNT910", sapCpiOutboundB2BCustomerModel.getSapCpiConfig().getReceiverName());
    assertEquals("QE6CLNT910", sapCpiOutboundB2BCustomerModel.getSapCpiConfig().getReceiverPort());
    assertEquals("HBRGTSM07", sapCpiOutboundB2BCustomerModel.getSapCpiConfig().getSenderName());
    assertEquals("HBRGTSM07", sapCpiOutboundB2BCustomerModel.getSapCpiConfig().getSenderPort());
    assertEquals("910", sapCpiOutboundB2BCustomerModel.getSapCpiConfig().getClient());

    assertEquals(3, sapCpiOutboundB2BCustomerModel.getSapCpiOutboundB2BContacts().size());

    SAPCpiOutboundB2BContactModel sapCpiOutboundB2BContactModel = sapCpiOutboundB2BCustomerModel.getSapCpiOutboundB2BContacts().iterator().next();
    assertEquals("0000000359",sapCpiOutboundB2BContactModel.getUid());
    assertEquals("Alice",sapCpiOutboundB2BContactModel.getFirstName());
    assertEquals("Kelly",sapCpiOutboundB2BContactModel.getLastName());

  }


  @Before
  public void setUp() {

    final B2BUnitModel rootB2BUnit = new B2BUnitModel();
    rootB2BUnit.setUid("0000000359");
    rootB2BUnit.setAddresses(Collections.EMPTY_LIST);
    when(b2bUnitService.getRootUnit(rootB2BUnit)).thenReturn(rootB2BUnit);

    Set<B2BCustomerModel> b2bContacts = getB2BContacts(rootB2BUnit);
    when(b2bUnitService.getB2BCustomers(rootB2BUnit)).thenReturn(b2bContacts);

    SAPHTTPDestinationModel sapHTTPDestinationModel = new SAPHTTPDestinationModel();
    sapHTTPDestinationModel.setTargetURL("http://ldai1qe6.wdf.sap.corp:44300/sap/bc/srt/idoc?sap-client=910");

    SAPLogicalSystemModel defaultLogicalSystem = new SAPLogicalSystemModel();
    defaultLogicalSystem.setDefaultLogicalSystem(true);
    defaultLogicalSystem.setSapLogicalSystemName("QE6CLNT910");
    defaultLogicalSystem.setSenderName("HBRGTSM07");
    defaultLogicalSystem.setSenderPort("HBRGTSM07");
    defaultLogicalSystem.setSapHTTPDestination(sapHTTPDestinationModel);

    Set<SAPLogicalSystemModel> sapLogicalSystemModels = new HashSet<>();
    sapLogicalSystemModels.add(defaultLogicalSystem);

    SAPGlobalConfigurationModel sapGlobalConfiguration = new SAPGlobalConfigurationModel();
    sapGlobalConfiguration.setSapLogicalSystemGlobalConfig(sapLogicalSystemModels);

    when(globalConfigurationDAO.getSAPGlobalConfiguration()).thenReturn(sapGlobalConfiguration);
    when(customerNameStrategy.splitName("Alice Kelly")).thenReturn(new String[] {"Alice","Kelly"});

    b2bCustomerModel = new B2BCustomerModel();
    b2bCustomerModel.setDefaultB2BUnit(rootB2BUnit);

  }

  private Set<B2BCustomerModel> getB2BContacts(B2BUnitModel b2bUnitModel) {

    Set<B2BCustomerModel> b2bContacts = new HashSet<>();
    TitleModel titleModel = new TitleModel();
    titleModel.setCode("0002");

    B2BUserGroupModel userGroup1 = new B2BUserGroupModel();
    userGroup1.setUid(B2BADMINGROUP);
    B2BUserGroupModel userGroup2 = new B2BUserGroupModel();
    userGroup2.setUid(B2BCUSTOMERGROUP);

    final Set<PrincipalGroupModel> userGroups = new HashSet<>();
    userGroups.add(userGroup1);
    userGroups.add(userGroup2);

    for (int i = 0; i < 3; i++) {

      B2BCustomerModel b2bContact = new B2BCustomerModel();
      b2bContact.setUid("0000000359");
      b2bContact.setCustomerID("000501844");
      b2bContact.setName("Alice Kelly");
      b2bContact.setEmail("alice@sap.com");
      b2bContact.setTitle(titleModel);
      b2bContact.setGroups(userGroups);
      b2bContact.setDefaultB2BUnit(b2bUnitModel);
      b2bContacts.add(b2bContact);

    }

    return b2bContacts;

  }


}