#Guice validator
[![Gitter](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/xvik/guice-validator)
[![License](http://img.shields.io/badge/license-MIT-blue.svg)](http://www.opensource.org/licenses/MIT)
[![Build Status](http://img.shields.io/travis/xvik/guice-validator.svg)](https://travis-ci.org/xvik/guice-validator)
[![Coverage Status](https://img.shields.io/coveralls/xvik/guice-validator.svg)](https://coveralls.io/r/xvik/guice-validator?branch=master)

### About

Allows to validate service method parameters and return value using javax.validation annotations.
Suggest to use it with [hibernate-validator](http://hibernate.org/validator/) (but can be used with other implementations).

Features:
* Trigger validation on service method call
* Explicit and implicit validation modes (driven by additional annotation or directly by validation annotations)
* Inject dependencies to custom validators

### Setup

Releases are published to [bintray jcenter](https://bintray.com/vyarus/xvik/guice-validator/) and 
[maven central](https://maven-badges.herokuapp.com/maven-central/ru.vyarus/guice-validator).


[![JCenter](https://img.shields.io/bintray/v/vyarus/xvik/guice-validator.svg?label=jcenter)](https://bintray.com/vyarus/xvik/guice-validator/_latestVersion)
[![Maven Central](https://img.shields.io/maven-central/v/ru.vyarus/guice-validator.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/ru.vyarus/guice-validator)

Maven:

```xml
<dependency>
  <groupId>ru.vyarus</groupId>
  <artifactId>guice-validator</artifactId>
  <version>1.1.0</version>
</dependency>
<dependency>
  <groupId>org.hibernate</groupId>
  <artifactId>hibernate-validator</artifactId>
  <version>5.2.4.Final</version>
</dependency>
<dependency>
  <groupId>org.glassfish.web</groupId>
  <artifactId>javax.el</artifactId>
  <version>2.2.6</version>
</dependency>
```

Gradle:

```groovy
compile 'ru.vyarus:guice-validator:1.1.0'
compile 'org.hibernate:hibernate-validator:5.2.4.Final'
compile 'org.glassfish.web:javax.el:2.2.6'
```

Library targets guice 4, but [could be used with guice 3](https://github.com/xvik/guice-validator/wiki/Guice-3)

### Install the Guice module

#### Explicit module

Explicit module requires additional annotation `@ValidateOnExecution` on class or method to trigger runtime validation.

```java
install(new ValidationModule());
```

To create and use with default validation factory.

```java
install(new ValidationModule(yourValidationFactory));
```

To use custom (pre-configured) validation factory.

#### Implicit module

Implicit mode is the same as explicit, but without requirement for `@ValidateOnExecution` annotation: just method or
method parameter must be annotated with `@Valid` or any constraint annotation.

```java
install(new ImplicitValidationModule());
```

To use custom (pre-configured) validation factory:

```java
install(new ImplicitValidationModule(yourValidationFactory));
```

In order to configure types from automatic validation use custom matcher (e.g. to exclude some classes):

```java
install(new ImplicitValidationModule()
                .withMatcher(Matchers.not(Matchers.annotatedWith(SuppressValidation.class))))
```

### Examples

Note: if explicit module (`ValidationModule`) used `@ValidateOnExecution` should be applied to class or method.

Annotating method parameter with `@NotNull`

```java
public SimpleBean beanRequired(@NotNull SimpleBean bean) 
```

Now, if we call it like this

```java
myService.beanRequired(null);
```

`ConstraintViolationException` will be casted.

The same could be done for return value:

```java
@NotNull
public SimpleBean beanRequired(SimpleBean bean) 
```

Now exception will be thrown if method returns null.


##### Object state

If parameter or returned object contains validation annotations, and it must be checked before/after method execution,
add `@Valid` annotation.

```java
public SimpleBean beanRequired(@NotNull @Valid SimpleBean bean) 
```

`@Valid` used on method means validation of returned object:

```java
@Valid @NotNull
public SimpleBean validReturn(SimpleBean bean)
```

[Full example](https://github.com/xvik/guice-validator/tree/master/src/test/java/ru/vyarus/guice/validator/simple)

##### Other samples

* [Cross parameters check](https://github.com/xvik/guice-validator/tree/master/src/test/java/ru/vyarus/guice/validator/crossparams)
* [Composed validation annotation (aggregating few annotations into single one)](https://github.com/xvik/guice-validator/tree/master/src/test/java/ru/vyarus/guice/validator/compositeannotation)
* [Scripted check (hibernate-validator feature)](https://github.com/xvik/guice-validator/tree/master/src/test/java/ru/vyarus/guice/validator/script)

##### Custom validator

Guice injections could be used when writing custom validators

```java
public class ComplexBeanValidator implements ConstraintValidator<ComplexBeanValid, ComplexBean> {

    @Inject
    private CustomService customService;

    @Override
    public void initialize(ComplexBeanValid constraintAnnotation) {
        /* if annotation contains addition parameter it must be parsed here.. skipping for simplicity.
          NOTE: in such simple case we can make validator singleton, because of no internal state */
    }

    @Override
    public boolean isValid(ComplexBean value, ConstraintValidatorContext context) {
        /* common convention is to treat null values as valid and explicitly check them with @NotNull */
        return value == null || customService.getRequiredValue().equals(value.getUser());
    }
}

@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {ComplexBeanValidator.class})
@Documented
public @interface ComplexBeanValid {
    /* ideally there should be just localization key, but for simplicity just message */
    String message() default "Bean is not valid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
```

[Full example](https://github.com/xvik/guice-validator/tree/master/src/test/java/ru/vyarus/guice/validator/customtype)

### Bound objects

Both modules bind extra objects to context (available for injection) :

* `javax.validation.Validator`
* `javax.validation.executable.ExecutableValidator`
* `javax.validation.ValidatorFactory`

### Limitations

Guice aop is applied only for objects constructed by guice, so validation will not work for types
bound by instance:

```java
bind(MyType.class).toInstance(new MyType());
```

### More

More examples could be found in tests.

Also, read hibernate-validator docs:
* [Base annotations](http://docs.jboss.org/hibernate/validator/5.1/reference/en-US/html/chapter-bean-constraints.html)
* [Writing custom annotations] (http://docs.jboss.org/hibernate/validator/5.1/reference/en-US/html/validator-customconstraints.html)
* [Validation factory configuration] (http://docs.jboss.org/hibernate/validator/5.1/reference/en-US/html/chapter-bootstrapping.html)


### Compile time validation

Hibernate-validator provides annotation processor to perform additional checks in compile time: [see docs](http://docs.jboss.org/hibernate/validator/5.1/reference/en-US/html/validator-annotation-processor.html)

Because of this feature `@ValidateOnExecution` annotation chosen for runtime validation (explicit module): to allow using other annotations
just for compile time checks.

### Supplement

[validator-collection](https://github.com/jirutka/validator-collection) annotations to validate collections of simple types

-
[![java lib generator](http://img.shields.io/badge/Powered%20by-%20Java%20lib%20generator-green.svg?style=flat-square)](https://github.com/xvik/generator-lib-java)
