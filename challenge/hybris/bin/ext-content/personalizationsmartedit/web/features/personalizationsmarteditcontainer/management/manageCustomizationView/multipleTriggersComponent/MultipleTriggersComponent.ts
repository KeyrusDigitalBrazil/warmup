/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
import {SeComponent} from 'smarteditcommons';
import {ITriggerTab} from "./ITriggerTab";
import {TriggerTabService} from "./TriggerTabService";

@SeComponent({
	templateUrl: 'multipleTriggersComponentTemplate.html'
})
export class MultipleTriggersComponent {

	public tabsList: ITriggerTab[];

	constructor(
		private triggerTabService: TriggerTabService
	) {}

	$onInit(): void {
		this.tabsList = this.triggerTabService.getTriggersTabs();
	}

}
