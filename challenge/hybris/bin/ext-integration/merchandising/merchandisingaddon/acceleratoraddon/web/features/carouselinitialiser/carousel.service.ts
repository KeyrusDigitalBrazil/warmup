import { log } from './utils';

/**
 * Data source for Merch Carousel
 */
export class DataService {
	private tenant: string;
	private strategy: string;
	private numberToDisplay: number;
	private category: string;
	private facets: string;
	private url: string;

	constructor({ strategy, url, numbertodisplay }) {
		this.tenant = this._getTenantId();
		this.strategy = strategy;
		this.numberToDisplay = numbertodisplay;
		this.category = this._getCategory();
		this.facets = this._getFacets();

		this.url = this._buildUrl(url);
	}

	/**
	 * Retrieves carousel data
	 * @returns {Promise<Object>}
	 */
	getProducts() {
		return $.get(this.url);
	}

	/**
	 * Build URL based on context data
	 * @param {string} rootUrl
	 * @returns {string|null}
	 */
	_buildUrl(rootUrl) {
		let url = null;

		if (!this.strategy || !this.tenant) {
			return null
		}

		url = rootUrl + `/${this.tenant}/strategies/${this.strategy}/products`;

		let queryString = '';
		if (this.category) {
			queryString = this._buildQueryString(queryString, `category=${this.category}`);
		}

		if (this.facets) {
			queryString = this._buildQueryString(queryString, `facets=${this.facets}`);
		}

		if (this.numberToDisplay) {
			queryString = this._buildQueryString(queryString, `pageSize=${this.numberToDisplay}`);
		}

		if (queryString) {
			url = url + `?` + queryString;
		}
		return url;
	}

	/**
	 * Help to update the qs
	 * @param queryString {string}
	 * @param valueToAdd {string}
	 */
	_buildQueryString(queryString: string, valueToAdd: string): string {
		if (queryString) {
			queryString += '&';
		}
		queryString += valueToAdd;
		return queryString;
	}

	/**
	 * Parses tenant ID from the ACC context
	 * @returns {string|null}
	 */
	_getTenantId() {
		let id = null;

		try {
			const data = JSON.parse(ACC.addons.merchandisingaddon.hybrisTenant);
			id = data.properties.hybrisTenant;
		} catch (e) {
			log('JSON parser error', e);
		}

		return id;
	}

	/**
	 * Parse category from the ACC context
	 * @returns {string|null}
	 */
	_getCategory() {
		let id = null;

		try {
			const data = JSON.parse(ACC.addons.merchandisingaddon.ItemCategory);
			id = data.properties.ItemCategory;
		} catch (e) {
			log('JSON parser error', e);
		}

		return id;
	}

	/**
	 * Parse category from the ACC context
	 * @returns {string|null}
	 */
	_getFacets() {
		let facetsString = null;

		try {
			const data = JSON.parse(ACC.addons.merchandisingaddon.ContextFacets);
			facetsString = '';

			data.properties.ContextFacets.forEach((facetObject) => {
				facetObject.values.forEach((facetValue) => {
					facetsString += `${facetObject.code}:${facetValue}:`;
				});
			});

		} catch (e) {
			log('JSON parser error', e);
		}

		return facetsString;
	}
}