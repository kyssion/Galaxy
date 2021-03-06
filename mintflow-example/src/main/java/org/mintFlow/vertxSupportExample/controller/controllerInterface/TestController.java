package org.mintFlow.vertxSupportExample.controller.controllerInterface;

import org.mintFlow.vertxSupportExample.controller.bean.DefaultRequestBean;
import org.mintFlow.vertxSupportExample.controller.bean.DefaultResponseBean;
import org.mintflow.vertx.http.controller.MintFlowController;
import org.mintflow.vertx.http.controller.MintFlowRequestMapper;

@MintFlowController(
        url="test_controller",
        nameSpace = "test_controller"
)
public interface TestController {
    /**
     * 测试自动化配置接口
     * @param name
     * @param defaultResponseBean
     * @return
     */
    @MintFlowRequestMapper(url = "test_process",process = "test_process")
    DefaultResponseBean test(String name,
                             DefaultRequestBean defaultResponseBean);
}
