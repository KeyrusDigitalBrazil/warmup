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

export interface IReflectable<T> {

	setMethod?(name: string, methodInstance: (...params: any[]) => (angular.IPromise<void | T> | angular.IPromise<T[]>)): void;
	getMethodForVoid?(name: string): (...params: any[]) => angular.IPromise<void>;
	getMethodForSingleInstance?(name: string): (...params: any[]) => angular.IPromise<T>;
	getMethodForArray?(name: string): (...params: any[]) => angular.IPromise<T[]>;

}