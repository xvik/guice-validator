* (breaking) Switch to `jakarta.validation` (EE9, Bean validation 3, hibernate-validator 7 support) from `javax.validation`) 
* Fix module name (to ru.vyarus.guice.validator)
* Update to guice 4.2.3

### 2.0.0 (2019-12-24)
* Update to guice 4.2.2
* (breaking) Minimum requirement is java 8
* Update to validation api 2.0 ([changes comparing to 1.1](https://beanvalidation.org/2.0/))
* (breaking) Implicit/explicit modules merged into one configurable module: `ValidationModule`.    
    - To enable explicit mode: `new ValidationModule().validateAnnotatedOnly()` 
        or with custom annotation: `new ValidationModule().validateAnnotatedOnly(ToValidate.class)`
    - To avoid adding default group (previously `alwaysAddDefaultGroup`): `new ValidationModule().strictGroupsDeclaration()`
    - `.withMatcher` previously available only in implicit mode now works in both modes:
        `new ValidationModule().targetClasses(...)` 
* Add ability to filter target methods (in both modes): `new ValidationModule().targetMethods(...)`
    By default, synthetic and bridge methods are filtered      

Migration:

Old | New
----|----
`new ValidationModule()` |`new ValidationModule().validateAnnotatedOnly()`
`new ImplicitValidationModule` | `new ValidationModule()`   
`.withMatcher(...)` | `.targetClasses(...)` 
`.alwaysAddDefaultGroup(false)` | `strictGroupsDeclaration()`


### 1.2.0 (2016-04-05)
* Add validation groups support: groups declared with annotation and used like transactions (defining groups scope)

### 1.1.0 (2014-12-20)
* Update guice 3.0 -> 4.0-beta5
* Add binding for ValidatorFactory instance
* Add ImplicitValidationModule to apply validation based on validation annotations only (without need for explicit @ValidateOnExecution marker)

### 1.0.2 (2014-08-16)
* Fix pmd/chcekstyle warnings

### 1.0.1 (2014-06-29)
* Fix maven central compatibility

### 1.0.0 (2014-06-26)
* Initial release