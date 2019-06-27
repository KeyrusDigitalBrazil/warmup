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
import 'jasmine';
import * as angular from 'angular';
import {annotationService, SeInjectable} from 'smarteditcommons';

/*
 * see test functions and classed at the end of the test suite
 */

describe('test annotationService', function() {

	//////////////////////////////////////////////
	///////// TEST FUNCTION AND CLASSES //////////
	//////////////////////////////////////////////

	const methodAnnotation1 = annotationService.getMethodAnnotationFactory('methodAnnotation1');

	function methodAnnotation1Factory($dep1: angular.ILogService) {
		return annotationService.setMethodAnnotationFactory("methodAnnotation1", (factoryArgument: any[]) => {

			return function(target: any, propertyName: string, originalMethod: (...x: any[]) => any, ...invocationArguments: any[]) {
				$dep1.info(`will wrap in methodAnnotation1`);
				return "wrappedInmethodAnnotation1_" + originalMethod.apply(this, invocationArguments);
			};
		});
	}

	const methodAnnotation2 = annotationService.getMethodAnnotationFactory('methodAnnotation2');

	function methodAnnotation2Factory($dep2: angular.ILogService) {
		return annotationService.setMethodAnnotationFactory("methodAnnotation2", (factoryArgument: any[]) => {

			return function(target: any, propertyName: string, originalMethod: (...x: any[]) => any, ...invocationArguments: any[]) {
				$dep2.info(`will wrap in methodAnnotation2 with factory arguments ${factoryArgument[0]} and ${factoryArgument[1]}`);
				return "wrappedInmethodAnnotation2_" + originalMethod.apply(this, invocationArguments);
			};
		});
	}

	const classAnnotation1 = annotationService.getClassAnnotationFactory('classAnnotation1');

	function classAnnotation1Factory($dep3: angular.ILogService) {
		return annotationService.setClassAnnotationFactory('classAnnotation1', function(factoryArgument: any[]) {
			return function(instance: any, originalConstructor: (...x: any[]) => any, invocationArguments: any[]) {
				originalConstructor.call(instance, ...invocationArguments);
				instance.addedProperty = factoryArgument[0];
				$dep3.info(`${originalConstructor.name} is mutated into a proxied service with the arguments (${factoryArgument})`);
			};
		});
	}

	const classAnnotation2 = annotationService.getClassAnnotationFactory('classAnnotation2');

	function classAnnotation2Factory($dep3: angular.ILogService) {
		return annotationService.setClassAnnotationFactory('classAnnotation2', function(factoryArgument: any[]) {
			return function(instance: any, originalConstructor: (...x: any[]) => any, invocationArguments: any[]) {

				return new ServiceToBeAnnotated($dep3, "somestring", 5);
			};
		});
	}

	const classAnnotation3 = annotationService.getClassAnnotationFactory('classAnnotation3');
	function classAnnotation3Factory($dep3: angular.ILogService) {
		return annotationService.setClassAnnotationFactory('classAnnotation3', function(factoryArgument: any[]) {
			return function(instance: any, originalConstructor: (...x: any[]) => any, invocationArguments: any[]) {
				originalConstructor.call(instance, ...invocationArguments);
				instance.anotherProperty = factoryArgument[0];
			};
		});
	}

	@classAnnotation1("classArg1")
	@SeInjectable()
	class ServiceToBeAnnotated {

		constructor(private _$log: angular.ILogService, private arg1: string, private arg2: number) {
			this._$log.info("original constructor invoked");
		}

		someMethod0(): string {
			return this.arg1 + this.arg2;
		}

		@methodAnnotation1()
		@methodAnnotation2('Frequent', 3)
		someMethod1(): string {
			return "rawMethod1Output";
		}

		@methodAnnotation2('Never', 5)
		@methodAnnotation1()
		someMethod2(): string {
			return "rawMethod2Output";
		}

	}

	@classAnnotation2("classArg1")
	// tslint:disable-next-line
	class OtherService {

	}

	@classAnnotation1("classArg1")
	@classAnnotation3("classArg3")
	@SeInjectable()
	// tslint:disable-next-line
	class ServiceWithMultipleClassAnnotations {

	}

	//////////////////////////////////////////////
	//////////////// ACTUAL TESTS ////////////////
	//////////////////////////////////////////////

	let $log: jasmine.SpyObj<angular.ILogService>;

	let service: ServiceToBeAnnotated;
	let otherService: OtherService;
	let serviceWithMultipleClassAnnotations: ServiceWithMultipleClassAnnotations;
	beforeEach(() => {

		spyOn((annotationService as any).functionsUtils, 'isUnitTestMode').and.returnValue(false);

		$log = jasmine.createSpyObj<angular.ILogService>('$log', ['info']);

		classAnnotation1Factory($log);
		classAnnotation2Factory($log);
		classAnnotation3Factory($log);

	});

	function initializeBothMethodAnnotations() {
		// invocations normally performed when the pertaining angular factories are called
		methodAnnotation1Factory($log);
		methodAnnotation2Factory($log);
	}

	function onlyInitializemethodAnnotation1() {
		// invocations normally performed when the pertaining angular factories are called
		methodAnnotation1Factory($log);
		annotationService.setMethodAnnotationFactory("methodAnnotation2", null);
	}

	describe('class annotations', () => {

		it('GIVEN one necessary annotation is not initialized WHEN ServiceToBeAnnotated is instantiated THEN exception is thrown', () => {
			annotationService.setClassAnnotationFactory('classAnnotation1', null);

			expect(function() {
				// tslint:disable-next-line
				new ServiceToBeAnnotated($log, "somestring", 5);
			}).toThrow(new Error("annotation 'classAnnotation1' is used on 'ServiceToBeAnnotated' but its ClassAnnotationFactory may not have been added to the dependency injection"));
		});

		it('GIVEN a constructor is annotated but not @SeInjectable hinted, an exception will be thrown', () => {

			expect(function() {
				@classAnnotation2("classArg1")
				// tslint:disable-next-line
				class NonHintedClass {

					constructor(private _$log: angular.ILogService) {
						this._$log.info("original constructor invoked");
					}
				}
				// tslint:disable-next-line
				new NonHintedClass($log);
			}).toThrow(new Error("NonHintedClass class was decorated with annotation classAnnotation2 but has probably not been annotated with @SeInjectable() or @SeComponent"));

		});

		it('GIVEN a constructor is annotated, it will be modified with data from factory arguments and a code executed at the end', () => {

			service = new ServiceToBeAnnotated($log, "somestring", 5);

			expect(service.someMethod0()).toEqual("somestring5");
			expect((service as any).addedProperty).toEqual("classArg1");

			expect($log.info.calls.count()).toBe(2);
			expect($log.info.calls.argsFor(0)[0]).toEqual('original constructor invoked');
			expect($log.info.calls.argsFor(1)[0]).toContain('ServiceToBeAnnotated is mutated into a proxied service with the arguments (classArg1)');
		});

		it("GIVEN a constructor is annotated, its constructor will be replaced by another class's constructor", () => {

			otherService = new OtherService();

			expect((otherService as any).addedProperty).toEqual("classArg1");
			expect(otherService.constructor.name).toBe("ServiceToBeAnnotated");
		});

		it('GIVEN a constructor is annotated with two annotations, both annotations will have access to the same class instance', () => {
			serviceWithMultipleClassAnnotations = new ServiceWithMultipleClassAnnotations();

			expect((serviceWithMultipleClassAnnotations as any).addedProperty).toEqual("classArg1");
			expect((serviceWithMultipleClassAnnotations as any).anotherProperty).toEqual("classArg3");

		});

		it('GIVEN a constructor is annotated with two annotations, both annotations are retrieved through the same root original constructor', () => {
			const decoratorObj1 = annotationService.getClassAnnotation(ServiceWithMultipleClassAnnotations, classAnnotation1);
			expect(decoratorObj1).toEqual(['classArg1']);
			const decoratorObj3 = annotationService.getClassAnnotation(ServiceWithMultipleClassAnnotations, classAnnotation3);
			expect(decoratorObj3).toEqual(['classArg3']);
		});
	});

	describe('method annotations', () => {

		beforeEach(() => {
			$log = jasmine.createSpyObj<angular.ILogService>('$log', ['info']);
			service = new ServiceToBeAnnotated($log, "somestring", 5);
			$log.info.calls.reset();
		});

		xit('GIVEN a same annotation is registered twice THEN an exception is thrown', () => {
			annotationService.getMethodAnnotationFactory("someannotation");

			expect(function() {
				annotationService.getMethodAnnotationFactory("someannotation");
			}).toThrow(new Error("annotation 'cache' has already been registered"));
		});

		it('GIVEN one necessary annotation is not initialized WHEN method1 is called THEN exception is thrown', () => {
			onlyInitializemethodAnnotation1();

			expect(function() {
				service.someMethod1();
			}).toThrow(new Error("annotation 'methodAnnotation2' is used but its MethodAnnotationFactory may not have been added to the dependency injection"));
		});

		it('WHEN method1 is called THEN the method1 is proxied twice: first cache then transactional', () => {
			initializeBothMethodAnnotations();

			expect(service.someMethod1()).toBe("wrappedInmethodAnnotation1_wrappedInmethodAnnotation2_rawMethod1Output");
		});

		it('WHEN method2 is called THEN the method2 is proxied twice: first transactional then cache', () => {
			initializeBothMethodAnnotations();

			expect(service.someMethod2()).toBe("wrappedInmethodAnnotation2_wrappedInmethodAnnotation1_rawMethod2Output");
		});

		it('WHEN method1 is called THEN $log.info is called twice', () => {
			initializeBothMethodAnnotations();

			service.someMethod1();

			expect($log.info.calls.count()).toBe(2);
			expect($log.info).toHaveBeenCalledWith("will wrap in methodAnnotation1");
			expect($log.info).toHaveBeenCalledWith("will wrap in methodAnnotation2 with factory arguments Frequent and 3");
		});
	});
});


