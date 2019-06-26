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
import * as lo from 'lodash';
import {domHelper, ElementForJQuery} from 'testhelpers';
import {Primitive} from 'smarteditcommons';

const lodash: lo.LoDashStatic = (window as any).smarteditLodash;

class JQueryHelper {

	private IDEMPOTENT_METHODS = ['show', 'hide', 'on', 'off'];
	private DEFAULT_MOCKED_JQUERY_FUNCTIONS = ['get', 'data', 'css', 'width', 'height', 'offset'];

	jQuery(selectorTransform?: (selector: string) => jasmine.SpyObj<JQuery>): JQueryStatic {
		const jq = (window as any).$ || (window as any).smarteditJQuery;
		const jqSpy = jasmine.createSpy("jQueryMock", jq);

		jqSpy.and.callFake((element: ElementForJQuery | string) => {
			if (typeof element === 'string') {
				if (selectorTransform) {
					return selectorTransform(element);
				} else {
					return this.wrap("jqSpyForElement", domHelper.element(`Element Mock for selector ${element}`));
				}
			} else {
				return this.wrap("jqSpyForElement", element);
			}
		});

		return (jqSpy as any) as JQueryStatic;
	}

	wrap(name: string, ...elementsArray: ElementForJQuery[]): jasmine.SpyObj<JQuery> {

		const filteredElementsArray = elementsArray.filter((element) => !!element);

		const elementWithMocks = filteredElementsArray.find((element) => !!element.mockedMethodsOfJQueryWrapper);

		const mockedMethodsOfJQueryWrapperNames = !!elementWithMocks ? lodash.uniq(lodash.cloneDeep(this.DEFAULT_MOCKED_JQUERY_FUNCTIONS).concat(Object.keys(elementWithMocks.mockedMethodsOfJQueryWrapper))) : this.DEFAULT_MOCKED_JQUERY_FUNCTIONS;

		const elementsWrapper = jasmine.createSpyObj<JQuery>(name, this.IDEMPOTENT_METHODS.concat(mockedMethodsOfJQueryWrapperNames));

		filteredElementsArray.forEach((element, index) => {
			const wrapper = elementsWrapper as any;
			wrapper[index] = element;
		});

		(elementsWrapper as any).length = filteredElementsArray.length;

		this.DEFAULT_MOCKED_JQUERY_FUNCTIONS.forEach((methodName: Extract<keyof JQuery, string>) => {
			elementsWrapper[methodName].and.returnValue({});
		});

		if (elementWithMocks) {
			lodash.forEach(elementWithMocks.mockedMethodsOfJQueryWrapper, (value: Primitive, methodName: Extract<keyof JQuery, string>) => {
				elementsWrapper[methodName].and.returnValue(value);
			});
		}

		this.IDEMPOTENT_METHODS.forEach((methodName: Extract<keyof JQuery, string>) => {
			elementsWrapper[methodName].and.returnValue(elementsWrapper);
		});

		return elementsWrapper;
	}

}

export const jQueryHelper = new JQueryHelper();