package cn.edu.njnu.minischema.base;

import cn.edu.njnu.minischema.enums.VariableType;

import java.lang.reflect.Method;

public class SentenceDescriptor {
	public int paramCount;

	public VariableType[] paramTypes;

	public Method method;

	public Class clazz;

	public Object instance;
}
