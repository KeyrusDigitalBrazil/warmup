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

import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule }   from '@angular/forms';
import { AddSupportTicketComponent } from './support-tickets/add-support-ticket/add-support-ticket.component';
import { ListSupportTicketsComponent } from './support-tickets/list-support-tickets/list-support-tickets.component';
import { PaginationAndSortComponent } from './support-tickets/pagintion-and-sorting/pagintion-and-sorting.component';
import { SupportTicketsService } from './support-tickets/service/support-tickets.service';
import { HttpService } from './support-tickets/http/http-service';
import { TicketUtils } from './support-tickets/utils/ticket-utils';
import { SupportTicketDetailsComponent } from './support-tickets/support-ticket-details/support-ticket-details.component';
import { PaginationAndSortService } from "./support-tickets/service/pagination-and-sort.service";
import {AddTicketMessageComponent} from "./support-tickets/add-ticket-message/add-ticket-message.component";

@NgModule({
  imports: [
    CommonModule,
    FormsModule
  ],
  declarations: [
    AddSupportTicketComponent,
    ListSupportTicketsComponent,
    AddTicketMessageComponent,
    SupportTicketDetailsComponent,
    PaginationAndSortComponent
  ],
  exports: [
    AddSupportTicketComponent,
    ListSupportTicketsComponent,
    AddTicketMessageComponent,
    SupportTicketDetailsComponent,
    PaginationAndSortComponent
  ],
  providers: [
    PaginationAndSortService,
    SupportTicketsService,
    HttpService,
    TicketUtils
  ]
})
export class MyAccountModule {
}
