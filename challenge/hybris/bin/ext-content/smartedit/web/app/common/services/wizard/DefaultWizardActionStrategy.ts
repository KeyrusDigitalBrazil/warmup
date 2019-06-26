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
import {WizardConfig, WizardService, WizardStep} from "./WizardService";
import {WizardAction, WizardActions} from "./WizardActions";

export interface IWizardActionStrategy {
	applyStrategy(wizardService: WizardService, conf: WizardConfig): void;
}

/* @internal */
@SeInjectable()
export class DefaultWizardActionStrategy implements IWizardActionStrategy {

	constructor(
		private wizardActions: WizardActions
	) {}

	applyStrategy(wizardService: WizardService, conf: WizardConfig): void {
		const nextAction = this.applyOverrides(wizardService, this.wizardActions.next(), conf.nextLabel, conf.onNext, conf.isFormValid);
		const doneAction = this.applyOverrides(wizardService, this.wizardActions.done(), conf.doneLabel, conf.onDone, conf.isFormValid);

		const backConf = conf.backLabel ? {
			i18n: conf.backLabel
		} : null;
		const backAction = this.wizardActions.back(backConf);

		conf.steps.forEach((step: WizardStep, index: number) => {
			step.actions = [];
			if (index > 0) {
				step.actions.push(backAction);
			}
			if (index === (conf.steps.length - 1)) {
				step.actions.push(doneAction);
			} else {
				step.actions.push(nextAction);
			}
		});

		conf.cancelAction = this.applyOverrides(wizardService, this.wizardActions.cancel(), conf.cancelLabel, conf.onCancel, null);
		conf.templateOverride = 'modalWizardNavBarTemplate.html';
	}

	private applyOverrides(wizardService: WizardService, action: WizardAction, label: string, executeCondition: (stepId: string) => boolean, enableCondition: (stepId: string) => boolean): WizardAction {

		if (label) {
			action.i18n = label;
		}
		if (executeCondition) {
			action.executeIfCondition = function() {
				return executeCondition(wizardService.getCurrentStepId());
			};
		}
		if (enableCondition) {
			action.enableIfCondition = function() {
				return enableCondition(wizardService.getCurrentStepId());
			};
		}

		return action;
	}

}
