package org.melkweg.handle.sync;

import org.melkweg.handle.HandleType;
import org.melkweg.param.ParamWrapper;
import org.melkweg.scheduler.sync.SyncScheduler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/**
 * Reorder Handler -> used to sub-pack all washable handle components
 */
public abstract class SyncReorderFnHandler extends SyncToolsFnHandler {


    public SyncReorderFnHandler(String name){
        this(name, HandleType.REORDER_HANDLE_SYNC);
    }

    private SyncReorderFnHandler(String name, HandleType handleType) {
        super(name, handleType);
    }

    public void addChilds(SyncFnHandler... fnHandlers) {
        this.getSyncChildren().addAll(Arrays.asList(fnHandlers));
    }

    public void addChilds(List<SyncFnHandler> fnHandlers) {
        this.getSyncChildren().addAll(fnHandlers);
    }


    @Override
    public ParamWrapper handle(ParamWrapper paramWrapper, SyncScheduler syncScheduler) {
        if (this.getSyncChildren() == null || this.getSyncChildren().size() == 0) {
            return paramWrapper;
        }
        if (syncScheduler != null) {
            List<SyncFnHandler> newFnHandlerList = new ArrayList<>(this.getSyncChildren());
            reorderHandlerList(paramWrapper,newFnHandlerList);
            return syncScheduler.run(paramWrapper, newFnHandlerList);
        } else {
            return paramWrapper;
        }
    }

    public abstract void reorderHandlerList(ParamWrapper paramWrapper,List<SyncFnHandler> fnHandlers);
}
