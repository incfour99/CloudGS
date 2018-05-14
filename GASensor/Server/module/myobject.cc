#include "myobject.h"
#include <Windows.h>

#define WM_MSG002  200

using namespace v8;

Persistent<Function> MyObject::constructor;

MyObject::MyObject(double value) : value_(value) {
}

MyObject::~MyObject() {
}

void MyObject::Init(Handle<Object> exports) {
  Isolate* isolate = Isolate::GetCurrent();

  // Prepare constructor template
  Local<FunctionTemplate> tpl = FunctionTemplate::New(isolate, New);
  tpl->SetClassName(String::NewFromUtf8(isolate, "MyObject"));
  tpl->InstanceTemplate()->SetInternalFieldCount(1);

  // Prototype
  NODE_SET_PROTOTYPE_METHOD(tpl, "CMD_SendWinMSG", SendWinMSG);

  constructor.Reset(isolate, tpl->GetFunction());
  exports->Set(String::NewFromUtf8(isolate, "MyObject"),
               tpl->GetFunction());
}

void MyObject::New(const FunctionCallbackInfo<Value>& args) {
  Isolate* isolate = Isolate::GetCurrent();
  HandleScope scope(isolate);

  if (args.IsConstructCall()) {
    // Invoked as constructor: `new MyObject(...)`
    double value = args[0]->IsUndefined() ? 0 : args[0]->NumberValue();
    MyObject* obj = new MyObject(value);
    obj->Wrap(args.This());
    args.GetReturnValue().Set(args.This());
  } else {
    // Invoked as plain function `MyObject(...)`, turn into construct call.
    const int argc = 1;
    Local<Value> argv[argc] = { args[0] };
    Local<Function> cons = Local<Function>::New(isolate, constructor);
    args.GetReturnValue().Set(cons->NewInstance(argc, argv));
  }
}

// void MyObject::GetValue(const FunctionCallbackInfo<Value>& args) {
//   Isolate* isolate = Isolate::GetCurrent();
//   HandleScope scope(isolate);
//   MyObject* obj = ObjectWrap::Unwrap<MyObject>(args.Holder());
//   args.GetReturnValue().Set(Number::New(isolate, obj->value_));
// }

void MyObject::SendWinMSG(const FunctionCallbackInfo<Value>& args) {
  Isolate* isolate = Isolate::GetCurrent();
  HandleScope scope(isolate);

  FILE *fp = NULL;

  if ((fp = fopen("C:\\shotdataCD.txt", "w")) != NULL)//쓰기전용
  {
	  fprintf(fp, "%f, %f, %f, %f, %f", 1.1f, 1.2f, 1.3f, 1.4f, 1.5f);

	  //fprintf(fp, "%f, %f, %f, %f, %f", args[0]->NumberValue()			// pshotdata.ballspeed
			//							  , args[1]->NumberValue()		// pshotdata.ballinci
			//							  , args[2]->NumberValue()		// pshotdata.balldir
			//							  , args[3]->NumberValue()		// pshotdata.backspin
			//							  , args[3]->NumberValue());	// pshotdata.sidespin

	  fclose(fp);
  }

  HWND hwnd = NULL;
  hwnd = ::FindWindowA((LPCSTR)("MsgWnd"), NULL);  
  if (hwnd != NULL)
	  ::SendMessage(hwnd, WM_MSG002, 0, 0);

  // MyObject* obj = ObjectWrap::Unwrap<MyObject>(args.Holder());
  // obj->value_ += 1;
  //
  // args.GetReturnValue().Set(Number::New(isolate, obj->value_));
}

// void MyObject::Multiply(const FunctionCallbackInfo<Value>& args) {
//   Isolate* isolate = Isolate::GetCurrent();
//   HandleScope scope(isolate);
//
//   MyObject* obj = ObjectWrap::Unwrap<MyObject>(args.Holder());
//   double multiple = args[0]->IsUndefined() ? 1 : args[0]->NumberValue();
//
//   const int argc = 1;
//   Local<Value> argv[argc] = { Number::New(isolate, obj->value_ * multiple) };
//
//   Local<Function> cons = Local<Function>::New(isolate, constructor);
//   args.GetReturnValue().Set(cons->NewInstance(argc, argv));
// }
