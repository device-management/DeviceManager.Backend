package com.nocotom.dm.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.handler.GenericHandler;
import org.springframework.messaging.MessagingException;

import javax.validation.*;
import java.util.Set;

@Configuration
public class ValidationConfiguration {

    @Bean(name = Handlers.CONSTRAINTS_VALIDATOR)
    public GenericHandler<Object> validationHandler(){
        final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        final Validator validator = factory.getValidator();

        return (payload, headers) ->{
            Set<ConstraintViolation<Object>> constraintViolations = validator.validate(payload);
            if(constraintViolations.size() > 0){
                throw new MessagingException("The message payload violates constraints.", new ConstraintViolationException(constraintViolations));
            }
            return payload;
        };
    }
}
