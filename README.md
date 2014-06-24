Guice method validator
======================

Integrates guice with javax.validation 1.1 method validators (aka allows to use validation annotations for method parameters and return value validation)

Usage tested with hibernate validator (http://hibernate.org/validator/)

Required dependencies:
'org.hibernate:hibernate-validator:5.1.1.Final'
'org.glassfish.web:javax.el:2.2.6'

Include ValidationModule in your module configuration:
install(new ValidationModule());

Optionally, ValidatorFactory instance may be provided to module constructor to re-use already configured instance or
simply fine-tune factory for your needs.

After that use annotations on methods to check method parameters or return values, like
public void action(@NotNull @Valid SimpleBean bean)

To activate runtime checks use @ValidateOnExecution on class to enable for all methods or on exact methods.
Using this annotation to activate checks a bit contradict with javadoc, but allows using annotation not just for 
runtime checks but also for compile time checks (using hibernate validator annotation processor)

Various usage examples could be find in tests.
Otherwise, consult with hibernate-validator documentation: http://docs.jboss.org/hibernate/validator/5.1/reference/en-US/html/

Most useful:
base annotations and general usage: http://docs.jboss.org/hibernate/validator/5.1/reference/en-US/html/chapter-bean-constraints.html
writing custom annotations: http://docs.jboss.org/hibernate/validator/5.1/reference/en-US/html/validator-customconstraints.html
factory configuration: http://docs.jboss.org/hibernate/validator/5.1/reference/en-US/html/chapter-bootstrapping.html
using annotation processor (compile time checks): http://docs.jboss.org/hibernate/validator/5.1/reference/en-US/html/validator-annotation-processor.html

