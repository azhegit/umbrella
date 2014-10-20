package com.umbrella;

import java.util.Map;
import java.util.Set;

import com.google.common.util.concurrent.Service;
import com.google.common.util.concurrent.ServiceManager;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.Singleton;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.multibindings.Multibinder;
import com.umbrella.service.beanstalkd.BeanstalkdDBServiceModule;

public class ServiceManagerModule extends AbstractModule{
	
	private MapBinder<String, Service> serviceBinder;
	
	@Override
	protected void configure() {
		serviceBinder = MapBinder.newMapBinder(binder(), String.class, Service.class);
		install(new BeanstalkdDBServiceModule(serviceBinder));
		Multibinder<ServiceManager.Listener> multibinder = Multibinder.newSetBinder(binder(), ServiceManager.Listener.class);
		multibinder.addBinding().to(ShutdownListener.class).in(Scopes.SINGLETON);
	}
	
	@Provides
	@Singleton
	ServiceManager provideServiceManager(Set<ServiceManager.Listener> listeners, Map<String, Service> serviceMapping) {
		ServiceManager manager = new ServiceManager(serviceMapping.values());
		listeners.stream().forEach(r->manager.addListener(r));
		return manager;
	}
	
}
