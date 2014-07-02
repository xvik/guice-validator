#Guice validator

[![Build Status](https://travis-ci.org/xvik/guice-validator.svg?branch=master)](https://travis-ci.org/xvik/guice-validator)
[ ![Download](https://api.bintray.com/packages/vyarus/xvik/guice-validator/images/download.png) ](https://bintray.com/vyarus/xvik/guice-validator/_latestVersion)

### About

Allows to validate service method parameters and return value using javax.validation annotations.
Suggest to use it with [hibernate-validator](http://hibernate.org/validator/) (but can be used with other implementations).

Features:
* Trigger validation on service method call
* Inject dependencies to custom validators

### Setup

Releases are published to [bintray jcenter](https://bintray.com/bintray/jcenter) (package appear immediately after release) 
and then to maven central (require few days after release to be published). 

Maven:

```xml
<dependency>
  <groupId>ru.vyarus</groupId>
  <artifactId>guice-validator</artifactId>
  <version>1.0.1</version>
</dependency>
<dependency>
  <groupId>org.hibernate</groupId>
  <artifactId>hibernate-validator</artifactId>
  <version>5.1.1.Final</version>
</dependency>
<dependency>
  <groupId>org.glassfish.web</groupId>
  <artifactId>web:javax.el</artifactId>
  <version>2.2.6</version>
</dependency>
```

Gradle:

```groovy
compile 'ru.vyarus:guice-validator:1.0.1'
compile 'org.hibernate:hibernate-validator:5.1.1.Final'
compile 'org.glassfish.web:javax.el:2.2.6'
```

### Install the Guice module

```java
install(new ValidationModule());
```

To create and use with default validation factory.

```java
install(new ValidationModule(yourValidationFactory));
```

To use custom (pre-configured) validation factory.

### Usage

To enable runtime method validation, annotate entire class or method with `@ValidateOnExecution`.
Now all parameter or return value annotations will trigger validations on method execution. 

### Examples

Assuming `@ValidateOnExecution` applied to class.

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
``` 

### More

More examples could be found in tests.

Also, read hibernate-validator docs:
* [Base annotations](http://docs.jboss.org/hibernate/validator/5.1/reference/en-US/html/chapter-bean-constraints.html)
* [Writing custom annotations] (http://docs.jboss.org/hibernate/validator/5.1/reference/en-US/html/validator-customconstraints.html)
* [Validation factory configuration] (http://docs.jboss.org/hibernate/validator/5.1/reference/en-US/html/chapter-bootstrapping.html)


### Compile time validation

Hibernate-validator provides annotation processor to perform additional checks in compile time: [see docs](http://docs.jboss.org/hibernate/validator/5.1/reference/en-US/html/validator-annotation-processor.html)

Because of this feature `@ValidateOnExecution` annotation chosen for runtime validation: to allow using other annotations 
just for compile time checks.

### Licence

MIT