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
import {SeComponent, SeDirective, SeInjectable, SeModule, SeModuleWithProviders} from "smarteditcommons";

// tslint:disable:max-classes-per-file

describe('SeModule Annotation', () => {

	let mockAngular: any;
	let mockAModule: any;
	let mockBModule: any;

	beforeEach(() => {

		mockAngular = jasmine.createSpyObj('mockAngular', ['module']);

		(SeModule as any).getAngular = function() {
			return mockAngular;
		};

		mockAModule = jasmine.createSpyObj('mockAModule', ['service', 'constant', 'factory', 'config', 'run', 'component', 'directive']);
		mockBModule = jasmine.createSpyObj('mockBModule', ['service', 'constant', 'factory', 'config', 'run', 'component', 'directive']);

		mockAModule.name = 'aModule';
		mockBModule.name = 'bModule';

		mockAngular.module.and.callFake((name: string) => {
			if (name === mockAModule.name) {
				return mockAModule;
			} else if (name === mockBModule.name) {
				return mockBModule;
			}

			throw new Error('module not found');
		});
	});

	it('SmartEdit annotates module', () => {

		class SomeFunctionModule {
			static moduleName = 'someFunctionModule';
		}

		const initialize = () => {
			return 'initialized';
		};

		const configure = () => {
			return 'configured';
		};

		@SeModule({
			initialize,
			config: configure,
			imports: [
				SomeFunctionModule,
				'anotherDummyModule'
			]
		})
		class AModule {}
		!AModule;

		expect(mockAModule.config.calls.argsFor(0)[0]).toBe(configure);
		expect(mockAModule.config.calls.argsFor(0)[0]()).toBe('configured');

		expect(mockAModule.run.calls.argsFor(0)[0]).toBe(initialize);
		expect(mockAModule.run.calls.argsFor(0)[0]()).toBe('initialized');

		expect(mockAngular.module.calls.argsFor(0)).toEqual(['aModule', ['someFunctionModule', 'anotherDummyModule']]);
	});

	it('SeModule to throw error when configurable class module has not yet been annotated', () => {

		class BModule {
			static configure(): SeModuleWithProviders {
				return {
					seModule: BModule,
					providers: [

					]
				};
			}
		}

		expect(() => {
			@SeModule({
				imports: [
					BModule.configure()
				]
			})
			class AModule {}
			!AModule;
		}).toThrow(new Error('bModule module was imported into aModule module but doesn\'t seem to have been @SeModule annotated'));

		expect(mockAngular.module).toHaveBeenCalledTimes(0);
	});

	it('BModule is a configurable module used by AModule with Value, Class, Factory, and Constructor type providers ', () => {
		class BService {

		}

		@SeInjectable()
		class BClassService {

		}

		@SeModule({
			imports: [
				'bSomeRandomImportA',
				'bSomeRandomImportB',
			]
		})
		class BModule {
			static config(configuration: string): SeModuleWithProviders {
				return {
					seModule: BModule,
					providers: [
						{
							provide: 'bValueProvider',
							useValue: configuration
						},
						{
							provide: 'bClassProvider',
							useClass: BClassService
						},
						{
							provide: 'bFactoryProvider',
							useFactory: () => {
								return 'helloFromBFactoryProvider';
							}
						},
						{
							provide: 'bFactoryProviderWithNgAnnotation',
							useFactory: (someRandomDep: any) => {
								'ngInject';
								return 'helloFromBFactoryProvider';
							}
						},
						BService
					]
				};
			}
		}

		@SeModule({
			imports: [
				BModule.config('bConfigurableString')
			]
		})
		class AModule {}
		!AModule;

		expect(mockAngular.module.calls.argsFor(0)).toEqual(['bModule', ['bSomeRandomImportA', 'bSomeRandomImportB']]); // Decorate module
		expect(mockAngular.module.calls.argsFor(1)).toEqual(['bModule']); // Resolve seModule with provider
		expect(mockAngular.module.calls.argsFor(2)).toEqual(['aModule', ['bModule']]); // Complete decoration of module
		expect(mockAngular.module).toHaveBeenCalledTimes(3);

		expect(mockBModule.constant.calls.argsFor(0)).toEqual(['bValueProvider', 'bConfigurableString']);
		expect(mockBModule.service.calls.argsFor(0)).toEqual(['bClassProvider', BClassService]);
		expect(mockBModule.service.calls.argsFor(1)).toEqual(['bService', BService]);
		expect(mockBModule.factory.calls.argsFor(0)).toEqual(['bFactoryProvider', [jasmine.any(Function)]]);
		expect(mockBModule.factory.calls.argsFor(0)[1][0]()).toBe('helloFromBFactoryProvider');
		expect(mockBModule.factory.calls.argsFor(1)[0]).toEqual('bFactoryProviderWithNgAnnotation');
		expect(mockBModule.factory.calls.argsFor(1)[1]).toEqual(['someRandomDep', jasmine.any(Function)]);
	});

	it('Module A registers two multi Value providers', () => {
		@SeModule({
			providers: [
				{
					provide: 'ROUTE',
					useValue: '/ go home',
					multi: true
				},
				{
					provide: 'ROUTE',
					useValue: '/about go to about page',
					multi: true
				}
			]
		})
		class AModule {}
		!AModule;

		expect(mockAModule.constant).toHaveBeenCalledTimes(2);
		expect(mockAModule.factory.calls.argsFor(0)[0]).toBe('ROUTE');
		expect(mockAModule.factory.calls.argsFor(0)[1].length).toBe(2); // First time register with one dep

		expect(mockAModule.factory.calls.argsFor(1)[1].length).toBe(3); // Second time register with two deps include the factory function
		expect(mockAModule.factory.calls.argsFor(1)[1][0]).toEqual(jasmine.any(String));
		expect(mockAModule.factory.calls.argsFor(1)[1][1]).toEqual(jasmine.any(String));
		expect(mockAModule.factory.calls.argsFor(1)[1][2]('a', 'b')).toEqual(['a', 'b']);
	});

	it('Module B can register providers in configuration and annotation', () => {
		class BService {

		}

		@SeModule({
			providers: [
				BService,
				{
					provide: 'MULTI',
					useValue: 'a',
					multi: true
				},
				{
					provide: 'MULTI',
					useValue: 'b',
					multi: true
				}
			]
		})
		class BModule {
			static config(configuration: string): SeModuleWithProviders {
				return {
					seModule: BModule,
					providers: [
						{
							provide: 'bValueProvider',
							useValue: configuration
						},
						{
							provide: 'MULTI',
							useValue: 'c',
							multi: true
						}
					]
				};
			}
		}

		@SeModule({
			imports: [
				BModule.config('hello')
			]
		})
		class AModule {}
		!AModule;

		expect(mockAngular.module).toHaveBeenCalledTimes(3);

		expect(mockBModule.constant.calls.argsFor(0)[1]).toEqual('a');
		expect(mockBModule.constant.calls.argsFor(1)[1]).toEqual('b');
		expect(mockBModule.constant.calls.argsFor(2)[1]).toEqual('hello');
		expect(mockBModule.constant.calls.argsFor(3)[1]).toEqual('c');

		expect(mockBModule.factory.calls.argsFor(2)[0]).toBe('MULTI');
		expect(mockBModule.factory.calls.argsFor(2)[1][0]).toEqual(jasmine.any(String));
		expect(mockBModule.factory.calls.argsFor(2)[1][1]).toEqual(jasmine.any(String));
		expect(mockBModule.factory.calls.argsFor(2)[1][2]).toEqual(jasmine.any(String));
		expect(mockBModule.factory.calls.argsFor(2)[1][3]).toEqual(jasmine.any(Function));
	});

	it('Module A will register the providers on SeComponents and SeDirectives', () => {
		@SeComponent({
			template: '<div>Hello</div>',
			providers: [
				{
					provide: 'aComponentProvider',
					useValue: 'AComponentProviderValue'
				}
			]
		})
		class AComponent {}

		@SeDirective({
			providers: [
				{
					provide: 'bComponentProvider',
					useValue: 'BComponentProviderValue'
				}
			]
		})
		class BDirective {}

		@SeModule({
			declarations: [
				AComponent,
				BDirective
			]
		})
		class AModule {}
		!AModule;

		expect(mockAModule.constant.calls.argsFor(0)).toEqual(['aComponentProvider', 'AComponentProviderValue']);
		expect(mockAModule.constant.calls.argsFor(1)).toEqual(['bComponentProvider', 'BComponentProviderValue']);
		expect(mockAModule.component.calls.argsFor(0)[0]).toBe('a');
		expect(mockAModule.directive.calls.argsFor(0)[0]).toBe('b');
	});

	it('SeDirective with attribute selector will register with camelCase of the attribute name', () => {

		@SeDirective({
			selector: "[the-attribute-name]"
		})
		class ADirective {}

		@SeModule({
			declarations: [
				ADirective
			]
		})
		class AModule {}
		!AModule;

		const args = mockAModule.directive.calls.argsFor(0);
		expect(mockAModule.directive.calls.argsFor(0)[0]).toBe('theAttributeName');
		expect(args[1]().restrict).toBe("A");
	});

	it('SeDirective with class selector will register with camelCase of the class name', () => {

		@SeDirective({
			selector: ".class-name"
		})
		class ADirective {}

		@SeModule({
			declarations: [
				ADirective
			]
		})
		class AModule {}
		!AModule;

		const args = mockAModule.directive.calls.argsFor(0);
		expect(mockAModule.directive.calls.argsFor(0)[0]).toBe('className');
		expect(args[1]().restrict).toBe("C");
	});

	it('SeDirective with element selector will register with camelCase of the element name', () => {

		@SeDirective({
			selector: "element-name"
		})
		class ADirective {}

		@SeModule({
			declarations: [
				ADirective
			]
		})
		class AModule {}
		!AModule;

		const args = mockAModule.directive.calls.argsFor(0);
		expect(mockAModule.directive.calls.argsFor(0)[0]).toBe('elementName');
		expect(args[1]().restrict).toBe("E");
	});

	it('SeComponent with attribute selector will throw an exception', () => {

		expect(() => {
			@SeComponent({
				selector: "[the-attribute-name]"
			})
			class AComponent {}
			!AComponent;
		}).toThrow(new Error("component AComponent declared a selector on class or attribute. version 1808 of Smartedit DI limits SeComponents to element selectors"));
	});

	it('SeComponent with class selector will throw an exception', () => {

		expect(() => {
			@SeComponent({
				selector: ".class-name"
			})
			class AComponent {}
			!AComponent;
		}).toThrow(new Error("component AComponent declared a selector on class or attribute. version 1808 of Smartedit DI limits SeComponents to element selectors"));
	});

	it('SeComponent with element selector will register with camelCase of the class name', () => {

		@SeComponent({
			selector: "element-name"
		})
		class AComponent {}

		@SeModule({
			declarations: [
				AComponent
			]
		})
		class AModule {}
		!AModule;

		expect(mockAModule.component.calls.argsFor(0)[0]).toBe('elementName');
	});

	afterEach(() => {
		(SeModule as any).getAngular = function() {
			return (window as any).angular;
		};
	});

});