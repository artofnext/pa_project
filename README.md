# pa_project

### Disclaimer:
This project was developed as part of a training course, use it at your own risk


## Description

This library is designed to create and visualize a data model based on the JSON view. Also, the library provides the ability to expand the functionality of working with the model using plugins.

## Usage

You can fork this repository or just save it to your Kotlin project

## Manual

### Data model

Instantiation of simple data types is possible using extension functions that convert all basic simple data types to their corresponding model elements.
Example:
```
<Simple Type>.toJvalue()
```
Enumerated data types are converted in the same way.
```
Enumerated<E>.toJvalue()
```
It is also possible to convert instances of data classes. This transformation is recursive and includes all the properties of the given class, transforming them into the corresponding model objects. The model will repeat the structure of the class, excluding inner classes that are not data classes and their methods. Usage example:
```
valueName = dataClassToJobject(<data class instance>)
```
It is also possible to control the creation of the model using annotations for the properties of the data class.

* `@Remove` allows you to exclude the creation of a model object for a given class property
* `@Rename("newPropertyName")` allows you to change the name of the property in the model object.
More specific examples can be found in the tests.

This model can be used to search for objects by type, name or content. For this, there are functions based on the Visitor pattern. It is also possible to expand the functionality by creating new Visitor classes that can implement any actions on the elements of the model. For this it is enough that your class implements the Visitor interface.
It is also possible to filter elements of a model using a predicate passed as a parameter to the filter extension function. This function returns a list of all model’s elements that match the predicate.
```
<Jvalue>.filter( (Jvalue) -> Boolean): List<Jvalue>
```

### Visualisation
Visualization is done using [Eclipse SWT library.](https://www.eclipse.org/swt/)
To use this part, you must import this library into your project
The data model is presented visually in the form of a tree structure. Search by tree structure and display of the selected node as JSON is implemented.

### Visualization plugin development
It is provided to add 2 types of plugins: to add icons for nodes and actions.
Plugin connection configuration is carried out in the file `dependency.properties`.

Each plugin must implement the corresponding interface, that is, `Appiarance` for icon abortions and `Action` for actions.
The source code provides examples of implementations of three different icon sets and three actions.
