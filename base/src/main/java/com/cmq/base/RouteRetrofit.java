package com.cmq.base;

import com.alibaba.android.arouter.launcher.ARouter;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

public class RouteRetrofit {

    private static Map<String,Object> cache = new HashMap<>();

    public static <T> T getRouteConfig(Class<T> routeMapping){
        T mapping = (T)cache.get(routeMapping.getCanonicalName());
        if(mapping == null){
            synchronized (cache){
                mapping = generateMapping(routeMapping);
                cache.put(routeMapping.getCanonicalName(), mapping);
            }

        }

        return mapping;
    }

    private static <T> T generateMapping(Class<T> routeMapping) {
        return (T)Proxy.newProxyInstance(RouteRetrofit.class.getClassLoader(), new Class[]{routeMapping}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                RouteMapping route = method.getAnnotation(RouteMapping.class);

                ARouter.getInstance().build(route.value()).navigation();
                return null;
            }
        });
    }
}
