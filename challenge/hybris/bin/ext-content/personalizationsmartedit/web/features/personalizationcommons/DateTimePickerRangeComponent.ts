
import {LanguageService, SeComponent} from 'smarteditcommons';
import {PersonalizationsmarteditDateUtils} from 'personalizationcommons';
import * as moment from 'moment';
import * as angular from 'angular';


@SeComponent({
	templateUrl: 'dateTimePickerRangeTemplate.html',
	inputs: [
		'name: =',
		'dateFrom: =',
		'dateTo: =',
		'isEditable: =',
		'dateFormat: ='
	]
})
export class DateTimePickerRangeComponent {

	public placeholderText: string = 'personalization.commons.datetimepicker.placeholder';
	public isFromDateValid: boolean = false;
	public isToDateValid: boolean = false;
	public isEndDateInThePast: boolean = false;
	public name: string;
	public dateFrom: string;
	public dateTo: string;
	public isEditable: string;
	public dateFormat: string;

	constructor(
		private DATE_CONSTANTS: any,
		public personalizationsmarteditDateUtils: PersonalizationsmarteditDateUtils,
		private $element: HTMLElement,
		private $scope: angular.IScope,
		private languageService: LanguageService) {
	}

	$onInit(): void {
		this.actionsIfisEditable();
	}

	getDateOrDefault(date: any): any {
		try {
			return moment(new Date(date));
		} catch (err) {
			return false;
		}
	}

	actionsIfisEditable(): any {
		if (this.isEditable) {

			this.getFromPickerNode()
				.datetimepicker({
					format: this.DATE_CONSTANTS.MOMENT_FORMAT,
					showClear: true,
					showClose: true,
					useCurrent: false,
					keepInvalid: true,
					locale: this.languageService.getBrowserLocale().split('-')[0]
				}).on('dp.change dp.hide', function(e: any) {
					let dateFrom = this.personalizationsmarteditDateUtils.formatDate(e.date, undefined);
					if (this.personalizationsmarteditDateUtils.isDateValidOrEmpty(dateFrom) &&
						this.personalizationsmarteditDateUtils.isDateValidOrEmpty(this.dateTo) &&
						!this.personalizationsmarteditDateUtils.isDateRangeValid(dateFrom, this.dateTo)) {
						dateFrom = angular.copy(this.dateTo);
					}
					this.dateFrom = dateFrom;
				}.bind(this));

			this.getToPickerNode()
				.datetimepicker({
					format: this.DATE_CONSTANTS.MOMENT_FORMAT,
					showClear: true,
					showClose: true,
					useCurrent: false,
					keepInvalid: true,
					locale: this.languageService.getBrowserLocale().split('-')[0]
				}).on('dp.change dp.hide', function(e: any) {
					let dateTo = this.personalizationsmarteditDateUtils.formatDate(e.date, undefined);
					if (this.personalizationsmarteditDateUtils.isDateValidOrEmpty(dateTo) &&
						this.personalizationsmarteditDateUtils.isDateValidOrEmpty(this.dateFrom) &&
						!this.personalizationsmarteditDateUtils.isDateRangeValid(this.dateFrom, dateTo)) {
						dateTo = angular.copy(this.dateFrom);
					}
					this.dateTo = dateTo;
				}.bind(this));

			this.$scope.$watch('this.$ctrl.dateFrom', () => {
				this.isFromDateValid = this.personalizationsmarteditDateUtils.isDateValidOrEmpty(this.dateFrom);
				if (this.personalizationsmarteditDateUtils.isDateStrFormatValid(this.dateFrom, this.DATE_CONSTANTS.MOMENT_FORMAT)) {
					this.getToDatetimepicker().minDate(this.getMinToDate(this.dateFrom));
				} else {
					this.getToDatetimepicker().minDate(moment());
				}
			}, true);

			this.$scope.$watch('this.$ctrl.dateTo', () => {
				const dateToValid = this.personalizationsmarteditDateUtils.isDateValidOrEmpty(this.dateTo);
				if (dateToValid) {
					this.isToDateValid = true;
					this.isEndDateInThePast = this.personalizationsmarteditDateUtils.isDateInThePast(this.dateTo);
				} else {
					this.isToDateValid = false;
					this.isEndDateInThePast = false;
				}
				if (this.personalizationsmarteditDateUtils.isDateStrFormatValid(this.dateTo, this.DATE_CONSTANTS.MOMENT_FORMAT)) {
					this.getFromDatetimepicker().maxDate(this.getDateOrDefault(this.dateTo));
				} else if (this.dateTo === "") {
					this.getFromDatetimepicker().maxDate(false);
				}
			}, true);

		}
	}



	getMinToDate(date: any): any {
		if (!this.personalizationsmarteditDateUtils.isDateInThePast(date)) {
			return this.getDateOrDefault(date);
		} else {
			return moment();
		}
	}

	getFromPickerNode(): any {
		return this.$element.querySelectorAll('#date-picker-range-from');
	}

	getFromDatetimepicker(): any {
		return this.getFromPickerNode().datetimepicker().data("DateTimePicker");
	}

	getToPickerNode(): any {
		return this.$element.querySelectorAll('#date-picker-range-to');
	}

	getToDatetimepicker(): any {
		return this.getToPickerNode().datetimepicker().data("DateTimePicker");
	}

}
