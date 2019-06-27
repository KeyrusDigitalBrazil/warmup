
import {SeInjectable, SeValueProvider, TypedMap} from 'smarteditcommons';
import * as moment from 'moment';

export const PERSONALIZATION_DATE_FORMATS_PROVIDER: SeValueProvider = {
	provide: "PERSONALIZATION_DATE_FORMATS",
	useValue: {
		SHORT_DATE_FORMAT: 'M/D/YY',
		MODEL_DATE_FORMAT: 'YYYY-MM-DDTHH:mm:SSZ'
	}
};


@SeInjectable()
export class PersonalizationsmarteditDateUtils {

	constructor(
		private $filter: angular.IFilterService,
		private DATE_CONSTANTS: any,
		private PERSONALIZATION_DATE_FORMATS: TypedMap<string>,
		private isBlank: any) {
	}

	formatDate(dateStr: string, format: string): any {
		format = format || this.DATE_CONSTANTS.MOMENT_FORMAT;
		if (dateStr) {
			if (dateStr.match && dateStr.match(/^(\d{4})\-(\d{2})\-(\d{2})T(\d{2}):(\d{2}):(\d{2})(\+|\-)(\d{4})$/)) {
				dateStr = dateStr.slice(0, -2) + ":" + dateStr.slice(-2);
			}
			return moment(new Date(dateStr)).format(format);
		} else {
			return "";
		}
	}

	formatDateWithMessage(dateStr: string, format: string): any {
		format = format || this.PERSONALIZATION_DATE_FORMATS.SHORT_DATE_FORMAT;
		if (dateStr) {
			return this.formatDate(dateStr, format);
		} else {
			return this.$filter('translate')('personalization.toolbar.pagecustomizations.nodatespecified');
		}
	}

	isDateInThePast(modelValue: any): boolean {
		if (this.isBlank(modelValue)) {
			return false;
		} else {
			return moment(modelValue, this.DATE_CONSTANTS.MOMENT_FORMAT).isBefore();
		}
	}

	isDateValidOrEmpty(modelValue: any): boolean {
		return this.isBlank(modelValue) || moment(modelValue, this.DATE_CONSTANTS.MOMENT_FORMAT).isValid();
	}

	isDateRangeValid(startDate: any, endDate: any): boolean {
		if (this.isBlank(startDate) || this.isBlank(endDate)) {
			return true;
		} else {
			return moment(new Date(startDate)).isSameOrBefore(moment(new Date(endDate)));
		}
	}

	isDateStrFormatValid(dateStr: string, format: string): boolean {
		format = format || this.DATE_CONSTANTS.MOMENT_FORMAT;
		if (this.isBlank(dateStr)) {
			return false;
		} else {
			return moment(dateStr, format, true).isValid();
		}
	}

}
