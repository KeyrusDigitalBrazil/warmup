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
import {SeComponentConstructor, SeComponentDefinition} from './types';
import {parseDirectiveBindings, parseDirectiveName} from './SeDirective';
import {functionsUtils} from 'smarteditcommons/utils/FunctionsUtils';
import {TypedMap} from "smarteditcommons";

/**
 * @ngdoc object
 * @name smarteditServicesModule.object:@SeComponent
 *
 * @description
 * Class level typescript {@link http://www.typescriptlang.org/docs/handbook/decorators.html decorator factory}
 * used to declare a Smartedit web component from a Depencency injection standpoint.
 * The controller alias will be $ctrl.
 * inherits properties from {@link smarteditServicesModule.object:@SeDirective}
 * @param {object} definition the component definition
 * @param {string?} definition.templateUrl the HTML file location for this component
 * @param {string?} definition.template the inline HTML template for this component
 * @param {object?} definition.entryComponents the array of {@link smarteditServicesModule.object:@SeComponent @SeComponent} that this new one requires.
 * @param {object} definition.providers the list of {@link smarteditServicesModule.interface:SeClassProvider service classes},
 * {@link smarteditServicesModule.interface:SeFactoryProvider service factories}, {@link smarteditServicesModule.interface:SeValueProvider value},
 * or multi providers to be injected into the component.
 */
'se:smarteditcommons';
export const SeComponent = function(definition: SeComponentDefinition) {
	return function <T extends SeComponentConstructor>(componentConstructor: T) {

		const component: angular.IComponentOptions = {
			controller: componentConstructor,
			controllerAs: '$ctrl',
			transclude: true,
			bindings: parseDirectiveBindings(definition.inputs),
			require: definition.require as TypedMap<string>
		};

		if (definition.templateUrl) {
			component.templateUrl = definition.templateUrl;
		} else if (definition.template) {
			component.template = definition.template;
		}
		const nameSet = parseDirectiveName(definition.selector, componentConstructor);

		if (nameSet.restrict !== "E") {
			const componentName = functionsUtils.getConstructorName(componentConstructor);
			throw new Error(`component ${componentName} declared a selector on class or attribute. version 1808 of Smartedit DI limits SeComponents to element selectors`);
		}

		componentConstructor.componentName = nameSet.name;
		componentConstructor.definition = component;

		// will be browsed by owning @SeModule
		componentConstructor.entryComponents = definition.entryComponents;
		componentConstructor.providers = definition.providers;

		return componentConstructor;
	};
};