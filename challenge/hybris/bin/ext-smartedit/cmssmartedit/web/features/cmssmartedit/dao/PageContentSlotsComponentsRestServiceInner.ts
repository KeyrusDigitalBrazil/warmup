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
import {pageChangeEvictionTag, rarelyChangingContent, Cached, GatewayProxied, InvalidateCache, IPageInfoService, IRestService, IRestServiceFactory, SeInjectable, TypedMap} from 'smarteditcommons';
import {IPageContentSlotsComponentsRestService} from 'cmscommons/dao/cmswebservices/IPageContentSlotsComponentsRestService';
import {ICMSComponent} from 'cmscommons/services/ICMSComponent';
import * as lo from 'lodash';

interface PageContentSlotComponent {
	pageId: string;
	slotId: string;
	componentId: string;
	componentUuid: string;
	position: number;
}

interface PageContentSlotComponentResponse {
	pageContentSlotComponentList: PageContentSlotComponent[];
}

interface CMSItemsResponse {
	response: ICMSComponent[];
}

@GatewayProxied('clearCache', 'getSlotsToComponentsMapForPageUid')
@SeInjectable()
export class PageContentSlotsComponentsRestService extends IPageContentSlotsComponentsRestService {

	private pagesContentSlotsComponentsRestService: IRestService<PageContentSlotComponentResponse>;

	constructor(
		restServiceFactory: IRestServiceFactory,
		private pageInfoService: IPageInfoService,
		private cmsitemsRestService: any,
		private lodash: lo.LoDashStatic,
		PAGE_CONTEXT_SITE_ID: string,
		PAGE_CONTEXT_CATALOG: string,
		PAGE_CONTEXT_CATALOG_VERSION: string,
	) {
		super();

		const contentSlotContainerResourceURI = `/cmswebservices/v1/sites/${PAGE_CONTEXT_SITE_ID}/catalogs/${PAGE_CONTEXT_CATALOG}/versions/${PAGE_CONTEXT_CATALOG_VERSION}/pagescontentslotscomponents?pageId=:pageId`;
		this.pagesContentSlotsComponentsRestService = restServiceFactory.get<PageContentSlotComponentResponse>(contentSlotContainerResourceURI);
	}

	@InvalidateCache(pageChangeEvictionTag)
	clearCache(): void {
		return;
	}

	getComponentsForSlot(slotUuid: string): angular.IPromise<ICMSComponent[]> {
		return this.pageInfoService.getPageUID().then((pageUID: string) => {
			return this.getSlotsToComponentsMapForPageUid(pageUID).then((slotsToComponentsMap: TypedMap<ICMSComponent[]>) => {
				return slotsToComponentsMap[slotUuid] || [];
			});
		});
	}

	/**
	 * @description
	 * Returns a list of pageContentSlotsComponents associated to a page.
	 *
	 * @param {string} pageUid The uid of the page to retrieve the content slots to components map.
	 * @return {Promise} A promise that resolves to the pagesContentSlotsComponents for the page in context.
	 */
	getSlotsToComponentsMapForPageUid(pageUid: string): angular.IPromise<TypedMap<ICMSComponent[]>> {
		return this.getCachedSlotsToComponentsMapForPageUid(pageUid).then((response) => {
			return this.lodash.cloneDeep(response);
		});
	}

	@Cached({actions: [rarelyChangingContent], tags: [pageChangeEvictionTag]})
	private getCachedSlotsToComponentsMapForPageUid(pageUid: string): angular.IPromise<TypedMap<ICMSComponent[]>> {
		return this.pagesContentSlotsComponentsRestService.get({
			pageId: pageUid
		}).then(({pageContentSlotComponentList}: PageContentSlotComponentResponse) => {
			const componentUuids = pageContentSlotComponentList.map(function(pageContentSlotComponent: PageContentSlotComponent) {
				return pageContentSlotComponent.componentUuid;
			});

			return this.cmsitemsRestService.getByIds(componentUuids).then((components: CMSItemsResponse) => {
				// load all components as ComponentUuid-Component map
				const allComponentsMap = (components.response || []).reduce((map: TypedMap<ICMSComponent>, component: ICMSComponent) => {
					map[component.uuid] = component;
					return map;
				}, {} as TypedMap<ICMSComponent>);

				// load all components as SlotUuid-Component[] map
				const allSlotsToComponentsMap: TypedMap<ICMSComponent[]> = pageContentSlotComponentList.reduce((map: TypedMap<ICMSComponent[]>, pageContentSlotComponent: PageContentSlotComponent) => {
					map[pageContentSlotComponent.slotId] = map[pageContentSlotComponent.slotId] || [];
					if (allComponentsMap[pageContentSlotComponent.componentUuid]) {
						map[pageContentSlotComponent.slotId].push(allComponentsMap[pageContentSlotComponent.componentUuid]);
					}
					return map;
				}, {} as TypedMap<ICMSComponent[]>);

				return allSlotsToComponentsMap;
			});
		});
	}

}