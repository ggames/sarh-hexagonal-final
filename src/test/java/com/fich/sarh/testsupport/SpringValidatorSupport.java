package com.fich.sarh.testsupport;

import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.SpringConstraintValidatorFactory;

/**
 * Fábrica de {@link jakarta.validation.Validator} para tests de una sola regla.
 *
 * <p>Los validadores custom ({@code UniqueDocumentAgentValidator},
 * {@code UniqueUsernameValidator}) son {@code @Component} sin constructor por
 * defecto: solo pueden instanciarse resolviendo sus dependencias (los puertos
 * mockeados) desde un {@code BeanFactory}. Así los tests quedan aislados del
 * contexto de Spring completo.</p>
 */
public final class SpringValidatorSupport {

    private SpringValidatorSupport() {
    }

    public static LocalValidatorFactoryBean validatorWithBeans(Object... beans) {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        int index = 0;
        for (Object bean : beans) {
            beanFactory.registerSingleton("testBean" + index++, bean);
        }
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.setConstraintValidatorFactory(new SpringConstraintValidatorFactory(beanFactory));
        validator.afterPropertiesSet();
        return validator;
    }
}
