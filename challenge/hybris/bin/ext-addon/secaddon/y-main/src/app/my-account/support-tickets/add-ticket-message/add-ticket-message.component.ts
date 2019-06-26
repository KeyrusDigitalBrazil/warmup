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

import {Component, EventEmitter, Input, Output} from '@angular/core';
import {AddTicketMessagePopupComponent} from "./add-ticket-message-poup-component";
import {Ticket} from "../models/ticket";
import {SupportTicketsService} from "../service/support-tickets.service";
import {Transcript} from "../models/transcript";

@Component({
  selector: 'add-ticket-message',
  templateUrl: './add-ticket-message.component.html',
  providers: [SupportTicketsService, AddTicketMessagePopupComponent]
})

export class AddTicketMessageComponent {
  @Input() ticket: Ticket;
  @Output() onMessageAdded = new EventEmitter<Ticket>();
  ticketMessage: string;
  isUpdateInProgress:boolean=false;

  constructor(private supportTicketsService: SupportTicketsService, private updateTicketPopupComponent: AddTicketMessagePopupComponent) {}

  public addMessage() {
    this.updateTicketPopupComponent.showPopup();
  }

  public addTicketMessage(): void {
    if(this.canNotUpdate()){
      return;
    }

    this.isUpdateInProgress = true;
    let transcript: Transcript = this.prepareTranscript();
    this.supportTicketsService.addMessage(this.ticket.id,transcript).subscribe(() => {
      this.refreshTicket();
      this.updateTicketPopupComponent.closePopup()
      this.clearForm();
      this.isUpdateInProgress = false;    });
  }

  private prepareTranscript():Transcript{
    return new Transcript(null,this.ticketMessage );
  }
  public canNotUpdate():boolean {
    return this.isBlank(this.ticketMessage) || this.isUpdateInProgress;
  }

  private isBlank(str:string):boolean {
    return (!str || /^\s*$/.test(str));
  }

  private refreshTicket():void{
    this.supportTicketsService.getTicket(this.ticket.id).subscribe(data=>{this.ticket=data.json(); this.onMessageAdded.emit( this.ticket);});
  }

  private clearForm():void{
    this.ticketMessage="";
  }
}
