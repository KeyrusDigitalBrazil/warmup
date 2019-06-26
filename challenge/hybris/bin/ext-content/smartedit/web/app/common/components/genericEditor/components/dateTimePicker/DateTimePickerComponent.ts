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
import {SeComponent, TypedMap} from "smarteditcommons";
import {DateTimePickerLocalizationService, GenericEditorField} from "smarteditcommons/components/genericEditor";
import * as moment from "moment";

/**
 * @ngdoc directive
 * @name dateTimePickerModule.directive:dateTimePicker
 * @description
 * The dateTimePicker directive
 * @param {=String} name The name of the datepicker.
 * @param {=Object} model The model object for the datepicker.
 * @param {<Boolean} isEditable This property specifies whether the datepicker can be edited or not.
 * @param {=Object} field The field description of the field being edited.
 */
@SeComponent({
	templateUrl: 'dateTimePickerComponentTemplate.html',
	inputs: [
		'name:=',
		'model:=',
		'isEditable',
		'field:='
	]
})
export class DateTimePickerComponent {

	public name: string;
	public model: TypedMap<any>;
	public isEditable: boolean;
	public field: GenericEditorField;
	public placeholderText: string;

	constructor(
		private $timeout: angular.ITimeoutService,
		private dateTimePickerLocalizationService: DateTimePickerLocalizationService,
		private DATE_CONSTANTS: any,
		private formatDateAsUtc: any,
		private $element: any
	) {}

	$onInit(): void {
		this.placeholderText = 'se.componentform.select.date';

		if (this.isEditable) {
			this.node.datetimepicker({
				format: this.DATE_CONSTANTS.MOMENT_FORMAT,
				keepOpen: true,
				minDate: 0,
				showClear: true,
				showClose: true,
				useCurrent: false,
				widgetPositioning: {
					horizontal: 'right',
					vertical: 'bottom'
				}
			})
				.on('dp.change', () => {
					this.$timeout(() => {
						const momentDate = this.datetimepicker.date();
						if (momentDate) {
							this.model = this.formatDateAsUtc(momentDate);
						} else {
							this.model = void 0;
						}
					});
				})
				.on('dp.show', () => {
					this.dateTimePickerLocalizationService.localizeDateTimePicker(this.datetimepicker);
				});

			if (this.model) {
				// TODO: Remove Global Moment
				this.datetimepicker.date(moment(this.model));
			}
		}
	}

	private get node(): any {
		return this.$element.children().first();
	}

	private get datetimepicker(): any {
		return this.node.datetimepicker().data("DateTimePicker");
	}

}
