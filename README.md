# DNAv [![Build Status](https://travis-ci.org/ProgrammingLife2016/PL2-2016.svg?branch=dev)](https://travis-ci.org/ProgrammingLife2016/PL2-2016)

DNAv (pronounced D-Nav) is a shorthand for DNA Navigation. This application is a semantic visualization tool for analysis of DNA sequences, aimed at biogenetic researchers. The application uses sophisticated data management to process a collection of sequences and detects areas of interest for the user, which it shows in a graphical user interface.

### Scientific Importance
DNAv attempts to aide researchers in discovering the mysteries of evolution of the Tuberculosis bacterium. This bacterium evolves at a rapid pace, and its evolution is a great risk for all affected areas, as it can cause resistance to antibiotics. 
DNA is complex and mutations therein are varied and mostly unpredictable. Because of this, biogenetic researchers can often only rely on very limited automation. Their job involves detecting mutations and pinning down the root cause of them. Only in this way, can they completely annihilate the bacterium and nullify the Tuberculosis disease.

DNAv gives these researchers interactive, semantic and clearly visualized tools in their toolbox to help them with the detection and root cause analysis of these mutations.

### Documentation
Documentation is currently stored under the `documentation` folder.

#### Testing documentation
Documentation regarding testing can be found at `documentation/testing.md`.

### Building and running
When the project is first cloned, you must run `mvn install` before any other lifecycle goals.
#### Site generation
To generate the site, run `mvn site:site site:stage`.

#### Packaging
To create a single runnable jar use `mvn compile assembly:single` inside the `PL2` directory. The executable jar will then be located at `PL2-launcher/target/PL2-launcher-<VERSION>-jar-with-dependencies.jar`.

This jar can be than executed by double-clicking.

### Organization and Team
DNAv is developed for the Contextproject course at the Delft University of Technology with course code TI2806, as part of the curriculum of the Bachelor Computer Science. The project is led by [Dr. Thomas Abeel](http://www.abeel.be/), who can be contacted by e-mail at [T.Abeel@tudelft.nl](mailto:T.Abeel@tudelft.nl).

#### Members
- Wouter Smit
- Cas Bilstra
- Faris Elghlan
- Justin van der Krieken
- Casper Athmer
