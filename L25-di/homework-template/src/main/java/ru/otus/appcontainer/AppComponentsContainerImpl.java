package ru.otus.appcontainer;

import java.lang.reflect.Method;
import java.util.*;
import ru.otus.appcontainer.api.AppComponent;
import ru.otus.appcontainer.api.AppComponentsContainer;
import ru.otus.appcontainer.api.AppComponentsContainerConfig;

@SuppressWarnings("squid:S1068")
public class AppComponentsContainerImpl implements AppComponentsContainer {

    private final List<Object> appComponents = new ArrayList<>();
    private final Map<String, Object> appComponentsByName = new HashMap<>();

    public AppComponentsContainerImpl(final Class<?> initialConfigClass) {
        this(new Class<?>[] {initialConfigClass});
    }

    public AppComponentsContainerImpl(final Class<?>... configClasses) {
        processConfig(configClasses);
    }

    private void processConfig(final Class<?>[] configClass) {
        Arrays.stream(configClass)
                .peek(this::checkConfigClass)
                .sorted(Comparator.comparingInt(
                        c -> c.getAnnotation(AppComponentsContainerConfig.class).order()))
                .forEach(this::processConfig);
    }

    private void processConfig(final Class<?> configClass) {
        try {
            Object configInstance = configClass.getDeclaredConstructor().newInstance();
            List<Method> methods = Arrays.stream(configClass.getDeclaredMethods())
                    .filter(m -> m.isAnnotationPresent(AppComponent.class))
                    .sorted(Comparator.comparingInt(
                            m -> m.getAnnotation(AppComponent.class).order()))
                    .toList();
            for (Method method : methods) {
                AppComponent annotation = method.getAnnotation(AppComponent.class);
                String componentName = annotation.name();
                if (appComponentsByName.containsKey(componentName)) {
                    throw new IllegalArgumentException("Duplicate component name: " + componentName);
                }

                Object[] args = resolveMethodParameters(method);
                Object component = method.invoke(configInstance, args);
                appComponents.add(component);
                appComponentsByName.put(componentName, component);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to process config: " + configClass.getName(), e);
        }
    }

    private Object[] resolveMethodParameters(final Method method) {
        return Arrays.stream(method.getParameterTypes())
                .map(this::getAppComponent)
                .toArray();
    }

    private void checkConfigClass(final Class<?> configClass) {
        if (!configClass.isAnnotationPresent(AppComponentsContainerConfig.class)) {
            throw new IllegalArgumentException(String.format("Given class is not config %s", configClass.getName()));
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <C> C getAppComponent(final Class<C> componentClass) {
        List<Object> candidates = appComponents.stream()
                .filter(component -> componentClass.isAssignableFrom(component.getClass()))
                .toList();

        if (candidates.isEmpty()) {
            throw new NoSuchElementException("Component not found for class: " + componentClass);
        }
        if (candidates.size() > 1) {
            throw new IllegalArgumentException("Multiple components found for class: " + componentClass);
        }

        return (C) candidates.get(0);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <C> C getAppComponent(String componentName) {
        Object component = appComponentsByName.get(componentName);
        if (component == null) {
            throw new NoSuchElementException("Component not found for name: " + componentName);
        }
        return (C) component;
    }
}
