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
import {CrossFrameEventService, ISeComponent, SeComponent} from 'smarteditcommons';

/** @internal */
@SeComponent({
	templateUrl: 'toolbarItemContextTemplate.html',
	inputs: ['itemKey', 'contextTemplateUrl', 'itemIsOpen']
})
export class ToolbarItemContextComponent implements ISeComponent {

	public displayContext: boolean = false;

	private itemKey: string;
	private unregShowContext: any;
	private unregHideContext: any;

	constructor(
		private crossFrameEventService: CrossFrameEventService,
		private SHOW_TOOLBAR_ITEM_CONTEXT: string,
		private HIDE_TOOLBAR_ITEM_CONTEXT: string) {}

	$onInit(): void {

		this.unregShowContext = this.crossFrameEventService.subscribe(this.SHOW_TOOLBAR_ITEM_CONTEXT, (eventId: string, itemKey: string) => {
			if (itemKey === this.itemKey) {
				this.showContext(true);
			}
		});

		this.unregHideContext = this.crossFrameEventService.subscribe(this.HIDE_TOOLBAR_ITEM_CONTEXT, (eventId: string, itemKey: string) => {
			if (itemKey === this.itemKey) {
				this.showContext(false);
			}
		});

	}

	$onDestroy(): void {
		this.unregShowContext();
		this.unregHideContext();
	}

	showContext(show: boolean): void {
		this.displayContext = show;
	}

}