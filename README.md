# Guice validator
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
* Validation groups support (as context, like transactional calls)

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
  <version>1.2.0</version>
</dependency>
<dependency>
  <groupId>org.hibernate</groupId>
  <artifactId>hibernate-validator</artifactId>
  <version>5.4.1.Final</version>
</dependency>
<dependency>
  <groupId>org.glassfish</groupId>
  <artifactId>javax.el</artifactId>
  <version>3.0.1-b08</version>
</dependency>
```

Gradle:

```groovy
compile 'ru.vyarus:guice-validator:1.2.0'
compile 'org.hibernate:hibernate-validator:5.4.1.Final'
compile 'org.glassfish:javax.el:3.0.1-b08'
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

### Custom validator

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
* `ru.vyarus.guice.validator.group.ValidationContext`

### Limitations

Guice aop is applied only for objects constructed by guice, so validation will not work for types
bound by instance:

```java
bind(MyType.class).toInstance(new MyType());
```

### Validation context

[Validation groups](http://docs.jboss.org/hibernate/validator/5.1/reference/en-US/html/chapter-groups.html) 
could be used to apply different validations for the same object (or same method).

For example, we have model class with 2 validation groups
```java
public class MyModel {
    @NotNull
    private String defField;
    @NotNull(group = Group1.class)
    private String group1Field;
    @NotNull(group = Group2.class)
    private String group2Field;
}   
```

Note that Group1 and Group2 could be any classes or interfaces (it doesn't matter, because they simply define group by type).

If we use model in method like this:

```java
public class MyService {
    public void do(@Valid MyModel model) {...}
}
```

Only `defField` will be validated (because it implicitly belongs to default (`Default`) group).

#### Groups annotation

In order to enable other groups use `@ValidationGroups` annotation.

For example,

```java
@ValidationGroups(Group1.class)
public void do(@Valid MyModel mode); {...}
```

This enables `Group1` so `defField` and `group1Field` will be validated (default group included by default, but can be disabled (read below)).

Annotation could define more then one group: 

```java
@ValidationGroups({Group1.class, Group2.class})
```

Annotation may be used on class to affect all methods. Also, see advanced annotations usage below.

#### Understanding context

`@ValidationGroups` annotation affects not just one method, but all methods executed by this method or any subsequent method (in the same thread!).
We can say that annotation creates *validation context*.

Suppose we have service without context:

```java
public class MyService {
    public void do(@Valid MyModel mode); {...}
}
```

Defining upper level service with context:

```java
@ValidationGroups(Group1.class)
public class MyGroup1Service {
    @Inject
    private MyService service;
    
    public void foo(MyModel model) {
        service.do(model);
    }
}
```

Validation context is defined for all methods in service (and all subsequent calls). 
So when `foo` method call `service.do` method, validation context would be already defined and actual validation
would be performed with default and Group1 groups.

The same way, some other upper level service could define different groups. So upper levels define general validation context,
while lower levels stay generic and re-usable.

Overall, validation context works very much like `@Transactional` in guice-persist or spring.

##### Context composition

In situation like this:

```java
public class RootService {
    @Inject
    private MyGroup1Service service;
    
    @ValidationGroups(Group2.class)
    public void bar(MyModel model) {
        service.foo(model);
    }
}
```

* Method `RootService.bar` is under {`Group2`} context
* Subsequent method `MyGroup1Service.foo` is under {`Group1`} context
* (Sub)Subsequent `method MyService.do` performs validation

Both contexts will compose (merge) and last method will be called with default, Group1 and Group2 groups.

##### Composition rules

If `@ValidationGroups` annotation defined both on type and method, then actual method context will use groups from both annotations.

Subsequent validation contexts inherit all groups from upper context.

##### Manual context definition

Validation context could be defined manually, by using `ru.vyarus.guice.validator.group.ValidationContext` singleton:

```java
public class ManualContextDemo {
    @Inject
    private ValidationContext context;
    
    public void foo() {
        context.doWithGroups(new GroupAction<Void>(){
            public Void call() throws Throwable {
                // all methods called here will be validated with specified groups
                // this is equivalent to method annotation @ValidationGroups({Group1.class, Group10.class})
            }
        }, Group1.class, Group10.class)
    }
}
```

#### Default group specifics

Default behaviour is to always use default group. So when you define validation context with groups {Group1, Group2}, 
actual context would be {Group1, Group2, Default}. This was done in order to provide more intuitive behavior:
validation context extends default validation scope.

If you want to prevent this behavior use `alwaysAddDefaultGroup` module option:

```java
new ImplicitValidationModule()
                .alwaysAddDefaultGroup(false)
```

Explicit module has the same option. If you disable default group addition, then default validations (annotations
where you didn't specify group) will not be used, unless you specify Default group manually in validation context.

#### Advanced annotations usage

In some cases it makes sense to use your own annotations for context definition, e.g.:
* Because they are more descriptive in code
* You want to group multiple groups (the same way as you can group multiple validations in javax validation).

Due to the fact that any class could be used for group name, we can use our new annotation class itself as group name.

For example (example taken from [hibernate-validator docs](http://docs.jboss.org/hibernate/validator/5.1/reference/en-US/html/chapter-groups.html#example-driver)):
```java
public class Person {
    @NotNull
    private String name;
    @AssertTrue(group = DriverContext.class)
    private boolean driverLicense;
}
```

We used annotation class as group name.

```java
@Target({TYPE, METHOD})
@Retention(RetentionPolicy.RUNTIME)
@ValidationGroups(DriverScope.class)
public @interface DriverScope {
}
```

Note that annotation is annotated by `@ValidationGroups(DriverScope.class)`.

Groups interceptor implementation is able to find such annotated annotations and use `@ValidationGroups` defined on them.

So in service you could simply use your annotation:

```java
@DriverContext
public class DriverService {
    ...
}
```

All method called by driver service will be validated with `DriverContext` group.

If you don't like the idea of using annotations as validation groups, then you can still use your own annotations just
for grouping. For example:

```java
@Target({TYPE, METHOD})
@Retention(RetentionPolicy.RUNTIME)
@ValidationGroups({Group1.class, Group10.class})
public @interface MyCustomScope {
}
```

#### Cache

In order to avoid `ValidationGroups` annotations lookup for each method call, resolution result is cached on first execution inside
`ru.vyarus.guice.validator.group.annotation.MethodGroupsFactory`.

If you use JRebel or other class reloading tool (maybe some other reason) you will need to disable descriptors caching.

To do it set system property or environment variable:

```
ru.vyarus.guice.validator.group.annotation.MethodGroupsFactory.cache=false
```

Or from code:

```java
MethodGroupsFactory.disableCache();
```

Also, you can clear cache manually (on instance):

```java
injector.getInstance(MethodGroupsFactory.class).clearCache()
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

---
[![java lib generator](http://img.shields.io/badge/Powered%20by-%20Java%20lib%20generator-green.svg?style=flat-square)](https://github.com/xvik/generator-lib-java)
