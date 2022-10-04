package dev.tarico.utils.forgeevent;

import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.IEventListener;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflectionEventListener implements IEventListener {

	final Object object;
	final Method targetMethod;

	public ReflectionEventListener(Object object, Method targetMethod) {
		this.object = object;
		this.targetMethod = targetMethod;
	}

	@Override
	public void invoke(Event event) {
		try {
			targetMethod.invoke(object, targetMethod.getParameterTypes()[0].cast(event));
		} catch (IllegalAccessException | IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e.getTargetException());
		}
	}

}
