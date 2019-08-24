
package org.mekweg.proxy;

import org.mekweg.annotation.ParamKey;
import org.mekweg.annotation.ProcessMethod;
import org.mekweg.annotation.ProcessNameSpace;
import org.mekweg.exception.NoProcessIException;
import org.mekweg.handle.StartHandler;
import org.mekweg.param.ParamWrapper;
import org.mekweg.scheduler.HandlerScheduler;
import org.mekweg.scheduler.Scheduler;
import org.mekweg.process.Process;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

/**
 *
 */
public class MapperProxy<T extends Process> implements InvocationHandler {

    private static final long serialVersionUID = -6424540398559729838L;

    private Map<String, StartHandler> startHanderMap;
    private Scheduler scheduler;
    Class<? extends Process> mapperClass;
    public MapperProxy(Class<? extends Process> mapperClass, Map<String, StartHandler> startHanderMap) {
        this(mapperClass, startHanderMap, null);
    }

    public MapperProxy(Class<? extends Process> mapperClass,
                       Map<String, StartHandler> startHanderMap, Scheduler scheduler) {
        this.startHanderMap = startHanderMap;
        this.scheduler = scheduler == null ? new HandlerScheduler() : scheduler;
        this.mapperClass=mapperClass;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        StringBuilder key = new StringBuilder();
        ProcessMethod process = method.getAnnotation(ProcessMethod.class);
        ProcessNameSpace nameSpace = this.mapperClass.getAnnotation(ProcessNameSpace.class);
        if (process != null&&nameSpace!=null) {
           key.append(nameSpace.value()).append(".").append(process.value());
        } else {
            key.append(this.mapperClass.getName()).append(".").append(method.getName());
        }
        StartHandler startHandler = startHanderMap.get(key.toString());
        if (startHandler == null) {
            try {
                throw new NoProcessIException("this is no namespace Recording which name is :" + key);
            } catch (NoProcessIException e) {
                e.printStackTrace();
                return null;
            }
        }
        ParamWrapper paramWrapper = new ParamWrapper();
        Annotation[][] annotations = method.getParameterAnnotations();
        for (int a=0;a<args.length;a++) {
            String keyName = args[a].getClass().getSimpleName();
            for (Annotation annotation : annotations[a]){
                if(annotation instanceof ParamKey){
                    keyName = ((ParamKey) annotation).value();
                }
            }
            paramWrapper.putOrigalParams(keyName,args[a]);
        }
        paramWrapper = this.scheduler.run(paramWrapper, startHandler.getHandleList());
        return paramWrapper.getReturnParams(method.getReturnType());
    }

}
