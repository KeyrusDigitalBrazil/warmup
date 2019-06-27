declare let ACC: IAcc;

interface Window {
	__merchcarousels: IMerchCarouselInitSettings;
}

interface IMerchCarouselInitSettings {
	CarouselComponent?: any;
	[key: string]: {
		el: HTMLElement;
		inited: boolean;
	}
}

interface IAcc {
 	sanitizer: {
		sanitize(s: string): string;
	}

	addons: {
		merchandisingaddon: {
			hybrisTenant?: string;
			ItemCategory?: string;
			ContextFacets?: string;
		}
	}
}