package org.mintflow.test;

import org.mintflow.MintFlow;
import org.mintflow.MintFlowTemplate;
import org.mintflow.handler.MintFlowHandlerMap;
import org.mintflow.handler.async.condition.AsyncConditionHandler1;
import org.mintflow.handler.async.condition.AsyncConditionHandler2;
import org.mintflow.handler.async.condition.AsyncConditionHandler3;
import org.mintflow.handler.async.condition.AsyncConditionHandler4;
import org.mintflow.handler.async.cycle.AsyncCycleTestHandler;
import org.mintflow.handler.async.reorder.AsyncReorderHandler;
import org.mintflow.handler.async.sample.*;
import org.mintflow.handler.sync.condition.ConditionHandler1;
import org.mintflow.handler.sync.condition.ConditionHandler2;
import org.mintflow.handler.sync.condition.ConditionHandler3;
import org.mintflow.handler.sync.condition.ConditionHandler4;
import org.mintflow.handler.sync.cycle.SyncCycleHandler;
import org.mintflow.handler.sync.reorder.SyncReorderHandler;
import org.mintflow.handler.sync.simple.*;
import org.mintflow.handler.util.MintFlowHandlerMapBuilder;
import org.mintflow.handler.util.MintFlowHandlerMapFinder;
import org.mintflow.param.ParamWrapper;
import org.mintflow.scheduler.sync.SyncFnEngineSyncScheduler;
import org.mintflow.templateFunction.Function1;
import org.mintflow.test.asyncBaseTest.AsyncConditionTest;
import org.mintflow.test.asyncBaseTest.AsyncCycleTest;
import org.mintflow.test.asyncBaseTest.AsyncReorderTest;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mintflow.handler.async.cycle.AsyncCycleTestHandler.random_number_cycle;
import static org.mintflow.handler.async.reorder.AsyncReorderHandler.random_number_reorder;
import static org.mintflow.handler.async.sample.AsyncCycleSampleHandler.ASYNC_CYCLE_STR;
import static org.mintflow.handler.async.sample.AsyncReorderSampleHandler.ASYNC_REORDER_STR;
import static org.mintflow.handler.sync.simple.CycleSampleHandler.SYNC_CYCLE_STR;
import static org.mintflow.handler.sync.simple.ReorderSampleHandler.SYNC_REORDER_STR;
import static org.mintflow.test.BaseTestUtil.*;
import static org.mintflow.test.syncBaseTest.SyncConditionTest.CAN_GO;
import static org.mintflow.test.syncBaseTest.SyncConditionTest.NO_GO;
import static org.mintflow.test.syncBaseTest.SyncCycleTest.ADD_DATA_CYCLE;
import static org.mintflow.test.syncBaseTest.SyncReorderTest.ADD_DATA_REORDER;

public class PerformanceTest {

    public static AtomicInteger atomicInteger = new AtomicInteger(0);

    public void tset(){

    }

    public static void main(String[] args) {
//        for (int a = 0; a < 100; a++) {
////            new Thread(new AsyncNoUseTemplateThread()).start();
////            new Thread(new AsyncUseTemplateTest()).start();
////            new Thread(new SyncNoUseTemplateThread()).start();
//            new Thread(new SyncUseTemplateThread()).start();
//        }
    }

    static class SyncUseTemplateThread implements Runnable {

        @Override
        public void run() {
            MintFlowHandlerMap dataMapper = MintFlowHandlerMapFinder.findHandlerDataMap(
                    "org.mintflow.handler"
            );
            MintFlow mintFlow = MintFlow.newBuilder(dataMapper).addFnMapper("base_sync_test/sync_complex_test.fn").build();
            MintFlowTemplate mintFlowTemplate = MintFlowTemplate.newBuilder().addInterface(mintFlow, "org.mintflow.templateFunction").build();
            Function1 function1 = mintFlowTemplate.getTemplateFunction(Function1.class);
            while (true) {
                String itemCycle = "test1";
                StringBuilder ansCycle = new StringBuilder(itemCycle);

                String itemReorder = "test1";
                StringBuilder ansReorder = new StringBuilder(itemReorder);

                ParamWrapper paramWrapper = function1.test(1, itemCycle, itemReorder, NO_GO, CAN_GO, NO_GO, CAN_GO, false, false);
                assertEquals(13, (int) paramWrapper.getResult(Integer.class));

                int numCycle = paramWrapper.getContextParam(random_number_cycle);
                while (numCycle > 0) {
                    ansCycle.append(ADD_DATA_CYCLE);
                    numCycle--;
                }
                itemCycle = paramWrapper.getContextParam(SYNC_CYCLE_STR);
                assertEquals(ansCycle.toString(), itemCycle);

                int numReorder = paramWrapper.getContextParam(random_number_reorder);
                while (numReorder > 0) {
                    ansReorder.append(ADD_DATA_REORDER);
                    numReorder--;
                }
                itemReorder = paramWrapper.getContextParam(SYNC_REORDER_STR);
                assertEquals(ansReorder.toString(), itemReorder);

                assertTrue(paramWrapper.getContextParam("show_start"));
                assertTrue(paramWrapper.getContextParam("show_end"));

                show();

            }
        }
    }

    static private void show() {
        System.out.print(".");
        if (atomicInteger.incrementAndGet() == 300) {
            System.out.println();
            atomicInteger.set(0);
        }
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    static class SyncNoUseTemplateThread implements Runnable {

        @Override
        public void run() {
            MintFlowHandlerMapBuilder mapBuilder;
            mapBuilder = new MintFlowHandlerMapBuilder();

            mapBuilder.put("condition_handle_1", new ConditionHandler1("condition_handle_1"));
            mapBuilder.put("condition_handle_2", new ConditionHandler2("condition_handle_2"));
            mapBuilder.put("condition_handle_3", new ConditionHandler3("condition_handle_3"));
            mapBuilder.put("condition_handle_4", new ConditionHandler4("condition_handle_4"));


            mapBuilder.put("base_test_handle1", new BaseTestHandler1("base_test_handle1"));
            mapBuilder.put("base_test_handle2", new BaseTestHandler2("base_test_handle2"));
            mapBuilder.put("base_test_handle3", new BaseTestHandler3("base_test_handle3"));

            mapBuilder.put("show_start_handle", new ShowStartHandler("show_start_handle"));
            mapBuilder.put("show_end_handle", new ShowEndHandler("show_end_handle"));

            mapBuilder.put("reorder_handle", new SyncReorderHandler("reorder_handle"));
            mapBuilder.put("reorder_sample_handle", new ReorderSampleHandler("reorder_sample_handle"));

            mapBuilder.put("sync_cycle_sample_handler",new CycleSampleHandler("sync_cycle_sample_handler"));
            mapBuilder.put("sync_cycle_test",new SyncCycleHandler("sync_cycle_test"));

            MintFlow mintFlow = MintFlow.newBuilder(mapBuilder.build()).addFnMapper("base_sync_test/sync_complex_test.fn").build();


            while (true) {
                ParamWrapper paramWrapper = new ParamWrapper();
                paramWrapper.setParam(1);
                paramWrapper.setContextParam("condition_1", NO_GO);
                paramWrapper.setContextParam("condition_2", CAN_GO);
                paramWrapper.setContextParam("condition_3", NO_GO);
                paramWrapper.setContextParam("condition_4", CAN_GO);
                paramWrapper.setContextParam("show_start", false);
                paramWrapper.setContextParam("show_end", false);

                String itemCycle = "test1";
                StringBuilder ansCycle = new StringBuilder(itemCycle);
                paramWrapper.setContextParam(SYNC_CYCLE_STR, itemCycle);

                String itemReorder = "test1";
                StringBuilder ansReorder = new StringBuilder(itemReorder);
                paramWrapper.setContextParam(SYNC_REORDER_STR, itemReorder);

                paramWrapper = mintFlow.runSync(NAME_SPACE, SYNC_PROCESS_NAME, paramWrapper, new SyncFnEngineSyncScheduler());
                assertEquals(13, (int) paramWrapper.getResult(Integer.class));

                int numCycle = paramWrapper.getContextParam(random_number_cycle);
                while (numCycle > 0) {
                    ansCycle.append(ADD_DATA_CYCLE);
                    numCycle--;
                }
                itemCycle = paramWrapper.getContextParam(SYNC_CYCLE_STR);
                assertEquals(ansCycle.toString(), itemCycle);

                int numReorder = paramWrapper.getContextParam(random_number_reorder);
                while (numReorder > 0) {
                    ansReorder.append(ADD_DATA_REORDER);
                    numReorder--;
                }
                itemReorder = paramWrapper.getContextParam(SYNC_REORDER_STR);
                assertEquals(ansReorder.toString(), itemReorder);

                assertTrue(paramWrapper.getContextParam("show_start"));
                assertTrue(paramWrapper.getContextParam("show_end"));
                show();
            }
        }
    }

    static class AsyncNoUseTemplateThread implements Runnable {

        @Override
        public void run() {
            /**
             * 初始化
             */
            MintFlowHandlerMapBuilder mapBuilder;
            mapBuilder = new MintFlowHandlerMapBuilder();

            mapBuilder.put("async_condition_handle_1", new AsyncConditionHandler1("async_condition_handle_1"));
            mapBuilder.put("async_condition_handle_2", new AsyncConditionHandler2("async_condition_handle_2"));
            mapBuilder.put("async_condition_handle_3", new AsyncConditionHandler3("async_condition_handle_3"));
            mapBuilder.put("async_condition_handle_4", new AsyncConditionHandler4("async_condition_handle_4"));

            mapBuilder.put("async_base_test_handle1", new AsyncBaseTestHandler1("async_base_test_handle1"));
            mapBuilder.put("async_base_test_handle2", new AsyncBaseTestHandler2("async_base_test_handle2"));
            mapBuilder.put("async_base_test_handle3", new AsyncBaseTestHandler3("async_base_test_handle3"));

            mapBuilder.put("async_show_start_handle", new AsyncShowStartHandler("async_show_start_handle"));
            mapBuilder.put("async_show_end_handle", new AsyncShowEndHandler("async_show_end_handle"));

            mapBuilder.put("async_reorder_handle", new AsyncReorderHandler("async_reorder_handle"));
            mapBuilder.put("async_reorder_sample_handle", new AsyncReorderSampleHandler("async_reorder_sample_handle"));
            mapBuilder.put("async_cycle_test",new AsyncCycleTestHandler("async_cycle_test"));
            mapBuilder.put("async_cycle_sample_handler",new AsyncCycleSampleHandler("async_cycle_sample_handler"));

            MintFlow mintFlow = MintFlow.newBuilder(mapBuilder.build()).addFnMapper("base_async_test/async_complex_test.fn").build();
            while (true) {
                AtomicBoolean atomicBoolean = new AtomicBoolean(false);
                ParamWrapper paramWrapper = new ParamWrapper();
                paramWrapper.setParam(1);
                paramWrapper.setContextParam("condition_1", AsyncConditionTest.NO_GO);
                paramWrapper.setContextParam("condition_2", AsyncConditionTest.CAN_GO);
                paramWrapper.setContextParam("condition_3", AsyncConditionTest.NO_GO);
                paramWrapper.setContextParam("condition_4", AsyncConditionTest.CAN_GO);
                paramWrapper.setContextParam("show_start", false);
                paramWrapper.setContextParam("show_end", false);

                String itemCycle = "test1";
                StringBuilder ansCycle = new StringBuilder(itemCycle);
                paramWrapper.setContextParam(ASYNC_CYCLE_STR, itemCycle);

                String itemReorder = "test1";
                StringBuilder ansReorder = new StringBuilder(itemReorder);
                paramWrapper.setContextParam(ASYNC_REORDER_STR, itemReorder);
                mintFlow.runAsync(NAME_SPACE, ASYNC_PROCESS_NAME, paramWrapper, param -> {
                    assertEquals(13, (int) param.getResult(Integer.class));
                    int numCycle = param.getContextParam(random_number_cycle);
                    while (numCycle > 0) {
                        ansCycle.append(AsyncCycleTest.ADD_DATA_CYCLE);
                        numCycle--;
                    }
                    String nowCycleItem = paramWrapper.getContextParam(ASYNC_CYCLE_STR);
                    assertEquals(ansCycle.toString(), nowCycleItem);

                    int numReorder = param.getContextParam(random_number_reorder);
                    while (numReorder > 0) {
                        ansReorder.append(AsyncReorderTest.ADD_DATA_REORDER);
                        numReorder--;
                    }
                    String nowItem = param.getContextParam(ASYNC_REORDER_STR);
                    assertEquals(ansReorder.toString(), nowItem);


                    assertTrue(param.getContextParam("show_start"));
                    assertTrue(param.getContextParam("show_end"));
                    atomicBoolean.set(true);
                });

                assertTrue(atomicBoolean.get());

                show();
            }
        }
    }

    static class AsyncUseTemplateTest implements Runnable {

        @Override
        public void run() {
            MintFlowHandlerMap dataMapper = MintFlowHandlerMapFinder.findHandlerDataMap(
                    "org.mintflow.handler"
            );
            MintFlow mintFlow = MintFlow.newBuilder(dataMapper).addFnMapper("base_async_test/async_complex_test.fn").build();
            MintFlowTemplate mintFlowTemplate = MintFlowTemplate.newBuilder().addInterface(mintFlow, "org.mintflow.templateFunction").build();
            Function1 function1 = mintFlowTemplate.getTemplateFunction(Function1.class);
            String itemCycle = "test1";
            String itemReorder = "test1";
            while (true) {
                AtomicBoolean atomicBoolean = new AtomicBoolean(false);
                function1.test(1, itemCycle, itemReorder, AsyncConditionTest.NO_GO, AsyncConditionTest.CAN_GO, AsyncConditionTest.NO_GO, AsyncConditionTest.CAN_GO, false, false, param -> {
                    assertEquals(13, (int) param.getResult(Integer.class));
                    StringBuilder ansCycle = new StringBuilder(itemCycle);
                    StringBuilder ansReorder = new StringBuilder(itemReorder);

                    int numCycle = param.getContextParam("random_number_cycle");
                    while (numCycle > 0) {
                        ansCycle.append(AsyncCycleTest.ADD_DATA_CYCLE);
                        numCycle--;
                    }
                    String nowCycleItem = param.getContextParam(ASYNC_CYCLE_STR);
                    assertEquals(ansCycle.toString(), nowCycleItem);

                    int numReorder = param.getContextParam(random_number_reorder);
                    while (numReorder > 0) {
                        ansReorder.append(AsyncReorderTest.ADD_DATA_REORDER);
                        numReorder--;
                    }
                    String nowItem = param.getContextParam(ASYNC_REORDER_STR);
                    assertEquals(ansReorder.toString(), nowItem);


                    assertTrue(param.getContextParam("show_start"));
                    assertTrue(param.getContextParam("show_end"));
                    atomicBoolean.set(true);
                });
                assertTrue(atomicBoolean.get());
                show();
            }
        }
    }
}