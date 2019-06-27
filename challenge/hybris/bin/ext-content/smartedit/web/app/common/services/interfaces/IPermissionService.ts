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
import {TypedMap} from "smarteditcommons";

export interface Rule {
	names: string[];
	verify: (permissionObjects?: PermissionContext[]) => angular.IPromise<boolean>;
}

export interface Permission {
	aliases: string[];
	rules: string[];
}

export interface MultiNamePermissionContext {
	names: string[];
	context?: TypedMap<any>;
}

export interface PermissionContext {
	name: string;
	context?: TypedMap<any>;
}

/* @internal */
export interface RuleNames {
	names: string[];
}

const prepareRuleConfiguration = function(ruleConfiguration: Rule): RuleNames {
	this.ruleVerifyFunctions = this.ruleVerifyFunctions || {};
	this.ruleVerifyFunctions[ruleConfiguration.names.join("-")] = {
		verify: ruleConfiguration.verify
	};
	delete ruleConfiguration.verify;

	return ruleConfiguration;
};

const validateRule = function(rule: Rule): void {
	if (!(rule.names instanceof Array)) {
		throw Error("Rule names must be array");
	}

	if (rule.names.length < 1) {
		throw Error("Rule requires at least one name");
	}

	if (!rule.verify) {
		throw Error("Rule requires a verify function");
	}

	if (typeof rule.verify !== 'function') {
		throw Error("Rule verify must be a function");
	}
};

/**
 * @ngdoc service
 * @name smarteditCommonsModule.service:PermissionServiceInterface
 *
 * @description
 * The permission service is used to check if a user has been granted certain permissions.
 *
 * It is configured with rules and permissions. A rule is used to execute some logic to determine whether or not
 * the permission should be granted. A permission references a list of rules. In order for a permission to be
 * granted, each rule must be executed successfully and return true.
 */
export abstract class IPermissionService {

	ruleVerifyFunctions: TypedMap<{verify: (obj: any) => angular.IPromise<boolean>}>;

	/**
	 * @ngdoc method
	 * @name smarteditCommonsModule.service:PermissionServiceInterface#clearCache
	 * @methodOf smarteditCommonsModule.service:PermissionServiceInterface
	 *
	 * @description
	 * This method clears all cached results in the rules' caches.
	 */
	clearCache(): void {
		'proxyFunction';
		return;
	}

	/**
	 * @ngdoc method
	 * @name smarteditCommonsModule.service:PermissionServiceInterface#getPermission
	 * @methodOf smarteditCommonsModule.service:PermissionServiceInterface
	 *
	 * @description
	 * This method returns the registered permission that contains the given name in its
	 * array of names.
	 *
	 * @param {String} permissionName The name of the permission to lookup.
	 *
	 * @returns {Object} rule The permission with the given name, undefined otherwise.
	 */
	getPermission(permission: string): Permission {
		'proxyFunction';
		return null;
	}

	/**
	 * @ngdoc method
	 * @name smarteditCommonsModule.service:PermissionServiceInterface#isPermitted
	 * @methodOf smarteditCommonsModule.service:PermissionServiceInterface
	 *
	 * @description
	 * This method checks if a user has been granted certain permissions.
	 *
	 * It takes an array of permission objects structured as follows:
	 *
	 * {
	 *     names: ["permission.aliases"],
	 *     context: {
	 *         data: "required to check a permission"
	 *     }
	 * }
	 *
	 * @param {Object[]} permissions A list of permission objects.
	 *
	 * @returns {IPromise} A promise that resolves to true if permission is granted, rejects to false if it isn't and rejects on error.
	 */
	isPermitted(permissions: MultiNamePermissionContext[]): angular.IPromise<boolean> {
		'proxyFunction';
		return null;
	}

	/**
	 * @ngdoc method
	 * @name smarteditCommonsModule.service:PermissionServiceInterface#registerPermission
	 * @methodOf smarteditCommonsModule.service:PermissionServiceInterface
	 *
	 * @description
	 * This method registers a permission.
	 *
	 * A permission is defined by a set of aliases and rules. It is verified by its set of rules.
	 * The set of aliases is there for convenience, as there may be different permissions
	 * that use the same set of rules to be verified. The permission aliases property
	 * will resolve if any one alias is in the aliases' array. Calling {@link smarteditCommonsModule.service:PermissionServiceInterface#isPermitted}
	 * with any of these aliases will use the same permission object, therefore the same
	 * combination of rules to check if the user has the appropriate clearance. This reduces the
	 * number of permissions you need to register.
	 *
	 * @param {Object} configuration The configuration of the permission to register.
	 * @param {String[]} configuration.aliases The list of aliases associated to the permission. A permission alias must be prefixed by at least one
	 * namespace followed by a "." character to be valid. i.e. "se.fake.permission"
	 * @param {String[]} configuration.rules The list of the names of the rules used to verify.
	 *
	 * @throws Will throw an error if the permission has no aliases array
	 * @throws Will throw an error if the permission's aliases array is empty
	 * @throws Will throw an error if the permission has no rules array
	 * @throws Will throw an error if the permission's rule aliases array is empty
	 * @throws Will throw an error if a permission is already registered with a common entry in its array of aliases
	 * @throws Will throw an error if one of the permission's aliases is not name spaced
	 * @throws Will throw an error if no rule is registered with on of the permission's rule names
	 */
	registerPermission(permission: Permission): void {
		'proxyFunction';
		return;
	}

	/**
	 * @ngdoc method
	 * @name smarteditCommonsModule.service:PermissionServiceInterface#registerRule
	 * @methodOf smarteditCommonsModule.service:PermissionServiceInterface
	 *
	 * @description
	 * This method registers a rule. These rules can be used by registering permissions that
	 * use them to verify if a user has the appropriate clearance.
	 *
	 * To avoid accidentally overriding the default rule, an error is thrown when attempting
	 * to register a rule with the {@link smarteditServicesModule.object:DEFAULT_RULE_NAME
	 * default rule name}.
	 *
	 * To register the default rule, see {@link smarteditCommonsModule.service:PermissionServiceInterface#registerDefaultRule}.
	 *
	 * @param {Object} ruleConfiguration The configuration of the rule to register.
	 * @param {String[]} ruleConfiguration.names The list of names associated to the rule.
	 * @param {Function} ruleConfiguration.verify The verification function of the rule. It must return a promise that responds with true, false, or an error.
	 *
	 * @throws Will throw an error if the list of rule names contains the reserved {@link smarteditServicesModule.object:DEFAULT_RULE_NAME default rule name}.
	 * @throws Will throw an error if the rule has no names array.
	 * @throws Will throw an error if the rule's names array is empty.
	 * @throws Will throw an error if the rule has no verify function.
	 * @throws Will throw an error if the rule's verify parameter is not a function.
	 * @throws Will throw an error if a rule is already registered with a common entry in its names array
	 */
	registerRule(ruleConfiguration: Rule): void {
		validateRule(ruleConfiguration);
		ruleConfiguration = prepareRuleConfiguration.bind(this)(ruleConfiguration);
		this._registerRule(ruleConfiguration);
	}

	/**
	 * @ngdoc method
	 * @name smarteditCommonsModule.service:PermissionServiceInterface#registerDefaultRule
	 * @methodOf smarteditCommonsModule.service:PermissionServiceInterface
	 *
	 * @description
	 * This method registers the default rule.
	 *
	 * The default rule is used when no permission is found for a given permission name when
	 * {@link smarteditCommonsModule.service:PermissionServiceInterface#isPermitted} is called.
	 *
	 * @param {Object} ruleConfiguration The configuration of the default rule.
	 * @param {String[]} ruleConfiguration.names The list of names associated to the default rule (must contain {@link smarteditServicesModule.object:DEFAULT_RULE_NAME}).
	 * @param {Function} ruleConfiguration.verify The verification function of the default rule.
	 *
	 * @throws Will throw an error if the default rule's names does not contain {@link smarteditServicesModule.object:DEFAULT_RULE_NAME}
	 * @throws Will throw an error if the default rule has no names array.
	 * @throws Will throw an error if the default rule's names array is empty.
	 * @throws Will throw an error if the default rule has no verify function.
	 * @throws Will throw an error if the default rule's verify parameter is not a function.
	 * @throws Will throw an error if a rule is already registered with a common entry in its names array
	 */
	registerDefaultRule(ruleConfiguration: Rule): void {
		ruleConfiguration = prepareRuleConfiguration.bind(this)(ruleConfiguration);
		this._registerDefaultRule(ruleConfiguration);
	}

	/**
	 * @ngdoc method
	 * @name smarteditCommonsModule.service:PermissionServiceInterface#unregisterDefaultRule
	 * @methodOf smarteditCommonsModule.service:PermissionServiceInterface
	 *
	 * @description
	 * This method un-registers the default rule.
	 */
	unregisterDefaultRule(): void {
		'proxyFunction';
		return;
	}

	protected _registerRule(ruleConfiguration: Rule): void {
		'proxyFunction';
		return;
	}

	protected _registerDefaultRule(ruleConfiguration: Rule): void {
		'proxyFunction';
		return;
	}

}