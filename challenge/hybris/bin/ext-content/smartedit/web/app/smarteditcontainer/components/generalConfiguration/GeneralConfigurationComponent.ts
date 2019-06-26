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
import {ISeComponent, SeComponent} from "smarteditcommons/services/dependencyInjection/di";
import {ConfigurationService} from "./ConfigurationService";

/**
 * @ngdoc directive
 *
 * @name administration.directive:generalConfiguration
 * @restrict E
 * @element ANY
 *
 * @description
 * The Generation Configuration directive is an HTML marker. It attaches functions of the Configuration Editor to the
 * DOM elements of the General Configuration Template in order to display the configuration editor.
 *
 */
/** @internal */
@SeComponent({
	templateUrl: 'generalConfigurationTemplate.html',
	providers: [
		ConfigurationService
	]
})
export class GeneralConfigurationComponent implements ISeComponent {

	constructor(
		private $log: angular.ILogService,
		private modalService: any,
		private confirmationModalService: any,
		private MODAL_BUTTON_ACTIONS: any,
		private MODAL_BUTTON_STYLES: any,
	) {}

	editConfiguration() {
		const self = this;
		this.modalService.open({
			title: 'se.modal.administration.configuration.edit.title',
			templateUrl: 'editConfigurationsTemplate.html',
			/* tslint:disable:no-shadowed-variable */
			controller($scope: any, $timeout: angular.ITimeoutService, yjQuery: any, configurationService: any, $q: angular.IQService, modalManager: any) {
				'ngInject';
				this.isDirty = false;
				$scope.form = {};

				this.onSave = () => {
					$scope.editor.submit($scope.form.configurationForm).then(() => {
						modalManager.close();
					});
				};

				this.onCancel = function() {
					const deferred = $q.defer();

					if (this.isDirty) {
						self.confirmationModalService.confirm({
							description: 'se.editor.cancel.confirm'
						}).then(() => {
							modalManager.close();
							deferred.resolve();
						}, function() {
							deferred.reject();
						});
					} else {
						deferred.resolve();
					}

					return deferred.promise;
				};

				this.init = function() {
					modalManager.setDismissCallback((this.onCancel).bind(this));

					modalManager.setButtonHandler((buttonId: string) => {
						switch (buttonId) {
							case 'save':
								return this.onSave();
							case 'cancel':
								return this.onCancel();
							default:
								self.$log.error('A button callback has not been registered for button with id', buttonId);
								break;
						}
					});

					$scope.$watch(() => {
						const isDirty = $scope.form.configurationForm && $scope.form.configurationForm.$dirty;
						const isValid = $scope.form.configurationForm && $scope.form.configurationForm.$valid;
						return {
							isDirty,
							isValid
						};
					}, (obj: any) => {
						if (typeof obj.isDirty === 'boolean') {
							if (obj.isDirty) {
								this.isDirty = true;
								modalManager.enableButton('save');
							} else {
								this.isDirty = false;
								modalManager.disableButton('save');
							}
						}
					}, true);
				};

				$scope.editor = configurationService;
				$scope.editor.init(function() {
					$timeout(function() {
						yjQuery("textarea").each(
							function() {
								yjQuery(this).height(this.scrollHeight);
							});
					}, 100);
				});
			}
			,
			buttons: [{
				id: 'cancel',
				label: 'se.cms.component.confirmation.modal.cancel',
				style: this.MODAL_BUTTON_STYLES.SECONDARY,
				action: this.MODAL_BUTTON_ACTIONS.DISMISS
			}, {
				id: 'save',
				label: 'se.cms.component.confirmation.modal.save',
				action: this.MODAL_BUTTON_ACTIONS.NONE,
				disabled: true
			}]
		});
	}

}
