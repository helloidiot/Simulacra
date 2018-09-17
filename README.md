# Simulacra

MA Computational Art final project.

## Getting Started

For ‘simulacraMaster’, simply place the folder in your Processing working folder and build.
For ‘simulacraViewer’, copy the contents of ofApp.h, ofApp.cpp and Main.h into a new project. 

### Prerequisites

simulacraMaster;

```
peasyCam
controlP5
Nervous System OBJ Exporter
Toxiclibs Grey Scott
```

simulacraViewer;

```
ofxOMXPlayer
```

## Running simulacraMaster

First select a shape to create in setup() by setting the corresponding boolean to 'true'

```
hyperbolic = true;
```

Select an update function in the Vert class by uncommenting the function desired.

```
// position = PVector.add(initPosition, getSimplexMod(i) );
```

Play with the GUI controls in order to create interesting behaviours

### Break down into end to end tests

Explain what these tests test and why

```
Give an example
```

### And coding style tests

Explain what these tests test and why

```
Give an example
```

## Deployment

Add additional notes about how to deploy this on a live system

## Built With

* [Dropwizard](http://www.dropwizard.io/1.0.2/docs/) - The web framework used
* [Maven](https://maven.apache.org/) - Dependency Management
* [ROME](https://rometools.github.io/rome/) - Used to generate RSS Feeds

## Contributing

Please read [CONTRIBUTING.md](https://gist.github.com/PurpleBooth/b24679402957c63ec426) for details on our code of conduct, and the process for submitting pull requests to us.

## Versioning

We use [SemVer](http://semver.org/) for versioning. For the versions available, see the [tags on this repository](https://github.com/your/project/tags). 

## Authors

* **Lior Ben-Gai** - *Initial boilerplate*
* **Joseph Rodrigues Marsh** - *Development*

## Acknowledgments

* Lior Ben Gai for the initial boilerplate
* Etienne Jacob for the looping simplex noise algorithm
* Daniel Shiffman for meshing 3D spherical geometry
