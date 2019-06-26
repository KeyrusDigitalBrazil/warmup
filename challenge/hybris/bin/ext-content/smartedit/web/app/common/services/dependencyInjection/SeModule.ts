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
/* forbiddenNameSpaces angular.module:false */
import * as angular from 'angular';
import {functionsUtils} from 'smarteditcommons/utils/FunctionsUtils';
import {diNameUtils} from './DINameUtils';
import {
	SeBaseProvider,
	SeClassProvider,
	SeComponentConstructor,
	SeConstructor,
	SeDirectiveConstructor,
	SeFactory,
	SeFactoryProvider,
	SeModuleConstructor,
	SeModuleWithProviders,
	SeProvider,
	SeValueProvider
} from "./types";

const MultiProviderMap: {
	[key: string]: string[]
} = {};

/**
 * @ngdoc object
 * @name smarteditServicesModule.object:@SeModule
 *
 * @description
 * Class level typescript {@link http://www.typescriptlang.org/docs/handbook/decorators.html decorator factory}
 * used to declare a Smartedit module from a Dependency injection standpoint.
 *
 * To create a configurable module, create a static method returning an SeModuleWithProvider object. The module
 * can then be imported by a parent module returning the SeModuleWithProvider object from the static method.
 * 
 * @param {object} definition the module definition
 * @param {object} definition.declarations the array of {@link smarteditServicesModule.object:@SeDirective @SeDirective} and {@link smarteditServicesModule.object:@SeComponent @SeComponent} on which this new {@link smarteditServicesModule.object:@SeModule @SeModule} depends.
 * @param {object} definition.imports the array of modules on which this new module depends.
 * <br/> This is a mixed array of string (legacy approach) and {@link smarteditServicesModule.object:@SeModule @SeModule} annotated classes (recommended approach).
 * @param {object} definition.providers the list of {@link smarteditServicesModule.interface:SeClassProvider service classes},
 * {@link smarteditServicesModule.interface:SeFactoryProvider service factories}, {@link smarteditServicesModule.interface:SeValueProvider value},
 * or multi providers to be injected into this new module.
 * @param {Array} definition.config the injectable callback to be executed at configuration time
 * @param {Array} definition.initialize the injectable callback to be executed at startup time
 */

'se:smarteditcommons';
export const SeModule = function(definition: {
	declarations?: (SeDirectiveConstructor | SeComponentConstructor)[],
	imports?: (string | SeModuleConstructor | SeModuleWithProviders)[],
	providers?: SeProvider[],
	config?: (...args: any[]) => void,
	initialize?: (...args: any[]) => void
}) {
	return function <T extends SeModuleConstructor>(moduleConstructor: T) {

		const seModuleName = diNameUtils.buildName(moduleConstructor);
		const angularInstance = getAngular();
		const allImports: string[] = [];

		if (definition.imports) {
			definition.imports.forEach((importStatement, index) => {

				const throwUnAnnotatedModuleError = (seModule: SeModuleConstructor) => {
					const importedModule = diNameUtils.buildName(seModule);
					throw new Error(`${importedModule} module was imported into ${seModuleName} module but doesn't seem to have been @SeModule annotated`);
				};

				let moduleName;
				if (typeof importStatement === 'string') {
					moduleName = importStatement;
				} else if (typeof importStatement === 'function') {
					moduleName = (importStatement as SeModuleConstructor).moduleName;
					if (!moduleName) {
						throwUnAnnotatedModuleError(importStatement);
					}
				} else if (importStatement && importStatement.seModule) {
					if (!importStatement.seModule.moduleName) {
						throwUnAnnotatedModuleError(importStatement.seModule);
					}

					const moduleWithProvidersName = diNameUtils.buildName(importStatement.seModule);
					const moduleWithProviders = angularInstance.module(moduleWithProvidersName);

					moduleName = moduleWithProviders.name;

					if (importStatement.providers) {
						addArrayOfProvidersToModule(moduleWithProviders, importStatement.providers);
					}
				} else {
					throw new Error(`the import statement ${importStatement} at index ${index} added to ${seModuleName} is neither a legacy string nor an SeModuleConstructor`);
				}
				if (allImports.indexOf(moduleName) > -1) {
					throw new Error(`module ${moduleName} is imported more than once into ${seModuleName}`);
				}
				allImports.push(moduleName);
			});
		}

		const module = angularInstance.module(seModuleName, allImports);

		if (definition.providers) {
			addArrayOfProvidersToModule(module, definition.providers);
		}

		if (definition.declarations) {
			definition.declarations.forEach((comp) => {
				addFullComponentGraphToModule(module, comp);
			});
		}

		if (definition.config) {
			module.config(definition.config);
		}

		if (definition.initialize) {
			module.run(definition.initialize);
		}

		moduleConstructor.moduleName = module.name;
		return moduleConstructor;
	};

};

function addArrayOfProvidersToModule(module: angular.IModule, providers: SeProvider[]) {
	providers.forEach((provider: SeProvider, index: number) => {
		const moduleName = module.name;

		if (!provider) {
			throw new Error(
				`At the time a provider at index ${index} was added to module ${moduleName},
				it was undefined, this is probably due to the path in your typescript import statement
				referencing a barrel file of an alias defined in a higher layer, consider using a relative path instead.`
			);
		}

		if ((provider as SeBaseProvider).provide && !(provider as SeValueProvider).useValue &&
			!(provider as SeClassProvider).useClass && !(provider as SeFactoryProvider).useFactory) {
			throw new Error(
				`At the time a provider named ${(provider as SeBaseProvider).provide} was added to module ${moduleName}
		        did not provide an instance of SeValueProvider, SeClassProvider, or FactoryProvider.`
			);
		}

		if ((provider as (SeValueProvider | SeFactoryProvider | SeClassProvider)).multi) {
			provider = (provider as SeValueProvider | SeFactoryProvider | SeClassProvider);
			addMultiProviderToModule(module, provider);
		} else {
			addProviderToModule(module, provider);
		}
	});
}

function addProviderToModule(module: angular.IModule, provider: SeProvider) {
	if ((provider as SeValueProvider).useValue) {

		provider = provider as SeValueProvider;
		module.constant(provider.provide, provider.useValue);

	} else if ((provider as SeClassProvider).useClass) {

		provider = provider as SeClassProvider;
		module.service(provider.provide, provider.useClass);

	} else if ((provider as SeFactoryProvider).useFactory) {
		provider = provider as SeFactoryProvider;

		const isNgAnnotated = Array.isArray(provider.useFactory) || provider.useFactory.$inject;

		if (isNgAnnotated && provider.deps) {
			throw Error(`At the time a provider ${provider.provide} uses ngInject annotations and 
			SeFactoryProvider.deps at the same time. Please use one or the other.`);
		}

		const dependencies = provider.deps ? provider.deps.map((dependency: SeConstructor | SeFactory | string) => {
			return typeof dependency === 'string' ? dependency : diNameUtils.buildServiceName(dependency);
		}) : [];

		// In current framework, this is only needed for case of multi and for uglify ready di
		module.factory(provider.provide, isNgAnnotated ? provider.useFactory : [...dependencies, provider.useFactory]);
	} else {
		provider = provider as SeFactory | SeConstructor;

		const serviceName = diNameUtils.buildServiceName(provider);

		module.service(serviceName, provider);
	}
}

function addMultiProviderToModule(module: angular.IModule, provider: SeValueProvider | SeClassProvider | SeFactoryProvider) {
	const multiProviderMapName = module.name + provider.provide;
	let dependencies = MultiProviderMap[multiProviderMapName];

	if (!dependencies) {
		dependencies = [];
	}

	const multiProviderInstance = multiProviderMapName + functionsUtils.getLodash().uniqueId();

	dependencies.push(multiProviderInstance);
	MultiProviderMap[multiProviderMapName] = dependencies;

	const useFactory = function() {
		return [].slice.call(arguments);
	};

	addProviderToModule(module, {...provider, provide: multiProviderInstance});
	addProviderToModule(module, {
		provide: provider.provide,
		useFactory,
		deps: dependencies
	});
}

function addFullComponentGraphToModule(module: angular.IModule, component: SeDirectiveConstructor | SeComponentConstructor) {

	const definition = component.definition;
	if (!definition) {
		const componentConstructorName = functionsUtils.getConstructorName(component);
		throw new Error(`${componentConstructorName} component was imported into ${module.name} module but doesn't seem to have been @SeComponent or @SeDirective annotated`);
	}

	if (component.providers) {
		addArrayOfProvidersToModule(module, component.providers);
	}

	const componentName = (component as SeComponentConstructor).componentName;
	const directivename = (component as SeDirectiveConstructor).directiveName;

	if (componentName) {
		module.component(componentName, definition as angular.IComponentOptions);

		delete component.definition;

		const entryComponents = (component as SeComponentConstructor).entryComponents;
		if (entryComponents) {
			entryComponents.forEach((entryComponent: any) => {
				addFullComponentGraphToModule(module, entryComponent);
			});

		}
		delete (component as SeComponentConstructor).entryComponents;
	} else if (directivename) {
		module.directive(directivename, () => definition as angular.IDirective);
	}
}

// For testing purposes.
(SeModule as any).getAngular = function() {
	return angular;
};

function getAngular() {
	return (SeModule as any).getAngular();
}
