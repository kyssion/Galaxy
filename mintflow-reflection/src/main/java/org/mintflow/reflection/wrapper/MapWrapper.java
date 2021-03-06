package org.mintflow.reflection.wrapper;


import org.mintflow.reflection.MirrorClass;
import org.mintflow.reflection.MirrorObject;
import org.mintflow.reflection.mirror.SystemMirrorObject;
import org.mintflow.reflection.object.ObjectFactory;
import org.mintflow.reflection.property.PropertyTokenizer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapWrapper extends BaseWrapper {
    private final Map<String, Object> map;
    public MapWrapper(MirrorObject mirrorObject, Map<String, Object> map) {
        super(mirrorObject);
        this.map = map;
    }

    @Override
    public Class<?> getType() {
        return this.mirrorObject.getType();
    }

    @Override
    public Object get(PropertyTokenizer prop) {
        if (prop.getIndex() != null) {
            Object collection = resolveCollection(prop, map);
            return getCollectionValue(prop, collection);
        } else {
            return map.get(prop.getName());
        }
    }

    @Override
    public void set(PropertyTokenizer prop, Object value) {
        if (prop.getIndex() != null) {
            Object collection = resolveCollection(prop, map);
            setCollectionValue(prop, collection, value);
        } else {
            map.put(prop.getName(), value);
        }
    }

    @Override
    public String[] getGetterNames() {
        return map.keySet().toArray(new String[map.keySet().size()]);
    }

    @Override
    public String[] getSetterNames() {
        return map.keySet().toArray(new String[map.keySet().size()]);
    }

    @Override
    public Class<?> getSetterType(String name) {
        PropertyTokenizer prop = new PropertyTokenizer(name);
        if (prop.hasNext()) {
            MirrorObject metaValue = mirrorObject.mirrorObjectForProperty(prop.getIndexedName());
            if (metaValue == SystemMirrorObject.NULL_META_OBJECT) {
                return Object.class;
            } else {
                return metaValue.getFiledMirrorObject().getSetterType(prop.getChildren());
            }
        } else {
            if (map.get(name) != null) {
                return map.get(name).getClass();
            } else {
                return Object.class;
            }
        }
    }

    @Override
    public Class<?> getGetterType(String name) {
        PropertyTokenizer prop = new PropertyTokenizer(name);
        if (prop.hasNext()) {
            MirrorObject metaValue = mirrorObject.mirrorObjectForProperty(prop.getIndexedName());
            if (metaValue == SystemMirrorObject.NULL_META_OBJECT) {
                return Object.class;
            } else {
                return metaValue.getFiledMirrorObject().getGetterType(prop.getChildren());
            }
        } else {
            if (map.get(name) != null) {
                return map.get(name).getClass();
            } else {
                return Object.class;
            }
        }
    }

    @Override
    public boolean hasSetter(String name) {
        return true;
    }

    @Override
    public boolean hasGetter(String name) {
        PropertyTokenizer prop = new PropertyTokenizer(name);
        if (prop.hasNext()) {
            if (map.containsKey(prop.getIndexedName())) {
                MirrorObject metaValue = mirrorObject.mirrorObjectForProperty(prop.getIndexedName());
                if (metaValue == SystemMirrorObject.NULL_META_OBJECT) {
                    return true;
                } else {
                    return metaValue.getFiledMirrorObject().hasGetter(prop.getChildren());
                }
            } else {
                return false;
            }
        } else {
            return map.containsKey(prop.getName());
        }
    }

    @Override
    public MirrorObject instantiatePropertyValue(String name, PropertyTokenizer prop, ObjectFactory objectFactory) {
        HashMap<String, Object> map = new HashMap<>();
        set(prop, map);
        return MirrorObject.forObject(map);
    }

    @Override
    public boolean isCollection() {
        return false;
    }

    @Override
    public void add(Object element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <E> void addAll(List<E> element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object invoke(String name, Object[] params) {
        return null;
    }
}
