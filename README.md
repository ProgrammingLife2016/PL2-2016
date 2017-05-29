# DNAv [![Build Status](https://travis-ci.org/ProgrammingLife2016/PL2-2016.svg?branch=dev)](https://travis-ci.org/ProgrammingLife2016/PL2-2016) [![Coverage Status](https://coveralls.io/repos/github/ProgrammingLife2016/PL2-2016/badge.svg?branch=dev)](https://coveralls.io/github/ProgrammingLife2016/PL2-2016?branch=dev)

DNAv (pronounced D-Nav) is a shorthand for DNA Navigation. This application is a semantic visualization tool for analysis of DNA sequences, aimed at biogenetic researchers. The application uses sophisticated data management to process a collection of sequences and detects areas of interest for the user, which it shows in a graphical user interface.

### Scientific Importance
DNAv attempts to aid researchers in discovering the mysteries of evolution of the Tuberculosis bacterium. This bacterium evolves at a rapid pace, and its evolution is a great risk for all affected areas, as it can cause resistance to antibiotics. 
DNA is complex and mutations therein are varied and mostly unpredictable. Because of this, biogenetic researchers can often only rely on very limited automation. Their job involves detecting mutations and pinning down the root cause of them. Only in this way, can they completely annihilate the bacterium and nullify the Tuberculosis disease.

DNAv gives these researchers interactive, semantic and clearly visualized tools in their toolbox to help them with the detection and root cause analysis of these mutations.

### Documentation
Documentation can be found in the `documentation` folder.

#### Testing Documentation
Documentation regarding testing can be found at `documentation/testing.md`.

### Building and Running
#### Non-development use
To use the application, please download the most recent release `.jar` file from the [release page](https://github.com/ProgrammingLife2016/PL2-2016/releases).

#### Building from Source
When the project is first cloned, you must run `mvn install` before any other lifecycle goals.
##### Site Generation
To generate the site, run `mvn site:site site:stage`.

##### Packaging
To create a single runnable jar use `mvn compile assembly:single` inside the `PL2` directory. The executable jar will then be located at `PL2-launcher/target/PL2-launcher-<VERSION>-jar-with-dependencies.jar`.

This jar can be than executed by double-clicking.

### Organization, Team and Contact
DNAv is developed for the Contextproject course at the Delft University of Technology with course code TI2806, as part of the curriculum of the Bachelor Computer Science. The project is led by [Dr. Thomas Abeel](http://www.abeel.be/), who can be contacted by e-mail at [T.Abeel@tudelft.nl](mailto:T.Abeel@tudelft.nl).

Questions regarding the application can be directed to [Wouter Smit](https://github.com/Pathemeous) at [w.j.smit-1@student.tudelft.nl](mailto:w.j.smit-1@student.tudelft.nl) or to Justin van der Krieken at [j.m.vanderkrieken@student.tudelft.nl](mailto:j.m.vanderkrieken@student.tudelft.nl).

#### Team Members
Name | Student number
---|---
Wouter Smit | 4401409
Cas Bilstra | 4381084
Faris Elghlan | 4341538
Justin van der Krieken | 4357116
Casper Athmer | 4329066
