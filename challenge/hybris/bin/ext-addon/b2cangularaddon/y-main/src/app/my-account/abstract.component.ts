import { Component }            from '@angular/core';
import { Error }                from '../shared/error-handling/error';
import { ValidationService }    from '../shared/error-handling/validation.service';
import { UserService }          from '../shared/occ/user.service';

export abstract class AbstractComponent
{
    protected static VALIDATION_ERROR_MSGS:string[] = ['Please correct the errors below.'];
    protected static MANDATORY_FIELD_MSG = 'This field can not be empty';
    protected static PASSWORD_MISMATCH_MSG = 'The password is incorrect';

    protected _successMsgs:string[];
    protected _errorMsgs:string[];
    protected validationErrors:Error[];

    constructor(protected userService:UserService, protected validationService:ValidationService)
    {
    }

    public abstract onSubmit():void;

    public abstract validateForm():void;

    public clearMessage(type:string)
    {
        switch (type)
        {
            case 'conf':
                this._successMsgs = null;
                break;
            case 'error':
                this._errorMsgs = null;
                break;
        }
    }

    public getValidationError(fieldName:string):Error
    {
        return this.validationService.getValidationError(fieldName, this.validationErrors);
    }

    protected setSuccessMessage(message:string[])
    {
        this._successMsgs = message;
    }

    protected setErrorMessage(message:string[], errors:Error[])
    {
        this._errorMsgs = message;
        this.validationErrors = errors;
    }

    public validateFieldEmpty(fieldName:string, fieldValue:string):void
    {
        if (!fieldValue)
        {
            this.pushError(fieldName, AbstractComponent.MANDATORY_FIELD_MSG);
        }
    }

    public validateFieldEqual(fieldName:string, fieldValue:string, valueToCompare:string, errorMsg:string):void
    {
        if (fieldValue != valueToCompare)
        {
            this.pushError(fieldName, errorMsg);
        }
    }

    protected pushError(fieldName:string, errorMsg:string)
    {
        this._errorMsgs = AbstractComponent.VALIDATION_ERROR_MSGS;
        this.validationErrors.push(new Error(fieldName, errorMsg));
    }

    public clearFormMessages()
    {
        this._successMsgs = [];
        this._errorMsgs = [];
        this.validationErrors = [];
    }

    public get successMsgs():string[]
    {
        return this._successMsgs;
    }

    public get errorMsgs():string[]
    {
        return this._errorMsgs;
    }
}