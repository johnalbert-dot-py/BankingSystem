package annotations;

import model.lib.enums.ParsedDataType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.HashMap;
import java.util.Map;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ModelField {

    boolean isNull() default false;
    int maxLength() default 255;
    boolean pk() default false;
    boolean ai() default false;
    String value() default "";
    String foreignKey() default "";

    String reference_to() default "";

    ParsedDataType foreignKeyType() default ParsedDataType.INT;
}
