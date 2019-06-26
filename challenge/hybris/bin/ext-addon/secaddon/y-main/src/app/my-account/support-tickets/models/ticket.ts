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

import {MetaData} from "./metadata";
import {TicketType} from "./ticketType";
import {Transcript} from "./transcript";

export class Ticket {
  constructor(
    public owner: string,
    public priority: string,
    public priorityDescription: string[],
    public status: string,
    public shortDescription: string,
    public id: string,
    public type: String,
    public typeDescription: string[],
    public statusDescription: string[],
    public transcript:Transcript[],
    public metadata:MetaData) { }

}
