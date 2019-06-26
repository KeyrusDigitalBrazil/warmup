
import {SeDirective} from 'smarteditcommons';
import {PersonalizationsmarteditDateUtils} from 'personalizationcommons';


@SeDirective({
	require: "ngModel",
	selector: '[isdatevalidorempty]'
})
export class IsDateValidOrEmptyDirective {

	constructor(
		private personalizationsmarteditDateUtils: PersonalizationsmarteditDateUtils) {
	}

	isDateInThePast(modelValue: any): boolean {
		return this.personalizationsmarteditDateUtils.isDateValidOrEmpty(modelValue);
	}

}
