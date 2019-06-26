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
import {SeInjectable} from "../dependencyInjection/SeInjectable";
import {IModalService} from "smarteditcommons/services/interfaces/IModalService";
import {WizardAction} from "./WizardActions";
import {ModalWizardControllerFactory} from "./ModalWizardControllerFactory";

/**
 * @ngdoc service
 * @name wizardServiceModule.modalWizard
 *
 * @description
 * The modalWizard service is used to create wizards that are embedded into the {@link modalServiceModule modalService}
 */
@SeInjectable()
export class ModalWizard {

	constructor(
		private modalService: IModalService,
	) {}

	/**
	 * @ngdoc method
	 * @name wizardServiceModule.modalWizard#open
	 * @methodOf wizardServiceModule.modalWizard
	 *
	 * @description
	 * Open provides a simple way to create modal wizards, with much of the boilerplate taken care of for you, such as look
	 * and feel, and wizard navigation.
	 *
	 * @param {WizardAction} conf configuration
	 * @param {String|function|Array} conf.controller An angular controller which will be the underlying controller
	 * for all of the wizard. This controller MUST implement the function <strong>getWizardConfig()</strong> which
	 * returns a {@link wizardServiceModule.object:ModalWizardConfig ModalWizardConfig}.<br />
	 * If you need to do any manual wizard manipulation, 'wizardManager' can be injected into your controller.
	 * See {@link wizardServiceModule.WizardManager WizardManager}
	 * @param {String} conf.controllerAs (OPTIONAL) An alternate controller name that can be used in your wizard step
	 * @param {=String=} conf.properties A map of properties to initialize the wizardManager with. They are accessible under wizardManager.properties.
	 * templates. By default the controller name is wizardController.
	 *
	 * @returns {function} {@link https://docs.angularjs.org/api/ng/service/$q promise} that will either be resolved (wizard finished) or
	 * rejected (wizard cancelled).
	 */
	open(config: WizardAction): angular.IPromise<any> {
		this.validateConfig(config);
		return this.modalService.open({
			templateUrl: 'modalWizardTemplate.html',
			controller: ModalWizardControllerFactory(config)
		});
	}

	private validateConfig(config: WizardAction): void {
		if (!config.controller) {
			throw new Error("WizardService - initialization exception. No controller provided");
		}
	}
}