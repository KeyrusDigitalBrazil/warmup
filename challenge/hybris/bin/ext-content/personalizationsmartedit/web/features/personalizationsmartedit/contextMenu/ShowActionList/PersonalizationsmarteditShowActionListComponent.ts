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
import {PersonalizationsmarteditContextService} from 'personalizationsmartedit/service/PersonalizationsmarteditContextServiceInner';
import {PersonalizationsmarteditComponentHandlerService} from 'personalizationsmartedit/service/PersonalizationsmarteditComponentHandlerService';

@SeComponent({
	templateUrl: 'personalizationsmarteditShowActionListTemplate.html',
	inputs: [
		'component'
	]
})
export class PersonalizationsmarteditShowActionListComponent {

	public selectedItems: any;
	public containerSourceId: string;
	public component: any;

	constructor(
		protected personalizationsmarteditContextService: PersonalizationsmarteditContextService,
		protected personalizationsmarteditUtils: any,
		protected personalizationsmarteditComponentHandlerService: PersonalizationsmarteditComponentHandlerService) {
	}

	$onInit(): void {
		this.selectedItems = this.personalizationsmarteditContextService.getCombinedView().selectedItems;
		this.containerSourceId = this.personalizationsmarteditComponentHandlerService.getContainerSourceIdForContainerId(this.component.containerId);
	}

	getLetterForElement(index: number): string {
		return this.personalizationsmarteditUtils.getLetterForElement(index);
	}

	getClassForElement(index: number): string {
		return this.personalizationsmarteditUtils.getClassForElement(index);
	}

	initItem(item: any): void {
		item.visible = false;
		(item.variation.actions || []).forEach((elem: any) => {
			if (elem.containerId && elem.containerId === this.containerSourceId) {
				item.visible = true;
			}
		});
		this.personalizationsmarteditUtils.getAndSetCatalogVersionNameL10N(item.variation);
	}

	isCustomizationFromCurrentCatalog(customization: string): boolean {
		return this.personalizationsmarteditUtils.isItemFromCurrentCatalog(customization, this.personalizationsmarteditContextService.getSeData());
	}

}
