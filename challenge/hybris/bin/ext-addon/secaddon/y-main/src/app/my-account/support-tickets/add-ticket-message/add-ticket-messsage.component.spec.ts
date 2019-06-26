///
/// [y] hybris Platform
///
/// Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
///
/// This software is the confidential and proprietary information of SAP
/// ("Confidential Information"). You shall not disclose such Confidential
/// Information and shall use it only in accordance with the terms of the
/// license agreement you entered into with SAP.
///

import {async, ComponentFixture, fakeAsync, TestBed} from '@angular/core/testing';
import { By }              from '@angular/platform-browser';
import {BaseRequestOptions, ConnectionBackend, Http, RequestOptions} from "@angular/http";
import {MockBackend} from "@angular/http/testing";
import {Observable} from "rxjs/Rx";
import {FormsModule} from "@angular/forms";
import {SupportTicketsService} from "../service/support-tickets.service";
import {HttpService} from "../http/http-service";
import {TicketUtils} from "app/my-account/support-tickets/utils/ticket-utils";
import {AddTicketMessageComponent} from "./add-ticket-message.component";
import {AddTicketMessagePopupComponent} from "./add-ticket-message-poup-component";


describe('Add support ticket message', () => {
  let addTicketMessageComponent:    AddTicketMessageComponent;
  let supportTicketsService:    SupportTicketsService;
  let addTicketMessagePopupComponent:    AddTicketMessagePopupComponent;
  let fixture: ComponentFixture<AddTicketMessageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AddTicketMessageComponent ],
      providers:    [ {provide: AddTicketMessagePopupComponent}, TicketUtils,SupportTicketsService,HttpService,{provide: RequestOptions, useClass: BaseRequestOptions}, {provide: ConnectionBackend, useClass: MockBackend}, Http ],
      imports: [ FormsModule ]
    })
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AddTicketMessageComponent);
    addTicketMessageComponent = fixture.componentInstance;
    supportTicketsService = fixture.debugElement.injector.get(SupportTicketsService);
    addTicketMessagePopupComponent = fixture.debugElement.injector.get(AddTicketMessagePopupComponent);
  });

  it('should update ticket when clicking on submit button', fakeAsync(() => {
   //arrange
    prepareaddTicketMessageComponent();
    prepareSpies();

   //act
    clickAddNewMessage();
    let ticketMessage:string = "addTicketMessage"
    setTicketMessage(ticketMessage);
    clickSubmitButton();

    //assert
   expect(supportTicketsService.addMessage).toHaveBeenCalled();
   expect(supportTicketsService.getTicket).toHaveBeenCalled()
   expect(addTicketMessagePopupComponent.showPopup).toHaveBeenCalled()
   //
    fixture.detectChanges(); expect(addTicketMessagePopupComponent.closePopup).toHaveBeenCalled
  }));

  it('should not update ticket when clicking on submit button without entering new message', fakeAsync(() => {
    //arrange
    prepareaddTicketMessageComponent();
    prepareSpies();

    //act
    let ticketMessage:string="";
    clickAddNewMessage();
    setTicketMessage(ticketMessage);
    clickSubmitButton();

    //assert
    expect(supportTicketsService.getTicket).not.toHaveBeenCalled()
    expect(addTicketMessagePopupComponent.showPopup).toHaveBeenCalled()
    expect(addTicketMessagePopupComponent.closePopup).not.toHaveBeenCalled
  }));

  it('should not add ticket message when message is blank', fakeAsync(() => {
    //arrange
    prepareaddTicketMessageComponent();
    prepareSpies();

    //act
    let ticketMessage:string = "  "
    clickAddNewMessage();
    setTicketMessage(ticketMessage);
    clickSubmitButton();

    //assert
    expect(supportTicketsService.getTicket).not.toHaveBeenCalled()
    expect(addTicketMessagePopupComponent.showPopup).toHaveBeenCalled()
    expect(addTicketMessagePopupComponent.closePopup).not.toHaveBeenCalled
  }));

  it('should not update ticket when clicking on cancel button', fakeAsync(() => {
    //arrange
    prepareaddTicketMessageComponent();
    prepareSpies();

    //act
    clickAddNewMessage();
    setTicketMessage("addTicketMessage");
    clickCloseButton();

    //assert
    expect(supportTicketsService.addMessage).not.toHaveBeenCalled()
    expect(supportTicketsService.getTicket).not.toHaveBeenCalled()

  }));

  function prepareaddTicketMessageComponent() {
    addTicketMessageComponent.ticket = {owner:'', priority: '', status: 'MEDIUM',  shortDescription: 'C2981063945', id: '', type: null,typeDescription:null,statusDescription: null, transcript: [], metadata: null}
    addTicketMessageComponent.ticketMessage="";
  }

  function prepareSpies() {
    spyOn(supportTicketsService, 'addMessage')
      .and.returnValue( Observable.of({}));
    spyOn(supportTicketsService, 'getTicket')
      .and.returnValue( Observable.of({}));
    spyOn(addTicketMessagePopupComponent, 'showPopup').and.callFake(function(){});
    spyOn(addTicketMessagePopupComponent, 'closePopup').and.callFake(function(){});
  }

  function clickAddNewMessage() {
    let addNewMessage = fixture.debugElement.query(By.css('.ct-add-new-msg-btn'));
    addNewMessage.triggerEventHandler('click', null);
  }

  function clickSubmitButton() {
    let updateButton = fixture.debugElement.query(By.css('button'));
    updateButton.triggerEventHandler('click', null);
  }

  function setTicketMessage(ticketMsg:string) {
    fixture.detectChanges();
    let ticketMessage = fixture.debugElement.query(By.css('textArea')).nativeElement;
    ticketMessage.value=ticketMsg;
    ticketMessage.dispatchEvent(new Event('input'));
  }

  function clickCloseButton() {
    let updateButton = fixture.debugElement.query(By.css('.closeColorBox'));
    updateButton.triggerEventHandler('click', null);
  }
});
