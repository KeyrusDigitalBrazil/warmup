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
import * as angular from 'angular';
import {manageCustomizationViewComponent} from "personalizationsmarteditcontainer/management/manageCustomizationView/manageCustomizationViewComponent";
import {basicInfoTabComponent} from "personalizationsmarteditcontainer/management/manageCustomizationView/basicInfoTabComponent";
import {targetGroupTabComponent} from "personalizationsmarteditcontainer/management/manageCustomizationView/targetGroupTabComponent";
import {TriggerService} from "personalizationsmarteditcontainer/management/manageCustomizationView/triggerService";

export const personalizationsmarteditManageCustomizationViewModule = angular.module('personalizationsmarteditManageCustomizationViewModule', [
	'modalServiceModule',
	'coretemplates',
	'ui.select',
	'confirmationModalServiceModule',
	'functionsModule',
	'personalizationsmarteditCommons',
	'personalizationsmarteditDataFactory',
	'sliderPanelModule',
	'seConstantsModule',
	'yjqueryModule',
	'personalizationsmarteditCommonsModule'
])
	.constant('CUSTOMIZATION_VARIATION_MANAGEMENT_TABS_CONSTANTS', {
		BASIC_INFO_TAB_NAME: 'basicinfotab',
		BASIC_INFO_TAB_FORM_NAME: 'form.basicinfotab',
		TARGET_GROUP_TAB_NAME: 'targetgrptab',
		TARGET_GROUP_TAB_FORM_NAME: 'form.targetgrptab'
	})
	.constant('CUSTOMIZATION_VARIATION_MANAGEMENT_BUTTONS', {
		CONFIRM_OK: 'confirmOk',
		CONFIRM_CANCEL: 'confirmCancel',
		CONFIRM_NEXT: 'confirmNext'
	})
	.constant('CUSTOMIZATION_VARIATION_MANAGEMENT_SEGMENTTRIGGER_GROUPBY', {
		CRITERIA_AND: 'AND',
		CRITERIA_OR: 'OR'
	})
	.constant('DATE_CONSTANTS', {
		ANGULAR_FORMAT: 'short',
		MOMENT_FORMAT: 'M/D/YY h:mm A',
		MOMENT_ISO: 'YYYY-MM-DDTHH:mm:00ZZ',
		ISO: 'yyyy-MM-ddTHH:mm:00Z'
	})
	.component('manageCustomizationViewComponent', manageCustomizationViewComponent)
	.component('basicInfoTabComponent', basicInfoTabComponent)
	.component('targetGroupTabComponent', targetGroupTabComponent)
	.service('personalizationsmarteditTriggerService', TriggerService)
	.factory('personalizationsmarteditManager', function(
		modalService: any,
		MODAL_BUTTON_ACTIONS: any,
		MODAL_BUTTON_STYLES: any,
		CUSTOMIZATION_VARIATION_MANAGEMENT_BUTTONS: any) {
		'ngInject';

		this.openCreateCustomizationModal = function() {
			return modalService.open({
				title: 'personalization.modal.customizationvariationmanagement.title',
				templateInline: '<manage-customization-view-component data-modal-manager="modalManager"/>',
				controller: ['$scope', 'modalManager', function($scope: any, modalManager: any) {
					'ngInject';
					$scope.modalManager = modalManager;
				}],
				buttons: [{
					id: CUSTOMIZATION_VARIATION_MANAGEMENT_BUTTONS.CONFIRM_CANCEL,
					label: 'personalization.modal.customizationvariationmanagement.button.cancel',
					style: MODAL_BUTTON_STYLES.SECONDARY
				}],
				size: 'lg sliderPanelParentModal'
			});
		};

		this.openEditCustomizationModal = function(customizationCode: string, variationCode: string) {
			return modalService.open({
				title: 'personalization.modal.customizationvariationmanagement.title',
				templateInline: '<manage-customization-view-component data-modal-manager="modalManager" data-customization-code="customizationCode" data-variation-code="variationCode"/>',
				controller: ['$scope', 'modalManager', function($scope: any, modalManager: any) {
					'ngInject';
					$scope.customizationCode = customizationCode;
					$scope.variationCode = variationCode;
					$scope.modalManager = modalManager;
				}],
				buttons: [{
					id: 'confirmCancel',
					label: 'personalization.modal.customizationvariationmanagement.button.cancel',
					style: MODAL_BUTTON_STYLES.SECONDARY
				}],
				size: 'lg sliderPanelParentModal'
			});
		};

		return this;
	});
