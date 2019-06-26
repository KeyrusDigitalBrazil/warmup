import { CarouselComponent } from "./carousel.component";
import { DataService } from "./carousel.service";

describe('CarouselComponent', () => {
	let component: CarouselComponent;
	let el: HTMLElement;
	let data;
	let response = {
		mixcardID: 'mixcardID',
		products: null
	};

	beforeEach(() => {
		delete window.__merchcarousels;
		spyOn(DataService.prototype, 'getProducts').and.callFake(() => {
			return Promise.resolve(response);
		}) // do not call network

		el = document.createElement('div');
		data = {
			items: [],
			title: ''
		};

		component = new CarouselComponent({
			el,
			data
		});
	});

	it('component constructor should exist', () => {
		expect(CarouselComponent).toBeTruthy();
	});

	it('getHtml() should return the html string if items', () => {
		const actual = component.getHtml();

		expect(typeof actual).toEqual('string');
	});

	it('render() should init owlCarousel', () => {
		const jProto = ($.fn as any);
		jProto.owlCarousel = () => {};
		spyOn(jProto, 'owlCarousel');

		component.render();

		expect(jProto.owlCarousel).toHaveBeenCalled();
	});

	it('init() should save a global ref to CarouselComponent', () => {
		CarouselComponent.init();

		expect(window.__merchcarousels.CarouselComponent).toEqual(CarouselComponent);
	});

	it('init() should not init an item if it is already inited', (done) => {
		window['__merchcarousels'] = {
			'item': {
				el,
				inited: true
			}
		};
		const constructorSpy = spyOn(CarouselComponent.prototype as any, 'constructor').and.callThrough();
		CarouselComponent.init();

		setTimeout(() => {
			expect(constructorSpy).not.toHaveBeenCalled();
			done();
		}, 0);
	});

	it('init() should init an item if it is not already inited', (done) => {
		el.dataset.numbertodisplay = '10';
		window['__merchcarousels'] = {
			'item': {
				el,
				inited: false
			}
		};

		response = {
			mixcardID: 'mixcardID',
			products: [
				{ name: 'name' }
			]
		};

		const renderSpy = spyOn(CarouselComponent.prototype, 'render');
		CarouselComponent.init();

		setTimeout(() => {
			expect(renderSpy).toHaveBeenCalled();
			done();
		}, 0);
	});

});

