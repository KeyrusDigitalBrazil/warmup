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

import { Component } from '@angular/core';
import { SupportTicketsService } from '../service/support-tickets.service';
import { Router } from '@angular/router';
import { DatePipe } from '@angular/common';
import { Ticket } from '../models/ticket';
import { PaginationAndSortService } from "../service/pagination-and-sort.service";
import { TicketUtils } from '../utils/ticket-utils';

@Component({
  selector: 'y-main',
  templateUrl: './list-support-tickets.component.html',
  providers: [SupportTicketsService, PaginationAndSortService, DatePipe]
})

export class ListSupportTicketsComponent {
  tickets: Ticket[];
  lang: string;
  private dateFormat = 'short';

  constructor(private supportTicketsService: SupportTicketsService, private pagingAndSortService: PaginationAndSortService,
    private router: Router, private datePipe: DatePipe, private ticketUtils: TicketUtils) {
  }

  ngOnInit(): void {
    this.supportTicketsService.getTickets(this.pagingAndSortService.getPagingData()).subscribe(data => {
      this.tickets = data.json();
      this.lang = this.ticketUtils.getCurrentLanguage();
      this.pagingAndSortService.setTotalItems(+data.headers.get('Hybris-Count'));
    });

    this.pagingAndSortService.paginationSource$.subscribe(() => {
      this.onPaging();
    });
    this.router.initialNavigation();
  }

  public goToDetails(ticketId: string) {
    this.router.navigate(['/my-account/support-ticket', ticketId], { skipLocationChange: true });
  }

  public addNewTicket() {
    this.router.navigate(['/my-account/add-support-ticket']);
  }

  public formatDate(date: String) {
    return this.datePipe.transform(date, this.dateFormat);
  }

  public getPriority(ticket: Ticket) {
    return ticket.priorityDescription[this.lang];
  }

  public getStatus(ticket: Ticket) {
    return ticket.statusDescription[this.lang];
  }

  public onPaging() {
    this.supportTicketsService.getTickets(this.pagingAndSortService.getPagingData()).subscribe(data => this.tickets = data.json());
  }
}
