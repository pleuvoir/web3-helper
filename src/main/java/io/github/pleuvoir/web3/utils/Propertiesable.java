package io.github.pleuvoir.web3.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * 支持 properties 文件到对象的转换<br>
 * 使用时继承此接口，建议仅当配置项只会使用一次时使用，因为每次创建对象都会重新载入文件
 * @author pleuvoir
 *
 */
public interface Propertiesable {

	default void load(String propPath) {
		PropLoaderUtil.setTargetFromProperties(this, propPath);
	}

	default void load(String propPath, String ignorePrefix) {
		PropLoaderUtil.setTargetFromProperties(this, propPath, ignorePrefix);
	}

	default void copyStateTo(Object target) {
		for (Field field : getClass().getDeclaredFields()) {
			if (!Modifier.isFinal(field.getModifiers())) {
				field.setAccessible(true);
				try {
					field.set(target, field.get(this));
				} catch (Exception e) {
					throw new RuntimeException("Failed to copy state: " + e.getMessage(), e);
				}
			}
		}
	}
}