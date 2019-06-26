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
import {SystemEventService} from "smarteditcommons/services/SystemEventService";
import {SeComponent} from "smarteditcommons/services/dependencyInjection/SeComponent";
import {IYEventMessageData} from "./IYEventMessageData";
import {ISeComponent} from "smarteditcommons/services";

/**
 * @ngdoc directive
 * @name smarteditCommonsModule.directive:YEventMessage
 * @scope
 * @restrict E
 *
 * @description
 * The YEventMessage is a wrapper around YMessage, used to display or hide the message based on events sent through the systemEventService.
 * 
 * @param {< string =} type The YMessage type
 * @param {< string =} title The YMessage title
 * @param {< string =} description The YMessage description
 * @param {< string =} showEvent The event id where the YMessage should be shown. You can update the message or title at this time, 
 * by passing a {@link smarteditCommonsModule.interface:IYEventMessageData IYEventMessageData} as argument to the event service.
 * @param {< string =} hideEvent The event id where the YMessage should be hidden
 * @param {< string =} showToStart Controls whether the component is shown right away after compiling the dom
 */
@SeComponent({
	template: `
		<div data-recompile-dom="$ctrl.recompile">
			<y-message data-type="$ctrl.type"
				data-ng-if="$ctrl.show">
				<message-title data-ng-if="$ctrl.title.length">
					{{ $ctrl.title | translate }}
				</message-title>
				<message-description data-ng-if="$ctrl.description.length">
					{{ $ctrl.description | translate }}
				</message-description>
			</y-message>
		</div>
    `,
	inputs: [
		'type: ?',
		'title: ?',
		'description: ?',
		'showEvent: ?',
		'hideEvent: ?',
		'showToStart: ?'
	]
})
export class YEventMessageComponent implements ISeComponent {

	public type: string = 'info';
	public title: string;
	public description: string;
	public show: boolean = false;
	public showToStart: string | boolean;
	public recompile: () => void;

	private unregisterShowEventHandler: () => void;
	private unregisterHideEventHandler: () => void;

	/** @internal */
	constructor(private systemEventService: SystemEventService) {
	}

	$onChanges(changesObj: angular.IOnChangesObject): void {
		if (changesObj.showEvent) {
			this.removeShowEventHandler();
			this.unregisterShowEventHandler = this.systemEventService.subscribe(changesObj.showEvent.currentValue,
				(eventId: string, eventData: any) => this.showEventHandler(eventId, eventData));
		}
		if (changesObj.hideEvent) {
			this.removeHideEventHandler();
			this.unregisterHideEventHandler = this.systemEventService.subscribe(changesObj.hideEvent.currentValue,
				() => this.show = false);
		}
		if (this.recompile) {
			this.recompile();
		}
	}

	$onInit(): void {
		this.show = this.showToStart === 'true' || this.showToStart === true;
	}

	$onDestroy() {
		this.removeShowEventHandler();
		this.removeHideEventHandler();
	}

	showDescription(): boolean {
		return typeof this.description === 'string' && this.description.length > 0;
	}

	showTitle(): boolean {
		return typeof this.title === 'string' && this.title.length > 0;
	}

	showEventHandler(eventId: string, eventData: IYEventMessageData): void {
		if (eventData.description && eventData.description.length) {
			this.description = eventData.description;
		}
		if (eventData.title && eventData.title.length) {
			this.title = eventData.title;
		}
		this.show = true;
		if (this.recompile) {
			this.recompile();
		}
	}

	private removeHideEventHandler(): void {
		if (this.unregisterHideEventHandler) {
			this.unregisterHideEventHandler();
		}
	}

	private removeShowEventHandler(): void {
		if (this.unregisterShowEventHandler) {
			this.unregisterShowEventHandler();
		}
	}

}
