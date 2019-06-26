import {SeInjectable} from 'smarteditcommons';


@SeInjectable()
export class PersonalizationsmarteditCommerceCustomizationService {
	protected nonCommerceActionTypes = ['cxCmsActionData'];
	protected types: any[] = [];

	isNonCommerceAction(action: any): any {
		return this.nonCommerceActionTypes.some((val) => {
			return val === action.type;
		});
	}


	isCommerceAction(action: any): boolean {
		return !this.isNonCommerceAction(action);
	}

	isTypeEnabled(type: any, seConfigurationData: any): boolean {
		return (seConfigurationData !== undefined && seConfigurationData !== null && seConfigurationData[type.confProperty] === true);
	}

	registerType(item: any): void {
		const type = item.type;
		let exists = false;

		this.types.forEach((val) => {
			if (val.type === type) {
				exists = true;
			}
		});

		if (!exists) {
			this.types.push(item);
		}
	}

	getAvailableTypes(seConfigurationData: any): any {
		return this.types.filter((item) => {
			return this.isTypeEnabled(item, seConfigurationData);
		});
	}

	isCommerceCustomizationEnabled(seConfigurationData: any): boolean {
		const at = this.getAvailableTypes(seConfigurationData);
		return at.length > 0;
	}

	getNonCommerceActionsCount(variation: any): number {
		return (variation.actions || []).filter(this.isNonCommerceAction, this).length;
	}

	getCommerceActionsCountMap(variation: any): any {
		const result: any = {};

		(variation.actions || [])
			.filter(this.isCommerceAction, this)
			.forEach((action: any) => {
				const typeKey = action.type.toLowerCase();

				let count = result[typeKey];
				if (count === undefined) {
					count = 1;
				} else {
					count += 1;
				}
				result[typeKey] = count;
			});

		return result;
	}

	getCommerceActionsCount(variation: any): number {
		return (variation.actions || [])
			.filter(this.isCommerceAction, this).length;
	}
}