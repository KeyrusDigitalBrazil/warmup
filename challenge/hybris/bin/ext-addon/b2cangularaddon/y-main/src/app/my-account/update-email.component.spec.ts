import {async, ComponentFixture, fakeAsync, TestBed} from '@angular/core/testing';
import { AbstractComponent } from './abstract.component';
import { By }              from '@angular/platform-browser';
import { BaseRequestOptions, ConnectionBackend, Http, RequestOptions} from "@angular/http";
import { CookieModule, CookieService }  from 'ngx-cookie';
import { ErrorHandlingModule }    from '../shared/error-handling/error-handling.module';
import { FormsModule} from "@angular/forms";
import { OAuth2Service } from '../shared/occ/oauth2.service';
import { Observable } from "rxjs/Rx";
import { OCCService }     from '../shared/occ/occ.service';
import { GlobalVarService }  from '../shared/occ/global-var.service';
import { MockBackend} from "@angular/http/testing";
import { RouterTestingModule } from '@angular/router/testing';
import { Title }               from '../shared/occ/title';
import { Titles }               from '../shared/occ/titles';
import { UpdateEmailComponent } from './update-email.component';
import { User }               from '../shared/occ/user';
import { UserService }          from '../shared/occ/user.service';
import { ValidationService }    from '../shared/error-handling/validation.service';

describe('Component - Update email', () => {
    let updateEmailComponent: UpdateEmailComponent;
    let userService: UserService;
    let fixture: ComponentFixture<UpdateEmailComponent>;
    
    beforeEach(async(() => {
        TestBed.configureTestingModule({
            declarations: [ UpdateEmailComponent ],
            providers:    [ CookieService, Http, OAuth2Service, OCCService, GlobalVarService, UserService, ValidationService, {provide: ConnectionBackend, useClass: MockBackend}, {provide: RequestOptions, useClass: BaseRequestOptions}],
            imports: [ CookieModule.forRoot(), FormsModule, ErrorHandlingModule, RouterTestingModule ]
        })
    }));
    
    beforeEach(() => {
        fixture = TestBed.createComponent(UpdateEmailComponent);
        updateEmailComponent = fixture.componentInstance;
        userService = fixture.debugElement.injector.get(UserService);
    });
    
    it('Should not call user service if field validation fails', fakeAsync(() => {
        // Given
        prepareSpies();
        
        // When
        clickSubmitButton();
        
        // Then
        expect(updateEmailComponent.onSubmit).toHaveBeenCalled()
        expect(updateEmailComponent.validateForm).toHaveBeenCalled();
        expect(updateEmailComponent.clearFormMessages).toHaveBeenCalled();
        expect(updateEmailComponent.validateFieldEmpty).toHaveBeenCalledWith('newLogin', undefined);
        expect(updateEmailComponent.validateFieldEmpty).toHaveBeenCalledWith('checkEmailAddress', undefined);
        expect(updateEmailComponent.validateFieldEmpty).toHaveBeenCalledWith('password', undefined);
        expect(updateEmailComponent.errorMsgs.length).toBe(1);
        expect(userService.changeLogin).not.toHaveBeenCalled();
    }));
    
    it('Should call user service when field validation passes', fakeAsync(() => {
        // Given
        prepareSpies();
        setEmailAndPassword();
        updateEmailComponent.emailAddress = 'emailAddress';
        updateEmailComponent.checkEmailAddress = 'emailAddress';
        
        // When
        clickSubmitButton();
        
        // Then
        expect(updateEmailComponent.onSubmit).toHaveBeenCalled()
        expect(updateEmailComponent.validateForm).toHaveBeenCalled();
        expect(updateEmailComponent.clearFormMessages).toHaveBeenCalled();
        expect(updateEmailComponent.validateFieldEmpty).toHaveBeenCalledWith('newLogin', 'emailAddress');
        expect(updateEmailComponent.validateFieldEmpty).toHaveBeenCalledWith('checkEmailAddress', 'emailAddress');
        expect(updateEmailComponent.validateFieldEmpty).toHaveBeenCalledWith('password', 'password');
        expect(updateEmailComponent.validateFieldEqual).toHaveBeenCalledWith('checkEmailAddress', 'emailAddress', 'emailAddress', 'Email and email confirmation do not match');
        expect(updateEmailComponent.errorMsgs.length).toBe(0);
        expect(userService.changeLogin).toHaveBeenCalled();
    }));
    
    function clickSubmitButton() {
        let updateButton = fixture.debugElement.query(By.css('#updateEmailForm'));
        updateButton.triggerEventHandler('ngSubmit', null);
    }
    
    function prepareSpies() {
        spyOn(userService, 'changeLogin').and.returnValue( Observable.of({}));
        spyOn(updateEmailComponent, 'ngOnInit').and.callThrough();
        spyOn(updateEmailComponent, 'onSubmit').and.callThrough();
        spyOn(updateEmailComponent, 'validateForm').and.callThrough();
        spyOn(AbstractComponent.prototype, 'clearFormMessages').and.callThrough();
        spyOn(AbstractComponent.prototype, 'validateFieldEmpty').and.callThrough();
        spyOn(AbstractComponent.prototype, 'validateFieldEqual').and.callThrough();
    }
    
    function setEmailAndPassword() {
        updateEmailComponent.checkEmailAddress = 'checkEmailAddress';
        updateEmailComponent.password = 'password';
        fixture.detectChanges();
    }
});
