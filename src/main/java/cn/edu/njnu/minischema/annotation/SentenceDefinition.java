package cn.edu.njnu.minischema.annotation;

import cn.edu.njnu.minischema.enums.VariableType;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface SentenceDefinition {
	// The operator used in the sentence, ignoring upper or lower case
	String operator();

	// The count of params accepted in the sentence, count < 0 means changeable variable counts
	int paramCount();

	// Accepted variable types of params
	VariableType[] paramTypes();
}
