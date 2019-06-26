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
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundB2BCustomerModel;
import de.hybris.platform.sap.sapcpiadapter.service.SapCpiOutboundService;
import de.hybris.platform.sap.sapcpicustomerexchangeb2b.outbound.services.SapCpiB2BCustomerConversionService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import rx.Observable;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class SapCpiB2BCustomerOutboundServiceTest {

  @Mock
  private SapCpiB2BCustomerConversionService sapCpiB2BCustomerConversionService;
  @Mock
  private SapCpiOutboundService sapCpiOutboundService;

  @InjectMocks
  private SapCpiB2BCustomerOutboundService sapCpiB2BCustomerOutboundService;

  @Test
  public void prepareAndSendB2BCustomerWithDefaultB2BUnit() {

    B2BCustomerModel b2bCustomerModel =  new B2BCustomerModel();
    b2bCustomerModel.setDefaultB2BUnit(new B2BUnitModel());
    SAPCpiOutboundB2BCustomerModel sapCpiOutboundB2BCustomerModel = new SAPCpiOutboundB2BCustomerModel();

    when(sapCpiB2BCustomerConversionService.convertB2BCustomerToSapCpiBb2BCustomer(b2bCustomerModel,"en")).thenReturn(sapCpiOutboundB2BCustomerModel);
    when(sapCpiOutboundService.sendB2BCustomer(sapCpiOutboundB2BCustomerModel)).thenReturn(Observable.just(new ResponseEntity<>(HttpStatus.OK)));

    sapCpiB2BCustomerOutboundService.prepareAndSend(b2bCustomerModel,"en");

    verify(sapCpiB2BCustomerConversionService).convertB2BCustomerToSapCpiBb2BCustomer(b2bCustomerModel,"en");
    verify(sapCpiOutboundService).sendB2BCustomer(sapCpiOutboundB2BCustomerModel);

  }

  @Test
  public void prepareAndSendB2BCustomerWithoutDefaultB2BUnit() {

    B2BCustomerModel b2bCustomerModel =  new B2BCustomerModel();
    SAPCpiOutboundB2BCustomerModel sapCpiOutboundB2BCustomerModel = new SAPCpiOutboundB2BCustomerModel();

    when(sapCpiB2BCustomerConversionService.convertB2BCustomerToSapCpiBb2BCustomer(b2bCustomerModel,"en")).thenReturn(sapCpiOutboundB2BCustomerModel);
    when(sapCpiOutboundService.sendB2BCustomer(sapCpiOutboundB2BCustomerModel)).thenReturn(Observable.just(new ResponseEntity<>(HttpStatus.OK)));

    sapCpiB2BCustomerOutboundService.prepareAndSend(b2bCustomerModel,"en");

    verify(sapCpiB2BCustomerConversionService, times(0)).convertB2BCustomerToSapCpiBb2BCustomer(b2bCustomerModel,"en");
    verify(sapCpiOutboundService,times(0)).sendB2BCustomer(sapCpiOutboundB2BCustomerModel);

  }


}