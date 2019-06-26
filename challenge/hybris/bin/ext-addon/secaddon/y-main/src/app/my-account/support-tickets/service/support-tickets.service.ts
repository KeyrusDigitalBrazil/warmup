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

import {Component, Inject, Injectable} from '@angular/core';
import {Response, Headers, RequestOptions, Http} from '@angular/http';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/filter';
import { Ticket } from '../models/ticket';
import {TicketType} from '../models/ticketType';
import {TicketPriority} from '../models/ticketPriority';
import { Observable } from 'rxjs/Observable';
import { HttpService } from '../http/http-service';
import { TicketUtils } from '../utils/ticket-utils';
import {PagingData} from "../models/pagingData";
import {Transcript} from "../models/transcript";

@Injectable()
export class SupportTicketsService {

  constructor(private httpService: HttpService,private ticketUtils: TicketUtils) {}

 public getTicketTypes(): Observable<any> {
  return  this.httpService.get('my-account/json/support-ticket/ticketTypes', this.prepareHeaders()). map((res: Response) => {
   let convertedTypes: TicketType[] = [];
   const language= this.ticketUtils.getCurrentLanguage();
   for (let ticketType of res.json()) {
      convertedTypes.push(new TicketType(ticketType.active,ticketType.description[language],ticketType.type));
 	}
 	return convertedTypes;
   });
   }
 
 public getTicketPriorities(): Observable<any> {
  return  this.httpService.get('my-account/json/support-ticket/ticketPriorities', this.prepareHeaders()). map((res: Response) => {
	let convertedPriorities: TicketPriority[] = [];
	const language= this.ticketUtils.getCurrentLanguage();
	for (let ticketPriority of res.json()) {
	   convertedPriorities.push(new TicketPriority(ticketPriority.active,ticketPriority.description[language],ticketPriority.type));
	 }
	 return convertedPriorities;
	 });
	}

  public createTicket(ticket: Ticket) {
    return this.httpService.post('my-account/json/support-ticket/add-support-ticket', ticket, this.prepareHeaders());
  }

  public getTickets(pageData: PagingData) {
    return this.httpService.get("my-account/json/support-ticket" +
      "?sort=" + pageData.sort +
      "&pageNumber=" + pageData.pageNumber +
      "&pageSize=" + pageData.pageSize, this.prepareHeaders())
      .map((res: Response) => res).share();
  }

  public getTicket(id: string) {
    return this.httpService.get('my-account/json/support-ticket/' + id,  this.prepareHeaders())
      .map((res: Response) => res);
  }

  public addMessage(id: string,transcript: Transcript) {
    return this.httpService.post('my-account/json/support-ticket/' + id+'/conversations',transcript,  this.prepareHeaders())
      .map((res: Response) => res);
  }

  private prepareHeaders() {
    const headers = new Headers({ 'Content-Type': 'application/json', 'hybris-languages': null });
    return new RequestOptions({ headers: headers });
  }
}
