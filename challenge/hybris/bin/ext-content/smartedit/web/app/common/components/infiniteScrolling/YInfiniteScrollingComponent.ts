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
import {
	ISeComponent,
	SeComponent
} from "smarteditcommons/services/dependencyInjection/di";
import {Page} from "smarteditcommons/dtos";
import {DiscardablePromiseUtils} from 'smarteditcommons/utils/DiscardablePromiseUtils';
import {TestModeService} from 'smarteditcommons/services/testModeService';

/** @internal */
export interface ITechnicalUniqueIdAware {
	technicalUniqueId: string;
}

/**
 * @ngdoc directive
 * @name yInfiniteScrollingModule.directive:yInfiniteScrolling
 * @scope
 * @restrict E
 *
 * @description
 * A component that you can use to implement infinite scrolling for an expanding content (typically with a ng-repeat) nested in it.
 * It is meant to handle paginated requests from a backend when data is expected to be large.
 * Since the expanding content is a <b>transcluded</b> element, we must specify the context to which the items will be attached:
 * If context is myContext, each pagination will push its new items to myContext.items.
 * @param {String} pageSize The maximum size of each page requested from the backend.
 * @param {String} mask A string value sent to the server upon fetching a page to further restrict the search, it is sent as query string "mask".
 * <br>The directive listens for change to mask and will reset the scroll and re-fetch data.
 * <br/>It it left to the implementers to decide what it filters on
 * @param {String} distance A number representing how close the bottom of the element must be to the bottom of the container before the expression specified by fetchPage function is triggered. Measured in multiples of the container height; for example, if the container is 1000 pixels tall and distance is set to 2, the infinite scroll expression will be evaluated when the bottom of the element is within 2000 pixels of the bottom of the container. Defaults to 0 (e.g. the expression will be evaluated when the bottom of the element crosses the bottom of the container).
 * @param {Object} context The container object to which the items of the fetched {@link Page.object:Page Page} will be added
 * @param {Function} fetchPage function to fetch the next page when the bottom of the element approaches the bottom of the container.
 *        fetchPage will be invoked with 3 arguments : <b>mask, pageSize, currentPage</b>. The currentPage is determined by the scrolling and starts with 0. The function must return a page of type {@link Page.object:Page Page}.
 * @param {String} dropDownContainerClass An optional CSS class to be added to the container of the dropdown. It would typically be used to override the default height. <b>The resolved CSS must set a height (or max-height) and overflow-y:scroll.</b>
 * @param {String} dropDownClass An optional CSS class to be added to the dropdown. <b>Neither height nor overflow should be set on the dropdown, it must be free to fill up the space and reach the container size. Failure to do so will cause the directive to call nextPage as many times as the number of available pages on the server.</b>
 */
@SeComponent({
	templateUrl: 'yInfiniteScrollingTemplate.html',
	inputs: [
		'pageSize',
		'mask:?',
		'fetchPage',
		'distance:?',
		'context:?',
		'dropDownContainerClass: @?',
		'dropDownClass: @?'
	]
})
export class YInfiniteScrollingComponent<T extends ITechnicalUniqueIdAware> implements ISeComponent {

	/** @internal */
	public CONTAINER_CLASS = "ySEInfiniteScrolling-container";

	/** @internal */
	public items: T[];
	/** @internal */
	public initiated = false;
	/** @internal */
	public currentPage: number;
	/** @internal */
	public pagingDisabled: boolean;
	// inputs
	public dropDownContainerClass: string;
	public dropDownClass: string;
	public pageSize: number;
	public mask: string;
	public fetchPage: (mask: string, pageSize: number, currentPage: number) => angular.IPromise<Page<T>>;
	private distance: number;
	private context: {items: T[]};
	// end of inputs

	private container: Element;
	private containerId: string;

	private THROTTLE_MILLISECONDS = 250;

	/** @internal */
	constructor(
		private $timeout: angular.ITimeoutService,
		private encode: (object: any) => string,
		private lodash: lo.LoDashStatic,
		private discardablePromiseUtils: DiscardablePromiseUtils,
		private $element: JQuery<Element>,
		generateIdentifier: () => string,
		throttle: (func: (...args: any[]) => any, maxWait: number) => any,
		testModeService: TestModeService
	) {
		this.containerId = generateIdentifier();

		// needs to be bound for usage by underlying infinite-scroll

		this.nextPage = this.nextPage.bind(this);
		if (!testModeService.isE2EMode()) {
			this.$onChanges = throttle(this.$onChanges.bind(this), this.THROTTLE_MILLISECONDS);
		}

	}

	/** @internal */
	$onChanges() {
		this.context = this.context || this;
		this.$postLink();
	}

	/** @internal */
	nextPage() {

		if (this.pagingDisabled) {
			return;
		}

		this.pagingDisabled = true;
		this.currentPage++;
		this.mask = this.mask || "";

		this.discardablePromiseUtils.apply(this.containerId, this.fetchPage(this.mask, this.pageSize, this.currentPage),
			(page: Page<T>) => {

				page.results.forEach((element: T) => {
					element.technicalUniqueId = this.encode(element);
				});

				const uniqueResults = this.lodash.differenceBy(page.results, this.context.items, "technicalUniqueId" as keyof ITechnicalUniqueIdAware);
				if (this.lodash.size(uniqueResults) > 0) {
					Array.prototype.push.apply(this.context.items, uniqueResults);
				}

				/*
				 * pagingDisabled controls the disablement of the native infinite-scroll directive therefore its
				 * re-evaluation must happen on the next digest cycle, after the HTML real estate has been modified
				 * by the new data set. Doing it on the same digest cycle would cause the non throttled infinite-scroll directive
				 * to fetch more pages than required
				 */
				this.$timeout(() => {
					this.pagingDisabled = page.results.length === 0 || (page.pagination && this.context.items.length === page.pagination.totalCount);
				});
			});
	}

	/** @internal */
	$postLink() {
		const wasInitiated = this.initiated;

		this.distance = this.distance || 0;
		this.context.items = [];
		this.currentPage = -1;
		this.pagingDisabled = false;
		if (!this.container && this.$element) {
			this.container = this.$element.find("." + this.CONTAINER_CLASS).get(0);
			this.initiated = true;
		} else {
			this.container.scrollTop = 0;
		}

		if (wasInitiated) {
			// not needed the first time since data-infinite-scroll-immediate-check="true"
			this.nextPage();
		}
	}

}
