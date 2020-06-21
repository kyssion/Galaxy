package org.mintflow.test.syncBaseTest;

import org.junit.Test;
import org.mintflow.MintFlow;
import org.mintflow.MintFlowTemplate;
import org.mintflow.handle.FnHandler;
import org.mintflow.handle.HandleType;
import org.mintflow.handle.util.MintFlowHandleMapBuilder;
import org.mintflow.handle.util.MintFlowHandleMapFinder;
import org.mintflow.param.ParamWrapper;
import org.mintflow.templateFunction.Function1;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mintflow.test.syncBaseTest.ConditionTest.CAN_GO;
import static org.mintflow.test.syncBaseTest.ConditionTest.NO_GO;
import static org.mintflow.test.syncBaseTest.ReorderTest.ADD_DATA;

public class SyncTempleteTest {

    @Test
    public void test(){
        MintFlowHandleMapBuilder.Mapper dataMapper = MintFlowHandleMapFinder.findHandleDataMap(
                "org.mintflow.handler"
        );
        MintFlow MintFlow = MintFlow.newBuilder(dataMapper).addFnMapper("base_sync_test/sync_complex_test.fn").build();
        MintFlowTemplate MintFlowTemplate = MintFlowTemplate.newBuilder().addInterface(MintFlow,"org.mintflow.templateFunction").build();
        Function1 function1 = MintFlowTemplate.getTemplateFunction(Function1.class);
        ParamWrapper paramWrapper = function1.test(1,"item",NO_GO,CAN_GO,NO_GO,CAN_GO,false,false);

        assertEquals(13, (int) paramWrapper.getResult(Integer.class));
        StringBuilder ans = new StringBuilder("item"+ ADD_DATA);
        int num = paramWrapper.getContextParam("random_number");
        while(num>=0){
            ans.append(ADD_DATA);
            num--;
        }
        String item = paramWrapper.getParam(String.class);
        assertEquals(ans.toString(),item);

        assertTrue(paramWrapper.getContextParam("show_start"));
        assertTrue(paramWrapper.getContextParam("show_end"));
    }
}