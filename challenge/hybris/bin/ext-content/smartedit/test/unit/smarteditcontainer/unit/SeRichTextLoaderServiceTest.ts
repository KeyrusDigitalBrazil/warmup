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
import {SeRichTextLoaderService} from "../../../../web/app/common/components/genericEditor/components/richText";
declare const inject: any;

describe('seRichTextLoaderService', () => {

	let seRichTextLoaderService: SeRichTextLoaderService;
	let $interval: any;
	let $q: angular.IQService;

	let originalCKEDITOR: any;

	beforeAll(() => {
		originalCKEDITOR = (window as any).CKEDITOR;
	});

	afterAll(() => {
		(window as any).CKEDITOR = originalCKEDITOR;
	});

	beforeEach(inject(function(_$q_: angular.IQService, _$interval_: any) {
		$q = _$q_;
		$interval = _$interval_;
	}));

	describe('load', () => {
		it('should return a resolved promise when CK Editor reports that it is loaded', () => {
			(window as any).CKEDITOR = {
				status: 'loaded'
			};
			seRichTextLoaderService = new SeRichTextLoaderService(
				$q,
				$interval
			);

			const result = seRichTextLoaderService.load();
			$interval.flush(400);

			expect(result).toBeResolved();
		});

		it('should return an unresolved promise when CK Editor is not loaded yet', () => {
			(window as any).CKEDITOR = {
				status: 'dummyStatus'
			};
			seRichTextLoaderService = new SeRichTextLoaderService(
				$q,
				$interval
			);

			const result = seRichTextLoaderService.load();
			$interval.flush(400);

			expect(result).not.toBeResolved();
		});
	});

});
