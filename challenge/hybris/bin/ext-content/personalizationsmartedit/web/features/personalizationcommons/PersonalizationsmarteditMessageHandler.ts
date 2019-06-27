import {SeInjectable} from 'smarteditcommons';

@SeInjectable()
export class PersonalizationsmarteditMessageHandler {

	constructor(
		private alertService: any
	) {}

	sendInformation(informationMessage: any): any {
		this.alertService.showInfo(informationMessage);
	}

	sendError(errorMessage: any): any {
		this.alertService.showDanger(errorMessage);
	}

	sendWarning(warningMessage: any): any {
		this.alertService.showWarning(warningMessage);
	}

	sendSuccess(successMessage: any): any {
		this.alertService.showSuccess(successMessage);
	}
}