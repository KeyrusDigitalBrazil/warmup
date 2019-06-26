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

import {SeDirective} from "smarteditcommons";

@SeDirective({
	selector: "body",
	inputs: [
		'class:@'
	]
})
export class BodyDirective {

	constructor(private personalizationsmarteditContextService: any, private $log: angular.ILogService) {
	}

	$onChanges(changes: any): void {
		if (changes.class && changes.class.currentValue) {
			const pageIdArray = changes.class.currentValue.split(" ").filter((elem: any) => {
				return /smartedit-page-uid\-(\S+)/.test(elem);
			});

			if (pageIdArray.length > 0) {
				const pageId = /smartedit-page-uid\-(\S+)/.exec(pageIdArray[0])[1];
				this.personalizationsmarteditContextService.setPageId(pageId);
				if (pageIdArray.length > 1) {
					this.$log.error("more than one page- class element attribute defined");
				}
			}
		}
	}

}
