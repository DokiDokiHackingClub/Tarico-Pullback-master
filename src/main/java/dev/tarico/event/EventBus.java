package dev.tarico.event;

import dev.tarico.event.events.world.EventPostUpdate;
import dev.tarico.event.events.world.EventPreUpdate;
import dev.tarico.event.events.world.EventTick;
import dev.tarico.module.modules.Module;
import net.minecraft.client.Minecraft;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class EventBus {
    private static final EventBus instance = new EventBus();
    private final ConcurrentHashMap<Class<? extends Event>, List<Handler>> registry = new ConcurrentHashMap<>();
    private final Comparator<Handler> comparator = Comparator.comparingInt(h -> h.priority);
    private final MethodHandles.Lookup lookup = MethodHandles.lookup();

    public static EventBus getInstance() {
        return instance;
    }

    @SuppressWarnings("all")
    public void register(Object... objs) {
        int n = objs.length;
        int n2 = 0;
        while (n2 < n) {
            Object obj = objs[n2];
            Method[] arrmethod = obj.getClass().getDeclaredMethods();
            int n3 = arrmethod.length;
            int n4 = 0;
            while (n4 < n3) {
                Method m = arrmethod[n4];
                if (m.getParameterCount() == 1 && m.isAnnotationPresent(EventTarget.class)) {
                    Class<?> eventClass = m.getParameterTypes()[0];
                    if (!this.registry.containsKey(eventClass)) {
                        this.registry.put((Class<? extends Event>) eventClass, new CopyOnWriteArrayList<>());
                    }
                    this.registry.get(eventClass).add(new Handler(m, obj, m.getDeclaredAnnotation(EventTarget.class).priority()));
                    this.registry.get(eventClass).sort(this.comparator);
                }
                ++n4;
            }
            ++n2;
        }
    }

    public void unregister(Object... objs) {
        int n = objs.length;
        int n2 = 0;
        while (n2 < n) {
            Object obj = objs[n2];
            for (List<Handler> list : this.registry.values()) {
                for (Handler data : list) {
                    if (data.parent != obj) continue;
                    list.remove(data);
                }
            }
            ++n2;
        }
    }

    public <E extends Event> E call(E event) {
        boolean whiteListedEvents = event instanceof EventTick || event instanceof EventPreUpdate || event instanceof EventPostUpdate;
        List<Handler> list = this.registry.get(event.getClass());
        if (list != null && !list.isEmpty()) {
            for (Handler data : list) {
                try {
                    if (list instanceof Module) {
                        if (((Module) list).getState()) {
                            if (whiteListedEvents) {
                                Minecraft.getMinecraft().mcProfiler.startSection(((Module) list).getName());
                            }
                            if (whiteListedEvents) {
                                Minecraft.getMinecraft().mcProfiler.endSection();
                            }
                        }
                    } else {
                        if (whiteListedEvents) {
                            Minecraft.getMinecraft().mcProfiler.startSection("non module");
                        }
                        if (whiteListedEvents) {
                            Minecraft.getMinecraft().mcProfiler.endSection();
                        }
                    }
                    if (Minecraft.getMinecraft().theWorld != null && Minecraft.getMinecraft().thePlayer != null)
                        data.handler.invokeExact(data.parent, event);
                } catch (Throwable e1) {
                    //
                }
            }
        }
        return event;
    }

    private class Handler {
        private final Object parent;
        private final byte priority;
        private MethodHandle handler;

        public Handler(Method method, Object parent, byte priority) {
            if (!method.isAccessible()) {
                method.setAccessible(true);
            }
            MethodHandle m = null;
            try {
                m = EventBus.this.lookup.unreflect(method);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            if (m != null) {
                this.handler = m.asType(m.type().changeParameterType(0, Object.class).changeParameterType(1, Event.class));
            }
            this.parent = parent;
            this.priority = priority;
        }
    }

}

