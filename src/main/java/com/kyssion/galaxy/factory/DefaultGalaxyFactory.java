package com.kyssion.galaxy.factory;

import com.kyssion.galaxy.Galaxy;
import com.kyssion.galaxy.builder.HandleMapBuilder;
import com.kyssion.galaxy.builder.ProcessMapBuilder;
import com.kyssion.galaxy.handle.Handle;
import com.kyssion.galaxy.handle.header.HeadHander;
import com.kyssion.galaxy.process.Process;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class DefaultGalaxyFactory implements GalaxyFactory {

    private static final AtomicReference<Galaxy> galaxyCache =
            new AtomicReference<>();

    private String handlePath;
    private String processPath;
    private String mapperPath;

    public DefaultGalaxyFactory(String handPath, String mapperPath, String processPath) {
        this.handlePath = handPath;
        this.mapperPath = mapperPath;
        this.processPath = processPath;
    }

    @Override
    public Galaxy create() {
        try {
            Galaxy galaxy = galaxyCache.get();
            if (galaxy == null) {
                //所有处理方法的集合
                Map<String, Handle> handleMap = HandleMapBuilder.build(this.handlePath);
                Map<String, Class<? extends Process>> processMap = ProcessMapBuilder.build(this.processPath);
                Map<String, HeadHander> headHanderMap = new HashMap<>();
                galaxy = new Galaxy(headHanderMap, processMap);
                galaxyCache.compareAndSet(null, galaxy);
                return galaxyCache.get();
            } else {
                return galaxy;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
