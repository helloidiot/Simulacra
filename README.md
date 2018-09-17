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

* [Processing](https://processing.org/) - Used for simulacraMaster
* [openFrameworks](https://openframeworks.cc/) - Used for simulacraViewer
* [Blender](https://www.blender.org/) - Used to render the OBJ sequences
* [Stop-motion-OBJ](https://github.com/neverhood311/Stop-motion-OBJ) - For importing OBJ sequences into Blender

## Contributing

Feel free to reach out if you are interested in contributing.

## Authors

* **Lior Ben-Gai** - *Initial boilerplate*
* **Joseph Rodrigues Marsh** - *Ongoing development*

## Acknowledgments

* Lior Ben Gai for the initial boilerplate
* Etienne Jacob for the looping simplex noise algorithm
* Daniel Shiffman for meshing 3D spherical geometry
