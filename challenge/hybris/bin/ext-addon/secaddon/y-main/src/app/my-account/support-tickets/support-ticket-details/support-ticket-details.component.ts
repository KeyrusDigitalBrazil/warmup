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
import { ActivatedRoute, Params } from '@angular/router';
import { Router } from '@angular/router';
import { DatePipe } from '@angular/common';
import { SupportTicketsService } from "../service/support-tickets.service";
import { Ticket } from "../models/ticket";
import { TicketUtils } from '../utils/ticket-utils';
import { Owner } from "../models/owner";

@Component({
  selector: 'y-main',
  templateUrl: './support-ticket-details.component.html',
  providers: [SupportTicketsService, DatePipe]
})

export class SupportTicketDetailsComponent {
  ticket: Ticket;
  lang: string;
  private dateFormat = 'short';

  constructor(private supportTicketsService: SupportTicketsService, private route: ActivatedRoute, private router: Router, private datePipe: DatePipe, private ticketUtils: TicketUtils) {
  }

  ngOnInit(): void {
    this.getTicket();
  }

  private getTicket() {
    this.route.params.subscribe(params => this.supportTicketsService.getTicket(params['id']).subscribe(data => {
      this.ticket = data.json();
      this.lang = this.ticketUtils.getCurrentLanguage();
    }));
  }

  public goBack() {
    this.router.navigate(['/my-account/support-tickets']);
  }

  public formatDate(date: String) {
    return this.datePipe.transform(date, this.dateFormat);
  }

  public onMessageAdded(ticket: Ticket) {
    this.ticket = ticket;
  }

  public getType(ticket: Ticket) {
    return ticket.typeDescription[this.lang];
  }

  public getStatus(ticket: Ticket) {
    return ticket.statusDescription[this.lang];
  }

  public getPriority(ticket: Ticket) {
    return ticket.priorityDescription[this.lang];
  }

  public getMessageAuthor(owner: Owner) {
    let author: String = "Customer Service";
    if (owner.isCustomer) {
      author = owner.displayName;
    }
    return author;
  }
}
