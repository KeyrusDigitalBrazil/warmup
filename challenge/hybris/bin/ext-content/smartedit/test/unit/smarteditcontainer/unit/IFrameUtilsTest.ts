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
import {WindowUtils} from "smarteditcommons";

describe('Windows Utils Test - getTargetFrame', function() {

	let windowUtils: WindowUtils;
	let $window: any;
	let isIFrame: any;
	let isIFrameVal = true;

	beforeEach(() => {
		isIFrame = jasmine.createSpy('isIFrame');

		$window = {
			addEventListener: jasmine.createSpy('addEventListener'),
			document: jasmine.createSpyObj('document', ['getElementById']),
			top: null
		};

		isIFrame.and.callFake(() => {
			return isIFrameVal;
		});

		windowUtils = new WindowUtils(isIFrame, 'ySmartEditFrame', $window);
	});

	const setIFrame = function(bool: boolean) {
		isIFrameVal = bool;
	};

	it('SHOULD return the parent frame if called within the iframe', function() {
		setIFrame(true);
		const targetFrame = windowUtils.getTargetIFrame();
		expect(targetFrame).toBe($window.parent);
	});

	it('SHOULD return the iframe if called from the parent window', function() {
		const contentWindowContent = 'TestContentWindow' as any;

		$window.document = jasmine.createSpyObj('document', ['getElementById']);
		$window.document.getElementById.and.returnValue({
			contentWindow: contentWindowContent
		});

		setIFrame(false);
		const targetFrame = windowUtils.getTargetIFrame();
		expect($window.document.getElementById).toHaveBeenCalledWith('ySmartEditFrame');
		expect(targetFrame).toBe(contentWindowContent);
	});

	it('SHOULD return null when called from the parent and no iframe exists', function() {
		$window.document = jasmine.createSpyObj('document', ['getElementById']);
		$window.document.getElementById.and.returnValue(null);

		setIFrame(false);
		expect(windowUtils.getTargetIFrame()).toBeNull();
	});

});