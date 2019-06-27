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
import {AddSupportTicketComponent} from "./add-support-ticket.component";
import {TicketType} from "../models/ticketType";
import {Router} from "@angular/router";
import {TicketUtils} from "app/my-account/support-tickets/utils/ticket-utils";

describe('Add support ticket', () => {
  let addSupportTicketComponent: AddSupportTicketComponent;
  let supportTicketsService: SupportTicketsService;
  let fixture: ComponentFixture<AddSupportTicketComponent>;
  let mockRouter = {
    navigate: jasmine.createSpy('navigate')
  };

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AddSupportTicketComponent ],
      providers:    [ { provide: Router, useValue: mockRouter},SupportTicketsService, TicketUtils,HttpService,{provide: RequestOptions, useClass: BaseRequestOptions}, {provide: ConnectionBackend, useClass: MockBackend}, Http ],
      imports: [ FormsModule]
    });

  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AddSupportTicketComponent);
    addSupportTicketComponent = fixture.componentInstance;
    supportTicketsService = fixture.debugElement.injector.get(SupportTicketsService);
    spyOn(supportTicketsService, 'getTicketTypes').and.callFake( () => Observable.of(Array.of(new TicketType(true, 'description', 'type'))));
  });

  it('should create ticket when clicking on submit button', fakeAsync(() => {
    //arrange
    prepareSpies();
    //act
    setTicketMessage("Non-empty message");
    clickCreate();
    //assert
    expect(supportTicketsService.createTicket).toHaveBeenCalled();
    expect (mockRouter.navigate).toHaveBeenCalledWith (['/my-account/support-tickets']);//url can be moved to const

  }));

  it('should not create ticket when clicking on back button', fakeAsync(() => {
    //arrange
    prepareSpies();

    //act
    //setTicketMessage("Non-empty message");
    clickBack();
    //assert
    expect(supportTicketsService.createTicket).not.toHaveBeenCalled()
    expect (mockRouter.navigate).toHaveBeenCalledWith (['/my-account/support-tickets']);//url can be moved to const
  }));


  it('should not create ticket when short description is blank', fakeAsync(() => {
    //arrange
    prepareSpies();

    //act
    let blankDescription:string = "  ";
    setTicketMessage(blankDescription);
    clickCreate();;
    //assert
    expect(supportTicketsService.createTicket).not.toHaveBeenCalled()
    expect (mockRouter.navigate).toHaveBeenCalledWith (['/my-account/support-tickets']);//url can be moved to const
  }));

  function prepareSpies() {
    spyOn(supportTicketsService, 'createTicket').and.returnValue( Observable.of({}));
  }

  function clickCreate() {
    let addnewMessage = fixture.debugElement.query(By.css('.btn-create'));
    addnewMessage.triggerEventHandler('click', null);
  }

  function setTicketMessage(ticketMsg:string) {
    fixture.detectChanges();
    let ticketMessage = fixture.debugElement.query(By.css('textarea')).nativeElement;
    ticketMessage.value=ticketMsg;
    ticketMessage.dispatchEvent(new Event('input'));
  }

  function clickBack() {
    let updateButton = fixture.debugElement.query(By.css('.btn-back'));
    updateButton.triggerEventHandler('click', null);
  }
});
