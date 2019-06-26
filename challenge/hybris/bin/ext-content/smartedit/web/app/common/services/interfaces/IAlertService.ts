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
export enum SeAlertServiceType {
	Info = "INFO",
	Success = "SUCCESS",
	Warning = "WARNING",
	Danger = "DANGER"
}

export interface IAlertConfig {
	message?: string;
	type?: SeAlertServiceType;
	messagePlaceholders?: {[key: string]: any};
	template?: string;
	templateUrl?: string;
	closeable?: boolean;
	timeout?: number;
}

export interface IAlertService {
	showAlert(alertConf: IAlertConfig | string): void;
	showInfo(alertConf: IAlertConfig | string): void;
	showDanger(alertConf: IAlertConfig | string): void;
	showWarning(alertConf: IAlertConfig | string): void;
	showSuccess(alertConf: IAlertConfig | string): void;
}