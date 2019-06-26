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
import {ListSupportTicketsComponent} from "./list-support-tickets.component";
import { RouterTestingModule } from '@angular/router/testing';
import {PaginationAndSortComponent} from "../pagintion-and-sorting/pagintion-and-sorting.component";
import {DatePipe} from "@angular/common";
import {Router} from "@angular/router";
import {TicketUtils} from "app/my-account/support-tickets/utils/ticket-utils";

describe('List support ticket', () => {
  let addSupportTicketComponent: ListSupportTicketsComponent;
  let supportTicketsService: SupportTicketsService;
  let fixture: ComponentFixture<ListSupportTicketsComponent>;
  let mockRouter = {
    navigate: jasmine.createSpy('navigate')
  };
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ListSupportTicketsComponent, PaginationAndSortComponent ],
      providers:    [ SupportTicketsService, TicketUtils, { provide: Router, useValue: mockRouter}, {provide: PaginationAndSortComponent}, {provide: PaginationAndSortComponent}, DatePipe, HttpService,{provide: RequestOptions, useClass: BaseRequestOptions}, {provide: ConnectionBackend, useClass: MockBackend}, Http ],

      imports: [ FormsModule, RouterTestingModule]
    })
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ListSupportTicketsComponent);
    addSupportTicketComponent = fixture.componentInstance;
    supportTicketsService = fixture.debugElement.injector.get(SupportTicketsService);
  });

  it('should list ticket on start', fakeAsync(() => {
    //arrange

    //act
    clickOnAddTicket();

    //assert
    expect(mockRouter.navigate).toHaveBeenCalledWith (['/my-account/add-support-ticket']);
  }));

  function clickOnAddTicket() {
    let addNew = fixture.debugElement.query(By.css('.add-address'));
    addNew.triggerEventHandler('click', null);
  }
});
