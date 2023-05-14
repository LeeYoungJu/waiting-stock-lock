package co.wadcorp.waiting.api.controller.support;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.List;

public class NonDuplicateOrderingConstraintValidator implements
    ConstraintValidator<NonDuplicateOrderingConstraint, List<? extends Ordering>> {

  @Override
  public boolean isValid(List<? extends Ordering> value, ConstraintValidatorContext context) {
    long distinctCount = value.stream()
        .map(Ordering::getOrdering)
        .distinct()
        .count();
    return distinctCount == value.size();
  }

}
