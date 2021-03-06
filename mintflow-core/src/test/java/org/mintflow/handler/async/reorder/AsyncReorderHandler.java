package org.mintflow.handler.async.reorder;

import org.mintflow.annotation.MintFlowHandler;
import org.mintflow.param.ParamWrapper;
import org.mintflow.handler.async.AsyncFnHandler;
import org.mintflow.handler.async.AsyncReorderFnHandler;
import org.mintflow.handler.async.sample.AsyncReorderSampleHandler;

import java.util.List;

@MintFlowHandler(name = "async_reorder_handle")
public class AsyncReorderHandler extends AsyncReorderFnHandler {

    public static final String random_number_reorder = "random_number_reorder";

    public AsyncReorderHandler(String name) {
        super(name);
    }

    @Override
    public void reorderHandlerList(ParamWrapper paramWrapper, List<AsyncFnHandler> fnHandlers) {
        int num= (int) (Math.random()*10);
        paramWrapper.setContextParam(random_number_reorder,num);
        fnHandlers.clear();
        while(num>0) {
            fnHandlers.add(new AsyncReorderSampleHandler("async_reorder_handle"));
            num--;
        }
    }
}
