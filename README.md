# Guice validator
[![License](http://img.shields.io/badge/license-MIT-blue.svg)](http://www.opensource.org/licenses/MIT)
[![Build Status](http://img.shields.io/travis/xvik/guice-validator.svg?branch=master)](https://travis-ci.org/xvik/guice-validator)
[![Appveyor build status](https://ci.appveyor.com/api/projects/status/github/xvik/guice-validator?svg=true&branch=master)](https://ci.appveyor.com/project/xvik/guice-validator)
[![codecov](https://codecov.io/gh/xvik/guice-validator/branch/master/graph/badge.svg)](https://codecov.io/gh/xvik/guice-validator)

Support:

* [gitter chat](https://gitter.im/xvik/guice-validator)

### About

Validates service method parameters and return value using jakarta.validation 3.0 (the difference with 2.0 is only in api package) annotations.
Used with [hibernate-validator](http://hibernate.org/validator/) (currently, the only [certified implementation](https://beanvalidation.org/2.0/)).

Features:

* Service method call parameters and return value validation
* Explicit and implicit validation modes (driven by additional annotation or directly by validation annotations)
* Guice injections work in custom validators
* Validation groups support (as context, like transactional calls)

For guice 4 and java 8 (binary compatible with java 11)

[Old version 1.2.0 docs](https://github.com/xvik/guice-validator/tree/1.2.0)

### Important!

Since Java EE 9 `javax.validation` was renamed to `jakarta.validation` and *Bean Validation* become 3.0.
[Hibernate-validator 7.0 targets new package](https://in.relation.to/2021/01/06/hibernate-validator-700-62-final-released/).

Current guice-validator (3.x) targets hibernate 7 (and `jakarta.validation`), but if you still
use hibernate-validator 6 (and `javax.validation`) then use previous version: 

Version | Target
----|----
guice validator 3.x | Hibernate-validator 7, for `jakarta.validation`
[guice-validator 2.x](https://github.com/xvik/guice-validator/tree/2.0.0) | Hibernate-validator 6.x, for `javax.validation` 

### Migration

If you migrating from hibernate-validator 6.x then change dependencies:

Before |  After
---- | ------
ru.vyarus:guice-validator:2.0.0 | ru.vyarus:guice-validator:3.0.0
javax.validation:validation-api:2.0.1.Final | jakarta.validation:jakarta.validation-api:3.0.0
org.hibernate:hibernate-validator:6.2.0.Final|  org.hibernate:hibernate-validator:7.0.0.Final
org.glassfish:javax.el:3.0.1-b12 |  org.glassfish:jakarta.el:4.0.1

And rename `javax.validation` package to `jakarta.validation` everywhere. Everything else is the same.

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
  <version>3.0.0</version>
</dependency>
<dependency>
  <groupId>org.hibernate</groupId>
  <artifactId>hibernate-validator</artifactId>
  <version>7.0.0.Final</version>
</dependency>
<dependency>
  <groupId>org.glassfish</groupId>
  <artifactId>jakarta.el</artifactId>
  <version>4.0.1</version>
</dependency>
```

Gradle:

```groovy
implementation 'ru.vyarus:guice-validator:3.0.0'
implementation 'org.hibernate:hibernate-validator:7.0.0.Final'
implementation 'org.glassfish:jakarta.el:4.0.1'
```

#### Snapshots

Snapshots could be used through JitPack:

* Go to [JitPack project page](https://jitpack.io/#ru.vyarus/guice-validator)
* Select `Commits` section and click `Get it` on commit you want to use (you may need to wait while version builds if no one requested it before)
* Follow displayed instruction: 
    - Add jitpack repository: `maven { url 'https://jitpack.io' }`
    - Use commit hash as version: `ru.vyarus:guice-validator:6933889d41`


### Usage

Install module:

```java
install(new ValidationModule())
```

#### Implicit                     

By default, will work in "implicit mode": matching all methods with `@Valid` or `Constraint` (all validation 
annotations are annotated with `@Constraint` and so easy to recognize) annotations.

For example, 

```java
public class SomeService {
    public SimpleBean beanRequired(@NotNull SimpleBean bean) {}
} 
```

Will throw `ConstraintViolationException` exception if called as: 

```java
service.beanRequired(null)
```

If return value must be validated, method must contain `@Valid` or `Constraint` annotation:

```java
@NotNull
public SimpleBean beanRequired(SimpleBean bean) {
    return null;
}
```     

Will throw `ConstraintViolationException` exception when called (due to returned null).

#### Explicit

Explicit mode may be used if you need to manually control validated methods:

```java
install(new ValidationModule().validateAnnotatedOnly())
```

This way only methods directly annotated with `@ValidateOnExecution` or methods inside annotated class
will trigger validation.

For example:

```java
@ValidateOnExecution 
public class SampleService {
    public void method1() {}
    public void method2() {}
}
```

Both methods will trigger validation. 

Note that in contrast to implicit mode, existence of constraint annotations is not checked
(simply nothing will happen on validation it not annotated, but validation will be called).

And for method:

```java
public class SampleService {
    
    @ValidateOnExecution
    public void method1() {}

    public void method2() {}
}
```

Now only `method1` will trigger validation.

NOTE: javadoc of `@ValidateOnExecution` contradicts with such usage, but its name is ideal for such usage
(no need to introduce more annotations).

In case if you don't like default annotation, you can use your own:

```java
install(new ValidationModule().validateAnnotatedOnly(ToValidate.class))
```

NOTE Hibernate-validator provides annotation processor to perform additional checks in compile time: [see docs](https://docs.jboss.org/hibernate/validator/6.0/reference/en-US/html_single/#validator-annotation-processor)
In this case explicit mode could be used to differentiate compile time-only annotations from 
runtime checks (only methods annotated with `@ValidateOnExecution` will be validated at runtime).

#### Validation factory

If you use custom validation factory then specify it directly:

```java
install(new ValidationModule(yourValidationFactory));
```

NOTE: even with custom validation factory, custom `ConstraintValidatorFactory` will be used
in order to be able to wire injections inside custom validators.

This also means that validator obtained directly from your validator factory and 
validator actually used in guice will be different: directly obtained validator will not be able
to inject guice dependencies.

#### Reducing scope

You can specify additional class and method matchers to exclude classes or methods from 
validation triggering. This works in both implicit and explicit modes.

For example, introduce custom annotation to manually disable validations:

```java
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SuppressValidation {} 
```

```java
install(new ValidationModule()
        .targetClasses(Matchers.not(Matchers.annotatedWith(SuppressValidation.class)))
        .targetMethods(Matchers.not(Matchers.annotatedWith(SuppressValidation.class))));
```

Now any annotated nethod (or all methods in annotated class) will not trigger validation:

```java
public class SampleService {    
    @SuppressValidation
    public void method(@NotNull String arg) {}
}
```    

### Bound objects

Both modules bind extra objects to context (available for injection) :

* `jakarta.validation.Validator`
* `jakarta.validation.executable.ExecutableValidator`
* `jakarta.validation.ValidatorFactory`
* `ru.vyarus.guice.validator.group.ValidationContext`

For example, `@Inject Validator validator` may be useful for manual object validations.

NOTE: don't use `ValidatorFactory` directly, because it is not aware of guice and so 
will not be able to wire guice injections into custom validators.    


### Examples

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

##### Annotations composition

If you often declare multiple annotations, then it could be simplier to introduce new 
composite validation.

For example, here is composition of `@NotNull` and `@Size(min = 2, max = 14)`:  

```java
@NotNull
@Size(min = 2, max = 14)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {})
@Documented
@ReportAsSingleViolation //optional
public @interface ComposedCheck {
    String message() default "Composed check failed";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
```     

```java
public String checkParam(@ComposedCheck String string) {}
```   

* [Full example](https://github.com/xvik/guice-validator/tree/master/src/test/java/ru/vyarus/guice/validator/compositeannotation)

##### Cross parameters check

If it is important to validate method parameters "together", then custom validator have to be declared:

```java
@SupportedValidationTarget(ValidationTarget.PARAMETERS)
public class CrossParamsValidator implements ConstraintValidator<CrossParamsCheck, Object[]> {

    @Override
    public void initialize(CrossParamsCheck constraintAnnotation) {}

    @Override
    public boolean isValid(Object[] value, ConstraintValidatorContext context) {
        Integer param1 = (Integer) value[0];
        Object param2 = value[1];
        return param1 != null && param1 == 1 && param2 instanceof Integer;
    }
}
```  

Validation annotation:

```java
@Constraint(validatedBy = CrossParamsValidator.class)
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CrossParamsCheck {
    String message() default "Parameters are not valid";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
```

And now, it may be used to validated method parameters:

```java
@CrossParamsCheck
public void action(Integer param1, Object param2) {}
```

[Hibernate docs](https://docs.jboss.org/hibernate/validator/6.0/reference/en-US/html_single/#example-using-cross-parameter-constraint)

[Full example](https://github.com/xvik/guice-validator/tree/master/src/test/java/ru/vyarus/guice/validator/crossparams)

##### Scripted check

Bean level validation:

```java
@ScriptAssert(lang = "javascript", script = "it.start.before(it.finish)", alias = "it")
public class ScriptedBean {

    private Date start;
    private Date finish;
    ...
}
```

Validation could be triggered by `@Valid`:

```java
public void method(@Valid ScriptedBean bean) {}
```

Parameter level check:

```java
@ParameterScriptAssert(lang = "javascript", script = "arg0.size() == arg1")
public void paramsValid(List<Integer> list, int count) {}
```

[Hibernate docs](https://docs.jboss.org/hibernate/validator/6.0/reference/en-US/html_single/#section-builtin-method-constraints)

[Full example](https://github.com/xvik/guice-validator/tree/master/src/test/java/ru/vyarus/guice/validator/script)

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

### Limitations

Guice aop is applied only for objects constructed by guice, so validation will not work for types
bound by instance:

```java
bind(MyType.class).toInstance(new MyType());
```

### Validation context

[Validation groups](https://docs.jboss.org/hibernate/validator/6.0/reference/en-US/html_single/#chapter-groups) 
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

If you want to prevent this behavior use `strictGroupsDeclaration` module option:

```java
new ValidationModule().strictGroupsDeclaration()
```

Explicit module has the same option. If you disable default group addition, then default validations (annotations
where you didn't specify group) will not be used, unless you specify Default group manually in validation context.

#### Advanced annotations usage

In some cases it makes sense to use your own annotations for context definition, e.g.:
* Because they are more descriptive in code
* You want to group multiple groups (the same way as you can group multiple validations in jakarta validation).

Due to the fact that any class could be used for group name, we can use our new annotation class itself as group name.

For example (example taken from [hibernate-validator docs](https://docs.jboss.org/hibernate/validator/7.0/reference/en-US/html_single/#chapter-groups)):
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

* [Constraints](https://docs.jboss.org/hibernate/validator/6.0/reference/en-US/html_single/#section-declaring-bean-constraints)
* [Declaration](https://docs.jboss.org/hibernate/validator/6.0/reference/en-US/html_single/#chapter-method-constraints)
* [Validation factory configuration](https://docs.jboss.org/hibernate/validator/6.0/reference/en-US/html_single/#chapter-bootstrapping)

### Supplement

[validator-collection](https://github.com/jirutka/validator-collection) annotations to validate collections of simple types

---
[![java lib generator](http://img.shields.io/badge/Powered%20by-%20Java%20lib%20generator-green.svg?style=flat-square)](https://github.com/xvik/generator-lib-java)
