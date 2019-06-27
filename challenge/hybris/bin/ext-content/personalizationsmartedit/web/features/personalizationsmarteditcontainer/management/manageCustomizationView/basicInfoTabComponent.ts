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

/* @ngInject */
class BasicInfoTabController implements angular.IController {

	public datetimeConfigurationEnabled: boolean = false;

	private _customization: any;
	get customization(): any {
		return this._customization;
	}
	set customization(value: any) {
		this._customization = value;
		this.datetimeConfigurationEnabled = (this.customization.enabledStartDate || this.customization.enabledEndDate);
	}

	constructor(
		public PERSONALIZATION_MODEL_STATUS_CODES: any,
		public DATE_CONSTANTS: any
	) {}

	resetDateTimeConfiguration(): void {
		this.customization.enabledStartDate = undefined;
		this.customization.enabledEndDate = undefined;
	}

	customizationStatusChange(): void {
		this.customization.status = this.customization.statusBoolean ? this.PERSONALIZATION_MODEL_STATUS_CODES.ENABLED : this.PERSONALIZATION_MODEL_STATUS_CODES.DISABLED;
	}

}

export const basicInfoTabComponent: angular.IComponentOptions = {
	controller: BasicInfoTabController,
	templateUrl: 'basicInfoTabTemplate.html',
	bindings: {
		customization: '=?'
	}
};