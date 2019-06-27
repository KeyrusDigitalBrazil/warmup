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

import {FunctionsUtils} from 'smarteditcommons';

// tslint:disable:max-classes-per-file
describe('FunctionsUtilsTests', () => {

	const functionsUtils: FunctionsUtils = new FunctionsUtils();

	describe('getConstructorName', function() {

		const testConstructor = jasmine.createSpyObj<any>('testConstructor', ['toString']);

		beforeEach(() => {
			testConstructor.name = undefined;
		});

		it('GIVEN function returns a name WHEN getConstructorName is called THEN it returns true', function() {
			// GIVEN 
			const expectedConstructorName = "ConstructorTest";
			testConstructor.name = expectedConstructorName;

			// WHEN 
			const result = functionsUtils.getConstructorName(testConstructor);

			// THEN 
			expect(result).toBe(expectedConstructorName);
		});

		it('GIVEN function does not return a name AND has no space in its name WHEN getConstructorName is called THEN it returns the right name', function() {
			// GIVEN
			const expectedConstructorName = "MyFunc";
			testConstructor.toString.and.returnValue("function MyFunc() { }");

			// WHEN 
			const result = functionsUtils.getConstructorName(testConstructor);

			// THEN 
			expect(result).toBe(expectedConstructorName);
		});

		it('GIVEN function does not return a name AND has spaces in its name WHEN getConstructorName is called THEN it returns the right name', function() {
			// GIVEN
			const expectedConstructorName = "myFunc";
			testConstructor.toString.and.returnValue("function myFunc    () { }");

			// WHEN 
			const result = functionsUtils.getConstructorName(testConstructor);

			// THEN 
			expect(result).toBe(expectedConstructorName);
		});

		it('GIVEN function does not return a name AND has a $ in its name WHEN getConstructorName is called THEN it returns the right name', function() {
			// GIVEN
			const expectedConstructorName = "$myFunc";
			testConstructor.toString.and.returnValue("function $myFunc() { }");

			// WHEN 
			const result = functionsUtils.getConstructorName(testConstructor);

			// THEN 
			expect(result).toBe(expectedConstructorName);
		});

		it('GIVEN invalid function WHEN getConstructorName is called THEN it throws an exception', () => {
			// GIVEN 
			testConstructor.toString.and.returnValue("invalid function");

			// WHEN
			expect(() => {
				functionsUtils.getConstructorName(testConstructor);
			}).toThrow(new Error("[FunctionsUtils] - Cannot get name from invalid constructor."));
		});

		it('GIVEN a class instance return the class name', () => {
			class Cat {

			}

			class FlyingCat extends Cat {
				constructor() {
					super();
				}
			}

			const flyingCat = new FlyingCat();
			expect(functionsUtils.getInstanceConstructorName(flyingCat)).toBe("FlyingCat");
		});
	});
}); 