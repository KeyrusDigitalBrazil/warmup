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
import {SeInjectable} from "../dependencyInjection/SeInjectable";
import {WizardService} from "./WizardService";
import * as angular from "angular";
import {TypedMap} from "smarteditcommons";

export interface WizardAction {
	id?: string;
	i18n: string;
	controller?: angular.IControllerService;
	controllerAs?: string;
	isMainAction?: boolean;
	destinationIndex?: number;
	stepIndex?: number;
	wizardService?: WizardService;
	properties?: TypedMap<any>;
	isCurrentStep?(): boolean;
	enableIfCondition?(): boolean;
	executeIfCondition?(): boolean;
	execute?(wizardService: WizardService): void;
}

const DEFAULT_WIZARD_ACTION: WizardAction = {
	id: "wizard_action_id",
	i18n: 'wizard_action_label',
	isMainAction: true,
	enableIfCondition() {
		return true;
	},
	executeIfCondition() {
		return true;
	},
	execute(wizardService: WizardService) {
		return;
	}
};

/* @internal */
@SeInjectable()
export class WizardActions {

	customAction(configuration: WizardAction): WizardAction {
		return this.createNewAction(configuration);
	}

	done(configuration?: WizardAction): WizardAction {
		const custom = {
			id: 'ACTION_DONE',
			i18n: 'se.action.done',
			execute: (wizardService: WizardService) => {
				wizardService.close();
			}
		};

		return this.createNewAction(configuration, custom);
	}

	next(configuration?: WizardAction): WizardAction {
		const custom = {
			id: 'ACTION_NEXT',
			i18n: 'se.action.next',
			execute(wizardService: WizardService) {
				wizardService.goToStepWithIndex(wizardService.getCurrentStepIndex() + 1);
			}
		};

		return this.createNewAction(configuration, custom);
	}

	navBarAction(configuration: WizardAction): WizardAction {
		if (!configuration.wizardService || configuration.destinationIndex === null) {
			throw new Error("Error initializating navBarAction, must provide the wizardService and destinationIndex fields");
		}

		const custom = {
			id: 'ACTION_GOTO',
			i18n: 'action.goto',
			enableIfCondition: () => {
				return configuration.wizardService.getCurrentStepIndex() >= configuration.destinationIndex;
			},
			execute: (wizardService: WizardService) => {
				wizardService.goToStepWithIndex(configuration.destinationIndex);
			}
		};

		return this.createNewAction(configuration, custom);
	}

	back(configuration: WizardAction): WizardAction {
		const custom = {
			id: 'ACTION_BACK',
			i18n: 'se.action.back',
			isMainAction: false,
			execute(wizardService: WizardService) {
				const currentIndex = wizardService.getCurrentStepIndex();
				if (currentIndex <= 0) {
					throw new Error("Failure to execute BACK action, no previous index exists!");
				}
				wizardService.goToStepWithIndex(currentIndex - 1);
			}
		};

		return this.createNewAction(configuration, custom);
	}

	cancel(): WizardAction {
		return this.createNewAction({
			id: 'ACTION_CANCEL',
			i18n: 'se.action.cancel',
			isMainAction: false,
			execute(wizardService: WizardService) {
				wizardService.cancel();
			}
		});
	}

	private createNewAction(configuration: WizardAction = null, customConfiguration: WizardAction = null): WizardAction {
		return {...DEFAULT_WIZARD_ACTION, ...customConfiguration, ...configuration};
	}

}
