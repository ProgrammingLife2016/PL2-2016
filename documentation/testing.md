Testing Document
===================
This testing document will describe everything about our tests. It will describe which classes are and are not well tested and all of the special cases. Special cases include:

- Untested code and the reason why we haven't tested this code
- How we have manually tested pieces of code, such as parts of the user interface and why we didn't make automated test for these parts of code
- Exceptionally well tested code
- Uncommon/unique ways of testing

### Organisation of the testing document
This testing document will discuss the code coverage and special cases based on the modules. To get a better understanding of each module we will discuss the role of each module here.

##### Core
The core module contains the code which performs the core activities of the application. Currently this only includes all of the algorithms used to process the data.

##### Gui
The gui module contains the code which manages the user interface. This includes the layout of the user interface, the interactions between the user and the application and visualizing the data which has been requested by the user.

##### Launcher
The launcher module contains the code to launc the application. This module contains the main function and only calls the methods which are necessary to initialize (launch) the application.

##### Parser
The parser module contains the code which parses all of the external file formats which are needed and turns them into objects.

##### Shared
The shared module contains all of the code which is used by multiple modules. This includes things like interfaces and utility classes.

Code Coverage
===================
In this chapter we will discuss the code coverage of each module separately.
### Core
Currently the core module is not tested, because of a lack of time. This is one of our highest priorities for the next sprint (sprint 3).

### Gui
Currently the GUI module is not tested (see special cases).

### Launcher
Currently the launcher module is not tested (see special cases).

### Parser
The parser is fully tested.

### Shared
Currently all of the utility classes are fully tested. The data containers and interfaces are not yet tested because of a lack of time.

Special Cases
===================
In this chapter we will discuss the special cases of each module separately.
### Core
None.

### Gui
Currently the GUI module is not tested at all. The reason for this is that the GUI module is changing very rapidly and the requirements of the GUI pretty much change on a weekly basis, because of new customer input. For this reason the GUI module is not yet tested, as writing tests for code which will most likely be changed a lot is not very efficient. We plan to test the most important parts of the GUI module (which are the least likely to change) during sprint 3. We also plan to test all other functionality of the GUI module which is reasonable to test (see the next paragraph) during sprint 4.
Some parts of the gui module are very hard to test using automated tests and very easy to test manually.  An example of this would be animations. For this reason we will not test some parts of the GUI module. All of the parts which we are not going to test for this reason are listed below, including the reason why these parts are not reasonable to test automatically and how these parts can be tested manually.

- None as of this moment

### Launcher
Currently the launcher module is not tested. The reason for this is that the launcher module doesn't implement any new functionality, since it only initializes other parts of the application. The actions performed by the launcher module will be tested using intergration tests in the other modules, so there is no need to test this again in the launcher module.

### Parser
None.

### Shared
The shared package contains a few classes which implement unique testing behavior, so this will be discussed here. These classes are present in the <code>nl.tudelft.pl2016gr2.test.utility</code> package. These classes can be used to annotate a private field or method of a class with a <code>@TestId</code> tag and retrieve the value of the field, or call the method in the test class. This way we will not have to make getters and setters which are only used by the test classes and we can call private methods from within the test classes without having to make them visible to classes in the source code. The functionality of the methods in this package is based on reflection and is only intended to be used by test classes. See the test classes of these utility classes for usage examples.

> last updated during sprint 2