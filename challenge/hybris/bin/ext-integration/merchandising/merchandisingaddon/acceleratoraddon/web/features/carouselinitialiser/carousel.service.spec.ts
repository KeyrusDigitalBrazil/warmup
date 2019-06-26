import { DataService } from "./carousel.service";

describe('DataService', () => {
	let service: DataService;
	let response = {
		mixcardID: 'mixcardID',
		products: null
	};

	beforeEach(() => {
		delete window.__merchcarousels;

		ACC.addons.merchandisingaddon = {};

		spyOn(DataService.prototype, 'getProducts').and.callFake(() => {
			return Promise.resolve(response);
		}) // do not call network

		service = new DataService({
			strategy: 'strategy',
			url: 'https://base.url',
			numbertodisplay: 10
		});
	});

	it('service constructor should exist', () => {
		expect(DataService).toBeTruthy();
	});

	it('service instance should exist', () => {
		expect(service).toBeTruthy();
	});

	it('_buildUrl should return url', () => {
		const actual = service._buildUrl.apply({
			strategy: 'strategy',
			tenant: 'tenant',
			baseUrl: 'baseUrl',
			category: 'category',
			facets: 'facets'
		});

		expect(actual).toEqual('baseUrl/tenant/strategies/strategy/products?category=category&facets=facets');
	});

	it('_buildUrl should return url', () => {
		const actual = service._buildUrl.apply({
			strategy: 'strategy',
			tenant: 'tenant',
			baseUrl: 'baseUrl',
			category: 'category',
			facets: 'facets'
		});

		expect(actual).toEqual('baseUrl/tenant/strategies/strategy/products?category=category&facets=facets');
	});

	it('_getTenantId should return tenant', () => {
		ACC.addons.merchandisingaddon.hybrisTenant = '{"properties":{"hybrisTenant":"tenant"}}';
		const actual = service._getTenantId.apply(null);

		expect(actual).toEqual('tenant');
	});

	it('_getCategory should return category', () => {
		ACC.addons.merchandisingaddon.ItemCategory = '{"properties":{"ItemCategory":"category"}}';
		const actual = service._getCategory.apply(null);

		expect(actual).toEqual('category');
	});

	it('_getFacets should return facet string', () => {
		ACC.addons.merchandisingaddon.ContextFacets = '{"properties":{"ContextFacets":[{"code":"code","values":["value1","value2"]}]}}';
		const actual = service._getFacets.apply(null);

		expect(actual).toEqual('code:value1:code:value2:');
	});

});

