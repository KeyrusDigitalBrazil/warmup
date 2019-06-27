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
import {TypedMap} from 'smarteditcommons';
import {functionsUtils} from 'smarteditcommons/utils/FunctionsUtils';
import {SeConstructor, SeDirectiveConstructor, SeDirectiveDefinition} from './types';
import {diNameUtils} from './DINameUtils';

/** @internal */
export const parseDirectiveBindings = function(inputs: string[]) {
	let bindings: TypedMap<string>;

	if (inputs && (inputs as any).length) {
		bindings = inputs.reduce((seed: any, element) => {
			const values = element.replace(/\s/g, "").split(":");
			let bindingProperty = values[values.length - 1];
			if (!bindingProperty.startsWith("@")
				&& !bindingProperty.startsWith("&")
				&& !bindingProperty.startsWith("=")) {
				bindingProperty = '<' + bindingProperty;
			}
			seed[values[0]] = bindingProperty;
			return seed;
		}, {});
	}
	return bindings;
};

/** @internal */
export const parseDirectiveName = function(selector: string, seContructor: SeConstructor): {name: string, restrict: string} {

	const attributeDirectiveNamePattern = /^\[([-\w]+)\]$/;
	const elementDirectiveNamePattern = /^([-\w]+)$/;
	const lodash = (window as any).smarteditLodash;

	if (!selector) {
		return {name: diNameUtils.buildComponentName(seContructor), restrict: "E"};
	} else if (selector.startsWith(".")) {
		return {name: lodash.camelCase(selector.substring(1)), restrict: "C"};
	} else if (attributeDirectiveNamePattern.test(selector)) {
		return {name: lodash.camelCase(attributeDirectiveNamePattern.exec(selector)[1]), restrict: "A"};
	} else if (elementDirectiveNamePattern.test(selector)) {
		return {name: lodash.camelCase(elementDirectiveNamePattern.exec(selector)[1]), restrict: "E"};
	} else {
		const directiveClassName = functionsUtils.getConstructorName(seContructor);
		throw new Error(`SeDirective ${directiveClassName} declared an unexpected selector (${selector}). 
		Make sure to use an element name or class (.class) or attribute ([attribute])`);
	}
};

/**
 * @ngdoc object
 * @name smarteditServicesModule.object:@SeDirective
 *
 * @description
 * Class level typescript {@link http://www.typescriptlang.org/docs/handbook/decorators.html decorator factory}
 * used to declare a Smartedit web directive from a Depencency injection standpoint.
 * This directive will have an isolated scope and will bind its properties to its controller
 * @param {object} definition the component definition
 * @param {string?} definition.selector The CSS selector that triggers the instantiation of a directive.
 * selector may be declared as one of the following:
 * <ul>
 * <li>element-name: select by element name.</li>
 * <li>.class: select by class name.</li>
 * <li>[attribute]: select by attribute name.</li>
 * </ul>
 * If no selector is set, will default to an element named as the lower camel case of the component class.
 * @param {string[]?} definition.inputs the array of input data binding
 * The inputs property defines a set of directiveProperty to bindingProperty configuration:
 * <ul>
 * <li>directiveProperty specifies the component property where the value is written.</li>
 * <li>bindingProperty specifies the binding type and/or the DOM property where the value is read from.</li>
 * binding type is legacy support for "@", "&" and "=" of Angular 1.x
 * </ul>
 * example: inputs: ['bankName', 'id: account-id']
 * @param {object} definition.providers the list of {@link smarteditServicesModule.interface:SeClassProvider service classes},
 * {@link smarteditServicesModule.interface:SeFactoryProvider service factories}, {@link smarteditServicesModule.interface:SeValueProvider value},
 * or multi providers to be injected into the component.
 */
'se:smarteditcommons';
export const SeDirective = function(definition: SeDirectiveDefinition) {
	return function(directiveConstructor: SeDirectiveConstructor) {

		const directive: angular.IDirective = {
			controller: directiveConstructor,
			scope: {},
			bindToController: parseDirectiveBindings(definition.inputs) || true,
			require: definition.require
		};

		const nameSet = parseDirectiveName(definition.selector, directiveConstructor);

		directive.restrict = nameSet.restrict;

		directiveConstructor.directiveName = nameSet.name;
		directiveConstructor.definition = directive;

		// will be browsed by owning @SeModule
		directiveConstructor.providers = definition.providers;

		return directiveConstructor;
	};
};