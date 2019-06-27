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
import {IWizardActionStrategy} from "smarteditcommons/services/wizard/DefaultWizardActionStrategy";
import {WizardService} from "smarteditcommons/services/wizard/WizardService";
import {promiseHelper} from 'testhelpers';

describe('wizardServiceModule', () => {
	let wizardService: WizardService;

	const handle1 = 'step1';
	const handle2 = 'step2';
	const handle3 = 'step3';

	const getStepId = (stepHandle: string) => {
		return stepHandle + '.id';
	};

	const getStepName = (stepHandle: string) => {
		return stepHandle + '.name';
	};

	const getStepTitle = (stepHandle: string) => {
		return stepHandle + '.title';
	};

	const getStepUrl = (stepHandle: string) => {
		return stepHandle + '.url';
	};

	const createDummyStep = (stepHandle: string): any => {
		return {
			id: getStepId(stepHandle),
			name: getStepName(stepHandle),
			title: getStepTitle(stepHandle),
			templateUrl: getStepUrl(stepHandle)
		};
	};

	const initDefaultData = () => {
		wizardService.initialize({
			steps: [
				createDummyStep(handle1) as any,
				createDummyStep(handle2),
				createDummyStep(handle3)
			]
		} as any);
	};

	beforeEach(angular.mock.module('wizardServiceModule'));

	beforeEach(angular.mock.inject(function(
		_defaultWizardActionStrategy_: IWizardActionStrategy,
		_generateIdentifier_: () => string
	) {
		wizardService = new WizardService(promiseHelper.$q(), _defaultWizardActionStrategy_, _generateIdentifier_);
	}));

	it('add the proper number of steps to the wizard', () => {
		const config1 = {
			steps: [
				createDummyStep(handle1)
			]
		} as any;
		const config2 = {
			steps: [
				createDummyStep(handle1),
				createDummyStep(handle2)
			]
		} as any;
		const config3 = {
			steps: [
				createDummyStep(handle1),
				createDummyStep(handle2),
				createDummyStep(handle3)
			]
		} as any;
		wizardService.initialize(config1);
		expect(wizardService.getStepsCount()).toBe(1);
		wizardService.initialize(config2);
		expect(wizardService.getStepsCount()).toBe(2);
		wizardService.initialize(config3);
		expect(wizardService.getStepsCount()).toBe(3);
	});

	it('begin with the first step', () => {
		initDefaultData();
		const currentStep = wizardService.getCurrentStep();
		const stepHandle = 'step1';

		expect(currentStep.id).toEqual(getStepId(stepHandle));
		expect(currentStep.name).toEqual(getStepName(stepHandle));
		expect(currentStep.title).toEqual(getStepTitle(stepHandle));
		expect(currentStep.templateUrl).toEqual(getStepUrl(stepHandle));
	});

	it('load a step by id', () => {
		initDefaultData();
		wizardService.goToStepWithId(getStepId(handle2));
		const currentStep = wizardService.getCurrentStep();

		expect(currentStep.id).toEqual(getStepId(handle2));
		expect(currentStep.name).toEqual(getStepName(handle2));
		expect(currentStep.title).toEqual(getStepTitle(handle2));
		expect(currentStep.templateUrl).toEqual(getStepUrl(handle2));
	});

	it('load a step by index', () => {
		initDefaultData();
		expect(wizardService.getCurrentStepIndex()).toEqual(0);

		wizardService.goToStepWithIndex(2);
		expect(wizardService.getCurrentStepIndex()).toEqual(2);

		wizardService.goToStepWithIndex(0);
		expect(wizardService.getCurrentStepIndex()).toEqual(0);
	});

	it('get the index of the current step', () => {
		initDefaultData();
		expect(wizardService.getCurrentStepIndex()).toEqual(0);

		wizardService.goToStepWithId(getStepId(handle3));
		expect(wizardService.getCurrentStepIndex()).toEqual(2);
	});

	it('see an exception if I pass duplicate step ids', () => {

		const init = wizardService.initialize.bind(wizardService, {
			steps: [
				createDummyStep(handle1),
				createDummyStep(handle1),
			]
		});

		expect(init).toThrow(new Error('Invalid (Duplicate) step id: step1.id'));
	});

	it('provide no stepUid and have one generated for me', () => {
		const step1 = createDummyStep(handle1);
		step1.id = null;
		wizardService.initialize({
			steps: [step1]
		} as any);

		expect(wizardService.getCurrentStep()).not.toBe(null);
	});

	it('remove a step by id', () => {
		initDefaultData();

		expect(wizardService.getStepsCount()).toBe(3);

		wizardService.removeStepById(getStepId(handle3));
		expect(wizardService.getStepsCount()).toBe(2);
		expect(wizardService.containsStep(getStepId(handle3))).toBe(false);
	});

	it('remove a step by index', () => {
		initDefaultData();

		expect(wizardService.getStepsCount()).toBe(3);

		wizardService.removeStepByIndex(1);
		expect(wizardService.getStepsCount()).toBe(2);
		expect(wizardService.containsStep(getStepId(handle2))).toBe(false);
	});

	it('add a step before the current step', () => {
		wizardService.initialize({
			steps: [createDummyStep(handle1), createDummyStep(handle2)]
		} as any);

		expect(wizardService.getStepsCount()).toBe(2);

		wizardService.addStep(createDummyStep(handle3), 0);
		expect(wizardService.getStepsCount()).toBe(3);
		expect(wizardService.getCurrentStepIndex()).toBe(1);
	});

	it('add a step after the current step', () => {
		wizardService.initialize({
			steps: [createDummyStep(handle1), createDummyStep(handle2)]
		} as any);

		expect(wizardService.getStepsCount()).toBe(2);

		wizardService.addStep(createDummyStep(handle3), 1);
		expect(wizardService.getStepsCount()).toBe(3);
		expect(wizardService.getCurrentStepIndex()).toBe(0);
	});

});
