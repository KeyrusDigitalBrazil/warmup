import {Customize} from "personalizationcommons/dtos/Customize";

export class CombinedView {
	enabled: boolean;
	selectedItems: any;
	customize: Customize;

	constructor() {
		this.enabled = false;
		this.selectedItems = null;
		this.customize = new Customize();
	}
}
