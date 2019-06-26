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
import * as angular from "angular";
import {WizardAction, WizardActions} from "./WizardActions";
import {TypedMap} from "smarteditcommons";
import {WizardConfig, WizardService, WizardStep} from "./WizardService";
import {IWizardActionStrategy} from "smarteditcommons/services/wizard/DefaultWizardActionStrategy";
import * as lo from "lodash";

interface ModalWizardScope extends angular.IScope {
	[name: string]: any;
}

/* @internal */
export const ModalWizardControllerFactory = (config: WizardAction): angular.IControllerConstructor => {

	/* @ngInject */
	class ModalWizardController {

		public executeAction: (action: WizardAction) => void;

		private getWizardConfig: () => WizardConfig;
		private _wizardContext: {_steps: WizardStep[], templateUrl?: string, navActions?: WizardAction[], templateOverride?: string};
		private wizardService: WizardService;

		constructor(
			private lodash: lo.LoDashStatic,
			private $scope: ModalWizardScope,
			private $rootScope: angular.IRootScopeService,
			private modalManager: any,
			$controller: angular.IControllerService,
			private wizardActions: WizardActions,
			private MODAL_BUTTON_STYLES: TypedMap<string>,
			private $q: angular.IQService,
			defaultWizardActionStrategy: IWizardActionStrategy,
			generateIdentifier: () => string
		) {
			this.wizardService = new WizardService(this.$q, defaultWizardActionStrategy, generateIdentifier);
			this.wizardService.properties = config.properties;
			this.lodash.assign(this, $controller(config.controller, {
				$scope,
				wizardManager: this.wizardService
			}));

			if (config.controllerAs) {
				this.$scope[config.controllerAs] = this;
			}

			if (typeof this.getWizardConfig !== 'function') {
				throw new Error("The provided controller must provide a getWizardConfig() function.");
			}

			const modalConfig = this.getWizardConfig();

			this._wizardContext = {
				_steps: modalConfig.steps
			};

			this.executeAction = (action: WizardAction) => {
				this.wizardService.executeAction(action);
			};

			let unregisterWatch: () => void;

			this.wizardService.onLoadStep = (stepIndex: number, step: WizardStep) => {
				this.modalManager.title = step.title;
				this._wizardContext.templateUrl = step.templateUrl;
				this.modalManager.removeAllButtons();
				(step.actions || []).forEach((action) => {
					if (typeof action.enableIfCondition === 'function') {
						unregisterWatch = this.$rootScope.$watch(action.enableIfCondition, (newVal) => {
							if (newVal) {
								this.modalManager.enableButton(action.id);
							} else {
								this.modalManager.disableButton(action.id);
							}
						});
					}

					this.modalManager.addButton(this.convertActionToButtonConf(action));
				});
			};

			this.wizardService.onClose = (result: unknown) => {
				this.modalManager.close(result);
				unregisterWatch();
			};

			this.wizardService.onCancel = () => {
				this.modalManager.dismiss();
				unregisterWatch();
			};

			this.wizardService.onStepsUpdated = (steps: WizardStep[]) => {
				this.setupNavBar(steps);
				this._wizardContext._steps = steps;
			};

			this.wizardService.initialize(modalConfig);
			this.setupModal(modalConfig);
		}

		private setupNavBar(steps: WizardStep[]) {
			this._wizardContext.navActions = steps.map((step, index: number) => {
				const action = this.wizardActions.navBarAction({
					id: 'NAV-' + step.id,
					stepIndex: index,
					wizardService: this.wizardService,
					destinationIndex: index,
					i18n: step.name,
					isCurrentStep: () => {
						return action.stepIndex === this.wizardService.getCurrentStepIndex();
					}
				});
				return action;
			});
		}

		private setupModal(setupConfig: WizardConfig) {
			this._wizardContext.templateOverride = setupConfig.templateOverride;
			if (setupConfig.cancelAction) {
				this.modalManager.setDismissCallback(() => {
					this.wizardService.executeAction(setupConfig.cancelAction);
					return this.$q.reject();
				});
			}

			this.setupNavBar(setupConfig.steps);
		}

		private convertActionToButtonConf(action: WizardAction) {
			return {
				id: action.id,
				style: action.isMainAction ? this.MODAL_BUTTON_STYLES.PRIMARY : this.MODAL_BUTTON_STYLES.SECONDARY,
				label: action.i18n,
				callback: () => {
					this.wizardService.executeAction(action);
				}
			};
		}
	}

	return ModalWizardController;
};