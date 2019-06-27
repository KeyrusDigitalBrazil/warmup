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

import {GatewayProxied, IPermissionService, PermissionContext, SeInjectable} from "smarteditcommons";
import * as angular from "angular";

@SeInjectable()
@GatewayProxied()
export class PermissionService extends IPermissionService {

	constructor(
		private $log: angular.ILogService,
	) {
		super();
	}

	_remoteCallRuleVerify(ruleKey: string, permissionNameObjs: PermissionContext[]) {
		if (this.ruleVerifyFunctions && this.ruleVerifyFunctions[ruleKey]) {
			return this.ruleVerifyFunctions[ruleKey].verify(permissionNameObjs);
		}

		this.$log.warn("could not call rule verify function for rule key: " + ruleKey + ", it was not found in the iframe");
		return null;
	}

}