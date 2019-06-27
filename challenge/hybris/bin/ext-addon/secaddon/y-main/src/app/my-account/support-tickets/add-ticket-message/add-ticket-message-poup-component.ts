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

import {Injectable, NgZone} from "@angular/core";

@Injectable()
export class AddTicketMessagePopupComponent {

  constructor( private zone: NgZone) {}

  public showPopup():void {
    this.zone.runOutsideAngular(() => {
      $.colorbox({
        href: "#ct-add-new-msg",
        maxWidth:"100%",
        width: 525,
        opacity:0.7,
        title: '<div class="headline"><span class="headline-text"></span></div>',
        inline: true,
        close: '<span class="glyphicon glyphicon-remove"></span>',
        onOpen: function () {
          $('#ct-add-new-msg').fadeIn();
        },
        onComplete: function () {
          $('#cboxTitle span').text($('.ct-add-new-msg-btn').text());
          $('button[id="updateTicket"]').attr('disabled', 'disabled');
        },
        onCleanup: function () {
          $('#ct-add-new-msg').hide();
        }
      });
    });
  }

  public closePopup():void {
    $.colorbox.close();
  }

}
