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
import {TypedMap} from "smarteditcommons";
import {WizardAction} from "./WizardActions";
import {IWizardActionStrategy} from "./DefaultWizardActionStrategy";

/**
 * @ngdoc object
 * @name wizardServiceModule.object:WizardStepConfig
 * @description
 * A plain JSON object, representing the configuration options for a single step in a wizard
 */

export interface WizardStep {
	/**
	 * @ngdoc property
	 * @name id
	 * @propertyOf wizardServiceModule.object:WizardStepConfig
	 * @description
	 * An optional unique ID for this step in the wizard. If no ID is provided, one is automatically generated.<br />
	 * You may choose to provide an ID, making it easier to reference this step explicitly via the wizard service, or
	 * be able to identify for which step a callback is being triggered.
	 */
	id: string;
	/**
	 * @ngdoc property
	 * @name templateUrl
	 * @propertyOf wizardServiceModule.object:WizardStepConfig
	 * @description The url of the html template for this step
	 */
	templateUrl: string;
	/**
	 * @ngdoc property
	 * @name title
	 * @propertyOf wizardServiceModule.object:WizardStepConfig
	 * @description An i18n key, representing the title that will be displayed at the top of the wizard for this step.
	 */
	/**
	 * @ngdoc property
	 * @name name
	 * @propertyOf wizardServiceModule.object:WizardStepConfig
	 * @description An i18n key representing a meaning (short) name for this step.
	 * This name will be displayed in the wizard navigation menu.
	 */
	name: string;
	title: string;
	actions: WizardAction[];
}

/**
 * @ngdoc object
 * @name wizardServiceModule.object:ModalWizardConfig
 * @description
 * A plain JSON object, representing the configuration options for a modal wizard
 */
export interface WizardConfig {
	/**
	 * @ngdoc property
	 * @name steps (Array)
	 * @propertyOf wizardServiceModule.object:ModalWizardConfig
	 * @description An ordered array of {@link wizardServiceModule.object:WizardStepConfig WizardStepConfig}
	 */
	steps: WizardStep[];
	actionStrategy: IWizardActionStrategy;
	/**
	 * @ngdoc property
	 * @name resultFn (Function)
	 * @propertyOf wizardServiceModule.object:ModalWizardConfig
	 * @description An optional callback function that has no parameters. This callback is triggered after the done
	 * action is fired, and the wizard is about to be closed. If this function is defined and returns a value, this
	 * value will be returned in the resolved promise returned by the {@link wizardServiceModule.modalWizard#methods_open modalWizard.open()}
	 * This is an easy way to pass a result from the wizard to the caller.
	 */
	resultFn: () => void;
	/**
	 * @ngdoc property
	 * @name isFormValid (Function)
	 * @propertyOf wizardServiceModule.object:ModalWizardConfig
	 * @description An optional callback function that receives a single parameter, the current step ID. This callback
	 * is used to enable/disable the next action and the done action.
	 * The callback should return a boolean to enabled the action. Null, or if this callback is not defined defaults to
	 * true (enabled)
	 */
	isFormValid?: (stepId: string) => boolean;
	/**
	 * @ngdoc property
	 * @name onNext (Function)
	 * @propertyOf wizardServiceModule.object:ModalWizardConfig
	 * @description An optional callback function that receives a single parameter, the current step ID.
	 * This callback is triggered after the next action is fired. You have the opportunity to halt the Next action by
	 * returning promise and rejecting it, otherwise the wizard will continue and load the next step.
	 */
	onNext?: (stepId: string) => boolean;
	/**
	 * @ngdoc property
	 * @name onCancel (Function)
	 * @propertyOf wizardServiceModule.object:ModalWizardConfig
	 * @description An optional callback function that receives a single parameter, the current step ID.
	 * This callback is triggered after the cancel action is fired. You have the opportunity to halt the cancel action
	 * (thereby stopping the wizard from being closed), by returning a promise and rejecting it, otherwise the wizard will
	 * continue the cancel action.
	 */
	onCancel?: (stepId: string) => boolean;
	/**
	 * @ngdoc property
	 * @name onDone (Function)
	 * @propertyOf wizardServiceModule.object:ModalWizardConfig
	 * @description An optional callback function that has no parameters. This callback is triggered after the done
	 * action is fired. You have the opportunity to halt the done action (thereby stopping the wizard from being closed),
	 * by returning a promise and rejecting it, otherwise the wizard will continue and close the wizard.
	 */
	onDone?: (stepId: string) => boolean;
	/**
	 * @ngdoc property
	 * @name doneLabel (String)
	 * @propertyOf wizardServiceModule.object:ModalWizardConfig
	 * @description An optional i18n key to override the default label for the Done button
	 */
	doneLabel?: string;
	/**
	 * @ngdoc property
	 * @name nextLabel (String)
	 * @propertyOf wizardServiceModule.object:ModalWizardConfig
	 * @description An optional i18n key to override the default label for the Next button
	 */
	nextLabel?: string;
	/**
	 * @ngdoc property
	 * @name backLabel (String)
	 * @propertyOf wizardServiceModule.object:ModalWizardConfig
	 * @description An optional i18n key to override the default label for the Back button
	 */
	backLabel?: string;
	/**
	 * @ngdoc property
	 * @name cancelLabel (String)
	 * @propertyOf wizardServiceModule.object:ModalWizardConfig
	 * @description An optional i18n key to override the default label for the Cancel button
	 */
	cancelLabel?: string;
	templateOverride?: string;
	cancelAction?: WizardAction;
}

/**
 * @ngdoc service
 * @name wizardServiceModule.WizardManager
 *
 * @description
 * The Wizard Manager is a wizard management service that can be injected into your wizard controller.
 *
 */
export class WizardService {

	public onLoadStep: (index: number, nextStep: WizardStep) => void;
	public onClose: (result: unknown) => void;
	public onCancel: () => void;
	public onStepsUpdated: (steps: WizardStep[]) => void;
	public properties: TypedMap<any>;

	private _actionStrategy: IWizardActionStrategy;
	private _currentIndex: number;
	private _conf: WizardConfig;
	private _steps: WizardStep[];
	private _getResult: () => void;

	constructor(
		private $q: angular.IQService,
		private defaultWizardActionStrategy: IWizardActionStrategy,
		private generateIdentifier: () => string
	) {
		// the overridable callbacks
		this.onLoadStep = function(index: number, nextStep: WizardStep) {
			return;
		};
		this.onClose = function(result: unknown) {
			return;
		};
		this.onCancel = function() {
			return;
		};
		this.onStepsUpdated = function(steps: WizardStep[]) {
			return;
		};
	}

	/* @internal */
	initialize(conf: WizardConfig): void {

		this.validateConfig(conf);

		this._actionStrategy = conf.actionStrategy || this.defaultWizardActionStrategy;
		this._actionStrategy.applyStrategy(this, conf);

		this._currentIndex = 0;
		this._conf = {...conf};
		this._steps = this._conf.steps;
		this._getResult = conf.resultFn;
		this.validateStepUids(this._steps);

		this.goToStepWithIndex(0);
	}

	/* @internal */
	executeAction(action: WizardAction): angular.IPromise<void> {
		if (action.executeIfCondition) {
			return this.$q.resolve(action.executeIfCondition()).then(() => {
				return action.execute(this);
			});
		}
		return this.$q.resolve(action.execute(this));
	}

	/**
	 * @ngdoc method
	 * @name wizardServiceModule.WizardManager#goToStepWithIndex
	 * @methodOf wizardServiceModule.WizardManager
	 * @description Navigates the wizard to the given step
	 * @param {Number} index The 0-based index from the steps array returned by the wizard controllers getWizardConfig() function
	 */
	goToStepWithIndex(index: number): void {
		const nextStep = this.getStepWithIndex(index);
		if (nextStep) {
			this.onLoadStep(index, nextStep);
			this._currentIndex = index;
		}
	}

	/**
	 * @ngdoc method
	 * @name wizardServiceModule.WizardManager#goToStepWithId
	 * @methodOf wizardServiceModule.WizardManager
	 * @description Navigates the wizard to the given step
	 * @param {String} id The ID of a step returned by the wizard controllers getWizardConfig() function. Note that if
	 * no id was provided for a given step, then one is automatically generated.
	 */
	goToStepWithId(id: string): void {
		this.goToStepWithIndex(this.getStepIndexFromId(id));
	}

	/**
	 * @ngdoc method
	 * @name wizardServiceModule.WizardManager#addStep
	 * @methodOf wizardServiceModule.WizardManager
	 * @description Adds an additional step to the wizard at runtime
	 * @param {Object} newStep A {@link wizardServiceModule.object:WizardStepConfig WizardStepConfig}
	 * @param {Number} index (OPTIONAL) A 0-based index position in the steps array. Default is 0.
	 */
	addStep(newStep: WizardStep, index: number): void {
		if (parseInt(newStep.id, 10) !== 0 && !newStep.id) {
			newStep.id = this.generateIdentifier();
		}
		if (!index) {
			index = 0;
		}
		if (this._currentIndex >= index) {
			this._currentIndex++;
		}
		this._steps.splice(index, 0, newStep);
		this.validateStepUids(this._steps);
		this._actionStrategy.applyStrategy(this, this._conf);
		this.onStepsUpdated(this._steps);
	}

	/**
	 * @ngdoc method
	 * @name wizardServiceModule.WizardManager#removeStepById
	 * @methodOf wizardServiceModule.WizardManager
	 * @description Remove a step form the wizard at runtime. If you are removing the currently displayed step, the
	 * wizard will return to the first step. Removing all the steps will result in an error.
	 * @param {String} id The id of the step you wish to remove
	 */
	removeStepById(id: string): void {
		this.removeStepByIndex(this.getStepIndexFromId(id));
	}

	/**
	 * @ngdoc method
	 * @name wizardServiceModule.WizardManager#removeStepByIndex
	 * @methodOf wizardServiceModule.WizardManager
	 * @description Remove a step form the wizard at runtime. If you are removing the currently displayed step, the
	 * wizard will return to the first step. Removing all the steps will result in an error.
	 * @param {Number} index The 0-based index of the step you wish to remove.
	 */
	removeStepByIndex(index: number): void {
		if (index >= 0 && index < this.getStepsCount()) {
			this._steps.splice(index, 1);
			if (index === this._currentIndex) {
				this.goToStepWithIndex(0);
			}
			this._actionStrategy.applyStrategy(this, this._conf);
			this.onStepsUpdated(this._steps);
		}
	}

	/**
	 * @ngdoc method
	 * @name wizardServiceModule.WizardManager#close
	 * @methodOf wizardServiceModule.WizardManager
	 * @description Close the wizard. This will return a resolved promise to the creator of the wizard, and if any
	 * resultFn was provided in the {@link wizardServiceModule.object:ModalWizardConfig ModalWizardConfig} the returned
	 * value of this function will be passed as the result.
	 */
	close(): void {
		let result: unknown;
		if (typeof this._getResult === 'function') {
			result = this._getResult();
		}
		this.onClose(result);
	}

	/**
	 * @ngdoc method
	 * @name wizardServiceModule.WizardManager#cancel
	 * @methodOf wizardServiceModule.WizardManager
	 * @description Cancel the wizard. This will return a rejected promise to the creator of the wizard.
	 */
	cancel(): void {
		this.onCancel();
	}

	/**
	 * @ngdoc method
	 * @name wizardServiceModule.WizardManager#getSteps
	 * @methodOf wizardServiceModule.WizardManager
	 * @returns {Array} An array of all the steps in the wizard
	 */
	getSteps(): WizardStep[] {
		return this._steps;
	}

	/**
	 * @ngdoc method
	 * @name wizardServiceModule.WizardManager#getStepIndexFromId
	 * @methodOf wizardServiceModule.WizardManager
	 * @param {String} id A step ID
	 * @returns {Number} The index of the step with the provided ID
	 */
	getStepIndexFromId(id: string): number {
		const index = this._steps.findIndex((step) => {
			return step.id === id;
		});
		return index;
	}

	/**
	 * @ngdoc method
	 * @name wizardServiceModule.WizardManager#containsStep
	 * @methodOf wizardServiceModule.WizardManager
	 * @param {String} id A step ID
	 * @returns {Boolean} True if the ID exists in one of the steps
	 */
	containsStep(stepId: string): boolean {
		return this.getStepIndexFromId(stepId) >= 0;
	}

	/**
	 * @ngdoc method
	 * @name wizardServiceModule.WizardManager#getCurrentStepId
	 * @methodOf wizardServiceModule.WizardManager
	 * @returns {String} The ID of the currently displayed step
	 */
	getCurrentStepId(): string {
		return this.getCurrentStep().id;
	}

	/**
	 * @ngdoc method
	 * @name wizardServiceModule.WizardManager#getCurrentStepIndex
	 * @methodOf wizardServiceModule.WizardManager
	 * @returns {Number} The index of the currently displayed step
	 */
	getCurrentStepIndex() {
		return this._currentIndex;
	}

	/**
	 * @ngdoc method
	 * @name wizardServiceModule.WizardManager#getCurrentStep
	 * @methodOf wizardServiceModule.WizardManager
	 * @returns {Object} The currently displayed step
	 */
	getCurrentStep(): WizardStep {
		return this.getStepWithIndex(this._currentIndex);
	}

	/**
	 * @ngdoc method
	 * @name wizardServiceModule.WizardManager#getStepsCount
	 * @methodOf wizardServiceModule.WizardManager
	 * @returns {Number} The number of steps in the wizard. This should always be equal to the size of the array
	 * returned by {@link wizardServiceModule.WizardManager#methods_getSteps getSteps()}
	 */
	getStepsCount(): number {
		return this._steps.length;
	}

	/**
	 * @ngdoc method
	 * @name wizardServiceModule.WizardManager#getStepWithId
	 * @methodOf wizardServiceModule.WizardManager
	 * @param {String} id The ID of a step
	 * @returns {Object} The {@link wizardServiceModule.object:WizardStepConfig step} with the given ID
	 */
	getStepWithId(id: string): WizardStep {
		const index = this.getStepIndexFromId(id);
		if (index >= 0) {
			return this.getStepWithIndex(index);
		}
		return null;
	}

	/**
	 * @ngdoc method
	 * @name wizardServiceModule.WizardManager#getStepWithIndex
	 * @methodOf wizardServiceModule.WizardManager
	 * @param {Number} index The ID of a step
	 * @returns {Object} The {@link wizardServiceModule.object:WizardStepConfig step} with the given index
	 */
	getStepWithIndex(index: number): WizardStep {
		if (index >= 0 && index < this.getStepsCount()) {
			return this._steps[index];
		}
		throw new Error(("wizardService.getStepForIndex - Index out of bounds: " + index));
	}

	private validateConfig(config: WizardConfig): void {
		if (!config.steps || config.steps.length <= 0) {
			throw new Error("Invalid WizardService configuration - no steps provided");
		}

		config.steps.forEach((step) => {
			if (!step.templateUrl) {
				throw new Error(`Invalid WizardService configuration - Step missing a url: ${step}`);
			}
		});
	}

	private validateStepUids(steps: WizardStep[]): void {
		const stepIds: TypedMap<string> = {};
		steps.forEach((step) => {
			if (!step.id) {
				step.id = this.generateIdentifier();
			} else if (stepIds[step.id]) {
				throw new Error(`Invalid (Duplicate) step id: ${step.id}`);
			} else {
				stepIds[step.id] = step.id;
			}
		});
	}
}