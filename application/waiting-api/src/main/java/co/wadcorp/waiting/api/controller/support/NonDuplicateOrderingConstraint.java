package co.wadcorp.waiting.api.controller.support;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Constraint(validatedBy = NonDuplicateOrderingConstraintValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface NonDuplicateOrderingConstraint {

  String message() default "순서값은 중복될 수 없습니다.";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

}
