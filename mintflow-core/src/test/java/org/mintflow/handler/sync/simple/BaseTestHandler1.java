package org.mintflow.handler.sync.simple;

import org.mintflow.annotation.MintFlowHandler;
import org.mintflow.handler.sync.SyncSampleFnHandler;
import org.mintflow.param.ParamWrapper;

@MintFlowHandler(name = "base_test_handle1")
public class BaseTestHandler1 extends SyncSampleFnHandler {
    public BaseTestHandler1(String name) {
        super(name);
    }

    @Override
    public ParamWrapper handle(ParamWrapper params) {
        Integer item = params.getParam(Integer.class);
        params.setParam(item+1);
        return params;
    }
}
