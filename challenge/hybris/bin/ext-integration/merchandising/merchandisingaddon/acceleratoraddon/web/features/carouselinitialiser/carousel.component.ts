import {sanitize, log} from './utils';
import { DataService } from './carousel.service';

/**
 * UI component, wrapper around the owlCarousel
 * @see jQuery.fn.owlCarousel
 */
export class CarouselComponent {
	private $el;
	private data;

	constructor({ el, data = {} }) {
		this.$el = $(el);
		this.data = data;
	}

	render() {
		log('render', this.data);
		this.$el
			.html(this.getHtml())
			.find('.js-merch-carousel')
			.owlCarousel({
				scrollPerPage: this.data.scroll === 'ALLVISIBLE',
				navigation: true,
				navigationText: ["<span class='glyphicon glyphicon-chevron-left'></span>", "<span class='glyphicon glyphicon-chevron-right'></span>"],
				pagination: false,
				itemsCustom: [[0, 2], [640, 4], [1024, 5], [1400, 7]]
			});
	}

	getHtml() {
		const { backgroundcolour, textcolour } = this.data;
		let textStyle = '',
		backgroundStyle = '',
		itemClassName = 'carousel__item--name',
		priceClassName = 'carousel__item--price';

		if (textcolour) {
			textStyle = `style="color: ${textcolour}"`;
		}
		if (backgroundcolour) {
			backgroundStyle = `style="background-color: ${backgroundcolour}"`;
			itemClassName += ' merchcarousel_custom-color';
			priceClassName += ' merchcarousel_custom-color';
		}
		const itemsHTML = this.data.items.reduce((acc, item) => {
			return acc += `
					<div class="carousel__item">
						<a href="/yacceleratorstorefront/en/${sanitize(item.pageUrl)}">
							<div class="carousel__item--thumb">
								<img src="${item.mainImage}" alt="${sanitize(item.name)}" title="${sanitize(item.name)}"/>
							</div>
							<div class="${itemClassName}" ${sanitize(textStyle)}>${sanitize(item.name)}</div>
							<div class="${priceClassName}" ${sanitize(textStyle)}>${sanitize(this.data.currency)}${sanitize(item.price)}</div>
						</a>
					</div>
				`;
		}, '');

		return `
				<div class="carousel__component" ${sanitize(backgroundStyle)}>
					<div class="carousel__component--headline" ${sanitize(textStyle)}>${sanitize(this.data.title)}</div>
					<div class="carousel__component--carousel js-merch-carousel">${itemsHTML}</div>
				</div>
			`;
	}

	static init() {
		log('init');
		Object.keys(window.__merchcarousels || {}).forEach((carouselId) => {
			const carouselSettings = window.__merchcarousels[carouselId];
			const { el, inited } = carouselSettings;

			if (!el || inited) {
				return;
			}
			const { numbertodisplay, title, currency, strategy, scroll, url, backgroundcolour, textcolour } = el.dataset;

			carouselSettings.inited = true;
			const service = new DataService({ strategy, url, numbertodisplay: +numbertodisplay });

			log('inited');
			service.getProducts().then((data) => {
				log('data received', data);

				let items = null;
				if (data && data.products) {
					items = +numbertodisplay !== 0 ? data.products.slice(0, +numbertodisplay) : data.products;
				}
				log('numbertodisplay', numbertodisplay);
				log('items', items);

				if (items && items.length && $.fn['owlCarousel'] != null) {
					new CarouselComponent({
						el,
						data: {
							scroll,
							items,
							title,
							currency,
							backgroundcolour,
							textcolour
						}
					} as any).render();
					log('rendered');
				}
			});
		});

		window.__merchcarousels = window.__merchcarousels || <any>{};
		window.__merchcarousels.CarouselComponent = CarouselComponent;
	}
}